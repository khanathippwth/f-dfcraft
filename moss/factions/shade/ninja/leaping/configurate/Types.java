/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class Types {
    private Types() {
    }

    public static @Nullable String asString(@Nullable Object object) {
        return object == null ? null : object.toString();
    }

    public static @Nullable String strictAsString(@Nullable Object object) {
        return object instanceof String ? (String)object : null;
    }

    public static @Nullable Float asFloat(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Float) {
            return (Float)object;
        }
        if (object instanceof Integer) {
            return Float.valueOf(((Number)object).floatValue());
        }
        try {
            return Float.valueOf(Float.parseFloat(object.toString()));
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }

    public static @Nullable Float strictAsFloat(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Float || object instanceof Integer) {
            return Float.valueOf(((Number)object).floatValue());
        }
        return null;
    }

    public static @Nullable Double asDouble(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Double) {
            return (Double)object;
        }
        if (object instanceof Integer || object instanceof Long || object instanceof Float) {
            return ((Number)object).doubleValue();
        }
        try {
            return Double.parseDouble(object.toString());
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }

    public static @Nullable Double strictAsDouble(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Double || object instanceof Float || object instanceof Integer || object instanceof Long) {
            return ((Number)object).doubleValue();
        }
        return null;
    }

    public static @Nullable Integer asInt(@Nullable Object object) {
        double d;
        if (object == null) {
            return null;
        }
        if (object instanceof Integer) {
            return (Integer)object;
        }
        if ((object instanceof Float || object instanceof Double) && (d = ((Number)object).doubleValue()) == Math.floor(d)) {
            return (int)d;
        }
        try {
            return Integer.parseInt(object.toString());
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }

    public static @Nullable Integer strictAsInt(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        return object instanceof Integer ? (Integer)object : null;
    }

    public static @Nullable Long asLong(@Nullable Object object) {
        double d;
        if (object == null) {
            return null;
        }
        if (object instanceof Long) {
            return (Long)object;
        }
        if (object instanceof Integer) {
            return ((Number)object).longValue();
        }
        if ((object instanceof Float || object instanceof Double) && (d = ((Number)object).doubleValue()) == Math.floor(d)) {
            return (long)d;
        }
        try {
            return Long.parseLong(object.toString());
        } catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }

    public static @Nullable Long strictAsLong(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Long) {
            return (Long)object;
        }
        if (object instanceof Integer) {
            return ((Number)object).longValue();
        }
        return null;
    }

    public static @Nullable Boolean asBoolean(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Boolean) {
            return (Boolean)object;
        }
        if (object instanceof Number) {
            return !object.equals(0);
        }
        String string = object.toString();
        if (string.equals("true") || string.equals("t") || string.equals("yes") || string.equals("y") || string.equals("1")) {
            return true;
        }
        if (string.equals("false") || string.equals("f") || string.equals("no") || string.equals("n") || string.equals("0")) {
            return false;
        }
        return null;
    }

    public static @Nullable Boolean strictAsBoolean(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        return object instanceof Boolean ? (Boolean)object : null;
    }
}

