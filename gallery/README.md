# LaserIO staging gallery

This directory defines the bounded deterministic datapack used to review the
exact LaserIO `1.9.11` BlueMap prototype. The exact operator-installed runtime
JAR is 1,305,285 bytes with SHA-1
`0545bff65926346c9663fe3c397029561a87400f` and SHA-256
`03e8537d75bc2f4ced2fc214d3409753e684d1056ee63b26db7a2b9e199ef4df`.

The gallery is synthetic prototype evidence. It directly places exact block
states and the persisted `laserColor`, `connections`, `renderedConnections`,
`myWorldPos`, `wrenchAlpha`, and `partnerDimPos` fields used by LaserIO
`1.9.11`; it does not claim that a player created or saved these networks. The
fixture is confined to inclusive x `160..191`, y `99..104`, z `160..191` in a
disposable staging world. Its smooth-stone pad occupies x `160..184`, y `99`,
z `160..189`.

## Twelve valid cells

| Cells | Exact coordinates | Fixture | Expected add-on result |
| --- | --- | --- | --- |
| A1–A3 | `164/170/176 100 164` | default-red node, down-facing basic connector, and down-facing advanced connector | all three installed stock shells with their tinted faces red |
| B1–B3 | `164/170/176 100 171` | cyan node, green east-facing basic connector, violet south-facing advanced connector | shell tint follows each persisted ARGB value |
| C1–C2 | `164/168 100 178` | reciprocal node-to-node link; C1 owns relative `renderedConnections` offset `4 0 0` | one orange center-to-center ordinary beam, never a duplicate |
| D1–D2 | `176 100 178` and `180 100 181` | reciprocal basic-to-advanced link; D1 owns relative offset `4 0 3` | one cyan diagonal ordinary beam, never a duplicate |
| E1–E2 | `164/171 100 186` | east/west-facing advanced connectors with reciprocal same-dimension `partnerDimPos` | one deterministic short facing port beam at each endpoint; no world-spanning partner line |

Every ordinary link stores reciprocal relative `connections`, but only its
lower-numbered source cell stores the offset in `renderedConnections`, matching
LaserIO's one-owner rendering rule. Every connection is nonzero and strictly
inside LaserIO's eight-block ordinary-link range. Both advanced partner records
use the exact `minecraft:overworld` plus absolute `blockpos` structure and point
back to each other.

The comparison target is deliberately narrow: replay the three
operator-installed JSON shells, apply `laserColor` only to faces with a tint
index, draw ordinary persisted `renderedConnections` with the installed
`laserio:textures/misc/laser.png`, and show the deterministic advanced port
beam for a valid partner. Missing or malformed overlays must leave the stock
shell intact.

Card-to-inventory and capability beams, item flow, activity/redstone state,
particles, `showParticles`, held-wrench alpha, animation phase, pulsing portal
geometry, and cross-dimension partner lines are not comparison targets. The
gallery contains no invalid or dangling records; those fallback cases belong
in unit tests.

## Generate, lint, and package

Run from the repository root:

```text
PYTHONDONTWRITEBYTECODE=1 python3 gallery/generate.py --check
PYTHONDONTWRITEBYTECODE=1 python3 gallery/lint.py
bash gallery/package.sh /tmp/bluemap-laserio-gallery.zip
```

Running `gallery/generate.py` without `--check` rewrites only the generated
ledger, datapack files, and `SHA256SUMS`. Packaging verifies them and creates a
ZIP from sorted paths with fixed modes, stripped metadata, and a fixed DOS
epoch. It bundles no LaserIO, Minecraft, or BlueMap code, model, texture, or
captured mesh.

## Staging functions

```text
/function laserio_gallery:build
/function laserio_gallery:verify
/function laserio_gallery:clear
/function laserio_gallery:release
```

`build` increments a persistent one-build counter, clears the bounded
envelope, creates the pad and twelve cells, verifies immediately, and repeats
the same compact check after 20 ticks. Each phase checks twelve exact block
states, twelve persisted NBT subsets, and the one build counter. Require:

```text
#immediate_checked = 25   #immediate_failures = 0
#20t_checked       = 25   #20t_failures       = 0
```

`release` cancels the delayed check and removes only this gallery's forceload
ticket; it deliberately retains the fixture for BlueMap rendering. To rebuild
deliberately in the disposable world, clear the fixture and reset `#builds` in
objective `laserio_glr` to zero first.
