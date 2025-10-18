/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.hocon;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import moss.factions.shade.com.typesafe.config.Config;
import moss.factions.shade.com.typesafe.config.ConfigFactory;
import moss.factions.shade.com.typesafe.config.ConfigList;
import moss.factions.shade.com.typesafe.config.ConfigObject;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigOriginFactory;
import moss.factions.shade.com.typesafe.config.ConfigParseOptions;
import moss.factions.shade.com.typesafe.config.ConfigRenderOptions;
import moss.factions.shade.com.typesafe.config.ConfigValue;
import moss.factions.shade.com.typesafe.config.ConfigValueFactory;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationOptions;
import moss.factions.shade.ninja.leaping.configurate.commented.CommentedConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.loader.AbstractConfigurationLoader;
import moss.factions.shade.ninja.leaping.configurate.loader.CommentHandler;
import moss.factions.shade.ninja.leaping.configurate.loader.CommentHandlers;
import org.checkerframework.checker.nullness.qual.NonNull;

public class HoconConfigurationLoader
extends AbstractConfigurationLoader<CommentedConfigurationNode> {
    public static final Pattern CRLF_MATCH;
    private static final ConfigRenderOptions DEFAULT_RENDER_OPTIONS;
    private static final ConfigOrigin CONFIGURATE_ORIGIN;
    private final ConfigRenderOptions render;
    private final ConfigParseOptions parse;
    private static final Constructor<? extends ConfigValue> CONFIG_OBJECT_CONSTRUCTOR;
    private static final Constructor<? extends ConfigValue> CONFIG_LIST_CONSTRUCTOR;

    public static ConfigRenderOptions defaultRenderOptions() {
        return DEFAULT_RENDER_OPTIONS;
    }

    public static ConfigParseOptions defaultParseOptions() {
        return ConfigParseOptions.defaults();
    }

    public static @NonNull Builder builder() {
        return new Builder();
    }

    private HoconConfigurationLoader(Builder builder) {
        super(builder, new CommentHandler[]{CommentHandlers.HASH, CommentHandlers.DOUBLE_SLASH});
        this.render = builder.getRenderOptions();
        this.parse = builder.getParseOptions();
    }

    @Override
    public void loadInternal(CommentedConfigurationNode commentedConfigurationNode, BufferedReader bufferedReader) {
        Config config = ConfigFactory.parseReader(bufferedReader, this.parse);
        config = config.resolve();
        for (Map.Entry entry : config.root().entrySet()) {
            HoconConfigurationLoader.readConfigValue((ConfigValue)entry.getValue(), commentedConfigurationNode.getNode(entry.getKey()));
        }
    }

    private static void readConfigValue(ConfigValue configValue, CommentedConfigurationNode commentedConfigurationNode) {
        if (!configValue.origin().comments().isEmpty()) {
            commentedConfigurationNode.setComment(CRLF_MATCH.matcher(Joiner.on('\n').join(configValue.origin().comments())).replaceAll(""));
        }
        switch (configValue.valueType()) {
            case OBJECT: {
                if (((ConfigObject)configValue).isEmpty()) {
                    commentedConfigurationNode.setValue(Collections.emptyMap());
                    break;
                }
                for (Map.Entry entry : ((ConfigObject)configValue).entrySet()) {
                    HoconConfigurationLoader.readConfigValue((ConfigValue)entry.getValue(), commentedConfigurationNode.getNode(entry.getKey()));
                }
                break;
            }
            case LIST: {
                ConfigList configList = (ConfigList)configValue;
                if (configList.isEmpty()) {
                    commentedConfigurationNode.setValue(Collections.emptyList());
                    break;
                }
                for (int i = 0; i < configList.size(); ++i) {
                    HoconConfigurationLoader.readConfigValue((ConfigValue)configList.get(i), commentedConfigurationNode.getNode(i));
                }
                break;
            }
            case NULL: {
                return;
            }
            default: {
                commentedConfigurationNode.setValue(configValue.unwrapped());
            }
        }
    }

    @Override
    protected void saveInternal(ConfigurationNode configurationNode, Writer writer) {
        if (!configurationNode.isMap()) {
            if (configurationNode.getValue() == null) {
                writer.write(SYSTEM_LINE_SEPARATOR);
                return;
            }
            throw new IOException("HOCON cannot write nodes not in map format!");
        }
        ConfigValue configValue = HoconConfigurationLoader.fromValue(configurationNode);
        String string = configValue.render(this.render);
        writer.write(string);
    }

    private static ConfigValue fromValue(ConfigurationNode configurationNode) {
        ConfigValue configValue;
        Object object;
        if (configurationNode.isMap()) {
            object = configurationNode.getOptions().getMapFactory().create();
            for (Map.Entry entry : configurationNode.getChildrenMap().entrySet()) {
                object.put(String.valueOf(entry.getKey()), HoconConfigurationLoader.fromValue((ConfigurationNode)entry.getValue()));
            }
            configValue = HoconConfigurationLoader.newConfigObject((Map<String, ConfigValue>)object);
        } else if (configurationNode.isList()) {
            object = new ArrayList();
            for (ConfigurationNode configurationNode2 : configurationNode.getChildrenList()) {
                object.add(HoconConfigurationLoader.fromValue(configurationNode2));
            }
            configValue = HoconConfigurationLoader.newConfigList((List<ConfigValue>)object);
        } else {
            configValue = ConfigValueFactory.fromAnyRef(configurationNode.getValue(), CONFIGURATE_ORIGIN.description());
        }
        if (configurationNode instanceof CommentedConfigurationNode) {
            object = (CommentedConfigurationNode)configurationNode;
            ConfigValue configValue2 = configValue;
            configValue = object.getComment().map(string -> configValue2.withOrigin(configValue2.origin().withComments(LINE_SPLITTER.splitToList((CharSequence)string)))).orElse(configValue);
        }
        return configValue;
    }

    static ConfigValue newConfigObject(Map<String, ConfigValue> map) {
        try {
            return CONFIG_OBJECT_CONSTRUCTOR.newInstance(CONFIGURATE_ORIGIN, map);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException reflectiveOperationException) {
            throw new RuntimeException(reflectiveOperationException);
        }
    }

    static ConfigValue newConfigList(List<ConfigValue> list) {
        try {
            return CONFIG_LIST_CONSTRUCTOR.newInstance(CONFIGURATE_ORIGIN, list);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException reflectiveOperationException) {
            throw new RuntimeException(reflectiveOperationException);
        }
    }

    @Override
    public @NonNull CommentedConfigurationNode createEmptyNode(@NonNull ConfigurationOptions configurationOptions) {
        configurationOptions = configurationOptions.withNativeTypes(ImmutableSet.of(Map.class, List.class, Double.class, Long.class, Integer.class, Boolean.class, String.class, Number.class));
        return CommentedConfigurationNode.root(configurationOptions);
    }

    static {
        Class<ConfigValue> clazz;
        Class<ConfigValue> clazz2;
        CRLF_MATCH = Pattern.compile("\r?");
        DEFAULT_RENDER_OPTIONS = ConfigRenderOptions.defaults().setOriginComments(false).setJson(false);
        CONFIGURATE_ORIGIN = ConfigOriginFactory.newSimple("configurate-hocon");
        try {
            clazz2 = Class.forName("moss.factions.shade.com.typesafe.config.impl.SimpleConfigObject").asSubclass(ConfigValue.class);
            clazz = Class.forName("moss.factions.shade.com.typesafe.config.impl.SimpleConfigList").asSubclass(ConfigValue.class);
        } catch (ClassNotFoundException classNotFoundException) {
            throw new ExceptionInInitializerError(classNotFoundException);
        }
        try {
            CONFIG_OBJECT_CONSTRUCTOR = clazz2.getDeclaredConstructor(ConfigOrigin.class, Map.class);
            CONFIG_OBJECT_CONSTRUCTOR.setAccessible(true);
            CONFIG_LIST_CONSTRUCTOR = clazz.getDeclaredConstructor(ConfigOrigin.class, List.class);
            CONFIG_LIST_CONSTRUCTOR.setAccessible(true);
        } catch (NoSuchMethodException noSuchMethodException) {
            throw new ExceptionInInitializerError(noSuchMethodException);
        }
    }

    public static class Builder
    extends AbstractConfigurationLoader.Builder<Builder> {
        private ConfigRenderOptions render = HoconConfigurationLoader.defaultRenderOptions();
        private ConfigParseOptions parse = HoconConfigurationLoader.defaultParseOptions();

        protected Builder() {
        }

        public @NonNull Builder setRenderOptions(@NonNull ConfigRenderOptions configRenderOptions) {
            this.render = configRenderOptions;
            return this;
        }

        public @NonNull ConfigRenderOptions getRenderOptions() {
            return this.render;
        }

        public @NonNull Builder setParseOptions(ConfigParseOptions configParseOptions) {
            this.parse = configParseOptions;
            return this;
        }

        public @NonNull ConfigParseOptions getParseOptions() {
            return this.parse;
        }

        public @NonNull HoconConfigurationLoader build() {
            return new HoconConfigurationLoader(this);
        }
    }
}

