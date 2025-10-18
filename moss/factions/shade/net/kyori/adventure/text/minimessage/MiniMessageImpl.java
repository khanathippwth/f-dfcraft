/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import moss.factions.shade.net.kyori.adventure.pointer.Pointered;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.minimessage.ContextImpl;
import moss.factions.shade.net.kyori.adventure.text.minimessage.MiniMessage;
import moss.factions.shade.net.kyori.adventure.text.minimessage.MiniMessageParser;
import moss.factions.shade.net.kyori.adventure.text.minimessage.MiniMessageSerializer;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import moss.factions.shade.net.kyori.adventure.util.Services;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class MiniMessageImpl
implements MiniMessage {
    private static final Optional<MiniMessage.Provider> SERVICE = Services.service(MiniMessage.Provider.class);
    static final Consumer<MiniMessage.Builder> BUILDER = SERVICE.map(MiniMessage.Provider::builder).orElseGet(() -> builder -> {});
    static final UnaryOperator<String> DEFAULT_NO_OP = UnaryOperator.identity();
    static final UnaryOperator<Component> DEFAULT_COMPACTING_METHOD = Component::compact;
    private final boolean strict;
    @Nullable
    private final Consumer<String> debugOutput;
    private final UnaryOperator<Component> postProcessor;
    private final UnaryOperator<String> preProcessor;
    final MiniMessageParser parser;

    MiniMessageImpl(@NotNull TagResolver tagResolver, boolean bl, @Nullable Consumer<String> consumer, @NotNull UnaryOperator<String> unaryOperator, @NotNull UnaryOperator<Component> unaryOperator2) {
        this.parser = new MiniMessageParser(tagResolver);
        this.strict = bl;
        this.debugOutput = consumer;
        this.preProcessor = unaryOperator;
        this.postProcessor = unaryOperator2;
    }

    @Override
    @NotNull
    public Component deserialize(@NotNull String string) {
        return this.parser.parseFormat(this.newContext(string, null, null));
    }

    @Override
    @NotNull
    public Component deserialize(@NotNull String string, @NotNull Pointered pointered) {
        return this.parser.parseFormat(this.newContext(string, Objects.requireNonNull(pointered, "target"), null));
    }

    @Override
    @NotNull
    public Component deserialize(@NotNull String string, @NotNull TagResolver tagResolver) {
        return this.parser.parseFormat(this.newContext(string, null, Objects.requireNonNull(tagResolver, "tagResolver")));
    }

    @Override
    @NotNull
    public Component deserialize(@NotNull String string, @NotNull Pointered pointered, @NotNull TagResolver tagResolver) {
        return this.parser.parseFormat(this.newContext(string, Objects.requireNonNull(pointered, "target"), Objects.requireNonNull(tagResolver, "tagResolver")));
    }

    @Override
    public  @NotNull Node.Root deserializeToTree(@NotNull String string) {
        return this.parser.parseToTree(this.newContext(string, null, null));
    }

    @Override
    public  @NotNull Node.Root deserializeToTree(@NotNull String string, @NotNull Pointered pointered) {
        return this.parser.parseToTree(this.newContext(string, Objects.requireNonNull(pointered, "target"), null));
    }

    @Override
    public  @NotNull Node.Root deserializeToTree(@NotNull String string, @NotNull TagResolver tagResolver) {
        return this.parser.parseToTree(this.newContext(string, null, Objects.requireNonNull(tagResolver, "tagResolver")));
    }

    @Override
    public  @NotNull Node.Root deserializeToTree(@NotNull String string, @NotNull Pointered pointered, @NotNull TagResolver tagResolver) {
        return this.parser.parseToTree(this.newContext(string, Objects.requireNonNull(pointered, "target"), Objects.requireNonNull(tagResolver, "tagResolver")));
    }

    @Override
    @NotNull
    public String serialize(@NotNull Component component) {
        return MiniMessageSerializer.serialize(component, this.serialResolver(null), this.strict);
    }

    private SerializableResolver serialResolver(@Nullable TagResolver tagResolver) {
        if (tagResolver == null) {
            if (this.parser.tagResolver instanceof SerializableResolver) {
                return (SerializableResolver)((Object)this.parser.tagResolver);
            }
        } else {
            TagResolver tagResolver2 = TagResolver.resolver(this.parser.tagResolver, tagResolver);
            if (tagResolver2 instanceof SerializableResolver) {
                return (SerializableResolver)((Object)tagResolver2);
            }
        }
        return (SerializableResolver)((Object)TagResolver.empty());
    }

    @Override
    @NotNull
    public String escapeTags(@NotNull String string) {
        return this.parser.escapeTokens(this.newContext(string, null, null));
    }

    @Override
    @NotNull
    public String escapeTags(@NotNull String string, @NotNull TagResolver tagResolver) {
        return this.parser.escapeTokens(this.newContext(string, null, tagResolver));
    }

    @Override
    @NotNull
    public String stripTags(@NotNull String string) {
        return this.parser.stripTokens(this.newContext(string, null, null));
    }

    @Override
    @NotNull
    public String stripTags(@NotNull String string, @NotNull TagResolver tagResolver) {
        return this.parser.stripTokens(this.newContext(string, null, tagResolver));
    }

    @Override
    public boolean strict() {
        return this.strict;
    }

    @Override
    @NotNull
    public TagResolver tags() {
        return this.parser.tagResolver;
    }

    @NotNull
    private ContextImpl newContext(@NotNull String string, @Nullable Pointered pointered, @Nullable TagResolver tagResolver) {
        Objects.requireNonNull(string, "input");
        return new ContextImpl(this.strict, this.debugOutput, string, this, pointered, tagResolver, this.preProcessor, this.postProcessor);
    }

    static /* synthetic */ Optional access$000() {
        return SERVICE;
    }

    static final class BuilderImpl
    implements MiniMessage.Builder {
        private TagResolver tagResolver = TagResolver.standard();
        private boolean strict = false;
        private Consumer<String> debug = null;
        private UnaryOperator<Component> postProcessor = DEFAULT_COMPACTING_METHOD;
        private UnaryOperator<String> preProcessor = DEFAULT_NO_OP;

        BuilderImpl() {
            BUILDER.accept(this);
        }

        BuilderImpl(MiniMessageImpl miniMessageImpl) {
            this();
            this.tagResolver = miniMessageImpl.parser.tagResolver;
            this.strict = miniMessageImpl.strict;
            this.debug = miniMessageImpl.debugOutput;
            this.postProcessor = miniMessageImpl.postProcessor;
            this.preProcessor = miniMessageImpl.preProcessor;
        }

        @Override
        @NotNull
        public MiniMessage.Builder tags(@NotNull TagResolver tagResolver) {
            this.tagResolver = Objects.requireNonNull(tagResolver, "tags");
            return this;
        }

        @Override
        @NotNull
        public MiniMessage.Builder editTags(@NotNull Consumer<TagResolver.Builder> consumer) {
            Objects.requireNonNull(consumer, "adder");
            TagResolver.Builder builder = TagResolver.builder().resolver(this.tagResolver);
            consumer.accept(builder);
            this.tagResolver = builder.build();
            return this;
        }

        @Override
        @NotNull
        public MiniMessage.Builder strict(boolean bl) {
            this.strict = bl;
            return this;
        }

        @Override
        @NotNull
        public MiniMessage.Builder debug(@Nullable Consumer<String> consumer) {
            this.debug = consumer;
            return this;
        }

        @Override
        @NotNull
        public MiniMessage.Builder postProcessor(@NotNull UnaryOperator<Component> unaryOperator) {
            this.postProcessor = Objects.requireNonNull(unaryOperator, "postProcessor");
            return this;
        }

        @Override
        @NotNull
        public MiniMessage.Builder preProcessor(@NotNull UnaryOperator<String> unaryOperator) {
            this.preProcessor = Objects.requireNonNull(unaryOperator, "preProcessor");
            return this;
        }

        @Override
        @NotNull
        public MiniMessage build() {
            return new MiniMessageImpl(this.tagResolver, this.strict, this.debug, this.preProcessor, this.postProcessor);
        }
    }

    static final class Instances {
        static final MiniMessage INSTANCE = MiniMessageImpl.access$000().map(MiniMessage.Provider::miniMessage).orElseGet(() -> new MiniMessageImpl(TagResolver.standard(), false, null, DEFAULT_NO_OP, DEFAULT_COMPACTING_METHOD));

        Instances() {
        }
    }
}

