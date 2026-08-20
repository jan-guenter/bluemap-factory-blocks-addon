/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.factoryblocks.activation;

/** Process-scoped state for the single exact Factory Blocks/Athena route. */
public final class FactoryBlocksRuntime {

    public static final String ROUTE_ID = "factory-blocks-athena-1.4.0-4.0.6";
    public static final FactoryBlocksRuntime INSTANCE = new FactoryBlocksRuntime();

    private final RouteActivation route = new RouteActivation(ROUTE_ID);

    private FactoryBlocksRuntime() {
    }

    public RouteActivation route() {
        return route;
    }

    public void disable(String detail) {
        route.fail(detail);
    }
}
