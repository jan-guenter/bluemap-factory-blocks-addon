/* SPDX-License-Identifier: MIT */
package io.github.janguenter.bluemap.factoryblocks.profile;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ExactModArtifactDetectorTest {

    @Test
    void onlyTheExactInstalledPairActivates() {
        String chiselValue = System.getProperty("factoryBlocksJar");
        String athenaValue = System.getProperty("athenaJar");
        if (chiselValue == null || athenaValue == null) {
            return;
        }
        Path factory_blocks = Path.of(chiselValue);
        Path athena = Path.of(athenaValue);
        if (!Files.isRegularFile(factory_blocks) || !Files.isRegularFile(athena)) {
            return;
        }
        assertTrue(ExactModArtifactDetector.matchesRequiredPair(List.of(factory_blocks, athena)));
        assertFalse(ExactModArtifactDetector.matchesRequiredPair(List.of(factory_blocks)));
        assertFalse(ExactModArtifactDetector.matches(
                List.of(factory_blocks, athena),
                Map.of(
                        "factory_blocks", new ExactModArtifactDetector.Identity(
                                FactoryBlocks140Athena406Profile.FACTORY_BLOCKS_SHA256,
                                FactoryBlocks140Athena406Profile.FACTORY_BLOCKS_SIZE + 1
                        ),
                        "athena", new ExactModArtifactDetector.Identity(
                                FactoryBlocks140Athena406Profile.ATHENA_SHA256,
                                FactoryBlocks140Athena406Profile.ATHENA_SIZE
                        )
                )
        ));
    }
}
