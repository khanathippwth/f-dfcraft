/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.attributed;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationOptions;
import moss.factions.shade.ninja.leaping.configurate.attributed.SimpleAttributedConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface AttributedConfigurationNode
extends CommentedConfigurationNode {
    public static @NonNull AttributedConfigurationNode root() {
        return AttributedConfigurationNode.root("root", ConfigurationOptions.defaults());
    }

    public static AttributedConfigurationNode root(Consumer<? super AttributedConfigurationNode> action) {
        return AttributedConfigurationNode.root("root", action);
    }

    public static @NonNull AttributedConfigurationNode root(@NonNull String tagName) {
        return AttributedConfigurationNode.root(tagName, ConfigurationOptions.defaults());
    }

    public static @NonNull AttributedConfigurationNode root(@NonNull String tagName, Consumer<? super AttributedConfigurationNode> action) {
        return AttributedConfigurationNode.root(tagName, ConfigurationOptions.defaults(), action);
    }

    public static @NonNull AttributedConfigurationNode root(@NonNull String tagName, @NonNull ConfigurationOptions options) {
        return new SimpleAttributedConfigurationNode(tagName, null, null, options);
    }

    public static @NonNull AttributedConfigurationNode root(@NonNull String tagName, @NonNull ConfigurationOptions options, Consumer<? super AttributedConfigurationNode> action) {
        AttributedConfigurationNode ret = AttributedConfigurationNode.root(tagName, options);
        action.accept(ret);
        return ret;
    }

    public @NonNull String getTagName();

    public @NonNull AttributedConfigurationNode setTagName(@NonNull String var1);

    public @NonNull AttributedConfigurationNode addAttribute(@NonNull String var1, @NonNull String var2);

    public @NonNull AttributedConfigurationNode removeAttribute(@NonNull String var1);

    public @NonNull AttributedConfigurationNode setAttributes(@NonNull Map<String, String> var1);

    public boolean hasAttributes();

    public @Nullable String getAttribute(@NonNull String var1);

    public @NonNull Map<String, String> getAttributes();

    @Override
    default public @NonNull Optional<String> getComment() {
        return Optional.empty();
    }

    @Override
    public @Nullable AttributedConfigurationNode getParent();

    public @NonNull List<? extends AttributedConfigurationNode> getChildrenList();

    public @NonNull Map<Object, ? extends AttributedConfigurationNode> getChildrenMap();

    @Override
    default public @NonNull AttributedConfigurationNode setComment(@Nullable String value) {
        return this;
    }

    @Override
    public @NonNull AttributedConfigurationNode setValue(@Nullable Object var1);

    @Override
    public @NonNull AttributedConfigurationNode mergeValuesFrom(@NonNull ConfigurationNode var1);

    @Override
    @Deprecated
    public @NonNull AttributedConfigurationNode getAppendedNode();

    @Override
    default public @NonNull AttributedConfigurationNode appendListNode() {
        return this.getAppendedNode();
    }

    @Override
    public @NonNull AttributedConfigurationNode getNode(@NonNull Object... var1);

    @Override
    public @NonNull AttributedConfigurationNode copy();

    @Override
    default public AttributedConfigurationNode act(Consumer<? super ConfigurationNode> action) {
        action.accept(this);
        return this;
    }
}

