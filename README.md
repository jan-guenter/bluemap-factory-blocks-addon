# BlueMap Factory Blocks Add-on

[![CI](https://github.com/jan-guenter/bluemap-factory-blocks-addon/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/jan-guenter/bluemap-factory-blocks-addon/actions/workflows/ci.yml)

An exact-profile BlueMap 5.23 feature-backport add-on for the Athena-backed connected models in
Factory Blocks as shipped by All the Mons 1.2.0.

## Status

Version `0.1.0-alpha.2` is the owner-accepted BlueMap 5.23 migration release
candidate. It preserves the alpha.1 renderer while replacing duplicate adapter
and Athena model primitives with exact source-module pins. Its production JAR
is 90,486 bytes with SHA-256
`bfa0c9aa6a96425fa62a5acf3a4a4e5c212e6537378da5bc8e1f59c19d09b0a0`.

The only supported input tuple is:

- Factory Blocks `1.4.0+mc1.21.1`, file
  `factory_blocks-neoforge-1.4.0+mc1.21.1.jar`, 809,234 bytes, SHA-256
  `404080fcf4747c6d84b73d1c204d047408aae476f57752bc5f38e9c16c7f51cd`;
- Athena `4.0.6`, file `athena-neoforge-1.21.1-4.0.6.jar`, 99,944 bytes,
  SHA-256
  `43699885bbce3343916d4c5c4940cf0e3f9f6f02fdeb46e8655e121b42282ec5`;
- Minecraft `1.21.1`, NeoForge `21.1.248`, Java `21`;
- BlueMap feature backport
  `5.22-feature.backport-5.23-stateless-java-web-server-46` at commit
  `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac`, API commit
  `285c9a60eff3ac2b0cab308ce1058d1565be0971`.

The route begins inactive. It activates only when both installed JARs and the
active owned JSON schemas match the exact profile. A changed artifact,
resource schema, required texture, or registry slot keeps all routed blocks on
BlueMap's stock path. Pixel-only texture-pack overrides remain supported when
the schema and texture IDs stay exact.

## Visual scope

Factory Blocks registers 49 blocks. This add-on routes only the 35
property-free blocks whose installed blockstates use Athena loaders:

| Athena loader | Routed blocks |
| --- | ---: |
| CTM | 32 |
| Giant 3×3 | 3 |
| **Total** | **35** |

The other 14 registered blocks remain stock. This includes all fans,
`fan_side`, `megacell`, `metalbox`, and every ordinary or weighted state. The
CTM mod's OptiFine fan rule is deliberately not implemented or required.

CTM faces sample eight same-state, face-local neighbors and select four
quadrant roles from `particle`, `empty`, `center`, `vertical`, and
`horizontal`. Giant faces select roles `1` through `9` from stable absolute
coordinates using the exact 3×3 face mirroring. The five animated
`factory_blocks:block/ctm/gears/{0..4}` role textures render frame zero only.

The profile pins 260 installed resource paths: 35 blockstates, 35 models, and
190 unique role PNGs. No third-party JSON, PNG, class, source, or JAR is
bundled. Malformed observations or rendering failures discard partial output,
restore the initial map color, and use BlueMap's original renderer for the
whole block.

The deliberately small [gallery](gallery/README.md) covers a disconnected CTM
block, a CTM 3×3 wall, every giant south-face role in one 3×3 wall, connected
animated gears, and two stock controls. Its reproducible ZIP is exactly 3,707
bytes with SHA-256
`cbea339239d7ddcfd2a771de204d64ba63e1941d28e74416c51919c32888e25b`.
All three retained-placement phases passed 26/26 checks with zero failures.

## Build

Clone recursively, then use Java 21, the exact BlueMap checkout, and the two
operator-supplied artifacts. The build rejects missing, dirty, or incorrectly
pinned support modules.

```bash
python3 -B -m unittest discover -s tools/tests -v
gradle --no-daemon \
  -PbluemapSourcePath=/absolute/path/to/bluemap-backport \
  -PfactoryBlocksJar=/absolute/path/factory_blocks-neoforge-1.4.0+mc1.21.1.jar \
  -PathenaJar=/absolute/path/athena-neoforge-1.21.1-4.0.6.jar \
  clean prototypeCheck build
```

The exact release gate is documented in [docs/RELEASING.md](docs/RELEASING.md).
It proves the production and sources JARs plus Maven metadata byte-for-byte,
rechecks the exact Factory Blocks/Athena inputs, and reproduces the accepted
gallery archive. Tagged releases use Maven coordinate
`io.github.jan-guenter:bluemap-factory-blocks-addon:<version>`; the annotated
tag must equal `v<addon_version>`.

## Installation

Place only the plain add-on JAR in BlueMap's `config/bluemap/packs` directory
and restart the JVM. It is not a NeoForge mod and does not belong in the
server's `mods` directory. Removal plus one restart restores stock rendering;
the add-on writes no world or player data.

## License and provenance

This project is MIT-licensed. It adapts the owner's MIT
[BlueMap Chisel Add-on](https://github.com/jan-guenter/bluemap-chisel-addon)
at tag `v0.1.0-alpha.1`, commit
`f9131a5143062e2045cf26823aabb8628bb5d94d`. Factory Blocks and Athena are
operator-installed resource inputs only; none of their code or assets is
copied. See [THIRD_PARTY.md](THIRD_PARTY.md), [NOTICE.md](NOTICE.md), and
[provenance](docs/PROVENANCE.md).
