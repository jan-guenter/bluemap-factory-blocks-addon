# ADR 0001: share pure model and adapter sources without a runtime dependency

Status: superseded by the BlueMap 5.23 migration.

## Context

The owner's published MIT BlueMap Chisel Add-on already implements the
Athena 4.0.6 CTM resource semantics, exact artifact/schema activation,
BlueMap 5.23 feature-backport adapter boundary, reversible emission, and stock fallback needed
by Factory Blocks. Factory Blocks adds a small, distinct resource roster and
uses Athena's 3×3 rather than 2×2 giant model.

The portfolio later proved stable, identical pure primitives across multiple
consumers. BlueMap still loads each pack independently, so the reusable code
must be compiled into each add-on rather than installed as a shared runtime.

## Decision

Keep the standalone add-on and specialize the Chisel-derived renderer with:

- unique `bluemap_factory_blocks:*` registrations;
- the exact Factory Blocks 1.4.0/Athena 4.0.6 artifact pair;
- the closed 32-CTM/3-giant allowlist and 260-path resource manifest;
- exact 3×3 giant roles `1..9` and face mirroring;
- deterministic frame zero for the five routed gears roles.

No Factory Blocks, Athena, CTM, or Chisel content source or assets are copied.
Compile the exact Adapter API and Athena Resource Models gitlink-pinned source
trees into this one deployable JAR. Do not add standalone module JARs to the
server or nest them in the add-on.
