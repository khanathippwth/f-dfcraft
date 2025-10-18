/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigMergeable;
import moss.factions.shade.com.typesafe.config.ConfigObject;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigRenderOptions;
import moss.factions.shade.com.typesafe.config.ConfigValue;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigObject;
import moss.factions.shade.com.typesafe.config.impl.ConfigDelayedMerge;
import moss.factions.shade.com.typesafe.config.impl.ConfigImplUtil;
import moss.factions.shade.com.typesafe.config.impl.Container;
import moss.factions.shade.com.typesafe.config.impl.MergeableValue;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.ResolveContext;
import moss.factions.shade.com.typesafe.config.impl.ResolveResult;
import moss.factions.shade.com.typesafe.config.impl.ResolveSource;
import moss.factions.shade.com.typesafe.config.impl.ResolveStatus;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfig;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigObject;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigOrigin;
import moss.factions.shade.com.typesafe.config.impl.Unmergeable;

abstract class AbstractConfigValue
implements ConfigValue,
MergeableValue {
    private final SimpleConfigOrigin origin;

    AbstractConfigValue(ConfigOrigin configOrigin) {
        this.origin = (SimpleConfigOrigin)configOrigin;
    }

    @Override
    public SimpleConfigOrigin origin() {
        return this.origin;
    }

    ResolveResult<? extends AbstractConfigValue> resolveSubstitutions(ResolveContext resolveContext, ResolveSource resolveSource) {
        return ResolveResult.make(resolveContext, this);
    }

    ResolveStatus resolveStatus() {
        return ResolveStatus.RESOLVED;
    }

    protected static List<AbstractConfigValue> replaceChildInList(List<AbstractConfigValue> list, AbstractConfigValue abstractConfigValue, AbstractConfigValue abstractConfigValue2) {
        int n;
        for (n = 0; n < list.size() && list.get(n) != abstractConfigValue; ++n) {
        }
        if (n == list.size()) {
            throw new ConfigException.BugOrBroken("tried to replace " + abstractConfigValue + " which is not in " + list);
        }
        ArrayList<AbstractConfigValue> arrayList = new ArrayList<AbstractConfigValue>(list);
        if (abstractConfigValue2 != null) {
            arrayList.set(n, abstractConfigValue2);
        } else {
            arrayList.remove(n);
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        return arrayList;
    }

    protected static boolean hasDescendantInList(List<AbstractConfigValue> list, AbstractConfigValue abstractConfigValue) {
        for (AbstractConfigValue abstractConfigValue2 : list) {
            if (abstractConfigValue2 != abstractConfigValue) continue;
            return true;
        }
        for (AbstractConfigValue abstractConfigValue2 : list) {
            if (!(abstractConfigValue2 instanceof Container) || !((Container)((Object)abstractConfigValue2)).hasDescendant(abstractConfigValue)) continue;
            return true;
        }
        return false;
    }

    AbstractConfigValue relativized(Path path) {
        return this;
    }

    @Override
    public AbstractConfigValue toFallbackValue() {
        return this;
    }

    protected abstract AbstractConfigValue newCopy(ConfigOrigin var1);

    protected boolean ignoresFallbacks() {
        return this.resolveStatus() == ResolveStatus.RESOLVED;
    }

    protected AbstractConfigValue withFallbacksIgnored() {
        if (this.ignoresFallbacks()) {
            return this;
        }
        throw new ConfigException.BugOrBroken("value class doesn't implement forced fallback-ignoring " + this);
    }

    protected final void requireNotIgnoringFallbacks() {
        if (this.ignoresFallbacks()) {
            throw new ConfigException.BugOrBroken("method should not have been called with ignoresFallbacks=true " + this.getClass().getSimpleName());
        }
    }

    protected AbstractConfigValue constructDelayedMerge(ConfigOrigin configOrigin, List<AbstractConfigValue> list) {
        return new ConfigDelayedMerge(configOrigin, list);
    }

    protected final AbstractConfigValue mergedWithTheUnmergeable(Collection<AbstractConfigValue> collection, Unmergeable unmergeable) {
        this.requireNotIgnoringFallbacks();
        ArrayList<AbstractConfigValue> arrayList = new ArrayList<AbstractConfigValue>();
        arrayList.addAll(collection);
        arrayList.addAll(unmergeable.unmergedValues());
        return this.constructDelayedMerge(AbstractConfigObject.mergeOrigins(arrayList), arrayList);
    }

    private final AbstractConfigValue delayMerge(Collection<AbstractConfigValue> collection, AbstractConfigValue abstractConfigValue) {
        ArrayList<AbstractConfigValue> arrayList = new ArrayList<AbstractConfigValue>();
        arrayList.addAll(collection);
        arrayList.add(abstractConfigValue);
        return this.constructDelayedMerge(AbstractConfigObject.mergeOrigins(arrayList), arrayList);
    }

    protected final AbstractConfigValue mergedWithObject(Collection<AbstractConfigValue> collection, AbstractConfigObject abstractConfigObject) {
        this.requireNotIgnoringFallbacks();
        if (this instanceof AbstractConfigObject) {
            throw new ConfigException.BugOrBroken("Objects must reimplement mergedWithObject");
        }
        return this.mergedWithNonObject(collection, abstractConfigObject);
    }

    protected final AbstractConfigValue mergedWithNonObject(Collection<AbstractConfigValue> collection, AbstractConfigValue abstractConfigValue) {
        this.requireNotIgnoringFallbacks();
        if (this.resolveStatus() == ResolveStatus.RESOLVED) {
            return this.withFallbacksIgnored();
        }
        return this.delayMerge(collection, abstractConfigValue);
    }

    protected AbstractConfigValue mergedWithTheUnmergeable(Unmergeable unmergeable) {
        this.requireNotIgnoringFallbacks();
        return this.mergedWithTheUnmergeable(Collections.singletonList(this), unmergeable);
    }

    protected AbstractConfigValue mergedWithObject(AbstractConfigObject abstractConfigObject) {
        this.requireNotIgnoringFallbacks();
        return this.mergedWithObject(Collections.singletonList(this), abstractConfigObject);
    }

    protected AbstractConfigValue mergedWithNonObject(AbstractConfigValue abstractConfigValue) {
        this.requireNotIgnoringFallbacks();
        return this.mergedWithNonObject(Collections.singletonList(this), abstractConfigValue);
    }

    @Override
    public AbstractConfigValue withOrigin(ConfigOrigin configOrigin) {
        if (this.origin == configOrigin) {
            return this;
        }
        return this.newCopy(configOrigin);
    }

    @Override
    public AbstractConfigValue withFallback(ConfigMergeable configMergeable) {
        if (this.ignoresFallbacks()) {
            return this;
        }
        ConfigValue configValue = ((MergeableValue)configMergeable).toFallbackValue();
        if (configValue instanceof Unmergeable) {
            return this.mergedWithTheUnmergeable((Unmergeable)((Object)configValue));
        }
        if (configValue instanceof AbstractConfigObject) {
            return this.mergedWithObject((AbstractConfigObject)configValue);
        }
        return this.mergedWithNonObject((AbstractConfigValue)configValue);
    }

    protected boolean canEqual(Object object) {
        return object instanceof ConfigValue;
    }

    public boolean equals(Object object) {
        if (object instanceof ConfigValue) {
            return this.canEqual(object) && this.valueType() == ((ConfigValue)object).valueType() && ConfigImplUtil.equalsHandlingNull(this.unwrapped(), ((ConfigValue)object).unwrapped());
        }
        return false;
    }

    public int hashCode() {
        Object object = this.unwrapped();
        if (object == null) {
            return 0;
        }
        return object.hashCode();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        this.render(stringBuilder, 0, true, null, ConfigRenderOptions.concise());
        return this.getClass().getSimpleName() + "(" + stringBuilder.toString() + ")";
    }

    protected static void indent(StringBuilder stringBuilder, int n, ConfigRenderOptions configRenderOptions) {
        if (configRenderOptions.getFormatted()) {
            for (int i = n; i > 0; --i) {
                stringBuilder.append("    ");
            }
        }
    }

    protected void render(StringBuilder stringBuilder, int n, boolean bl, String string, ConfigRenderOptions configRenderOptions) {
        if (string != null) {
            String string2 = configRenderOptions.getJson() ? ConfigImplUtil.renderJsonString(string) : ConfigImplUtil.renderStringUnquotedIfPossible(string);
            stringBuilder.append(string2);
            if (configRenderOptions.getJson()) {
                if (configRenderOptions.getFormatted()) {
                    stringBuilder.append(" : ");
                } else {
                    stringBuilder.append(":");
                }
            } else if (this instanceof ConfigObject) {
                if (configRenderOptions.getFormatted()) {
                    stringBuilder.append(' ');
                }
            } else {
                stringBuilder.append("=");
            }
        }
        this.render(stringBuilder, n, bl, configRenderOptions);
    }

    protected void render(StringBuilder stringBuilder, int n, boolean bl, ConfigRenderOptions configRenderOptions) {
        Object object = this.unwrapped();
        stringBuilder.append(object.toString());
    }

    @Override
    public final String render() {
        return this.render(ConfigRenderOptions.defaults());
    }

    @Override
    public final String render(ConfigRenderOptions configRenderOptions) {
        StringBuilder stringBuilder = new StringBuilder();
        this.render(stringBuilder, 0, true, null, configRenderOptions);
        return stringBuilder.toString();
    }

    String transformToString() {
        return null;
    }

    SimpleConfig atKey(ConfigOrigin configOrigin, String string) {
        Map<String, AbstractConfigValue> map = Collections.singletonMap(string, this);
        return new SimpleConfigObject(configOrigin, map).toConfig();
    }

    @Override
    public SimpleConfig atKey(String string) {
        return this.atKey(SimpleConfigOrigin.newSimple("atKey(" + string + ")"), string);
    }

    SimpleConfig atPath(ConfigOrigin configOrigin, Path path) {
        SimpleConfig simpleConfig = this.atKey(configOrigin, path.last());
        for (Path path2 = path.parent(); path2 != null; path2 = path2.parent()) {
            String string = path2.last();
            simpleConfig = simpleConfig.atKey(configOrigin, string);
        }
        return simpleConfig;
    }

    @Override
    public SimpleConfig atPath(String string) {
        SimpleConfigOrigin simpleConfigOrigin = SimpleConfigOrigin.newSimple("atPath(" + string + ")");
        return this.atPath(simpleConfigOrigin, Path.newPath(string));
    }

    protected abstract class NoExceptionsModifier
    implements Modifier {
        protected NoExceptionsModifier() {
        }

        @Override
        public final AbstractConfigValue modifyChildMayThrow(String string, AbstractConfigValue abstractConfigValue) {
            try {
                return this.modifyChild(string, abstractConfigValue);
            } catch (RuntimeException runtimeException) {
                throw runtimeException;
            } catch (Exception exception) {
                throw new ConfigException.BugOrBroken("Unexpected exception", exception);
            }
        }

        abstract AbstractConfigValue modifyChild(String var1, AbstractConfigValue var2);
    }

    protected static interface Modifier {
        public AbstractConfigValue modifyChildMayThrow(String var1, AbstractConfigValue var2) throws Exception;
    }

    static class NotPossibleToResolve
    extends Exception {
        private static final long serialVersionUID = 1L;
        private final String traceString;

        NotPossibleToResolve(ResolveContext resolveContext) {
            super("was not possible to resolve");
            this.traceString = resolveContext.traceString();
        }

        String traceString() {
            return this.traceString;
        }
    }
}

