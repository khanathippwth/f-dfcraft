/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.commented;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationOptions;
import moss.factions.shade.ninja.leaping.configurate.SimpleConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SimpleCommentedConfigurationNode
extends SimpleConfigurationNode
implements CommentedConfigurationNode {
    private final AtomicReference<String> comment = new AtomicReference();

    @Deprecated
    public static @NonNull SimpleCommentedConfigurationNode root() {
        return SimpleCommentedConfigurationNode.root(ConfigurationOptions.defaults());
    }

    @Deprecated
    public static @NonNull SimpleCommentedConfigurationNode root(@NonNull ConfigurationOptions configurationOptions) {
        return new SimpleCommentedConfigurationNode(null, null, configurationOptions);
    }

    protected SimpleCommentedConfigurationNode(@Nullable Object object, @Nullable SimpleConfigurationNode simpleConfigurationNode, @NonNull ConfigurationOptions configurationOptions) {
        super(object, simpleConfigurationNode, configurationOptions);
    }

    protected SimpleCommentedConfigurationNode(@Nullable SimpleConfigurationNode simpleConfigurationNode, @NonNull SimpleConfigurationNode simpleConfigurationNode2) {
        super(simpleConfigurationNode, simpleConfigurationNode2);
    }

    @Override
    public @NonNull Optional<String> getComment() {
        return Optional.ofNullable(this.comment.get());
    }

    @Override
    public @NonNull SimpleCommentedConfigurationNode setComment(@Nullable String string) {
        if (!Objects.equals(this.comment.getAndSet(string), string)) {
            this.attachIfNecessary();
        }
        return this;
    }

    @Override
    public @NonNull CommentedConfigurationNode setCommentIfAbsent(String string) {
        if (this.comment.compareAndSet(null, string)) {
            this.attachIfNecessary();
        }
        return this;
    }

    @Override
    public @Nullable SimpleCommentedConfigurationNode getParent() {
        return (SimpleCommentedConfigurationNode)super.getParent();
    }

    @Override
    protected SimpleCommentedConfigurationNode createNode(Object object) {
        return new SimpleCommentedConfigurationNode(object, this, this.getOptions());
    }

    @Override
    public @NonNull SimpleCommentedConfigurationNode setValue(@Nullable Object object) {
        if (object instanceof CommentedConfigurationNode) {
            ((CommentedConfigurationNode)object).getComment().ifPresent(this::setComment);
        }
        return (SimpleCommentedConfigurationNode)super.setValue(object);
    }

    @Override
    public @NonNull SimpleCommentedConfigurationNode mergeValuesFrom(@NonNull ConfigurationNode configurationNode) {
        if (configurationNode instanceof CommentedConfigurationNode) {
            Optional<String> optional = ((CommentedConfigurationNode)configurationNode).getComment();
            optional.ifPresent(this::setCommentIfAbsent);
        }
        return (SimpleCommentedConfigurationNode)super.mergeValuesFrom(configurationNode);
    }

    @Override
    public @NonNull SimpleCommentedConfigurationNode getNode(@NonNull Object... objectArray) {
        return (SimpleCommentedConfigurationNode)super.getNode(objectArray);
    }

    public @NonNull List<? extends SimpleCommentedConfigurationNode> getChildrenList() {
        return super.getChildrenList();
    }

    public @NonNull Map<Object, ? extends SimpleCommentedConfigurationNode> getChildrenMap() {
        return super.getChildrenMap();
    }

    @Override
    @Deprecated
    public @NonNull SimpleCommentedConfigurationNode getAppendedNode() {
        return (SimpleCommentedConfigurationNode)super.getAppendedNode();
    }

    @Override
    public @NonNull SimpleCommentedConfigurationNode appendListNode() {
        return (SimpleCommentedConfigurationNode)super.appendListNode();
    }

    @Override
    public @NonNull SimpleCommentedConfigurationNode copy() {
        return this.copy(null);
    }

    @Override
    protected @NonNull SimpleCommentedConfigurationNode copy(@Nullable SimpleConfigurationNode simpleConfigurationNode) {
        SimpleCommentedConfigurationNode simpleCommentedConfigurationNode = new SimpleCommentedConfigurationNode(simpleConfigurationNode, this);
        simpleCommentedConfigurationNode.comment.set(this.comment.get());
        return simpleCommentedConfigurationNode;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SimpleCommentedConfigurationNode)) {
            return false;
        }
        if (!super.equals(object)) {
            return false;
        }
        SimpleCommentedConfigurationNode simpleCommentedConfigurationNode = (SimpleCommentedConfigurationNode)object;
        return Objects.equals(this.comment.get(), simpleCommentedConfigurationNode.comment.get());
    }

    @Override
    public int hashCode() {
        int n = super.hashCode();
        n = 31 * n + Objects.hashCode(this.comment.get());
        return n;
    }

    @Override
    public String toString() {
        return "SimpleCommentedConfigurationNode{super=" + super.toString() + ", comment=" + this.comment.get() + '}';
    }
}

