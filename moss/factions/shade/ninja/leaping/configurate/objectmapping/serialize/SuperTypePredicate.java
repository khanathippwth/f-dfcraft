/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Predicate;

final class SuperTypePredicate
implements Predicate<TypeToken<?>> {
    private static final MethodHandle SUPERTYPE_TEST;
    private final TypeToken<?> type;

    SuperTypePredicate(TypeToken<?> typeToken) {
        this.type = typeToken;
    }

    @Override
    public boolean test(TypeToken<?> typeToken) {
        try {
            return SUPERTYPE_TEST.invokeExact(this.type, typeToken);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return false;
        }
    }

    static {
        MethodHandle methodHandle;
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodType methodType = MethodType.methodType(Boolean.TYPE, TypeToken.class);
        try {
            try {
                methodHandle = lookup.findVirtual(TypeToken.class, "isSupertypeOf", methodType);
            } catch (NoSuchMethodException noSuchMethodException) {
                try {
                    methodHandle = lookup.findVirtual(TypeToken.class, "isAssignableFrom", methodType);
                } catch (NoSuchMethodException noSuchMethodException2) {
                    throw new RuntimeException("Unable to get TypeToken#isSupertypeOf or TypeToken#isAssignableFrom method");
                }
            }
        } catch (IllegalAccessException illegalAccessException) {
            throw new ExceptionInInitializerError("Could not access isSupertypeOf/isAssignableFrom method in TypeToken");
        }
        SUPERTYPE_TEST = methodHandle;
    }
}

