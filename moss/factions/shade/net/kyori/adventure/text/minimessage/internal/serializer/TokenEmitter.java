/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer;

import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.QuotingOverride;
import org.jetbrains.annotations.NotNull;

public interface TokenEmitter {
    @NotNull
    public TokenEmitter tag(@NotNull String var1);

    @NotNull
    public TokenEmitter selfClosingTag(@NotNull String var1);

    @NotNull
    default public TokenEmitter arguments(@NotNull String ... args) {
        for (String arg : args) {
            this.argument(arg);
        }
        return this;
    }

    @NotNull
    public TokenEmitter argument(@NotNull String var1);

    @NotNull
    public TokenEmitter argument(@NotNull String var1, @NotNull QuotingOverride var2);

    @NotNull
    public TokenEmitter argument(@NotNull Component var1);

    @NotNull
    public TokenEmitter text(@NotNull String var1);

    @NotNull
    public TokenEmitter pop();
}

