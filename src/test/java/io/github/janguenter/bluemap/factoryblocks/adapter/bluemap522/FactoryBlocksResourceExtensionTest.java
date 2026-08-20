/* SPDX-License-Identifier: MIT */
package io.github.janguenter.bluemap.factoryblocks.adapter.bluemap522;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Key;
import io.github.janguenter.bluemap.factoryblocks.profile.FactoryBlocks140Athena406Profile;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class FactoryBlocksResourceExtensionTest {

    @Test
    void pixelOverrideAlphaCanOnlyMakeFullCubePropertiesMoreConservative()
            throws Exception {
        Texture opaque = Texture.MISSING;
        BufferedImage translucentPixel = new BufferedImage(
                1, 1, BufferedImage.TYPE_INT_ARGB
        );
        translucentPixel.setRGB(0, 0, 0x80FFFFFF);
        Texture translucent = Texture.from(
                Key.parse("test:translucent-role"), translucentPixel
        );
        List<Texture> opaqueRoles = List.of(opaque, opaque, opaque, opaque, opaque);

        assertTrue(FactoryBlocksResourceExtension.opaqueFullCube(true, opaqueRoles));
        assertFalse(FactoryBlocksResourceExtension.opaqueFullCube(false, opaqueRoles));

        List<Texture> overriddenRoles = new ArrayList<>(opaqueRoles);
        overriddenRoles.set(3, translucent);
        assertFalse(FactoryBlocksResourceExtension.opaqueFullCube(true, overriddenRoles));
        overriddenRoles.set(3, null);
        assertFalse(FactoryBlocksResourceExtension.opaqueFullCube(true, overriddenRoles));
    }

    @Test
    void cullingUsesEveryActiveRoleAndExcludesUnusedGiantParticle() {
        assertEquals(
                List.of("particle", "empty", "center", "vertical", "horizontal"),
                FactoryBlocksResourceExtension.activeCullingRoles(
                        FactoryBlocks140Athena406Profile.DEFINITIONS.get("factory_blocks:ice")
                )
        );
        assertEquals(
                List.of("1", "2", "3", "4", "5", "6", "7", "8", "9"),
                FactoryBlocksResourceExtension.activeCullingRoles(
                        FactoryBlocks140Athena406Profile.DEFINITIONS.get("factory_blocks:hex")
                )
        );
    }

    @Test
    void animatedVerticalStripUsesOnlyDeterministicFirstSquareFrame() {
        BufferedImage strip = new BufferedImage(2, 4, BufferedImage.TYPE_INT_ARGB);
        strip.setRGB(0, 0, 0xFF112233);
        strip.setRGB(1, 1, 0xFF445566);
        strip.setRGB(0, 2, 0xFF778899);

        BufferedImage first = FactoryBlocksResourceExtension.firstFrame(strip);

        assertEquals(2, first.getWidth());
        assertEquals(2, first.getHeight());
        assertEquals(0xFF112233, first.getRGB(0, 0));
        assertEquals(0xFF445566, first.getRGB(1, 1));
    }
}
