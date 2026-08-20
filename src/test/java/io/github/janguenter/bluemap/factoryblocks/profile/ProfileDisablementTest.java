/* SPDX-License-Identifier: MIT */
package io.github.janguenter.bluemap.factoryblocks.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;

class ProfileDisablementTest {

    @Test
    void propertyAndEnvironmentValuesMergeCanonically() {
        ProfileDisablement disabled = ProfileDisablement.from(
                " Factory-Blocks-Athena-1.4.0-4.0.6,INVALID VALUE ",
                "future,factory-blocks-athena-1.4.0-4.0.6"
        );
        assertEquals(
                Set.of("factory-blocks-athena-1.4.0-4.0.6", "future"),
                disabled.disabledProfiles()
        );
        assertTrue(disabled.isDisabled("FACTORY-BLOCKS-ATHENA-1.4.0-4.0.6"));
        assertFalse(disabled.isDisabled("missing"));
    }
}
