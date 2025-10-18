/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.Collection;
import java.util.Collections;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigRenderOptions;
import moss.factions.shade.com.typesafe.config.ConfigValue;
import moss.factions.shade.com.typesafe.config.ConfigValueType;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigObject;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigImpl;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.ResolveContext;
import moss.factions.shade.com.typesafe.config.impl.ResolveResult;
import moss.factions.shade.com.typesafe.config.impl.ResolveSource;
import moss.factions.shade.com.typesafe.config.impl.ResolveStatus;
import moss.factions.shade.com.typesafe.config.impl.SubstitutionExpression;
import moss.factions.shade.com.typesafe.config.impl.Unmergeable;

final class ConfigReference
extends AbstractConfigValue
implements Unmergeable {
    private final SubstitutionExpression expr;
    private final int prefixLength;

    ConfigReference(ConfigOrigin configOrigin, SubstitutionExpression substitutionExpression) {
        this(configOrigin, substitutionExpression, 0);
    }

    private ConfigReference(ConfigOrigin configOrigin, SubstitutionExpression substitutionExpression, int n) {
        super(configOrigin);
        this.expr = substitutionExpression;
        this.prefixLength = n;
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
    protected ConfigReference newCopy(ConfigOrigin configOrigin) {
        return new ConfigReference(configOrigin, this.expr, this.prefixLength);
    }

    @Override
    protected boolean ignoresFallbacks() {
        return false;
    }

    public Collection<ConfigReference> unmergedValues() {
        return Collections.singleton(this);
    }

    @Override
    ResolveResult<? extends AbstractConfigValue> resolveSubstitutions(ResolveContext resolveContext, ResolveSource resolveSource) {
        AbstractConfigValue abstractConfigValue;
        ResolveContext resolveContext2 = resolveContext.addCycleMarker(this);
        try {
            ResolveSource.ResultWithPath resultWithPath = resolveSource.lookupSubst(resolveContext2, this.expr, this.prefixLength);
            resolveContext2 = resultWithPath.result.context;
            if (resultWithPath.result.value != null) {
                if (ConfigImpl.traceSubstitutionsEnabled()) {
                    ConfigImpl.trace(resolveContext2.depth(), "recursively resolving " + resultWithPath + " which was the resolution of " + this.expr + " against " + resolveSource);
                }
                ResolveSource resolveSource2 = new ResolveSource((AbstractConfigObject)resultWithPath.pathFromRoot.last(), resultWithPath.pathFromRoot);
                if (ConfigImpl.traceSubstitutionsEnabled()) {
                    ConfigImpl.trace(resolveContext2.depth(), "will recursively resolve against " + resolveSource2);
                }
                ResolveResult<? extends AbstractConfigValue> resolveResult = resolveContext2.resolve((AbstractConfigValue)resultWithPath.result.value, resolveSource2);
                abstractConfigValue = resolveResult.value;
                resolveContext2 = resolveResult.context;
            } else {
                ConfigValue configValue = resolveContext.options().getResolver().lookup(this.expr.path().render());
                abstractConfigValue = (AbstractConfigValue)configValue;
            }
        } catch (AbstractConfigValue.NotPossibleToResolve notPossibleToResolve) {
            if (ConfigImpl.traceSubstitutionsEnabled()) {
                ConfigImpl.trace(resolveContext2.depth(), "not possible to resolve " + this.expr + ", cycle involved: " + notPossibleToResolve.traceString());
            }
            if (this.expr.optional()) {
                abstractConfigValue = null;
            }
            throw new ConfigException.UnresolvedSubstitution(this.origin(), this.expr + " was part of a cycle of substitutions involving " + notPossibleToResolve.traceString(), notPossibleToResolve);
        }
        if (abstractConfigValue == null && !this.expr.optional()) {
            if (resolveContext2.options().getAllowUnresolved()) {
                return ResolveResult.make(resolveContext2.removeCycleMarker(this), this);
            }
            throw new ConfigException.UnresolvedSubstitution(this.origin(), this.expr.toString());
        }
        return ResolveResult.make(resolveContext2.removeCycleMarker(this), abstractConfigValue);
    }

    @Override
    ResolveStatus resolveStatus() {
        return ResolveStatus.UNRESOLVED;
    }

    @Override
    ConfigReference relativized(Path path) {
        SubstitutionExpression substitutionExpression = this.expr.changePath(this.expr.path().prepend(path));
        return new ConfigReference(this.origin(), substitutionExpression, this.prefixLength + path.length());
    }

    @Override
    protected boolean canEqual(Object object) {
        return object instanceof ConfigReference;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ConfigReference) {
            return this.canEqual(object) && this.expr.equals(((ConfigReference)object).expr);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.expr.hashCode();
    }

    @Override
    protected void render(StringBuilder stringBuilder, int n, boolean bl, ConfigRenderOptions configRenderOptions) {
        stringBuilder.append(this.expr.toString());
    }

    SubstitutionExpression expression() {
        return this.expr;
    }
}

