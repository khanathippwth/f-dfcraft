/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard;

import java.util.Collections;
import java.util.PrimitiveIterator;
import java.util.stream.Stream;
import moss.factions.shade.net.kyori.adventure.internal.Internals;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.TextComponent;
import moss.factions.shade.net.kyori.adventure.text.flattener.ComponentFlattener;
import moss.factions.shade.net.kyori.adventure.text.format.TextColor;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.TagNode;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.ValueNode;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Inserting;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Modifying;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tree.Node;
import moss.factions.shade.net.kyori.examination.Examinable;
import moss.factions.shade.net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class AbstractColorChangingTag
implements Modifying,
Examinable {
    private static final ComponentFlattener LENGTH_CALCULATOR = (ComponentFlattener)ComponentFlattener.builder().mapper(TextComponent.class, TextComponent::content).unknownMapper(component -> "_").build();
    private boolean visited;
    private int size = 0;
    private int disableApplyingColorDepth = -1;

    AbstractColorChangingTag() {
    }

    protected final int size() {
        return this.size;
    }

    @Override
    public final void visit(@NotNull Node node, int n) {
        TagNode tagNode;
        if (this.visited) {
            throw new IllegalStateException("Color changing tag instances cannot be re-used, return a new one for each resolve");
        }
        if (node instanceof ValueNode) {
            String string2 = ((ValueNode)node).value();
            this.size += string2.codePointCount(0, string2.length());
        } else if (node instanceof TagNode && (tagNode = (TagNode)node).tag() instanceof Inserting) {
            LENGTH_CALCULATOR.flatten(((Inserting)tagNode.tag()).value(), string -> this.size += string.codePointCount(0, string.length()));
        }
    }

    @Override
    public final void postVisit() {
        this.visited = true;
        this.init();
    }

    @Override
    public final Component apply(@NotNull Component component, int n) {
        if (this.disableApplyingColorDepth != -1 && n > this.disableApplyingColorDepth || component.style().color() != null) {
            if (this.disableApplyingColorDepth == -1 || n < this.disableApplyingColorDepth) {
                this.disableApplyingColorDepth = n;
            }
            if (component instanceof TextComponent) {
                String string = ((TextComponent)component).content();
                int n2 = string.codePointCount(0, string.length());
                for (int i = 0; i < n2; ++i) {
                    this.advanceColor();
                }
            }
            return component.children(Collections.emptyList());
        }
        this.disableApplyingColorDepth = -1;
        if (component instanceof TextComponent && ((TextComponent)component).content().length() > 0) {
            TextComponent textComponent = (TextComponent)component;
            String string = textComponent.content();
            TextComponent.Builder builder = Component.text();
            int[] nArray = new int[1];
            PrimitiveIterator.OfInt ofInt = string.codePoints().iterator();
            while (ofInt.hasNext()) {
                nArray[0] = ofInt.nextInt();
                TextComponent textComponent2 = Component.text(new String(nArray, 0, 1), component.style().color(this.color()));
                this.advanceColor();
                builder.append((Component)textComponent2);
            }
            return builder.build();
        }
        if (!(component instanceof TextComponent)) {
            Component component2 = component.children(Collections.emptyList()).colorIfAbsent(this.color());
            this.advanceColor();
            return component2;
        }
        return Component.empty().mergeStyle(component);
    }

    protected abstract void init();

    protected abstract void advanceColor();

    protected abstract TextColor color();

    @Override
    @NotNull
    public abstract Stream<? extends ExaminableProperty> examinableProperties();

    @NotNull
    public final String toString() {
        return Internals.toString(this);
    }

    public abstract boolean equals(@Nullable Object var1);

    public abstract int hashCode();
}

