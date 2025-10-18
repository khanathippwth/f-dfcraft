/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import moss.factions.shade.net.kyori.adventure.pointer.Pointered;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.minimessage.ArgumentQueueImpl;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.MiniMessage;
import moss.factions.shade.net.kyori.adventure.text.minimessage.ParsingException;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.ParsingExceptionImpl;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.Token;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.TagPart;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ContextImpl
implements Context {
    private static final Token[] EMPTY_TOKEN_ARRAY = new Token[0];
    private final boolean strict;
    private final Consumer<String> debugOutput;
    private String message;
    private final MiniMessage miniMessage;
    @Nullable
    private final Pointered target;
    private final TagResolver tagResolver;
    private final UnaryOperator<String> preProcessor;
    private final UnaryOperator<Component> postProcessor;

    ContextImpl(boolean bl, Consumer<String> consumer, String string, MiniMessage miniMessage, @Nullable Pointered pointered, @Nullable TagResolver tagResolver, @Nullable UnaryOperator<String> unaryOperator, @Nullable UnaryOperator<Component> unaryOperator2) {
        this.strict = bl;
        this.debugOutput = consumer;
        this.message = string;
        this.miniMessage = miniMessage;
        this.target = pointered;
        this.tagResolver = tagResolver == null ? TagResolver.empty() : tagResolver;
        this.preProcessor = unaryOperator == null ? UnaryOperator.identity() : unaryOperator;
        this.postProcessor = unaryOperator2 == null ? UnaryOperator.identity() : unaryOperator2;
    }

    public boolean strict() {
        return this.strict;
    }

    public Consumer<String> debugOutput() {
        return this.debugOutput;
    }

    @NotNull
    public String message() {
        return this.message;
    }

    void message(@NotNull String string) {
        this.message = string;
    }

    @NotNull
    public TagResolver extraTags() {
        return this.tagResolver;
    }

    public UnaryOperator<Component> postProcessor() {
        return this.postProcessor;
    }

    public UnaryOperator<String> preProcessor() {
        return this.preProcessor;
    }

    @Override
    @Nullable
    public Pointered target() {
        return this.target;
    }

    @Override
    @NotNull
    public Pointered targetOrThrow() {
        if (this.target == null) {
            throw this.newException("A target is required for this deserialization attempt");
        }
        return this.target;
    }

    @Override
    @NotNull
    public <T extends Pointered> T targetAsType(@NotNull Class<T> clazz) {
        if (Objects.requireNonNull(clazz, "targetClass").isInstance(this.target)) {
            return (T)((Pointered)clazz.cast(this.target));
        }
        throw this.newException("A target with type " + clazz.getSimpleName() + " is required for this deserialization attempt");
    }

    @Override
    @NotNull
    public Component deserialize(@NotNull String string) {
        return this.miniMessage.deserialize(Objects.requireNonNull(string, "message"), this.tagResolver);
    }

    @Override
    @NotNull
    public Component deserialize(@NotNull String string, @NotNull TagResolver tagResolver) {
        return this.miniMessage.deserialize(Objects.requireNonNull(string, "message"), TagResolver.builder().resolver(this.tagResolver).resolver(Objects.requireNonNull(tagResolver, "resolver")).build());
    }

    @Override
    @NotNull
    public Component deserialize(@NotNull String string, @NotNull @NotNull TagResolver @NotNull ... tagResolverArray) {
        return this.miniMessage.deserialize(Objects.requireNonNull(string, "message"), TagResolver.builder().resolver(this.tagResolver).resolvers(Objects.requireNonNull(tagResolverArray, "resolvers")).build());
    }

    @Override
    @NotNull
    public ParsingException newException(@NotNull String string) {
        return new ParsingExceptionImpl(string, this.message, null, false, EMPTY_TOKEN_ARRAY);
    }

    @Override
    @NotNull
    public ParsingException newException(@NotNull String string, @NotNull ArgumentQueue argumentQueue) {
        return new ParsingExceptionImpl(string, this.message, null, false, ContextImpl.tagsToTokens(((ArgumentQueueImpl)argumentQueue).args));
    }

    @Override
    @NotNull
    public ParsingException newException(@NotNull String string, @Nullable Throwable throwable, @NotNull ArgumentQueue argumentQueue) {
        return new ParsingExceptionImpl(string, this.message, throwable, false, ContextImpl.tagsToTokens(((ArgumentQueueImpl)argumentQueue).args));
    }

    private static Token[] tagsToTokens(List<? extends Tag.Argument> list) {
        Token[] tokenArray = new Token[list.size()];
        int n = tokenArray.length;
        for (int i = 0; i < n; ++i) {
            tokenArray[i] = ((TagPart)list.get(i)).token();
        }
        return tokenArray;
    }
}

