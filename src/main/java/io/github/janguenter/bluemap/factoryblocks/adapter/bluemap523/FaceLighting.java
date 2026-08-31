/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.factoryblocks.adapter.bluemap523;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.util.Direction;
import de.bluecolored.bluemap.core.util.math.VectorM3f;
import de.bluecolored.bluemap.core.world.LightData;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;

/** Samples the light on the exposed side of a transformed model face. */
final class FaceLighting {

    private FaceLighting() {
    }

    static Sample sample(
            BlockNeighborhood block,
            Direction direction,
            Variant transform,
            int lightEmission
    ) {
        VectorM3f relative = new VectorM3f(0F, 0F, 0F).set(direction.toVector());
        if (transform.isTransformed()) {
            relative.rotateAndScale(transform.getTransformMatrix());
        }

        LightData own = block.getLightData();
        LightData faced = block.getNeighborBlock(
                Math.round(relative.x),
                Math.round(relative.y),
                Math.round(relative.z)
        ).getLightData();
        return maximum(
                own.getSkyLight(), own.getBlockLight(),
                faced.getSkyLight(), faced.getBlockLight(),
                lightEmission
        );
    }

    static Sample maximum(
            int ownSky,
            int ownBlock,
            int neighborSky,
            int neighborBlock,
            int emission
    ) {
        return new Sample(
                Math.max(ownSky, neighborSky),
                Math.max(emission, Math.max(ownBlock, neighborBlock))
        );
    }

    record Sample(int sunlight, int blocklight) {
    }
}
