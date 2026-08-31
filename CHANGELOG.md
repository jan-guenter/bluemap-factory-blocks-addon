# Changelog

## 0.1.0-alpha.2 - 2026-08-31

- Target only BlueMap feature-backport commit
  `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac` and API commit
  `285c9a60eff3ac2b0cab308ce1058d1565be0971`.
- Move the local adapter boundary from `bluemap522` to `bluemap523` and
  compile the exact shared Adapter API source pin.
- Compile the exact shared Athena model primitives and remove their four
  duplicate local implementations.
- Preserve the accepted 35-block CTM and giant-texture rendering contract.

## 0.1.0-alpha.1 - 2026-08-20

- Add the exact Factory Blocks `1.4.0+mc1.21.1` and Athena `4.0.6` profile for
  All the Mons 1.2.0.
- Route 32 Athena CTM blocks and three Athena 3×3 giant blocks while leaving
  the other 14 registered Factory Blocks IDs stock.
- Pin the exact 260-path installed-resource closure and deterministic frame
  zero for five routed gears textures.
- Adapt the owner-authored MIT BlueMap Chisel renderer into a collision-safe,
  independently removable Factory Blocks add-on.
- Add a small 25-placement staging gallery with CTM, giant, gears, and stock
  controls.

- Freeze the owner-accepted 86,501-byte production JAR, exact input pair,
  3,707-byte gallery archive, Maven metadata, and release provenance.
