/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard;

import moss.factions.shade.net.kyori.adventure.key.InvalidKeyException;
import moss.factions.shade.net.kyori.adventure.key.Key;
import moss.factions.shade.net.kyori.adventure.text.format.Style;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.StyleClaim;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

final class FontTag {
    static final String FONT = "font";
    static final TagResolver RESOLVER = SerializableResolver.claimingStyle("font", FontTag::create, StyleClaim.claim("font", Style::font, FontTag::emit));

    private FontTag() {
    }

    static Tag create(ArgumentQueue argumentQueue, Context context) {
        Key key;
        String string = argumentQueue.popOr("A font tag must have either arguments of either <value> or <namespace:value>").value();
        try {
            if (!argumentQueue.hasNext()) {
                key = Key.key(string);
            } else {
                String string2 = argumentQueue.pop().value();
                key = Key.key(string, string2);
            }
        } catch (InvalidKeyException invalidKeyException) {
            throw context.newException(invalidKeyException.getMessage(), argumentQueue);
        }
        return Tag.styling(builder -> builder.font(key));
    }

    static void emit(Key key, TokenEmitter tokenEmitter) {
        tokenEmitter.tag(FONT);
        if (key.namespace().equals("minecraft")) {
            tokenEmitter.argument(key.value());
        } else {
            tokenEmitter.arguments(key.namespace(), key.value());
        }
    }
}

