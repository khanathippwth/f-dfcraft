/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.perms;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.perms.Selectable;
import java.util.Map;
import moss.factions.shade.net.kyori.adventure.text.Component;

public interface PermSelector {
    public Descriptor descriptor();

    public boolean test(Selectable var1, Faction var2);

    default public String serialize() {
        return this.descriptor().getName() + ":" + this.serializeValue();
    }

    default public Component displayName() {
        return this.descriptor().getDisplayName();
    }

    public String serializeValue();

    default public Component displayValue(Faction context) {
        return Component.text(this.serializeValue());
    }

    public static interface Descriptor {
        public PermSelector create(String var1);

        public String getName();

        public Component getDisplayName();

        default public Map<String, String> getOptions(Faction faction) {
            return null;
        }

        default public boolean acceptsEmpty() {
            return false;
        }

        default public String getInstructions() {
            return null;
        }
    }
}

