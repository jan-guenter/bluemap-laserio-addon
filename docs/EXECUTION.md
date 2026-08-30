# Add-on execution

This repository remains inactive and stock-safe until the exact
`laserio-1.9.11` artifact and installed resource contract are admitted. The
prototype scope is frozen to the three tinted installed shells, ordinary
persisted `renderedConnections` beams, and deterministic short advanced
partner port beams. Card/capability paths, flow/activity, particles,
held-wrench alpha, animation, and cross-dimension partner lines are excluded.

Before running Gradle gates, activate a Python 3.11 or newer virtual
environment, initialize the exact development-tool and three source modules,
and install the matching toolkit wheel into it:

```bash
git submodule update --init --recursive -- \
  tooling/bluemap-addon-toolkit modules/bluemap-addon-runtime \
  modules/bluemap-addon-render-core modules/bluemap-addon-adapter-api
python -m pip install --disable-pip-version-check --no-deps \
  --require-hashes --only-binary=:all: \
  --requirement requirements/toolkit.txt
```

The requirement locks the 20,585-byte `v0.3.0-alpha.1` wheel at SHA-256
`82f1ec53603646849a7c2d4b58f3fb7000413fe83043a302bee88cc88daeb8f7`.
The runtime gitlink pins `v0.1.0-alpha.1` commit
`6c062239f2669de9d20da32dc8b5372a5653b19d`; its main sources are compiled
into the add-on and its standalone JAR is never installed or nested.
The render-core gitlink similarly contributes only its exact pinned production
source; its standalone JAR is never installed or nested.
The adapter-API gitlink contributes four exact production sources. Its
standalone JAR is also never installed or nested. The settings preflight
accepts only the exact BlueMap feature-backport commit
`7e07f4e74ec1e92a6ead9aa1e66054af3e133aac`.

## Prototype

Acquire and verify the exact candidate JARs outside Git. Their Gradle
properties are:

- `-PlaserIoJar=/path/to/laserio-1.9.11.jar`

Then run:

```bash
gradle --no-daemon -PbluemapSourcePath=../bluemap-backport \
  <exact-candidate-properties> clean prototypeCheck build
bash gallery/package.sh /tmp/laserio-gallery.zip
```

Deploy that JAR and gallery only to disposable staging, verify the intended
BlueMap link loads, and compare it with the matching client. Iterate from
observed defects until the owner explicitly accepts one exact staging JAR.

## Acceptance and release

Freeze that accepted JAR's functional entries once; the writer refuses to
overwrite an existing acceptance record:

```bash
bluemap-addon-toolkit jar-entries write \
  --jar /absolute/path/accepted-staging.jar \
  --entries provenance/accepted-staging-entries.sha256
```

Record the manifest in `provenance/release.json` as
`accepted_staging_entries` with exact `path`, `entry_count`, and `sha256`.
Record `visual_acceptance: true` under `owner_accepted_staging`, and record the
production JAR, sources JAR, POM and Gradle module file names, sizes and hashes
under `final_release_artifacts`.

Promote `addon_version` through a pull request, confirm no generated
implementation placeholders remain, and run with all exact candidate
properties:

```bash
gradle --no-daemon -PbluemapSourcePath=../bluemap-backport \
  <exact-candidate-properties> -PreleaseTag=v<version> \
  clean build generatePomFileForAddonPublication \
  generateMetadataFileForAddonPublication verifyReleaseCandidate
```

Merge only after final-version CI passes this gate. Create an annotated
`v<version>` tag at reviewed `main`; the release workflow independently checks
the tag, exact BlueMap checkout, accepted bytes and draft assets before making
the prerelease public. Publication never deploys to production.
