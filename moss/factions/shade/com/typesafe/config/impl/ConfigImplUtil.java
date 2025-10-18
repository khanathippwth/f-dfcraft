/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.DataOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigSyntax;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.SerializedConfigValue;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigOrigin;

public final class ConfigImplUtil {
    static boolean equalsHandlingNull(Object object, Object object2) {
        if (object == null && object2 != null) {
            return false;
        }
        if (object != null && object2 == null) {
            return false;
        }
        if (object == object2) {
            return true;
        }
        return object.equals(object2);
    }

    static boolean isC0Control(int n) {
        return n >= 0 && n <= 31;
    }

    public static String renderJsonString(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('\"');
        block9: for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            switch (c) {
                case '\"': {
                    stringBuilder.append("\\\"");
                    continue block9;
                }
                case '\\': {
                    stringBuilder.append("\\\\");
                    continue block9;
                }
                case '\n': {
                    stringBuilder.append("\\n");
                    continue block9;
                }
                case '\b': {
                    stringBuilder.append("\\b");
                    continue block9;
                }
                case '\f': {
                    stringBuilder.append("\\f");
                    continue block9;
                }
                case '\r': {
                    stringBuilder.append("\\r");
                    continue block9;
                }
                case '\t': {
                    stringBuilder.append("\\t");
                    continue block9;
                }
                default: {
                    if (ConfigImplUtil.isC0Control(c)) {
                        stringBuilder.append(String.format("\\u%04x", c));
                        continue block9;
                    }
                    stringBuilder.append(c);
                }
            }
        }
        stringBuilder.append('\"');
        return stringBuilder.toString();
    }

    static String renderStringUnquotedIfPossible(String string) {
        if (string.length() == 0) {
            return ConfigImplUtil.renderJsonString(string);
        }
        int n = string.codePointAt(0);
        if (Character.isDigit(n) || n == 45) {
            return ConfigImplUtil.renderJsonString(string);
        }
        if (string.startsWith("include") || string.startsWith("true") || string.startsWith("false") || string.startsWith("null") || string.contains("//")) {
            return ConfigImplUtil.renderJsonString(string);
        }
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (Character.isLetter(c) || Character.isDigit(c) || c == '-') continue;
            return ConfigImplUtil.renderJsonString(string);
        }
        return string;
    }

    static boolean isWhitespace(int n) {
        switch (n) {
            case 10: 
            case 32: 
            case 160: 
            case 8199: 
            case 8239: 
            case 65279: {
                return true;
            }
        }
        return Character.isWhitespace(n);
    }

    public static String unicodeTrim(String string) {
        int n;
        int n2;
        int n3 = string.length();
        if (n3 == 0) {
            return string;
        }
        int n4 = 0;
        while (n4 < n3) {
            n2 = string.charAt(n4);
            if (n2 == 32 || n2 == 10) {
                ++n4;
                continue;
            }
            n = string.codePointAt(n4);
            if (!ConfigImplUtil.isWhitespace(n)) break;
            n4 += Character.charCount(n);
        }
        n2 = n3;
        while (n2 > n4) {
            int n5;
            int n6;
            n = string.charAt(n2 - 1);
            if (n == 32 || n == 10) {
                --n2;
                continue;
            }
            if (Character.isLowSurrogate((char)n)) {
                n6 = string.codePointAt(n2 - 2);
                n5 = 2;
            } else {
                n6 = string.codePointAt(n2 - 1);
                n5 = 1;
            }
            if (!ConfigImplUtil.isWhitespace(n6)) break;
            n2 -= n5;
        }
        return string.substring(n4, n2);
    }

    public static ConfigException extractInitializerError(ExceptionInInitializerError exceptionInInitializerError) {
        Throwable throwable = exceptionInInitializerError.getCause();
        if (throwable != null && throwable instanceof ConfigException) {
            return (ConfigException)throwable;
        }
        throw exceptionInInitializerError;
    }

    static File urlToFile(URL uRL) {
        try {
            return new File(uRL.toURI());
        } catch (URISyntaxException uRISyntaxException) {
            return new File(uRL.getPath());
        } catch (IllegalArgumentException illegalArgumentException) {
            return new File(uRL.getPath());
        }
    }

    public static String joinPath(String ... stringArray) {
        return new Path(stringArray).render();
    }

    public static String joinPath(List<String> list) {
        return ConfigImplUtil.joinPath(list.toArray(new String[0]));
    }

    public static List<String> splitPath(String string) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Path path = Path.newPath(string); path != null; path = path.remainder()) {
            arrayList.add(path.first());
        }
        return arrayList;
    }

    public static ConfigOrigin readOrigin(ObjectInputStream objectInputStream) {
        return SerializedConfigValue.readOrigin(objectInputStream, null);
    }

    public static void writeOrigin(ObjectOutputStream objectOutputStream, ConfigOrigin configOrigin) {
        SerializedConfigValue.writeOrigin(new DataOutputStream(objectOutputStream), (SimpleConfigOrigin)configOrigin, null);
    }

    static String toCamelCase(String string) {
        String[] stringArray = string.split("-+");
        StringBuilder stringBuilder = new StringBuilder(string.length());
        for (String string2 : stringArray) {
            if (stringBuilder.length() == 0) {
                stringBuilder.append(string2);
                continue;
            }
            stringBuilder.append(string2.substring(0, 1).toUpperCase());
            stringBuilder.append(string2.substring(1));
        }
        return stringBuilder.toString();
    }

    private static char underscoreMappings(int n) {
        switch (n) {
            case 1: {
                return '.';
            }
            case 2: {
                return '-';
            }
            case 3: {
                return '_';
            }
        }
        return '\u0000';
    }

    static String envVariableAsProperty(String string, String string2) {
        StringBuilder stringBuilder = new StringBuilder();
        String string3 = string.substring(string2.length(), string.length());
        int n = 0;
        for (char c : string3.toCharArray()) {
            if (c == '_') {
                ++n;
                continue;
            }
            if (n > 0 && n < 4) {
                stringBuilder.append(ConfigImplUtil.underscoreMappings(n));
            } else if (n > 3) {
                throw new ConfigException.BadPath(string, "Environment variable contains an un-mapped number of underscores.");
            }
            n = 0;
            stringBuilder.append(c);
        }
        if (n > 0 && n < 4) {
            stringBuilder.append(ConfigImplUtil.underscoreMappings(n));
        } else if (n > 3) {
            throw new ConfigException.BadPath(string, "Environment variable contains an un-mapped number of underscores.");
        }
        return stringBuilder.toString();
    }

    public static ConfigSyntax syntaxFromExtension(String string) {
        if (string == null) {
            return null;
        }
        if (string.endsWith(".json")) {
            return ConfigSyntax.JSON;
        }
        if (string.endsWith(".conf")) {
            return ConfigSyntax.CONF;
        }
        if (string.endsWith(".properties")) {
            return ConfigSyntax.PROPERTIES;
        }
        return null;
    }
}

