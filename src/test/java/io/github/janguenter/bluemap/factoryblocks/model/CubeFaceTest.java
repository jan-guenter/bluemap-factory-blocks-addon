/* SPDX-License-Identifier: MIT */
package io.github.janguenter.bluemap.factoryblocks.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CubeFaceTest {

    @Test
    void exactFaceLocalNeighborBasesAreStable() {
        assertBasis(CubeFace.UP, vec(0, 0, -1), vec(0, 0, 1), vec(-1, 0, 0), vec(1, 0, 0));
        assertBasis(CubeFace.DOWN, vec(0, 0, 1), vec(0, 0, -1), vec(-1, 0, 0), vec(1, 0, 0));
        assertBasis(CubeFace.NORTH, vec(0, 1, 0), vec(0, -1, 0), vec(1, 0, 0), vec(-1, 0, 0));
        assertBasis(CubeFace.SOUTH, vec(0, 1, 0), vec(0, -1, 0), vec(-1, 0, 0), vec(1, 0, 0));
        assertBasis(CubeFace.WEST, vec(0, 1, 0), vec(0, -1, 0), vec(0, 0, -1), vec(0, 0, 1));
        assertBasis(CubeFace.EAST, vec(0, 1, 0), vec(0, -1, 0), vec(0, 0, 1), vec(0, 0, -1));
    }

    @Test
    void allMasksRemainFaceLocalForEveryCubeFace() {
        for (CubeFace face : CubeFace.values()) {
            CubeFace.Vec normal = face.normal();
            CubeFace.Vec[] offsets = {
                face.localUp(),
                face.localDown(),
                face.localLeft(),
                face.localRight(),
                face.localUp().add(face.localLeft()),
                face.localUp().add(face.localRight()),
                face.localDown().add(face.localLeft()),
                face.localDown().add(face.localRight()),
            };
            for (CubeFace.Vec offset : offsets) {
                assertEquals(0, offset.x() * normal.x()
                        + offset.y() * normal.y() + offset.z() * normal.z());
            }
            for (int mask = 0; mask <= 0xFF; mask++) {
                assertEquals(mask, CtmConnections.fromMask(mask).mask());
                assertEquals(4, CtmConnections.fromMask(mask).quadrants().size());
            }
        }
    }

    private static void assertBasis(
            CubeFace face,
            CubeFace.Vec up,
            CubeFace.Vec down,
            CubeFace.Vec left,
            CubeFace.Vec right
    ) {
        assertEquals(up, face.localUp());
        assertEquals(down, face.localDown());
        assertEquals(left, face.localLeft());
        assertEquals(right, face.localRight());
    }

    private static CubeFace.Vec vec(int x, int y, int z) {
        return new CubeFace.Vec(x, y, z);
    }
}
