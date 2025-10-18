/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.util;

import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.ComponentLike;
import moss.factions.shade.net.kyori.adventure.text.minimessage.MiniMessage;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import moss.factions.shade.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Mini {
    public static Component parse(String string) {
        return Mini.miniMessage().deserialize(string);
    }

    public static Component parse(String string, TagResolver ... tagResolverArray) {
        return Mini.miniMessage().deserialize(string, tagResolverArray);
    }

    public static Component parse(String string, Iterable<TagResolver> iterable) {
        return Mini.miniMessage().deserialize(string, TagResolver.resolver(iterable));
    }

    public static MiniMessage miniMessage() {
        return MiniMessage.miniMessage();
    }

    public static String legacy(ComponentLike componentLike) {
        return LegacyComponentSerializer.legacySection().serialize(componentLike.asComponent());
    }
}

