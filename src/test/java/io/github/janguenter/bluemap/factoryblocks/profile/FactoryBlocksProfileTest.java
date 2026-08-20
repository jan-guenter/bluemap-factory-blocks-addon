/* SPDX-License-Identifier: MIT */
package io.github.janguenter.bluemap.factoryblocks.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.jupiter.api.Test;

class FactoryBlocksProfileTest {

    @Test
    void exactMetadataOnlyCatalogHasClosedTwoFamilyRoster() {
        assertEquals(35, FactoryBlocks140Athena406Profile.DEFINITIONS.size());
        assertEquals(190, FactoryBlocks140Athena406Profile.REQUIRED_TEXTURES.size());
        Map<LoaderFamily, Integer> counts = new EnumMap<>(LoaderFamily.class);
        FactoryBlocks140Athena406Profile.DEFINITIONS.values().forEach(
                definition -> counts.merge(definition.family(), 1, Integer::sum)
        );
        assertEquals(Map.of(
                LoaderFamily.CTM, 32,
                LoaderFamily.GIANT, 3
        ), counts);
        assertEquals(expectedRoutedBlocks(), FactoryBlocks140Athena406Profile.ROUTED_BLOCKS);
    }

    @Test
    void giantRolesAndAnimatedGearsSubsetAreClosed() {
        assertEquals(
                java.util.List.of("particle", "1", "2", "3", "4", "5", "6", "7", "8", "9"),
                LoaderFamily.GIANT.textureRoles()
        );
        assertEquals(5, FactoryBlocks140Athena406Profile.STATIC_FIRST_FRAME_TEXTURES.size());
        assertTrue(FactoryBlocks140Athena406Profile.ROUTED_BLOCKS.contains(
                "factory_blocks:gears"
        ));
        assertEquals(
                Set.of(
                        "factory_blocks:block/ctm/gears/0",
                        "factory_blocks:block/ctm/gears/1",
                        "factory_blocks:block/ctm/gears/2",
                        "factory_blocks:block/ctm/gears/3",
                        "factory_blocks:block/ctm/gears/4"
                ),
                FactoryBlocks140Athena406Profile.STATIC_FIRST_FRAME_TEXTURES.stream()
                        .map(key -> key.getFormatted())
                        .collect(java.util.stream.Collectors.toUnmodifiableSet())
        );
    }

    @Test
    void exactJarContainsOnlyTheClosedAthenaRoster() throws IOException {
        String factoryBlocksValue = System.getProperty("factoryBlocksJar");
        if (factoryBlocksValue == null || !Files.isRegularFile(Path.of(factoryBlocksValue))) {
            return;
        }
        int all = 0;
        int stock = 0;
        Set<String> routed = new java.util.HashSet<>();
        try (ZipFile archive = new ZipFile(factoryBlocksValue)) {
            for (ZipEntry entry : java.util.Collections.list(archive.entries())) {
                String name = entry.getName();
                String prefix = "assets/factory_blocks/blockstates/";
                if (entry.isDirectory() || !name.startsWith(prefix) || !name.endsWith(".json")) {
                    continue;
                }
                all++;
                String block = "factory_blocks:" + name.substring(prefix.length(), name.length() - 5);
                if (FactoryBlocks140Athena406Profile.ROUTED_BLOCKS.contains(block)) {
                    routed.add(block);
                    continue;
                }
                stock++;
            }
        }
        assertEquals(FactoryBlocks140Athena406Profile.RESOURCE_BLOCKSTATE_COUNT, all);
        assertEquals(17, stock);
        assertEquals(expectedRoutedBlocks(), routed);
    }

    private static Set<String> expectedRoutedBlocks() {
        return Set.of(
                "factory_blocks:bcircuit", "factory_blocks:bwireframe",
                "factory_blocks:cables", "factory_blocks:caution", "factory_blocks:circuit",
                "factory_blocks:engineer", "factory_blocks:exhaust", "factory_blocks:factory",
                "factory_blocks:gcircuit", "factory_blocks:gears", "factory_blocks:grate",
                "factory_blocks:grinder", "factory_blocks:gvent", "factory_blocks:hazard",
                "factory_blocks:hazardo", "factory_blocks:hex", "factory_blocks:ice",
                "factory_blocks:insulation", "factory_blocks:large_pipes",
                "factory_blocks:large_plating", "factory_blocks:mosaic",
                "factory_blocks:old_vents", "factory_blocks:pgcircuit",
                "factory_blocks:pwireframe", "factory_blocks:rgrate", "factory_blocks:rust",
                "factory_blocks:rust_plates", "factory_blocks:rusty_scaffold",
                "factory_blocks:scaffold", "factory_blocks:small_pipes", "factory_blocks:srust",
                "factory_blocks:sturdy", "factory_blocks:vent", "factory_blocks:vrust",
                "factory_blocks:wireframe"
        );
    }
}
