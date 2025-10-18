/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigParseOptions;
import moss.factions.shade.com.typesafe.config.ConfigSyntax;
import moss.factions.shade.com.typesafe.config.ConfigValueType;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNode;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNodeValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigImplUtil;
import moss.factions.shade.com.typesafe.config.impl.ConfigIncludeKind;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeArray;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeComment;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeComplexValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeConcatenation;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeField;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeInclude;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeObject;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodePath;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeRoot;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeSimpleValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeSingleToken;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.PathParser;
import moss.factions.shade.com.typesafe.config.impl.Token;
import moss.factions.shade.com.typesafe.config.impl.Tokens;

final class ConfigDocumentParser {
    ConfigDocumentParser() {
    }

    static ConfigNodeRoot parse(Iterator<Token> iterator, ConfigOrigin configOrigin, ConfigParseOptions configParseOptions) {
        ConfigSyntax configSyntax = configParseOptions.getSyntax() == null ? ConfigSyntax.CONF : configParseOptions.getSyntax();
        ParseContext parseContext = new ParseContext(configSyntax, configOrigin, iterator);
        return parseContext.parse();
    }

    static AbstractConfigNodeValue parseValue(Iterator<Token> iterator, ConfigOrigin configOrigin, ConfigParseOptions configParseOptions) {
        ConfigSyntax configSyntax = configParseOptions.getSyntax() == null ? ConfigSyntax.CONF : configParseOptions.getSyntax();
        ParseContext parseContext = new ParseContext(configSyntax, configOrigin, iterator);
        return parseContext.parseSingleValue();
    }

    private static final class ParseContext {
        private int lineNumber = 1;
        private final Stack<Token> buffer = new Stack();
        private final Iterator<Token> tokens;
        private final ConfigSyntax flavor;
        private final ConfigOrigin baseOrigin;
        int equalsCount;
        private final String ExpectingClosingParenthesisError = "expecting a close parentheses ')' here, not: ";

        ParseContext(ConfigSyntax configSyntax, ConfigOrigin configOrigin, Iterator<Token> iterator) {
            this.tokens = iterator;
            this.flavor = configSyntax;
            this.equalsCount = 0;
            this.baseOrigin = configOrigin;
        }

        private Token popToken() {
            if (this.buffer.isEmpty()) {
                return this.tokens.next();
            }
            return this.buffer.pop();
        }

        private Token nextToken() {
            Token token = this.popToken();
            if (this.flavor == ConfigSyntax.JSON) {
                if (Tokens.isUnquotedText(token) && !ParseContext.isUnquotedWhitespace(token)) {
                    throw this.parseError("Token not allowed in valid JSON: '" + Tokens.getUnquotedText(token) + "'");
                }
                if (Tokens.isSubstitution(token)) {
                    throw this.parseError("Substitutions (${} syntax) not allowed in JSON");
                }
            }
            return token;
        }

        private Token nextTokenCollectingWhitespace(Collection<AbstractConfigNode> collection) {
            Token token;
            while (true) {
                if (Tokens.isIgnoredWhitespace(token = this.nextToken()) || Tokens.isNewline(token) || ParseContext.isUnquotedWhitespace(token)) {
                    collection.add(new ConfigNodeSingleToken(token));
                    if (!Tokens.isNewline(token)) continue;
                    this.lineNumber = token.lineNumber() + 1;
                    continue;
                }
                if (!Tokens.isComment(token)) break;
                collection.add(new ConfigNodeComment(token));
            }
            int n = token.lineNumber();
            if (n >= 0) {
                this.lineNumber = n;
            }
            return token;
        }

        private void putBack(Token token) {
            this.buffer.push(token);
        }

        private boolean checkElementSeparator(Collection<AbstractConfigNode> collection) {
            if (this.flavor == ConfigSyntax.JSON) {
                Token token = this.nextTokenCollectingWhitespace(collection);
                if (token == Tokens.COMMA) {
                    collection.add(new ConfigNodeSingleToken(token));
                    return true;
                }
                this.putBack(token);
                return false;
            }
            boolean bl = false;
            Token token = this.nextToken();
            while (true) {
                if (Tokens.isIgnoredWhitespace(token) || ParseContext.isUnquotedWhitespace(token)) {
                    collection.add(new ConfigNodeSingleToken(token));
                } else if (Tokens.isComment(token)) {
                    collection.add(new ConfigNodeComment(token));
                } else if (Tokens.isNewline(token)) {
                    bl = true;
                    ++this.lineNumber;
                    collection.add(new ConfigNodeSingleToken(token));
                } else {
                    if (token == Tokens.COMMA) {
                        collection.add(new ConfigNodeSingleToken(token));
                        return true;
                    }
                    this.putBack(token);
                    return bl;
                }
                token = this.nextToken();
            }
        }

        private AbstractConfigNodeValue consolidateValues(Collection<AbstractConfigNode> collection) {
            AbstractConfigNodeValue abstractConfigNodeValue;
            if (this.flavor == ConfigSyntax.JSON) {
                return null;
            }
            ArrayList<AbstractConfigNode> arrayList = new ArrayList<AbstractConfigNode>();
            int n = 0;
            Token token = this.nextTokenCollectingWhitespace(collection);
            while (true) {
                abstractConfigNodeValue = null;
                if (Tokens.isIgnoredWhitespace(token)) {
                    arrayList.add(new ConfigNodeSingleToken(token));
                    token = this.nextToken();
                    continue;
                }
                if (!Tokens.isValue(token) && !Tokens.isUnquotedText(token) && !Tokens.isSubstitution(token) && token != Tokens.OPEN_CURLY && token != Tokens.OPEN_SQUARE) break;
                abstractConfigNodeValue = this.parseValue(token);
                ++n;
                if (abstractConfigNodeValue == null) {
                    throw new ConfigException.BugOrBroken("no value");
                }
                arrayList.add(abstractConfigNodeValue);
                token = this.nextToken();
            }
            this.putBack(token);
            if (n < 2) {
                abstractConfigNodeValue = null;
                for (AbstractConfigNode abstractConfigNode : arrayList) {
                    if (abstractConfigNode instanceof AbstractConfigNodeValue) {
                        abstractConfigNodeValue = (AbstractConfigNodeValue)abstractConfigNode;
                        continue;
                    }
                    if (abstractConfigNodeValue == null) {
                        collection.add(abstractConfigNode);
                        continue;
                    }
                    this.putBack(new ArrayList<Token>(abstractConfigNode.tokens()).get(0));
                }
                return abstractConfigNodeValue;
            }
            for (int i = arrayList.size() - 1; i >= 0 && arrayList.get(i) instanceof ConfigNodeSingleToken; --i) {
                this.putBack(((ConfigNodeSingleToken)arrayList.get(i)).token());
                arrayList.remove(i);
            }
            return new ConfigNodeConcatenation(arrayList);
        }

        private ConfigException parseError(String string) {
            return this.parseError(string, null);
        }

        private ConfigException parseError(String string, Throwable throwable) {
            return new ConfigException.Parse(this.baseOrigin.withLineNumber(this.lineNumber), string, throwable);
        }

        private String addQuoteSuggestion(String string, String string2) {
            return this.addQuoteSuggestion(null, this.equalsCount > 0, string, string2);
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        private String addQuoteSuggestion(Path path, boolean bl, String string, String string2) {
            String string3;
            String string4;
            String string5 = string4 = path != null ? path.render() : null;
            if (string.equals(Tokens.END.toString())) {
                if (string4 == null) return string2;
                string3 = string2 + " (if you intended '" + string4 + "' to be part of a value, instead of a key, try adding double quotes around the whole value";
            } else {
                string3 = string4 != null ? string2 + " (if you intended " + string + " to be part of the value for '" + string4 + "', try enclosing the value in double quotes" : string2 + " (if you intended " + string + " to be part of a key or string value, try enclosing the key or value in double quotes";
            }
            if (!bl) return string3 + ")";
            return string3 + ", or you may be able to rename the file .properties rather than .conf)";
        }

        private AbstractConfigNodeValue parseValue(Token token) {
            AbstractConfigNodeValue abstractConfigNodeValue = null;
            int n = this.equalsCount;
            if (Tokens.isValue(token) || Tokens.isUnquotedText(token) || Tokens.isSubstitution(token)) {
                abstractConfigNodeValue = new ConfigNodeSimpleValue(token);
            } else if (token == Tokens.OPEN_CURLY) {
                abstractConfigNodeValue = this.parseObject(true);
            } else if (token == Tokens.OPEN_SQUARE) {
                abstractConfigNodeValue = this.parseArray();
            } else {
                throw this.parseError(this.addQuoteSuggestion(token.toString(), "Expecting a value but got wrong token: " + token));
            }
            if (this.equalsCount != n) {
                throw new ConfigException.BugOrBroken("Bug in config parser: unbalanced equals count");
            }
            return abstractConfigNodeValue;
        }

        private ConfigNodePath parseKey(Token token) {
            if (this.flavor == ConfigSyntax.JSON) {
                if (Tokens.isValueWithType(token, ConfigValueType.STRING)) {
                    return PathParser.parsePathNodeExpression(Collections.singletonList(token).iterator(), this.baseOrigin.withLineNumber(this.lineNumber));
                }
                throw this.parseError("Expecting close brace } or a field name here, got " + token);
            }
            ArrayList<Token> arrayList = new ArrayList<Token>();
            Token token2 = token;
            while (Tokens.isValue(token2) || Tokens.isUnquotedText(token2)) {
                arrayList.add(token2);
                token2 = this.nextToken();
            }
            if (arrayList.isEmpty()) {
                throw this.parseError("expecting a close parentheses ')' here, not: " + token2);
            }
            this.putBack(token2);
            return PathParser.parsePathNodeExpression(arrayList.iterator(), this.baseOrigin.withLineNumber(this.lineNumber));
        }

        private static boolean isIncludeKeyword(Token token) {
            return Tokens.isUnquotedText(token) && Tokens.getUnquotedText(token).equals("include");
        }

        private static boolean isUnquotedWhitespace(Token token) {
            if (!Tokens.isUnquotedText(token)) {
                return false;
            }
            String string = Tokens.getUnquotedText(token);
            for (int i = 0; i < string.length(); ++i) {
                char c = string.charAt(i);
                if (ConfigImplUtil.isWhitespace(c)) continue;
                return false;
            }
            return true;
        }

        private boolean isKeyValueSeparatorToken(Token token) {
            if (this.flavor == ConfigSyntax.JSON) {
                return token == Tokens.COLON;
            }
            return token == Tokens.COLON || token == Tokens.EQUALS || token == Tokens.PLUS_EQUALS;
        }

        private ConfigNodeInclude parseInclude(ArrayList<AbstractConfigNode> arrayList) {
            Token token = this.nextTokenCollectingWhitespace(arrayList);
            if (Tokens.isUnquotedText(token)) {
                String string = Tokens.getUnquotedText(token);
                if (string.startsWith("required(")) {
                    String string2 = string.replaceFirst("required\\(", "");
                    if (string2.length() > 0) {
                        this.putBack(Tokens.newUnquotedText(token.origin(), string2));
                    }
                    arrayList.add(new ConfigNodeSingleToken(token));
                    ConfigNodeInclude configNodeInclude = this.parseIncludeResource(arrayList, true);
                    token = this.nextTokenCollectingWhitespace(arrayList);
                    if (!Tokens.isUnquotedText(token) || !Tokens.getUnquotedText(token).equals(")")) {
                        throw this.parseError("expecting a close parentheses ')' here, not: " + token);
                    }
                    return configNodeInclude;
                }
                this.putBack(token);
                return this.parseIncludeResource(arrayList, false);
            }
            this.putBack(token);
            return this.parseIncludeResource(arrayList, false);
        }

        private ConfigNodeInclude parseIncludeResource(ArrayList<AbstractConfigNode> arrayList, boolean bl) {
            Token token = this.nextTokenCollectingWhitespace(arrayList);
            if (Tokens.isUnquotedText(token)) {
                String string;
                ConfigIncludeKind configIncludeKind;
                String string2 = Tokens.getUnquotedText(token);
                if (string2.startsWith("url(")) {
                    configIncludeKind = ConfigIncludeKind.URL;
                    string = "url(";
                } else if (string2.startsWith("file(")) {
                    configIncludeKind = ConfigIncludeKind.FILE;
                    string = "file(";
                } else if (string2.startsWith("classpath(")) {
                    configIncludeKind = ConfigIncludeKind.CLASSPATH;
                    string = "classpath(";
                } else {
                    throw this.parseError("expecting include parameter to be quoted filename, file(), classpath(), or url(). No spaces are allowed before the open paren. Not expecting: " + token);
                }
                String string3 = string2.replaceFirst("[^(]*\\(", "");
                if (string3.length() > 0) {
                    this.putBack(Tokens.newUnquotedText(token.origin(), string3));
                }
                arrayList.add(new ConfigNodeSingleToken(token));
                token = this.nextTokenCollectingWhitespace(arrayList);
                if (!Tokens.isValueWithType(token, ConfigValueType.STRING)) {
                    throw this.parseError("expecting include " + string + ") parameter to be a quoted string, rather than: " + token);
                }
                arrayList.add(new ConfigNodeSimpleValue(token));
                token = this.nextTokenCollectingWhitespace(arrayList);
                if (Tokens.isUnquotedText(token) && Tokens.getUnquotedText(token).startsWith(")")) {
                    String string4 = Tokens.getUnquotedText(token).substring(1);
                    if (string4.length() > 0) {
                        this.putBack(Tokens.newUnquotedText(token.origin(), string4));
                    }
                } else {
                    throw this.parseError("expecting a close parentheses ')' here, not: " + token);
                }
                return new ConfigNodeInclude(arrayList, configIncludeKind, bl);
            }
            if (Tokens.isValueWithType(token, ConfigValueType.STRING)) {
                arrayList.add(new ConfigNodeSimpleValue(token));
                return new ConfigNodeInclude(arrayList, ConfigIncludeKind.HEURISTIC, bl);
            }
            throw this.parseError("include keyword is not followed by a quoted string, but by: " + token);
        }

        private ConfigNodeComplexValue parseObject(boolean bl) {
            ArrayList<AbstractConfigNode> arrayList;
            block24: {
                Token token;
                boolean bl2 = false;
                Path path = null;
                boolean bl3 = false;
                arrayList = new ArrayList<AbstractConfigNode>();
                HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();
                if (bl) {
                    arrayList.add(new ConfigNodeSingleToken(Tokens.OPEN_CURLY));
                }
                while (true) {
                    Object object;
                    if ((token = this.nextTokenCollectingWhitespace(arrayList)) == Tokens.CLOSE_CURLY) {
                        if (this.flavor == ConfigSyntax.JSON && bl2) {
                            throw this.parseError(this.addQuoteSuggestion(token.toString(), "expecting a field name after a comma, got a close brace } instead"));
                        }
                        if (!bl) {
                            throw this.parseError(this.addQuoteSuggestion(token.toString(), "unbalanced close brace '}' with no open brace"));
                        }
                        arrayList.add(new ConfigNodeSingleToken(Tokens.CLOSE_CURLY));
                        break block24;
                    }
                    if (token == Tokens.END && !bl) {
                        this.putBack(token);
                        break block24;
                    }
                    if (this.flavor != ConfigSyntax.JSON && ParseContext.isIncludeKeyword(token)) {
                        object = new ArrayList();
                        ((ArrayList)object).add(new ConfigNodeSingleToken(token));
                        arrayList.add(this.parseInclude((ArrayList<AbstractConfigNode>)object));
                        bl2 = false;
                    } else {
                        AbstractConfigNodeValue abstractConfigNodeValue;
                        ArrayList<AbstractConfigNode> arrayList2 = new ArrayList<AbstractConfigNode>();
                        object = token;
                        ConfigNodePath configNodePath = this.parseKey((Token)object);
                        arrayList2.add(configNodePath);
                        Token token2 = this.nextTokenCollectingWhitespace(arrayList2);
                        boolean bl4 = false;
                        if (this.flavor == ConfigSyntax.CONF && token2 == Tokens.OPEN_CURLY) {
                            abstractConfigNodeValue = this.parseValue(token2);
                        } else {
                            if (!this.isKeyValueSeparatorToken(token2)) {
                                throw this.parseError(this.addQuoteSuggestion(token2.toString(), "Key '" + configNodePath.render() + "' may not be followed by token: " + token2));
                            }
                            arrayList2.add(new ConfigNodeSingleToken(token2));
                            if (token2 == Tokens.EQUALS) {
                                bl4 = true;
                                ++this.equalsCount;
                            }
                            if ((abstractConfigNodeValue = this.consolidateValues(arrayList2)) == null) {
                                abstractConfigNodeValue = this.parseValue(this.nextTokenCollectingWhitespace(arrayList2));
                            }
                        }
                        arrayList2.add(abstractConfigNodeValue);
                        if (bl4) {
                            --this.equalsCount;
                        }
                        bl3 = bl4;
                        String string = configNodePath.value().first();
                        Path path2 = configNodePath.value().remainder();
                        if (path2 == null) {
                            Boolean bl5 = (Boolean)hashMap.get(string);
                            if (bl5 != null && this.flavor == ConfigSyntax.JSON) {
                                throw this.parseError("JSON does not allow duplicate fields: '" + string + "' was already seen");
                            }
                            hashMap.put(string, true);
                        } else {
                            if (this.flavor == ConfigSyntax.JSON) {
                                throw new ConfigException.BugOrBroken("somehow got multi-element path in JSON mode");
                            }
                            hashMap.put(string, true);
                        }
                        bl2 = false;
                        arrayList.add(new ConfigNodeField(arrayList2));
                    }
                    if (!this.checkElementSeparator(arrayList)) break;
                    bl2 = true;
                }
                token = this.nextTokenCollectingWhitespace(arrayList);
                if (token == Tokens.CLOSE_CURLY) {
                    if (!bl) {
                        throw this.parseError(this.addQuoteSuggestion(path, bl3, token.toString(), "unbalanced close brace '}' with no open brace"));
                    }
                    arrayList.add(new ConfigNodeSingleToken(token));
                } else {
                    if (bl) {
                        throw this.parseError(this.addQuoteSuggestion(path, bl3, token.toString(), "Expecting close brace } or a comma, got " + token));
                    }
                    if (token == Tokens.END) {
                        this.putBack(token);
                    } else {
                        throw this.parseError(this.addQuoteSuggestion(path, bl3, token.toString(), "Expecting end of input or a comma, got " + token));
                    }
                }
            }
            return new ConfigNodeObject(arrayList);
        }

        private ConfigNodeComplexValue parseArray() {
            Token token;
            ArrayList<AbstractConfigNode> arrayList = new ArrayList<AbstractConfigNode>();
            arrayList.add(new ConfigNodeSingleToken(Tokens.OPEN_SQUARE));
            AbstractConfigNodeValue abstractConfigNodeValue = this.consolidateValues(arrayList);
            if (abstractConfigNodeValue != null) {
                arrayList.add(abstractConfigNodeValue);
            } else {
                token = this.nextTokenCollectingWhitespace(arrayList);
                if (token == Tokens.CLOSE_SQUARE) {
                    arrayList.add(new ConfigNodeSingleToken(token));
                    return new ConfigNodeArray(arrayList);
                }
                if (Tokens.isValue(token) || token == Tokens.OPEN_CURLY || token == Tokens.OPEN_SQUARE || Tokens.isUnquotedText(token) || Tokens.isSubstitution(token)) {
                    abstractConfigNodeValue = this.parseValue(token);
                    arrayList.add(abstractConfigNodeValue);
                } else {
                    throw this.parseError("List should have ] or a first element after the open [, instead had token: " + token + " (if you want " + token + " to be part of a string value, then double-quote it)");
                }
            }
            while (true) {
                if (!this.checkElementSeparator(arrayList)) {
                    token = this.nextTokenCollectingWhitespace(arrayList);
                    if (token == Tokens.CLOSE_SQUARE) {
                        arrayList.add(new ConfigNodeSingleToken(token));
                        return new ConfigNodeArray(arrayList);
                    }
                    throw this.parseError("List should have ended with ] or had a comma, instead had token: " + token + " (if you want " + token + " to be part of a string value, then double-quote it)");
                }
                abstractConfigNodeValue = this.consolidateValues(arrayList);
                if (abstractConfigNodeValue != null) {
                    arrayList.add(abstractConfigNodeValue);
                    continue;
                }
                token = this.nextTokenCollectingWhitespace(arrayList);
                if (Tokens.isValue(token) || token == Tokens.OPEN_CURLY || token == Tokens.OPEN_SQUARE || Tokens.isUnquotedText(token) || Tokens.isSubstitution(token)) {
                    abstractConfigNodeValue = this.parseValue(token);
                    arrayList.add(abstractConfigNodeValue);
                    continue;
                }
                if (this.flavor == ConfigSyntax.JSON || token != Tokens.CLOSE_SQUARE) break;
                this.putBack(token);
            }
            throw this.parseError("List should have had new element after a comma, instead had token: " + token + " (if you want the comma or " + token + " to be part of a string value, then double-quote it)");
        }

        ConfigNodeRoot parse() {
            ArrayList<AbstractConfigNode> arrayList = new ArrayList<AbstractConfigNode>();
            Token token = this.nextToken();
            if (token != Tokens.START) {
                throw new ConfigException.BugOrBroken("token stream did not begin with START, had " + token);
            }
            token = this.nextTokenCollectingWhitespace(arrayList);
            AbstractConfigNodeValue abstractConfigNodeValue = null;
            boolean bl = false;
            if (token == Tokens.OPEN_CURLY || token == Tokens.OPEN_SQUARE) {
                abstractConfigNodeValue = this.parseValue(token);
            } else {
                if (this.flavor == ConfigSyntax.JSON) {
                    if (token == Tokens.END) {
                        throw this.parseError("Empty document");
                    }
                    throw this.parseError("Document must have an object or array at root, unexpected token: " + token);
                }
                this.putBack(token);
                bl = true;
                abstractConfigNodeValue = this.parseObject(false);
            }
            if (abstractConfigNodeValue instanceof ConfigNodeObject && bl) {
                arrayList.addAll(((ConfigNodeComplexValue)abstractConfigNodeValue).children());
            } else {
                arrayList.add(abstractConfigNodeValue);
            }
            token = this.nextTokenCollectingWhitespace(arrayList);
            if (token == Tokens.END) {
                if (bl) {
                    return new ConfigNodeRoot(Collections.singletonList(new ConfigNodeObject(arrayList)), this.baseOrigin);
                }
                return new ConfigNodeRoot(arrayList, this.baseOrigin);
            }
            throw this.parseError("Document has trailing tokens after first object or array: " + token);
        }

        AbstractConfigNodeValue parseSingleValue() {
            Token token = this.nextToken();
            if (token != Tokens.START) {
                throw new ConfigException.BugOrBroken("token stream did not begin with START, had " + token);
            }
            token = this.nextToken();
            if (Tokens.isIgnoredWhitespace(token) || Tokens.isNewline(token) || ParseContext.isUnquotedWhitespace(token) || Tokens.isComment(token)) {
                throw this.parseError("The value from withValueText cannot have leading or trailing newlines, whitespace, or comments");
            }
            if (token == Tokens.END) {
                throw this.parseError("Empty value");
            }
            if (this.flavor == ConfigSyntax.JSON) {
                AbstractConfigNodeValue abstractConfigNodeValue = this.parseValue(token);
                token = this.nextToken();
                if (token == Tokens.END) {
                    return abstractConfigNodeValue;
                }
                throw this.parseError("Parsing JSON and the value set in withValueText was either a concatenation or had trailing whitespace, newlines, or comments");
            }
            this.putBack(token);
            ArrayList<AbstractConfigNode> arrayList = new ArrayList<AbstractConfigNode>();
            AbstractConfigNodeValue abstractConfigNodeValue = this.consolidateValues(arrayList);
            token = this.nextToken();
            if (token == Tokens.END) {
                return abstractConfigNodeValue;
            }
            throw this.parseError("The value from withValueText cannot have leading or trailing newlines, whitespace, or comments");
        }
    }
}

