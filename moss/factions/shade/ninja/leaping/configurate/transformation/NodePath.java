/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.transformation;

import java.util.Iterator;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface NodePath
extends Iterable<Object> {
    public Object get(int var1);

    public int size();

    public Object[] getArray();

    @Override
    public @NonNull Iterator<Object> iterator();
}

