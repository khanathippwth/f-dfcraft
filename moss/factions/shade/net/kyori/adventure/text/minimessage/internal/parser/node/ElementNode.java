/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.Token;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.TokenType;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.TextNode;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tree.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElementNode
implements Node {
    @Nullable
    private final ElementNode parent;
    @Nullable
    private final Token token;
    private final String sourceMessage;
    private final List<ElementNode> children = new ArrayList<ElementNode>();

    ElementNode(@Nullable ElementNode elementNode, @Nullable Token token, @NotNull String string) {
        this.parent = elementNode;
        this.token = token;
        this.sourceMessage = string;
    }

    @Override
    @Nullable
    public ElementNode parent() {
        return this.parent;
    }

    @Nullable
    public Token token() {
        return this.token;
    }

    @NotNull
    public String sourceMessage() {
        return this.sourceMessage;
    }

    @NotNull
    public List<ElementNode> children() {
        return Collections.unmodifiableList(this.children);
    }

    @NotNull
    public List<ElementNode> unsafeChildren() {
        return this.children;
    }

    public void addChild(@NotNull ElementNode elementNode) {
        int n = this.children.size() - 1;
        if (!(elementNode instanceof TextNode) || this.children.isEmpty() || !(this.children.get(n) instanceof TextNode)) {
            this.children.add(elementNode);
        } else {
            TextNode textNode = (TextNode)this.children.remove(n);
            if (textNode.token().endIndex() == elementNode.token().startIndex()) {
                Token token = new Token(textNode.token().startIndex(), elementNode.token().endIndex(), TokenType.TEXT);
                this.children.add(new TextNode(this, token, textNode.sourceMessage()));
            } else {
                this.children.add(textNode);
                this.children.add(elementNode);
            }
        }
    }

    @NotNull
    public StringBuilder buildToString(@NotNull StringBuilder stringBuilder, int n) {
        char[] cArray = this.ident(n);
        stringBuilder.append(cArray).append("Node {\n");
        for (ElementNode elementNode : this.children) {
            elementNode.buildToString(stringBuilder, n + 1);
        }
        stringBuilder.append(cArray).append("}\n");
        return stringBuilder;
    }

    char @NotNull [] ident(int n) {
        char[] cArray = new char[n * 2];
        Arrays.fill(cArray, ' ');
        return cArray;
    }

    @Override
    @NotNull
    public String toString() {
        return this.buildToString(new StringBuilder(), 0).toString();
    }
}

