/* SPDX-License-Identifier: MIT */
package io.github.janguenter.bluemap.factoryblocks.activation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RouteActivationTest {

    @Test
    void failureIsTerminalAndFailClosed() {
        RouteActivation route = new RouteActivation("factory_blocks-test");
        assertFalse(route.isActive());
        route.activate();
        assertTrue(route.isActive());
        route.fail("render-failed");
        route.activate();
        route.inactive("operator-disabled");
        assertEquals(RouteActivation.State.FAILED, route.snapshot().state());
        assertEquals("render-failed", route.snapshot().detail());
    }
}
