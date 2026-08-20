/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.factoryblocks.adapter.bluemap522;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.Key;
import io.github.janguenter.bluemap.factoryblocks.activation.FactoryBlocksRuntime;

/** Resource-pack extension factory registered before resource loading begins. */
final class FactoryBlocksResourceExtensionType
        implements ResourcePack.Extension<FactoryBlocksResourceExtension> {

    static final Key KEY = Key.parse("bluemap_factory_blocks:exact_profile");

    private final FactoryBlocksRuntime runtime;

    FactoryBlocksResourceExtensionType(FactoryBlocksRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public Key getKey() {
        return KEY;
    }

    @Override
    public FactoryBlocksResourceExtension create(ResourcePack pack) {
        return new FactoryBlocksResourceExtension(pack, runtime);
    }
}
