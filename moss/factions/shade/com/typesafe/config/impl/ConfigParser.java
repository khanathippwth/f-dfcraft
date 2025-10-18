/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigIncludeContext;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigParseOptions;
import moss.factions.shade.com.typesafe.config.ConfigSyntax;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNode;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNodeValue;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigObject;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigConcatenation;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeArray;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeComment;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeComplexValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeConcatenation;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeField;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeInclude;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeObject;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeRoot;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeSimpleValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeSingleToken;
import moss.factions.shade.com.typesafe.config.impl.ConfigReference;
import moss.factions.shade.com.typesafe.config.impl.FullIncluder;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.ResolveStatus;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigList;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigObject;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigOrigin;
import moss.factions.shade.com.typesafe.config.impl.SimpleIncluder;
import moss.factions.shade.com.typesafe.config.impl.SubstitutionExpression;
import moss.factions.shade.com.typesafe.config.impl.Token;
import moss.factions.shade.com.typesafe.config.impl.Tokens;

final class ConfigParser {
    ConfigParser() {
    }

    static AbstractConfigValue parse(ConfigNodeRoot configNodeRoot, ConfigOrigin configOrigin, ConfigParseOptions configParseOptions, ConfigIncludeContext configIncludeContext) {
        ParseContext parseContext = new ParseContext(configParseOptions.getSyntax(), configOrigin, configNodeRoot, SimpleIncluder.makeFull(configParseOptions.getIncluder()), configIncludeContext);
        return parseContext.parse();
    }

    private static final class ParseContext {
        private int lineNumber = 1;
        private final ConfigNodeRoot document;
        private final FullIncluder includer;
        private final ConfigIncludeContext includeContext;
        private final ConfigSyntax flavor;
        private final ConfigOrigin baseOrigin;
        private final LinkedList<Path> pathStack;
        int arrayCount;

        ParseContext(ConfigSyntax configSyntax, ConfigOrigin configOrigin, ConfigNodeRoot configNodeRoot, FullIncluder fullIncluder, ConfigIncludeContext configIncludeContext) {
            this.document = configNodeRoot;
            this.flavor = configSyntax;
            this.baseOrigin = configOrigin;
            this.includer = fullIncluder;
            this.includeContext = configIncludeContext;
            this.pathStack = new LinkedList();
            this.arrayCount = 0;
        }

        private AbstractConfigValue parseConcatenation(ConfigNodeConcatenation configNodeConcatenation) {
            if (this.flavor == ConfigSyntax.JSON) {
                throw new ConfigException.BugOrBroken("Found a concatenation node in JSON");
            }
            ArrayList<AbstractConfigValue> arrayList = new ArrayList<AbstractConfigValue>(configNodeConcatenation.children().size());
            for (AbstractConfigNode abstractConfigNode : configNodeConcatenation.children()) {
                AbstractConfigValue abstractConfigValue = null;
                if (!(abstractConfigNode instanceof AbstractConfigNodeValue)) continue;
                abstractConfigValue = this.parseValue((AbstractConfigNodeValue)abstractConfigNode, null);
                arrayList.add(abstractConfigValue);
            }
            return ConfigConcatenation.concatenate(arrayList);
        }

        private SimpleConfigOrigin lineOrigin() {
            return ((SimpleConfigOrigin)this.baseOrigin).withLineNumber(this.lineNumber);
        }

        private ConfigException parseError(String string) {
            return this.parseError(string, null);
        }

        private ConfigException parseError(String string, Throwable throwable) {
            return new ConfigException.Parse(this.lineOrigin(), string, throwable);
        }

        private Path fullCurrentPath() {
            if (this.pathStack.isEmpty()) {
                throw new ConfigException.BugOrBroken("Bug in parser; tried to get current path when at root");
            }
            return new Path(this.pathStack.descendingIterator());
        }

        private AbstractConfigValue parseValue(AbstractConfigNodeValue abstractConfigNodeValue, List<String> list) {
            AbstractConfigValue abstractConfigValue;
            int n = this.arrayCount;
            if (abstractConfigNodeValue instanceof ConfigNodeSimpleValue) {
                abstractConfigValue = ((ConfigNodeSimpleValue)abstractConfigNodeValue).value();
            } else if (abstractConfigNodeValue instanceof ConfigNodeObject) {
                abstractConfigValue = this.parseObject((ConfigNodeObject)abstractConfigNodeValue);
            } else if (abstractConfigNodeValue instanceof ConfigNodeArray) {
                abstractConfigValue = this.parseArray((ConfigNodeArray)abstractConfigNodeValue);
            } else if (abstractConfigNodeValue instanceof ConfigNodeConcatenation) {
                abstractConfigValue = this.parseConcatenation((ConfigNodeConcatenation)abstractConfigNodeValue);
            } else {
                throw this.parseError("Expecting a value but got wrong node type: " + abstractConfigNodeValue.getClass());
            }
            if (list != null && !list.isEmpty()) {
                abstractConfigValue = abstractConfigValue.withOrigin(abstractConfigValue.origin().prependComments(new ArrayList<String>(list)));
                list.clear();
            }
            if (this.arrayCount != n) {
                throw new ConfigException.BugOrBroken("Bug in config parser: unbalanced array count");
            }
            return abstractConfigValue;
        }

        private static AbstractConfigObject createValueUnderPath(Path path, AbstractConfigValue abstractConfigValue) {
            ArrayList<String> arrayList = new ArrayList<String>();
            String string = path.first();
            Path path2 = path.remainder();
            while (string != null) {
                arrayList.add(string);
                if (path2 == null) break;
                string = path2.first();
                path2 = path2.remainder();
            }
            ListIterator listIterator = arrayList.listIterator(arrayList.size());
            String string2 = (String)listIterator.previous();
            SimpleConfigObject simpleConfigObject = new SimpleConfigObject(abstractConfigValue.origin().withComments((List)null), Collections.singletonMap(string2, abstractConfigValue));
            while (listIterator.hasPrevious()) {
                Map<String, AbstractConfigValue> map = Collections.singletonMap(listIterator.previous(), simpleConfigObject);
                simpleConfigObject = new SimpleConfigObject(abstractConfigValue.origin().withComments((List)null), map);
            }
            return simpleConfigObject;
        }

        private void parseInclude(Map<String, AbstractConfigValue> map, ConfigNodeInclude configNodeInclude) {
            AbstractConfigObject abstractConfigObject;
            Iterator iterator;
            boolean bl = configNodeInclude.isRequired();
            ConfigIncludeContext configIncludeContext = this.includeContext.setParseOptions(this.includeContext.parseOptions().setAllowMissing(!bl));
            switch (configNodeInclude.kind()) {
                case URL: {
                    try {
                        iterator = new URL(configNodeInclude.name());
                    } catch (MalformedURLException malformedURLException) {
                        throw this.parseError("include url() specifies an invalid URL: " + configNodeInclude.name(), malformedURLException);
                    }
                    abstractConfigObject = (AbstractConfigObject)this.includer.includeURL(configIncludeContext, (URL)((Object)iterator));
                    break;
                }
                case FILE: {
                    abstractConfigObject = (AbstractConfigObject)this.includer.includeFile(configIncludeContext, new File(configNodeInclude.name()));
                    break;
                }
                case CLASSPATH: {
                    abstractConfigObject = (AbstractConfigObject)this.includer.includeResources(configIncludeContext, configNodeInclude.name());
                    break;
                }
                case HEURISTIC: {
                    abstractConfigObject = (AbstractConfigObject)this.includer.include(configIncludeContext, configNodeInclude.name());
                    break;
                }
                default: {
                    throw new ConfigException.BugOrBroken("should not be reached");
                }
            }
            if (this.arrayCount > 0 && abstractConfigObject.resolveStatus() != ResolveStatus.RESOLVED) {
                throw this.parseError("Due to current limitations of the config parser, when an include statement is nested inside a list value, ${} substitutions inside the included file cannot be resolved correctly. Either move the include outside of the list value or remove the ${} statements from the included file.");
            }
            if (!this.pathStack.isEmpty()) {
                iterator = this.fullCurrentPath();
                abstractConfigObject = abstractConfigObject.relativized((Path)((Object)iterator));
            }
            for (String string : abstractConfigObject.keySet()) {
                AbstractConfigValue abstractConfigValue = abstractConfigObject.get(string);
                AbstractConfigValue abstractConfigValue2 = map.get(string);
                if (abstractConfigValue2 != null) {
                    map.put(string, abstractConfigValue.withFallback(abstractConfigValue2));
                    continue;
                }
                map.put(string, abstractConfigValue);
            }
        }

        private AbstractConfigObject parseObject(ConfigNodeObject configNodeObject) {
            HashMap<String, AbstractConfigValue> hashMap = new HashMap<String, AbstractConfigValue>();
            SimpleConfigOrigin simpleConfigOrigin = this.lineOrigin();
            boolean bl = false;
            ArrayList<AbstractConfigNode> arrayList = new ArrayList<AbstractConfigNode>(configNodeObject.children());
            ArrayList<String> arrayList2 = new ArrayList<String>();
            for (int i = 0; i < arrayList.size(); ++i) {
                AbstractConfigValue abstractConfigValue;
                Object object;
                Object object2;
                AbstractConfigNode abstractConfigNode = arrayList.get(i);
                if (abstractConfigNode instanceof ConfigNodeComment) {
                    bl = false;
                    arrayList2.add(((ConfigNodeComment)abstractConfigNode).commentText());
                    continue;
                }
                if (abstractConfigNode instanceof ConfigNodeSingleToken && Tokens.isNewline(((ConfigNodeSingleToken)abstractConfigNode).token())) {
                    ++this.lineNumber;
                    if (bl) {
                        arrayList2.clear();
                    }
                    bl = true;
                    continue;
                }
                if (this.flavor != ConfigSyntax.JSON && abstractConfigNode instanceof ConfigNodeInclude) {
                    this.parseInclude(hashMap, (ConfigNodeInclude)abstractConfigNode);
                    bl = false;
                    continue;
                }
                if (!(abstractConfigNode instanceof ConfigNodeField)) continue;
                bl = false;
                Path path = ((ConfigNodeField)abstractConfigNode).path().value();
                arrayList2.addAll(((ConfigNodeField)abstractConfigNode).comments());
                this.pathStack.push(path);
                if (((ConfigNodeField)abstractConfigNode).separator() == Tokens.PLUS_EQUALS) {
                    if (this.arrayCount > 0) {
                        throw this.parseError("Due to current limitations of the config parser, += does not work nested inside a list. += expands to a ${} substitution and the path in ${} cannot currently refer to list elements. You might be able to move the += outside of the list and then refer to it from inside the list with ${}.");
                    }
                    ++this.arrayCount;
                }
                AbstractConfigNodeValue abstractConfigNodeValue = ((ConfigNodeField)abstractConfigNode).value();
                AbstractConfigValue abstractConfigValue2 = this.parseValue(abstractConfigNodeValue, arrayList2);
                if (((ConfigNodeField)abstractConfigNode).separator() == Tokens.PLUS_EQUALS) {
                    --this.arrayCount;
                    object2 = new ArrayList(2);
                    object = new ConfigReference(abstractConfigValue2.origin(), new SubstitutionExpression(this.fullCurrentPath(), true));
                    abstractConfigValue = new SimpleConfigList(abstractConfigValue2.origin(), Collections.singletonList(abstractConfigValue2));
                    object2.add(object);
                    object2.add(abstractConfigValue);
                    abstractConfigValue2 = ConfigConcatenation.concatenate((List<AbstractConfigValue>)object2);
                }
                if (i < arrayList.size() - 1) {
                    ++i;
                    while (i < arrayList.size()) {
                        if (arrayList.get(i) instanceof ConfigNodeComment) {
                            object2 = (ConfigNodeComment)arrayList.get(i);
                            abstractConfigValue2 = abstractConfigValue2.withOrigin(abstractConfigValue2.origin().appendComments(Collections.singletonList(((ConfigNodeComment)object2).commentText())));
                            break;
                        }
                        if (arrayList.get(i) instanceof ConfigNodeSingleToken) {
                            object2 = (ConfigNodeSingleToken)arrayList.get(i);
                            if (((ConfigNodeSingleToken)object2).token() != Tokens.COMMA && !Tokens.isIgnoredWhitespace(((ConfigNodeSingleToken)object2).token())) {
                                --i;
                                break;
                            }
                        } else {
                            --i;
                            break;
                        }
                        ++i;
                    }
                }
                this.pathStack.pop();
                object2 = path.first();
                object = path.remainder();
                if (object == null) {
                    abstractConfigValue = (AbstractConfigValue)hashMap.get(object2);
                    if (abstractConfigValue != null) {
                        if (this.flavor == ConfigSyntax.JSON) {
                            throw this.parseError("JSON does not allow duplicate fields: '" + (String)object2 + "' was already seen at " + abstractConfigValue.origin().description());
                        }
                        abstractConfigValue2 = abstractConfigValue2.withFallback(abstractConfigValue);
                    }
                    hashMap.put((String)object2, abstractConfigValue2);
                    continue;
                }
                if (this.flavor == ConfigSyntax.JSON) {
                    throw new ConfigException.BugOrBroken("somehow got multi-element path in JSON mode");
                }
                abstractConfigValue = ParseContext.createValueUnderPath((Path)object, abstractConfigValue2);
                AbstractConfigValue abstractConfigValue3 = (AbstractConfigValue)hashMap.get(object2);
                if (abstractConfigValue3 != null) {
                    abstractConfigValue = ((AbstractConfigObject)abstractConfigValue).withFallback(abstractConfigValue3);
                }
                hashMap.put((String)object2, abstractConfigValue);
            }
            return new SimpleConfigObject(simpleConfigOrigin, hashMap);
        }

        private SimpleConfigList parseArray(ConfigNodeArray configNodeArray) {
            ++this.arrayCount;
            SimpleConfigOrigin simpleConfigOrigin = this.lineOrigin();
            ArrayList<AbstractConfigValue> arrayList = new ArrayList<AbstractConfigValue>();
            boolean bl = false;
            ArrayList<String> arrayList2 = new ArrayList<String>();
            AbstractConfigValue abstractConfigValue = null;
            for (AbstractConfigNode abstractConfigNode : configNodeArray.children()) {
                if (abstractConfigNode instanceof ConfigNodeComment) {
                    arrayList2.add(((ConfigNodeComment)abstractConfigNode).commentText());
                    bl = false;
                    continue;
                }
                if (abstractConfigNode instanceof ConfigNodeSingleToken && Tokens.isNewline(((ConfigNodeSingleToken)abstractConfigNode).token())) {
                    ++this.lineNumber;
                    if (bl && abstractConfigValue == null) {
                        arrayList2.clear();
                    } else if (abstractConfigValue != null) {
                        arrayList.add(abstractConfigValue.withOrigin(abstractConfigValue.origin().appendComments(new ArrayList<String>(arrayList2))));
                        arrayList2.clear();
                        abstractConfigValue = null;
                    }
                    bl = true;
                    continue;
                }
                if (!(abstractConfigNode instanceof AbstractConfigNodeValue)) continue;
                bl = false;
                if (abstractConfigValue != null) {
                    arrayList.add(abstractConfigValue.withOrigin(abstractConfigValue.origin().appendComments(new ArrayList<String>(arrayList2))));
                    arrayList2.clear();
                }
                abstractConfigValue = this.parseValue((AbstractConfigNodeValue)abstractConfigNode, arrayList2);
            }
            if (abstractConfigValue != null) {
                arrayList.add(abstractConfigValue.withOrigin(abstractConfigValue.origin().appendComments(new ArrayList<String>(arrayList2))));
            }
            --this.arrayCount;
            return new SimpleConfigList(simpleConfigOrigin, arrayList);
        }

        AbstractConfigValue parse() {
            AbstractConfigValue abstractConfigValue = null;
            ArrayList<String> arrayList = new ArrayList<String>();
            boolean bl = false;
            for (AbstractConfigNode abstractConfigNode : this.document.children()) {
                if (abstractConfigNode instanceof ConfigNodeComment) {
                    arrayList.add(((ConfigNodeComment)abstractConfigNode).commentText());
                    bl = false;
                    continue;
                }
                if (abstractConfigNode instanceof ConfigNodeSingleToken) {
                    Token token = ((ConfigNodeSingleToken)abstractConfigNode).token();
                    if (!Tokens.isNewline(token)) continue;
                    ++this.lineNumber;
                    if (bl && abstractConfigValue == null) {
                        arrayList.clear();
                    } else if (abstractConfigValue != null) {
                        abstractConfigValue = abstractConfigValue.withOrigin(abstractConfigValue.origin().appendComments(new ArrayList<String>(arrayList)));
                        arrayList.clear();
                        break;
                    }
                    bl = true;
                    continue;
                }
                if (!(abstractConfigNode instanceof ConfigNodeComplexValue)) continue;
                abstractConfigValue = this.parseValue((ConfigNodeComplexValue)abstractConfigNode, arrayList);
                bl = false;
            }
            return abstractConfigValue;
        }
    }
}

