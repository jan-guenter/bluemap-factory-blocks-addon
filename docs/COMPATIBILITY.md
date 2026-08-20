# Compatibility

Compatibility is exact and evidence-locked.

| Component | Required identity |
| --- | --- |
| All the Mons | `1.2.0`, commit `c7bb230f21d14d26859d0b92548f089b3a493ad9` |
| Minecraft / NeoForge / Java | `1.21.1` / `21.1.248` / `21` |
| BlueMap | `5.22-agent.backport-5.22-mc1.21.1-2`, commit `9be321df995a1103808621d529eb72773e719d4d` |
| Factory Blocks | `factory_blocks-neoforge-1.4.0+mc1.21.1.jar`, 809,234 bytes, SHA-256 `404080fcf4747c6d84b73d1c204d047408aae476f57752bc5f38e9c16c7f51cd` |
| Athena | `athena-neoforge-1.21.1-4.0.6.jar`, 99,944 bytes, SHA-256 `43699885bbce3343916d4c5c4940cf0e3f9f6f02fdeb46e8655e121b42282ec5` |

Both runtime artifacts and the closed 35-block/260-resource schema are
mandatory. CTM and Chisel are not dependencies or activation inputs. The 14
non-Athena registered blocks remain stock regardless of whether CTM is
installed.

This profile makes no claim for another Factory Blocks or Athena build, later
All the Mons release, OptiFine/CTM fan rendering, generalized Athena formats,
or cross-mod appearance proxies. A changed tuple requires a new profile and
owner visual review.
