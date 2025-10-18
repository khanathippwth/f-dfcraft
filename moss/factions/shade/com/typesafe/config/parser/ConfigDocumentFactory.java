/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.parser;

import java.io.File;
import java.io.Reader;
import moss.factions.shade.com.typesafe.config.ConfigParseOptions;
import moss.factions.shade.com.typesafe.config.impl.Parseable;
import moss.factions.shade.com.typesafe.config.parser.ConfigDocument;

public final class ConfigDocumentFactory {
    public static ConfigDocument parseReader(Reader reader, ConfigParseOptions configParseOptions) {
        return Parseable.newReader(reader, configParseOptions).parseConfigDocument();
    }

    public static ConfigDocument parseReader(Reader reader) {
        return ConfigDocumentFactory.parseReader(reader, ConfigParseOptions.defaults());
    }

    public static ConfigDocument parseFile(File file, ConfigParseOptions configParseOptions) {
        return Parseable.newFile(file, configParseOptions).parseConfigDocument();
    }

    public static ConfigDocument parseFile(File file) {
        return ConfigDocumentFactory.parseFile(file, ConfigParseOptions.defaults());
    }

    public static ConfigDocument parseString(String string, ConfigParseOptions configParseOptions) {
        return Parseable.newString(string, configParseOptions).parseConfigDocument();
    }

    public static ConfigDocument parseString(String string) {
        return ConfigDocumentFactory.parseString(string, ConfigParseOptions.defaults());
    }
}

