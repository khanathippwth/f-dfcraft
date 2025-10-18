/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import moss.factions.shade.com.typesafe.config.Config;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigFactory;
import moss.factions.shade.com.typesafe.config.ConfigLoadingStrategy;
import moss.factions.shade.com.typesafe.config.ConfigParseOptions;

public class DefaultConfigLoadingStrategy
implements ConfigLoadingStrategy {
    @Override
    public Config parseApplicationConfig(ConfigParseOptions configParseOptions) {
        String string;
        String string2;
        ClassLoader classLoader = configParseOptions.getClassLoader();
        if (classLoader == null) {
            throw new ConfigException.BugOrBroken("ClassLoader should have been set here; bug in ConfigFactory. (You can probably work around this bug by passing in a class loader or calling currentThread().setContextClassLoader() though.)");
        }
        int n = 0;
        String string3 = System.getProperty("config.resource");
        if (string3 != null) {
            ++n;
        }
        if ((string2 = System.getProperty("config.file")) != null) {
            ++n;
        }
        if ((string = System.getProperty("config.url")) != null) {
            ++n;
        }
        if (n == 0) {
            return ConfigFactory.parseResourcesAnySyntax("application", configParseOptions);
        }
        if (n > 1) {
            throw new ConfigException.Generic("You set more than one of config.file='" + string2 + "', config.url='" + string + "', config.resource='" + string3 + "'; don't know which one to use!");
        }
        ConfigParseOptions configParseOptions2 = configParseOptions.setAllowMissing(false);
        if (string3 != null) {
            if (string3.startsWith("/")) {
                string3 = string3.substring(1);
            }
            return ConfigFactory.parseResources(classLoader, string3, configParseOptions2);
        }
        if (string2 != null) {
            return ConfigFactory.parseFile(new File(string2), configParseOptions2);
        }
        try {
            return ConfigFactory.parseURL(new URL(string), configParseOptions2);
        } catch (MalformedURLException malformedURLException) {
            throw new ConfigException.Generic("Bad URL in config.url system property: '" + string + "': " + malformedURLException.getMessage(), malformedURLException);
        }
    }
}

