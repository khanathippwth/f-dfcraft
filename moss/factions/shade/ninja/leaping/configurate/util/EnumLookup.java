/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class EnumLookup {
    private static final LoadingCache<Class<? extends Enum<?>>, Map<String, Enum<?>>> ENUM_FIELD_CACHE = CacheBuilder.newBuilder().weakKeys().maximumSize(512L).build(new CacheLoader<Class<? extends Enum<?>>, Map<String, Enum<?>>>(){

        @Override
        public Map<String, Enum<?>> load(@NonNull Class<? extends Enum<?>> clazz) {
            HashMap hashMap = new HashMap();
            for (Enum<?> enum_ : clazz.getEnumConstants()) {
                hashMap.put(enum_.name(), enum_);
                hashMap.putIfAbsent(EnumLookup.processKey(enum_.name()), enum_);
            }
            return ImmutableMap.copyOf(hashMap);
        }
    });

    private EnumLookup() {
    }

    private static @NonNull String processKey(@NonNull String string) {
        return "\ud83c\udf38" + string.toLowerCase().replace("_", "");
    }

    public static <T extends Enum<T>> @NonNull Optional<T> lookupEnum(@NonNull Class<T> clazz, @NonNull String string) {
        try {
            Map<String, Enum<?>> map = ENUM_FIELD_CACHE.get(Objects.requireNonNull(clazz, "clazz"));
            Enum<?> enum_ = map.get(Objects.requireNonNull(string, "key"));
            if (enum_ != null) {
                return Optional.of(enum_);
            }
            return Optional.ofNullable(map.get(EnumLookup.processKey(string)));
        } catch (ExecutionException executionException) {
            return Optional.empty();
        }
    }
}

