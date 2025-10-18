/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.me.lucko.commodore.file;

import moss.factions.shade.me.lucko.commodore.file.ParseException;
import moss.factions.shade.me.lucko.commodore.file.Token;

public interface TokenStream {
    public boolean hasNext();

    public Token next();

    public Token peek();

    public ParseException createException(String var1);

    public ParseException createException(Throwable var1);

    public ParseException createException(String var1, Throwable var2);
}

