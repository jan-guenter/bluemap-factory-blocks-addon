# Disposable staging gate

Status: passed and owner accepted on 2026-08-20 for commit
`236ff727f18694d1b7509a763fdafe2375768e05`, tree
`47c816c8a5b3cbc41ee682dcd6a210ad492ad4d9`, and the exact 86,501-byte JAR
with SHA-256
`69f4f53022aac455a4bcc362dc09cbaf5b3f73cf108ccc154dffa8e238869302`.

Reuse only the authorized disposable All the Mons 1.2.0 staging server. Before
changing it, snapshot the current accepted staging state for rollback. Install
the exact Factory Blocks/Athena pair and the exact candidate JAR in BlueMap's
packs directory. CTM and Chisel are not add-on inputs.

Use the shared low-cost staging settings, including disabled time/weather,
random ticks, mobs, patrols, traders, wardens, raids, sound events, player
movement checks, PvP, and environmental damage. `spawner_blocks_work` is not
available on Minecraft 1.21.1; prove the gallery contains no spawner instead
of setting an invalid rule.

## Fixture

Install the deterministic [gallery](../gallery/README.md), then run:

```text
/function factory_blocks_gallery:build
```

Require the immediate, 20-tick, and 100-tick phases to each report 26 checks
and zero failures: 25 exact placements plus the one-build counter. The fixture
contains:

- one isolated `factory_blocks:factory` block and one 3×3 factory CTM wall;
- one `factory_blocks:hex` 3×3 wall exposing south-face roles `1..9`;
- one 2×2 connected `factory_blocks:gears` wall using deterministic frame zero;
- stock `factory_blocks:metalbox` and `factory_blocks:piping` controls.

Require exact-profile activation with no adapter/resource/render fault, then
purge and render only the bounded Factory Blocks map. Verify that the three
routed fixtures use custom output while both controls retain stock output.

Open the exact external BlueMap link in the agent browser and check that it is
not blank, black, missing, or grossly broken before giving it to the owner.
Keep the matching Minecraft staging server running for direct comparison.

Record the candidate JAR size/SHA, gallery package identity, pod identity and
restart count, verifier scores, map name, link check, and owner response only
after observing them. Do not publish before explicit owner acceptance.

The exact 3,707-byte gallery archive had SHA-256
`cbea339239d7ddcfd2a771de204d64ba63e1941d28e74416c51919c32888e25b`.
Immediate, 20-tick, and 100-tick verification each passed 26 checks with zero
failures. The bounded Factory Blocks map and matching modded-client rendering
were visually accepted. These are disposable staging results, not production
deployment evidence.
