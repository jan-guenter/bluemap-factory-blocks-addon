/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.factoryblocks.profile;

import de.bluecolored.bluemap.core.util.Key;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Exact All the Mons 1.2.0 Factory Blocks/Athena profile. */
public final class FactoryBlocks140Athena406Profile {

    public static final String PROFILE_ID = "factory-blocks-athena-1.4.0-4.0.6";
    public static final String FACTORY_BLOCKS_SHA256 =
            "404080fcf4747c6d84b73d1c204d047408aae476f57752bc5f38e9c16c7f51cd";
    public static final long FACTORY_BLOCKS_SIZE = 809_234L;
    public static final String ATHENA_SHA256 =
            "43699885bbce3343916d4c5c4940cf0e3f9f6f02fdeb46e8655e121b42282ec5";
    public static final long ATHENA_SIZE = 99_944L;
    public static final int RESOURCE_BLOCKSTATE_COUNT = 52;
    public static final int REGISTERED_BLOCK_COUNT = 49;
    public static final int ROUTED_BLOCK_COUNT = 35;
    public static final int CTM_BLOCK_COUNT = 32;
    public static final int GIANT_BLOCK_COUNT = 3;
    public static final int STOCK_REGISTERED_BLOCK_COUNT = 14;
    public static final int UNREGISTERED_RESOURCE_BLOCKSTATE_COUNT = 3;
    public static final int REQUIRED_TEXTURE_COUNT = 190;
    public static final int REQUIRED_RESOURCE_PATH_COUNT = 260;
    public static final int REQUIRED_PNG_COUNT = 190;
    public static final String DEFINITIONS_SHA256 =
            "3a5f6ecd5901875ffda10e232d35ce3839f3a098d17aeea93de1095c8bda900e";
    public static final String RESOURCE_MANIFEST_SHA256 =
            "f5bca2172149b885ac3b4899c4ede36629adbd8605ae80dd385cbde79b9d1873";

    public static final DefinitionCatalog CATALOG = DefinitionCatalog.load(
            "/bluemap-factory-blocks/profiles/factory_blocks/1.4.0-athena-4.0.6/definitions.tsv",
            ROUTED_BLOCK_COUNT,
            DEFINITIONS_SHA256
    );
    public static final Map<String, FactoryBlocksDefinition> DEFINITIONS = CATALOG.definitions();
    public static final Set<String> ROUTED_BLOCKS = DEFINITIONS.keySet();
    public static final Set<Key> REQUIRED_TEXTURES = CATALOG.textureIds().stream()
            .map(Key::parse)
            .collect(Collectors.toUnmodifiableSet());
    public static final Set<Key> STATIC_FIRST_FRAME_TEXTURES = Set.of(
            Key.parse("factory_blocks:block/ctm/gears/0"),
            Key.parse("factory_blocks:block/ctm/gears/1"),
            Key.parse("factory_blocks:block/ctm/gears/2"),
            Key.parse("factory_blocks:block/ctm/gears/3"),
            Key.parse("factory_blocks:block/ctm/gears/4")
    );

    static {
        if (REQUIRED_TEXTURES.size() != REQUIRED_TEXTURE_COUNT) {
            throw new IllegalStateException("required texture roster changed");
        }
    }

    private FactoryBlocks140Athena406Profile() {
    }
}
