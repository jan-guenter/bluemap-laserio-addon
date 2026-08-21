# Third-party material

## BlueMap

- Exact backport: `5.22-agent.backport-5.22-mc1.21.1-2`
- Commit: `9be321df995a1103808621d529eb72773e719d4d`
- API commit: `285c9a60eff3ac2b0cab308ce1058d1565be0971`
- License: MIT
- Use: compile-only internal renderer API and owner-authored adapter patterns
- Redistributed: complete license notice only, as `LICENSE-BlueMap`

## LaserIO

- Exact runtime: `laserio-1.9.11.jar`, 1,305,285 bytes
- SHA-1: `0545bff65926346c9663fe3c397029561a87400f`
- SHA-256: `03e8537d75bc2f4ced2fc214d3409753e684d1056ee63b26db7a2b9e199ef4df`
- Source repository: `https://github.com/Direwolf20-MC/LaserIO`
- Exact source commit: `4d278a6351a434d343fbf70d942baeeaf2607536`
- Exact source tree: `37b76004c4c442106ab875c11562223366dd50e1`
- License: MIT, declared in upstream `LICENSE.txt`
- Copyright notice: Copyright (c) 2022 Direwolf20-MC
- Source-use lane: compact MIT behavior adaptation plus interpretation of
  operator-installed models and `laserio:textures/misc/laser.png`
- Redistributed: no LaserIO source file, class, binary, model, texture, or
  captured mesh

The exact source paths used to bound the implementation are:

| Evidence | Path at the exact commit |
| --- | --- |
| three block and block-entity registrations | `src/main/java/com/direwolf20/laserio/setup/Registration.java` |
| installed-model tint registration | `src/main/java/com/direwolf20/laserio/setup/ClientSetup.java` |
| persisted color and ordinary-link NBT | `src/main/java/com/direwolf20/laserio/common/blockentities/basebe/BaseLaserBE.java` |
| ordinary connection render queue | `src/main/java/com/direwolf20/laserio/client/blockentityrenders/baseberender/BaseLaserBERender.java` |
| ordinary beam texture, color, lighting, and geometry | `src/main/java/com/direwolf20/laserio/client/renderer/RenderUtils.java` |
| advanced `partnerDimPos` persistence | `src/main/java/com/direwolf20/laserio/common/blockentities/LaserConnectorAdvBE.java` |
| advanced facing endpoint behavior | `src/main/java/com/direwolf20/laserio/client/blockentityrenders/LaserConnectorAdvBERender.java` |
| global-position NBT codec | `src/main/java/com/direwolf20/laserio/util/MiscTools.java` |
| ordinary eight-block range | `src/main/java/com/direwolf20/laserio/common/items/LaserWrench.java` |
| upstream license | `LICENSE.txt` |

The repository does not reproduce LaserIO source expression. Its implementation
independently maps the exact persisted fields to BlueMap primitives and asks
BlueMap to resolve the operator-installed resources only after exact-artifact
admission. The complete machine-readable record is
`provenance/upstreams.json`.
