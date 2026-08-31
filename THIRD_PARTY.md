# Third-party and source provenance

## Adapted implementation source

| Component | Exact identity | License | Bundled upstream assets |
| --- | --- | --- | --- |
| BlueMap Chisel Add-on | tag `v0.1.0-alpha.1`, commit `f9131a5143062e2045cf26823aabb8628bb5d94d` | MIT | No |

## Runtime, evidence, and build inputs

| Component | Exact identity | Declared license/evidence | Bundled |
| --- | --- | --- | --- |
| BlueMap | feature-backport commit `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac`, API commit `285c9a60eff3ac2b0cab308ce1058d1565be0971` | MIT | No |
| BlueMap Add-on Adapter API | `v0.1.0-alpha.2`, commit `e81f08bc4bfbf02d810ec8949a019130e2e61634`, source tree `2f974c9bb2ba13888d69682f86f30f58922d30eb` | MIT; exact gitlink-pinned sources compiled into this JAR | Yes, source-derived classes only |
| BlueMap Athena Resource Models | `v0.1.0-alpha.1`, commit `4a503a63f7f10b7c414c6c1228207a5ba00bfd54`, source tree `882689c2f9a0875547f4e30aefd68659103d5046` | MIT; exact gitlink-pinned sources compiled into this JAR | Yes, source-derived classes only |
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
