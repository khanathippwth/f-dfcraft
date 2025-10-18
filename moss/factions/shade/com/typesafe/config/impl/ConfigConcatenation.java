/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigObject;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigRenderOptions;
import moss.factions.shade.com.typesafe.config.ConfigValueType;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigImpl;
import moss.factions.shade.com.typesafe.config.impl.ConfigString;
import moss.factions.shade.com.typesafe.config.impl.Container;
import moss.factions.shade.com.typesafe.config.impl.DefaultTransformer;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.ResolveContext;
import moss.factions.shade.com.typesafe.config.impl.ResolveResult;
import moss.factions.shade.com.typesafe.config.impl.ResolveSource;
import moss.factions.shade.com.typesafe.config.impl.ResolveStatus;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigList;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigOrigin;
import moss.factions.shade.com.typesafe.config.impl.Unmergeable;

final class ConfigConcatenation
extends AbstractConfigValue
implements Unmergeable,
Container {
    private final List<AbstractConfigValue> pieces;

    ConfigConcatenation(ConfigOrigin configOrigin, List<AbstractConfigValue> list) {
        super(configOrigin);
        this.pieces = list;
        if (list.size() < 2) {
            throw new ConfigException.BugOrBroken("Created concatenation with less than 2 items: " + this);
        }
        boolean bl = false;
        for (AbstractConfigValue abstractConfigValue : list) {
            if (abstractConfigValue instanceof ConfigConcatenation) {
                throw new ConfigException.BugOrBroken("ConfigConcatenation should never be nested: " + this);
            }
            if (!(abstractConfigValue instanceof Unmergeable)) continue;
            bl = true;
        }
        if (!bl) {
            throw new ConfigException.BugOrBroken("Created concatenation without an unmergeable in it: " + this);
        }
    }

    private ConfigException.NotResolved notResolved() {
        return new ConfigException.NotResolved("need to Config#resolve(), see the API docs for Config#resolve(); substitution not resolved: " + this);
    }

    @Override
    public ConfigValueType valueType() {
        throw this.notResolved();
    }

    @Override
    public Object unwrapped() {
        throw this.notResolved();
    }

    @Override
    protected ConfigConcatenation newCopy(ConfigOrigin configOrigin) {
        return new ConfigConcatenation(configOrigin, this.pieces);
    }

    @Override
    protected boolean ignoresFallbacks() {
        return false;
    }

    public Collection<ConfigConcatenation> unmergedValues() {
        return Collections.singleton(this);
    }

    private static boolean isIgnoredWhitespace(AbstractConfigValue abstractConfigValue) {
        return abstractConfigValue instanceof ConfigString && !((ConfigString)abstractConfigValue).wasQuoted();
    }

    private static void join(ArrayList<AbstractConfigValue> arrayList, AbstractConfigValue abstractConfigValue) {
        AbstractConfigValue abstractConfigValue2 = arrayList.get(arrayList.size() - 1);
        AbstractConfigValue abstractConfigValue3 = abstractConfigValue;
        if (abstractConfigValue2 instanceof ConfigObject && abstractConfigValue3 instanceof SimpleConfigList) {
            abstractConfigValue2 = DefaultTransformer.transform(abstractConfigValue2, ConfigValueType.LIST);
        } else if (abstractConfigValue2 instanceof SimpleConfigList && abstractConfigValue3 instanceof ConfigObject) {
            abstractConfigValue3 = DefaultTransformer.transform(abstractConfigValue3, ConfigValueType.LIST);
        }
        AbstractConfigValue abstractConfigValue4 = null;
        if (abstractConfigValue2 instanceof ConfigObject && abstractConfigValue3 instanceof ConfigObject) {
            abstractConfigValue4 = abstractConfigValue3.withFallback(abstractConfigValue2);
        } else if (abstractConfigValue2 instanceof SimpleConfigList && abstractConfigValue3 instanceof SimpleConfigList) {
            abstractConfigValue4 = ((SimpleConfigList)abstractConfigValue2).concatenate((SimpleConfigList)abstractConfigValue3);
        } else if ((abstractConfigValue2 instanceof SimpleConfigList || abstractConfigValue2 instanceof ConfigObject) && ConfigConcatenation.isIgnoredWhitespace(abstractConfigValue3)) {
            abstractConfigValue4 = abstractConfigValue2;
        } else {
            if (abstractConfigValue2 instanceof ConfigConcatenation || abstractConfigValue3 instanceof ConfigConcatenation) {
                throw new ConfigException.BugOrBroken("unflattened ConfigConcatenation");
            }
            if (!(abstractConfigValue2 instanceof Unmergeable) && !(abstractConfigValue3 instanceof Unmergeable)) {
                String string = abstractConfigValue2.transformToString();
                String string2 = abstractConfigValue3.transformToString();
                if (string == null || string2 == null) {
                    throw new ConfigException.WrongType(abstractConfigValue2.origin(), "Cannot concatenate object or list with a non-object-or-list, " + abstractConfigValue2 + " and " + abstractConfigValue3 + " are not compatible");
                }
                ConfigOrigin configOrigin = SimpleConfigOrigin.mergeOrigins(abstractConfigValue2.origin(), abstractConfigValue3.origin());
                abstractConfigValue4 = new ConfigString.Quoted(configOrigin, string + string2);
            }
        }
        if (abstractConfigValue4 == null) {
            arrayList.add(abstractConfigValue3);
        } else {
            arrayList.remove(arrayList.size() - 1);
            arrayList.add(abstractConfigValue4);
        }
    }

    static List<AbstractConfigValue> consolidate(List<AbstractConfigValue> list) {
        if (list.size() < 2) {
            return list;
        }
        ArrayList<AbstractConfigValue> arrayList = new ArrayList<AbstractConfigValue>(list.size());
        for (AbstractConfigValue object : list) {
            if (object instanceof ConfigConcatenation) {
                arrayList.addAll(((ConfigConcatenation)object).pieces);
                continue;
            }
            arrayList.add(object);
        }
        ArrayList arrayList2 = new ArrayList(arrayList.size());
        for (AbstractConfigValue abstractConfigValue : arrayList) {
            if (arrayList2.isEmpty()) {
                arrayList2.add(abstractConfigValue);
                continue;
            }
            ConfigConcatenation.join(arrayList2, abstractConfigValue);
        }
        return arrayList2;
    }

    static AbstractConfigValue concatenate(List<AbstractConfigValue> list) {
        List<AbstractConfigValue> list2 = ConfigConcatenation.consolidate(list);
        if (list2.isEmpty()) {
            return null;
        }
        if (list2.size() == 1) {
            return list2.get(0);
        }
        ConfigOrigin configOrigin = SimpleConfigOrigin.mergeOrigins(list2);
        return new ConfigConcatenation(configOrigin, list2);
    }

    @Override
    ResolveResult<? extends AbstractConfigValue> resolveSubstitutions(ResolveContext resolveContext, ResolveSource resolveSource) {
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            int n = resolveContext.depth() + 2;
            ConfigImpl.trace(n - 1, "concatenation has " + this.pieces.size() + " pieces:");
            int n2 = 0;
            for (AbstractConfigValue list2 : this.pieces) {
                ConfigImpl.trace(n, n2 + ": " + list2);
                ++n2;
            }
        }
        ResolveSource resolveSource2 = resolveSource;
        ResolveContext resolveContext2 = resolveContext;
        ArrayList<AbstractConfigValue> arrayList = new ArrayList<AbstractConfigValue>(this.pieces.size());
        for (AbstractConfigValue abstractConfigValue : this.pieces) {
            Path path = resolveContext2.restrictToChild();
            ResolveResult<? extends AbstractConfigValue> resolveResult = resolveContext2.unrestricted().resolve(abstractConfigValue, resolveSource2);
            Object v = resolveResult.value;
            resolveContext2 = resolveResult.context.restrict(path);
            if (ConfigImpl.traceSubstitutionsEnabled()) {
                ConfigImpl.trace(resolveContext.depth(), "resolved concat piece to " + v);
            }
            if (v == null) continue;
            arrayList.add((AbstractConfigValue)v);
        }
        List<AbstractConfigValue> list = ConfigConcatenation.consolidate(arrayList);
        if (list.size() > 1 && resolveContext.options().getAllowUnresolved()) {
            return ResolveResult.make(resolveContext2, new ConfigConcatenation(this.origin(), list));
        }
        if (list.isEmpty()) {
            return ResolveResult.make(resolveContext2, null);
        }
        if (list.size() == 1) {
            return ResolveResult.make(resolveContext2, list.get(0));
        }
        throw new ConfigException.BugOrBroken("Bug in the library; resolved list was joined to too many values: " + list);
    }

    @Override
    ResolveStatus resolveStatus() {
        return ResolveStatus.UNRESOLVED;
    }

    @Override
    public ConfigConcatenation replaceChild(AbstractConfigValue abstractConfigValue, AbstractConfigValue abstractConfigValue2) {
        List<AbstractConfigValue> list = ConfigConcatenation.replaceChildInList(this.pieces, abstractConfigValue, abstractConfigValue2);
        if (list == null) {
            return null;
        }
        return new ConfigConcatenation(this.origin(), list);
    }

    @Override
    public boolean hasDescendant(AbstractConfigValue abstractConfigValue) {
        return ConfigConcatenation.hasDescendantInList(this.pieces, abstractConfigValue);
    }

    @Override
    ConfigConcatenation relativized(Path path) {
        ArrayList<AbstractConfigValue> arrayList = new ArrayList<AbstractConfigValue>();
        for (AbstractConfigValue abstractConfigValue : this.pieces) {
            arrayList.add(abstractConfigValue.relativized(path));
        }
        return new ConfigConcatenation(this.origin(), arrayList);
    }

    @Override
    protected boolean canEqual(Object object) {
        return object instanceof ConfigConcatenation;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ConfigConcatenation) {
            return this.canEqual(object) && this.pieces.equals(((ConfigConcatenation)object).pieces);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.pieces.hashCode();
    }

    @Override
    protected void render(StringBuilder stringBuilder, int n, boolean bl, ConfigRenderOptions configRenderOptions) {
        for (AbstractConfigValue abstractConfigValue : this.pieces) {
            abstractConfigValue.render(stringBuilder, n, bl, configRenderOptions);
        }
    }
}

