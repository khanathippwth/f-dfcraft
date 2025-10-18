/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.perms;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.PermSelector;
import com.massivecraft.factions.perms.selector.AllSelector;
import com.massivecraft.factions.perms.selector.FactionSelector;
import com.massivecraft.factions.perms.selector.PlayerSelector;
import com.massivecraft.factions.perms.selector.RelationAtLeastSelector;
import com.massivecraft.factions.perms.selector.RelationAtMostSelector;
import com.massivecraft.factions.perms.selector.RelationSingleSelector;
import com.massivecraft.factions.perms.selector.RoleAtLeastSelector;
import com.massivecraft.factions.perms.selector.RoleAtMostSelector;
import com.massivecraft.factions.perms.selector.RoleSingleSelector;
import com.massivecraft.factions.perms.selector.UnknownSelector;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class PermSelectorRegistry {
    private static boolean closed = false;
    private static final Map<String, PermSelector.Descriptor> registry = new ConcurrentHashMap<String, PermSelector.Descriptor>();

    private static void close() {
        closed = true;
    }

    public static PermSelector create(String string, boolean bl) {
        try {
            return PermSelectorRegistry.createOrThrow(string);
        } catch (Exception exception) {
            if (bl) {
                FactionsPlugin.getInstance().getLogger().log(Level.WARNING, "Could not parse perm selector: " + string, exception);
            }
            return new UnknownSelector(string);
        }
    }

    public static PermSelector createOrThrow(String string) {
        String[] stringArray = string.split(":", 2);
        PermSelector.Descriptor descriptor = PermSelectorRegistry.getDescriptor(stringArray[0]);
        if (stringArray.length < 2 || descriptor == null) {
            throw new IllegalArgumentException(stringArray.length < 2 ? "Missing ':'" : "Unknown selector");
        }
        return descriptor.create(stringArray[1]);
    }

    public static Set<String> getSelectors() {
        return registry.keySet();
    }

    public static PermSelector.Descriptor getDescriptor(String string) {
        return string == null ? null : registry.get(string.toLowerCase());
    }

    public static void register(PermSelector.Descriptor descriptor) {
        if (closed) {
            throw new IllegalStateException("Cannot register PermSelectors. Must be done during onLoad().");
        }
        String string = descriptor.getName();
        if (registry.containsKey(string.toLowerCase())) {
            throw new IllegalArgumentException("PermSelector with name " + string + " already registered");
        }
        registry.put(string.toLowerCase(), descriptor);
    }

    static {
        PermSelectorRegistry.register(AllSelector.DESCRIPTOR);
        PermSelectorRegistry.register(FactionSelector.DESCRIPTOR);
        PermSelectorRegistry.register(PlayerSelector.DESCRIPTOR);
        PermSelectorRegistry.register(RoleSingleSelector.DESCRIPTOR);
        PermSelectorRegistry.register(RoleAtLeastSelector.DESCRIPTOR);
        PermSelectorRegistry.register(RoleAtMostSelector.DESCRIPTOR);
        PermSelectorRegistry.register(RelationSingleSelector.DESCRIPTOR);
        PermSelectorRegistry.register(RelationAtLeastSelector.DESCRIPTOR);
        PermSelectorRegistry.register(RelationAtMostSelector.DESCRIPTOR);
    }
}

