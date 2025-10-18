/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping;

import com.google.common.reflect.TypeToken;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMapper;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface ObjectMapperFactory {
    public <T> @NonNull ObjectMapper<T> getMapper(@NonNull Class<T> var1) throws ObjectMappingException;

    default public <T> @NonNull ObjectMapper<T> getMapper(@NonNull TypeToken<T> type) throws ObjectMappingException {
        return this.getMapper(type.getRawType());
    }
}

