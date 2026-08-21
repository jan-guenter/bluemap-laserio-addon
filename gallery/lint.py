#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Lint the generated LaserIO gallery without starting Minecraft."""

from __future__ import annotations

from collections import Counter
import json
from pathlib import Path
import re
import sys

sys.dont_write_bytecode = True
import cases
import generate


ROOT = Path(__file__).resolve().parent


def fail(message: str) -> None:
    raise ValueError(message)


def reciprocal(offset: cases.Offset) -> cases.Offset:
    return cases.Offset(-offset.x, -offset.y, -offset.z)


def main() -> int:
    expected = generate.generated_files()
    for relative, payload in expected.items():
        path = ROOT / relative
        if not path.is_file() or path.read_bytes() != payload:
            fail(f"generated file differs: {relative}")

    expected_datapack_files = {
        relative for relative in expected if relative.parts[0] == "datapack"
    }
    actual_datapack_files = {
        path.relative_to(ROOT)
        for path in (ROOT / "datapack").rglob("*")
        if path.is_file()
    }
    if actual_datapack_files != expected_datapack_files:
        fail("datapack contains unexpected or missing files")

    metadata = json.loads(
        (ROOT / "datapack/pack.mcmeta").read_text(encoding="utf-8")
    )
    if metadata["pack"].get("pack_format") != 48:
        fail("gallery must target the exact Minecraft 1.21.1 pack format")
    if "not natural-fixture proof" not in metadata["pack"].get(
        "description", ""
    ):
        fail("gallery must retain the synthetic-evidence warning")
    load_tag = json.loads(
        (ROOT / "datapack/data/minecraft/tags/function/load.json").read_text(
            encoding="utf-8"
        )
    )
    if load_tag != {"values": [f"{cases.NAMESPACE}:load"]}:
        fail("load tag differs from the exact namespace")

    if len(cases.PLACEMENTS) != 12:
        fail("gallery must retain exactly twelve valid LaserIO cells")
    cells = [placement.cell for placement in cases.PLACEMENTS]
    if len(set(cells)) != 12:
        fail("gallery cell IDs must be unique")
    if Counter(placement.section for placement in cases.PLACEMENTS) != Counter(
        {
            "defaults": 3,
            "recolors": 3,
            "node-link": 2,
            "mixed-link": 2,
            "advanced-partner": 2,
        }
    ):
        fail("gallery section census drifted")
    if Counter(placement.block_id for placement in cases.PLACEMENTS) != Counter(
        {
            "laserio:laser_node": 4,
            "laserio:laser_connector": 3,
            "laserio:laser_connector_advanced": 5,
        }
    ):
        fail("gallery block census drifted")

    coordinates = [placement.position for placement in cases.PLACEMENTS]
    if len(set(coordinates)) != 12:
        fail("gallery coordinates must be unique")
    envelope = cases.ENVELOPE
    for position in coordinates:
        if not (
            envelope["min_x"] <= position.x <= envelope["max_x"]
            and envelope["min_y"] <= position.y <= envelope["max_y"]
            and envelope["min_z"] <= position.z <= envelope["max_z"]
        ):
            fail(f"placement escaped the bounded envelope: {position}")
        if position.y != 100:
            fail(f"gallery cell escaped the canonical y=100 plane: {position}")

    defaults = [
        placement for placement in cases.PLACEMENTS
        if placement.section == "defaults"
    ]
    recolors = [
        placement for placement in cases.PLACEMENTS
        if placement.section == "recolors"
    ]
    if any(placement.laser_color != cases.DEFAULT_RED for placement in defaults):
        fail("default shell cells must use LaserIO's exact default ARGB")
    if [placement.laser_color for placement in recolors] != [
        cases.CYAN,
        cases.GREEN,
        cases.VIOLET,
    ]:
        fail("recolored shell palette drifted")

    by_position = {
        placement.position: placement for placement in cases.PLACEMENTS
    }
    rendered_owners = []
    for placement in cases.PLACEMENTS:
        if not set(placement.rendered_connections).issubset(
            placement.connections
        ):
            fail(f"rendered connection is not a real connection: {placement.cell}")
        if placement.rendered_connections:
            rendered_owners.append(placement.cell)
        for offset in placement.connections:
            distance_squared = offset.x ** 2 + offset.y ** 2 + offset.z ** 2
            if not 0 < distance_squared < 64:
                fail(f"ordinary link escaped LaserIO's strict 8-block range: {placement.cell}")
            target_position = cases.Position(
                placement.x + offset.x,
                placement.y + offset.y,
                placement.z + offset.z,
            )
            target = by_position.get(target_position)
            if target is None or reciprocal(offset) not in target.connections:
                fail(f"ordinary link is not reciprocal: {placement.cell}")
            if target.laser_color != placement.laser_color:
                fail(f"ordinary link colors disagree: {placement.cell}")
    if rendered_owners != ["C1", "D1"]:
        fail(f"ordinary beam ownership drifted: {rendered_owners}")

    partners = [
        placement for placement in cases.PLACEMENTS
        if placement.partner is not None
    ]
    if [placement.cell for placement in partners] != ["E1", "E2"]:
        fail("advanced partner cells drifted")
    for placement in partners:
        if placement.block_id != "laserio:laser_connector_advanced":
            fail("partnerDimPos may only be used on an advanced connector")
        partner = by_position.get(placement.partner)
        if partner is None or partner.partner != placement.position:
            fail(f"advanced partner is not reciprocal: {placement.cell}")
        if partner.laser_color != placement.laser_color:
            fail(f"advanced partner colors disagree: {placement.cell}")

    function_root = ROOT / f"datapack/data/{cases.NAMESPACE}/function"
    build = (function_root / "build.mcfunction").read_text(encoding="utf-8")
    verify = (function_root / "verify.mcfunction").read_text(encoding="utf-8")
    clear = (function_root / "clear.mcfunction").read_text(encoding="utf-8")
    functions = "\n".join(
        path.read_text(encoding="utf-8")
        for path in sorted(function_root.glob("*.mcfunction"))
    )
    if len(re.findall(r"^setblock ", build, re.MULTILINE)) != 12:
        fail("build must place exactly twelve target blocks")
    if len(re.findall(
        rf"^scoreboard players add #checked {cases.OBJECTIVE} 1$",
        verify,
        re.MULTILINE,
    )) != 25:
        fail("verify must retain 12 state, 12 NBT, and one build check")
    if build.count(
        f"scoreboard players add #builds {cases.OBJECTIVE} 1"
    ) != 1:
        fail("build must increment its persistent counter exactly once")
    expected_clear = (
        "fill 160 99 160 191 104 191 minecraft:air"
    )
    if clear.strip().splitlines()[-1] != expected_clear:
        fail("clear command escaped the exact bounded envelope")
    if any("wrenchAlpha:0" not in placement.nbt for placement in cases.PLACEMENTS):
        fail("every valid fixture must persist neutral wrench alpha")
    lowered = functions.lower()
    for forbidden in (
        "summon ",
        "data merge",
        "inventory0",
        "cardrenders",
        "showparticles",
        "op ",
        "deop ",
        "stop ",
    ):
        if forbidden in lowered:
            fail(f"excluded LaserIO path entered the gallery: {forbidden}")
    print(
        "LaserIO gallery lint passed: 12 valid cells, 2 ordinary beams, "
        "2 reciprocal advanced partners, 25 checks/phase"
    )
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, ValueError) as error:
        print(f"gallery lint failed: {error}", file=sys.stderr)
        raise SystemExit(1)
