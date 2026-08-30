# Releasing

Prototype work is intentionally light. Before owner acceptance, run only the
focused Java checks, exact candidate verifier, gallery checks, and disposable
staging comparison needed to get useful visual feedback.

After the owner accepts the candidate:

1. Initialize the pinned development toolkit, runtime source module, and
   render-core source module with `git submodule update --init --recursive --
   tooling/bluemap-addon-toolkit modules/bluemap-addon-runtime
   modules/bluemap-addon-render-core`.
2. Confirm the accepted renderer remains inside the documented narrow scope
   and the deterministic twelve-cell gallery exactly matches that candidate.
3. Freeze the accepted staging JAR's non-manifest entry hashes in
   `provenance/accepted-staging-entries.sha256` with the one-time
   `bluemap-addon-toolkit jar-entries write` command.
4. Change `addon_version` from the SNAPSHOT to its final version through a PR.
5. Build production JAR, sources JAR, POM, and Gradle module metadata with the
   exact promotion Java/Gradle/BlueMap inputs.
6. Put their exact sizes and SHA-256 values in `gradle.properties` and complete
   `provenance/release.json`.
7. Run `verifyReleaseCandidate -PreleaseTag=v<version>` with all exact candidate
   JAR Gradle properties.
8. Merge the reviewed commit, create an annotated `v<version>` tag at that
   commit, and let `.github/workflows/release.yml` publish.
9. Compare every downloaded release asset to the locally accepted bytes.
10. Update the private root portfolio, queue, and `workspace.json` in a separate
   orchestration commit.

The tag must exactly equal `v<addon_version>`. No release authorizes production
deployment.

The command sequence and required release-provenance fields are recorded in
[`EXECUTION.md`](EXECUTION.md).
