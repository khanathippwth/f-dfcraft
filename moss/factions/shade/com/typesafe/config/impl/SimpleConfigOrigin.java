/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigImplUtil;
import moss.factions.shade.com.typesafe.config.impl.OriginType;
import moss.factions.shade.com.typesafe.config.impl.SerializedConfigValue;

final class SimpleConfigOrigin
implements ConfigOrigin {
    private final String description;
    private final int lineNumber;
    private final int endLineNumber;
    private final OriginType originType;
    private final String urlOrNull;
    private final String resourceOrNull;
    private final List<String> commentsOrNull;
    static final String MERGE_OF_PREFIX = "merge of ";

    protected SimpleConfigOrigin(String string, int n, int n2, OriginType originType, String string2, String string3, List<String> list) {
        if (string == null) {
            throw new ConfigException.BugOrBroken("description may not be null");
        }
        this.description = string;
        this.lineNumber = n;
        this.endLineNumber = n2;
        this.originType = originType;
        this.urlOrNull = string2;
        this.resourceOrNull = string3;
        this.commentsOrNull = list;
    }

    static SimpleConfigOrigin newSimple(String string) {
        return new SimpleConfigOrigin(string, -1, -1, OriginType.GENERIC, null, null, null);
    }

    static SimpleConfigOrigin newFile(String string) {
        String string2;
        try {
            string2 = new File(string).toURI().toURL().toExternalForm();
        } catch (MalformedURLException malformedURLException) {
            string2 = null;
        }
        return new SimpleConfigOrigin(string, -1, -1, OriginType.FILE, string2, null, null);
    }

    static SimpleConfigOrigin newURL(URL uRL) {
        String string = uRL.toExternalForm();
        return new SimpleConfigOrigin(string, -1, -1, OriginType.URL, string, null, null);
    }

    static SimpleConfigOrigin newResource(String string, URL uRL) {
        String string2 = uRL != null ? string + " @ " + uRL.toExternalForm() : string;
        return new SimpleConfigOrigin(string2, -1, -1, OriginType.RESOURCE, uRL != null ? uRL.toExternalForm() : null, string, null);
    }

    static SimpleConfigOrigin newResource(String string) {
        return SimpleConfigOrigin.newResource(string, null);
    }

    @Override
    public SimpleConfigOrigin withLineNumber(int n) {
        if (n == this.lineNumber && n == this.endLineNumber) {
            return this;
        }
        return new SimpleConfigOrigin(this.description, n, n, this.originType, this.urlOrNull, this.resourceOrNull, this.commentsOrNull);
    }

    SimpleConfigOrigin addURL(URL uRL) {
        return new SimpleConfigOrigin(this.description, this.lineNumber, this.endLineNumber, this.originType, uRL != null ? uRL.toExternalForm() : null, this.resourceOrNull, this.commentsOrNull);
    }

    @Override
    public SimpleConfigOrigin withComments(List<String> list) {
        if (ConfigImplUtil.equalsHandlingNull(list, this.commentsOrNull)) {
            return this;
        }
        return new SimpleConfigOrigin(this.description, this.lineNumber, this.endLineNumber, this.originType, this.urlOrNull, this.resourceOrNull, list);
    }

    SimpleConfigOrigin prependComments(List<String> list) {
        if (ConfigImplUtil.equalsHandlingNull(list, this.commentsOrNull) || list == null) {
            return this;
        }
        if (this.commentsOrNull == null) {
            return this.withComments((List)list);
        }
        ArrayList<String> arrayList = new ArrayList<String>(list.size() + this.commentsOrNull.size());
        arrayList.addAll(list);
        arrayList.addAll(this.commentsOrNull);
        return this.withComments(arrayList);
    }

    SimpleConfigOrigin appendComments(List<String> list) {
        if (ConfigImplUtil.equalsHandlingNull(list, this.commentsOrNull) || list == null) {
            return this;
        }
        if (this.commentsOrNull == null) {
            return this.withComments((List)list);
        }
        ArrayList<String> arrayList = new ArrayList<String>(list.size() + this.commentsOrNull.size());
        arrayList.addAll(this.commentsOrNull);
        arrayList.addAll(list);
        return this.withComments(arrayList);
    }

    @Override
    public String description() {
        if (this.lineNumber < 0) {
            return this.description;
        }
        if (this.endLineNumber == this.lineNumber) {
            return this.description + ": " + this.lineNumber;
        }
        return this.description + ": " + this.lineNumber + "-" + this.endLineNumber;
    }

    public boolean equals(Object object) {
        if (object instanceof SimpleConfigOrigin) {
            SimpleConfigOrigin simpleConfigOrigin = (SimpleConfigOrigin)object;
            return this.description.equals(simpleConfigOrigin.description) && this.lineNumber == simpleConfigOrigin.lineNumber && this.endLineNumber == simpleConfigOrigin.endLineNumber && this.originType == simpleConfigOrigin.originType && ConfigImplUtil.equalsHandlingNull(this.urlOrNull, simpleConfigOrigin.urlOrNull) && ConfigImplUtil.equalsHandlingNull(this.resourceOrNull, simpleConfigOrigin.resourceOrNull);
        }
        return false;
    }

    public int hashCode() {
        int n = 41 * (41 + this.description.hashCode());
        n = 41 * (n + this.lineNumber);
        n = 41 * (n + this.endLineNumber);
        n = 41 * (n + this.originType.hashCode());
        if (this.urlOrNull != null) {
            n = 41 * (n + this.urlOrNull.hashCode());
        }
        if (this.resourceOrNull != null) {
            n = 41 * (n + this.resourceOrNull.hashCode());
        }
        return n;
    }

    public String toString() {
        return "ConfigOrigin(" + this.description + ")";
    }

    @Override
    public String filename() {
        if (this.originType == OriginType.FILE) {
            return this.description;
        }
        if (this.urlOrNull != null) {
            URL uRL;
            try {
                uRL = new URL(this.urlOrNull);
            } catch (MalformedURLException malformedURLException) {
                return null;
            }
            if (uRL.getProtocol().equals("file")) {
                return uRL.getFile();
            }
            return null;
        }
        return null;
    }

    @Override
    public URL url() {
        if (this.urlOrNull == null) {
            return null;
        }
        try {
            return new URL(this.urlOrNull);
        } catch (MalformedURLException malformedURLException) {
            return null;
        }
    }

    @Override
    public String resource() {
        return this.resourceOrNull;
    }

    @Override
    public int lineNumber() {
        return this.lineNumber;
    }

    @Override
    public List<String> comments() {
        if (this.commentsOrNull != null) {
            return Collections.unmodifiableList(this.commentsOrNull);
        }
        return Collections.emptyList();
    }

    private static SimpleConfigOrigin mergeTwo(SimpleConfigOrigin simpleConfigOrigin, SimpleConfigOrigin simpleConfigOrigin2) {
        List<String> list;
        String string;
        String string2;
        int n;
        int n2;
        String string3;
        OriginType originType = simpleConfigOrigin.originType == simpleConfigOrigin2.originType ? simpleConfigOrigin.originType : OriginType.GENERIC;
        String string4 = simpleConfigOrigin.description;
        String string5 = simpleConfigOrigin2.description;
        if (string4.startsWith(MERGE_OF_PREFIX)) {
            string4 = string4.substring(MERGE_OF_PREFIX.length());
        }
        if (string5.startsWith(MERGE_OF_PREFIX)) {
            string5 = string5.substring(MERGE_OF_PREFIX.length());
        }
        if (string4.equals(string5)) {
            string3 = string4;
            n2 = simpleConfigOrigin.lineNumber < 0 ? simpleConfigOrigin2.lineNumber : (simpleConfigOrigin2.lineNumber < 0 ? simpleConfigOrigin.lineNumber : Math.min(simpleConfigOrigin.lineNumber, simpleConfigOrigin2.lineNumber));
            n = Math.max(simpleConfigOrigin.endLineNumber, simpleConfigOrigin2.endLineNumber);
        } else {
            string2 = simpleConfigOrigin.description();
            string = simpleConfigOrigin2.description();
            if (string2.startsWith(MERGE_OF_PREFIX)) {
                string2 = string2.substring(MERGE_OF_PREFIX.length());
            }
            if (string.startsWith(MERGE_OF_PREFIX)) {
                string = string.substring(MERGE_OF_PREFIX.length());
            }
            string3 = MERGE_OF_PREFIX + string2 + "," + string;
            n2 = -1;
            n = -1;
        }
        string2 = ConfigImplUtil.equalsHandlingNull(simpleConfigOrigin.urlOrNull, simpleConfigOrigin2.urlOrNull) ? simpleConfigOrigin.urlOrNull : null;
        string = ConfigImplUtil.equalsHandlingNull(simpleConfigOrigin.resourceOrNull, simpleConfigOrigin2.resourceOrNull) ? simpleConfigOrigin.resourceOrNull : null;
        if (ConfigImplUtil.equalsHandlingNull(simpleConfigOrigin.commentsOrNull, simpleConfigOrigin2.commentsOrNull)) {
            list = simpleConfigOrigin.commentsOrNull;
        } else {
            list = new ArrayList<String>();
            if (simpleConfigOrigin.commentsOrNull != null) {
                list.addAll(simpleConfigOrigin.commentsOrNull);
            }
            if (simpleConfigOrigin2.commentsOrNull != null) {
                list.addAll(simpleConfigOrigin2.commentsOrNull);
            }
        }
        return new SimpleConfigOrigin(string3, n2, n, originType, string2, string, list);
    }

    private static int similarity(SimpleConfigOrigin simpleConfigOrigin, SimpleConfigOrigin simpleConfigOrigin2) {
        int n = 0;
        if (simpleConfigOrigin.originType == simpleConfigOrigin2.originType) {
            ++n;
        }
        if (simpleConfigOrigin.description.equals(simpleConfigOrigin2.description)) {
            ++n;
            if (simpleConfigOrigin.lineNumber == simpleConfigOrigin2.lineNumber) {
                ++n;
            }
            if (simpleConfigOrigin.endLineNumber == simpleConfigOrigin2.endLineNumber) {
                ++n;
            }
            if (ConfigImplUtil.equalsHandlingNull(simpleConfigOrigin.urlOrNull, simpleConfigOrigin2.urlOrNull)) {
                ++n;
            }
            if (ConfigImplUtil.equalsHandlingNull(simpleConfigOrigin.resourceOrNull, simpleConfigOrigin2.resourceOrNull)) {
                ++n;
            }
        }
        return n;
    }

    private static SimpleConfigOrigin mergeThree(SimpleConfigOrigin simpleConfigOrigin, SimpleConfigOrigin simpleConfigOrigin2, SimpleConfigOrigin simpleConfigOrigin3) {
        if (SimpleConfigOrigin.similarity(simpleConfigOrigin, simpleConfigOrigin2) >= SimpleConfigOrigin.similarity(simpleConfigOrigin2, simpleConfigOrigin3)) {
            return SimpleConfigOrigin.mergeTwo(SimpleConfigOrigin.mergeTwo(simpleConfigOrigin, simpleConfigOrigin2), simpleConfigOrigin3);
        }
        return SimpleConfigOrigin.mergeTwo(simpleConfigOrigin, SimpleConfigOrigin.mergeTwo(simpleConfigOrigin2, simpleConfigOrigin3));
    }

    static ConfigOrigin mergeOrigins(ConfigOrigin configOrigin, ConfigOrigin configOrigin2) {
        return SimpleConfigOrigin.mergeTwo((SimpleConfigOrigin)configOrigin, (SimpleConfigOrigin)configOrigin2);
    }

    static ConfigOrigin mergeOrigins(List<? extends AbstractConfigValue> list) {
        ArrayList<SimpleConfigOrigin> arrayList = new ArrayList<SimpleConfigOrigin>(list.size());
        for (AbstractConfigValue abstractConfigValue : list) {
            arrayList.add(abstractConfigValue.origin());
        }
        return SimpleConfigOrigin.mergeOrigins(arrayList);
    }

    static ConfigOrigin mergeOrigins(Collection<? extends ConfigOrigin> collection) {
        if (collection.isEmpty()) {
            throw new ConfigException.BugOrBroken("can't merge empty list of origins");
        }
        if (collection.size() == 1) {
            return collection.iterator().next();
        }
        if (collection.size() == 2) {
            Iterator<? extends ConfigOrigin> iterator = collection.iterator();
            return SimpleConfigOrigin.mergeTwo((SimpleConfigOrigin)iterator.next(), (SimpleConfigOrigin)iterator.next());
        }
        ArrayList<SimpleConfigOrigin> arrayList = new ArrayList<SimpleConfigOrigin>(collection.size());
        for (ConfigOrigin configOrigin : collection) {
            arrayList.add((SimpleConfigOrigin)configOrigin);
        }
        while (arrayList.size() > 2) {
            SimpleConfigOrigin simpleConfigOrigin = (SimpleConfigOrigin)arrayList.get(arrayList.size() - 1);
            arrayList.remove(arrayList.size() - 1);
            SimpleConfigOrigin simpleConfigOrigin2 = (SimpleConfigOrigin)arrayList.get(arrayList.size() - 1);
            arrayList.remove(arrayList.size() - 1);
            SimpleConfigOrigin simpleConfigOrigin3 = (SimpleConfigOrigin)arrayList.get(arrayList.size() - 1);
            arrayList.remove(arrayList.size() - 1);
            SimpleConfigOrigin simpleConfigOrigin4 = SimpleConfigOrigin.mergeThree(simpleConfigOrigin3, simpleConfigOrigin2, simpleConfigOrigin);
            arrayList.add(simpleConfigOrigin4);
        }
        return SimpleConfigOrigin.mergeOrigins(arrayList);
    }

    Map<SerializedConfigValue.SerializedField, Object> toFields() {
        EnumMap<SerializedConfigValue.SerializedField, Object> enumMap = new EnumMap<SerializedConfigValue.SerializedField, Object>(SerializedConfigValue.SerializedField.class);
        enumMap.put(SerializedConfigValue.SerializedField.ORIGIN_DESCRIPTION, this.description);
        if (this.lineNumber >= 0) {
            enumMap.put(SerializedConfigValue.SerializedField.ORIGIN_LINE_NUMBER, Integer.valueOf(this.lineNumber));
        }
        if (this.endLineNumber >= 0) {
            enumMap.put(SerializedConfigValue.SerializedField.ORIGIN_END_LINE_NUMBER, Integer.valueOf(this.endLineNumber));
        }
        enumMap.put(SerializedConfigValue.SerializedField.ORIGIN_TYPE, Integer.valueOf(this.originType.ordinal()));
        if (this.urlOrNull != null) {
            enumMap.put(SerializedConfigValue.SerializedField.ORIGIN_URL, this.urlOrNull);
        }
        if (this.resourceOrNull != null) {
            enumMap.put(SerializedConfigValue.SerializedField.ORIGIN_RESOURCE, this.resourceOrNull);
        }
        if (this.commentsOrNull != null) {
            enumMap.put(SerializedConfigValue.SerializedField.ORIGIN_COMMENTS, this.commentsOrNull);
        }
        return enumMap;
    }

    Map<SerializedConfigValue.SerializedField, Object> toFieldsDelta(SimpleConfigOrigin simpleConfigOrigin) {
        Map<SerializedConfigValue.SerializedField, Object> map = simpleConfigOrigin != null ? simpleConfigOrigin.toFields() : Collections.emptyMap();
        return SimpleConfigOrigin.fieldsDelta(map, this.toFields());
    }

    static Map<SerializedConfigValue.SerializedField, Object> fieldsDelta(Map<SerializedConfigValue.SerializedField, Object> map, Map<SerializedConfigValue.SerializedField, Object> map2) {
        EnumMap<SerializedConfigValue.SerializedField, Object> enumMap = new EnumMap<SerializedConfigValue.SerializedField, Object>(map2);
        for (Map.Entry<SerializedConfigValue.SerializedField, Object> entry : map.entrySet()) {
            SerializedConfigValue.SerializedField serializedField = entry.getKey();
            if (enumMap.containsKey((Object)serializedField) && ConfigImplUtil.equalsHandlingNull(entry.getValue(), enumMap.get((Object)serializedField))) {
                enumMap.remove((Object)serializedField);
                continue;
            }
            if (enumMap.containsKey((Object)serializedField)) continue;
            switch (serializedField) {
                case ORIGIN_DESCRIPTION: {
                    throw new ConfigException.BugOrBroken("origin missing description field? " + map2);
                }
                case ORIGIN_LINE_NUMBER: {
                    enumMap.put(SerializedConfigValue.SerializedField.ORIGIN_LINE_NUMBER, (Object)-1);
                    break;
                }
                case ORIGIN_END_LINE_NUMBER: {
                    enumMap.put(SerializedConfigValue.SerializedField.ORIGIN_END_LINE_NUMBER, (Object)-1);
                    break;
                }
                case ORIGIN_TYPE: {
                    throw new ConfigException.BugOrBroken("should always be an ORIGIN_TYPE field");
                }
                case ORIGIN_URL: {
                    enumMap.put(SerializedConfigValue.SerializedField.ORIGIN_NULL_URL, (Object)"");
                    break;
                }
                case ORIGIN_RESOURCE: {
                    enumMap.put(SerializedConfigValue.SerializedField.ORIGIN_NULL_RESOURCE, (Object)"");
                    break;
                }
                case ORIGIN_COMMENTS: {
                    enumMap.put(SerializedConfigValue.SerializedField.ORIGIN_NULL_COMMENTS, (Object)"");
                    break;
                }
                case ORIGIN_NULL_URL: 
                case ORIGIN_NULL_RESOURCE: 
                case ORIGIN_NULL_COMMENTS: {
                    throw new ConfigException.BugOrBroken("computing delta, base object should not contain " + (Object)((Object)serializedField) + " " + map);
                }
                case END_MARKER: 
                case ROOT_VALUE: 
                case ROOT_WAS_CONFIG: 
                case UNKNOWN: 
                case VALUE_DATA: 
                case VALUE_ORIGIN: {
                    throw new ConfigException.BugOrBroken("should not appear here: " + (Object)((Object)serializedField));
                }
            }
        }
        return enumMap;
    }

    static SimpleConfigOrigin fromFields(Map<SerializedConfigValue.SerializedField, Object> map) {
        if (map.isEmpty()) {
            return null;
        }
        String string = (String)map.get((Object)SerializedConfigValue.SerializedField.ORIGIN_DESCRIPTION);
        Integer n = (Integer)map.get((Object)SerializedConfigValue.SerializedField.ORIGIN_LINE_NUMBER);
        Integer n2 = (Integer)map.get((Object)SerializedConfigValue.SerializedField.ORIGIN_END_LINE_NUMBER);
        Number number = (Number)map.get((Object)SerializedConfigValue.SerializedField.ORIGIN_TYPE);
        if (number == null) {
            throw new IOException("Missing ORIGIN_TYPE field");
        }
        OriginType originType = OriginType.values()[number.byteValue()];
        String string2 = (String)map.get((Object)SerializedConfigValue.SerializedField.ORIGIN_URL);
        String string3 = (String)map.get((Object)SerializedConfigValue.SerializedField.ORIGIN_RESOURCE);
        List list = (List)map.get((Object)SerializedConfigValue.SerializedField.ORIGIN_COMMENTS);
        if (originType == OriginType.RESOURCE && string3 == null) {
            string3 = string;
        }
        return new SimpleConfigOrigin(string, n != null ? n : -1, n2 != null ? n2 : -1, originType, string2, string3, list);
    }

    static Map<SerializedConfigValue.SerializedField, Object> applyFieldsDelta(Map<SerializedConfigValue.SerializedField, Object> map, Map<SerializedConfigValue.SerializedField, Object> map2) {
        EnumMap<SerializedConfigValue.SerializedField, Object> enumMap = new EnumMap<SerializedConfigValue.SerializedField, Object>(map2);
        for (Map.Entry<SerializedConfigValue.SerializedField, Object> entry : map.entrySet()) {
            SerializedConfigValue.SerializedField serializedField = entry.getKey();
            if (map2.containsKey((Object)serializedField)) continue;
            switch (serializedField) {
                case ORIGIN_DESCRIPTION: {
                    enumMap.put(serializedField, map.get((Object)serializedField));
                    break;
                }
                case ORIGIN_URL: {
                    if (map2.containsKey((Object)SerializedConfigValue.SerializedField.ORIGIN_NULL_URL)) {
                        enumMap.remove((Object)SerializedConfigValue.SerializedField.ORIGIN_NULL_URL);
                        break;
                    }
                    enumMap.put(serializedField, map.get((Object)serializedField));
                    break;
                }
                case ORIGIN_RESOURCE: {
                    if (map2.containsKey((Object)SerializedConfigValue.SerializedField.ORIGIN_NULL_RESOURCE)) {
                        enumMap.remove((Object)SerializedConfigValue.SerializedField.ORIGIN_NULL_RESOURCE);
                        break;
                    }
                    enumMap.put(serializedField, map.get((Object)serializedField));
                    break;
                }
                case ORIGIN_COMMENTS: {
                    if (map2.containsKey((Object)SerializedConfigValue.SerializedField.ORIGIN_NULL_COMMENTS)) {
                        enumMap.remove((Object)SerializedConfigValue.SerializedField.ORIGIN_NULL_COMMENTS);
                        break;
                    }
                    enumMap.put(serializedField, map.get((Object)serializedField));
                    break;
                }
                case ORIGIN_NULL_URL: 
                case ORIGIN_NULL_RESOURCE: 
                case ORIGIN_NULL_COMMENTS: {
                    throw new ConfigException.BugOrBroken("applying fields, base object should not contain " + (Object)((Object)serializedField) + " " + map);
                }
                case ORIGIN_LINE_NUMBER: 
                case ORIGIN_END_LINE_NUMBER: 
                case ORIGIN_TYPE: {
                    enumMap.put(serializedField, map.get((Object)serializedField));
                    break;
                }
                case END_MARKER: 
                case ROOT_VALUE: 
                case ROOT_WAS_CONFIG: 
                case UNKNOWN: 
                case VALUE_DATA: 
                case VALUE_ORIGIN: {
                    throw new ConfigException.BugOrBroken("should not appear here: " + (Object)((Object)serializedField));
                }
            }
        }
        return enumMap;
    }

    static SimpleConfigOrigin fromBase(SimpleConfigOrigin simpleConfigOrigin, Map<SerializedConfigValue.SerializedField, Object> map) {
        Map<SerializedConfigValue.SerializedField, Object> map2 = simpleConfigOrigin != null ? simpleConfigOrigin.toFields() : Collections.emptyMap();
        Map<SerializedConfigValue.SerializedField, Object> map3 = SimpleConfigOrigin.applyFieldsDelta(map2, map);
        return SimpleConfigOrigin.fromFields(map3);
    }
}

