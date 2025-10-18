/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigObject;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigImpl;
import moss.factions.shade.com.typesafe.config.impl.ConfigString;
import moss.factions.shade.com.typesafe.config.impl.FromMapMode;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.ResolveStatus;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigObject;

final class PropertiesParser {
    PropertiesParser() {
    }

    static AbstractConfigObject parse(Reader reader, ConfigOrigin configOrigin) {
        Properties properties = new Properties();
        properties.load(reader);
        return PropertiesParser.fromProperties(configOrigin, properties);
    }

    static String lastElement(String string) {
        int n = string.lastIndexOf(46);
        if (n < 0) {
            return string;
        }
        return string.substring(n + 1);
    }

    static String exceptLastElement(String string) {
        int n = string.lastIndexOf(46);
        if (n < 0) {
            return null;
        }
        return string.substring(0, n);
    }

    static Path pathFromPropertyKey(String string) {
        String string2 = PropertiesParser.lastElement(string);
        String string3 = PropertiesParser.exceptLastElement(string);
        Path path = new Path(string2, null);
        while (string3 != null) {
            string2 = PropertiesParser.lastElement(string3);
            string3 = PropertiesParser.exceptLastElement(string3);
            path = new Path(string2, path);
        }
        return path;
    }

    static AbstractConfigObject fromProperties(ConfigOrigin configOrigin, Properties properties) {
        return PropertiesParser.fromEntrySet(configOrigin, properties.entrySet());
    }

    private static <K, V> AbstractConfigObject fromEntrySet(ConfigOrigin configOrigin, Set<Map.Entry<K, V>> set) {
        Map<Path, Object> map = PropertiesParser.getPathMap(set);
        return PropertiesParser.fromPathMap(configOrigin, map, true);
    }

    private static <K, V> Map<Path, Object> getPathMap(Set<Map.Entry<K, V>> set) {
        HashMap<Path, Object> hashMap = new HashMap<Path, Object>();
        for (Map.Entry<K, V> entry : set) {
            K k = entry.getKey();
            if (!(k instanceof String)) continue;
            Path path = PropertiesParser.pathFromPropertyKey((String)k);
            hashMap.put(path, entry.getValue());
        }
        return hashMap;
    }

    static AbstractConfigObject fromStringMap(ConfigOrigin configOrigin, Map<String, String> map) {
        return PropertiesParser.fromEntrySet(configOrigin, map.entrySet());
    }

    static AbstractConfigObject fromPathMap(ConfigOrigin configOrigin, Map<?, ?> map) {
        HashMap<Path, Object> hashMap = new HashMap<Path, Object>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object obj = entry.getKey();
            if (!(obj instanceof String)) {
                throw new ConfigException.BugOrBroken("Map has a non-string as a key, expecting a path expression as a String");
            }
            Path path = Path.newPath((String)obj);
            hashMap.put(path, entry.getValue());
        }
        return PropertiesParser.fromPathMap(configOrigin, hashMap, false);
    }

    private static AbstractConfigObject fromPathMap(ConfigOrigin configOrigin, Map<Path, Object> map, boolean bl) {
        AbstractConfigValue abstractConfigValue;
        Object object;
        Object object2;
        HashMap hashMap;
        Object object3;
        Object object4;
        HashSet<Path> hashSet = new HashSet<Path>();
        HashSet<Path> hashSet2 = new HashSet<Path>();
        for (Path object62 : map.keySet()) {
            hashSet2.add(object62);
            for (object4 = object62.parent(); object4 != null; object4 = ((Path)object4).parent()) {
                hashSet.add((Path)object4);
            }
        }
        if (bl) {
            hashSet2.removeAll(hashSet);
        } else {
            for (Path path : hashSet2) {
                if (!hashSet.contains(path)) continue;
                throw new ConfigException.BugOrBroken("In the map, path '" + path.render() + "' occurs as both the parent object of a value and as a value. Because Map has no defined ordering, this is a broken situation.");
            }
        }
        HashMap hashMap2 = new HashMap();
        HashMap hashMap3 = new HashMap();
        for (Path path : hashSet) {
            object3 = new HashMap();
            hashMap3.put(path, object3);
        }
        for (Path path : hashSet2) {
            object3 = path.parent();
            hashMap = object3 != null ? (Map)hashMap3.get(object3) : hashMap2;
            object2 = path.last();
            object = map.get(path);
            abstractConfigValue = bl ? (object instanceof String ? new ConfigString.Quoted(configOrigin, (String)object) : null) : ConfigImpl.fromAnyRef(map.get(path), configOrigin, FromMapMode.KEYS_ARE_PATHS);
            if (abstractConfigValue == null) continue;
            hashMap.put(object2, abstractConfigValue);
        }
        object4 = new ArrayList();
        object4.addAll(hashSet);
        Collections.sort(object4, new Comparator<Path>(){

            @Override
            public int compare(Path path, Path path2) {
                return path2.length() - path.length();
            }
        });
        Iterator iterator = object4.iterator();
        while (iterator.hasNext()) {
            object3 = (Path)iterator.next();
            hashMap = (Map)hashMap3.get(object3);
            object2 = ((Path)object3).parent();
            object = object2 != null ? (Map)hashMap3.get(object2) : hashMap2;
            abstractConfigValue = new SimpleConfigObject(configOrigin, hashMap, ResolveStatus.RESOLVED, false);
            object.put(((Path)object3).last(), abstractConfigValue);
        }
        return new SimpleConfigObject(configOrigin, hashMap2, ResolveStatus.RESOLVED, false);
    }
}

