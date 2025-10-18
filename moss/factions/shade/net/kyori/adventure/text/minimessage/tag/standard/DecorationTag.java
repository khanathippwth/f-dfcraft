/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import moss.factions.shade.net.kyori.adventure.text.format.TextDecoration;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.StyleClaim;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class DecorationTag {
    private static final String B = "b";
    private static final String I = "i";
    private static final String EM = "em";
    private static final String OBF = "obf";
    private static final String ST = "st";
    private static final String U = "u";
    public static final String REVERT = "!";
    static final Map<TextDecoration, TagResolver> RESOLVERS = Stream.of(DecorationTag.resolvers(TextDecoration.OBFUSCATED, "obf", new String[0]), DecorationTag.resolvers(TextDecoration.BOLD, "b", new String[0]), DecorationTag.resolvers(TextDecoration.STRIKETHROUGH, "st", new String[0]), DecorationTag.resolvers(TextDecoration.UNDERLINED, "u", new String[0]), DecorationTag.resolvers(TextDecoration.ITALIC, "em", "i")).collect(Collectors.toMap(Map.Entry::getKey, entry -> ((Stream)entry.getValue()).collect(TagResolver.toTagResolver()), (tagResolver, tagResolver2) -> TagResolver.builder().resolver((TagResolver)tagResolver).resolver((TagResolver)tagResolver2).build(), LinkedHashMap::new));
    static final TagResolver RESOLVER = TagResolver.resolver(RESOLVERS.values());

    static Map.Entry<TextDecoration, Stream<TagResolver>> resolvers(TextDecoration textDecoration, @Nullable String string2, @NotNull @NotNull String @NotNull ... stringArray) {
        String string3 = TextDecoration.NAMES.key(textDecoration);
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.add(string3);
        if (string2 != null) {
            hashSet.add(string2);
        }
        Collections.addAll(hashSet, stringArray);
        return new AbstractMap.SimpleImmutableEntry<TextDecoration, Stream<TagResolver>>(textDecoration, Stream.concat(Stream.of(SerializableResolver.claimingStyle(hashSet, (argumentQueue, context) -> DecorationTag.create(textDecoration, argumentQueue, context), DecorationTag.claim(textDecoration, (state, tokenEmitter) -> DecorationTag.emit(string3, string2 == null ? string3 : string2, state, tokenEmitter)))), hashSet.stream().map(string -> TagResolver.resolver(REVERT + string, DecorationTag.createNegated(textDecoration)))));
    }

    private DecorationTag() {
    }

    static Tag create(TextDecoration textDecoration, ArgumentQueue argumentQueue, Context context) {
        boolean bl = !argumentQueue.hasNext() || !argumentQueue.pop().isFalse();
        return Tag.styling(textDecoration.withState(bl));
    }

    static Tag createNegated(TextDecoration textDecoration) {
        return Tag.styling(textDecoration.withState(false));
    }

    @NotNull
    static StyleClaim<TextDecoration.State> claim(@NotNull TextDecoration textDecoration, @NotNull BiConsumer<TextDecoration.State, TokenEmitter> biConsumer) {
        Objects.requireNonNull(textDecoration, "decoration");
        return StyleClaim.claim("decoration_" + TextDecoration.NAMES.key(textDecoration), style -> style.decoration(textDecoration), state -> state != TextDecoration.State.NOT_SET, biConsumer);
    }

    static void emit(@NotNull String string, @NotNull String string2, @NotNull TextDecoration.State state, @NotNull TokenEmitter tokenEmitter) {
        if (state == TextDecoration.State.FALSE) {
            tokenEmitter.tag(REVERT + string);
        } else {
            tokenEmitter.tag(string);
        }
    }
}

