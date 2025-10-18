/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.util;

import java.util.concurrent.ConcurrentMap;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface MapFactory {
    public <K, V> @NonNull ConcurrentMap<K, V> create();
}

