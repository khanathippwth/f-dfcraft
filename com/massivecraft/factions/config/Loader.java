/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.config;

import com.google.common.reflect.TypeToken;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.annotation.Comment;
import com.massivecraft.factions.config.annotation.ConfigName;
import com.massivecraft.factions.config.annotation.DefinedType;
import com.massivecraft.factions.config.annotation.WipeOnReload;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import moss.factions.shade.com.typesafe.config.ConfigRenderOptions;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationOptions;
import moss.factions.shade.ninja.leaping.configurate.commented.CommentedConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class Loader {
    private static final Set<Class<?>> types = new HashSet();

    public static void loadAndSave(String string, Object object) {
        HoconConfigurationLoader hoconConfigurationLoader = Loader.getLoader(string);
        Loader.loadAndSave(hoconConfigurationLoader, object);
    }

    public static HoconConfigurationLoader getLoader(String string) {
        Path path = FactionsPlugin.getInstance().getDataFolder().toPath().resolve("config");
        if (!path.toFile().exists()) {
            path.toFile().mkdir();
        }
        Path path2 = path.resolve(string + ".conf");
        return ((HoconConfigurationLoader.Builder)((HoconConfigurationLoader.Builder)HoconConfigurationLoader.builder().setPath(path2)).setDefaultOptions(ConfigurationOptions.defaults().withNativeTypes(null))).setRenderOptions(ConfigRenderOptions.defaults().setComments(true).setOriginComments(false).setJson(false)).build();
    }

    public static void loadAndSave(HoconConfigurationLoader hoconConfigurationLoader, Object object) {
        CommentedConfigurationNode commentedConfigurationNode = (CommentedConfigurationNode)hoconConfigurationLoader.load();
        CommentedConfigurationNode commentedConfigurationNode2 = (CommentedConfigurationNode)hoconConfigurationLoader.createEmptyNode();
        Loader.loadNode(commentedConfigurationNode, commentedConfigurationNode2, object);
        hoconConfigurationLoader.save(commentedConfigurationNode2);
    }

    public static void load(HoconConfigurationLoader hoconConfigurationLoader, Object object) {
        CommentedConfigurationNode commentedConfigurationNode = (CommentedConfigurationNode)hoconConfigurationLoader.load();
        Loader.loadNode(commentedConfigurationNode, (CommentedConfigurationNode)hoconConfigurationLoader.createEmptyNode(), object);
    }

    private static void loadNode(CommentedConfigurationNode commentedConfigurationNode, CommentedConfigurationNode commentedConfigurationNode2, Object object) {
        for (Field field : Loader.getFields(object.getClass())) {
            boolean bl;
            if (field.isSynthetic()) continue;
            if ((field.getModifiers() & 0x80) != 0) {
                if (field.getAnnotation(WipeOnReload.class) == null) continue;
                field.setAccessible(true);
                field.set(object, null);
                continue;
            }
            field.setAccessible(true);
            ConfigName configName = field.getAnnotation(ConfigName.class);
            Comment comment = field.getAnnotation(Comment.class);
            DefinedType definedType = field.getAnnotation(DefinedType.class);
            String string = configName == null || configName.value().isEmpty() ? field.getName() : configName.value();
            CommentedConfigurationNode commentedConfigurationNode3 = commentedConfigurationNode.getNode(string);
            CommentedConfigurationNode commentedConfigurationNode4 = commentedConfigurationNode2.getNode(string);
            boolean bl2 = bl = commentedConfigurationNode3.isVirtual() || commentedConfigurationNode3.getValue() == null || field.getAnnotation(WipeOnReload.class) != null;
            if (comment != null) {
                commentedConfigurationNode4.setComment(comment.value());
            }
            Object object2 = field.get(object);
            if (types.contains(field.getType())) {
                if (bl) {
                    if (definedType == null) {
                        commentedConfigurationNode4.setValue(object2);
                        continue;
                    }
                    try {
                        Field field2 = field.getDeclaringClass().getDeclaredField(field.getName() + "Token");
                        field2.setAccessible(true);
                        commentedConfigurationNode4.setValue((TypeToken)field2.get(object), object2);
                    } catch (NoSuchFieldException | ObjectMappingException exception) {
                        FactionsPlugin.getInstance().getLogger().severe("Failed horrifically to handle " + string);
                    }
                    continue;
                }
                try {
                    if (Set.class.isAssignableFrom(field.getType()) && List.class.isAssignableFrom(commentedConfigurationNode3.getValue().getClass())) {
                        field.set(object, new HashSet((List)commentedConfigurationNode3.getValue()));
                    } else {
                        field.set(object, commentedConfigurationNode3.getValue());
                    }
                    commentedConfigurationNode4.setValue(commentedConfigurationNode3.getValue());
                } catch (IllegalArgumentException illegalArgumentException) {
                    FactionsPlugin.getInstance().getLogger().severe("Found incorrect type for " + Loader.getNodeName(commentedConfigurationNode3.getPath()) + ": Expected " + String.valueOf(field.getType()) + ", found " + String.valueOf(commentedConfigurationNode3.getValue().getClass()));
                    field.set(object, object2);
                }
                continue;
            }
            if (object2 == null) {
                commentedConfigurationNode3.setValue(null);
                commentedConfigurationNode4.setValue(null);
                continue;
            }
            Loader.loadNode(commentedConfigurationNode3, commentedConfigurationNode4, object2);
        }
    }

    private static List<Field> getFields(Class<?> clazz) {
        return Loader.getFields(new ArrayList<Field>(), clazz);
    }

    private static List<Field> getFields(List<Field> list, Class<?> clazz) {
        list.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            Loader.getFields(list, clazz.getSuperclass());
        }
        return list;
    }

    private static String getNodeName(Object[] objectArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object object : objectArray) {
            if (object == null) continue;
            stringBuilder.append(object).append('.');
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    static {
        types.add(Boolean.TYPE);
        types.add(Byte.TYPE);
        types.add(Character.TYPE);
        types.add(Double.TYPE);
        types.add(Float.TYPE);
        types.add(Integer.TYPE);
        types.add(Long.TYPE);
        types.add(Short.TYPE);
        types.add(List.class);
        types.add(Map.class);
        types.add(Set.class);
        types.add(String.class);
    }
}

