/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.commented;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationOptions;
import moss.factions.shade.ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface CommentedConfigurationNode
extends ConfigurationNode {
    public static @NonNull CommentedConfigurationNode root() {
        return CommentedConfigurationNode.root(ConfigurationOptions.defaults());
    }

    public static @NonNull CommentedConfigurationNode root(@NonNull Consumer<? super CommentedConfigurationNode> action) {
        return CommentedConfigurationNode.root(ConfigurationOptions.defaults(), action);
    }

    public static @NonNull CommentedConfigurationNode root(@NonNull ConfigurationOptions options) {
        return new SimpleCommentedConfigurationNode(null, null, options);
    }

    public static @NonNull CommentedConfigurationNode root(@NonNull ConfigurationOptions options, @NonNull Consumer<? super CommentedConfigurationNode> action) {
        CommentedConfigurationNode ret = CommentedConfigurationNode.root(options);
        action.accept(ret);
        return ret;
    }

    public @NonNull Optional<String> getComment();

    public @NonNull CommentedConfigurationNode setComment(@Nullable String var1);

    default public @NonNull CommentedConfigurationNode setCommentIfAbsent(String comment) {
        if (!this.getComment().isPresent()) {
            this.setComment(comment);
        }
        return this;
    }

    @Override
    public @Nullable CommentedConfigurationNode getParent();

    public @NonNull List<? extends CommentedConfigurationNode> getChildrenList();

    public @NonNull Map<Object, ? extends CommentedConfigurationNode> getChildrenMap();

    @Override
    public @NonNull CommentedConfigurationNode setValue(@Nullable Object var1);

    @Override
    public @NonNull CommentedConfigurationNode mergeValuesFrom(@NonNull ConfigurationNode var1);

    @Override
    @Deprecated
    public @NonNull CommentedConfigurationNode getAppendedNode();

    @Override
    default public @NonNull CommentedConfigurationNode appendListNode() {
        return this.getAppendedNode();
    }

    @Override
    public @NonNull CommentedConfigurationNode getNode(@NonNull Object... var1);

    @Override
    public @NonNull CommentedConfigurationNode copy();

    @Override
    default public CommentedConfigurationNode act(Consumer<? super ConfigurationNode> action) {
        action.accept(this);
        return this;
    }
}

