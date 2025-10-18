/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigIncludeContext;
import moss.factions.shade.com.typesafe.config.ConfigObject;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigParseOptions;
import moss.factions.shade.com.typesafe.config.ConfigParseable;
import moss.factions.shade.com.typesafe.config.ConfigSyntax;
import moss.factions.shade.com.typesafe.config.ConfigValue;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNode;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigObject;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigDocumentParser;
import moss.factions.shade.com.typesafe.config.impl.ConfigImpl;
import moss.factions.shade.com.typesafe.config.impl.ConfigImplUtil;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeObject;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeRoot;
import moss.factions.shade.com.typesafe.config.impl.ConfigParser;
import moss.factions.shade.com.typesafe.config.impl.PropertiesParser;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigDocument;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigObject;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigOrigin;
import moss.factions.shade.com.typesafe.config.impl.SimpleIncludeContext;
import moss.factions.shade.com.typesafe.config.impl.SimpleIncluder;
import moss.factions.shade.com.typesafe.config.impl.Token;
import moss.factions.shade.com.typesafe.config.impl.Tokenizer;
import moss.factions.shade.com.typesafe.config.parser.ConfigDocument;

public abstract class Parseable
implements ConfigParseable {
    private ConfigIncludeContext includeContext;
    private ConfigParseOptions initialOptions;
    private ConfigOrigin initialOrigin;
    private static final ThreadLocal<LinkedList<Parseable>> parseStack = new ThreadLocal<LinkedList<Parseable>>(){

        @Override
        protected LinkedList<Parseable> initialValue() {
            return new LinkedList<Parseable>();
        }
    };
    private static final int MAX_INCLUDE_DEPTH = 50;
    private static final String jsonContentType = "application/json";
    private static final String propertiesContentType = "text/x-java-properties";
    private static final String hoconContentType = "application/hocon";

    protected Parseable() {
    }

    private ConfigParseOptions fixupOptions(ConfigParseOptions configParseOptions) {
        ConfigSyntax configSyntax = configParseOptions.getSyntax();
        if (configSyntax == null) {
            configSyntax = this.guessSyntax();
        }
        if (configSyntax == null) {
            configSyntax = ConfigSyntax.CONF;
        }
        ConfigParseOptions configParseOptions2 = configParseOptions.setSyntax(configSyntax);
        configParseOptions2 = configParseOptions2.appendIncluder(ConfigImpl.defaultIncluder());
        configParseOptions2 = configParseOptions2.setIncluder(SimpleIncluder.makeFull(configParseOptions2.getIncluder()));
        return configParseOptions2;
    }

    protected void postConstruct(ConfigParseOptions configParseOptions) {
        this.initialOptions = this.fixupOptions(configParseOptions);
        this.includeContext = new SimpleIncludeContext(this);
        this.initialOrigin = this.initialOptions.getOriginDescription() != null ? SimpleConfigOrigin.newSimple(this.initialOptions.getOriginDescription()) : this.createOrigin();
    }

    protected abstract Reader reader();

    protected Reader reader(ConfigParseOptions configParseOptions) {
        return this.reader();
    }

    protected static void trace(String string) {
        if (ConfigImpl.traceLoadsEnabled()) {
            ConfigImpl.trace(string);
        }
    }

    ConfigSyntax guessSyntax() {
        return null;
    }

    ConfigSyntax contentType() {
        return null;
    }

    ConfigParseable relativeTo(String string) {
        String string2 = string;
        if (string.startsWith("/")) {
            string2 = string.substring(1);
        }
        return Parseable.newResources(string2, this.options().setOriginDescription(null));
    }

    ConfigIncludeContext includeContext() {
        return this.includeContext;
    }

    static AbstractConfigObject forceParsedToObject(ConfigValue configValue) {
        if (configValue instanceof AbstractConfigObject) {
            return (AbstractConfigObject)configValue;
        }
        throw new ConfigException.WrongType(configValue.origin(), "", "object at file root", configValue.valueType().name());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ConfigObject parse(ConfigParseOptions configParseOptions) {
        LinkedList<Parseable> linkedList = parseStack.get();
        if (linkedList.size() >= 50) {
            throw new ConfigException.Parse(this.initialOrigin, "include statements nested more than 50 times, you probably have a cycle in your includes. Trace: " + linkedList);
        }
        linkedList.addFirst(this);
        try {
            AbstractConfigObject abstractConfigObject = Parseable.forceParsedToObject(this.parseValue(configParseOptions));
            return abstractConfigObject;
        } finally {
            linkedList.removeFirst();
            if (linkedList.isEmpty()) {
                parseStack.remove();
            }
        }
    }

    final AbstractConfigValue parseValue(ConfigParseOptions configParseOptions) {
        ConfigParseOptions configParseOptions2 = this.fixupOptions(configParseOptions);
        ConfigOrigin configOrigin = configParseOptions2.getOriginDescription() != null ? SimpleConfigOrigin.newSimple(configParseOptions2.getOriginDescription()) : this.initialOrigin;
        return this.parseValue(configOrigin, configParseOptions2);
    }

    private final AbstractConfigValue parseValue(ConfigOrigin configOrigin, ConfigParseOptions configParseOptions) {
        try {
            return this.rawParseValue(configOrigin, configParseOptions);
        } catch (IOException iOException) {
            if (configParseOptions.getAllowMissing()) {
                Parseable.trace(iOException.getMessage() + ". Allowing Missing File, this can be turned off by setting ConfigParseOptions.allowMissing = false");
                return SimpleConfigObject.emptyMissing(configOrigin);
            }
            Parseable.trace("exception loading " + configOrigin.description() + ": " + iOException.getClass().getName() + ": " + iOException.getMessage());
            throw new ConfigException.IO(configOrigin, iOException.getClass().getName() + ": " + iOException.getMessage(), iOException);
        }
    }

    final ConfigDocument parseDocument(ConfigParseOptions configParseOptions) {
        ConfigParseOptions configParseOptions2 = this.fixupOptions(configParseOptions);
        ConfigOrigin configOrigin = configParseOptions2.getOriginDescription() != null ? SimpleConfigOrigin.newSimple(configParseOptions2.getOriginDescription()) : this.initialOrigin;
        return this.parseDocument(configOrigin, configParseOptions2);
    }

    private final ConfigDocument parseDocument(ConfigOrigin configOrigin, ConfigParseOptions configParseOptions) {
        try {
            return this.rawParseDocument(configOrigin, configParseOptions);
        } catch (IOException iOException) {
            if (configParseOptions.getAllowMissing()) {
                ArrayList<AbstractConfigNode> arrayList = new ArrayList<AbstractConfigNode>();
                arrayList.add(new ConfigNodeObject(new ArrayList<AbstractConfigNode>()));
                return new SimpleConfigDocument(new ConfigNodeRoot(arrayList, configOrigin), configParseOptions);
            }
            Parseable.trace("exception loading " + configOrigin.description() + ": " + iOException.getClass().getName() + ": " + iOException.getMessage());
            throw new ConfigException.IO(configOrigin, iOException.getClass().getName() + ": " + iOException.getMessage(), iOException);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected AbstractConfigValue rawParseValue(ConfigOrigin configOrigin, ConfigParseOptions configParseOptions) {
        ConfigParseOptions configParseOptions2;
        Reader reader = this.reader(configParseOptions);
        ConfigSyntax configSyntax = this.contentType();
        if (configSyntax != null) {
            if (ConfigImpl.traceLoadsEnabled() && configParseOptions.getSyntax() != null) {
                Parseable.trace("Overriding syntax " + (Object)((Object)configParseOptions.getSyntax()) + " with Content-Type which specified " + (Object)((Object)configSyntax));
            }
            configParseOptions2 = configParseOptions.setSyntax(configSyntax);
        } else {
            configParseOptions2 = configParseOptions;
        }
        try {
            AbstractConfigValue abstractConfigValue = this.rawParseValue(reader, configOrigin, configParseOptions2);
            return abstractConfigValue;
        } finally {
            reader.close();
        }
    }

    private AbstractConfigValue rawParseValue(Reader reader, ConfigOrigin configOrigin, ConfigParseOptions configParseOptions) {
        if (configParseOptions.getSyntax() == ConfigSyntax.PROPERTIES) {
            return PropertiesParser.parse(reader, configOrigin);
        }
        Iterator<Token> iterator = Tokenizer.tokenize(configOrigin, reader, configParseOptions.getSyntax());
        ConfigNodeRoot configNodeRoot = ConfigDocumentParser.parse(iterator, configOrigin, configParseOptions);
        return ConfigParser.parse(configNodeRoot, configOrigin, configParseOptions, this.includeContext());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected ConfigDocument rawParseDocument(ConfigOrigin configOrigin, ConfigParseOptions configParseOptions) {
        ConfigParseOptions configParseOptions2;
        Reader reader = this.reader(configParseOptions);
        ConfigSyntax configSyntax = this.contentType();
        if (configSyntax != null) {
            if (ConfigImpl.traceLoadsEnabled() && configParseOptions.getSyntax() != null) {
                Parseable.trace("Overriding syntax " + (Object)((Object)configParseOptions.getSyntax()) + " with Content-Type which specified " + (Object)((Object)configSyntax));
            }
            configParseOptions2 = configParseOptions.setSyntax(configSyntax);
        } else {
            configParseOptions2 = configParseOptions;
        }
        try {
            ConfigDocument configDocument = this.rawParseDocument(reader, configOrigin, configParseOptions2);
            return configDocument;
        } finally {
            reader.close();
        }
    }

    private ConfigDocument rawParseDocument(Reader reader, ConfigOrigin configOrigin, ConfigParseOptions configParseOptions) {
        Iterator<Token> iterator = Tokenizer.tokenize(configOrigin, reader, configParseOptions.getSyntax());
        return new SimpleConfigDocument(ConfigDocumentParser.parse(iterator, configOrigin, configParseOptions), configParseOptions);
    }

    public ConfigObject parse() {
        return Parseable.forceParsedToObject(this.parseValue(this.options()));
    }

    public ConfigDocument parseConfigDocument() {
        return this.parseDocument(this.options());
    }

    AbstractConfigValue parseValue() {
        return this.parseValue(this.options());
    }

    @Override
    public final ConfigOrigin origin() {
        return this.initialOrigin;
    }

    protected abstract ConfigOrigin createOrigin();

    @Override
    public ConfigParseOptions options() {
        return this.initialOptions;
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    private static Reader readerFromStream(InputStream inputStream) {
        return Parseable.readerFromStream(inputStream, "UTF-8");
    }

    private static Reader readerFromStream(InputStream inputStream, String string) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, string);
            return new BufferedReader(inputStreamReader);
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new ConfigException.BugOrBroken("Java runtime does not support UTF-8", unsupportedEncodingException);
        }
    }

    private static Reader doNotClose(Reader reader) {
        return new FilterReader(reader){

            @Override
            public void close() {
            }
        };
    }

    static URL relativeTo(URL uRL, String string) {
        if (new File(string).isAbsolute()) {
            return null;
        }
        try {
            URI uRI = uRL.toURI();
            URI uRI2 = new URI(string);
            URL uRL2 = uRI.resolve(uRI2).toURL();
            return uRL2;
        } catch (MalformedURLException malformedURLException) {
            return null;
        } catch (URISyntaxException uRISyntaxException) {
            return null;
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }

    static File relativeTo(File file, String string) {
        File file2 = new File(string);
        if (file2.isAbsolute()) {
            return null;
        }
        File file3 = file.getParentFile();
        if (file3 == null) {
            return null;
        }
        return new File(file3, string);
    }

    public static Parseable newNotFound(String string, String string2, ConfigParseOptions configParseOptions) {
        return new ParseableNotFound(string, string2, configParseOptions);
    }

    public static Parseable newReader(Reader reader, ConfigParseOptions configParseOptions) {
        return new ParseableReader(Parseable.doNotClose(reader), configParseOptions);
    }

    public static Parseable newString(String string, ConfigParseOptions configParseOptions) {
        return new ParseableString(string, configParseOptions);
    }

    public static Parseable newURL(URL uRL, ConfigParseOptions configParseOptions) {
        if (uRL.getProtocol().equals("file")) {
            return Parseable.newFile(ConfigImplUtil.urlToFile(uRL), configParseOptions);
        }
        return new ParseableURL(uRL, configParseOptions);
    }

    public static Parseable newFile(File file, ConfigParseOptions configParseOptions) {
        return new ParseableFile(file, configParseOptions);
    }

    private static Parseable newResourceURL(URL uRL, ConfigParseOptions configParseOptions, String string, Relativizer relativizer) {
        return new ParseableResourceURL(uRL, configParseOptions, string, relativizer);
    }

    public static Parseable newResources(Class<?> clazz, String string, ConfigParseOptions configParseOptions) {
        return Parseable.newResources(Parseable.convertResourceName(clazz, string), configParseOptions.setClassLoader(clazz.getClassLoader()));
    }

    private static String convertResourceName(Class<?> clazz, String string) {
        if (string.startsWith("/")) {
            return string.substring(1);
        }
        String string2 = clazz.getName();
        int n = string2.lastIndexOf(46);
        if (n < 0) {
            return string;
        }
        String string3 = string2.substring(0, n);
        String string4 = string3.replace('.', '/');
        return string4 + "/" + string;
    }

    public static Parseable newResources(String string, ConfigParseOptions configParseOptions) {
        if (configParseOptions.getClassLoader() == null) {
            throw new ConfigException.BugOrBroken("null class loader; pass in a class loader or use Thread.currentThread().setContextClassLoader()");
        }
        return new ParseableResources(string, configParseOptions);
    }

    public static Parseable newProperties(Properties properties, ConfigParseOptions configParseOptions) {
        return new ParseableProperties(properties, configParseOptions);
    }

    private static final class ParseableProperties
    extends Parseable {
        private final Properties props;

        ParseableProperties(Properties properties, ConfigParseOptions configParseOptions) {
            this.props = properties;
            this.postConstruct(configParseOptions);
        }

        @Override
        protected Reader reader() {
            throw new ConfigException.BugOrBroken("reader() should not be called on props");
        }

        @Override
        protected AbstractConfigObject rawParseValue(ConfigOrigin configOrigin, ConfigParseOptions configParseOptions) {
            if (ConfigImpl.traceLoadsEnabled()) {
                ParseableProperties.trace("Loading config from properties " + this.props);
            }
            return PropertiesParser.fromProperties(configOrigin, this.props);
        }

        @Override
        ConfigSyntax guessSyntax() {
            return ConfigSyntax.PROPERTIES;
        }

        @Override
        protected ConfigOrigin createOrigin() {
            return SimpleConfigOrigin.newSimple("properties");
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + this.props.size() + " props)";
        }
    }

    private static final class ParseableResources
    extends Parseable
    implements Relativizer {
        private final String resource;

        ParseableResources(String string, ConfigParseOptions configParseOptions) {
            this.resource = string;
            this.postConstruct(configParseOptions);
        }

        @Override
        protected Reader reader() {
            throw new ConfigException.BugOrBroken("reader() should not be called on resources");
        }

        @Override
        protected AbstractConfigObject rawParseValue(ConfigOrigin configOrigin, ConfigParseOptions configParseOptions) {
            ClassLoader classLoader = configParseOptions.getClassLoader();
            if (classLoader == null) {
                throw new ConfigException.BugOrBroken("null class loader; pass in a class loader or use Thread.currentThread().setContextClassLoader()");
            }
            Enumeration<URL> enumeration = classLoader.getResources(this.resource);
            if (!enumeration.hasMoreElements()) {
                if (ConfigImpl.traceLoadsEnabled()) {
                    ParseableResources.trace("Loading config from class loader " + classLoader + " but there were no resources called " + this.resource);
                }
                throw new IOException("resource not found on classpath: " + this.resource);
            }
            AbstractConfigObject abstractConfigObject = SimpleConfigObject.empty(configOrigin);
            while (enumeration.hasMoreElements()) {
                URL uRL = enumeration.nextElement();
                if (ConfigImpl.traceLoadsEnabled()) {
                    ParseableResources.trace("Loading config from resource '" + this.resource + "' URL " + uRL.toExternalForm() + " from class loader " + classLoader);
                }
                Parseable parseable = Parseable.newResourceURL(uRL, configParseOptions, this.resource, this);
                AbstractConfigValue abstractConfigValue = parseable.parseValue();
                abstractConfigObject = abstractConfigObject.withFallback(abstractConfigValue);
            }
            return abstractConfigObject;
        }

        @Override
        ConfigSyntax guessSyntax() {
            return ConfigImplUtil.syntaxFromExtension(this.resource);
        }

        static String parent(String string) {
            int n = string.lastIndexOf(47);
            if (n < 0) {
                return null;
            }
            return string.substring(0, n);
        }

        @Override
        public ConfigParseable relativeTo(String string) {
            if (string.startsWith("/")) {
                return ParseableResources.newResources(string.substring(1), this.options().setOriginDescription(null));
            }
            String string2 = ParseableResources.parent(this.resource);
            if (string2 == null) {
                return ParseableResources.newResources(string, this.options().setOriginDescription(null));
            }
            return ParseableResources.newResources(string2 + "/" + string, this.options().setOriginDescription(null));
        }

        @Override
        protected ConfigOrigin createOrigin() {
            return SimpleConfigOrigin.newResource(this.resource);
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + this.resource + ")";
        }
    }

    private static final class ParseableResourceURL
    extends ParseableURL {
        private final Relativizer relativizer;
        private final String resource;

        ParseableResourceURL(URL uRL, ConfigParseOptions configParseOptions, String string, Relativizer relativizer) {
            super(uRL);
            this.relativizer = relativizer;
            this.resource = string;
            this.postConstruct(configParseOptions);
        }

        @Override
        protected ConfigOrigin createOrigin() {
            return SimpleConfigOrigin.newResource(this.resource, this.input);
        }

        @Override
        ConfigParseable relativeTo(String string) {
            return this.relativizer.relativeTo(string);
        }
    }

    private static final class ParseableFile
    extends Parseable {
        private final File input;

        ParseableFile(File file, ConfigParseOptions configParseOptions) {
            this.input = file;
            this.postConstruct(configParseOptions);
        }

        @Override
        protected Reader reader() {
            if (ConfigImpl.traceLoadsEnabled()) {
                ParseableFile.trace("Loading config from a file: " + this.input);
            }
            FileInputStream fileInputStream = new FileInputStream(this.input);
            return Parseable.readerFromStream(fileInputStream);
        }

        @Override
        ConfigSyntax guessSyntax() {
            return ConfigImplUtil.syntaxFromExtension(this.input.getName());
        }

        @Override
        ConfigParseable relativeTo(String string) {
            File file = new File(string).isAbsolute() ? new File(string) : ParseableFile.relativeTo(this.input, string);
            if (file == null) {
                return null;
            }
            if (file.exists()) {
                ParseableFile.trace(file + " exists, so loading it as a file");
                return ParseableFile.newFile(file, this.options().setOriginDescription(null));
            }
            ParseableFile.trace(file + " does not exist, so trying it as a classpath resource");
            return super.relativeTo(string);
        }

        @Override
        protected ConfigOrigin createOrigin() {
            return SimpleConfigOrigin.newFile(this.input.getPath());
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + this.input.getPath() + ")";
        }
    }

    private static class ParseableURL
    extends Parseable {
        protected final URL input;
        private String contentType = null;

        protected ParseableURL(URL uRL) {
            this.input = uRL;
        }

        ParseableURL(URL uRL, ConfigParseOptions configParseOptions) {
            this(uRL);
            this.postConstruct(configParseOptions);
        }

        @Override
        protected Reader reader() {
            throw new ConfigException.BugOrBroken("reader() without options should not be called on ParseableURL");
        }

        private static String acceptContentType(ConfigParseOptions configParseOptions) {
            if (configParseOptions.getSyntax() == null) {
                return null;
            }
            switch (configParseOptions.getSyntax()) {
                case JSON: {
                    return Parseable.jsonContentType;
                }
                case CONF: {
                    return Parseable.hoconContentType;
                }
                case PROPERTIES: {
                    return Parseable.propertiesContentType;
                }
            }
            return null;
        }

        @Override
        protected Reader reader(ConfigParseOptions configParseOptions) {
            try {
                if (ConfigImpl.traceLoadsEnabled()) {
                    ParseableURL.trace("Loading config from a URL: " + this.input.toExternalForm());
                }
                URLConnection uRLConnection = this.input.openConnection();
                String string = ParseableURL.acceptContentType(configParseOptions);
                if (string != null) {
                    uRLConnection.setRequestProperty("Accept", string);
                }
                uRLConnection.connect();
                this.contentType = uRLConnection.getContentType();
                if (this.contentType != null) {
                    if (ConfigImpl.traceLoadsEnabled()) {
                        ParseableURL.trace("URL sets Content-Type: '" + this.contentType + "'");
                    }
                    this.contentType = this.contentType.trim();
                    int n = this.contentType.indexOf(59);
                    if (n >= 0) {
                        this.contentType = this.contentType.substring(0, n);
                    }
                }
                InputStream inputStream = uRLConnection.getInputStream();
                return Parseable.readerFromStream(inputStream);
            } catch (FileNotFoundException fileNotFoundException) {
                throw fileNotFoundException;
            } catch (IOException iOException) {
                throw new ConfigException.BugOrBroken("Cannot load config from URL: " + this.input.toExternalForm(), iOException);
            }
        }

        @Override
        ConfigSyntax guessSyntax() {
            return ConfigImplUtil.syntaxFromExtension(this.input.getPath());
        }

        @Override
        ConfigSyntax contentType() {
            if (this.contentType != null) {
                if (this.contentType.equals(Parseable.jsonContentType)) {
                    return ConfigSyntax.JSON;
                }
                if (this.contentType.equals(Parseable.propertiesContentType)) {
                    return ConfigSyntax.PROPERTIES;
                }
                if (this.contentType.equals(Parseable.hoconContentType)) {
                    return ConfigSyntax.CONF;
                }
                if (ConfigImpl.traceLoadsEnabled()) {
                    ParseableURL.trace("'" + this.contentType + "' isn't a known content type");
                }
                return null;
            }
            return null;
        }

        @Override
        ConfigParseable relativeTo(String string) {
            URL uRL = ParseableURL.relativeTo(this.input, string);
            if (uRL == null) {
                return null;
            }
            return ParseableURL.newURL(uRL, this.options().setOriginDescription(null));
        }

        @Override
        protected ConfigOrigin createOrigin() {
            return SimpleConfigOrigin.newURL(this.input);
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + this.input.toExternalForm() + ")";
        }
    }

    private static final class ParseableString
    extends Parseable {
        private final String input;

        ParseableString(String string, ConfigParseOptions configParseOptions) {
            this.input = string;
            this.postConstruct(configParseOptions);
        }

        @Override
        protected Reader reader() {
            if (ConfigImpl.traceLoadsEnabled()) {
                ParseableString.trace("Loading config from a String " + this.input);
            }
            return new StringReader(this.input);
        }

        @Override
        protected ConfigOrigin createOrigin() {
            return SimpleConfigOrigin.newSimple("String");
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "(" + this.input + ")";
        }
    }

    private static final class ParseableReader
    extends Parseable {
        private final Reader reader;

        ParseableReader(Reader reader, ConfigParseOptions configParseOptions) {
            this.reader = reader;
            this.postConstruct(configParseOptions);
        }

        @Override
        protected Reader reader() {
            if (ConfigImpl.traceLoadsEnabled()) {
                ParseableReader.trace("Loading config from reader " + this.reader);
            }
            return this.reader;
        }

        @Override
        protected ConfigOrigin createOrigin() {
            return SimpleConfigOrigin.newSimple("Reader");
        }
    }

    private static final class ParseableNotFound
    extends Parseable {
        private final String what;
        private final String message;

        ParseableNotFound(String string, String string2, ConfigParseOptions configParseOptions) {
            this.what = string;
            this.message = string2;
            this.postConstruct(configParseOptions);
        }

        @Override
        protected Reader reader() {
            throw new FileNotFoundException(this.message);
        }

        @Override
        protected ConfigOrigin createOrigin() {
            return SimpleConfigOrigin.newSimple(this.what);
        }
    }

    protected static interface Relativizer {
        public ConfigParseable relativeTo(String var1);
    }
}

