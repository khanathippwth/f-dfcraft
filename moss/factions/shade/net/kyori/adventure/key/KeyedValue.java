/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$ScheduledForRemoval
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.key;

import java.util.Objects;
import moss.factions.shade.net.kyori.adventure.key.Key;
import moss.factions.shade.net.kyori.adventure.key.Keyed;
import moss.factions.shade.net.kyori.adventure.key.KeyedValueImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface KeyedValue<T>
extends Keyed {
    @NotNull
    public static <T> KeyedValue<T> keyedValue(@NotNull Key key, @NotNull T value) {
        return new KeyedValueImpl<T>(key, Objects.requireNonNull(value, "value"));
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion="5.0.0")
    @NotNull
    public static <T> KeyedValue<T> of(@NotNull Key key, @NotNull T value) {
        return new KeyedValueImpl<T>(key, Objects.requireNonNull(value, "value"));
    }

    @NotNull
    public T value();
}

