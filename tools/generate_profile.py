#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Generate the metadata-only exact Factory Blocks/Athena rendering profile."""

from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path, PurePosixPath
from typing import Any, Iterable
import zipfile


PROFILE_ROOT = Path("src/main/resources/bluemap-factory-blocks/profiles")
PROFILE_DIRECTORY = PROFILE_ROOT / "factory_blocks/1.4.0-athena-4.0.6"
CATALOG_PATH = PROFILE_ROOT / "exact-artifacts.json"
PROFILE_PATH = PROFILE_DIRECTORY / "profile.json"
DEFINITIONS_PATH = PROFILE_DIRECTORY / "definitions.tsv"
RESOURCES_PATH = PROFILE_DIRECTORY / "required-resources.tsv"

FACTORY_BLOCKS_FILENAME = "factory_blocks-neoforge-1.4.0+mc1.21.1.jar"
FACTORY_BLOCKS_SIZE = 809_234
FACTORY_BLOCKS_SHA1 = "51fa93c3b0978f4f7e3b801bea6822ec72522e50"
FACTORY_BLOCKS_SHA256 = "404080fcf4747c6d84b73d1c204d047408aae476f57752bc5f38e9c16c7f51cd"
FACTORY_BLOCKS_SHA512 = (
    "2273baa299f4f829a2f17f5409a8f206aad87abef65f62e3d20c63be85281f21"
    "7f64f2ecaed60063fa8c8a457ec6d63eb40778dec3c318af5a70741704b43cc1"
)
ATHENA_FILENAME = "athena-neoforge-1.21.1-4.0.6.jar"
ATHENA_SIZE = 99_944
ATHENA_SHA1 = "4bcbdf388bd5e387beca7c627224aac33584b55b"
ATHENA_SHA256 = "43699885bbce3343916d4c5c4940cf0e3f9f6f02fdeb46e8655e121b42282ec5"
ATHENA_SHA512 = (
    "ab40a306a26ce834daae921a1e87768cd2538a4bfe27a4480f97af854084cc334"
    "e7416b1bd0b7583834a32a86951283f29fd4b1df7b98a967a6b26a3ec05e2cf"
)

ALL_BLOCKSTATES_COUNT = 52
ALL_BLOCKSTATES_DIGEST = "ef9e89d25ef6c1f2043a6a43ff9afe678f67e766f2a257785f016f135ab598e3"
REGISTERED_BLOCK_COUNT = 49
ROSTER_COUNT = 35
ROSTER_DIGEST = "3824bb95f66d5110688a63e77c14127d333bd5f0c109d958417aa01be9239c94"
MODEL_COUNT = 35
MODEL_DIGEST = "6156c92752e03a69c58e6a60966d1bd095c773839de7f4106dfa18ca2e5cc235"
ROLE_TEXTURE_COUNT = 190
ROLE_TEXTURE_DIGEST = "2eb455934a794181f8f49e8a03752bb79f1856e59b5ceb7e7f36843132bf61b0"
PNG_COUNT = 190
RESOURCE_PATH_COUNT = 260
RESOURCE_MANIFEST_SHA256 = (
    "f5bca2172149b885ac3b4899c4ede36629adbd8605ae80dd385cbde79b9d1873"
)

ROUTED_BLOCKS = {
    "bcircuit", "bwireframe", "cables", "caution", "circuit", "engineer",
    "exhaust", "factory", "gcircuit", "gears", "grate", "grinder", "gvent",
    "hazard", "hazardo", "hex", "ice", "insulation", "large_pipes",
    "large_plating", "mosaic", "old_vents", "pgcircuit", "pwireframe",
    "rgrate", "rust", "rust_plates", "rusty_scaffold", "scaffold",
    "small_pipes", "srust", "sturdy", "vent", "vrust", "wireframe",
}

LOADERS: dict[str, tuple[int, str, tuple[str, ...], str]] = {
    "athena:ctm": (
        32,
        "7f738459b75909a5b755c595cdaf1c5c86df788ad71c058ab62b982837a33cb2",
        ("particle", "empty", "center", "vertical", "horizontal"),
        "block/cube_all",
    ),
    "athena:giant": (
        3,
        "9f07a3f7f037a6be1c686bb5d0ac33afcec853c49dc88e8861adc0aa85f07892",
        ("particle", "1", "2", "3", "4", "5", "6", "7", "8", "9"),
        "block/cube_all",
    ),
}

ANIMATED_TEXTURES = {
    "factory_blocks:block/ctm/gears/0",
    "factory_blocks:block/ctm/gears/1",
    "factory_blocks:block/ctm/gears/2",
    "factory_blocks:block/ctm/gears/3",
    "factory_blocks:block/ctm/gears/4",
}


def digest_bytes(raw: bytes, algorithm: str = "sha256") -> str:
    return hashlib.new(algorithm, raw).hexdigest()


def digest_path(path: Path, algorithm: str) -> str:
    value = hashlib.new(algorithm)
    with path.open("rb") as source:
        for chunk in iter(lambda: source.read(64 * 1024), b""):
            value.update(chunk)
    return value.hexdigest()


def roster_digest(values: Iterable[str]) -> str:
    payload = "".join(f"{value}\n" for value in sorted(values)).encode("utf-8")
    return digest_bytes(payload)


def resource_path(key: str, kind: str, suffix: str) -> str:
    if ":" in key:
        namespace, value = key.split(":", 1)
    else:
        namespace, value = "minecraft", key
    path = PurePosixPath(value)
    if path.is_absolute() or any(part in {"", ".", ".."} for part in path.parts):
        raise ValueError(f"unsafe resource key: {key}")
    return f"assets/{namespace}/{kind}/{value}{suffix}"


def verify_file_identity(
    path: Path, *, filename: str, size: int, sha1: str, sha256: str, sha512: str
) -> None:
    if not path.is_file() or path.name != filename:
        raise ValueError(f"unexpected artifact path: {path}")
    if path.stat().st_size != size:
        raise ValueError(f"unexpected artifact size for {path}")
    for algorithm, expected in (
        ("sha1", sha1),
        ("sha256", sha256),
        ("sha512", sha512),
    ):
        actual = digest_path(path, algorithm)
        if actual != expected:
            raise ValueError(
                f"{path.name} {algorithm} changed: got {actual}, expected {expected}"
            )


def _model_texture_map(model: dict[str, Any]) -> tuple[str, tuple[str, ...]]:
    textures = model.get("textures")
    if not isinstance(textures, dict) or not textures:
        raise ValueError("ordinary model has no texture map")
    rows: list[str] = []
    for key, value in sorted(textures.items()):
        if not isinstance(key, str) or not isinstance(value, str):
            raise ValueError("ordinary model texture map is malformed")
        rows.append(f"{key}={value}\n")
    return digest_bytes("".join(rows).encode("utf-8")), tuple(
        value for _key, value in sorted(textures.items())
    )


def _parse_definition(
    archive: zipfile.ZipFile, path: str, raw: bytes
) -> tuple[tuple[str, ...], str, tuple[str, ...]] | None:
    value = json.loads(raw)
    if not isinstance(value, dict) or "athena:loader" not in value:
        return None
    loader = value.get("athena:loader")
    if loader not in LOADERS:
        raise ValueError(f"{path} has unsupported Athena loader {loader!r}")
    expected_count, _digest, roles, parent = LOADERS[loader]
    del expected_count

    expected_keys = {"athena:loader", "ctm_textures", "variants"}
    if loader == "athena:giant":
        expected_keys.update(("width", "height"))
        if value.get("width") != 3 or value.get("height") != 3:
            raise ValueError(f"{path} giant dimensions changed")
    if set(value) != expected_keys:
        raise ValueError(f"{path} blockstate schema changed")

    variants = value.get("variants")
    if not isinstance(variants, dict) or set(variants) != {""}:
        raise ValueError(f"{path} variants changed")
    variant = variants[""]
    if not isinstance(variant, dict) or set(variant) != {"model"}:
        raise ValueError(f"{path} default variant changed")
    model_key = variant.get("model")
    if not isinstance(model_key, str):
        raise ValueError(f"{path} model key is malformed")

    textures = value.get("ctm_textures")
    if not isinstance(textures, dict) or set(textures) != set(roles):
        raise ValueError(f"{path} Athena texture schema changed")
    texture_values = tuple(textures[role] for role in roles)
    if any(not isinstance(texture, str) for texture in texture_values):
        raise ValueError(f"{path} Athena texture key is malformed")

    model_path = resource_path(model_key, "models", ".json")
    try:
        model = json.loads(archive.read(model_path))
    except KeyError as error:
        raise ValueError(f"missing ordinary model {model_path}") from error
    if not isinstance(model, dict) or set(model) != {"parent", "textures"}:
        raise ValueError(f"{model_path} ordinary model schema changed")
    if model.get("parent") != parent:
        raise ValueError(f"{model_path} parent changed")

    block = path.removeprefix("assets/factory_blocks/blockstates/").removesuffix(".json")
    model_texture_digest, model_textures = _model_texture_map(model)
    row = (
        f"factory_blocks:{block}",
        loader.removeprefix("athena:"),
        model_key,
        parent,
        *texture_values,
        model_texture_digest,
    )
    return row, model_path, texture_values


def build_outputs(factory_blocks: Path, athena: Path) -> dict[Path, bytes]:
    verify_file_identity(
        factory_blocks,
        filename=FACTORY_BLOCKS_FILENAME,
        size=FACTORY_BLOCKS_SIZE,
        sha1=FACTORY_BLOCKS_SHA1,
        sha256=FACTORY_BLOCKS_SHA256,
        sha512=FACTORY_BLOCKS_SHA512,
    )
    verify_file_identity(
        athena,
        filename=ATHENA_FILENAME,
        size=ATHENA_SIZE,
        sha1=ATHENA_SHA1,
        sha256=ATHENA_SHA256,
        sha512=ATHENA_SHA512,
    )

    definitions: list[tuple[str, ...]] = []
    family_blocks: dict[str, list[str]] = {loader: [] for loader in LOADERS}
    all_blocks: list[str] = []
    model_keys: set[str] = set()
    texture_keys: set[str] = set()
    required_paths: set[str] = set()

    with zipfile.ZipFile(factory_blocks) as archive:
        names = archive.namelist()
        if len(names) != len(set(names)):
            raise ValueError("FactoryBlocks JAR contains duplicate ZIP entries")
        for path in sorted(names):
            prefix = "assets/factory_blocks/blockstates/"
            if not path.startswith(prefix) or not path.endswith(".json"):
                continue
            bare_block = path[len(prefix) : -len(".json")]
            all_blocks.append(bare_block)
            raw_blockstate = archive.read(path)
            parsed = _parse_definition(archive, path, raw_blockstate)
            if parsed is None:
                continue
            row, model_path, textures = parsed
            definitions.append(row)
            loader = f"athena:{row[1]}"
            family_blocks[loader].append(bare_block)
            model_keys.add(row[2])
            texture_keys.update(textures)
            required_paths.add(path)
            required_paths.add(model_path)
            required_paths.update(
                resource_path(texture, "textures", ".png") for texture in textures
            )

        if len(all_blocks) != ALL_BLOCKSTATES_COUNT:
            raise ValueError("FactoryBlocks blockstate count changed")
        if roster_digest(all_blocks) != ALL_BLOCKSTATES_DIGEST:
            raise ValueError("FactoryBlocks complete blockstate roster changed")
        for loader, (count, expected_digest, _roles, _parent) in LOADERS.items():
            values = family_blocks[loader]
            if len(values) != count or roster_digest(values) != expected_digest:
                raise ValueError(f"{loader} roster changed")
        routed = [block for values in family_blocks.values() for block in values]
        if (set(routed) != ROUTED_BLOCKS
                or len(routed) != ROSTER_COUNT
                or roster_digest(routed) != ROSTER_DIGEST):
            raise ValueError("FactoryBlocks Athena-loader roster changed")
        if len(model_keys) != MODEL_COUNT or roster_digest(model_keys) != MODEL_DIGEST:
            raise ValueError("FactoryBlocks Athena model roster changed")
        if (len(texture_keys) != ROLE_TEXTURE_COUNT
                or roster_digest(texture_keys) != ROLE_TEXTURE_DIGEST):
            raise ValueError("FactoryBlocks Athena texture roster changed")

        all_animated = {
            path.removeprefix("assets/").replace("/textures/", ":", 1)
            .removesuffix(".png.mcmeta")
            for path in names
            if path.startswith("assets/factory_blocks/textures/")
            and path.endswith(".png.mcmeta")
        }
        animated = all_animated.intersection(texture_keys)
        if animated != ANIMATED_TEXTURES:
            raise ValueError("Factory Blocks routed animated texture roster changed")
        for texture in sorted(ANIMATED_TEXTURES):
            metadata_path = resource_path(texture, "textures", ".png.mcmeta")
            metadata = json.loads(archive.read(metadata_path))
            if metadata != {"animation": {"interpolate": True, "frametime": 2}}:
                raise ValueError(f"{metadata_path} animation schema changed")

        resource_rows: list[str] = []
        resource_bytes = 0
        for path in sorted(required_paths):
            try:
                raw = archive.read(path)
            except KeyError as error:
                raise ValueError(f"missing required FactoryBlocks resource {path}") from error
            resource_rows.append(f"{path}\t{digest_bytes(raw)}\n")
            resource_bytes += len(raw)
        if (len(resource_rows) != RESOURCE_PATH_COUNT
                or sum(path.endswith(".png") for path in required_paths) != PNG_COUNT):
            raise ValueError("FactoryBlocks exact resource closure changed")

    definitions.sort(key=lambda row: row[0])
    definitions_raw = "".join("\t".join(row) + "\n" for row in definitions).encode(
        "ascii"
    )
    resources_raw = "".join(resource_rows).encode("ascii")
    definitions_digest = digest_bytes(definitions_raw)
    resources_digest = digest_bytes(resources_raw)
    if resources_digest != RESOURCE_MANIFEST_SHA256:
        raise ValueError("FactoryBlocks exact resource manifest changed")

    family_counts = {
        loader.removeprefix("athena:"): LOADERS[loader][0]
        for loader in sorted(LOADERS)
    }
    family_digests = {
        loader.removeprefix("athena:"): LOADERS[loader][1]
        for loader in sorted(LOADERS)
    }
    catalog = {
        "schemaVersion": 1,
        "baseline": {
            "packVersion": "1.2.0",
            "packRepositoryCommit": "c7bb230f21d14d26859d0b92548f089b3a493ad9",
            "minecraft": "1.21.1",
            "neoforge": "21.1.248",
            "java": 21,
        },
        "requiredForStaticRendering": ["factory_blocks", "athena"],
        "artifacts": [
            {
                "modId": "factory_blocks",
                "metadataVersion": "1.4.0+mc1.21.1",
                "filename": FACTORY_BLOCKS_FILENAME,
                "sizeBytes": FACTORY_BLOCKS_SIZE,
                "sha1": FACTORY_BLOCKS_SHA1,
                "sha256": FACTORY_BLOCKS_SHA256,
                "sha512": FACTORY_BLOCKS_SHA512,
                "licenseDeclaration": "MIT in exact NeoForge descriptor",
                "sourceUseLane": "independently-authored/resource-interpreter",
                "curseForgeProjectId": 640001,
                "curseForgeFileId": 6266334,
                "verificationRole": "consumer-resource-owner",
            },
            {
                "modId": "athena",
                "metadataVersion": "4.0.6",
                "filename": ATHENA_FILENAME,
                "sizeBytes": ATHENA_SIZE,
                "sha1": ATHENA_SHA1,
                "sha256": ATHENA_SHA256,
                "sha512": ATHENA_SHA512,
                "license": "MIT",
                "curseForgeProjectId": 841890,
                "curseForgeFileId": 8061947,
                "curseForgeFingerprint": 669268138,
                "modrinthProjectId": "b1ZV3DIJ",
                "modrinthVersionId": "dJgL278E",
                "verificationRole": "renderer-format-identity",
            },
        ],
    }
    profile = {
        "schemaVersion": 1,
        "profileId": "factory-blocks-athena-1.4.0-4.0.6",
        "minecraft": "1.21.1",
        "neoforge": "21.1.248",
        "factoryBlocksVersion": "1.4.0+mc1.21.1",
        "athenaVersion": "4.0.6",
        "coverage": {
            "resourceBlockstates": ALL_BLOCKSTATES_COUNT,
            "resourceBlockstatesDigest": ALL_BLOCKSTATES_DIGEST,
            "registeredBlocks": REGISTERED_BLOCK_COUNT,
            "routedBlocks": ROSTER_COUNT,
            "routedBlocksDigest": ROSTER_DIGEST,
            "stockRegisteredBlocks": REGISTERED_BLOCK_COUNT - ROSTER_COUNT,
            "unregisteredResourceBlockstates": ALL_BLOCKSTATES_COUNT - REGISTERED_BLOCK_COUNT,
            "loaderCounts": family_counts,
            "loaderDigests": family_digests,
            "modelCount": MODEL_COUNT,
            "modelDigest": MODEL_DIGEST,
            "roleTextureCount": ROLE_TEXTURE_COUNT,
            "roleTextureDigest": ROLE_TEXTURE_DIGEST,
            "pngCount": PNG_COUNT,
            "resourcePathCount": RESOURCE_PATH_COUNT,
            "resourceManifestSha256": RESOURCE_MANIFEST_SHA256,
            "animatedTextureCount": len(ANIMATED_TEXTURES),
        },
        "definitionCatalog": {
            "path": "definitions.tsv",
            "rows": len(definitions),
            "sha256": definitions_digest,
        },
        "resourceClosure": {
            "path": "required-resources.tsv",
            "rows": len(resource_rows),
            "bytes": resource_bytes,
            "sha256": resources_digest,
        },
        "runtimePolicy": {
            "resourceSource": "operator-installed roots only",
            "pixelOverrides": "allowed when all schema and texture IDs remain exact",
            "schemaOverrides": "deactivate the route and preserve stock rendering",
            "nonNativeAppearanceProxies": "stock fallback",
            "animation": "deterministic first frame; playback excluded",
        },
    }
    outputs = {
        CATALOG_PATH: json.dumps(catalog, indent=2, sort_keys=True).encode("utf-8")
        + b"\n",
        PROFILE_PATH: json.dumps(profile, indent=2, sort_keys=True).encode("utf-8")
        + b"\n",
        DEFINITIONS_PATH: definitions_raw,
        RESOURCES_PATH: resources_raw,
    }
    return outputs


def apply_outputs(outputs: dict[Path, bytes], *, check: bool) -> None:
    mismatches: list[str] = []
    for path, expected in outputs.items():
        if check:
            if not path.is_file() or path.read_bytes() != expected:
                mismatches.append(str(path))
        else:
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_bytes(expected)
    if mismatches:
        raise ValueError("generated profile is stale: " + ", ".join(mismatches))


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--factory_blocks", required=True, type=Path)
    parser.add_argument("--athena", required=True, type=Path)
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    outputs = build_outputs(args.factory_blocks, args.athena)
    apply_outputs(outputs, check=args.check)
    action = "verified" if args.check else "generated"
    print(f"{action} exact FactoryBlocks/Athena profile ({ROSTER_COUNT} routed blocks)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
