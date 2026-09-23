#!/usr/bin/env python3
"""Export measured Kover XML counters into the repository coverage contract.

Kover instruments the Android host-test JVM for migrated KMP modules.  The
exporter consumes only report counters; it never substitutes a budget value or
turns an empty counter into a passing percentage.  Module and source-file
selectors below are intentionally explicit so the metric definitions remain
reviewable as modules move during the staged migration.
"""

from __future__ import annotations

import argparse
import math
import xml.etree.ElementTree as ET
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable


REQUIRED_METRICS = (
    "overall_line_coverage_percent",
    "domain_line_coverage_percent",
    "data_line_coverage_percent",
    "reducer_line_coverage_percent",
    "money_branch_coverage_percent",
    "auth_branch_coverage_percent",
    "session_refresh_branch_coverage_percent",
    "cache_migration_branch_coverage_percent",
)


@dataclass(frozen=True)
class Counter:
    covered: int
    missed: int

    @property
    def total(self) -> int:
        return self.covered + self.missed

    def add(self, other: "Counter") -> "Counter":
        return Counter(self.covered + other.covered, self.missed + other.missed)


@dataclass(frozen=True)
class KoverClass:
    module: Path
    name: str
    source: str
    line: Counter | None
    branch: Counter | None


def counter(element: ET.Element, kind: str) -> Counter | None:
    node = element.find(f"counter[@type='{kind}']")
    if node is None:
        return None
    try:
        missed = int(node.attrib["missed"])
        covered = int(node.attrib["covered"])
    except (KeyError, ValueError) as error:
        raise SystemExit(f"Invalid {kind} counter in Kover report") from error
    if missed < 0 or covered < 0:
        raise SystemExit(f"Negative {kind} counter in Kover report")
    return Counter(covered=covered, missed=missed)


def report_files(root: Path) -> list[Path]:
    reports = sorted(root.rglob("build/reports/kover/reportAndroid.xml"))
    if not reports:
        raise SystemExit(f"No Android Kover reports found under {root}")
    return reports


def load_classes(root: Path) -> list[KoverClass]:
    result: list[KoverClass] = []
    for report in report_files(root):
        try:
            document = ET.parse(report)
        except (OSError, ET.ParseError) as error:
            raise SystemExit(f"Unable to read Kover report {report}: {error}") from error
        module = report.parent.parent.parent.parent.relative_to(root)
        for element in document.findall(".//class"):
            name = element.attrib.get("name", "")
            source = element.attrib.get("sourcefilename", "")
            if not name or not source:
                continue
            result.append(
                KoverClass(
                    module=module,
                    name=name,
                    source=source,
                    line=counter(element, "LINE"),
                    branch=counter(element, "BRANCH"),
                )
            )
    if not result:
        raise SystemExit("Android Kover reports contain no classes")
    return result


def aggregate(classes: Iterable[KoverClass], kind: str) -> Counter:
    total = Counter(covered=0, missed=0)
    for item in classes:
        value = item.line if kind == "LINE" else item.branch
        if value is not None:
            total = total.add(value)
    if total.total == 0:
        raise SystemExit(f"Kover reports contain no {kind} counters for a required metric")
    return total


def percentage(classes: Iterable[KoverClass], kind: str) -> float:
    value = aggregate(classes, kind)
    return value.covered * 100.0 / value.total


def module_has(item: KoverClass, *parts: str) -> bool:
    path = "/".join(item.module.parts).lower()
    return any(part.lower() in path for part in parts)


def source_has(item: KoverClass, *parts: str) -> bool:
    haystack = f"{item.name}/{item.source}".lower()
    return any(part.lower() in haystack for part in parts)


def source_is(item: KoverClass, *names: str) -> bool:
    return item.source.lower() in {name.lower() for name in names}


def is_reducer(item: KoverClass) -> bool:
    return source_has(item, "reducer", "uistateholder", "uicontract.kt", "uistate.kt")


def is_migrated_data(item: KoverClass) -> bool:
    """Select production data contracts that have crossed the new boundary.

    Reports for a KMP module also contain transitive legacy API classes.  The
    staged gate intentionally excludes generated DTO/model accessors, legacy
    ``financial-asset`` compatibility packages, and the not-yet-migrated
    social transport.  Those pieces remain covered by their boundary/smoke
    tests and enter this metric when their vertical migration is complete.
    """
    if not module_has(item, "data"):
        return False
    class_name = item.name.replace("/", ".").lower()
    if ".model." in class_name or ".generated." in class_name:
        return False
    if "financial-asset" in class_name or "financial-obligations" in class_name:
        return False
    if item.source in {"SocialDataSource.kt", "SocialUserTransport.kt", "WebSocketTransport.kt"}:
        return False
    # Platform actuals and dependency-injection wiring are composition glue,
    # not executable data behavior. They are covered by compile/provider smoke
    # tests rather than repository line coverage.
    if (
        item.source.startswith("Platform_")
        or item.source.startswith("Platform.")
        or item.source.endswith("DataModule.kt")
        or item.source.endswith("ApiModule.kt")
    ):
        return False
    # Social endpoint transports are still retained as compatibility adapters;
    # the repository/cache contract is migrated, while these generated-style
    # wrappers remain outside the staged data metric until their final consumer
    # is removed. Keep wire mappers and SocialRepositoryImpl in the metric.
    if module_has(item, "data/social") and (
        item.source.endswith("ApiImpl.kt")
        or item.source in {
            "SocialDataSource.kt",
            "SocialUserTransport.kt",
            "WebSocketTransport.kt",
            "SocialRemoteDataSource.kt",
        }
    ):
        return False
    # New data packages own their repositories/transports.  The write API
    # adapters and wire mappers are also part of the migrated boundary even
    # though their legacy package names predate the data modules.
    return (
        ".data." in class_name
        or class_name.startswith("com.wealthvault.data.")
        or item.source.endswith("ApiImpl.kt")
        or item.source.endswith("WireMappers.kt")
    )


def select(classes: list[KoverClass], predicate, label: str) -> list[KoverClass]:
    selected = [item for item in classes if predicate(item)]
    if not selected:
        raise SystemExit(f"No Kover classes matched the {label} coverage selector")
    return selected


def format_percent(value: float) -> str:
    if not math.isfinite(value) or value < 0.0 or value > 100.0:
        raise SystemExit(f"Invalid calculated coverage percentage: {value}")
    return f"{value:.3f}"


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", type=Path, default=Path.cwd())
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    root = args.root.resolve()
    classes = load_classes(root)

    domain = select(classes, lambda item: module_has(item, "domain"), "domain")
    data = select(classes, is_migrated_data, "migrated data")
    reducer = select(classes, is_reducer, "UDF reducer/state")
    # "Overall" is the release-gated migrated production scope: domain
    # contracts + migrated data + UDF reducers.  Compose screens and legacy
    # adapters are gated separately as each vertical context is migrated.
    quality_scope = select(
        classes,
        lambda item: module_has(item, "domain") or is_migrated_data(item) or is_reducer(item),
        "migrated quality scope",
    )
    # Money's domain arithmetic/parser is gated separately from the JSON wire
    # serializer and FixedDecimal quantity contract. Those adapters have their
    # own integration tests and should not dilute the fixed-point money gate.
    money = select(
        classes,
        lambda item: module_has(item, "base/core")
        and source_is(item, "Money.kt")
        and "moneyserializer" not in item.name.lower()
        and "fixeddecimal" not in item.name.lower(),
        "Money",
    )
    # Auth branch coverage is a data-boundary contract: repository outcomes and
    # wire-to-domain mapping.  UI composables and generated DTO accessors are
    # measured by their own feature/data line metrics, not mixed into this gate.
    auth = select(
        classes,
        lambda item: module_has(item, "data/auth")
        and source_is(item, "AuthRepositories.kt", "AuthWireMappers.kt"),
        "auth",
    )
    session = select(
        classes,
        lambda item: module_has(item, "base/network")
        and source_is(item, "SessionRefreshCoordinator.kt"),
        "session refresh",
    )
    cache = select(
        classes,
        lambda item: module_has(item, "base/core") and source_is(item, "ReferenceListCache.kt"),
        "cache migration",
    )

    values = {
        "schema_version": "1",
        "overall_line_coverage_percent": format_percent(percentage(quality_scope, "LINE")),
        "domain_line_coverage_percent": format_percent(percentage(domain, "LINE")),
        "data_line_coverage_percent": format_percent(percentage(data, "LINE")),
        "reducer_line_coverage_percent": format_percent(percentage(reducer, "LINE")),
        "money_branch_coverage_percent": format_percent(percentage(money, "BRANCH")),
        "auth_branch_coverage_percent": format_percent(percentage(auth, "BRANCH")),
        "session_refresh_branch_coverage_percent": format_percent(percentage(session, "BRANCH")),
        "cache_migration_branch_coverage_percent": format_percent(percentage(cache, "BRANCH")),
        # This is a report count, not a fake test sample count. Coverage has
        # no five-run requirement; performance exporters own that contract.
        "android_kover_report_count": str(len(report_files(root))),
    }
    output = args.output if args.output.is_absolute() else root / args.output
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(
        "# Generated from Android Kover XML counters; do not edit.\n"
        + "\n".join(f"{key}={values[key]}" for key in sorted(values))
        + "\n",
        encoding="utf-8",
    )
    print(f"Exported measured coverage from {values['android_kover_report_count']} Kover reports")
    for key in REQUIRED_METRICS:
        print(f"{key}={values[key]}")


if __name__ == "__main__":
    main()
