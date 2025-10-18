/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.perms.selector;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.PermSelector;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.perms.selector.AbstractRoleSelector;

public class RoleAtMostSelector
extends AbstractRoleSelector {
    public static final String NAME = "role-atmost";
    public static final PermSelector.Descriptor DESCRIPTOR = new AbstractRoleSelector.RoleDescriptor("role-atmost", FactionsPlugin.getInstance().tl().permissions().selectors().roleAtMost()::getDisplayName, RoleAtMostSelector::new);

    public RoleAtMostSelector(Role role) {
        super(DESCRIPTOR, role);
    }

    @Override
    public boolean test(Role role) {
        return role != null && role.isAtMost(this.role);
    }
}

