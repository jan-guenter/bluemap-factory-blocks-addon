# Third-party and source provenance

## Adapted implementation source

| Component | Exact identity | License | Bundled upstream assets |
| --- | --- | --- | --- |
| BlueMap Chisel Add-on | tag `v0.1.0-alpha.1`, commit `f9131a5143062e2045cf26823aabb8628bb5d94d` | MIT | No |

## Runtime, evidence, and build inputs

| Component | Exact identity | Declared license/evidence | Bundled |
| --- | --- | --- | --- |
| BlueMap | backport commit `9be321df995a1103808621d529eb72773e719d4d` | MIT | No |
| Factory Blocks | `1.4.0+mc1.21.1`, 809,234 bytes, SHA-256 `404080fcf4747c6d84b73d1c204d047408aae476f57752bc5f38e9c16c7f51cd` | Exact NeoForge descriptor declares MIT; current distribution metadata conflicts, so no Factory Blocks source is adapted | No |
| Athena | `4.0.6`, 99,944 bytes, SHA-256 `43699885bbce3343916d4c5c4940cf0e3f9f6f02fdeb46e8655e121b42282ec5` | MIT; artifact/resource-format identity only | No |
| JetBrains annotations | `23.0.0` | Apache-2.0 | No |
| JUnit | `5.11.4` | EPL-2.0 | No |
| Checkstyle | `10.18.2` | LGPL-2.1-or-later | No |
| Gradle | compatible complete Gradle 9.x distribution | Apache-2.0 | No |

Factory Blocks tag `1.21.1-1.4.0`, commit
`c4fb3667688ed7b0498575c1f0c83f8ff9f45237`, is exact-correlated evidence,
not implementation source. The CTM `1.21-1.2.1+3` and Chisel content artifacts
are evidence-only and never enter generation, compilation, activation, or
packaging.
