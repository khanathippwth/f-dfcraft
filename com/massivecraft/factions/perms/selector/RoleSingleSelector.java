/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.perms.selector;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.PermSelector;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.perms.selector.AbstractRoleSelector;

public class RoleSingleSelector
extends AbstractRoleSelector {
    public static final String NAME = "role-single";
    public static final PermSelector.Descriptor DESCRIPTOR = new AbstractRoleSelector.RoleDescriptor("role-single", FactionsPlugin.getInstance().tl().permissions().selectors().roleSingle()::getDisplayName, RoleSingleSelector::new);

    public RoleSingleSelector(Role role) {
        super(DESCRIPTOR, role);
    }

    @Override
    public boolean test(Role role) {
        return role == this.role;
    }
}

