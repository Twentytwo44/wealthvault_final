#!/usr/bin/env python3
"""Export measured XCTest/xccov coverage without inventing missing values."""

from __future__ import annotations

import argparse
import json
import subprocess
from pathlib import Path
from typing import Any


def numeric(value: Any) -> float | None:
    return float(value) if isinstance(value, (int, float)) and not isinstance(value, bool) else None


def target_totals(target: dict[str, Any]) -> tuple[int, int]:
    covered = target.get("coveredLines")
    executable = target.get("executableLines")
    if isinstance(covered, int) and isinstance(executable, int):
        return covered, executable
    return 0, 0


def is_first_party_target(name: str) -> bool:
    """Keep third-party Pods from masquerading as app coverage."""
    normalized = name.lower()
    return "wealthvault" in normalized or "composeapp" in normalized


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--result-bundle", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    bundle = args.result_bundle.resolve()
    if not bundle.exists():
        raise SystemExit(f"XCTest result bundle does not exist: {bundle}")

    command = ["xcrun", "xccov", "view", "--report", "--json", str(bundle)]
    completed = subprocess.run(command, text=True, capture_output=True, check=False)
    if completed.returncode != 0:
        raise SystemExit(
            f"Unable to read XCTest coverage with {' '.join(command)}:\n"
            f"{completed.stderr[-4000:]}"
        )
    try:
        report = json.loads(completed.stdout)
    except json.JSONDecodeError as error:
        raise SystemExit(f"xccov returned invalid JSON: {error}") from error

    covered = 0
    executable = 0
    targets = report.get("targets") if isinstance(report, dict) else None
    if isinstance(targets, list):
        for target in targets:
            if not isinstance(target, dict):
                continue
            name = str(target.get("name", "")).lower()
            if "test" in name or not is_first_party_target(name):
                continue
            target_covered, target_executable = target_totals(target)
            covered += target_covered
            executable += target_executable

    if executable == 0 and isinstance(report, dict):
        line_coverage = numeric(report.get("lineCoverage"))
        if line_coverage is not None:
            # xccov reports lineCoverage as a ratio. Preserve the measured
            # ratio without manufacturing a line count when the schema omits it.
            percentage = line_coverage * 100.0 if line_coverage <= 1.0 else line_coverage
        else:
            percentage = None
    elif executable > 0:
        percentage = covered * 100.0 / executable
    else:
        percentage = None

    if percentage is None or not 0.0 <= percentage <= 100.0:
        raise SystemExit("XCTest result contains no measurable line coverage")

    output = args.output.resolve()
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(
        "# Generated from xccov XCTest output; do not edit.\n"
        f"ios_xctest_line_coverage_percent={percentage:.3f}\n",
        encoding="utf-8",
    )
    print(f"ios_xctest_line_coverage_percent={percentage:.3f}")


if __name__ == "__main__":
    main()
