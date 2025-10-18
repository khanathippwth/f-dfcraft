/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.reflect.TypeToken;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMapper;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMapperFactory;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DefaultObjectMapperFactory
implements ObjectMapperFactory {
    private static final ObjectMapperFactory INSTANCE = new DefaultObjectMapperFactory();
    private final LoadingCache<TypeToken<?>, ObjectMapper<?>> mapperCache = CacheBuilder.newBuilder().weakKeys().maximumSize(500L).build(new CacheLoader<TypeToken<?>, ObjectMapper<?>>(){

        @Override
        public ObjectMapper<?> load(TypeToken<?> typeToken) {
            return new ObjectMapper(typeToken);
        }
    });

    public static @NonNull ObjectMapperFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public <T> @NonNull ObjectMapper<T> getMapper(@NonNull Class<T> clazz) {
        return this.getMapper(TypeToken.of(clazz));
    }

    @Override
    public <T> @NonNull ObjectMapper<T> getMapper(@NonNull TypeToken<T> typeToken) {
        Objects.requireNonNull(typeToken, "type");
        try {
            return this.mapperCache.get(typeToken);
        } catch (ExecutionException executionException) {
            if (executionException.getCause() instanceof ObjectMappingException) {
                throw (ObjectMappingException)executionException.getCause();
            }
            throw new ObjectMappingException(executionException);
        }
    }

    public String toString() {
        return "DefaultObjectMapperFactory{}";
    }
}

