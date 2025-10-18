/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.reflect.TypeToken;
import com.google.inject.Injector;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import javax.inject.Singleton;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.GuiceObjectMapper;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMapper;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMapperFactory;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.checkerframework.checker.nullness.qual.NonNull;

@Singleton
public final class GuiceObjectMapperFactory
implements ObjectMapperFactory {
    private final LoadingCache<TypeToken<?>, ObjectMapper<?>> cache = CacheBuilder.newBuilder().weakKeys().maximumSize(512L).build(new CacheLoader<TypeToken<?>, ObjectMapper<?>>(){

        @Override
        public ObjectMapper<?> load(TypeToken<?> typeToken) {
            return new GuiceObjectMapper(GuiceObjectMapperFactory.this.injector, typeToken);
        }
    });
    private final Injector injector;

    @Inject
    protected GuiceObjectMapperFactory(Injector injector) {
        this.injector = injector;
    }

    @Override
    public <T> @NonNull ObjectMapper<T> getMapper(@NonNull Class<T> clazz) {
        return this.getMapper(TypeToken.of(clazz));
    }

    @Override
    public <T> @NonNull ObjectMapper<T> getMapper(@NonNull TypeToken<T> typeToken) {
        Objects.requireNonNull(typeToken, "type");
        try {
            return this.cache.get(typeToken);
        } catch (ExecutionException executionException) {
            if (executionException.getCause() instanceof ObjectMappingException) {
                throw (ObjectMappingException)executionException.getCause();
            }
            throw new RuntimeException(executionException);
        }
    }

    public String toString() {
        return "GuiceObjectMapperFactory{}";
    }
}

