#!/usr/bin/env python3
"""Export the iOS XCTest hitch metric from an xcresult bundle.

XCTest's ``XCTOSSignpostMetric.scrollingAndDecelerationMetric`` is reported by
``xcresulttool`` as a metric whose unit is milliseconds per second.  This
exporter keeps the measured samples intact and refuses to guess a value when
the test was skipped or the result bundle does not contain the expected
metric.  A JSON input option makes the parser deterministic and unit-testable
without an installed simulator.
"""

from __future__ import annotations

import argparse
import json
import math
import subprocess
from pathlib import Path
from typing import Any, Iterable


def flatten_numbers(value: Any) -> list[float]:
    if isinstance(value, (int, float)) and not isinstance(value, bool):
        number = float(value)
        return [number] if math.isfinite(number) else []
    if isinstance(value, list):
        result: list[float] = []
        for item in value:
            result.extend(flatten_numbers(item))
        return result
    return []


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


def test_records(payload: Any) -> Iterable[dict[str, Any]]:
    if isinstance(payload, list):
        yield from (item for item in payload if isinstance(item, dict))
    elif isinstance(payload, dict):
        # xcresulttool normally returns one record per test. Accept a single
        # record too, which is useful with --test-id and in fixture tests.
        if isinstance(payload.get("testRuns"), list):
            yield payload
        else:
            tests = payload.get("tests")
            if isinstance(tests, list):
                yield from (item for item in tests if isinstance(item, dict))


def metric_samples(payload: Any) -> list[float]:
    samples: list[float] = []
    saw_scroll_duration = False
    for record in test_records(payload):
        runs = record.get("testRuns")
        if not isinstance(runs, list):
            continue
        for run in runs:
            if not isinstance(run, dict):
                continue
            metrics = run.get("metrics")
            if not isinstance(metrics, list):
                continue
            for metric in metrics:
                if not isinstance(metric, dict):
                    continue
                display = str(metric.get("displayName", "")).lower()
                identifier = str(metric.get("identifier", "")).lower()
                unit = str(metric.get("unitOfMeasurement", "")).lower()
                is_scroll_metric = (
                    "scroll" in display
                    or "deceleration" in display
                    or "scroll" in identifier
                    or "deceleration" in identifier
                )
                if is_scroll_metric and unit in {"s", "sec", "second", "seconds"}:
                    # XCTest on some simulator/runtime combinations exposes
                    # only total scroll duration. That is useful evidence that
                    # the test ran, but it is not the hitch-time-ratio contract
                    # and must never be converted heuristically.
                    saw_scroll_duration = True
                is_scroll_hitch = (
                    is_scroll_metric
                    and unit in {"ms/s", "milliseconds per second", "millisecond/second"}
                )
                is_hitch = "hitch" in display or "hitch" in identifier or is_scroll_hitch
                if not is_hitch:
                    continue
                if unit not in {"ms/s", "milliseconds per second", "millisecond/second"}:
                    raise SystemExit(
                        "iOS hitch metric has an unexpected unit: "
                        f"{metric.get('unitOfMeasurement')!r}"
                    )
                samples.extend(flatten_numbers(metric.get("measurements")))
    if not samples and saw_scroll_duration:
        raise SystemExit(
            "iOS XCTest reported scroll duration in seconds but no hitch-time-ratio "
            "metric in ms/s; run the performance test on a fixed runner/device "
            "that exposes hitch measurements"
        )
    return samples


def read_payload(result_bundle: Path, metrics_json: Path | None) -> Any:
    if metrics_json is not None:
        try:
            return json.loads(metrics_json.read_text(encoding="utf-8"))
        except (OSError, json.JSONDecodeError) as error:
            raise SystemExit(f"Unable to read iOS metrics JSON {metrics_json}: {error}") from error
    if not result_bundle.is_dir():
        raise SystemExit(f"iOS result bundle does not exist: {result_bundle}")
    try:
        completed = subprocess.run(
            [
                "xcrun",
                "xcresulttool",
                "get",
                "test-results",
                "metrics",
                "--path",
                str(result_bundle),
                "--compact",
            ],
            check=True,
            capture_output=True,
            text=True,
        )
        return json.loads(completed.stdout)
    except FileNotFoundError as error:
        raise SystemExit("xcrun is required to read an iOS result bundle") from error
    except subprocess.CalledProcessError as error:
        detail = error.stderr.strip() or error.stdout.strip()
        raise SystemExit(f"xcresulttool could not read {result_bundle}: {detail}") from error
    except json.JSONDecodeError as error:
        raise SystemExit(f"xcresulttool returned invalid JSON: {error}") from error


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--result-bundle", type=Path, required=True)
    parser.add_argument("--metrics-json", type=Path)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--minimum-samples", type=int, default=5)
    args = parser.parse_args()
    if args.minimum_samples < 1:
        raise SystemExit("--minimum-samples must be positive")

    samples = metric_samples(read_payload(args.result_bundle, args.metrics_json))
    if len(samples) < args.minimum_samples:
        raise SystemExit(
            "iOS scrolling hitch samples are missing or incomplete: "
            f"found {len(samples)}, need at least {args.minimum_samples}"
        )
    if any(value < 0.0 for value in samples):
        raise SystemExit("iOS scrolling hitch samples must be non-negative")

    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(
        "# Generated from XCTest xcresult metrics; do not edit.\n"
        f"schema_version=1\n"
        f"sample_count={len(samples)}\n"
        # The shared performance contract compares medians over five runs;
        # XCTest already gives us one measurement per iteration.
        f"ios_hitch_ms_per_s={percentile(samples, 50):.3f}\n",
        encoding="utf-8",
    )


if __name__ == "__main__":
    main()
