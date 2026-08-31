/* SPDX-License-Identifier: MIT */
package io.github.janguenter.bluemap.factoryblocks.adapter.bluemap523;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.janguenter.bluemap.resource.athena.model.CubeFace;
import de.bluecolored.bluemap.core.util.math.Color;
import org.junit.jupiter.api.Test;

class AthenaQuadEmitterTest {

    @Test
    void emittedTriangleWindingPointsOutwardOnEveryCubeFace() {
        for (CubeFace face : CubeFace.values()) {
            AthenaQuadEmitter.Point bottomLeft = AthenaQuadEmitter.point(
                    face, 0F, 0F, 0F
            );
            AthenaQuadEmitter.Point bottomRight = AthenaQuadEmitter.point(
                    face, 0F, 1F, 0F
            );
            AthenaQuadEmitter.Point topRight = AthenaQuadEmitter.point(
                    face, 0F, 1F, 1F
            );
            float ax = bottomRight.x() - bottomLeft.x();
            float ay = bottomRight.y() - bottomLeft.y();
            float az = bottomRight.z() - bottomLeft.z();
            float bx = topRight.x() - bottomLeft.x();
            float by = topRight.y() - bottomLeft.y();
            float bz = topRight.z() - bottomLeft.z();
            CubeFace.Vec normal = face.normal();
            assertEquals(normal.x(), ay * bz - az * by, 0.00001F);
            assertEquals(normal.y(), az * bx - ax * bz, 0.00001F);
            assertEquals(normal.z(), ax * by - ay * bx, 0.00001F);
        }
    }

    @Test
    void uvQuarterTurnsPreserveGeometryLockedCropCoordinates() {
        float[] uv = {0.25F, 0.75F};
        assertArrayEquals(new float[]{0.25F, 0.75F},
                AthenaQuadEmitter.rotateUv(uv, 0));
        assertArrayEquals(new float[]{0.25F, 0.25F},
                AthenaQuadEmitter.rotateUv(uv, 1));
        assertArrayEquals(new float[]{0.75F, 0.25F},
                AthenaQuadEmitter.rotateUv(uv, 2));
        assertArrayEquals(new float[]{0.75F, 0.75F},
                AthenaQuadEmitter.rotateUv(uv, 3));
    }

    @Test
    void malformedOrDegenerateBoundsFailBeforeEmission() {
        assertTrue(AthenaQuadEmitter.validBounds(0F, 0F, 0F, 0.5F, 0.5F));
        assertFalse(AthenaQuadEmitter.validBounds(0F, 0.5F, 0F, 0.5F, 1F));
        assertFalse(AthenaQuadEmitter.validBounds(Float.NaN, 0F, 0F, 1F, 1F));
    }

    @Test
    void fourOpaqueQuadrantsNormalizeToOneRepresentativeOpacity() {
        Color accumulated = new Color().set(0F, 0F, 0F, 0F, true);
        Color quadrant = new Color().set(0.25F, 0.5F, 0.75F, 1F, true);
        for (int index = 0; index < 4; index++) {
            accumulated.add(quadrant);
        }
        assertEquals(4F, accumulated.a, 0.00001F);

        AthenaQuadEmitter.finishVariantColor(accumulated, 1F);

        assertEquals(1F, accumulated.a, 0.00001F);
        assertEquals(0.25F, accumulated.r, 0.00001F);
        assertEquals(0.5F, accumulated.g, 0.00001F);
        assertEquals(0.75F, accumulated.b, 0.00001F);
    }

    @Test
    void caveRemovalMatchesBlueMapSunAndOptionalBlockLightPolicy() {
        FaceLighting.Sample dark = new FaceLighting.Sample(0, 0);
        FaceLighting.Sample torch = new FaceLighting.Sample(0, 7);
        FaceLighting.Sample sky = new FaceLighting.Sample(9, 0);

        assertFalse(AthenaQuadEmitter.hiddenByCave(false, false, dark));
        assertTrue(AthenaQuadEmitter.hiddenByCave(true, false, dark));
        assertTrue(AthenaQuadEmitter.hiddenByCave(true, false, torch));
        assertFalse(AthenaQuadEmitter.hiddenByCave(true, true, torch));
        assertFalse(AthenaQuadEmitter.hiddenByCave(true, false, sky));
    }

    @Test
    void surfaceOnlyPassRetainsOnlyTheTopFace() {
        for (CubeFace face : CubeFace.values()) {
            assertEquals(face != CubeFace.UP,
                    AthenaQuadEmitter.skipForTopOnly(true, face));
            assertFalse(AthenaQuadEmitter.skipForTopOnly(false, face));
        }
    }

    @Test
    void cullingRequiresOpaqueNeighborOrExplicitIdenticalStateRule() {
        assertTrue(AthenaQuadEmitter.shouldCull(true, false, false));
        assertTrue(AthenaQuadEmitter.shouldCull(false, true, true));
        assertFalse(AthenaQuadEmitter.shouldCull(false, true, false));
        assertFalse(AthenaQuadEmitter.shouldCull(false, false, true));
    }

    @Test
    void faceLightingUsesMaximumOwnNeighborAndEmissionLevels() {
        assertEquals(new FaceLighting.Sample(13, 11),
                FaceLighting.maximum(13, 2, 4, 11, 7));
        assertEquals(new FaceLighting.Sample(15, 14),
                FaceLighting.maximum(1, 3, 15, 6, 14));
    }
}
