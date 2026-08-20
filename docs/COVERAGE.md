# Visual coverage

The exact Factory Blocks 1.4.0 source/runtime correlation establishes 49
registered blocks. The profile routes the 35 property-free blocks whose exact
blockstates use Athena:

| Athena loader | Blocks |
| --- | ---: |
| CTM | 32 |
| giant 3×3 | 3 |
| **Total routed** | **35** |
| **Registered stock** | **14** |

The 14 stock IDs are `fan`, `fan_four`, `fan_four_on`, `fan_malfunction`,
`fan_malfunction_on`, `fan_on`, `fan_side`, `medium_fan`, `megacell`,
`metalbox`, `piping`, `rust_bplates`, `wgpanel`, and `wopanel`. The artifact
also carries three unregistered `engineer1..3` blockstate resources; they are
never routed.

## Resource closure

| Class | Paths |
| --- | ---: |
| Blockstates | 35 |
| Models | 35 |
| Role PNGs | 190 |
| **Total** | **260** |

The sorted `<path>\t<sha256>` manifest has SHA-256
`f5bca2172149b885ac3b4899c4ede36629adbd8605ae80dd385cbde79b9d1873`.
All bytes come from operator-installed resource roots; only factual IDs and
hashes are packaged.

## Included behavior

- All 256 Athena CTM neighbor masks and face-local quadrants.
- Same-state connection and same-native-ID internal-face suppression.
- Giant roles `1..9` on all faces, including exact east/north/down mirroring,
  positive and negative coordinates, and `Integer.MIN_VALUE`.
- Alpha-sensitive full-cube culling, surface/cave rules, lighting, winding,
  UVs, map color, and atomic fallback inherited from the reviewed MIT
  interpreter.
- Deterministic top-frame output for exactly
  `factory_blocks:block/ctm/gears/{0,1,2,3,4}`.

## Excluded behavior

All fan and `fan_side` OptiFine/CTM rules, animation playback, contents, NBT,
block entities, transient state, and generalized resource formats are out of
scope. The [25-placement gallery](../gallery/README.md) is representative,
not an exhaustive census. No runtime or owner result is recorded until
staging actually runs.
