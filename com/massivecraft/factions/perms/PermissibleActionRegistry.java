/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.perms;

import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.PermissibleActions;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PermissibleActionRegistry {
    private static boolean closed = false;
    private static final Map<String, PermissibleAction> registry = new ConcurrentHashMap<String, PermissibleAction>();

    private static void close() {
        closed = true;
    }

    public static PermissibleAction get(String string) {
        return string == null ? null : registry.get(string.toLowerCase());
    }

    public static Collection<? extends PermissibleAction> getActions() {
        return new HashSet<PermissibleAction>(registry.values());
    }

    public static void register(PermissibleAction permissibleAction) {
        if (closed) {
            throw new IllegalStateException("Cannot register PermissibleActions. Must be done during onLoad().");
        }
        if (registry.containsKey(permissibleAction.getName().toLowerCase())) {
            throw new IllegalArgumentException("Permissible action with name " + permissibleAction.getName() + " already registered");
        }
        registry.put(permissibleAction.getName().toLowerCase(), permissibleAction);
    }

    static {
        for (PermissibleActions permissibleActions : PermissibleActions.values()) {
            PermissibleActionRegistry.register(permissibleActions);
        }
    }
}

