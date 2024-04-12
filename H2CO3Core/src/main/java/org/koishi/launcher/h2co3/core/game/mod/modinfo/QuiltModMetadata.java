package org.koishi.launcher.h2co3.core.game.mod.modinfo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.koishi.launcher.h2co3.core.download.ModLoaderType;
import org.koishi.launcher.h2co3.core.game.mod.LocalModFile;
import org.koishi.launcher.h2co3.core.game.mod.ModManager;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.core.utils.gson.JsonUtils;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

public final class QuiltModMetadata {
    private final int schema_version;
    private final QuiltLoader quilt_loader;
    public QuiltModMetadata(int schemaVersion, QuiltLoader quiltLoader) {
        this.schema_version = schemaVersion;
        this.quilt_loader = quiltLoader;
    }

    public static LocalModFile fromFile(ModManager modManager, Path modFile, FileSystem fs) throws IOException, JsonParseException {
        Path path = fs.getPath("quilt.mod.json");
        if (Files.notExists(path)) {
            throw new IOException("File " + modFile + " is not a Quilt mod.");
        }

        QuiltModMetadata root = JsonUtils.fromNonNullJson(FileTools.readText(path), QuiltModMetadata.class);
        if (root.schema_version != 1) {
            throw new IOException("File " + modFile + " is not a supported Quilt mod.");
        }

        return new LocalModFile(
                modManager,
                modManager.getLocalMod(root.quilt_loader.id, ModLoaderType.QUILT),
                modFile,
                root.quilt_loader.metadata.name,
                new LocalModFile.Description(root.quilt_loader.metadata.description),
                root.quilt_loader.metadata.contributors.entrySet().stream().map(entry -> String.format("%s (%s)", entry.getKey(), entry.getValue().getAsJsonPrimitive().getAsString())).collect(Collectors.joining(", ")),
                root.quilt_loader.version,
                "",
                Optional.ofNullable(root.quilt_loader.metadata.contact.get("homepage")).map(jsonElement -> jsonElement.getAsJsonPrimitive().getAsString()).orElse(""),
                root.quilt_loader.metadata.icon
        );
    }

    private static final class QuiltLoader {
        private final String id;
        private final String version;
        private final Metadata metadata;
        public QuiltLoader(String id, String version, Metadata metadata) {
            this.id = id;
            this.version = version;
            this.metadata = metadata;
        }

        private static final class Metadata {
            private final String name;
            private final String description;
            private final JsonObject contributors;
            private final String icon;
            private final JsonObject contact;

            public Metadata(String name, String description, JsonObject contributors, String icon, JsonObject contact) {
                this.name = name;
                this.description = description;
                this.contributors = contributors;
                this.icon = icon;
                this.contact = contact;
            }
        }
    }
}
