# Add-on execution

This repository remains inactive and stock-safe until the exact
`laserio-1.9.11` artifact and installed resource contract are admitted. The
prototype scope is frozen to the three tinted installed shells, ordinary
persisted `renderedConnections` beams, and deterministic short advanced
partner port beams. Card/capability paths, flow/activity, particles,
held-wrench alpha, animation, and cross-dimension partner lines are excluded.

Before running Gradle gates, activate a Python 3.11 or newer virtual
environment and install the exact development-only toolkit into it:

```bash
python -m pip install --disable-pip-version-check --no-deps \
  --require-hashes --only-binary=:all: \
  --requirement requirements/toolkit.txt
```

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
