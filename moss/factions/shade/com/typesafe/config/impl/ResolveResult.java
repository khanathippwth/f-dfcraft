/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigObject;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ResolveContext;

final class ResolveResult<V extends AbstractConfigValue> {
    public final ResolveContext context;
    public final V value;

    private ResolveResult(ResolveContext resolveContext, V v) {
        this.context = resolveContext;
        this.value = v;
    }

    static <V extends AbstractConfigValue> ResolveResult<V> make(ResolveContext resolveContext, V v) {
        return new ResolveResult<V>(resolveContext, v);
    }

    ResolveResult<AbstractConfigObject> asObjectResult() {
        if (!(this.value instanceof AbstractConfigObject)) {
            throw new ConfigException.BugOrBroken("Expecting a resolve result to be an object, but it was " + this.value);
        }
        ResolveResult resolveResult = this;
        return resolveResult;
    }

    ResolveResult<AbstractConfigValue> asValueResult() {
        ResolveResult resolveResult = this;
        return resolveResult;
    }

    ResolveResult<V> popTrace() {
        return ResolveResult.make(this.context.popTrace(), this.value);
    }

    public String toString() {
        return "ResolveResult(" + this.value + ")";
    }
}

