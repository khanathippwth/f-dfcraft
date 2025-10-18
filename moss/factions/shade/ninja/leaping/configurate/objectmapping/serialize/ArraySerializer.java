/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize;

import com.google.common.reflect.TypeToken;
import java.lang.reflect.Array;
import java.util.function.Predicate;
import moss.factions.shade.ninja.leaping.configurate.Types;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.AbstractListChildSerializer;
import moss.factions.shade.ninja.leaping.configurate.util.CheckedConsumer;

abstract class ArraySerializer<T>
extends AbstractListChildSerializer<T> {
    ArraySerializer() {
    }

    @Override
    TypeToken<?> getElementType(TypeToken<?> typeToken) {
        return typeToken.getComponentType();
    }

    static class Doubles
    extends ArraySerializer<double[]> {
        Doubles() {
        }

        @Override
        double[] createNew(int n, TypeToken<?> typeToken) {
            return new double[n];
        }

        @Override
        void forEachElement(double[] dArray, CheckedConsumer<Object, ObjectMappingException> checkedConsumer) {
            for (double d : dArray) {
                checkedConsumer.accept(d);
            }
        }

        @Override
        void deserializeSingle(int n, double[] dArray, Object object) {
            Double d = Types.asDouble(object);
            dArray[n] = d == null ? 0.0 : d;
        }
    }

    static class Floats
    extends ArraySerializer<float[]> {
        Floats() {
        }

        @Override
        float[] createNew(int n, TypeToken<?> typeToken) {
            return new float[n];
        }

        @Override
        void forEachElement(float[] fArray, CheckedConsumer<Object, ObjectMappingException> checkedConsumer) {
            for (float f : fArray) {
                checkedConsumer.accept(Float.valueOf(f));
            }
        }

        @Override
        void deserializeSingle(int n, float[] fArray, Object object) {
            Float f = Types.asFloat(object);
            fArray[n] = f == null ? 0.0f : f.floatValue();
        }
    }

    static class Longs
    extends ArraySerializer<long[]> {
        Longs() {
        }

        @Override
        long[] createNew(int n, TypeToken<?> typeToken) {
            return new long[n];
        }

        @Override
        void forEachElement(long[] lArray, CheckedConsumer<Object, ObjectMappingException> checkedConsumer) {
            for (long l : lArray) {
                checkedConsumer.accept(l);
            }
        }

        @Override
        void deserializeSingle(int n, long[] lArray, Object object) {
            Long l = Types.asLong(object);
            lArray[n] = l == null ? 0L : l;
        }
    }

    static class Ints
    extends ArraySerializer<int[]> {
        Ints() {
        }

        @Override
        int[] createNew(int n, TypeToken<?> typeToken) {
            return new int[n];
        }

        @Override
        void forEachElement(int[] nArray, CheckedConsumer<Object, ObjectMappingException> checkedConsumer) {
            for (int n : nArray) {
                checkedConsumer.accept(n);
            }
        }

        @Override
        void deserializeSingle(int n, int[] nArray, Object object) {
            Integer n2 = Types.asInt(object);
            nArray[n] = n2 == null ? 0 : n2;
        }
    }

    static class Shorts
    extends ArraySerializer<short[]> {
        Shorts() {
        }

        @Override
        TypeToken<?> getElementType(TypeToken<?> typeToken) {
            return typeToken.getComponentType();
        }

        @Override
        short[] createNew(int n, TypeToken<?> typeToken) {
            return new short[n];
        }

        @Override
        void forEachElement(short[] sArray, CheckedConsumer<Object, ObjectMappingException> checkedConsumer) {
            for (short s : sArray) {
                checkedConsumer.accept(s);
            }
        }

        @Override
        void deserializeSingle(int n, short[] sArray, Object object) {
            Integer n2 = Types.asInt(object);
            sArray[n] = n2 == null ? (short)0 : n2.shortValue();
        }
    }

    static class Chars
    extends ArraySerializer<char[]> {
        Chars() {
        }

        @Override
        char[] createNew(int n, TypeToken<?> typeToken) {
            return new char[n];
        }

        @Override
        void forEachElement(char[] cArray, CheckedConsumer<Object, ObjectMappingException> checkedConsumer) {
            for (char c : cArray) {
                checkedConsumer.accept(Character.valueOf(c));
            }
        }

        @Override
        void deserializeSingle(int n, char[] cArray, Object object) {
            if (!(object instanceof Character)) {
                throw new ObjectMappingException("Deserialized value must be a Character at index " + n);
            }
            cArray[n] = ((Character)object).charValue();
        }
    }

    static class Bytes
    extends ArraySerializer<byte[]> {
        Bytes() {
        }

        @Override
        byte[] createNew(int n, TypeToken<?> typeToken) {
            return new byte[n];
        }

        @Override
        void forEachElement(byte[] byArray, CheckedConsumer<Object, ObjectMappingException> checkedConsumer) {
            for (byte by : byArray) {
                checkedConsumer.accept(by);
            }
        }

        @Override
        void deserializeSingle(int n, byte[] byArray, Object object) {
            Integer n2 = Types.asInt(object);
            byArray[n] = n2 == null ? (byte)0 : n2.byteValue();
        }
    }

    static class Booleans
    extends ArraySerializer<boolean[]> {
        Booleans() {
        }

        @Override
        boolean[] createNew(int n, TypeToken<?> typeToken) {
            return new boolean[n];
        }

        @Override
        void forEachElement(boolean[] blArray, CheckedConsumer<Object, ObjectMappingException> checkedConsumer) {
            for (boolean bl : blArray) {
                checkedConsumer.accept(bl);
            }
        }

        @Override
        void deserializeSingle(int n, boolean[] blArray, Object object) {
            Boolean bl = Types.asBoolean(object);
            blArray[n] = bl == null ? false : bl;
        }
    }

    static class Objects
    extends ArraySerializer<Object[]> {
        Objects() {
        }

        public static Predicate<TypeToken<Object[]>> predicate() {
            return typeToken -> {
                TypeToken<?> typeToken2 = typeToken.getComponentType();
                return typeToken2 != null && !typeToken2.isPrimitive();
            };
        }

        @Override
        Object[] createNew(int n, TypeToken<?> typeToken) {
            return (Object[])Array.newInstance(typeToken.getRawType(), n);
        }

        @Override
        void forEachElement(Object[] objectArray, CheckedConsumer<Object, ObjectMappingException> checkedConsumer) {
            for (Object object : objectArray) {
                checkedConsumer.accept(object);
            }
        }

        @Override
        void deserializeSingle(int n, Object[] objectArray, Object object) {
            objectArray[n] = object;
        }
    }
}

