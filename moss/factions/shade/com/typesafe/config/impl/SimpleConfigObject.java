/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigObject;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigRenderOptions;
import moss.factions.shade.com.typesafe.config.ConfigValue;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigObject;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.Container;
import moss.factions.shade.com.typesafe.config.impl.Path;
import moss.factions.shade.com.typesafe.config.impl.ResolveContext;
import moss.factions.shade.com.typesafe.config.impl.ResolveResult;
import moss.factions.shade.com.typesafe.config.impl.ResolveSource;
import moss.factions.shade.com.typesafe.config.impl.ResolveStatus;
import moss.factions.shade.com.typesafe.config.impl.SerializedConfigValue;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfig;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigOrigin;

final class SimpleConfigObject
extends AbstractConfigObject
implements Serializable {
    private static final long serialVersionUID = 2L;
    private final Map<String, AbstractConfigValue> value;
    private final boolean resolved;
    private final boolean ignoresFallbacks;
    private static final String EMPTY_NAME = "empty config";
    private static final SimpleConfigObject emptyInstance = SimpleConfigObject.empty(SimpleConfigOrigin.newSimple("empty config"));

    SimpleConfigObject(ConfigOrigin configOrigin, Map<String, AbstractConfigValue> map, ResolveStatus resolveStatus, boolean bl) {
        super(configOrigin);
        if (map == null) {
            throw new ConfigException.BugOrBroken("creating config object with null map");
        }
        this.value = map;
        this.resolved = resolveStatus == ResolveStatus.RESOLVED;
        this.ignoresFallbacks = bl;
        if (resolveStatus != ResolveStatus.fromValues(map.values())) {
            throw new ConfigException.BugOrBroken("Wrong resolved status on " + this);
        }
    }

    SimpleConfigObject(ConfigOrigin configOrigin, Map<String, AbstractConfigValue> map) {
        this(configOrigin, map, ResolveStatus.fromValues(map.values()), false);
    }

    @Override
    public SimpleConfigObject withOnlyKey(String string) {
        return this.withOnlyPath(Path.newKey(string));
    }

    @Override
    public SimpleConfigObject withoutKey(String string) {
        return this.withoutPath(Path.newKey(string));
    }

    @Override
    protected SimpleConfigObject withOnlyPathOrNull(Path path) {
        String string = path.first();
        Path path2 = path.remainder();
        AbstractConfigValue abstractConfigValue = this.value.get(string);
        if (path2 != null) {
            abstractConfigValue = abstractConfigValue != null && abstractConfigValue instanceof AbstractConfigObject ? ((AbstractConfigObject)abstractConfigValue).withOnlyPathOrNull(path2) : null;
        }
        if (abstractConfigValue == null) {
            return null;
        }
        return new SimpleConfigObject(this.origin(), Collections.singletonMap(string, abstractConfigValue), abstractConfigValue.resolveStatus(), this.ignoresFallbacks);
    }

    @Override
    SimpleConfigObject withOnlyPath(Path path) {
        SimpleConfigObject simpleConfigObject = this.withOnlyPathOrNull(path);
        if (simpleConfigObject == null) {
            return new SimpleConfigObject(this.origin(), Collections.emptyMap(), ResolveStatus.RESOLVED, this.ignoresFallbacks);
        }
        return simpleConfigObject;
    }

    @Override
    SimpleConfigObject withoutPath(Path path) {
        String string = path.first();
        Path path2 = path.remainder();
        AbstractConfigValue abstractConfigValue = this.value.get(string);
        if (abstractConfigValue != null && path2 != null && abstractConfigValue instanceof AbstractConfigObject) {
            abstractConfigValue = ((AbstractConfigObject)abstractConfigValue).withoutPath(path2);
            HashMap<String, AbstractConfigValue> hashMap = new HashMap<String, AbstractConfigValue>(this.value);
            hashMap.put(string, abstractConfigValue);
            return new SimpleConfigObject(this.origin(), hashMap, ResolveStatus.fromValues(hashMap.values()), this.ignoresFallbacks);
        }
        if (path2 != null || abstractConfigValue == null) {
            return this;
        }
        HashMap<String, AbstractConfigValue> hashMap = new HashMap<String, AbstractConfigValue>(this.value.size() - 1);
        for (Map.Entry<String, AbstractConfigValue> entry : this.value.entrySet()) {
            if (entry.getKey().equals(string)) continue;
            hashMap.put(entry.getKey(), entry.getValue());
        }
        return new SimpleConfigObject(this.origin(), hashMap, ResolveStatus.fromValues(hashMap.values()), this.ignoresFallbacks);
    }

    @Override
    public SimpleConfigObject withValue(String string, ConfigValue configValue) {
        Map<String, AbstractConfigValue> map;
        if (configValue == null) {
            throw new ConfigException.BugOrBroken("Trying to store null ConfigValue in a ConfigObject");
        }
        if (this.value.isEmpty()) {
            map = Collections.singletonMap(string, (AbstractConfigValue)configValue);
        } else {
            map = new HashMap<String, AbstractConfigValue>(this.value);
            map.put(string, (AbstractConfigValue)configValue);
        }
        return new SimpleConfigObject(this.origin(), map, ResolveStatus.fromValues(map.values()), this.ignoresFallbacks);
    }

    @Override
    SimpleConfigObject withValue(Path path, ConfigValue configValue) {
        String string = path.first();
        Path path2 = path.remainder();
        if (path2 == null) {
            return this.withValue(string, configValue);
        }
        AbstractConfigValue abstractConfigValue = this.value.get(string);
        if (abstractConfigValue != null && abstractConfigValue instanceof AbstractConfigObject) {
            return this.withValue(string, (ConfigValue)((AbstractConfigObject)abstractConfigValue).withValue(path2, configValue));
        }
        SimpleConfig simpleConfig = ((AbstractConfigValue)configValue).atPath(SimpleConfigOrigin.newSimple("withValue(" + path2.render() + ")"), path2);
        return this.withValue(string, (ConfigValue)simpleConfig.root());
    }

    @Override
    protected AbstractConfigValue attemptPeekWithPartialResolve(String string) {
        return this.value.get(string);
    }

    private SimpleConfigObject newCopy(ResolveStatus resolveStatus, ConfigOrigin configOrigin, boolean bl) {
        return new SimpleConfigObject(configOrigin, this.value, resolveStatus, bl);
    }

    @Override
    protected SimpleConfigObject newCopy(ResolveStatus resolveStatus, ConfigOrigin configOrigin) {
        return this.newCopy(resolveStatus, configOrigin, this.ignoresFallbacks);
    }

    @Override
    protected SimpleConfigObject withFallbacksIgnored() {
        if (this.ignoresFallbacks) {
            return this;
        }
        return this.newCopy(this.resolveStatus(), this.origin(), true);
    }

    @Override
    ResolveStatus resolveStatus() {
        return ResolveStatus.fromBoolean(this.resolved);
    }

    @Override
    public SimpleConfigObject replaceChild(AbstractConfigValue abstractConfigValue, AbstractConfigValue abstractConfigValue2) {
        HashMap<String, AbstractConfigValue> hashMap = new HashMap<String, AbstractConfigValue>(this.value);
        for (Map.Entry<String, AbstractConfigValue> entry : hashMap.entrySet()) {
            if (entry.getValue() != abstractConfigValue) continue;
            if (abstractConfigValue2 != null) {
                entry.setValue(abstractConfigValue2);
            } else {
                hashMap.remove(entry.getKey());
            }
            return new SimpleConfigObject(this.origin(), hashMap, ResolveStatus.fromValues(hashMap.values()), this.ignoresFallbacks);
        }
        throw new ConfigException.BugOrBroken("SimpleConfigObject.replaceChild did not find " + abstractConfigValue + " in " + this);
    }

    @Override
    public boolean hasDescendant(AbstractConfigValue abstractConfigValue) {
        for (AbstractConfigValue abstractConfigValue2 : this.value.values()) {
            if (abstractConfigValue2 != abstractConfigValue) continue;
            return true;
        }
        for (AbstractConfigValue abstractConfigValue2 : this.value.values()) {
            if (!(abstractConfigValue2 instanceof Container) || !((Container)((Object)abstractConfigValue2)).hasDescendant(abstractConfigValue)) continue;
            return true;
        }
        return false;
    }

    @Override
    protected boolean ignoresFallbacks() {
        return this.ignoresFallbacks;
    }

    @Override
    public Map<String, Object> unwrapped() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        for (Map.Entry<String, AbstractConfigValue> entry : this.value.entrySet()) {
            hashMap.put(entry.getKey(), entry.getValue().unwrapped());
        }
        return hashMap;
    }

    @Override
    protected SimpleConfigObject mergedWithObject(AbstractConfigObject abstractConfigObject) {
        this.requireNotIgnoringFallbacks();
        if (!(abstractConfigObject instanceof SimpleConfigObject)) {
            throw new ConfigException.BugOrBroken("should not be reached (merging non-SimpleConfigObject)");
        }
        SimpleConfigObject simpleConfigObject = (SimpleConfigObject)abstractConfigObject;
        boolean bl = false;
        boolean bl2 = true;
        HashMap<String, AbstractConfigValue> hashMap = new HashMap<String, AbstractConfigValue>();
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.addAll(this.keySet());
        hashSet.addAll(simpleConfigObject.keySet());
        for (String string : hashSet) {
            AbstractConfigValue abstractConfigValue = this.value.get(string);
            AbstractConfigValue abstractConfigValue2 = simpleConfigObject.value.get(string);
            AbstractConfigValue abstractConfigValue3 = abstractConfigValue == null ? abstractConfigValue2 : (abstractConfigValue2 == null ? abstractConfigValue : abstractConfigValue.withFallback(abstractConfigValue2));
            hashMap.put(string, abstractConfigValue3);
            if (abstractConfigValue != abstractConfigValue3) {
                bl = true;
            }
            if (abstractConfigValue3.resolveStatus() != ResolveStatus.UNRESOLVED) continue;
            bl2 = false;
        }
        Object object = ResolveStatus.fromBoolean(bl2);
        boolean bl3 = simpleConfigObject.ignoresFallbacks();
        if (bl) {
            return new SimpleConfigObject(SimpleConfigObject.mergeOrigins(this, simpleConfigObject), (Map<String, AbstractConfigValue>)hashMap, (ResolveStatus)((Object)object), bl3);
        }
        if (object != this.resolveStatus() || bl3 != this.ignoresFallbacks()) {
            return this.newCopy((ResolveStatus)((Object)object), this.origin(), bl3);
        }
        return this;
    }

    private SimpleConfigObject modify(AbstractConfigValue.NoExceptionsModifier noExceptionsModifier) {
        try {
            return this.modifyMayThrow(noExceptionsModifier);
        } catch (RuntimeException runtimeException) {
            throw runtimeException;
        } catch (Exception exception) {
            throw new ConfigException.BugOrBroken("unexpected checked exception", exception);
        }
    }

    private SimpleConfigObject modifyMayThrow(AbstractConfigValue.Modifier modifier) {
        HashMap<String, AbstractConfigValue> hashMap = null;
        for (String string : this.keySet()) {
            Object object;
            AbstractConfigValue object2 = modifier.modifyChildMayThrow(string, (AbstractConfigValue)(object = this.value.get(string)));
            if (object2 == object) continue;
            if (hashMap == null) {
                hashMap = new HashMap<String, AbstractConfigValue>();
            }
            hashMap.put(string, object2);
        }
        if (hashMap == null) {
            return this;
        }
        HashMap hashMap2 = new HashMap();
        boolean bl = false;
        for (String string : this.keySet()) {
            AbstractConfigValue abstractConfigValue;
            if (hashMap.containsKey(string)) {
                abstractConfigValue = (AbstractConfigValue)hashMap.get(string);
                if (abstractConfigValue == null) continue;
                hashMap2.put(string, abstractConfigValue);
                if (abstractConfigValue.resolveStatus() != ResolveStatus.UNRESOLVED) continue;
                bl = true;
                continue;
            }
            abstractConfigValue = this.value.get(string);
            hashMap2.put(string, abstractConfigValue);
            if (abstractConfigValue.resolveStatus() != ResolveStatus.UNRESOLVED) continue;
            bl = true;
        }
        return new SimpleConfigObject(this.origin(), hashMap2, bl ? ResolveStatus.UNRESOLVED : ResolveStatus.RESOLVED, this.ignoresFallbacks());
    }

    @Override
    ResolveResult<? extends AbstractConfigObject> resolveSubstitutions(ResolveContext resolveContext, ResolveSource resolveSource) {
        if (this.resolveStatus() == ResolveStatus.RESOLVED) {
            return ResolveResult.make(resolveContext, this);
        }
        ResolveSource resolveSource2 = resolveSource.pushParent(this);
        try {
            ResolveModifier resolveModifier = new ResolveModifier(resolveContext, resolveSource2);
            SimpleConfigObject simpleConfigObject = this.modifyMayThrow(resolveModifier);
            return ResolveResult.make(resolveModifier.context, simpleConfigObject).asObjectResult();
        } catch (AbstractConfigValue.NotPossibleToResolve notPossibleToResolve) {
            throw notPossibleToResolve;
        } catch (RuntimeException runtimeException) {
            throw runtimeException;
        } catch (Exception exception) {
            throw new ConfigException.BugOrBroken("unexpected checked exception", exception);
        }
    }

    @Override
    SimpleConfigObject relativized(final Path path) {
        return this.modify(new AbstractConfigValue.NoExceptionsModifier(){

            @Override
            public AbstractConfigValue modifyChild(String string, AbstractConfigValue abstractConfigValue) {
                return abstractConfigValue.relativized(path);
            }
        });
    }

    @Override
    protected void render(StringBuilder stringBuilder, int n, boolean bl, ConfigRenderOptions configRenderOptions) {
        if (this.isEmpty()) {
            stringBuilder.append("{}");
        } else {
            int n2;
            boolean bl2;
            boolean bl3 = bl2 = configRenderOptions.getJson() || !bl;
            if (bl2) {
                n2 = n + 1;
                stringBuilder.append("{");
                if (configRenderOptions.getFormatted()) {
                    stringBuilder.append('\n');
                }
            } else {
                n2 = n;
            }
            int n3 = 0;
            String[] stringArray = this.keySet().toArray(new String[this.size()]);
            Arrays.sort(stringArray, new RenderComparator());
            for (String string : stringArray) {
                Object object;
                AbstractConfigValue abstractConfigValue = this.value.get(string);
                if (configRenderOptions.getOriginComments()) {
                    for (String string2 : object = abstractConfigValue.origin().description().split("\n")) {
                        SimpleConfigObject.indent(stringBuilder, n + 1, configRenderOptions);
                        stringBuilder.append('#');
                        if (!string2.isEmpty()) {
                            stringBuilder.append(' ');
                        }
                        stringBuilder.append(string2);
                        stringBuilder.append("\n");
                    }
                }
                if (configRenderOptions.getComments()) {
                    object = abstractConfigValue.origin().comments().iterator();
                    while (object.hasNext()) {
                        String string3 = (String)object.next();
                        SimpleConfigObject.indent(stringBuilder, n2, configRenderOptions);
                        stringBuilder.append("#");
                        if (!string3.startsWith(" ")) {
                            stringBuilder.append(' ');
                        }
                        stringBuilder.append(string3);
                        stringBuilder.append("\n");
                    }
                }
                SimpleConfigObject.indent(stringBuilder, n2, configRenderOptions);
                abstractConfigValue.render(stringBuilder, n2, false, string, configRenderOptions);
                if (configRenderOptions.getFormatted()) {
                    if (configRenderOptions.getJson()) {
                        stringBuilder.append(",");
                        n3 = 2;
                    } else {
                        n3 = 1;
                    }
                    stringBuilder.append('\n');
                    continue;
                }
                stringBuilder.append(",");
                n3 = 1;
            }
            stringBuilder.setLength(stringBuilder.length() - n3);
            if (bl2) {
                if (configRenderOptions.getFormatted()) {
                    stringBuilder.append('\n');
                    if (bl2) {
                        SimpleConfigObject.indent(stringBuilder, n, configRenderOptions);
                    }
                }
                stringBuilder.append("}");
            }
        }
        if (bl && configRenderOptions.getFormatted()) {
            stringBuilder.append('\n');
        }
    }

    @Override
    public AbstractConfigValue get(Object object) {
        return this.value.get(object);
    }

    private static boolean mapEquals(Map<String, ConfigValue> map, Map<String, ConfigValue> map2) {
        Set<String> set;
        if (map == map2) {
            return true;
        }
        Set<String> set2 = map.keySet();
        if (!set2.equals(set = map2.keySet())) {
            return false;
        }
        for (String string : set2) {
            if (map.get(string).equals(map2.get(string))) continue;
            return false;
        }
        return true;
    }

    private static int mapHash(Map<String, ConfigValue> map) {
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.addAll(map.keySet());
        Collections.sort(arrayList);
        int n = 0;
        for (String string : arrayList) {
            n += map.get(string).hashCode();
        }
        return 41 * (41 + arrayList.hashCode()) + n;
    }

    @Override
    protected boolean canEqual(Object object) {
        return object instanceof ConfigObject;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ConfigObject) {
            return this.canEqual(object) && SimpleConfigObject.mapEquals(this, (ConfigObject)object);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return SimpleConfigObject.mapHash(this);
    }

    @Override
    public boolean containsKey(Object object) {
        return this.value.containsKey(object);
    }

    @Override
    public Set<String> keySet() {
        return this.value.keySet();
    }

    @Override
    public boolean containsValue(Object object) {
        return this.value.containsValue(object);
    }

    @Override
    public Set<Map.Entry<String, ConfigValue>> entrySet() {
        HashSet<Map.Entry<String, ConfigValue>> hashSet = new HashSet<Map.Entry<String, ConfigValue>>();
        for (Map.Entry<String, AbstractConfigValue> entry : this.value.entrySet()) {
            hashSet.add(new AbstractMap.SimpleImmutableEntry<String, AbstractConfigValue>(entry.getKey(), entry.getValue()));
        }
        return hashSet;
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    @Override
    public int size() {
        return this.value.size();
    }

    @Override
    public Collection<ConfigValue> values() {
        return new HashSet<ConfigValue>(this.value.values());
    }

    static final SimpleConfigObject empty() {
        return emptyInstance;
    }

    static final SimpleConfigObject empty(ConfigOrigin configOrigin) {
        if (configOrigin == null) {
            return SimpleConfigObject.empty();
        }
        return new SimpleConfigObject(configOrigin, Collections.emptyMap());
    }

    static final SimpleConfigObject emptyMissing(ConfigOrigin configOrigin) {
        return new SimpleConfigObject(SimpleConfigOrigin.newSimple(configOrigin.description() + " (not found)"), Collections.emptyMap());
    }

    private Object writeReplace() {
        return new SerializedConfigValue(this);
    }

    private static final class RenderComparator
    implements Comparator<String>,
    Serializable {
        private static final long serialVersionUID = 1L;

        private RenderComparator() {
        }

        private static boolean isAllDigits(String string) {
            int n = string.length();
            if (n == 0) {
                return false;
            }
            for (int i = 0; i < n; ++i) {
                char c = string.charAt(i);
                if (Character.isDigit(c)) continue;
                return false;
            }
            return true;
        }

        @Override
        public int compare(String string, String string2) {
            boolean bl = RenderComparator.isAllDigits(string);
            boolean bl2 = RenderComparator.isAllDigits(string2);
            if (bl && bl2) {
                return new BigInteger(string).compareTo(new BigInteger(string2));
            }
            if (bl) {
                return -1;
            }
            if (bl2) {
                return 1;
            }
            return string.compareTo(string2);
        }
    }

    private static final class ResolveModifier
    implements AbstractConfigValue.Modifier {
        final Path originalRestrict;
        ResolveContext context;
        final ResolveSource source;

        ResolveModifier(ResolveContext resolveContext, ResolveSource resolveSource) {
            this.context = resolveContext;
            this.source = resolveSource;
            this.originalRestrict = resolveContext.restrictToChild();
        }

        @Override
        public AbstractConfigValue modifyChildMayThrow(String string, AbstractConfigValue abstractConfigValue) {
            if (this.context.isRestrictedToChild()) {
                if (string.equals(this.context.restrictToChild().first())) {
                    Path path = this.context.restrictToChild().remainder();
                    if (path != null) {
                        ResolveResult<? extends AbstractConfigValue> resolveResult = this.context.restrict(path).resolve(abstractConfigValue, this.source);
                        this.context = resolveResult.context.unrestricted().restrict(this.originalRestrict);
                        return resolveResult.value;
                    }
                    return abstractConfigValue;
                }
                return abstractConfigValue;
            }
            ResolveResult<? extends AbstractConfigValue> resolveResult = this.context.unrestricted().resolve(abstractConfigValue, this.source);
            this.context = resolveResult.context.unrestricted().restrict(this.originalRestrict);
            return resolveResult.value;
        }
    }
}

