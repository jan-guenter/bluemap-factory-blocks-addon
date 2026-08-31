# Provenance

The machine-readable artifact lock is
`src/main/resources/bluemap-factory-blocks/profiles/exact-artifacts.json`.
The generated profile is under
`src/main/resources/bluemap-factory-blocks/profiles/factory_blocks/1.4.0-athena-4.0.6/`.

## Exact runtime inputs

- Factory Blocks `factory_blocks-neoforge-1.4.0+mc1.21.1.jar`: 809,234 bytes,
  SHA-1 `51fa93c3b0978f4f7e3b801bea6822ec72522e50`, SHA-256
  `404080fcf4747c6d84b73d1c204d047408aae476f57752bc5f38e9c16c7f51cd`,
  SHA-512
  `2273baa299f4f829a2f17f5409a8f206aad87abef65f62e3d20c63be85281f217f64f2ecaed60063fa8c8a457ec6d63eb40778dec3c318af5a70741704b43cc1`;
- Athena `athena-neoforge-1.21.1-4.0.6.jar`: 99,944 bytes, SHA-1
  `4bcbdf388bd5e387beca7c627224aac33584b55b`, SHA-256
  `43699885bbce3343916d4c5c4940cf0e3f9f6f02fdeb46e8655e121b42282ec5`,
  SHA-512
  `ab40a306a26ce834daae921a1e87768cd2538a4bfe27a4480f97af854084cc334e7416b1bd0b7583834a32a86951283f29fd4b1df7b98a967a6b26a3ec05e2cf`.

The generator verifies both byte identities, all 52 Factory Blocks blockstate
resources, the exact 35 Athena-loader allowlist, loader/model/role rosters,
five routed animation metadata files, and the 260-path closure. It packages
no upstream bytes.

## Implementation source

The direct implementation source is the owner's MIT BlueMap Chisel Add-on at
annotated tag `v0.1.0-alpha.1`, target commit
`f9131a5143062e2045cf26823aabb8628bb5d94d`. This repository adapts its
BlueMap adapter, Athena interpreter, fail-closed activation, atomic fallback,
tests, and build boundary. Chisel itself inherited earlier MIT BlueMap Chipped
work; that ancestry is recorded by the Chisel project and is not represented
here as this repository's direct source.

The BlueMap 5.23 adapter primitives are compiled from Adapter API
`v0.1.0-alpha.2`, commit `e81f08bc4bfbf02d810ec8949a019130e2e61634`,
source tree `2f974c9bb2ba13888d69682f86f30f58922d30eb`. The pure Athena model
primitives are compiled from Athena Resource Models `v0.1.0-alpha.1`, commit
`4a503a63f7f10b7c414c6c1228207a5ba00bfd54`, source tree
`882689c2f9a0875547f4e30aefd68659103d5046`. Both are exact gitlink pins;
neither standalone module JAR is nested or installed at runtime.

Factory Blocks tag `1.21.1-1.4.0`, commit
`c4fb3667688ed7b0498575c1f0c83f8ff9f45237`, is exact-correlated evidence.
Its resource assets match the runtime artifact, but no Factory Blocks source
or algorithm is adapted. This resource-interpreter lane avoids relying on the
conflict between the exact JAR's MIT declaration and current distribution
metadata. Upstream Athena source is likewise not adapted.

CTM and Chisel content artifacts are evidence-only. Neither is a generator,
build, runtime, activation, publication, or packaged input.
