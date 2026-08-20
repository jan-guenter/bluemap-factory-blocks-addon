/* SPDX-License-Identifier: MIT */
package io.github.janguenter.bluemap.factoryblocks.adapter.bluemap522;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.janguenter.bluemap.factoryblocks.profile.LoaderFamily;
import org.junit.jupiter.api.Test;

class FactoryBlocksRendererMappingTest {

    @Test
    void onlyCtmPerformsExactNativeIdInternalFaceSuppression() {
        assertTrue(FactoryBlocksRenderer.earlySuppressesInternalFace(LoaderFamily.CTM));
        assertFalse(FactoryBlocksRenderer.earlySuppressesInternalFace(LoaderFamily.GIANT));
    }

    @Test
    void registryAndSyntheticIdsCannotCollideWithChippedAddon() {
        assertEquals("bluemap_factory_blocks:athena_shape",
                BlueMap522Adapter.RENDERER_KEY.getFormatted());
        assertEquals(BlueMap522Adapter.RENDERER_KEY, FactoryBlocksResourceExtension.SYNTHETIC);
        assertEquals("bluemap_factory_blocks:exact_profile",
                FactoryBlocksResourceExtensionType.KEY.getFormatted());
        assertFalse(BlueMap522Adapter.RENDERER_KEY.getFormatted()
                .startsWith("bluemap_chipped:"));
        assertFalse(FactoryBlocksResourceExtensionType.KEY.getFormatted()
                .startsWith("bluemap_chipped:"));
    }
}
