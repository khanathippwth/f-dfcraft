/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.TagInternals;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.ParsingExceptionImpl;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.Token;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.TokenType;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.match.MatchedTokenConsumer;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.match.StringResolvingMatchedTokenConsumer;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.match.TokenListProducingMatchedTokenConsumer;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.ElementNode;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.RootNode;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.TagNode;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.TagPart;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.TextNode;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Inserting;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.ParserDirective;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class TokenParser {
    private static final int MAX_DEPTH = 16;
    public static final char TAG_START = '<';
    public static final char TAG_END = '>';
    public static final char CLOSE_TAG = '/';
    public static final char SEPARATOR = ':';
    public static final char ESCAPE = '\\';

    private TokenParser() {
    }

    public static RootNode parse(@NotNull TagProvider tagProvider, @NotNull Predicate<String> predicate, @NotNull String string, @NotNull String string2, boolean bl) {
        List<Token> list = TokenParser.tokenize(string, false);
        return TokenParser.buildTree(tagProvider, predicate, list, string, string2, bl);
    }

    public static String resolvePreProcessTags(String string, TagProvider tagProvider) {
        String string2;
        int n = 0;
        String string3 = string;
        do {
            string2 = string3;
            StringResolvingMatchedTokenConsumer stringResolvingMatchedTokenConsumer = new StringResolvingMatchedTokenConsumer(string2, tagProvider);
            TokenParser.parseString(string2, false, stringResolvingMatchedTokenConsumer);
            string3 = stringResolvingMatchedTokenConsumer.result();
        } while (++n < 16 && !string2.equals(string3));
        return string2;
    }

    public static List<Token> tokenize(String string, boolean bl) {
        TokenListProducingMatchedTokenConsumer tokenListProducingMatchedTokenConsumer = new TokenListProducingMatchedTokenConsumer(string);
        TokenParser.parseString(string, bl, tokenListProducingMatchedTokenConsumer);
        Object object = tokenListProducingMatchedTokenConsumer.result();
        TokenParser.parseSecondPass(string, (List<Token>)object);
        return object;
    }

    public static void parseString(String string, boolean bl, MatchedTokenConsumer<?> matchedTokenConsumer) {
        int n;
        FirstPassState firstPassState = FirstPassState.NORMAL;
        boolean bl2 = false;
        int n2 = 0;
        int n3 = -1;
        int n4 = 0;
        int n5 = string.length();
        for (n = 0; n < n5; ++n) {
            int n6;
            int n7 = string.codePointAt(n);
            if (!bl && n7 == 167 && n + 1 < n5 && ((n6 = Character.toLowerCase(string.codePointAt(n + 1))) >= 48 && n6 <= 57 || n6 >= 97 && n6 <= 102 || n6 == 114 || n6 >= 107 && n6 <= 111)) {
                throw new ParsingExceptionImpl("Legacy formatting codes have been detected in a MiniMessage string - this is unsupported behaviour. Please refer to the Adventure documentation (https://docs.advntr.dev) for more information.", string, null, true, new Token(n, n + 2, TokenType.TEXT));
            }
            if (!Character.isBmpCodePoint(n7)) {
                ++n;
            }
            if (!bl2) {
                if (n7 == 92 && n + 1 < string.length()) {
                    n6 = string.codePointAt(n + 1);
                    switch (firstPassState) {
                        case NORMAL: {
                            bl2 = n6 == 60 || n6 == 92;
                            break;
                        }
                        case STRING: {
                            bl2 = n4 == n6 || n6 == 92;
                            break;
                        }
                        case TAG: {
                            if (n6 != 60) break;
                            bl2 = true;
                            firstPassState = FirstPassState.NORMAL;
                        }
                    }
                    if (bl2) {
                        continue;
                    }
                }
            } else {
                bl2 = false;
                continue;
            }
            switch (firstPassState) {
                case NORMAL: {
                    if (n7 != 60) break;
                    n3 = n;
                    firstPassState = FirstPassState.TAG;
                    break;
                }
                case TAG: {
                    switch (n7) {
                        case 62: {
                            if (n == n3 + 1) {
                                firstPassState = FirstPassState.NORMAL;
                                break;
                            }
                            if (n2 != n3) {
                                matchedTokenConsumer.accept(n2, n3, TokenType.TEXT);
                            }
                            n2 = n + 1;
                            TokenType tokenType = TokenType.OPEN_TAG;
                            if (TokenParser.boundsCheck(string, n3, 1) && string.charAt(n3 + 1) == '/') {
                                tokenType = TokenType.CLOSE_TAG;
                            } else if (TokenParser.boundsCheck(string, n3, 2) && string.charAt(n - 1) == '/') {
                                tokenType = TokenType.OPEN_CLOSE_TAG;
                            }
                            matchedTokenConsumer.accept(n3, n2, tokenType);
                            firstPassState = FirstPassState.NORMAL;
                            break;
                        }
                        case 60: {
                            n3 = n;
                            break;
                        }
                        case 34: 
                        case 39: {
                            n4 = (char)n7;
                            if (string.indexOf(n7, n + 1) == -1) break;
                            firstPassState = FirstPassState.STRING;
                        }
                    }
                    break;
                }
                case STRING: {
                    if (n7 != n4) break;
                    firstPassState = FirstPassState.TAG;
                }
            }
            if (n != n5 - 1 || firstPassState != FirstPassState.TAG) continue;
            n = n3;
            firstPassState = FirstPassState.NORMAL;
        }
        n = matchedTokenConsumer.lastEndIndex();
        if (n == -1) {
            matchedTokenConsumer.accept(0, string.length(), TokenType.TEXT);
        } else if (n != string.length()) {
            matchedTokenConsumer.accept(n, string.length(), TokenType.TEXT);
        }
    }

    private static void parseSecondPass(String string, List<Token> list) {
        for (Token token : list) {
            int n;
            TokenType tokenType = token.type();
            if (tokenType != TokenType.OPEN_TAG && tokenType != TokenType.OPEN_CLOSE_TAG && tokenType != TokenType.CLOSE_TAG) continue;
            int n2 = tokenType == TokenType.CLOSE_TAG ? token.startIndex() + 2 : token.startIndex() + 1;
            int n3 = tokenType == TokenType.OPEN_CLOSE_TAG ? token.endIndex() - 2 : token.endIndex() - 1;
            SecondPassState secondPassState = SecondPassState.NORMAL;
            boolean bl = false;
            int n4 = 0;
            int n5 = n2;
            block9: for (n = n2; n < n3; ++n) {
                int n6 = string.codePointAt(n);
                if (!Character.isBmpCodePoint(n)) {
                    ++n;
                }
                if (!bl) {
                    if (n6 == 92 && n + 1 < string.length()) {
                        int n7 = string.codePointAt(n + 1);
                        switch (secondPassState) {
                            case NORMAL: {
                                bl = n7 == 60 || n7 == 92;
                                break;
                            }
                            case STRING: {
                                boolean bl2 = bl = n4 == n7 || n7 == 92;
                            }
                        }
                        if (bl) {
                            continue;
                        }
                    }
                } else {
                    bl = false;
                    continue;
                }
                switch (secondPassState) {
                    case NORMAL: {
                        if (n6 == 58) {
                            if (TokenParser.boundsCheck(string, n, 2) && string.charAt(n + 1) == '/' && string.charAt(n + 2) == '/') continue block9;
                            if (n5 == n) {
                                TokenParser.insert(token, new Token(n, n, TokenType.TAG_VALUE));
                                ++n5;
                                continue block9;
                            }
                            TokenParser.insert(token, new Token(n5, n, TokenType.TAG_VALUE));
                            n5 = n + 1;
                            continue block9;
                        }
                        if (n6 != 39 && n6 != 34) continue block9;
                        secondPassState = SecondPassState.STRING;
                        n4 = (char)n6;
                        continue block9;
                    }
                    case STRING: {
                        if (n6 != n4) continue block9;
                        secondPassState = SecondPassState.NORMAL;
                    }
                }
            }
            if (token.childTokens() == null || token.childTokens().isEmpty()) {
                TokenParser.insert(token, new Token(n2, n3, TokenType.TAG_VALUE));
                continue;
            }
            n = token.childTokens().get(token.childTokens().size() - 1).endIndex();
            if (n == n3) continue;
            TokenParser.insert(token, new Token(n + 1, n3, TokenType.TAG_VALUE));
        }
    }

    /*
     * WARNING - void declaration
     */
    private static RootNode buildTree(@NotNull TagProvider tagProvider, @NotNull Predicate<String> predicate, @NotNull List<Token> list, @NotNull String string, @NotNull String string2, boolean bl) {
        TagNode tagNode;
        Object object;
        Object object2;
        RootNode rootNode = new RootNode(string, string2);
        ElementNode elementNode = rootNode;
        for (Token tokenArray : list) {
            object2 = tokenArray.type();
            switch (1.$SwitchMap$net$kyori$adventure$text$minimessage$internal$parser$TokenType[((Enum)object2).ordinal()]) {
                case 1: {
                    elementNode.addChild(new TextNode(elementNode, tokenArray, string));
                    break;
                }
                case 2: 
                case 3: {
                    Object object3;
                    Token token = tokenArray.childTokens().get(0);
                    object = string.substring(token.startIndex(), token.endIndex());
                    if (!TagInternals.sanitizeAndCheckValidTagName((String)object)) {
                        elementNode.addChild(new TextNode(elementNode, tokenArray, string));
                        break;
                    }
                    tagNode = new TagNode(elementNode, tokenArray, string, tagProvider);
                    if (predicate.test(tagNode.name())) {
                        object3 = tagProvider.resolve(tagNode);
                        if (object3 == null) {
                            elementNode.addChild(new TextNode(elementNode, tokenArray, string));
                            break;
                        }
                        if (object3 == ParserDirective.RESET) {
                            if (bl) {
                                throw new ParsingExceptionImpl("<reset> tags are not allowed when strict mode is enabled", string, tokenArray);
                            }
                            elementNode = rootNode;
                            break;
                        }
                        tagNode.tag((Tag)object3);
                        elementNode.addChild(tagNode);
                        if (object2 == TokenType.OPEN_CLOSE_TAG || object3 instanceof Inserting && !((Inserting)object3).allowsChildren()) break;
                        elementNode = tagNode;
                        break;
                    }
                    elementNode.addChild(new TextNode(elementNode, tokenArray, string));
                    break;
                }
                case 4: {
                    Object object4;
                    Object object3 = tokenArray.childTokens();
                    if (object3.isEmpty()) {
                        throw new IllegalStateException("CLOSE_TAG token somehow has no children - the parser should not allow this. Original text: " + string);
                    }
                    ArrayList<String> arrayList = new ArrayList<String>(object3.size());
                    Object object5 = object3.iterator();
                    while (object5.hasNext()) {
                        object4 = object5.next();
                        arrayList.add(TagPart.unquoteAndEscape(string, ((Token)object4).startIndex(), ((Token)object4).endIndex()));
                    }
                    object5 = (String)arrayList.get(0);
                    if (predicate.test((String)object5)) {
                        object4 = tagProvider.resolve((String)object5);
                        if (object4 == ParserDirective.RESET) {
                            break;
                        }
                    } else {
                        elementNode.addChild(new TextNode(elementNode, tokenArray, string));
                        break;
                    }
                    object4 = elementNode;
                    while (object4 instanceof TagNode) {
                        List<TagPart> list2 = ((TagNode)object4).parts();
                        if (TokenParser.tagCloses(arrayList, list2)) {
                            Object object6;
                            if (object4 != elementNode && bl) {
                                object6 = "Unclosed tag encountered; " + ((TagNode)elementNode).name() + " is not closed, because " + arrayList.get(0) + " was closed first.";
                                throw new ParsingExceptionImpl((String)object6, string, ((ElementNode)object4).token(), elementNode.token(), tokenArray);
                            }
                            object6 = ((ElementNode)object4).parent();
                            if (object6 != null) {
                                elementNode = object6;
                                break;
                            }
                            throw new IllegalStateException("Root node matched with close tag value, this should not be possible. Original text: " + string);
                        }
                        object4 = ((ElementNode)object4).parent();
                    }
                    if (object4 != null && !(object4 instanceof RootNode)) break;
                    elementNode.addChild(new TextNode(elementNode, tokenArray, string));
                    break;
                }
            }
        }
        if (bl && rootNode != elementNode) {
            void var9_11;
            ArrayList arrayList = new ArrayList();
            RootNode rootNode2 = elementNode;
            while (var9_11 != null && var9_11 instanceof TagNode) {
                arrayList.add((TagNode)var9_11);
                ElementNode elementNode2 = var9_11.parent();
            }
            Token[] tokenArray = new Token[arrayList.size()];
            object2 = new StringBuilder("All tags must be explicitly closed while in strict mode. End of string found with open tags: ");
            int n = 0;
            object = arrayList.listIterator(arrayList.size());
            while (object.hasPrevious()) {
                tagNode = (TagNode)object.previous();
                tokenArray[n++] = tagNode.token();
                ((StringBuilder)object2).append(tagNode.name());
                if (!object.hasPrevious()) continue;
                ((StringBuilder)object2).append(", ");
            }
            throw new ParsingExceptionImpl(((StringBuilder)object2).toString(), string, tokenArray);
        }
        return rootNode;
    }

    private static boolean tagCloses(List<String> list, List<TagPart> list2) {
        if (list.size() > list2.size()) {
            return false;
        }
        if (!list.get(0).equalsIgnoreCase(list2.get(0).value())) {
            return false;
        }
        for (int i = 1; i < list.size(); ++i) {
            if (list.get(i).equals(list2.get(i).value())) continue;
            return false;
        }
        return true;
    }

    private static boolean boundsCheck(String string, int n, int n2) {
        return n + n2 < string.length();
    }

    private static void insert(Token token, Token token2) {
        if (token.childTokens() == null) {
            token.childTokens(Collections.singletonList(token2));
            return;
        }
        if (token.childTokens().size() == 1) {
            ArrayList<Token> arrayList = new ArrayList<Token>(3);
            arrayList.add(token.childTokens().get(0));
            arrayList.add(token2);
            token.childTokens(arrayList);
        } else {
            token.childTokens().add(token2);
        }
    }

    public static String unescape(String string, int n, int n2, IntPredicate intPredicate) {
        int n3 = n;
        int n4 = string.indexOf(92, n3);
        if (n4 == -1 || n4 >= n2) {
            return string.substring(n3, n2);
        }
        StringBuilder stringBuilder = new StringBuilder(n2 - n);
        while (n4 != -1 && n4 + 1 < n2) {
            if (intPredicate.test(string.codePointAt(n4 + 1))) {
                stringBuilder.append(string, n3, n4);
                if (++n4 >= n2) {
                    n3 = n2;
                    break;
                }
                int n5 = string.codePointAt(n4);
                stringBuilder.appendCodePoint(n5);
                n4 = Character.isBmpCodePoint(n5) ? ++n4 : (n4 += 2);
                if (n4 >= n2) {
                    n3 = n2;
                    break;
                }
            } else {
                stringBuilder.append(string, n3, ++n4);
            }
            n3 = n4;
            n4 = string.indexOf(92, n3);
        }
        stringBuilder.append(string, n3, n2);
        return stringBuilder.toString();
    }

    @ApiStatus.Internal
    public static interface TagProvider {
        @Nullable
        public Tag resolve(@NotNull String var1, @NotNull List<? extends Tag.Argument> var2, @Nullable Token var3);

        @Nullable
        default public Tag resolve(@NotNull String name) {
            return this.resolve(name, Collections.emptyList(), null);
        }

        @Nullable
        default public Tag resolve(@NotNull TagNode node) {
            return this.resolve(TagProvider.sanitizePlaceholderName(node.name()), node.parts().subList(1, node.parts().size()), node.token());
        }

        @NotNull
        public static String sanitizePlaceholderName(@NotNull String name) {
            return name.toLowerCase(Locale.ROOT);
        }
    }

    static enum SecondPassState {
        NORMAL,
        STRING;

    }

    static enum FirstPassState {
        NORMAL,
        TAG,
        STRING;

    }
}

