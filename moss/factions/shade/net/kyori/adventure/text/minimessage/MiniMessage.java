/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import moss.factions.shade.net.kyori.adventure.builder.AbstractBuilder;
import moss.factions.shade.net.kyori.adventure.pointer.Pointered;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.minimessage.MiniMessageImpl;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import moss.factions.shade.net.kyori.adventure.text.serializer.ComponentSerializer;
import moss.factions.shade.net.kyori.adventure.util.PlatformAPI;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MiniMessage
extends ComponentSerializer<Component, Component, String> {
    @NotNull
    public static MiniMessage miniMessage() {
        return MiniMessageImpl.Instances.INSTANCE;
    }

    @NotNull
    public String escapeTags(@NotNull String var1);

    @NotNull
    public String escapeTags(@NotNull String var1, @NotNull TagResolver var2);

    @NotNull
    default public String escapeTags(@NotNull String input, @NotNull TagResolver ... tagResolvers) {
        return this.escapeTags(input, TagResolver.resolver(tagResolvers));
    }

    @NotNull
    public String stripTags(@NotNull String var1);

    @NotNull
    public String stripTags(@NotNull String var1, @NotNull TagResolver var2);

    @NotNull
    default public String stripTags(@NotNull String input, @NotNull TagResolver ... tagResolvers) {
        return this.stripTags(input, TagResolver.resolver(tagResolvers));
    }

    @NotNull
    public Component deserialize(@NotNull String var1, @NotNull Pointered var2);

    @NotNull
    public Component deserialize(@NotNull String var1, @NotNull TagResolver var2);

    @NotNull
    public Component deserialize(@NotNull String var1, @NotNull Pointered var2, @NotNull TagResolver var3);

    @NotNull
    default public Component deserialize(@NotNull String input, @NotNull TagResolver ... tagResolvers) {
        return this.deserialize(input, TagResolver.resolver(tagResolvers));
    }

    @NotNull
    default public Component deserialize(@NotNull String input, @NotNull Pointered target, @NotNull TagResolver ... tagResolvers) {
        return this.deserialize(input, target, TagResolver.resolver(tagResolvers));
    }

    public  @NotNull Node.Root deserializeToTree(@NotNull String var1);

    public  @NotNull Node.Root deserializeToTree(@NotNull String var1, @NotNull Pointered var2);

    public  @NotNull Node.Root deserializeToTree(@NotNull String var1, @NotNull TagResolver var2);

    public  @NotNull Node.Root deserializeToTree(@NotNull String var1, @NotNull Pointered var2, @NotNull TagResolver var3);

    default public  @NotNull Node.Root deserializeToTree(@NotNull String input, @NotNull TagResolver ... tagResolvers) {
        return this.deserializeToTree(input, TagResolver.resolver(tagResolvers));
    }

    default public  @NotNull Node.Root deserializeToTree(@NotNull String input, @NotNull Pointered target, @NotNull TagResolver ... tagResolvers) {
        return this.deserializeToTree(input, target, TagResolver.resolver(tagResolvers));
    }

    public boolean strict();

    @NotNull
    public TagResolver tags();

    public static Builder builder() {
        return new MiniMessageImpl.BuilderImpl();
    }

    @PlatformAPI
    @ApiStatus.Internal
    public static interface Provider {
        @PlatformAPI
        @ApiStatus.Internal
        @NotNull
        public MiniMessage miniMessage();

        @PlatformAPI
        @ApiStatus.Internal
        @NotNull
        public Consumer<Builder> builder();
    }

    public static interface Builder
    extends AbstractBuilder<MiniMessage> {
        @NotNull
        public Builder tags(@NotNull TagResolver var1);

        @NotNull
        public Builder editTags(@NotNull Consumer<TagResolver.Builder> var1);

        @NotNull
        public Builder strict(boolean var1);

        @NotNull
        public Builder debug(@Nullable Consumer<String> var1);

        @NotNull
        public Builder postProcessor(@NotNull UnaryOperator<Component> var1);

        @NotNull
        public Builder preProcessor(@NotNull UnaryOperator<String> var1);

        @Override
        @NotNull
        public MiniMessage build();
    }
}

