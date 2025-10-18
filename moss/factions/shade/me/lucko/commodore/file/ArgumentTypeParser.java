/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package moss.factions.shade.me.lucko.commodore.file;

import com.mojang.brigadier.arguments.ArgumentType;
import moss.factions.shade.me.lucko.commodore.file.ParseException;
import moss.factions.shade.me.lucko.commodore.file.TokenStream;

public interface ArgumentTypeParser {
    public boolean canParse(String var1, String var2);

    public ArgumentType<?> parse(String var1, String var2, TokenStream var3) throws ParseException;
}

