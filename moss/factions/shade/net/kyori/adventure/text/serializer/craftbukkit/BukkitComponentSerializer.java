/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.serializer.craftbukkit;

import moss.factions.shade.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import moss.factions.shade.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

@Deprecated
public final class BukkitComponentSerializer {
    private BukkitComponentSerializer() {
    }

    @NotNull
    public static LegacyComponentSerializer legacy() {
        return moss.factions.shade.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer.legacy();
    }

    @NotNull
    public static GsonComponentSerializer gson() {
        return moss.factions.shade.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer.gson();
    }
}

