# Architecture

This repository produces one plain BlueMap add-on JAR. It has no NeoForge
metadata, client renderer, world state, packet, nested dependency, or required
configuration.

```text
BlueMap entrypoint and unique adapter IDs
        |
exact Factory Blocks + Athena artifact gate
        |
first-resource-wins active JSON schema gate
        |
35 immutable definitions (32 CTM + 3 giant 3×3)
        |
bounded emission -> atomic stock fallback
```

## Activation and resources

The route starts inactive. It requires the exact 809,234-byte Factory Blocks
1.4.0 JAR, exact 99,944-byte Athena 4.0.6 JAR, all 35 active blockstates and
models, and all 190 role texture IDs. The 260-line installed-resource manifest
has SHA-256
`f5bca2172149b885ac3b4899c4ede36629adbd8605ae80dd385cbde79b9d1873`.

Active JSON validation follows BlueMap's resource-root ordering and accepts
only the first resource for each path. A malformed higher-priority winner
deactivates the route; it is never bypassed in favor of a lower-priority file.
Pixel-only overrides remain valid when all schemas and texture IDs stay exact.

The add-on installs no `factory_blocks:*` asset. The five routed gears strips
are replaced in BlueMap's runtime texture table by their top square frame.
Stock fan animations and the CTM mod's OptiFine rule are untouched.

## CTM

Each visible face samples eight same-state neighbors in the face plane. The
unsigned mask selects four quadrant roles from `particle`, `empty`, `center`,
`vertical`, and `horizontal`. A direct face is suppressed only for the same
native block ID.

## Giant 3×3

Each face selects roles `1..9` from stable absolute coordinates. East, north,
and down apply Athena's exact mirror offset four before modulo-three role
selection. Coordinate conversion is long-safe for `Integer.MIN_VALUE`.

## Failure and isolation

A changed artifact, resource schema, required texture, or registration keeps
all 35 blocks stock. A per-block failure resets custom geometry and map color
before running BlueMap's original renderer. If either custom or stock fallback
emission reaches capacity, partial block output is reset and the original
capacity exception propagates.

All entrypoint, renderer, extension, and synthetic IDs use the
`bluemap_factory_blocks` namespace and are distinct from other published
add-ons.
