# Notices

BlueMap LaserIO Add-on is independently authored under MIT for the
exact compatibility profile recorded in `provenance/upstreams.json`.

It compiles against the MIT-licensed BlueMap internal API at exact feature
backport commit `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac`. BlueMap is not
bundled.

Candidate artifacts are runtime evidence only. Their binaries, source, classes,
assets, and captured meshes are not redistributed.

The add-on compiles the MIT-licensed `FaceLighting` source from the exact
`bluemap-addon-render-core` gitlink into its namespace-neutral shared package.
The module JAR is not bundled or installed separately; its identity is
recorded in `THIRD_PARTY.md` and release provenance.

The add-on also compiles the MIT-licensed production sources from the exact
`bluemap-addon-adapter-api` gitlink. The standalone adapter module JAR is not
installed or nested.

The narrow renderer behavior is informed by the MIT-licensed LaserIO source at
commit `4d278a6351a434d343fbf70d942baeeaf2607536` (Copyright (c) 2022
Direwolf20-MC). Exact source paths and the upstream `LICENSE.txt` identity are
recorded in `THIRD_PARTY.md` and `provenance/upstreams.json`; no upstream file
or asset is packaged.
