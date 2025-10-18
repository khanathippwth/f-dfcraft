/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.ParsingExceptionImpl;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.Token;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.TokenParser;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.ElementNode;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.TagPart;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TagNode
extends ElementNode {
    private final List<TagPart> parts;
    @Nullable
    private Tag tag = null;

    public TagNode(@NotNull ElementNode elementNode, @NotNull Token token, @NotNull String string, @NotNull TokenParser.TagProvider tagProvider) {
        super(elementNode, token, string);
        this.parts = TagNode.genParts(token, string, tagProvider);
        if (this.parts.isEmpty()) {
            throw new ParsingExceptionImpl("Tag has no parts? " + this, this.sourceMessage(), this.token());
        }
    }

    @NotNull
    private static List<TagPart> genParts(@NotNull Token token, @NotNull String string, @NotNull TokenParser.TagProvider tagProvider) {
        ArrayList<TagPart> arrayList = new ArrayList<TagPart>();
        if (token.childTokens() != null) {
            for (Token token2 : token.childTokens()) {
                arrayList.add(new TagPart(string, token2, tagProvider));
            }
        }
        return arrayList;
    }

    @NotNull
    public List<TagPart> parts() {
        return this.parts;
    }

    @NotNull
    public String name() {
        return this.parts.get(0).value();
    }

    @Override
    @NotNull
    public Token token() {
        return Objects.requireNonNull(super.token(), "token is not set");
    }

    @NotNull
    public Tag tag() {
        return Objects.requireNonNull(this.tag, "no tag set");
    }

    public void tag(@NotNull Tag tag) {
        this.tag = tag;
    }

    @Override
    @NotNull
    public StringBuilder buildToString(@NotNull StringBuilder stringBuilder, int n) {
        char[] cArray = this.ident(n);
        stringBuilder.append(cArray).append("TagNode(");
        int n2 = this.parts.size();
        for (int i = 0; i < n2; ++i) {
            TagPart object = this.parts.get(i);
            stringBuilder.append('\'').append(object.value()).append('\'');
            if (i == n2 - 1) continue;
            stringBuilder.append(", ");
        }
        stringBuilder.append(") {\n");
        for (ElementNode elementNode : this.children()) {
            elementNode.buildToString(stringBuilder, n + 1);
        }
        stringBuilder.append(cArray).append("}\n");
        return stringBuilder;
    }
}

