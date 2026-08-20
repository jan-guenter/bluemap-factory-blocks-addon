# Factory Blocks staging gallery

This directory defines the deliberately small, deterministic datapack used to
review the Factory Blocks BlueMap prototype. It is confined to the inclusive
safe envelope x `160..223`, y `99..124`, z `160..191`. The clear function
splits that whole envelope into two fill-command-safe halves before every
build. A smooth-stone pad occupies x `160..211`, y `99`, z `160..168`.

The gallery contains exactly 25 asserted Factory Blocks placements, all in the
vertical x/y plane at z `164`:

| Section | Fixture | Exact coordinates | Count | Visual purpose |
| --- | --- | --- | ---: | --- |
| A | isolated `factory_blocks:factory` | `164 100 164` | 1 | disconnected texture |
| A | 3x3 `factory_blocks:factory` wall | x `168..170`, y `100..102`, z `164` | 9 | corner, edge, center, and internal-face culling |
| B | 3x3 `factory_blocks:hex` wall | x `180..182`, y `102..104`, z `164` | 9 | south faces viewed from +Z expose giant roles 1..9 once |
| C | 2x2 `factory_blocks:gears` wall | x `192..193`, y `100..101`, z `164` | 4 | connected selection with deterministic animation frame zero |
| D | stock `factory_blocks:metalbox` | `204 100 164` | 1 | ordinary-model control |
| D | stock `factory_blocks:piping` | `208 100 164` | 1 | weighted-variant control |

The hex wall starts at x `180`, y `102`, both divisible by three. On its south
faces the lower row selects roles 1, 2, 3; the middle row 4, 5, 6; and the
upper row 7, 8, 9. `placements.tsv` is the exact per-block coordinate ledger.

There is no NBT, block entity, spawner, fan/OptiFine fixture, or 35-block
census in this prototype gallery.

## Generate, lint, and package

Run from the repository root:

```text
PYTHONDONTWRITEBYTECODE=1 python3 gallery/generate.py --check
PYTHONDONTWRITEBYTECODE=1 python3 gallery/lint.py
bash gallery/package.sh /tmp/bluemap-factory-blocks-gallery.zip
```

Running `gallery/generate.py` without `--check` rewrites only the deterministic
datapack, `placements.tsv`, and `SHA256SUMS`. Packaging verifies the generator,
linter, and checksums, then creates the ZIP from sorted paths with stripped
metadata and a fixed DOS epoch. It bundles no Factory Blocks or Athena assets.

## Staging functions

```text
/function factory_blocks_gallery:build
/function factory_blocks_gallery:verify
/function factory_blocks_gallery:clear
/function factory_blocks_gallery:release
```

`build` increments the persistent `#builds` score in objective `fb_gallery`
before clearing and placing the fixture. The verifier requires that score to
equal one, making an accidental second build visible. Reset that score to zero
only when deliberately starting a fresh staging lifecycle.

The build verifies immediately and schedules retained-placement checks at 20
and 100 ticks. Every phase asserts all 25 target blocks plus the one-build
counter. Require:

```text
#immediate_checked = 26   #immediate_failures = 0
#20t_checked       = 26   #20t_failures       = 0
#100t_checked      = 26   #100t_failures      = 0
```

`release` cancels delayed checks and removes only the gallery's forceload
ticket; it deliberately retains the 25-block fixture for rendering.
