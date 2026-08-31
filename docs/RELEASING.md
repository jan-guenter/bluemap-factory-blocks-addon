# Release procedure

Releases are promoted only from an owner-accepted, independently audited
commit on `main`. The accepted candidate identity is recorded in
`provenance/release.json`.

## Clean gate

Use Java 21, a complete Gradle distribution, the exact sibling BlueMap
checkout, and the two exact local artifacts:

```bash
python3 -B -m unittest discover -s tools/tests -v
gradle --no-daemon \
  -PbluemapSourcePath=/absolute/path/to/bluemap-backport \
  -PfactoryBlocksJar=/absolute/path/factory_blocks-neoforge-1.4.0+mc1.21.1.jar \
  -PathenaJar=/absolute/path/athena-neoforge-1.21.1-4.0.6.jar \
  -PreleaseTag=v0.1.0-alpha.2 \
  clean check build generatePomFileForAddonPublication \
  generateMetadataFileForAddonPublication verifyPublicationArtifacts \
  verifyReleaseCandidate
```

Inspect the production and sources JARs. Reject NeoForge metadata, nested
JARs, third-party classes/assets, gallery output, tests, research data, or
unexpanded metadata.

## Runtime and publication

Run [STAGING.md](STAGING.md) against that exact JAR, open the intended BlueMap
link for the required lightweight sanity check, and obtain explicit owner
acceptance. Do not substitute a rebuilt JAR afterward.

Before tagging, merge the independently audited release pull request. Create
and push an annotated `v<addon_version>` tag at that reviewed `main` commit.
The release workflow reproduces every accepted byte, creates a draft
prerelease, uploads and attests the assets, publishes the Maven package,
verifies the draft assets, and only then makes the prerelease public.

Never reuse or move a release tag. A failed prepublication run may be resumed
with the workflow's exact immutable tag input while its GitHub release remains
a draft. Publication deploys nothing to the Minecraft server.
