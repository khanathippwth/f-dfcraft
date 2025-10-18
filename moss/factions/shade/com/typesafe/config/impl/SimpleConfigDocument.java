/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.StringReader;
import java.util.Iterator;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigParseOptions;
import moss.factions.shade.com.typesafe.config.ConfigRenderOptions;
import moss.factions.shade.com.typesafe.config.ConfigValue;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNodeValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigDocumentParser;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeRoot;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigOrigin;
import moss.factions.shade.com.typesafe.config.impl.Token;
import moss.factions.shade.com.typesafe.config.impl.Tokenizer;
import moss.factions.shade.com.typesafe.config.parser.ConfigDocument;

final class SimpleConfigDocument
implements ConfigDocument {
    private ConfigNodeRoot configNodeTree;
    private ConfigParseOptions parseOptions;

    SimpleConfigDocument(ConfigNodeRoot configNodeRoot, ConfigParseOptions configParseOptions) {
        this.configNodeTree = configNodeRoot;
        this.parseOptions = configParseOptions;
    }

    @Override
    public ConfigDocument withValueText(String string, String string2) {
        if (string2 == null) {
            throw new ConfigException.BugOrBroken("null value for " + string + " passed to withValueText");
        }
        SimpleConfigOrigin simpleConfigOrigin = SimpleConfigOrigin.newSimple("single value parsing");
        StringReader stringReader = new StringReader(string2);
        Iterator<Token> iterator = Tokenizer.tokenize(simpleConfigOrigin, stringReader, this.parseOptions.getSyntax());
        AbstractConfigNodeValue abstractConfigNodeValue = ConfigDocumentParser.parseValue(iterator, simpleConfigOrigin, this.parseOptions);
        stringReader.close();
        return new SimpleConfigDocument(this.configNodeTree.setValue(string, abstractConfigNodeValue, this.parseOptions.getSyntax()), this.parseOptions);
    }

    @Override
    public ConfigDocument withValue(String string, ConfigValue configValue) {
        if (configValue == null) {
            throw new ConfigException.BugOrBroken("null value for " + string + " passed to withValue");
        }
        ConfigRenderOptions configRenderOptions = ConfigRenderOptions.defaults();
        configRenderOptions = configRenderOptions.setOriginComments(false);
        return this.withValueText(string, configValue.render(configRenderOptions).trim());
    }

    @Override
    public ConfigDocument withoutPath(String string) {
        return new SimpleConfigDocument(this.configNodeTree.setValue(string, null, this.parseOptions.getSyntax()), this.parseOptions);
    }

    @Override
    public boolean hasPath(String string) {
        return this.configNodeTree.hasValue(string);
    }

    @Override
    public String render() {
        return this.configNodeTree.render();
    }

    public boolean equals(Object object) {
        return object instanceof ConfigDocument && this.render().equals(((ConfigDocument)object).render());
    }

    public int hashCode() {
        return this.render().hashCode();
    }
}

