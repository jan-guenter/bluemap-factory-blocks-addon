/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.factoryblocks.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.MaxCapacityReachedException;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.ResourceModelRenderer;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import io.github.janguenter.bluemap.factoryblocks.activation.FactoryBlocksRuntime;
import io.github.janguenter.bluemap.factoryblocks.model.GiantTextureSelector;
import io.github.janguenter.bluemap.factoryblocks.profile.FactoryBlocks140Athena406Profile;
import io.github.janguenter.bluemap.factoryblocks.profile.FactoryBlocksDefinition;
import io.github.janguenter.bluemap.factoryblocks.profile.LoaderFamily;
import io.github.janguenter.bluemap.resource.athena.model.CtmConnections;
import io.github.janguenter.bluemap.resource.athena.model.CtmTextureRole;
import io.github.janguenter.bluemap.resource.athena.model.CubeFace;

import java.util.function.Consumer;

/** Static connected renderer for the two exact Factory Blocks Athena loader families. */
final class FactoryBlocksRenderer implements BlockRenderer {

    private final ResourcePack resourcePack;
    private final FactoryBlocksRuntime runtime;
    private final ResourceModelRenderer stock;
    private final AthenaQuadEmitter emitter;
    private final BoundedDiagnostics diagnostics = new BoundedDiagnostics();

    FactoryBlocksRenderer(
            ResourcePack resourcePack,
            TextureGallery textureGallery,
            RenderSettings renderSettings,
            FactoryBlocksRuntime runtime
    ) {
        this.resourcePack = resourcePack;
        this.runtime = runtime;
        this.stock = new ResourceModelRenderer(resourcePack, textureGallery, renderSettings);
        this.emitter = new AthenaQuadEmitter(resourcePack, textureGallery, renderSettings);
    }

    @Override
    public void render(
            BlockNeighborhood block,
            Variant original,
            TileModelView target,
            Color mapColor
    ) {
        int start = target.getStart();
        Color initialMapColor = new Color().set(mapColor);
        if (!runtime.route().isActive()) {
            renderStock(block, target, mapColor);
            return;
        }
        FactoryBlocksDefinition definition = FactoryBlocks140Athena406Profile.DEFINITIONS.get(
                block.getBlockState().getId().getFormatted()
        );
        if (definition == null) {
            diagnostics.report("unknown-routed-block");
            renderStock(block, target, mapColor);
            return;
        }

        emitter.beginVariantColor();
        try {
            boolean rendered = switch (definition.family()) {
                case CTM -> renderCtm(definition, block, target, mapColor);
                case GIANT -> renderGiant(definition, block, target, mapColor);
            };
            if (!rendered) {
                diagnostics.report("resource-render-failed");
                resetAndRenderStock(block, target, start, mapColor, initialMapColor);
            } else {
                emitter.finishVariantColor(mapColor);
            }
        } catch (MaxCapacityReachedException exception) {
            resetPartialGeometry(target, start, mapColor, initialMapColor);
            throw capacityFailure(exception);
        } catch (IllegalArgumentException exception) {
            diagnostics.report("malformed-persisted-state");
            resetAndRenderStock(block, target, start, mapColor, initialMapColor);
        } catch (RuntimeException exception) {
            diagnostics.report("contained-render-failure");
            resetAndRenderStock(block, target, start, mapColor, initialMapColor);
        }
    }

    private boolean renderCtm(
            FactoryBlocksDefinition definition,
            BlockNeighborhood block,
            TileModelView target,
        Color mapColor
    ) {
        for (CubeFace face : CubeFace.values()) {
            if (earlySuppressesInternalFace(definition.family())
                    && sameBlock(block, face.normal())) {
                continue;
            }
            CtmConnections connections = connections(block, face);
            if (connections.completelyConnected()) {
                if (!emitFull(definition, "empty", block, target, mapColor, face)) {
                    return false;
                }
                continue;
            }
            if (!emitQuarter(definition, block, target, mapColor, face,
                    0F, 0.5F, 0.5F, 1F, connections.quadrants().get(0))
                    || !emitQuarter(definition, block, target, mapColor, face,
                    0.5F, 0.5F, 1F, 1F, connections.quadrants().get(1))
                    || !emitQuarter(definition, block, target, mapColor, face,
                    0F, 0F, 0.5F, 0.5F, connections.quadrants().get(2))
                    || !emitQuarter(definition, block, target, mapColor, face,
                    0.5F, 0F, 1F, 0.5F, connections.quadrants().get(3))) {
                return false;
            }
        }
        return true;
    }

    static boolean earlySuppressesInternalFace(LoaderFamily family) {
        return family == LoaderFamily.CTM;
    }

    private boolean renderGiant(
            FactoryBlocksDefinition definition,
            BlockNeighborhood block,
            TileModelView target,
        Color mapColor
    ) {
        for (CubeFace face : CubeFace.values()) {
            String role = Integer.toString(GiantTextureSelector.select(
                    face, block.getX(), block.getY(), block.getZ()
            ));
            if (!emitFull(definition, role, block, target, mapColor, face)) {
                return false;
            }
        }
        return true;
    }

    private boolean emitQuarter(
            FactoryBlocksDefinition definition,
            BlockNeighborhood block,
            TileModelView target,
            Color mapColor,
            CubeFace face,
            float left,
            float bottom,
            float right,
            float top,
            CtmTextureRole role
    ) {
        return emitter.emit(
                block, target, mapColor, face, 0F, left, bottom, right, top,
                Key.parse(definition.texture(role.wireName())), 0, true
        );
    }

    private boolean emitFull(
            FactoryBlocksDefinition definition,
            String role,
            BlockNeighborhood block,
            TileModelView target,
            Color mapColor,
            CubeFace face
    ) {
        return emitter.emit(
                block, target, mapColor, face, 0F, 0F, 0F, 1F, 1F,
                Key.parse(definition.texture(role)), 0, true
        );
    }

    static CtmConnections connections(BlockNeighborhood block, CubeFace face) {
        CubeFace.Vec up = face.localUp();
        CubeFace.Vec down = face.localDown();
        CubeFace.Vec left = face.localLeft();
        CubeFace.Vec right = face.localRight();
        return new CtmConnections(
                sameState(block, up),
                sameState(block, down),
                sameState(block, left),
                sameState(block, right),
                sameState(block, up.add(left)),
                sameState(block, up.add(right)),
                sameState(block, down.add(left)),
                sameState(block, down.add(right))
        );
    }

    private static boolean sameState(BlockNeighborhood block, CubeFace.Vec offset) {
        return block.getNeighborBlock(offset.x(), offset.y(), offset.z())
                .getBlockState().equals(block.getBlockState());
    }

    private static boolean sameBlock(BlockNeighborhood block, CubeFace.Vec offset) {
        return block.getNeighborBlock(offset.x(), offset.y(), offset.z())
                .getBlockState().getId().equals(block.getBlockState().getId());
    }

    private void resetAndRenderStock(
            BlockNeighborhood block,
            TileModelView target,
            int start,
            Color mapColor,
            Color initialMapColor
    ) {
        renderStockAtomically(
                target, start, mapColor, initialMapColor,
                () -> renderStock(block, target, mapColor)
        );
    }

    static void renderStockAtomically(
            TileModelView target,
            int start,
            Color mapColor,
            Color initialMapColor,
            Runnable stockRender
    ) {
        resetPartialGeometry(target, start, mapColor, initialMapColor);
        try {
            stockRender.run();
        } catch (MaxCapacityReachedException exception) {
            resetPartialGeometry(target, start, mapColor, initialMapColor);
            throw exception;
        }
    }

    static void resetPartialGeometry(
            TileModelView target,
            int start,
            Color mapColor,
            Color initialMapColor
    ) {
        target.getTileModel().reset(start);
        target.initialize(start);
        mapColor.set(initialMapColor);
    }

    static MaxCapacityReachedException capacityFailure(
            MaxCapacityReachedException exception
    ) {
        return exception;
    }

    private void renderStock(
            BlockNeighborhood block,
            TileModelView target,
            Color mapColor
    ) {
        de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state =
                resourcePack.getBlockStates().get(block.getBlockState().getId());
        if (state == null) {
            return;
        }
        forEachIsolatedVariant(
                state,
                block.getBlockState(),
                block.getX(), block.getY(), block.getZ(),
                target,
                variant -> stock.render(block, variant, target, mapColor)
        );
    }

    static void forEachIsolatedVariant(
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state,
            BlockState worldState,
            int x,
            int y,
            int z,
            TileModelView target,
            Consumer<Variant> renderer
    ) {
        state.forEach(worldState, x, y, z, variant -> {
            target.initialize();
            renderer.accept(variant);
        });
    }
}
