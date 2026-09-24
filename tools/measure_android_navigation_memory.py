#!/usr/bin/env python3
"""Measure retained app memory after repeated navigation loops on Android.

The benchmark activity is deterministic and does not require a backend
session.  Each sample starts from a clean process, records PSS after the first
screen is shown, opens the same route ``loops`` times, and records PSS again.
The exporter emits a median over at least five samples; it never substitutes a
budget or a placeholder when adb cannot provide a measurement.
"""

from __future__ import annotations

import argparse
import re
import statistics
import subprocess
import time
from pathlib import Path


TOTAL_PSS_PATTERN = re.compile(
    r"^\s*TOTAL(?:\s+PSS:?)?\s+([0-9,]+)",
    re.MULTILINE,
)


def run_adb(adb: str, *args: str) -> str:
    command = [adb, *args]
    completed = subprocess.run(
        command,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        check=False,
    )
    if completed.returncode != 0:
        raise SystemExit(
            f"adb command failed ({' '.join(command)}):\n{completed.stdout[-4000:]}"
        )
    return completed.stdout


def read_pss_kb(adb: str, package_name: str) -> int:
    output = run_adb(adb, "shell", "dumpsys", "meminfo", package_name)
    match = TOTAL_PSS_PATTERN.search(output)
    if match is None:
        raise SystemExit(
            f"dumpsys meminfo did not contain a TOTAL PSS row for {package_name}"
        )
    value = int(match.group(1).replace(",", ""))
    if value <= 0:
        raise SystemExit(f"dumpsys meminfo returned non-positive PSS: {value} KB")
    return value


def measure_sample(
    adb: str,
    package_name: str,
    component: str,
    loops: int,
    settle_seconds: float,
) -> float:
    run_adb(adb, "shell", "am", "force-stop", package_name)
    run_adb(adb, "shell", "am", "start", "-W", "-n", component)
    time.sleep(settle_seconds)
    baseline_kb = read_pss_kb(adb, package_name)

    for _ in range(loops):
        run_adb(adb, "shell", "input", "keyevent", "KEYCODE_HOME")
        run_adb(adb, "shell", "am", "start", "-W", "-n", component)
        time.sleep(settle_seconds)

    retained_kb = read_pss_kb(adb, package_name)
    return max(0.0, retained_kb - baseline_kb) / 1024.0


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--adb", default="adb")
    parser.add_argument("--package", required=True)
    parser.add_argument("--component", required=True)
    parser.add_argument("--runs", type=int, default=5)
    parser.add_argument("--loops", type=int, default=10)
    parser.add_argument("--settle-seconds", type=float, default=0.25)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()

    if args.runs < 5:
        raise SystemExit("The performance contract requires at least five runs")
    if args.loops < 10:
        raise SystemExit("The retained-memory contract requires at least ten loops")
    if args.settle_seconds < 0:
        raise SystemExit("--settle-seconds must not be negative")

    deltas: list[float] = []
    for run in range(1, args.runs + 1):
        delta = measure_sample(
            adb=args.adb,
            package_name=args.package,
            component=args.component,
            loops=args.loops,
            settle_seconds=args.settle_seconds,
        )
        deltas.append(delta)
        print(f"navigation run {run}/{args.runs}: retained_memory_delta_mb={delta:.3f}")

    median_delta = statistics.median(deltas)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(
        "# Generated from adb navigation-loop measurements; do not edit.\n"
        f"schema_version=1\n"
        f"sample_count={args.runs}\n"
        f"retained_memory_delta_mb={median_delta:.3f}\n"
        f"navigation_loop_count={args.loops}\n",
        encoding="utf-8",
    )


if __name__ == "__main__":
    main()
