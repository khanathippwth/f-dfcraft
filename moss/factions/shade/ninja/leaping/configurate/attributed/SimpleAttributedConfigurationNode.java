/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.attributed;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationOptions;
import moss.factions.shade.ninja.leaping.configurate.SimpleConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.attributed.AttributedConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SimpleAttributedConfigurationNode
extends SimpleCommentedConfigurationNode
implements AttributedConfigurationNode {
    private String tagName;
    private final Map<String, String> attributes = new LinkedHashMap<String, String>();

    @Deprecated
    public static @NonNull SimpleAttributedConfigurationNode root() {
        return SimpleAttributedConfigurationNode.root("root", ConfigurationOptions.defaults());
    }

    @Deprecated
    public static @NonNull SimpleAttributedConfigurationNode root(@NonNull String string) {
        return SimpleAttributedConfigurationNode.root(string, ConfigurationOptions.defaults());
    }

    @Deprecated
    public static @NonNull SimpleAttributedConfigurationNode root(@NonNull String string, @NonNull ConfigurationOptions configurationOptions) {
        return new SimpleAttributedConfigurationNode(string, null, null, configurationOptions);
    }

    protected SimpleAttributedConfigurationNode(@NonNull String string, @Nullable Object object, @Nullable SimpleConfigurationNode simpleConfigurationNode, @NonNull ConfigurationOptions configurationOptions) {
        super(object, simpleConfigurationNode, configurationOptions);
        this.setTagName(string);
    }

    protected SimpleAttributedConfigurationNode(@NonNull String string, @Nullable SimpleConfigurationNode simpleConfigurationNode, @NonNull SimpleConfigurationNode simpleConfigurationNode2) {
        super(simpleConfigurationNode, simpleConfigurationNode2);
        this.setTagName(string);
    }

    @Override
    public @NonNull String getTagName() {
        return this.tagName;
    }

    @Override
    public @NonNull SimpleAttributedConfigurationNode setTagName(@NonNull String string) {
        if (Strings.isNullOrEmpty(string)) {
            throw new IllegalArgumentException("Tag name cannot be null/empty");
        }
        this.tagName = string;
        return this;
    }

    @Override
    public @NonNull SimpleAttributedConfigurationNode addAttribute(@NonNull String string, @NonNull String string2) {
        if (Strings.isNullOrEmpty(string)) {
            throw new IllegalArgumentException("Attribute name cannot be null/empty");
        }
        this.attachIfNecessary();
        this.attributes.put(string, string2);
        return this;
    }

    @Override
    public @NonNull SimpleAttributedConfigurationNode removeAttribute(@NonNull String string) {
        this.attributes.remove(string);
        return this;
    }

    @Override
    public @NonNull SimpleAttributedConfigurationNode setAttributes(@NonNull Map<String, String> map) {
        for (String string : map.keySet()) {
            if (!Strings.isNullOrEmpty(string)) continue;
            throw new IllegalArgumentException("Attribute name cannot be null/empty");
        }
        this.attributes.clear();
        if (!map.isEmpty()) {
            this.attachIfNecessary();
            this.attributes.putAll(map);
        }
        return this;
    }

    @Override
    public boolean hasAttributes() {
        return !this.attributes.isEmpty();
    }

    @Override
    public @Nullable String getAttribute(@NonNull String string) {
        return this.attributes.get(string);
    }

    @Override
    public @NonNull Map<String, String> getAttributes() {
        return ImmutableMap.copyOf(this.attributes);
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && this.attributes.isEmpty();
    }

    @Override
    public @Nullable SimpleAttributedConfigurationNode getParent() {
        return (SimpleAttributedConfigurationNode)super.getParent();
    }

    @Override
    protected SimpleAttributedConfigurationNode createNode(Object object) {
        return new SimpleAttributedConfigurationNode("element", object, this, this.getOptions());
    }

    @Override
    public @NonNull SimpleAttributedConfigurationNode setValue(@Nullable Object object) {
        if (object instanceof AttributedConfigurationNode) {
            AttributedConfigurationNode attributedConfigurationNode = (AttributedConfigurationNode)object;
            this.setTagName(attributedConfigurationNode.getTagName());
            this.setAttributes((Map)attributedConfigurationNode.getAttributes());
        }
        return (SimpleAttributedConfigurationNode)super.setValue(object);
    }

    @Override
    public @NonNull SimpleAttributedConfigurationNode mergeValuesFrom(@NonNull ConfigurationNode configurationNode) {
        if (configurationNode instanceof AttributedConfigurationNode) {
            AttributedConfigurationNode attributedConfigurationNode = (AttributedConfigurationNode)configurationNode;
            this.setTagName(attributedConfigurationNode.getTagName());
            for (Map.Entry<String, String> entry : attributedConfigurationNode.getAttributes().entrySet()) {
                this.addAttribute(entry.getKey(), entry.getValue());
            }
        }
        return (SimpleAttributedConfigurationNode)super.mergeValuesFrom(configurationNode);
    }

    @Override
    public @NonNull SimpleAttributedConfigurationNode getNode(@NonNull Object... objectArray) {
        return (SimpleAttributedConfigurationNode)super.getNode(objectArray);
    }

    public @NonNull List<? extends SimpleAttributedConfigurationNode> getChildrenList() {
        return super.getChildrenList();
    }

    public @NonNull Map<Object, ? extends SimpleAttributedConfigurationNode> getChildrenMap() {
        return super.getChildrenMap();
    }

    @Override
    @Deprecated
    public @NonNull SimpleAttributedConfigurationNode getAppendedNode() {
        return (SimpleAttributedConfigurationNode)super.getAppendedNode();
    }

    @Override
    public @NonNull SimpleAttributedConfigurationNode appendListNode() {
        return (SimpleAttributedConfigurationNode)super.appendListNode();
    }

    @Override
    public @NonNull SimpleAttributedConfigurationNode copy() {
        return this.copy(null);
    }

    @Override
    protected @NonNull SimpleAttributedConfigurationNode copy(@Nullable SimpleConfigurationNode simpleConfigurationNode) {
        SimpleAttributedConfigurationNode simpleAttributedConfigurationNode = new SimpleAttributedConfigurationNode(this.tagName, simpleConfigurationNode, this);
        simpleAttributedConfigurationNode.attributes.putAll(this.attributes);
        this.getComment().ifPresent(simpleAttributedConfigurationNode::setComment);
        return simpleAttributedConfigurationNode;
    }

    @Override
    public @NonNull SimpleAttributedConfigurationNode setComment(@Nullable String string) {
        return (SimpleAttributedConfigurationNode)super.setComment(string);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SimpleAttributedConfigurationNode)) {
            return false;
        }
        if (!super.equals(object)) {
            return false;
        }
        SimpleAttributedConfigurationNode simpleAttributedConfigurationNode = (SimpleAttributedConfigurationNode)object;
        return this.tagName.equals(simpleAttributedConfigurationNode.tagName) && this.attributes.equals(simpleAttributedConfigurationNode.attributes);
    }

    @Override
    public int hashCode() {
        int n = super.hashCode();
        n = 31 * n + this.tagName.hashCode();
        n = 31 * n + this.attributes.hashCode();
        return n;
    }

    @Override
    public String toString() {
        return "SimpleAttributedConfigurationNode{super=" + super.toString() + ", tagName=" + this.tagName + ", attributes=" + this.attributes + '}';
    }
}

