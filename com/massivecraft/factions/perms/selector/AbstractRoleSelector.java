/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.perms.selector;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.data.MemoryFaction;
import com.massivecraft.factions.perms.PermSelector;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.perms.Selectable;
import com.massivecraft.factions.perms.selector.AbstractSelector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public abstract class AbstractRoleSelector
extends AbstractSelector {
    protected final Role role;

    public AbstractRoleSelector(PermSelector.Descriptor descriptor, Role role) {
        super(descriptor);
        this.role = Objects.requireNonNull(role);
    }

    public Role getRole() {
        return this.role;
    }

    @Override
    public String serializeValue() {
        return this.role.name();
    }

    @Override
    public Component displayValue(Faction faction) {
        return LegacyComponentSerializer.legacySection().deserialize(String.valueOf(this.role.getColor()) + this.role.getTranslation());
    }

    @Override
    public boolean test(Selectable selectable, Faction faction) {
        if (selectable instanceof FPlayer) {
            FPlayer fPlayer = (FPlayer)selectable;
            if (fPlayer.getFaction() == faction) {
                return this.test(fPlayer.getRole());
            }
        } else if (selectable instanceof Role) {
            return this.test((Role)selectable);
        }
        return false;
    }

    public abstract boolean test(Role var1);

    public static class RoleDescriptor
    extends AbstractSelector.BasicDescriptor {
        private List<PermSelector> roleSelectors;
        private final Function<Role, PermSelector> function;

        public RoleDescriptor(String string2, Supplier<String> supplier, Function<Role, PermSelector> function) {
            super(string2, supplier, (String string) -> (PermSelector)function.apply(Role.fromString(string)));
            this.function = function;
        }

        @Override
        public Map<String, String> getOptions(Faction faction) {
            ArrayList<PermSelector> arrayList = new ArrayList<PermSelector>(this.roleSelectors == null ? (this.roleSelectors = Arrays.stream(Role.values()).map(this.function).collect(Collectors.toList())) : this.roleSelectors);
            arrayList.removeAll(((MemoryFaction)faction).getPermissions().keySet());
            LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>();
            for (PermSelector permSelector : arrayList) {
                linkedHashMap.put(permSelector.serialize(), ((AbstractRoleSelector)permSelector).getRole().getTranslation());
            }
            return linkedHashMap;
        }
    }
}

