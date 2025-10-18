/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import moss.factions.shade.com.typesafe.config.ConfigIncludeContext;
import moss.factions.shade.com.typesafe.config.ConfigParseOptions;
import moss.factions.shade.com.typesafe.config.ConfigParseable;
import moss.factions.shade.com.typesafe.config.impl.ConfigImpl;
import moss.factions.shade.com.typesafe.config.impl.Parseable;
import moss.factions.shade.com.typesafe.config.impl.SimpleIncluder;

class SimpleIncludeContext
implements ConfigIncludeContext {
    private final Parseable parseable;
    private final ConfigParseOptions options;

    SimpleIncludeContext(Parseable parseable) {
        this.parseable = parseable;
        this.options = SimpleIncluder.clearForInclude(parseable.options());
    }

    private SimpleIncludeContext(Parseable parseable, ConfigParseOptions configParseOptions) {
        this.parseable = parseable;
        this.options = configParseOptions;
    }

    SimpleIncludeContext withParseable(Parseable parseable) {
        if (parseable == this.parseable) {
            return this;
        }
        return new SimpleIncludeContext(parseable);
    }

    @Override
    public ConfigParseable relativeTo(String string) {
        if (ConfigImpl.traceLoadsEnabled()) {
            ConfigImpl.trace("Looking for '" + string + "' relative to " + this.parseable);
        }
        if (this.parseable != null) {
            return this.parseable.relativeTo(string);
        }
        return null;
    }

    @Override
    public ConfigParseOptions parseOptions() {
        return this.options;
    }

    @Override
    public ConfigIncludeContext setParseOptions(ConfigParseOptions configParseOptions) {
        return new SimpleIncludeContext(this.parseable, configParseOptions.setSyntax(null).setOriginDescription(null));
    }
}

