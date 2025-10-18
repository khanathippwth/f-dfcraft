/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.serializer.craftbukkit;

import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

@Deprecated
public final class MinecraftComponentSerializer
implements ComponentSerializer<Component, Component, Object> {
    private static final MinecraftComponentSerializer INSTANCE = new MinecraftComponentSerializer();
    private final moss.factions.shade.net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer realSerial = moss.factions.shade.net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer.get();

    public static boolean isSupported() {
        return moss.factions.shade.net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer.isSupported();
    }

    @NotNull
    public static MinecraftComponentSerializer get() {
        return INSTANCE;
    }

    @Override
    @NotNull
    public Component deserialize(@NotNull Object object) {
        return this.realSerial.deserialize(object);
    }

    @Override
    @NotNull
    public Object serialize(@NotNull Component component) {
        return this.realSerial.serialize(component);
    }
}

