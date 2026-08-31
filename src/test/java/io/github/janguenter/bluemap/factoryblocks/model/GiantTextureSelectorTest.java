/* SPDX-License-Identifier: MIT */
package io.github.janguenter.bluemap.factoryblocks.model;

import io.github.janguenter.bluemap.resource.athena.model.CubeFace;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GiantTextureSelectorTest {

    @Test
    void everyFaceAndPositiveNegativePhaseMatchesExactOracle() {
        for (CubeFace face : CubeFace.values()) {
            for (int x = -5; x <= 5; x++) {
                for (int y = -5; y <= 5; y++) {
                    for (int z = -5; z <= 5; z++) {
                        assertEquals(expected(face, x, y, z),
                                GiantTextureSelector.select(face, x, y, z),
                                face + " at " + x + "," + y + "," + z);
                    }
                }
            }
        }
    }

    @Test
    void integerMinimumIsConvertedToLongBeforeAbsoluteValue() {
        for (CubeFace face : CubeFace.values()) {
            assertEquals(
                    expected(face, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE),
                    GiantTextureSelector.select(
                            face, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE
                    )
            );
        }
    }

    private static int expected(CubeFace face, int worldX, int worldY, int worldZ) {
        long x = Math.abs((long) worldX);
        long y = Math.abs((long) worldY);
        long z = Math.abs((long) worldZ);
        if (face == CubeFace.EAST || face == CubeFace.DOWN) {
            z = Math.abs(z - 4L);
        }
        if (face == CubeFace.NORTH) {
            x = Math.abs(x - 4L);
        }
        long index = switch (face) {
            case EAST, WEST -> 1L + z % 3L + y % 3L * 3L;
            case NORTH, SOUTH -> 1L + x % 3L + y % 3L * 3L;
            case DOWN, UP -> 1L + x % 3L + z % 3L * 3L;
        };
        return Math.toIntExact(index);
    }
}
