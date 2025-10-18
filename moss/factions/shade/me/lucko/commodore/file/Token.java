/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.me.lucko.commodore.file;

public interface Token {

    public static final class StringToken
    implements Token {
        private final String string;

        StringToken(String string) {
            this.string = string;
        }

        public String getString() {
            return this.string;
        }
    }

    public static enum ConstantToken implements Token
    {
        OPEN_BRACKET,
        CLOSE_BRACKET,
        SEMICOLON,
        EOF;

    }
}

