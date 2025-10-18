/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.loader;

import java.io.IOException;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationOptions;
import moss.factions.shade.ninja.leaping.configurate.reference.ConfigurationReference;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface ConfigurationLoader<NodeType extends ConfigurationNode> {
    public @NonNull ConfigurationOptions getDefaultOptions();

    default public @NonNull NodeType load() throws IOException {
        return this.load(this.getDefaultOptions());
    }

    default public ConfigurationReference<NodeType> loadToReference() throws IOException {
        return ConfigurationReference.createFixed(this);
    }

    public @NonNull NodeType load(@NonNull ConfigurationOptions var1) throws IOException;

    public void save(@NonNull ConfigurationNode var1) throws IOException;

    default public @NonNull NodeType createEmptyNode() {
        return this.createEmptyNode(this.getDefaultOptions());
    }

    public @NonNull NodeType createEmptyNode(@NonNull ConfigurationOptions var1);

    default public boolean canLoad() {
        return true;
    }

    default public boolean canSave() {
        return true;
    }
}

