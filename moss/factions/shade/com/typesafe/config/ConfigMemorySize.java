/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

public final class ConfigMemorySize {
    private final long bytes;

    private ConfigMemorySize(long l) {
        if (l < 0L) {
            throw new IllegalArgumentException("Attempt to construct ConfigMemorySize with negative number: " + l);
        }
        this.bytes = l;
    }

    public static ConfigMemorySize ofBytes(long l) {
        return new ConfigMemorySize(l);
    }

    public long toBytes() {
        return this.bytes;
    }

    public String toString() {
        return "ConfigMemorySize(" + this.bytes + ")";
    }

    public boolean equals(Object object) {
        if (object instanceof ConfigMemorySize) {
            return ((ConfigMemorySize)object).bytes == this.bytes;
        }
        return false;
    }

    public int hashCode() {
        return Long.valueOf(this.bytes).hashCode();
    }
}

