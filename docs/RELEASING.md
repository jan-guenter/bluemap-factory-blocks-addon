# Release procedure

No release exists. Owner visual acceptance of the exact staged candidate is
required before any publication work.

## Clean gate

Use Java 21, a complete Gradle distribution, the exact sibling BlueMap
checkout, and the two exact local artifacts:

```bash
python3 -B -m unittest discover -s tools/tests -v
gradle --no-daemon \
  -PfactoryBlocksJar=/absolute/path/factory_blocks-neoforge-1.4.0+mc1.21.1.jar \
  -PathenaJar=/absolute/path/athena-neoforge-1.21.1-4.0.6.jar \
  clean check build generatePomFileForAddonPublication \
  generateMetadataFileForAddonPublication verifyPinnedArtifacts
```

Inspect the production and sources JARs. Reject NeoForge metadata, nested
JARs, third-party classes/assets, gallery output, tests, research data, or
unexpanded metadata.

## Runtime and publication

Run [STAGING.md](STAGING.md) against that exact JAR, open the intended BlueMap
link for the required lightweight sanity check, and obtain explicit owner
acceptance. Do not substitute a rebuilt JAR afterward.

Before tagging, confirm the clean reviewed commit, pull-request version
change, tag `v<addon_version>`, release assets, Maven coordinates, checksums,
and provenance. Publication deploys nothing to the Minecraft server and must
not move an existing tag or replace immutable assets.
