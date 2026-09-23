#!/usr/bin/env python3
"""Run fixed Gradle measurements and emit measured performance properties.

The caller supplies one or more ``--measurement`` groups.  The first token is
the canonical performance key and the remaining tokens are Gradle tasks.  Each
group is executed ``--runs`` times and the median measured time is exported.
The ``gradle_configuration_ms`` key uses Gradle's profile-reported
``Configuring Projects`` phase so JVM/wrapper startup is not misclassified as
configuration; other keys use wall-clock task duration.
There is deliberately no fallback value: a failed command or fewer than five
runs fails the exporter so CI cannot turn an incomplete run into a passing
metric.

Example::

    python3 tools/measure_gradle_performance.py \
      --measurement gradle_configuration_ms help \
      --measurement warm_incremental_build_ms :androidApp:compileDebugKotlin \
      --measurement clean_android_build_ms clean :androidApp:assembleRelease \
      --output build/performance/inputs/android-build.properties
"""

from __future__ import annotations

import argparse
import re
import statistics
import subprocess
import time
from pathlib import Path


MEASURABLE_KEYS = {
    "gradle_configuration_ms",
    "warm_incremental_build_ms",
    "clean_android_build_ms",
    "ios_simulator_link_ms",
}

PROFILE_CONFIGURATION_PATTERN = re.compile(
    r"<td>Configuring Projects</td>\s*<td class=\"numeric\">([0-9.]+)s</td>",
)


def parse_measurements(raw: list[list[str]]) -> list[tuple[str, list[str]]]:
    measurements: list[tuple[str, list[str]]] = []
    seen: set[str] = set()
    for group in raw:
        if len(group) < 2:
            raise SystemExit("Each --measurement needs a metric key and at least one Gradle task")
        key, tasks = group[0], group[1:]
        if key not in MEASURABLE_KEYS:
            allowed = ", ".join(sorted(MEASURABLE_KEYS))
            raise SystemExit(f"Unsupported build metric {key!r}; choose one of: {allowed}")
        if key in seen:
            raise SystemExit(f"Build metric specified more than once: {key}")
        seen.add(key)
        measurements.append((key, tasks))
    if not measurements:
        raise SystemExit("At least one --measurement is required")
    return measurements


def run_measurement(
    metric_key: str,
    gradle: str,
    tasks: list[str],
    root: Path,
    runs: int,
    gradle_args: list[str],
) -> float:
    elapsed_ms: list[float] = []
    command = [gradle, *tasks, *gradle_args]
    if metric_key == "gradle_configuration_ms" and "--profile" not in command:
        # The metric is the Gradle configuration phase, not JVM/wrapper startup
        # time. Gradle's own profile is the authoritative phase measurement.
        command.append("--profile")
    for run in range(1, runs + 1):
        before_profiles = {
            path: path.stat().st_mtime_ns
            for path in (root / "build/reports/profile").glob("profile-*.html")
            if path.is_file()
        }
        started = time.perf_counter()
        completed = subprocess.run(
            command,
            cwd=root,
            text=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            check=False,
        )
        elapsed = (time.perf_counter() - started) * 1000.0
        if completed.returncode != 0:
            output = completed.stdout[-4000:]
            raise SystemExit(
                f"Gradle measurement failed on run {run}/{runs}: "
                f"{' '.join(command)}\n{output}"
            )
        if metric_key == "gradle_configuration_ms":
            profiles = [
                path
                for path in (root / "build/reports/profile").glob("profile-*.html")
                if path.is_file()
                and (
                    path not in before_profiles
                    or path.stat().st_mtime_ns > before_profiles[path]
                )
            ]
            if not profiles:
                raise SystemExit(
                    "Gradle --profile did not produce a new profile report for "
                    f"run {run}/{runs}"
                )
            profile = max(profiles, key=lambda path: path.stat().st_mtime_ns)
            match = PROFILE_CONFIGURATION_PATTERN.search(
                profile.read_text(encoding="utf-8", errors="replace"),
            )
            if match is None:
                raise SystemExit(
                    "Gradle profile is missing the Configuring Projects duration: "
                    f"{profile}"
                )
            elapsed_ms.append(float(match.group(1)) * 1000.0)
        else:
            elapsed_ms.append(elapsed)
    return statistics.median(elapsed_ms)


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", type=Path, default=Path.cwd())
    parser.add_argument("--gradle", default="./gradlew")
    parser.add_argument("--runs", type=int, default=5)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument(
        "--measurement",
        action="append",
        nargs="+",
        metavar="TOKEN",
        help="metric key followed by one or more Gradle tasks",
    )
    parser.add_argument(
        "--gradle-arg",
        action="append",
        default=[],
        help="additional Gradle argument, repeatable",
    )
    args = parser.parse_args()
    if args.runs < 5:
        raise SystemExit("The performance contract requires at least five runs")
    measurements = parse_measurements(args.measurement or [])
    root = args.root.resolve()
    output = args.output if args.output.is_absolute() else root / args.output

    measured: dict[str, str] = {
        "schema_version": "1",
        "sample_count": str(args.runs),
    }
    for key, tasks in measurements:
        median_ms = run_measurement(
            metric_key=key,
            gradle=args.gradle,
            tasks=tasks,
            root=root,
            runs=args.runs,
            gradle_args=args.gradle_arg,
        )
        measured[key] = f"{median_ms:.3f}"
        print(f"{key}={measured[key]} (median of {args.runs} runs)")

    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(
        "# Generated from measured Gradle runs; do not edit.\n"
        + "\n".join(f"{key}={measured[key]}" for key in sorted(measured))
        + "\n",
        encoding="utf-8",
    )


if __name__ == "__main__":
    main()
