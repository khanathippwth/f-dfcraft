/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigMergeable;
import moss.factions.shade.com.typesafe.config.ConfigObject;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigRenderOptions;
import moss.factions.shade.com.typesafe.config.ConfigValue;
import moss.factions.shade.com.typesafe.config.ConfigValueType;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigDelayedMergeObject;
import moss.factions.shade.com.typesafe.config.impl.ConfigImpl;
import moss.factions.shade.com.typesafe.config.impl.Container;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.ResolveContext;
import moss.factions.shade.com.typesafe.config.impl.ResolveResult;
import moss.factions.shade.com.typesafe.config.impl.ResolveSource;
import moss.factions.shade.com.typesafe.config.impl.ResolveStatus;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfig;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigOrigin;

abstract class AbstractConfigObject
extends AbstractConfigValue
implements ConfigObject,
Container {
    private final SimpleConfig config = new SimpleConfig(this);

    protected AbstractConfigObject(ConfigOrigin configOrigin) {
        super(configOrigin);
    }

    @Override
    public SimpleConfig toConfig() {
        return this.config;
    }

    @Override
    public AbstractConfigObject toFallbackValue() {
        return this;
    }

    @Override
    public abstract AbstractConfigObject withOnlyKey(String var1);

    @Override
    public abstract AbstractConfigObject withoutKey(String var1);

    @Override
    public abstract AbstractConfigObject withValue(String var1, ConfigValue var2);

    protected abstract AbstractConfigObject withOnlyPathOrNull(Path var1);

    abstract AbstractConfigObject withOnlyPath(Path var1);

    abstract AbstractConfigObject withoutPath(Path var1);

    abstract AbstractConfigObject withValue(Path var1, ConfigValue var2);

    protected final AbstractConfigValue peekAssumingResolved(String string, Path path) {
        try {
            return this.attemptPeekWithPartialResolve(string);
        } catch (ConfigException.NotResolved notResolved) {
            throw ConfigImpl.improveNotResolved(path, notResolved);
        }
    }

    abstract AbstractConfigValue attemptPeekWithPartialResolve(String var1);

    protected AbstractConfigValue peekPath(Path path) {
        return AbstractConfigObject.peekPath(this, path);
    }

    private static AbstractConfigValue peekPath(AbstractConfigObject abstractConfigObject, Path path) {
        try {
            Path path2 = path.remainder();
            AbstractConfigValue abstractConfigValue = abstractConfigObject.attemptPeekWithPartialResolve(path.first());
            if (path2 == null) {
                return abstractConfigValue;
            }
            if (abstractConfigValue instanceof AbstractConfigObject) {
                return AbstractConfigObject.peekPath((AbstractConfigObject)abstractConfigValue, path2);
            }
            return null;
        } catch (ConfigException.NotResolved notResolved) {
            throw ConfigImpl.improveNotResolved(path, notResolved);
        }
    }

    @Override
    public ConfigValueType valueType() {
        return ConfigValueType.OBJECT;
    }

    protected abstract AbstractConfigObject newCopy(ResolveStatus var1, ConfigOrigin var2);

    @Override
    protected AbstractConfigObject newCopy(ConfigOrigin configOrigin) {
        return this.newCopy(this.resolveStatus(), configOrigin);
    }

    @Override
    protected AbstractConfigObject constructDelayedMerge(ConfigOrigin configOrigin, List<AbstractConfigValue> list) {
        return new ConfigDelayedMergeObject(configOrigin, list);
    }

    @Override
    protected abstract AbstractConfigObject mergedWithObject(AbstractConfigObject var1);

    @Override
    public AbstractConfigObject withFallback(ConfigMergeable configMergeable) {
        return (AbstractConfigObject)super.withFallback(configMergeable);
    }

    static ConfigOrigin mergeOrigins(Collection<? extends AbstractConfigValue> collection) {
        if (collection.isEmpty()) {
            throw new ConfigException.BugOrBroken("can't merge origins on empty list");
        }
        ArrayList<SimpleConfigOrigin> arrayList = new ArrayList<SimpleConfigOrigin>();
        SimpleConfigOrigin simpleConfigOrigin = null;
        int n = 0;
        for (AbstractConfigValue abstractConfigValue : collection) {
            if (simpleConfigOrigin == null) {
                simpleConfigOrigin = abstractConfigValue.origin();
            }
            if (abstractConfigValue instanceof AbstractConfigObject && ((AbstractConfigObject)abstractConfigValue).resolveStatus() == ResolveStatus.RESOLVED && ((ConfigObject)((Object)abstractConfigValue)).isEmpty()) continue;
            arrayList.add(abstractConfigValue.origin());
            ++n;
        }
        if (n == 0) {
            arrayList.add(simpleConfigOrigin);
        }
        return SimpleConfigOrigin.mergeOrigins(arrayList);
    }

    static ConfigOrigin mergeOrigins(AbstractConfigObject ... abstractConfigObjectArray) {
        return AbstractConfigObject.mergeOrigins(Arrays.asList(abstractConfigObjectArray));
    }

    abstract ResolveResult<? extends AbstractConfigObject> resolveSubstitutions(ResolveContext var1, ResolveSource var2);

    @Override
    abstract AbstractConfigObject relativized(Path var1);

    @Override
    public abstract AbstractConfigValue get(Object var1);

    @Override
    protected abstract void render(StringBuilder var1, int var2, boolean var3, ConfigRenderOptions var4);

    private static UnsupportedOperationException weAreImmutable(String string) {
        return new UnsupportedOperationException("ConfigObject is immutable, you can't call Map." + string);
    }

    @Override
    public void clear() {
        throw AbstractConfigObject.weAreImmutable("clear");
    }

    @Override
    public ConfigValue put(String string, ConfigValue configValue) {
        throw AbstractConfigObject.weAreImmutable("put");
    }

    @Override
    public void putAll(Map<? extends String, ? extends ConfigValue> map) {
        throw AbstractConfigObject.weAreImmutable("putAll");
    }

    @Override
    public ConfigValue remove(Object object) {
        throw AbstractConfigObject.weAreImmutable("remove");
    }

    @Override
    public AbstractConfigObject withOrigin(ConfigOrigin configOrigin) {
        return (AbstractConfigObject)super.withOrigin(configOrigin);
    }
}

