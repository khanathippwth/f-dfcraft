/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node;

import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.Token;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.TokenParser;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.ElementNode;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.ValueNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TextNode
extends ValueNode {
    private static boolean isEscape(int n) {
        return n == 60 || n == 92;
    }

    public TextNode(@Nullable ElementNode elementNode, @NotNull Token token, @NotNull String string) {
        super(elementNode, token, string, TokenParser.unescape(string, token.startIndex(), token.endIndex(), TextNode::isEscape));
    }

    @Override
    String valueName() {
        return "TextNode";
    }
}

