# Agent guide for BlueMap Factory Blocks Add-on

Read `/root/work/allthemons/AGENTS.md` and this file before changing this
repository. This is a standalone MIT BlueMap add-on, not a NeoForge mod and
not part of the root orchestration repository.

## Exact baseline

| Component | Identity |
| --- | --- |
| All the Mons | `1.2.0`, pack commit `c7bb230f21d14d26859d0b92548f089b3a493ad9` |
| Minecraft / NeoForge / Java | `1.21.1` / `21.1.248` / `21` |
| BlueMap | backport `5.22-agent.backport-5.22-mc1.21.1-2`, commit `9be321df995a1103808621d529eb72773e719d4d` |
| Factory Blocks | `1.4.0+mc1.21.1`, 809,234 bytes, SHA-256 `404080fcf4747c6d84b73d1c204d047408aae476f57752bc5f38e9c16c7f51cd` |
| Athena | `4.0.6`, 99,944 bytes, SHA-256 `43699885bbce3343916d4c5c4940cf0e3f9f6f02fdeb46e8655e121b42282ec5` |

A changed pack, BlueMap build, Factory Blocks artifact, or Athena artifact
starts a fresh evidence and visual-review task.

## Project boundaries

- Route exactly 35 property-free `factory_blocks:*` IDs: 32 `athena:ctm`
  definitions and three 3×3 `athena:giant` definitions.
- Leave the other 14 registered Factory Blocks IDs stock, including every fan,
  `fan_side`, `megacell`, `metalbox`, and ordinary or weighted model.
- The exact resource closure is 260 operator-installed paths: 35 blockstates,
  35 models, and 190 role PNGs. Its manifest SHA-256 is
  `f5bca2172149b885ac3b4899c4ede36629adbd8605ae80dd385cbde79b9d1873`.
- Render only frame zero of the five routed `gears` role textures. Animation
  playback and every fan/OptiFine rule are out of scope.
- Activate only for the exact Factory Blocks/Athena artifact pair and exact
  active resource schemas. Any route-wide mismatch keeps all 35 IDs stock.
- Keep per-block fallback atomic. Reset partial geometry and map color before
  stock fallback or propagation of a capacity exception.
- Bundle no Factory Blocks, Athena, CTM, Chisel, or Minecraft code or assets.
  The CTM mod and Chisel content JAR are not build or activation inputs.
- The implementation adapts only the owner's MIT BlueMap Chisel Add-on at tag
  `v0.1.0-alpha.1`, commit
  `f9131a5143062e2045cf26823aabb8628bb5d94d`.
- Do not edit `gallery/**` concurrently with its dedicated owner.

## Validation cadence

For a prototype, run the focused Python checks, Java tests, and package audit
with the exact two local inputs. A release still requires the documented clean
gate after owner visual acceptance.

```bash
python3 -B -m unittest discover -s tools/tests -v
gradle --no-daemon \
  -PfactoryBlocksJar=/absolute/path/factory_blocks-neoforge-1.4.0+mc1.21.1.jar \
  -PathenaJar=/absolute/path/athena-neoforge-1.21.1-4.0.6.jar \
  test jar verifyProductionJar verifyPinnedArtifacts
```

Do not record staging, browser, owner-acceptance, publication, or release
claims until those exact gates have actually run.
