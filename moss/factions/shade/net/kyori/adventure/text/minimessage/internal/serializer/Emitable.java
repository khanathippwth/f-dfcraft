/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer;

import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Emitable {
    public void emit(@NotNull TokenEmitter var1);
}

