# BlueMap LaserIO Add-on

A Java 21 BlueMap add-on for the exact `laserio-1.9.11` profile in All the Mons
`1.2.0` / Minecraft `1.21.1`.

The adapter for the exact tested BlueMap 5.23 feature backport replays the
operator-installed models for all three LaserIO blocks. It applies persisted
`laserColor` to their tinted faces and adds only the persisted connection
geometry described below. Unsupported or malformed inputs retain BlueMap's
stock rendering.

## Build

Clone with `--recurse-submodules`, or initialize an existing checkout with
`git submodule update --init --recursive`, before invoking Gradle.

```bash
gradle --no-daemon -PbluemapSourcePath=../bluemap-backport clean check build
```

`check` is the quick Java/checkstyle/archive gate. `prototypeCheck` also
requires every exact candidate JAR property and validates the deterministic
twelve-cell gallery. See `provenance/upstreams.json` for immutable artifact
and source identities. Release identity and verification are recorded in
`provenance/release.json`.

The pinned `modules/bluemap-addon-runtime` source module supplies the neutral
exact-artifact detector classes. Gradle compiles those sources into this
add-on; the runtime module JAR is neither nested nor installed separately.
The gitlink pins `v0.1.0-alpha.1` commit
`6c062239f2669de9d20da32dc8b5372a5653b19d` and the settings preflight rejects
an uninitialized, changed, or dirty checkout.

The pinned `modules/bluemap-addon-render-core` source module supplies the
byte-equivalent face-lighting sampler shared by compatible BlueMap adapters.
Its source is also compiled directly into this add-on; no shared runtime JAR
is installed or nested. The settings preflight pins both its exact
commit and production-source tree and rejects an uninitialized or dirty copy.

The pinned `modules/bluemap-addon-adapter-api` source module supplies the
exact 5.23 feature-backport runtime identity, registry guards, resource
extension wrapper, and synthetic-dispatch validator. Gradle compiles its four
production sources into this add-on. The standalone module JAR is neither
installed nor nested. The gitlink pins `v0.1.0-alpha.2` commit
`e81f08bc4bfbf02d810ec8949a019130e2e61634`. The consumer accepts only commit
`7e07f4e74ec1e92a6ead9aa1e66054af3e133aac` of the BlueMap feature backport.

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
