/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard;

import moss.factions.shade.net.kyori.adventure.text.format.Style;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.StyleClaim;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

final class InsertionTag {
    private static final String INSERTION = "insert";
    static final TagResolver RESOLVER = SerializableResolver.claimingStyle("insert", InsertionTag::create, StyleClaim.claim("insert", Style::insertion, InsertionTag::emit));

    private InsertionTag() {
    }

    static Tag create(ArgumentQueue argumentQueue, Context context) {
        String string = argumentQueue.popOr("A value is required to produce an insertion component").value();
        return Tag.styling(builder -> builder.insertion(string));
    }

    static void emit(String string, TokenEmitter tokenEmitter) {
        tokenEmitter.tag(INSERTION).argument(string);
    }
}

