/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.match;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.TagInternals;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.Token;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.TokenParser;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.TokenType;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.match.MatchedTokenConsumer;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.TagPart;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.PreProcess;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import org.jetbrains.annotations.NotNull;

public final class StringResolvingMatchedTokenConsumer
extends MatchedTokenConsumer<String> {
    private final StringBuilder builder;
    private final TokenParser.TagProvider tagProvider;

    public StringResolvingMatchedTokenConsumer(@NotNull String string, @NotNull TokenParser.TagProvider tagProvider) {
        super(string);
        this.builder = new StringBuilder(string.length());
        this.tagProvider = tagProvider;
    }

    @Override
    public void accept(int n, int n2, @NotNull TokenType tokenType) {
        super.accept(n, n2, tokenType);
        if (tokenType != TokenType.OPEN_TAG) {
            this.builder.append(this.input, n, n2);
        } else {
            String string;
            String string2 = this.input.substring(n, n2);
            String string3 = this.input.substring(n + 1, n2 - 1);
            int n3 = string3.indexOf(58);
            String string4 = string = n3 == -1 ? string3 : string3.substring(0, n3);
            if (TagInternals.sanitizeAndCheckValidTagName(string)) {
                Tag tag;
                List<Token> list;
                List<Token> list2 = TokenParser.tokenize(string2, false);
                ArrayList<TagPart> arrayList = new ArrayList<TagPart>();
                List<Token> list3 = list = list2.isEmpty() ? null : list2.get(0).childTokens();
                if (list != null) {
                    for (int i = 1; i < list.size(); ++i) {
                        arrayList.add(new TagPart(string2, list.get(i), this.tagProvider));
                    }
                }
                if ((tag = this.tagProvider.resolve(TokenParser.TagProvider.sanitizePlaceholderName(string), arrayList, list2.get(0))) instanceof PreProcess) {
                    this.builder.append(Objects.requireNonNull(((PreProcess)tag).value(), "PreProcess replacements cannot return null"));
                    return;
                }
            }
            this.builder.append(string2);
        }
    }

    @Override
    @NotNull
    public String result() {
        return this.builder.toString();
    }
}

