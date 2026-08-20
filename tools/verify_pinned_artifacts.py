#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Fail-closed review gate for exact Factory Blocks 1.4.0/Athena 4.0.6."""

from __future__ import annotations

import argparse
from pathlib import Path
import sys
import zipfile

import generate_profile


def _verify_mod_metadata(factory_blocks: Path, athena: Path) -> None:
    with zipfile.ZipFile(factory_blocks) as archive:
        try:
            metadata = archive.read("META-INF/neoforge.mods.toml")
        except KeyError as error:
            raise ValueError("missing FactoryBlocks NeoForge metadata") from error
        if b'"factory_blocks"' not in metadata or b'"1.4.0+mc1.21.1"' not in metadata:
            raise ValueError("FactoryBlocks NeoForge metadata identity changed")
        names = archive.namelist()
        if not any(name.startswith("assets/factory_blocks/") for name in names):
            raise ValueError("FactoryBlocks archive has no installed resource root")
        if any(name.startswith("earth/terrarium/athena/") for name in names):
            raise ValueError("FactoryBlocks archive unexpectedly embeds Athena classes")

    with zipfile.ZipFile(athena) as archive:
        names = archive.namelist()
        if "META-INF/neoforge.mods.toml" not in names:
            raise ValueError("Athena archive has no NeoForge metadata")
        metadata = archive.read("META-INF/neoforge.mods.toml")
        if b'"athena"' not in metadata or b'"4.0.6"' not in metadata:
            raise ValueError("Athena NeoForge metadata identity changed")
        if not any(name.startswith("earth/terrarium/athena/") for name in names):
            raise ValueError("Athena archive has no expected implementation package")


def verify(factory_blocks: Path, athena: Path) -> None:
    outputs = generate_profile.build_outputs(factory_blocks, athena)
    generate_profile.apply_outputs(outputs, check=True)
    _verify_mod_metadata(factory_blocks, athena)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--factory_blocks", required=True, type=Path)
    parser.add_argument("--athena", required=True, type=Path)
    args = parser.parse_args()
    try:
        verify(args.factory_blocks, args.athena)
    except (OSError, ValueError, zipfile.BadZipFile) as error:
        print(f"artifact verification failed: {error}", file=sys.stderr)
        return 1
    print(
        "Verified exact Factory Blocks 1.4.0+mc1.21.1 + Athena 4.0.6 artifacts, "
        "35 routed definitions, and the 260-path metadata-only resource closure."
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
