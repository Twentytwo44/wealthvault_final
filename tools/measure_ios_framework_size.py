#!/usr/bin/env python3
"""Export the measured size of the iOS Compose framework.

The framework is built once by Gradle, then its byte size is read five times
to make the exporter obey the shared sample-count contract. A later aggregation
run compares ``ios_framework_size_bytes`` with the size saved by the latest
successful main-branch performance artifact. The first measured main run is a
bootstrap and records zero growth relative to itself.
"""

from __future__ import annotations

import argparse
import statistics
from pathlib import Path


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--framework", type=Path, required=True)
    parser.add_argument("--runs", type=int, default=5)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()

    if args.runs < 5:
        raise SystemExit("The performance contract requires at least five runs")
    framework = args.framework.resolve()
    if not framework.is_dir():
        raise SystemExit(f"iOS framework directory does not exist: {framework}")

    sizes: list[int] = []
    for run in range(1, args.runs + 1):
        size = sum(path.stat().st_size for path in framework.rglob("*") if path.is_file())
        if size <= 0:
            raise SystemExit(f"iOS framework size is not positive: {framework}")
        sizes.append(size)
        print(f"framework size run {run}/{args.runs}: {size} bytes")

    median_size = int(statistics.median(sizes))
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(
        "# Generated from the linked iOS framework; do not edit.\n"
        "schema_version=1\n"
        f"sample_count={args.runs}\n"
        f"ios_framework_size_bytes={median_size}\n",
        encoding="utf-8",
    )


if __name__ == "__main__":
    main()
