/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node;

import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.Token;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.TokenParser;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import org.jetbrains.annotations.NotNull;

public final class TagPart
implements Tag.Argument {
    private final String value;
    private final Token token;

    public TagPart(@NotNull String string, @NotNull Token token, @NotNull TokenParser.TagProvider tagProvider) {
        String string2 = TagPart.unquoteAndEscape(string, token.startIndex(), token.endIndex());
        this.value = string2 = TokenParser.resolvePreProcessTags(string2, tagProvider);
        this.token = token;
    }

    @Override
    @NotNull
    public String value() {
        return this.value;
    }

    @NotNull
    public Token token() {
        return this.token;
    }

    @NotNull
    public static String unquoteAndEscape(@NotNull String string, int n2, int n3) {
        if (n2 == n3) {
            return "";
        }
        int n4 = n2;
        int n5 = n3;
        char c = string.charAt(n4);
        char c2 = string.charAt(n5 - 1);
        if (c == '\'' || c == '\"') {
            ++n4;
        } else {
            return string.substring(n4, n5);
        }
        if (c2 == '\'' || c2 == '\"') {
            --n5;
        }
        if (n4 > n5) {
            return string.substring(n2, n3);
        }
        return TokenParser.unescape(string, n4, n5, n -> n == c || n == 92);
    }

    public String toString() {
        return this.value;
    }
}

