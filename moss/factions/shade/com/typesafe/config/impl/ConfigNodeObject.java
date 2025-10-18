/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import moss.factions.shade.com.typesafe.config.ConfigSyntax;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNode;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigNodeValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeComplexValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeField;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeInclude;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodePath;
import moss.factions.shade.com.typesafe.config.impl.ConfigNodeSingleToken;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.PathParser;
import moss.factions.shade.com.typesafe.config.impl.Token;
import moss.factions.shade.com.typesafe.config.impl.Tokens;

final class ConfigNodeObject
extends ConfigNodeComplexValue {
    ConfigNodeObject(Collection<AbstractConfigNode> collection) {
        super(collection);
    }

    @Override
    protected ConfigNodeObject newNode(Collection<AbstractConfigNode> collection) {
        return new ConfigNodeObject(collection);
    }

    public boolean hasValue(Path path) {
        for (AbstractConfigNode abstractConfigNode : this.children) {
            Path path2;
            ConfigNodeObject configNodeObject;
            if (!(abstractConfigNode instanceof ConfigNodeField)) continue;
            ConfigNodeField configNodeField = (ConfigNodeField)abstractConfigNode;
            Path path3 = configNodeField.path().value();
            if (path3.equals(path) || path3.startsWith(path)) {
                return true;
            }
            if (!path.startsWith(path3) || !(configNodeField.value() instanceof ConfigNodeObject) || !(configNodeObject = (ConfigNodeObject)configNodeField.value()).hasValue(path2 = path.subPath(path3.length()))) continue;
            return true;
        }
        return false;
    }

    protected ConfigNodeObject changeValueOnPath(Path path, AbstractConfigNodeValue abstractConfigNodeValue, ConfigSyntax configSyntax) {
        ArrayList<AbstractConfigNode> arrayList = new ArrayList<AbstractConfigNode>(this.children);
        boolean bl = false;
        AbstractConfigNodeValue abstractConfigNodeValue2 = abstractConfigNodeValue;
        for (int i = arrayList.size() - 1; i >= 0; --i) {
            Object object;
            Object object2;
            if (arrayList.get(i) instanceof ConfigNodeSingleToken) {
                object2 = ((ConfigNodeSingleToken)arrayList.get(i)).token();
                if (configSyntax != ConfigSyntax.JSON || bl || object2 != Tokens.COMMA) continue;
                arrayList.remove(i);
                continue;
            }
            if (!(arrayList.get(i) instanceof ConfigNodeField)) continue;
            object2 = (ConfigNodeField)arrayList.get(i);
            Path path2 = ((ConfigNodeField)object2).path().value();
            if (abstractConfigNodeValue2 == null && path2.equals(path) || path2.startsWith(path) && !path2.equals(path)) {
                arrayList.remove(i);
                for (int j = i; j < arrayList.size() && arrayList.get(j) instanceof ConfigNodeSingleToken && (Tokens.isIgnoredWhitespace((Token)(object = ((ConfigNodeSingleToken)arrayList.get(j)).token())) || object == Tokens.COMMA); ++j) {
                    arrayList.remove(j);
                    --j;
                }
                continue;
            }
            if (path2.equals(path)) {
                bl = true;
                object = i - 1 > 0 ? arrayList.get(i - 1) : null;
                AbstractConfigNodeValue abstractConfigNodeValue3 = abstractConfigNodeValue instanceof ConfigNodeComplexValue && object instanceof ConfigNodeSingleToken && Tokens.isIgnoredWhitespace(((ConfigNodeSingleToken)object).token()) ? ((ConfigNodeComplexValue)abstractConfigNodeValue).indentText((AbstractConfigNode)object) : abstractConfigNodeValue;
                arrayList.set(i, ((ConfigNodeField)object2).replaceValue(abstractConfigNodeValue3));
                abstractConfigNodeValue2 = null;
                continue;
            }
            if (path.startsWith(path2)) {
                bl = true;
                if (!(((ConfigNodeField)object2).value() instanceof ConfigNodeObject)) continue;
                Path path3 = path.subPath(path2.length());
                arrayList.set(i, ((ConfigNodeField)object2).replaceValue(((ConfigNodeObject)((ConfigNodeField)object2).value()).changeValueOnPath(path3, abstractConfigNodeValue2, configSyntax)));
                if (abstractConfigNodeValue2 == null || ((AbstractConfigNode)object2).equals(this.children.get(i))) continue;
                abstractConfigNodeValue2 = null;
                continue;
            }
            bl = true;
        }
        return new ConfigNodeObject(arrayList);
    }

    public ConfigNodeObject setValueOnPath(String string, AbstractConfigNodeValue abstractConfigNodeValue) {
        return this.setValueOnPath(string, abstractConfigNodeValue, ConfigSyntax.CONF);
    }

    public ConfigNodeObject setValueOnPath(String string, AbstractConfigNodeValue abstractConfigNodeValue, ConfigSyntax configSyntax) {
        ConfigNodePath configNodePath = PathParser.parsePathNode(string, configSyntax);
        return this.setValueOnPath(configNodePath, abstractConfigNodeValue, configSyntax);
    }

    private ConfigNodeObject setValueOnPath(ConfigNodePath configNodePath, AbstractConfigNodeValue abstractConfigNodeValue, ConfigSyntax configSyntax) {
        ConfigNodeObject configNodeObject = this.changeValueOnPath(configNodePath.value(), abstractConfigNodeValue, configSyntax);
        if (!configNodeObject.hasValue(configNodePath.value())) {
            return configNodeObject.addValueOnPath(configNodePath, abstractConfigNodeValue, configSyntax);
        }
        return configNodeObject;
    }

    private Collection<AbstractConfigNode> indentation() {
        boolean bl = false;
        ArrayList<AbstractConfigNode> arrayList = new ArrayList<AbstractConfigNode>();
        if (this.children.isEmpty()) {
            return arrayList;
        }
        for (int i = 0; i < this.children.size(); ++i) {
            if (!bl) {
                if (!(this.children.get(i) instanceof ConfigNodeSingleToken) || !Tokens.isNewline(((ConfigNodeSingleToken)this.children.get(i)).token())) continue;
                bl = true;
                arrayList.add(new ConfigNodeSingleToken(Tokens.newLine(null)));
                continue;
            }
            if (!(this.children.get(i) instanceof ConfigNodeSingleToken) || !Tokens.isIgnoredWhitespace(((ConfigNodeSingleToken)this.children.get(i)).token()) || i + 1 >= this.children.size() || !(this.children.get(i + 1) instanceof ConfigNodeField) && !(this.children.get(i + 1) instanceof ConfigNodeInclude)) continue;
            arrayList.add((AbstractConfigNode)this.children.get(i));
            return arrayList;
        }
        if (arrayList.isEmpty()) {
            arrayList.add(new ConfigNodeSingleToken(Tokens.newIgnoredWhitespace(null, " ")));
        } else {
            AbstractConfigNode abstractConfigNode = (AbstractConfigNode)this.children.get(this.children.size() - 1);
            if (abstractConfigNode instanceof ConfigNodeSingleToken && ((ConfigNodeSingleToken)abstractConfigNode).token() == Tokens.CLOSE_CURLY) {
                AbstractConfigNode abstractConfigNode2 = (AbstractConfigNode)this.children.get(this.children.size() - 2);
                String string = "";
                if (abstractConfigNode2 instanceof ConfigNodeSingleToken && Tokens.isIgnoredWhitespace(((ConfigNodeSingleToken)abstractConfigNode2).token())) {
                    string = ((ConfigNodeSingleToken)abstractConfigNode2).token().tokenText();
                }
                string = string + "  ";
                arrayList.add(new ConfigNodeSingleToken(Tokens.newIgnoredWhitespace(null, string)));
                return arrayList;
            }
        }
        return arrayList;
    }

    protected ConfigNodeObject addValueOnPath(ConfigNodePath configNodePath, AbstractConfigNodeValue abstractConfigNodeValue, ConfigSyntax configSyntax) {
        AbstractConfigNode abstractConfigNode;
        Object object;
        Object object2;
        int n;
        boolean bl;
        Path path = configNodePath.value();
        ArrayList<AbstractConfigNode> arrayList = new ArrayList<AbstractConfigNode>(this.children);
        ArrayList<AbstractConfigNode> arrayList2 = new ArrayList<AbstractConfigNode>(this.indentation());
        AbstractConfigNodeValue abstractConfigNodeValue2 = abstractConfigNodeValue instanceof ConfigNodeComplexValue && !arrayList2.isEmpty() ? ((ConfigNodeComplexValue)abstractConfigNodeValue).indentText(arrayList2.get(arrayList2.size() - 1)) : abstractConfigNodeValue;
        boolean bl2 = bl = arrayList2.size() <= 0 || !(arrayList2.get(0) instanceof ConfigNodeSingleToken) || !Tokens.isNewline(((ConfigNodeSingleToken)arrayList2.get(0)).token());
        if (path.length() > 1) {
            for (n = this.children.size() - 1; n >= 0; --n) {
                if (!(this.children.get(n) instanceof ConfigNodeField) || !path.startsWith((Path)(object2 = ((ConfigNodeField)(object = (ConfigNodeField)this.children.get(n))).path().value())) || !(((ConfigNodeField)object).value() instanceof ConfigNodeObject)) continue;
                ConfigNodePath configNodePath2 = configNodePath.subPath(((Path)object2).length());
                ConfigNodeObject configNodeObject = (ConfigNodeObject)((ConfigNodeField)object).value();
                arrayList.set(n, ((ConfigNodeField)object).replaceValue(configNodeObject.addValueOnPath(configNodePath2, abstractConfigNodeValue, configSyntax)));
                return new ConfigNodeObject(arrayList);
            }
        }
        n = !this.children.isEmpty() && this.children.get(0) instanceof ConfigNodeSingleToken && ((ConfigNodeSingleToken)this.children.get(0)).token() == Tokens.OPEN_CURLY ? 1 : 0;
        object = new ArrayList<AbstractConfigNode>();
        ((ArrayList)object).addAll(arrayList2);
        ((ArrayList)object).add(configNodePath.first());
        ((ArrayList)object).add(new ConfigNodeSingleToken(Tokens.newIgnoredWhitespace(null, " ")));
        ((ArrayList)object).add(new ConfigNodeSingleToken(Tokens.COLON));
        ((ArrayList)object).add(new ConfigNodeSingleToken(Tokens.newIgnoredWhitespace(null, " ")));
        if (path.length() == 1) {
            ((ArrayList)object).add(abstractConfigNodeValue2);
        } else {
            object2 = new ArrayList();
            ((ArrayList)object2).add(new ConfigNodeSingleToken(Tokens.OPEN_CURLY));
            if (arrayList2.isEmpty()) {
                ((ArrayList)object2).add(new ConfigNodeSingleToken(Tokens.newLine(null)));
            }
            ((ArrayList)object2).addAll(arrayList2);
            ((ArrayList)object2).add(new ConfigNodeSingleToken(Tokens.CLOSE_CURLY));
            abstractConfigNode = new ConfigNodeObject((Collection<AbstractConfigNode>)object2);
            ((ArrayList)object).add(abstractConfigNode.addValueOnPath(configNodePath.subPath(1), abstractConfigNodeValue2, configSyntax));
        }
        if (configSyntax == ConfigSyntax.JSON || n != 0 || bl) {
            for (int i = arrayList.size() - 1; i >= 0; --i) {
                if ((configSyntax == ConfigSyntax.JSON || bl) && arrayList.get(i) instanceof ConfigNodeField) {
                    if (i + 1 < arrayList.size() && arrayList.get(i + 1) instanceof ConfigNodeSingleToken && ((ConfigNodeSingleToken)arrayList.get(i + 1)).token() == Tokens.COMMA) break;
                    arrayList.add(i + 1, new ConfigNodeSingleToken(Tokens.COMMA));
                    break;
                }
                if (n == 0 || !(arrayList.get(i) instanceof ConfigNodeSingleToken) || ((ConfigNodeSingleToken)arrayList.get((int)i)).token != Tokens.CLOSE_CURLY) continue;
                abstractConfigNode = arrayList.get(i - 1);
                if (abstractConfigNode instanceof ConfigNodeSingleToken && Tokens.isNewline(((ConfigNodeSingleToken)abstractConfigNode).token())) {
                    arrayList.add(i - 1, new ConfigNodeField((Collection<AbstractConfigNode>)object));
                    --i;
                    continue;
                }
                if (abstractConfigNode instanceof ConfigNodeSingleToken && Tokens.isIgnoredWhitespace(((ConfigNodeSingleToken)abstractConfigNode).token())) {
                    AbstractConfigNode abstractConfigNode2 = arrayList.get(i - 2);
                    if (bl) {
                        arrayList.add(i - 1, new ConfigNodeField((Collection<AbstractConfigNode>)object));
                        --i;
                        continue;
                    }
                    if (abstractConfigNode2 instanceof ConfigNodeSingleToken && Tokens.isNewline(((ConfigNodeSingleToken)abstractConfigNode2).token())) {
                        arrayList.add(i - 2, new ConfigNodeField((Collection<AbstractConfigNode>)object));
                        i -= 2;
                        continue;
                    }
                    arrayList.add(i, new ConfigNodeField((Collection<AbstractConfigNode>)object));
                    continue;
                }
                arrayList.add(i, new ConfigNodeField((Collection<AbstractConfigNode>)object));
            }
        }
        if (n == 0) {
            if (!arrayList.isEmpty() && arrayList.get(arrayList.size() - 1) instanceof ConfigNodeSingleToken && Tokens.isNewline(((ConfigNodeSingleToken)arrayList.get(arrayList.size() - 1)).token())) {
                arrayList.add(arrayList.size() - 1, new ConfigNodeField((Collection<AbstractConfigNode>)object));
            } else {
                arrayList.add(new ConfigNodeField((Collection<AbstractConfigNode>)object));
            }
        }
        return new ConfigNodeObject(arrayList);
    }

    public ConfigNodeObject removeValueOnPath(String string, ConfigSyntax configSyntax) {
        Path path = PathParser.parsePathNode(string, configSyntax).value();
        return this.changeValueOnPath(path, null, configSyntax);
    }
}

