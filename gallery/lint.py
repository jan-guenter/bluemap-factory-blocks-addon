#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Lint the generated Factory Blocks gallery without starting Minecraft."""

from __future__ import annotations

from collections import Counter
import json
from pathlib import Path
import re
import sys

sys.dont_write_bytecode = True
import generate


ROOT = Path(__file__).resolve().parent


def fail(message: str) -> None:
    raise ValueError(message)


def main() -> int:
    expected = generate.generated_files()
    for relative, payload in expected.items():
        path = ROOT / relative
        if not path.is_file() or path.read_bytes() != payload:
            fail(f"generated file differs: {relative}")

    json.loads((ROOT / "datapack/pack.mcmeta").read_text(encoding="utf-8"))
    json.loads(
        (ROOT / "datapack/data/minecraft/tags/function/load.json").read_text(
            encoding="utf-8"
        )
    )

    function_root = ROOT / f"datapack/data/{generate.NAMESPACE}/function"
    build = (function_root / "build.mcfunction").read_text(encoding="utf-8")
    clear = (function_root / "clear.mcfunction").read_text(encoding="utf-8")
    verify = (function_root / "verify.mcfunction").read_text(encoding="utf-8")
    all_functions = "\n".join(
        path.read_text(encoding="utf-8")
        for path in sorted(function_root.glob("*.mcfunction"))
    )

    if len(generate.PLACEMENTS) != 25:
        fail("gallery must define exactly 25 target placements")
    if len(re.findall(r"^setblock ", build, re.MULTILINE)) != 25:
        fail("build must contain exactly 25 setblock placements")
    if len(re.findall(r"^scoreboard players add #checked ", verify, re.MULTILINE)) != 26:
        fail("verify must contain 25 block checks and one build-counter check")
    if build.count(f"scoreboard players add #builds {generate.OBJECTIVE} 1") != 1:
        fail("exactly one persistent build-counter increment is required")

    expected_blocks = Counter(
        {
            "factory_blocks:factory": 10,
            "factory_blocks:hex": 9,
            "factory_blocks:gears": 4,
            "factory_blocks:metalbox": 1,
            "factory_blocks:piping": 1,
        }
    )
    actual_blocks = Counter(row[5] for row in generate.PLACEMENTS)
    if actual_blocks != expected_blocks:
        fail(f"unexpected target block census: {actual_blocks}")

    coordinates = [(row[2], row[3], row[4]) for row in generate.PLACEMENTS]
    if len(set(coordinates)) != len(coordinates):
        fail("target coordinates must be unique")
    envelope = generate.ENVELOPE
    for x, y, z in coordinates:
        if not (
            envelope["min_x"] <= x <= envelope["max_x"]
            and envelope["min_y"] <= y <= envelope["max_y"]
            and envelope["min_z"] <= z <= envelope["max_z"]
        ):
            fail(f"placement escaped safe envelope: {(x, y, z)}")

    factory_coordinates = {
        (x, y, z)
        for _section, _label, x, y, z, block in generate.PLACEMENTS
        if block == "factory_blocks:factory"
    }
    expected_factory = {(164, 100, 164)} | {
        (x, y, 164) for x in range(168, 171) for y in range(100, 103)
    }
    if factory_coordinates != expected_factory:
        fail("factory fixture must be one isolated block plus the exact 3x3 wall")

    hex_coordinates = {
        (x, y, z)
        for _section, _label, x, y, z, block in generate.PLACEMENTS
        if block == "factory_blocks:hex"
    }
    expected_hex = {
        (x, y, 164) for x in range(180, 183) for y in range(102, 105)
    }
    if hex_coordinates != expected_hex:
        fail("hex fixture must be the exact positive-coordinate x/y 3x3 wall")
    south_roles = {1 + (x % 3) + (y % 3) * 3 for x, y, _z in hex_coordinates}
    if south_roles != set(range(1, 10)):
        fail("hex south faces must expose roles 1 through 9 exactly once")

    gears_coordinates = {
        (x, y, z)
        for _section, _label, x, y, z, block in generate.PLACEMENTS
        if block == "factory_blocks:gears"
    }
    if gears_coordinates != {
        (192, 100, 164),
        (193, 100, 164),
        (192, 101, 164),
        (193, 101, 164),
    }:
        fail("gears fixture must be the exact 2x2 wall")

    expected_clear = {
        "fill 160 99 160 191 124 191 minecraft:air",
        "fill 192 99 160 223 124 191 minecraft:air",
    }
    actual_clear = set(re.findall(r"^fill .* minecraft:air$", clear, re.MULTILINE))
    if actual_clear != expected_clear:
        fail("clear must cover the full safe envelope in two legal fill volumes")

    forbidden = ("data merge", "summon ", "spawner", "factory_blocks:fan")
    lowered = all_functions.lower()
    for token in forbidden:
        if token in lowered:
            fail(f"forbidden gallery operation or block: {token}")
    if "{" in build or "}" in build:
        fail("build must contain no NBT payloads")

    for phase, delay in (("20t", "20t"), ("100t", "100t")):
        command = (
            f"schedule function {generate.NAMESPACE}:verify_{phase} "
            f"{delay} replace"
        )
        if build.count(command) != 1:
            fail(f"missing exact {phase} retained-placement schedule")

    print(
        "Factory Blocks gallery lint passed: "
        "25 placements, 26 checks/phase, full bounded clear"
    )
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except ValueError as error:
        print(f"lint failed: {error}", file=sys.stderr)
        raise SystemExit(1)
