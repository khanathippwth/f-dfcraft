/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigMergeable;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigRenderOptions;
import moss.factions.shade.com.typesafe.config.ConfigValueType;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigObject;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigDelayedMergeObject;
import moss.factions.shade.com.typesafe.config.impl.ConfigImpl;
import moss.factions.shade.com.typesafe.config.impl.ConfigImplUtil;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.ReplaceableMergeStack;
import moss.factions.shade.com.typesafe.config.impl.ResolveContext;
import moss.factions.shade.com.typesafe.config.impl.ResolveResult;
import moss.factions.shade.com.typesafe.config.impl.ResolveSource;
import moss.factions.shade.com.typesafe.config.impl.ResolveStatus;
import moss.factions.shade.com.typesafe.config.impl.Unmergeable;

final class ConfigDelayedMerge
extends AbstractConfigValue
implements Unmergeable,
ReplaceableMergeStack {
    private final List<AbstractConfigValue> stack;

    ConfigDelayedMerge(ConfigOrigin configOrigin, List<AbstractConfigValue> list) {
        super(configOrigin);
        this.stack = list;
        if (list.isEmpty()) {
            throw new ConfigException.BugOrBroken("creating empty delayed merge value");
        }
        for (AbstractConfigValue abstractConfigValue : list) {
            if (!(abstractConfigValue instanceof ConfigDelayedMerge) && !(abstractConfigValue instanceof ConfigDelayedMergeObject)) continue;
            throw new ConfigException.BugOrBroken("placed nested DelayedMerge in a ConfigDelayedMerge, should have consolidated stack");
        }
    }

    @Override
    public ConfigValueType valueType() {
        throw new ConfigException.NotResolved("called valueType() on value with unresolved substitutions, need to Config#resolve() first, see API docs");
    }

    @Override
    public Object unwrapped() {
        throw new ConfigException.NotResolved("called unwrapped() on value with unresolved substitutions, need to Config#resolve() first, see API docs");
    }

    @Override
    ResolveResult<? extends AbstractConfigValue> resolveSubstitutions(ResolveContext resolveContext, ResolveSource resolveSource) {
        return ConfigDelayedMerge.resolveSubstitutions(this, this.stack, resolveContext, resolveSource);
    }

    static ResolveResult<? extends AbstractConfigValue> resolveSubstitutions(ReplaceableMergeStack replaceableMergeStack, List<AbstractConfigValue> list, ResolveContext resolveContext, ResolveSource resolveSource) {
        AbstractConfigValue abstractConfigValue2;
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(resolveContext.depth(), "delayed merge stack has " + list.size() + " items:");
            int n = 0;
            for (AbstractConfigValue abstractConfigValue2 : list) {
                ConfigImpl.trace(resolveContext.depth() + 1, n + ": " + abstractConfigValue2);
                ++n;
            }
        }
        ResolveContext resolveContext2 = resolveContext;
        int n = 0;
        abstractConfigValue2 = null;
        for (AbstractConfigValue abstractConfigValue3 : list) {
            ResolveSource resolveSource2;
            Object object;
            if (abstractConfigValue3 instanceof ReplaceableMergeStack) {
                throw new ConfigException.BugOrBroken("A delayed merge should not contain another one: " + replaceableMergeStack);
            }
            if (abstractConfigValue3 instanceof Unmergeable) {
                object = replaceableMergeStack.makeReplacement(resolveContext, n + 1);
                if (ConfigImpl.traceSubstitutionsEnabled()) {
                    ConfigImpl.trace(resolveContext2.depth(), "remainder portion: " + object);
                }
                if (ConfigImpl.traceSubstitutionsEnabled()) {
                    ConfigImpl.trace(resolveContext2.depth(), "building sourceForEnd");
                }
                resolveSource2 = resolveSource.replaceWithinCurrentParent((AbstractConfigValue)((Object)replaceableMergeStack), (AbstractConfigValue)object);
                if (ConfigImpl.traceSubstitutionsEnabled()) {
                    ConfigImpl.trace(resolveContext2.depth(), "  sourceForEnd before reset parents but after replace: " + resolveSource2);
                }
                resolveSource2 = resolveSource2.resetParents();
            } else {
                if (ConfigImpl.traceSubstitutionsEnabled()) {
                    ConfigImpl.trace(resolveContext2.depth(), "will resolve end against the original source with parent pushed");
                }
                resolveSource2 = resolveSource.pushParent(replaceableMergeStack);
            }
            if (ConfigImpl.traceSubstitutionsEnabled()) {
                ConfigImpl.trace(resolveContext2.depth(), "sourceForEnd      =" + resolveSource2);
            }
            if (ConfigImpl.traceSubstitutionsEnabled()) {
                ConfigImpl.trace(resolveContext2.depth(), "Resolving highest-priority item in delayed merge " + abstractConfigValue3 + " against " + resolveSource2 + " endWasRemoved=" + (resolveSource != resolveSource2));
            }
            object = resolveContext2.resolve(abstractConfigValue3, resolveSource2);
            Object v = ((ResolveResult)object).value;
            resolveContext2 = ((ResolveResult)object).context;
            if (v != null) {
                if (abstractConfigValue2 == null) {
                    abstractConfigValue2 = v;
                } else {
                    if (ConfigImpl.traceSubstitutionsEnabled()) {
                        ConfigImpl.trace(resolveContext2.depth() + 1, "merging " + abstractConfigValue2 + " with fallback " + v);
                    }
                    abstractConfigValue2 = abstractConfigValue2.withFallback((ConfigMergeable)v);
                }
            }
            ++n;
            if (!ConfigImpl.traceSubstitutionsEnabled()) continue;
            ConfigImpl.trace(resolveContext2.depth(), "stack merged, yielding: " + abstractConfigValue2);
        }
        return ResolveResult.make(resolveContext2, abstractConfigValue2);
    }

    @Override
    public AbstractConfigValue makeReplacement(ResolveContext resolveContext, int n) {
        return ConfigDelayedMerge.makeReplacement(resolveContext, this.stack, n);
    }

    static AbstractConfigValue makeReplacement(ResolveContext resolveContext, List<AbstractConfigValue> list, int n) {
        List<AbstractConfigValue> list2 = list.subList(n, list.size());
        if (list2.isEmpty()) {
            if (ConfigImpl.traceSubstitutionsEnabled()) {
                ConfigImpl.trace(resolveContext.depth(), "Nothing else in the merge stack, replacing with null");
            }
            return null;
        }
        AbstractConfigValue abstractConfigValue = null;
        for (AbstractConfigValue abstractConfigValue2 : list2) {
            if (abstractConfigValue == null) {
                abstractConfigValue = abstractConfigValue2;
                continue;
            }
            abstractConfigValue = abstractConfigValue.withFallback(abstractConfigValue2);
        }
        return abstractConfigValue;
    }

    @Override
    ResolveStatus resolveStatus() {
        return ResolveStatus.UNRESOLVED;
    }

    @Override
    public AbstractConfigValue replaceChild(AbstractConfigValue abstractConfigValue, AbstractConfigValue abstractConfigValue2) {
        List<AbstractConfigValue> list = ConfigDelayedMerge.replaceChildInList(this.stack, abstractConfigValue, abstractConfigValue2);
        if (list == null) {
            return null;
        }
        return new ConfigDelayedMerge(this.origin(), list);
    }

    @Override
    public boolean hasDescendant(AbstractConfigValue abstractConfigValue) {
        return ConfigDelayedMerge.hasDescendantInList(this.stack, abstractConfigValue);
    }

    @Override
    ConfigDelayedMerge relativized(Path path) {
        ArrayList<AbstractConfigValue> arrayList = new ArrayList<AbstractConfigValue>();
        for (AbstractConfigValue abstractConfigValue : this.stack) {
            arrayList.add(abstractConfigValue.relativized(path));
        }
        return new ConfigDelayedMerge(this.origin(), arrayList);
    }

    static boolean stackIgnoresFallbacks(List<AbstractConfigValue> list) {
        AbstractConfigValue abstractConfigValue = list.get(list.size() - 1);
        return abstractConfigValue.ignoresFallbacks();
    }

    @Override
    protected boolean ignoresFallbacks() {
        return ConfigDelayedMerge.stackIgnoresFallbacks(this.stack);
    }

    @Override
    protected AbstractConfigValue newCopy(ConfigOrigin configOrigin) {
        return new ConfigDelayedMerge(configOrigin, this.stack);
    }

    @Override
    protected final ConfigDelayedMerge mergedWithTheUnmergeable(Unmergeable unmergeable) {
        return (ConfigDelayedMerge)this.mergedWithTheUnmergeable(this.stack, unmergeable);
    }

    @Override
    protected final ConfigDelayedMerge mergedWithObject(AbstractConfigObject abstractConfigObject) {
        return (ConfigDelayedMerge)this.mergedWithObject(this.stack, abstractConfigObject);
    }

    @Override
    protected ConfigDelayedMerge mergedWithNonObject(AbstractConfigValue abstractConfigValue) {
        return (ConfigDelayedMerge)this.mergedWithNonObject(this.stack, abstractConfigValue);
    }

    public Collection<AbstractConfigValue> unmergedValues() {
        return this.stack;
    }

    @Override
    protected boolean canEqual(Object object) {
        return object instanceof ConfigDelayedMerge;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ConfigDelayedMerge) {
            return this.canEqual(object) && (this.stack == ((ConfigDelayedMerge)object).stack || this.stack.equals(((ConfigDelayedMerge)object).stack));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.stack.hashCode();
    }

    @Override
    protected void render(StringBuilder stringBuilder, int n, boolean bl, String string, ConfigRenderOptions configRenderOptions) {
        ConfigDelayedMerge.render(this.stack, stringBuilder, n, bl, string, configRenderOptions);
    }

    @Override
    protected void render(StringBuilder stringBuilder, int n, boolean bl, ConfigRenderOptions configRenderOptions) {
        this.render(stringBuilder, n, bl, null, configRenderOptions);
    }

    static void render(List<AbstractConfigValue> list, StringBuilder stringBuilder, int n, boolean bl, String string, ConfigRenderOptions configRenderOptions) {
        boolean bl2 = configRenderOptions.getComments();
        if (bl2) {
            stringBuilder.append("# unresolved merge of " + list.size() + " values follows (\n");
            if (string == null) {
                ConfigDelayedMerge.indent(stringBuilder, n, configRenderOptions);
                stringBuilder.append("# this unresolved merge will not be parseable because it's at the root of the object\n");
                ConfigDelayedMerge.indent(stringBuilder, n, configRenderOptions);
                stringBuilder.append("# the HOCON format has no way to list multiple root objects in a single file\n");
            }
        }
        ArrayList<AbstractConfigValue> arrayList = new ArrayList<AbstractConfigValue>();
        arrayList.addAll(list);
        Collections.reverse(arrayList);
        int n2 = 0;
        for (AbstractConfigValue abstractConfigValue : arrayList) {
            if (bl2) {
                ConfigDelayedMerge.indent(stringBuilder, n, configRenderOptions);
                if (string != null) {
                    stringBuilder.append("#     unmerged value " + n2 + " for key " + ConfigImplUtil.renderJsonString(string) + " from ");
                } else {
                    stringBuilder.append("#     unmerged value " + n2 + " from ");
                }
                ++n2;
                stringBuilder.append(abstractConfigValue.origin().description());
                stringBuilder.append("\n");
                for (String string2 : abstractConfigValue.origin().comments()) {
                    ConfigDelayedMerge.indent(stringBuilder, n, configRenderOptions);
                    stringBuilder.append("# ");
                    stringBuilder.append(string2);
                    stringBuilder.append("\n");
                }
            }
            ConfigDelayedMerge.indent(stringBuilder, n, configRenderOptions);
            if (string != null) {
                stringBuilder.append(ConfigImplUtil.renderJsonString(string));
                if (configRenderOptions.getFormatted()) {
                    stringBuilder.append(" : ");
                } else {
                    stringBuilder.append(":");
                }
            }
            abstractConfigValue.render(stringBuilder, n, bl, configRenderOptions);
            stringBuilder.append(",");
            if (!configRenderOptions.getFormatted()) continue;
            stringBuilder.append('\n');
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        if (configRenderOptions.getFormatted()) {
            stringBuilder.setLength(stringBuilder.length() - 1);
            stringBuilder.append("\n");
        }
        if (bl2) {
            ConfigDelayedMerge.indent(stringBuilder, n, configRenderOptions);
            stringBuilder.append("# ) end of unresolved merge\n");
        }
    }
}

