/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping;

import com.google.common.reflect.TypeToken;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMapper;
import org.checkerframework.checker.nullness.qual.NonNull;

class GuiceObjectMapper<T>
extends ObjectMapper<T> {
    private final Injector injector;
    private final Key<T> typeKey;

    protected GuiceObjectMapper(@NonNull Injector injector, @NonNull TypeToken<T> typeToken) {
        super(typeToken);
        this.injector = injector;
        this.typeKey = Key.get(typeToken.getType());
    }

    @Override
    public boolean canCreateInstances() {
        try {
            this.injector.getProvider(this.typeKey);
            return true;
        } catch (ConfigurationException configurationException) {
            return false;
        }
    }

    @Override
    protected T constructObject() {
        return this.injector.getInstance(this.typeKey);
    }
}

