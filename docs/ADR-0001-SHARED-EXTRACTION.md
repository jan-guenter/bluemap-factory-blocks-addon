# ADR 0001: adapt the proven MIT interpreter without a shared runtime

Status: accepted for the unreleased Factory Blocks prototype.

## Context

The owner's published MIT BlueMap Chisel Add-on already implements the
Athena 4.0.6 CTM resource semantics, exact artifact/schema activation,
BlueMap 5.22 adapter boundary, reversible emission, and stock fallback needed
by Factory Blocks. Factory Blocks adds a small, distinct resource roster and
uses Athena's 3×3 rather than 2×2 giant model.

BlueMap loads each pack in a separate classloader. A mandatory shared runtime
would couple otherwise independent consumer add-ons and is not justified for
this prototype.

## Decision

Adapt the MIT BlueMap Chisel Add-on at tag `v0.1.0-alpha.1`, commit
`f9131a5143062e2045cf26823aabb8628bb5d94d`, into this standalone repository.
Specialize it with:

- unique `bluemap_factory_blocks:*` registrations;
- the exact Factory Blocks 1.4.0/Athena 4.0.6 artifact pair;
- the closed 32-CTM/3-giant allowlist and 260-path resource manifest;
- exact 3×3 giant roles `1..9` and face mirroring;
- deterministic frame zero for the five routed gears roles.

No Factory Blocks, Athena, CTM, or Chisel content source or assets are copied.
The interpreter remains compiled into this one deployable JAR. Revisit a
shared source extraction only after another concrete consumer proves the same
API and failure semantics.
