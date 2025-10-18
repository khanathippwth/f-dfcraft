/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigList;
import moss.factions.shade.com.typesafe.config.ConfigMergeable;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigRenderOptions;
import moss.factions.shade.com.typesafe.config.ConfigValue;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigObject;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigDelayedMerge;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.ReplaceableMergeStack;
import moss.factions.shade.com.typesafe.config.impl.ResolveContext;
import moss.factions.shade.com.typesafe.config.impl.ResolveResult;
import moss.factions.shade.com.typesafe.config.impl.ResolveSource;
import moss.factions.shade.com.typesafe.config.impl.ResolveStatus;
import moss.factions.shade.com.typesafe.config.impl.Unmergeable;

final class ConfigDelayedMergeObject
extends AbstractConfigObject
implements Unmergeable,
ReplaceableMergeStack {
    private final List<AbstractConfigValue> stack;

    ConfigDelayedMergeObject(ConfigOrigin configOrigin, List<AbstractConfigValue> list) {
        super(configOrigin);
        this.stack = list;
        if (list.isEmpty()) {
            throw new ConfigException.BugOrBroken("creating empty delayed merge object");
        }
        if (!(list.get(0) instanceof AbstractConfigObject)) {
            throw new ConfigException.BugOrBroken("created a delayed merge object not guaranteed to be an object");
        }
        for (AbstractConfigValue abstractConfigValue : list) {
            if (!(abstractConfigValue instanceof ConfigDelayedMerge) && !(abstractConfigValue instanceof ConfigDelayedMergeObject)) continue;
            throw new ConfigException.BugOrBroken("placed nested DelayedMerge in a ConfigDelayedMergeObject, should have consolidated stack");
        }
    }

    @Override
    protected ConfigDelayedMergeObject newCopy(ResolveStatus resolveStatus, ConfigOrigin configOrigin) {
        if (resolveStatus != this.resolveStatus()) {
            throw new ConfigException.BugOrBroken("attempt to create resolved ConfigDelayedMergeObject");
        }
        return new ConfigDelayedMergeObject(configOrigin, this.stack);
    }

    @Override
    ResolveResult<? extends AbstractConfigObject> resolveSubstitutions(ResolveContext resolveContext, ResolveSource resolveSource) {
        ResolveResult<? extends AbstractConfigValue> resolveResult = ConfigDelayedMerge.resolveSubstitutions(this, this.stack, resolveContext, resolveSource);
        return resolveResult.asObjectResult();
    }

    @Override
    public AbstractConfigValue makeReplacement(ResolveContext resolveContext, int n) {
        return ConfigDelayedMerge.makeReplacement(resolveContext, this.stack, n);
    }

    @Override
    ResolveStatus resolveStatus() {
        return ResolveStatus.UNRESOLVED;
    }

    @Override
    public AbstractConfigValue replaceChild(AbstractConfigValue abstractConfigValue, AbstractConfigValue abstractConfigValue2) {
        List<AbstractConfigValue> list = ConfigDelayedMergeObject.replaceChildInList(this.stack, abstractConfigValue, abstractConfigValue2);
        if (list == null) {
            return null;
        }
        return new ConfigDelayedMergeObject(this.origin(), list);
    }

    @Override
    public boolean hasDescendant(AbstractConfigValue abstractConfigValue) {
        return ConfigDelayedMergeObject.hasDescendantInList(this.stack, abstractConfigValue);
    }

    @Override
    ConfigDelayedMergeObject relativized(Path path) {
        ArrayList<AbstractConfigValue> arrayList = new ArrayList<AbstractConfigValue>();
        for (AbstractConfigValue abstractConfigValue : this.stack) {
            arrayList.add(abstractConfigValue.relativized(path));
        }
        return new ConfigDelayedMergeObject(this.origin(), arrayList);
    }

    @Override
    protected boolean ignoresFallbacks() {
        return ConfigDelayedMerge.stackIgnoresFallbacks(this.stack);
    }

    @Override
    protected final ConfigDelayedMergeObject mergedWithTheUnmergeable(Unmergeable unmergeable) {
        this.requireNotIgnoringFallbacks();
        return (ConfigDelayedMergeObject)this.mergedWithTheUnmergeable(this.stack, unmergeable);
    }

    @Override
    protected final ConfigDelayedMergeObject mergedWithObject(AbstractConfigObject abstractConfigObject) {
        return this.mergedWithNonObject(abstractConfigObject);
    }

    @Override
    protected final ConfigDelayedMergeObject mergedWithNonObject(AbstractConfigValue abstractConfigValue) {
        this.requireNotIgnoringFallbacks();
        return (ConfigDelayedMergeObject)this.mergedWithNonObject(this.stack, abstractConfigValue);
    }

    @Override
    public ConfigDelayedMergeObject withFallback(ConfigMergeable configMergeable) {
        return (ConfigDelayedMergeObject)super.withFallback(configMergeable);
    }

    @Override
    public ConfigDelayedMergeObject withOnlyKey(String string) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public ConfigDelayedMergeObject withoutKey(String string) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    protected AbstractConfigObject withOnlyPathOrNull(Path path) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    AbstractConfigObject withOnlyPath(Path path) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    AbstractConfigObject withoutPath(Path path) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public ConfigDelayedMergeObject withValue(String string, ConfigValue configValue) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    ConfigDelayedMergeObject withValue(Path path, ConfigValue configValue) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    public Collection<AbstractConfigValue> unmergedValues() {
        return this.stack;
    }

    @Override
    protected boolean canEqual(Object object) {
        return object instanceof ConfigDelayedMergeObject;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ConfigDelayedMergeObject) {
            return this.canEqual(object) && (this.stack == ((ConfigDelayedMergeObject)object).stack || this.stack.equals(((ConfigDelayedMergeObject)object).stack));
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

    private static ConfigException notResolved() {
        return new ConfigException.NotResolved("need to Config#resolve() before using this object, see the API docs for Config#resolve()");
    }

    @Override
    public Map<String, Object> unwrapped() {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public AbstractConfigValue get(Object object) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public boolean containsKey(Object object) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public boolean containsValue(Object object) {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public Set<Map.Entry<String, ConfigValue>> entrySet() {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public boolean isEmpty() {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public Set<String> keySet() {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public int size() {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    public Collection<ConfigValue> values() {
        throw ConfigDelayedMergeObject.notResolved();
    }

    @Override
    protected AbstractConfigValue attemptPeekWithPartialResolve(String string) {
        for (AbstractConfigValue abstractConfigValue : this.stack) {
            if (abstractConfigValue instanceof AbstractConfigObject) {
                AbstractConfigObject abstractConfigObject = (AbstractConfigObject)abstractConfigValue;
                AbstractConfigValue abstractConfigValue2 = abstractConfigObject.attemptPeekWithPartialResolve(string);
                if (abstractConfigValue2 != null) {
                    if (!abstractConfigValue2.ignoresFallbacks()) continue;
                    return abstractConfigValue2;
                }
                if (!(abstractConfigValue instanceof Unmergeable)) continue;
                throw new ConfigException.BugOrBroken("should not be reached: unmergeable object returned null value");
            }
            if (abstractConfigValue instanceof Unmergeable) {
                throw new ConfigException.NotResolved("Key '" + string + "' is not available at '" + this.origin().description() + "' because value at '" + abstractConfigValue.origin().description() + "' has not been resolved and may turn out to contain or hide '" + string + "'. Be sure to Config#resolve() before using a config object.");
            }
            if (abstractConfigValue.resolveStatus() == ResolveStatus.UNRESOLVED) {
                if (!(abstractConfigValue instanceof ConfigList)) {
                    throw new ConfigException.BugOrBroken("Expecting a list here, not " + abstractConfigValue);
                }
                return null;
            }
            if (!abstractConfigValue.ignoresFallbacks()) {
                throw new ConfigException.BugOrBroken("resolved non-object should ignore fallbacks");
            }
            return null;
        }
        throw new ConfigException.BugOrBroken("Delayed merge stack does not contain any unmergeable values");
    }
}

