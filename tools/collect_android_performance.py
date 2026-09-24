#!/usr/bin/env python3
"""Convert AndroidX Macrobenchmark JSON into measured performance properties.

The script deliberately exports only values that are present in the benchmark
reports. It never fills a missing metric with zero or with a budget value. A
runner may pass measured supplement files for metrics that are collected by a
separate trace (for example cached dashboard content).
"""

from __future__ import annotations

import argparse
import json
import math
from pathlib import Path
from typing import Any, Iterable


def percentile(values: list[float], percentile_value: float) -> float:
    if not values:
        raise ValueError("cannot calculate a percentile from an empty sample")
    ordered = sorted(values)
    if len(ordered) == 1:
        return ordered[0]
    position = (len(ordered) - 1) * percentile_value / 100.0
    lower = math.floor(position)
    upper = math.ceil(position)
    if lower == upper:
        return ordered[lower]
    fraction = position - lower
    return ordered[lower] + (ordered[upper] - ordered[lower]) * fraction


def flatten_numbers(value: Any) -> list[float]:
    if isinstance(value, (int, float)) and not isinstance(value, bool):
        return [float(value)]
    if isinstance(value, list):
        result: list[float] = []
        for item in value:
            result.extend(flatten_numbers(item))
        return result
    return []


def metric_runs(test: dict[str, Any], names: Iterable[str]) -> list[float]:
    names_lower = {name.lower() for name in names}
    for container_name in ("metrics", "sampledMetrics", "measurements"):
        container = test.get(container_name)
        if not isinstance(container, dict):
            continue
        for name, payload in container.items():
            if name.lower() not in names_lower:
                continue
            if isinstance(payload, dict):
                runs = flatten_numbers(payload.get("runs"))
                if runs:
                    return runs
                # A report without per-iteration data is not sufficient for
                # the five-run gate, but returning the aggregate here produces
                # a useful, correctly rejected sample_count=1 export.
                for aggregate_name in ("p95", "median", "value"):
                    aggregate = flatten_numbers(payload.get(aggregate_name))
                    if aggregate:
                        return aggregate
            else:
                direct = flatten_numbers(payload)
                if direct:
                    return direct
    return []


def tests_from_report(report: Any) -> list[dict[str, Any]]:
    if isinstance(report, dict):
        benchmarks = report.get("benchmarks")
        if isinstance(benchmarks, list):
            return [item for item in benchmarks if isinstance(item, dict)]
        # Some runners store one TestResult directly rather than the enclosing
        # BenchmarkData object.
        if isinstance(report.get("metrics"), dict) or isinstance(
            report.get("sampledMetrics"), dict
        ):
            return [report]
    if isinstance(report, list):
        return [item for item in report if isinstance(item, dict)]
    return []


def load_tests(input_root: Path) -> list[dict[str, Any]]:
    # Gradle/UTP prefixes the report with the instrumentation package on some
    # versions (for example `com.example-benchmarkData.json`) and emits the
    # bare `benchmarkData.json` name on others.
    files = sorted(
        file
        for file in input_root.rglob("*.json")
        if file.name == "benchmarkData.json" or file.name.endswith("-benchmarkData.json")
    )
    if not files:
        raise SystemExit(f"No benchmarkData.json found under {input_root}")
    tests: list[dict[str, Any]] = []
    for file in files:
        try:
            report = json.loads(file.read_text(encoding="utf-8"))
        except (OSError, json.JSONDecodeError) as error:
            raise SystemExit(f"Unable to read benchmark report {file}: {error}") from error
        tests.extend(tests_from_report(report))
    if not tests:
        raise SystemExit(f"Benchmark reports under {input_root} contain no test results")
    return tests


def test_name(test: dict[str, Any]) -> str:
    return " ".join(
        str(test.get(key, "")) for key in ("name", "className", "testName")
    ).lower()


def read_properties(paths: list[Path]) -> dict[str, str]:
    properties: dict[str, str] = {}
    for path in paths:
        if not path.is_file():
            raise SystemExit(f"Measured properties file does not exist: {path}")
        for raw_line in path.read_text(encoding="utf-8").splitlines():
            line = raw_line.strip()
            if not line or line.startswith("#"):
                continue
            if "=" not in line:
                raise SystemExit(f"Invalid properties line in {path}: {raw_line}")
            key, value = (part.strip() for part in line.split("=", 1))
            if not key or not value or value.startswith("<"):
                raise SystemExit(f"Unmeasured property in {path}: {raw_line}")
            previous = properties.get(key)
            if previous is not None and previous != value:
                raise SystemExit(f"Conflicting property {key}: {previous} vs {value}")
            properties[key] = value
    return properties


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--supplement", type=Path, action="append", default=[])
    args = parser.parse_args()

    tests = load_tests(args.input)
    cold_runs: list[float] = []
    warm_runs: list[float] = []
    cached_dashboard_runs: list[float] = []
    frame_overrun_runs: list[float] = []
    scrolling_frame_overrun_runs: list[float] = []
    cold_heap_runs: list[float] = []
    warm_heap_runs: list[float] = []
    cold_rss_anon_runs: list[float] = []
    warm_rss_anon_runs: list[float] = []
    for test in tests:
        name = test_name(test)
        startup = metric_runs(
            test,
            ("timeToInitialDisplayMs", "timeToFullDisplayMs", "startupMs"),
        )
        if "cold" in name:
            cold_runs.extend(startup)
            cold_heap_runs.extend(metric_runs(test, ("memoryHeapSizeMaxKb",)))
            cold_rss_anon_runs.extend(metric_runs(test, ("memoryRssAnonMaxKb",)))
        elif "warm" in name:
            warm_runs.extend(startup)
            warm_heap_runs.extend(metric_runs(test, ("memoryHeapSizeMaxKb",)))
            warm_rss_anon_runs.extend(metric_runs(test, ("memoryRssAnonMaxKb",)))
        if "cached" in name and "dashboard" in name:
            cached_dashboard_runs.extend(startup)
        test_frame_overruns = metric_runs(test, ("frameOverrunMs",))
        frame_overrun_runs.extend(test_frame_overruns)
        if "scroll" in name or "jank" in name:
            scrolling_frame_overrun_runs.extend(test_frame_overruns)

    if not cold_runs:
        raise SystemExit("Cold startup samples are missing from Macrobenchmark JSON")
    if not warm_runs:
        raise SystemExit("Warm startup samples are missing from Macrobenchmark JSON")
    if not cached_dashboard_runs:
        raise SystemExit("Cached dashboard samples are missing from Macrobenchmark JSON")
    sample_count = min(
        len(cold_runs),
        len(warm_runs),
        len(cached_dashboard_runs),
    )

    measured: dict[str, str] = {
        "schema_version": "1",
        "sample_count": str(sample_count),
        "cold_start_p95_ms": f"{percentile(cold_runs, 95):.3f}",
        "warm_start_p95_ms": f"{percentile(warm_runs, 95):.3f}",
        "cached_dashboard_ttc_ms": f"{percentile(cached_dashboard_runs, 95):.3f}",
    }
    if frame_overrun_runs:
        startup_overrun_percent = (
            100.0 * sum(value > 0.0 for value in frame_overrun_runs) / len(frame_overrun_runs)
        )
        measured["startup_frame_overrun_percent"] = f"{startup_overrun_percent:.3f}"
        measured["startup_frame_overrun_p95_ms"] = f"{percentile(frame_overrun_runs, 95):.3f}"
    if scrolling_frame_overrun_runs:
        jank_count = sum(value > 0.0 for value in scrolling_frame_overrun_runs)
        jank_percent = 100.0 * jank_count / len(scrolling_frame_overrun_runs)
        measured["android_jank_percent"] = f"{jank_percent:.3f}"
    if cold_heap_runs:
        measured["cold_memory_heap_max_kb_median"] = f"{percentile(cold_heap_runs, 50):.3f}"
    if warm_heap_runs:
        measured["warm_memory_heap_max_kb_median"] = f"{percentile(warm_heap_runs, 50):.3f}"
    if cold_rss_anon_runs:
        measured["cold_memory_rss_anon_max_kb_median"] = f"{percentile(cold_rss_anon_runs, 50):.3f}"
    if warm_rss_anon_runs:
        measured["warm_memory_rss_anon_max_kb_median"] = f"{percentile(warm_rss_anon_runs, 50):.3f}"

    measured.update(read_properties(args.supplement))
    measured["sample_count"] = str(
        min(sample_count, int(measured.get("sample_count", str(sample_count))))
    )
    args.output.parent.mkdir(parents=True, exist_ok=True)
    lines = [
        "# Generated from AndroidX Macrobenchmark output; do not edit.",
        *[f"{key}={measured[key]}" for key in sorted(measured)],
        "",
    ]
    args.output.write_text("\n".join(lines), encoding="utf-8")


if __name__ == "__main__":
    main()
