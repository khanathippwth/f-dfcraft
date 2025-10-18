/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigResolveOptions;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigObject;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigImpl;
import moss.factions.shade.com.typesafe.config.impl.ConfigReference;
import moss.factions.shade.com.typesafe.config.impl.MemoKey;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.ResolveMemos;
import moss.factions.shade.com.typesafe.config.impl.ResolveResult;
import moss.factions.shade.com.typesafe.config.impl.ResolveSource;
import moss.factions.shade.com.typesafe.config.impl.ResolveStatus;

final class ResolveContext {
    private final ResolveMemos memos;
    private final ConfigResolveOptions options;
    private final Path restrictToChild;
    private final List<AbstractConfigValue> resolveStack;
    private final Set<AbstractConfigValue> cycleMarkers;

    ResolveContext(ResolveMemos resolveMemos, ConfigResolveOptions configResolveOptions, Path path, List<AbstractConfigValue> list, Set<AbstractConfigValue> set) {
        this.memos = resolveMemos;
        this.options = configResolveOptions;
        this.restrictToChild = path;
        this.resolveStack = Collections.unmodifiableList(list);
        this.cycleMarkers = Collections.unmodifiableSet(set);
    }

    private static Set<AbstractConfigValue> newCycleMarkers() {
        return Collections.newSetFromMap(new IdentityHashMap());
    }

    ResolveContext(ConfigResolveOptions configResolveOptions, Path path) {
        this(new ResolveMemos(), configResolveOptions, path, new ArrayList<AbstractConfigValue>(), ResolveContext.newCycleMarkers());
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(this.depth(), "ResolveContext restrict to child " + path);
        }
    }

    ResolveContext addCycleMarker(AbstractConfigValue abstractConfigValue) {
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(this.depth(), "++ Cycle marker " + abstractConfigValue + "@" + System.identityHashCode(abstractConfigValue));
        }
        if (this.cycleMarkers.contains(abstractConfigValue)) {
            throw new ConfigException.BugOrBroken("Added cycle marker twice " + abstractConfigValue);
        }
        Set<AbstractConfigValue> set = ResolveContext.newCycleMarkers();
        set.addAll(this.cycleMarkers);
        set.add(abstractConfigValue);
        return new ResolveContext(this.memos, this.options, this.restrictToChild, this.resolveStack, set);
    }

    ResolveContext removeCycleMarker(AbstractConfigValue abstractConfigValue) {
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(this.depth(), "-- Cycle marker " + abstractConfigValue + "@" + System.identityHashCode(abstractConfigValue));
        }
        Set<AbstractConfigValue> set = ResolveContext.newCycleMarkers();
        set.addAll(this.cycleMarkers);
        set.remove(abstractConfigValue);
        return new ResolveContext(this.memos, this.options, this.restrictToChild, this.resolveStack, set);
    }

    private ResolveContext memoize(MemoKey memoKey, AbstractConfigValue abstractConfigValue) {
        ResolveMemos resolveMemos = this.memos.put(memoKey, abstractConfigValue);
        return new ResolveContext(resolveMemos, this.options, this.restrictToChild, this.resolveStack, this.cycleMarkers);
    }

    ConfigResolveOptions options() {
        return this.options;
    }

    boolean isRestrictedToChild() {
        return this.restrictToChild != null;
    }

    Path restrictToChild() {
        return this.restrictToChild;
    }

    ResolveContext restrict(Path path) {
        if (path == this.restrictToChild) {
            return this;
        }
        return new ResolveContext(this.memos, this.options, path, this.resolveStack, this.cycleMarkers);
    }

    ResolveContext unrestricted() {
        return this.restrict(null);
    }

    String traceString() {
        String string = ", ";
        StringBuilder stringBuilder = new StringBuilder();
        for (AbstractConfigValue abstractConfigValue : this.resolveStack) {
            if (!(abstractConfigValue instanceof ConfigReference)) continue;
            stringBuilder.append(((ConfigReference)abstractConfigValue).expression().toString());
            stringBuilder.append(string);
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length() - string.length());
        }
        return stringBuilder.toString();
    }

    private ResolveContext pushTrace(AbstractConfigValue abstractConfigValue) {
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(this.depth(), "pushing trace " + abstractConfigValue);
        }
        ArrayList<AbstractConfigValue> arrayList = new ArrayList<AbstractConfigValue>(this.resolveStack);
        arrayList.add(abstractConfigValue);
        return new ResolveContext(this.memos, this.options, this.restrictToChild, arrayList, this.cycleMarkers);
    }

    ResolveContext popTrace() {
        ArrayList<AbstractConfigValue> arrayList = new ArrayList<AbstractConfigValue>(this.resolveStack);
        AbstractConfigValue abstractConfigValue = (AbstractConfigValue)arrayList.remove(this.resolveStack.size() - 1);
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(this.depth() - 1, "popped trace " + abstractConfigValue);
        }
        return new ResolveContext(this.memos, this.options, this.restrictToChild, arrayList, this.cycleMarkers);
    }

    int depth() {
        if (this.resolveStack.size() > 30) {
            throw new ConfigException.BugOrBroken("resolve getting too deep");
        }
        return this.resolveStack.size();
    }

    ResolveResult<? extends AbstractConfigValue> resolve(AbstractConfigValue abstractConfigValue, ResolveSource resolveSource) {
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(this.depth(), "resolving " + abstractConfigValue + " restrictToChild=" + this.restrictToChild + " in " + resolveSource);
        }
        return this.pushTrace(abstractConfigValue).realResolve(abstractConfigValue, resolveSource).popTrace();
    }

    private ResolveResult<? extends AbstractConfigValue> realResolve(AbstractConfigValue abstractConfigValue, ResolveSource resolveSource) {
        MemoKey memoKey = new MemoKey(abstractConfigValue, null);
        MemoKey memoKey2 = null;
        AbstractConfigValue abstractConfigValue2 = this.memos.get(memoKey);
        if (abstractConfigValue2 == null && this.isRestrictedToChild()) {
            memoKey2 = new MemoKey(abstractConfigValue, this.restrictToChild());
            abstractConfigValue2 = this.memos.get(memoKey2);
        }
        if (abstractConfigValue2 != null) {
            if (ConfigImpl.traceSubstitutionsEnabled()) {
                ConfigImpl.trace(this.depth(), "using cached resolution " + abstractConfigValue2 + " for " + abstractConfigValue + " restrictToChild " + this.restrictToChild());
            }
            return ResolveResult.make(this, abstractConfigValue2);
        }
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(this.depth(), "not found in cache, resolving " + abstractConfigValue + "@" + System.identityHashCode(abstractConfigValue));
        }
        if (this.cycleMarkers.contains(abstractConfigValue)) {
            if (ConfigImpl.traceSubstitutionsEnabled()) {
                ConfigImpl.trace(this.depth(), "Cycle detected, can't resolve; " + abstractConfigValue + "@" + System.identityHashCode(abstractConfigValue));
            }
            throw new AbstractConfigValue.NotPossibleToResolve(this);
        }
        ResolveResult<? extends AbstractConfigValue> resolveResult = abstractConfigValue.resolveSubstitutions(this, resolveSource);
        Object v = resolveResult.value;
        if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace(this.depth(), "resolved to " + v + "@" + System.identityHashCode(v) + " from " + abstractConfigValue + "@" + System.identityHashCode(v));
        }
        ResolveContext resolveContext = resolveResult.context;
        if (v == null || ((AbstractConfigValue)v).resolveStatus() == ResolveStatus.RESOLVED) {
            if (ConfigImpl.traceSubstitutionsEnabled()) {
                ConfigImpl.trace(this.depth(), "caching " + memoKey + " result " + v);
            }
            resolveContext = resolveContext.memoize(memoKey, (AbstractConfigValue)v);
        } else if (this.isRestrictedToChild()) {
            if (memoKey2 == null) {
                throw new ConfigException.BugOrBroken("restrictedKey should not be null here");
            }
            if (ConfigImpl.traceSubstitutionsEnabled()) {
                ConfigImpl.trace(this.depth(), "caching " + memoKey2 + " result " + v);
            }
            resolveContext = resolveContext.memoize(memoKey2, (AbstractConfigValue)v);
        } else if (this.options().getAllowUnresolved()) {
            if (ConfigImpl.traceSubstitutionsEnabled()) {
                ConfigImpl.trace(this.depth(), "caching " + memoKey + " result " + v);
            }
            resolveContext = resolveContext.memoize(memoKey, (AbstractConfigValue)v);
        } else {
            throw new ConfigException.BugOrBroken("resolveSubstitutions() did not give us a resolved object");
        }
        return ResolveResult.make(resolveContext, v);
    }

    static AbstractConfigValue resolve(AbstractConfigValue abstractConfigValue, AbstractConfigObject abstractConfigObject, ConfigResolveOptions configResolveOptions) {
        ResolveSource resolveSource = new ResolveSource(abstractConfigObject);
        ResolveContext resolveContext = new ResolveContext(configResolveOptions, null);
        try {
            return resolveContext.resolve((AbstractConfigValue)abstractConfigValue, (ResolveSource)resolveSource).value;
        } catch (AbstractConfigValue.NotPossibleToResolve notPossibleToResolve) {
            throw new ConfigException.BugOrBroken("NotPossibleToResolve was thrown from an outermost resolve", notPossibleToResolve);
        }
    }
}

