/*
 * SPDX-License-Identifier: MIT
 */
package io.github.janguenter.bluemap.factoryblocks.profile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** Strict immutable loader for the packaged metadata-only profile. */
public final class DefinitionCatalog {

    private static final int MAX_BYTES = 2 * 1024 * 1024;

    private final Map<String, FactoryBlocksDefinition> definitions;
    private final Set<String> textureIds;

    private DefinitionCatalog(
            Map<String, FactoryBlocksDefinition> definitions,
            Set<String> textureIds
    ) {
        this.definitions = Collections.unmodifiableMap(new LinkedHashMap<>(definitions));
        this.textureIds = Collections.unmodifiableSet(new TreeSet<>(textureIds));
    }

    public static DefinitionCatalog load(
            String resource,
            int expectedRows,
            String expectedSha256
    ) {
        byte[] raw;
        try (InputStream input = DefinitionCatalog.class.getResourceAsStream(resource)) {
            if (input == null) {
                throw new IllegalStateException("definition catalog is missing");
            }
            raw = input.readNBytes(MAX_BYTES + 1);
        } catch (IOException exception) {
            throw new IllegalStateException("definition catalog is unreadable", exception);
        }
        if (raw.length > MAX_BYTES || !expectedSha256.equals(sha256(raw))) {
            throw new IllegalStateException("definition catalog integrity mismatch");
        }
        String text = new String(raw, StandardCharsets.US_ASCII);
        if (!text.endsWith("\n")) {
            throw new IllegalStateException("definition catalog is not LF-terminated");
        }

        Map<String, FactoryBlocksDefinition> definitions = new LinkedHashMap<>();
        Set<String> textures = new TreeSet<>();
        String previous = null;
        for (String line : text.split("\n", -1)) {
            if (line.isEmpty()) {
                continue;
            }
            String[] fields = line.split("\t", -1);
            if (fields.length < 2) {
                throw new IllegalStateException("definition catalog row shape changed");
            }
            LoaderFamily family = LoaderFamily.parse(fields[1]);
            int digestIndex = 4 + family.textureRoles().size();
            if (fields.length != digestIndex + 1) {
                throw new IllegalStateException("definition catalog row shape changed");
            }
            List<String> rowTextures = List.copyOf(
                    Arrays.asList(fields).subList(4, digestIndex)
            );
            FactoryBlocksDefinition definition = new FactoryBlocksDefinition(
                    fields[0], family, fields[2], fields[3], rowTextures, fields[digestIndex]
            );
            if (previous != null && previous.compareTo(definition.blockId()) >= 0) {
                throw new IllegalStateException("definition catalog is not sorted");
            }
            if (definitions.put(definition.blockId(), definition) != null) {
                throw new IllegalStateException("definition catalog repeats a block");
            }
            textures.addAll(rowTextures);
            previous = definition.blockId();
        }
        if (definitions.size() != expectedRows) {
            throw new IllegalStateException("definition catalog row count changed");
        }
        return new DefinitionCatalog(definitions, textures);
    }

    public Map<String, FactoryBlocksDefinition> definitions() {
        return definitions;
    }

    public FactoryBlocksDefinition get(String blockId) {
        return definitions.get(blockId);
    }

    public Set<String> textureIds() {
        return textureIds;
    }

    private static String sha256(byte[] raw) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(raw));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }
}
