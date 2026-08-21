# BlueMap LaserIO Add-on

A Java 21 BlueMap add-on for the exact `laserio-1.9.11` profile in All the Mons
`1.2.0` / Minecraft `1.21.1`.

Status: narrow renderer prototype. After exact-artifact admission, the BlueMap
5.22 adapter replays the operator-installed models for all three LaserIO
blocks, applies persisted `laserColor` to their tinted faces, and adds only the
persisted connection geometry described below. Unsupported or malformed
inputs retain BlueMap's stock rendering. Visual acceptance and release sealing
remain pending.

## Build

```bash
gradle --no-daemon -PbluemapSourcePath=../bluemap-backport clean check build
```

`check` is the quick Java/checkstyle/archive gate. `prototypeCheck` additionally
requires every exact candidate JAR property and validates the deterministic
twelve-cell gallery. See `provenance/upstreams.json` for immutable artifact
and source identities and the [execution guide](docs/EXECUTION.md) for the
prototype-to-release loop.

## Install

Place the production JAR in BlueMap's add-on pack directory and restart the
BlueMap JVM. Removal plus one restart restores stock behavior; the add-on
creates no custom world state.

Set `-Dbluemap.laserio.disabled=true` to leave the exact profile inactive.

## Scope boundary

The exact scope is:

- replay the installed `laser_node`, `laser_connector`, and
  `laser_connector_advanced` JSON shells and apply persisted `laserColor` only
  to faces with a tint index;
- add deterministic fullbright crossed-quad beams only for valid relative
  offsets stored in `renderedConnections`, using the operator-installed
  `laserio:textures/misc/laser.png`; and
- for a valid advanced `partnerDimPos`, add only a deterministic short facing
  port beam, never a line to the remote partner.

Card/capability beams, item flow, activity and redstone state, particles,
`showParticles`, held-wrench alpha, animation/pulsing, and cross-dimension
partner lines are excluded. Malformed or out-of-range overlay data is omitted while
the admitted stock shell remains.

No LaserIO binary, source, class, asset, captured mesh, or gallery is
bundled in the add-on.
