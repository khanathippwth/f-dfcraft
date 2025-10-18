/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.ComponentLike;
import moss.factions.shade.net.kyori.adventure.text.minimessage.ArgumentQueueImpl;
import moss.factions.shade.net.kyori.adventure.text.minimessage.ContextImpl;
import moss.factions.shade.net.kyori.adventure.text.minimessage.ParsingException;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.ParsingExceptionImpl;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.Token;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.TokenParser;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.TokenType;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.ElementNode;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.RootNode;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.TagNode;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.ValueNode;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Inserting;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Modifying;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import moss.factions.shade.net.kyori.examination.Examinable;
import moss.factions.shade.net.kyori.examination.string.MultiLineStringExaminer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class MiniMessageParser {
    final TagResolver tagResolver;

    MiniMessageParser() {
        this.tagResolver = TagResolver.standard();
    }

    MiniMessageParser(TagResolver tagResolver) {
        this.tagResolver = tagResolver;
    }

    @NotNull
    String escapeTokens(@NotNull ContextImpl contextImpl) {
        StringBuilder stringBuilder = new StringBuilder(contextImpl.message().length());
        this.escapeTokens(stringBuilder, contextImpl);
        return stringBuilder.toString();
    }

    void escapeTokens(StringBuilder stringBuilder, @NotNull ContextImpl contextImpl) {
        this.escapeTokens(stringBuilder, contextImpl.message(), contextImpl);
    }

    private void escapeTokens(StringBuilder stringBuilder2, String string, ContextImpl contextImpl) {
        this.processTokens(stringBuilder2, string, contextImpl, (token, stringBuilder) -> {
            stringBuilder.append('\\').append('<');
            if (token.type() == TokenType.CLOSE_TAG) {
                stringBuilder.append('/');
            }
            List<Token> list = token.childTokens();
            for (int i = 0; i < list.size(); ++i) {
                if (i != 0) {
                    stringBuilder.append(':');
                }
                this.escapeTokens((StringBuilder)stringBuilder, list.get(i).get(string).toString(), contextImpl);
            }
            stringBuilder.append('>');
        });
    }

    @NotNull
    String stripTokens(@NotNull ContextImpl contextImpl) {
        StringBuilder stringBuilder2 = new StringBuilder(contextImpl.message().length());
        this.processTokens(stringBuilder2, contextImpl, (token, stringBuilder) -> {});
        return stringBuilder2.toString();
    }

    private void processTokens(@NotNull StringBuilder stringBuilder, @NotNull ContextImpl contextImpl, BiConsumer<Token, StringBuilder> biConsumer) {
        this.processTokens(stringBuilder, contextImpl.message(), contextImpl, biConsumer);
    }

    private void processTokens(@NotNull StringBuilder stringBuilder, @NotNull String string, @NotNull ContextImpl contextImpl, BiConsumer<Token, StringBuilder> biConsumer) {
        TagResolver tagResolver = TagResolver.resolver(this.tagResolver, contextImpl.extraTags());
        List<Token> list = TokenParser.tokenize(string, true);
        block4: for (Token token : list) {
            switch (token.type()) {
                case TEXT: {
                    stringBuilder.append(string, token.startIndex(), token.endIndex());
                    continue block4;
                }
                case OPEN_TAG: 
                case CLOSE_TAG: 
                case OPEN_CLOSE_TAG: {
                    if (token.childTokens().isEmpty()) {
                        stringBuilder.append(string, token.startIndex(), token.endIndex());
                        continue block4;
                    }
                    String string2 = TokenParser.TagProvider.sanitizePlaceholderName(token.childTokens().get(0).get(string).toString());
                    if (tagResolver.has(string2)) {
                        biConsumer.accept(token, stringBuilder);
                        continue block4;
                    }
                    stringBuilder.append(string, token.startIndex(), token.endIndex());
                    continue block4;
                }
            }
            throw new IllegalArgumentException("Unsupported token type " + (Object)((Object)token.type()));
        }
    }

    @NotNull
    RootNode parseToTree(@NotNull ContextImpl contextImpl) {
        TagResolver tagResolver = TagResolver.resolver(this.tagResolver, contextImpl.extraTags());
        String string2 = (String)contextImpl.preProcessor().apply(contextImpl.message());
        Consumer<String> consumer = contextImpl.debugOutput();
        if (consumer != null) {
            consumer.accept("Beginning parsing message ");
            consumer.accept(string2);
            consumer.accept("\n");
        }
        TokenParser.TagProvider tagProvider = consumer != null ? (string, list, token) -> {
            try {
                consumer.accept("Attempting to match node '");
                consumer.accept(string);
                consumer.accept("'");
                if (token != null) {
                    consumer.accept(" at column ");
                    consumer.accept(String.valueOf(token.startIndex()));
                }
                consumer.accept("\n");
                @Nullable Tag tag = tagResolver.resolve(string, new ArgumentQueueImpl(contextImpl, list), contextImpl);
                if (tag == null) {
                    consumer.accept("Could not match node '");
                    consumer.accept(string);
                    consumer.accept("'\n");
                } else {
                    consumer.accept("Successfully matched node '");
                    consumer.accept(string);
                    consumer.accept("' to tag ");
                    consumer.accept(tag instanceof Examinable ? ((Examinable)((Object)tag)).examinableName() : tag.getClass().getName());
                    consumer.accept("\n");
                }
                return tag;
            } catch (ParsingException parsingException) {
                ParsingExceptionImpl parsingExceptionImpl;
                if (token != null && parsingException instanceof ParsingExceptionImpl && (parsingExceptionImpl = (ParsingExceptionImpl)parsingException).tokens().length == 0) {
                    parsingExceptionImpl.tokens(new Token[]{token});
                }
                consumer.accept("Could not match node '");
                consumer.accept(string);
                consumer.accept("' - ");
                consumer.accept(parsingException.getMessage());
                consumer.accept("\n");
                return null;
            }
        } : (string, list, token) -> {
            try {
                return tagResolver.resolve(string, new ArgumentQueueImpl(contextImpl, list), contextImpl);
            } catch (ParsingException parsingException) {
                return null;
            }
        };
        Predicate<String> predicate = string -> {
            String string2 = TokenParser.TagProvider.sanitizePlaceholderName(string);
            return tagResolver.has(string2);
        };
        String string3 = TokenParser.resolvePreProcessTags(string2, tagProvider);
        contextImpl.message(string3);
        RootNode rootNode = TokenParser.parse(tagProvider, predicate, string3, string2, contextImpl.strict());
        if (consumer != null) {
            consumer.accept("Text parsed into element tree:\n");
            consumer.accept(rootNode.toString());
        }
        return rootNode;
    }

    @NotNull
    Component parseFormat(@NotNull ContextImpl contextImpl) {
        RootNode rootNode = this.parseToTree(contextImpl);
        return Objects.requireNonNull((Component)contextImpl.postProcessor().apply(this.treeToComponent(rootNode, contextImpl)), "Post-processor must not return null");
    }

    @NotNull
    Component treeToComponent(@NotNull ElementNode elementNode, @NotNull ContextImpl contextImpl) {
        Object object;
        Component component = Component.empty();
        Tag tag = null;
        if (elementNode instanceof ValueNode) {
            component = Component.text(((ValueNode)elementNode).value());
        } else if (elementNode instanceof TagNode) {
            object = (TagNode)elementNode;
            tag = ((TagNode)object).tag();
            if (tag instanceof Modifying) {
                Modifying modifying = (Modifying)tag;
                this.visitModifying(modifying, (ElementNode)object, 0);
                modifying.postVisit();
            }
            if (tag instanceof Inserting) {
                component = ((Inserting)tag).value();
            }
        }
        if (!elementNode.unsafeChildren().isEmpty()) {
            object = new ArrayList<Component>(component.children().size() + elementNode.children().size());
            object.addAll(component.children());
            for (ElementNode elementNode2 : elementNode.unsafeChildren()) {
                object.add(this.treeToComponent(elementNode2, contextImpl));
            }
            component = component.children((List<? extends ComponentLike>)object);
        }
        if (tag instanceof Modifying) {
            component = this.handleModifying((Modifying)tag, component, 0);
        }
        if ((object = contextImpl.debugOutput()) != null) {
            object.accept((String)"==========\ntreeToComponent \n");
            object.accept((String)elementNode.toString());
            object.accept((String)"\n");
            object.accept((String)component.examine(MultiLineStringExaminer.simpleEscaping()).collect(Collectors.joining("\n")));
            object.accept((String)"\n==========\n");
        }
        return component;
    }

    private void visitModifying(Modifying modifying, ElementNode elementNode, int n) {
        modifying.visit(elementNode, n);
        for (ElementNode elementNode2 : elementNode.unsafeChildren()) {
            this.visitModifying(modifying, elementNode2, n + 1);
        }
    }

    private Component handleModifying(Modifying modifying, Component component, int n) {
        Component component2 = modifying.apply(component, n);
        for (Component component3 : component.children()) {
            component2 = component2.append(this.handleModifying(modifying, component3, n + 1));
        }
        return component2;
    }
}

