/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard;

import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.Emitable;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.Nullable;

final class NewlineTag {
    private static final String BR = "br";
    private static final String NEWLINE = "newline";
    static final TagResolver RESOLVER = SerializableResolver.claimingComponent(StandardTags.names("newline", "br"), NewlineTag::create, NewlineTag::claimComponent);

    private NewlineTag() {
    }

    static Tag create(ArgumentQueue argumentQueue, Context context) {
        return Tag.selfClosingInserting(Component.newline());
    }

    @Nullable
    static Emitable claimComponent(Component component) {
        if (Component.newline().equals(component)) {
            return tokenEmitter -> tokenEmitter.selfClosingTag(BR);
        }
        return null;
    }
}

