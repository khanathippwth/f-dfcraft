/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate;

@Deprecated
public enum ValueType {
    SCALAR,
    MAP,
    LIST,
    NULL;


    public boolean canHaveChildren() {
        return this == MAP || this == LIST;
    }
}

