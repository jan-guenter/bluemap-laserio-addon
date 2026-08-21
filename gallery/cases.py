#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Family-owned valid LaserIO cases for the deterministic gallery."""

from __future__ import annotations

from dataclasses import dataclass


NAMESPACE = "laserio_gallery"
OBJECTIVE = "laserio_glr"
ENVELOPE = {
    "min_x": 160,
    "max_x": 191,
    "min_y": 99,
    "max_y": 104,
    "min_z": 160,
    "max_z": 191,
}
PAD = (160, 99, 160, 184, 99, 189)
FORCELOAD = (160, 160, 191, 191)
DIMENSION = "minecraft:overworld"

DEFAULT_RED = 0x54FF0000
CYAN = 0x5400CCFF
GREEN = 0x5455FF55
VIOLET = 0x54CC55FF
ORANGE = 0x54FFAA22
LINK_CYAN = 0x5422CCEE
MAGENTA = 0x54EE44AA


@dataclass(frozen=True)
class Offset:
    x: int
    y: int
    z: int


@dataclass(frozen=True)
class Position:
    x: int
    y: int
    z: int


@dataclass(frozen=True)
class Placement:
    cell: str
    section: str
    label: str
    x: int
    y: int
    z: int
    block_state: str
    laser_color: int
    connections: tuple[Offset, ...] = ()
    rendered_connections: tuple[Offset, ...] = ()
    partner: Position | None = None
    expected: str = "tinted-shell"

    @property
    def block_id(self) -> str:
        return self.block_state.split("[", 1)[0]

    @property
    def position(self) -> Position:
        return Position(self.x, self.y, self.z)

    @property
    def nbt(self) -> str:
        return block_entity_nbt(self)


def block_pos_nbt(position: Position | Offset) -> str:
    return f"{{X:{position.x},Y:{position.y},Z:{position.z}}}"


def connection_list(offsets: tuple[Offset, ...]) -> str:
    return "[" + ",".join(
        f"{{pos:{block_pos_nbt(offset)}}}" for offset in offsets
    ) + "]"


def block_entity_nbt(placement: Placement) -> str:
    fields = [
        f"connections:{connection_list(placement.connections)}",
        "renderedConnections:"
        f"{connection_list(placement.rendered_connections)}",
        f"myWorldPos:{block_pos_nbt(placement.position)}",
        f"laserColor:{placement.laser_color}",
        "wrenchAlpha:0",
    ]
    if placement.partner is not None:
        fields.append(
            "partnerDimPos:{"
            f'dimension:"{DIMENSION}",'
            f"blockpos:{block_pos_nbt(placement.partner)}"
            "}"
        )
    return "{" + ",".join(fields) + "}"


PLACEMENTS = (
    Placement(
        "A1", "defaults", "default laser node", 164, 100, 164,
        "laserio:laser_node", DEFAULT_RED,
    ),
    Placement(
        "A2", "defaults", "default basic connector facing down",
        170, 100, 164, "laserio:laser_connector[facing=down]", DEFAULT_RED,
    ),
    Placement(
        "A3", "defaults", "default advanced connector facing down",
        176, 100, 164,
        "laserio:laser_connector_advanced[facing=down]", DEFAULT_RED,
    ),
    Placement(
        "B1", "recolors", "cyan laser node", 164, 100, 171,
        "laserio:laser_node", CYAN,
    ),
    Placement(
        "B2", "recolors", "green basic connector facing east",
        170, 100, 171, "laserio:laser_connector[facing=east]", GREEN,
    ),
    Placement(
        "B3", "recolors", "violet advanced connector facing south",
        176, 100, 171,
        "laserio:laser_connector_advanced[facing=south]", VIOLET,
    ),
    Placement(
        "C1", "node-link", "node link renderer", 164, 100, 178,
        "laserio:laser_node", ORANGE,
        connections=(Offset(4, 0, 0),),
        rendered_connections=(Offset(4, 0, 0),),
        expected="tinted-shell+ordinary-beam-source",
    ),
    Placement(
        "C2", "node-link", "node link target", 168, 100, 178,
        "laserio:laser_node", ORANGE,
        connections=(Offset(-4, 0, 0),),
        expected="tinted-shell+ordinary-beam-target",
    ),
    Placement(
        "D1", "mixed-link", "basic connector diagonal-link renderer",
        176, 100, 178, "laserio:laser_connector[facing=east]", LINK_CYAN,
        connections=(Offset(4, 0, 3),),
        rendered_connections=(Offset(4, 0, 3),),
        expected="tinted-shell+ordinary-beam-source",
    ),
    Placement(
        "D2", "mixed-link", "advanced connector diagonal-link target",
        180, 100, 181,
        "laserio:laser_connector_advanced[facing=west]", LINK_CYAN,
        connections=(Offset(-4, 0, -3),),
        expected="tinted-shell+ordinary-beam-target",
    ),
    Placement(
        "E1", "advanced-partner", "advanced partner west port",
        164, 100, 186,
        "laserio:laser_connector_advanced[facing=east]", MAGENTA,
        partner=Position(171, 100, 186),
        expected="tinted-shell+advanced-port-beam",
    ),
    Placement(
        "E2", "advanced-partner", "advanced partner east port",
        171, 100, 186,
        "laserio:laser_connector_advanced[facing=west]", MAGENTA,
        partner=Position(164, 100, 186),
        expected="tinted-shell+advanced-port-beam",
    ),
)
