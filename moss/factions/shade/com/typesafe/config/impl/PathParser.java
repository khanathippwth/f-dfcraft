/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigSyntax;
import moss.factions.shade.com.typesafe.config.ConfigValueType;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigImplUtil;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodePath;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.PathBuilder;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigOrigin;
import moss.factions.shade.com.typesafe.config.impl.Token;
import moss.factions.shade.com.typesafe.config.impl.Tokenizer;
import moss.factions.shade.com.typesafe.config.impl.Tokens;

final class PathParser {
    static ConfigOrigin apiOrigin = SimpleConfigOrigin.newSimple("path parameter");

    PathParser() {
    }

    static ConfigNodePath parsePathNode(String string) {
        return PathParser.parsePathNode(string, ConfigSyntax.CONF);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static ConfigNodePath parsePathNode(String string, ConfigSyntax configSyntax) {
        try (StringReader stringReader = new StringReader(string);){
            Iterator<Token> iterator = Tokenizer.tokenize(apiOrigin, stringReader, configSyntax);
            iterator.next();
            ConfigNodePath configNodePath = PathParser.parsePathNodeExpression(iterator, apiOrigin, string, configSyntax);
            return configNodePath;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static Path parsePath(String string) {
        Path path = PathParser.speculativeFastParsePath(string);
        if (path != null) {
            return path;
        }
        try (StringReader stringReader = new StringReader(string);){
            Iterator<Token> iterator = Tokenizer.tokenize(apiOrigin, stringReader, ConfigSyntax.CONF);
            iterator.next();
            Path path2 = PathParser.parsePathExpression(iterator, apiOrigin, string);
            return path2;
        }
    }

    protected static Path parsePathExpression(Iterator<Token> iterator, ConfigOrigin configOrigin) {
        return PathParser.parsePathExpression(iterator, configOrigin, null, null, ConfigSyntax.CONF);
    }

    protected static Path parsePathExpression(Iterator<Token> iterator, ConfigOrigin configOrigin, String string) {
        return PathParser.parsePathExpression(iterator, configOrigin, string, null, ConfigSyntax.CONF);
    }

    protected static ConfigNodePath parsePathNodeExpression(Iterator<Token> iterator, ConfigOrigin configOrigin) {
        return PathParser.parsePathNodeExpression(iterator, configOrigin, null, ConfigSyntax.CONF);
    }

    protected static ConfigNodePath parsePathNodeExpression(Iterator<Token> iterator, ConfigOrigin configOrigin, String string, ConfigSyntax configSyntax) {
        ArrayList<Token> arrayList = new ArrayList<Token>();
        Path path = PathParser.parsePathExpression(iterator, configOrigin, string, arrayList, configSyntax);
        return new ConfigNodePath(path, arrayList);
    }

    protected static Path parsePathExpression(Iterator<Token> iterator, ConfigOrigin configOrigin, String string, ArrayList<Token> arrayList, ConfigSyntax configSyntax) {
        Object object;
        ArrayList<Element> arrayList2 = new ArrayList<Element>();
        arrayList2.add(new Element("", false));
        if (!iterator.hasNext()) {
            throw new ConfigException.BadPath(configOrigin, string, "Expecting a field name or path here, but got nothing");
        }
        while (iterator.hasNext()) {
            Object object3;
            object = iterator.next();
            if (arrayList != null) {
                arrayList.add((Token)object);
            }
            if (Tokens.isIgnoredWhitespace((Token)object)) continue;
            if (Tokens.isValueWithType((Token)object, ConfigValueType.STRING)) {
                object3 = Tokens.getValue((Token)object);
                String string2 = ((AbstractConfigValue)object3).transformToString();
                PathParser.addPathText(arrayList2, true, string2);
                continue;
            }
            if (object == Tokens.END) continue;
            if (Tokens.isValue((Token)object)) {
                AbstractConfigValue abstractConfigValue = Tokens.getValue((Token)object);
                if (arrayList != null) {
                    arrayList.remove(arrayList.size() - 1);
                    arrayList.addAll(PathParser.splitTokenOnPeriod((Token)object, configSyntax));
                }
                object3 = abstractConfigValue.transformToString();
            } else if (Tokens.isUnquotedText((Token)object)) {
                if (arrayList != null) {
                    arrayList.remove(arrayList.size() - 1);
                    arrayList.addAll(PathParser.splitTokenOnPeriod((Token)object, configSyntax));
                }
                object3 = Tokens.getUnquotedText((Token)object);
            } else {
                throw new ConfigException.BadPath(configOrigin, string, "Token not allowed in path expression: " + object + " (you can double-quote this token if you really want it here)");
            }
            PathParser.addPathText(arrayList2, false, (String)object3);
        }
        object = new PathBuilder();
        for (Element element : arrayList2) {
            if (element.sb.length() == 0 && !element.canBeEmpty) {
                throw new ConfigException.BadPath(configOrigin, string, "path has a leading, trailing, or two adjacent period '.' (use quoted \"\" empty string if you want an empty element)");
            }
            ((PathBuilder)object).appendKey(element.sb.toString());
        }
        return ((PathBuilder)object).result();
    }

    private static Collection<Token> splitTokenOnPeriod(Token token, ConfigSyntax configSyntax) {
        String string = token.tokenText();
        if (string.equals(".")) {
            return Collections.singletonList(token);
        }
        String[] stringArray = string.split("\\.");
        ArrayList<Token> arrayList = new ArrayList<Token>();
        for (String string2 : stringArray) {
            if (configSyntax == ConfigSyntax.CONF) {
                arrayList.add(Tokens.newUnquotedText(token.origin(), string2));
            } else {
                arrayList.add(Tokens.newString(token.origin(), string2, "\"" + string2 + "\""));
            }
            arrayList.add(Tokens.newUnquotedText(token.origin(), "."));
        }
        if (string.charAt(string.length() - 1) != '.') {
            arrayList.remove(arrayList.size() - 1);
        }
        return arrayList;
    }

    private static void addPathText(List<Element> list, boolean bl, String string) {
        int n = bl ? -1 : string.indexOf(46);
        Element element = list.get(list.size() - 1);
        if (n < 0) {
            element.sb.append(string);
            if (bl && element.sb.length() == 0) {
                element.canBeEmpty = true;
            }
        } else {
            element.sb.append(string.substring(0, n));
            list.add(new Element("", false));
            PathParser.addPathText(list, false, string.substring(n + 1));
        }
    }

    private static boolean looksUnsafeForFastParser(String string) {
        boolean bl = true;
        int n = string.length();
        if (string.isEmpty()) {
            return true;
        }
        if (string.charAt(0) == '.') {
            return true;
        }
        if (string.charAt(n - 1) == '.') {
            return true;
        }
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_') {
                bl = false;
                continue;
            }
            if (c == '.') {
                if (bl) {
                    return true;
                }
                bl = true;
                continue;
            }
            if (c == '-') {
                if (!bl) continue;
                return true;
            }
            return true;
        }
        return bl;
    }

    private static Path fastPathBuild(Path path, String string, int n) {
        int n2 = string.lastIndexOf(46, n - 1);
        ArrayList<Token> arrayList = new ArrayList<Token>();
        arrayList.add(Tokens.newUnquotedText(null, string));
        Path path2 = new Path(string.substring(n2 + 1, n), path);
        if (n2 < 0) {
            return path2;
        }
        return PathParser.fastPathBuild(path2, string, n2);
    }

    private static Path speculativeFastParsePath(String string) {
        String string2 = ConfigImplUtil.unicodeTrim(string);
        if (PathParser.looksUnsafeForFastParser(string2)) {
            return null;
        }
        return PathParser.fastPathBuild(null, string2, string2.length());
    }

    static class Element {
        StringBuilder sb;
        boolean canBeEmpty;

        Element(String string, boolean bl) {
            this.canBeEmpty = bl;
            this.sb = new StringBuilder(string);
        }

        public String toString() {
            return "Element(" + this.sb.toString() + "," + this.canBeEmpty + ")";
        }
    }
}

