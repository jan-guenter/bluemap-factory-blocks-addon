/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.factoryblocks.adapter.bluemap523;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePackExtension;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.VariantSet;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variants;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import io.github.janguenter.bluemap.factoryblocks.activation.FactoryBlocksRuntime;
import io.github.janguenter.bluemap.factoryblocks.profile.FactoryBlocks140Athena406Profile;
import io.github.janguenter.bluemap.factoryblocks.profile.ExactModArtifactDetector;
import io.github.janguenter.bluemap.factoryblocks.profile.ProfileDisablement;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Exact artifact/schema activation and routing for the Factory Blocks/Athena profile. */
final class FactoryBlocksResourceExtension implements ResourcePackExtension {

    static final Key SYNTHETIC = Key.parse("bluemap_factory_blocks:athena_shape");

    private final ResourcePack resourcePack;
    private final FactoryBlocksRuntime runtime;

    FactoryBlocksResourceExtension(ResourcePack resourcePack, FactoryBlocksRuntime runtime) {
        this.resourcePack = resourcePack;
        this.runtime = runtime;
    }

    @Override
    public void loadResources(Iterable<Path> roots) throws IOException, InterruptedException {
        if (ProfileDisablement.current().isDisabled(
                FactoryBlocks140Athena406Profile.PROFILE_ID
        )) {
            runtime.route().inactive("operator-disabled");
            return;
        }
        if (!ExactModArtifactDetector.matchesRequiredPair(roots)) {
            runtime.route().inactive("exact-artifact-pair-missing");
            return;
        }
        ActiveResourceSchemaValidator.Result schema = ActiveResourceSchemaValidator.validate(
                resourcePack, roots, FactoryBlocks140Athena406Profile.DEFINITIONS
        );
        if (!schema.valid()) {
            runtime.route().inactive(schema.reason());
            return;
        }
        de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState dispatch =
                resourcePack.getBlockStates().get(SYNTHETIC);
        if (!validDispatch(dispatch)) {
            runtime.route().inactive("synthetic-dispatch-invalid");
            return;
        }
        runtime.route().activate();
    }

    @Override
    public Set<Key> collectUsedTextureKeys() {
        return runtime.route().isActive()
                ? FactoryBlocks140Athena406Profile.REQUIRED_TEXTURES
                : Set.of();
    }

    @Override
    public void bake() {
        if (!runtime.route().isActive()) {
            return;
        }
        for (Key texture : FactoryBlocks140Athena406Profile.REQUIRED_TEXTURES) {
            if (resourcePack.getTextures().get(texture) == null) {
                runtime.route().inactive("required-texture-missing");
                return;
            }
        }
        Map<Key, Texture> firstFrames = new LinkedHashMap<>();
        try {
            for (Key key : FactoryBlocks140Athena406Profile.STATIC_FIRST_FRAME_TEXTURES) {
                Texture texture = resourcePack.getTextures().get(key);
                if (texture == null) {
                    runtime.route().inactive("animated-texture-missing");
                    return;
                }
                firstFrames.put(key, Texture.from(key, firstFrame(texture.getTextureImage())));
            }
        } catch (IOException | RuntimeException exception) {
            runtime.route().inactive("animated-texture-invalid");
            return;
        }
        firstFrames.forEach(resourcePack.getTextures()::put);
    }

    @Override
    public Key getBlockStateKey(Key key) {
        return runtime.route().isActive()
                && FactoryBlocks140Athena406Profile.ROUTED_BLOCKS.contains(key.getFormatted())
                ? SYNTHETIC : key;
    }

    @Override
    public void getBlockProperties(BlockState blockState, BlockProperties.Builder builder) {
        if (!runtime.route().isActive()) {
            return;
        }
        io.github.janguenter.bluemap.factoryblocks.profile.FactoryBlocksDefinition definition =
                FactoryBlocks140Athena406Profile.DEFINITIONS.get(
                        blockState.getId().getFormatted()
                );
        if (definition == null) {
            return;
        }
        Model originalModel = resourcePack.getModels().get(Key.parse(definition.modelId()));
        if (originalModel != null) {
            // Routing swaps in a synthetic dispatch model. Preserve the active
            // full-cube model's alpha-sensitive culling result. All owned
            // ordinary models are full cube_all shapes, so a
            // non-culling result means an active translucent texture and must
            // also be non-occluding (BlueMap's generic model property alone
            // treats every full cube as occluding regardless of alpha).
            List<Texture> roleTextures = activeCullingRoles(definition).stream()
                    .map(definition::texture)
                    .map(Key::parse)
                    .map(resourcePack.getTextures()::get)
                    .toList();
            boolean opaqueFullCube = opaqueFullCube(
                    originalModel.isCulling(), roleTextures
            );
            builder.culling(opaqueFullCube)
                    .occluding(opaqueFullCube)
                    .cullingIdentical(false);
        }
    }

    static List<String> activeCullingRoles(
            io.github.janguenter.bluemap.factoryblocks.profile.FactoryBlocksDefinition definition
    ) {
        return definition.family().textureRoles().stream()
                .filter(role -> definition.family() !=
                        io.github.janguenter.bluemap.factoryblocks.profile.LoaderFamily.GIANT
                        || !role.equals("particle"))
                .toList();
    }

    static boolean opaqueFullCube(
            boolean originalModelCulling,
            List<Texture> roleTextures
    ) {
        return originalModelCulling
                && !roleTextures.isEmpty()
                && roleTextures.stream().allMatch(texture ->
                        texture != null && texture.getColorStraight().a >= 1F
                );
    }

    static BufferedImage firstFrame(BufferedImage image) {
        if (image == null || image.getWidth() < 1 || image.getHeight() < image.getWidth()
                || image.getHeight() % image.getWidth() != 0) {
            throw new IllegalArgumentException("texture is not a vertical square-frame strip");
        }
        return image.getSubimage(0, 0, image.getWidth(), image.getWidth());
    }

    private static boolean validDispatch(
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state
    ) {
        if (state == null || state.getMultipart() != null) {
            return false;
        }
        Variants variants = state.getVariants();
        if (variants == null || variants.getDefaultVariant() == null) {
            return false;
        }
        VariantSet set = variants.getDefaultVariant();
        if (set.getVariants().length != 1) {
            return false;
        }
        Variant variant = set.getVariants()[0];
        return BlueMap523Adapter.isExpectedDispatch(variant);
    }
}
