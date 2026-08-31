/* SPDX-License-Identifier: MIT */
package io.github.janguenter.bluemap.factoryblocks.adapter.bluemap523;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.janguenter.bluemap.factoryblocks.profile.LoaderFamily;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.ResourceExtensionType;
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
                BlueMap523Adapter.RENDERER_KEY.getFormatted());
        assertEquals(BlueMap523Adapter.RENDERER_KEY, FactoryBlocksResourceExtension.SYNTHETIC);
        assertEquals("bluemap_factory_blocks:exact_profile",
                BlueMap523Adapter.EXTENSION_KEY.getFormatted());
        assertTrue(BlueMap523Adapter.extensionType() instanceof ResourceExtensionType);
        assertFalse(BlueMap523Adapter.RENDERER_KEY.getFormatted()
                .startsWith("bluemap_chipped:"));
        assertFalse(BlueMap523Adapter.EXTENSION_KEY.getFormatted()
                .startsWith("bluemap_chipped:"));
    }
}
