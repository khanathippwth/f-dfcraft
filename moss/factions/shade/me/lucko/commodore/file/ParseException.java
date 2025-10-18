/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.me.lucko.commodore.file;

public final class ParseException
extends Exception {
    ParseException(String string, int n) {
        super(string + " (at line " + n + ")");
    }

    ParseException(Throwable throwable, int n) {
        super("At line " + n, throwable);
    }

    ParseException(String string, Throwable throwable, int n) {
        super(string + " (at line " + n + ")", throwable);
    }
}

