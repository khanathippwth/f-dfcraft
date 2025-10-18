/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.BrigadierProvider;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CommandRequirements {
    private final Permission permission;
    private final boolean playerOnly;
    private final boolean memberOnly;
    private final Role role;
    private final PermissibleAction action;
    private final Class<? extends BrigadierProvider> brigadier;
    private boolean errorOnManyArgs;
    private boolean disableOnLock;

    private CommandRequirements(Permission permission, boolean bl, boolean bl2, Role role, PermissibleAction permissibleAction, Class<? extends BrigadierProvider> clazz) {
        this.permission = permission;
        this.playerOnly = bl;
        this.memberOnly = bl2;
        this.role = role;
        this.action = permissibleAction;
        this.brigadier = clazz;
    }

    public boolean computeRequirements(CommandContext commandContext, boolean bl) {
        if (this.permission == null) {
            return true;
        }
        if (commandContext.player != null) {
            if (!commandContext.fPlayer.hasFaction() && this.memberOnly) {
                if (bl) {
                    commandContext.msg(TL.GENERIC_MEMBERONLY, new Object[0]);
                }
                return false;
            }
            if (!FactionsPlugin.getInstance().getPermUtil().has(commandContext.sender, this.permission.node, bl)) {
                return false;
            }
            if (this.action != null) {
                boolean bl2 = commandContext.faction.hasAccess(commandContext.fPlayer, this.action, commandContext.fPlayer.getLastStoodAt());
                if (!bl2) {
                    if (bl) {
                        commandContext.msg(TL.GENERIC_NOPERMISSION, this.action.getShortDescription());
                    }
                    return false;
                }
                return true;
            }
            if (this.role != null && !commandContext.fPlayer.getRole().isAtLeast(this.role) && bl) {
                commandContext.msg(TL.GENERIC_YOUMUSTBE, new Object[]{this.role.translation});
            }
            return this.role == null || commandContext.fPlayer.getRole().isAtLeast(this.role);
        }
        if (this.playerOnly) {
            if (bl) {
                commandContext.sender.sendMessage(TL.GENERIC_PLAYERONLY.toString());
            }
            return false;
        }
        return commandContext.sender.hasPermission(this.permission.node);
    }

    public boolean isErrorOnManyArgs() {
        return this.errorOnManyArgs;
    }

    public boolean isDisableOnLock() {
        return this.disableOnLock;
    }

    public Class<? extends BrigadierProvider> getBrigadier() {
        return this.brigadier;
    }

    public static class Builder {
        private final Permission permission;
        private boolean playerOnly = false;
        private boolean memberOnly = false;
        private Role role = null;
        private PermissibleAction action;
        private Class<? extends BrigadierProvider> brigadier;
        private boolean errorOnManyArgs = true;
        private boolean disableOnLock = true;

        public Builder(Permission permission) {
            this.permission = permission;
        }

        public Builder playerOnly() {
            this.playerOnly = true;
            return this;
        }

        public Builder memberOnly() {
            this.playerOnly = true;
            this.memberOnly = true;
            return this;
        }

        public Builder withRole(Role role) {
            this.role = role;
            return this;
        }

        public Builder withAction(PermissibleAction permissibleAction) {
            this.action = permissibleAction;
            return this;
        }

        public Builder brigadier(Class<? extends BrigadierProvider> clazz) {
            this.brigadier = clazz;
            return this;
        }

        public CommandRequirements build() {
            CommandRequirements commandRequirements = new CommandRequirements(this.permission, this.playerOnly, this.memberOnly, this.role, this.action, this.brigadier);
            commandRequirements.errorOnManyArgs = this.errorOnManyArgs;
            commandRequirements.disableOnLock = this.disableOnLock;
            return commandRequirements;
        }

        public Builder noErrorOnManyArgs() {
            this.errorOnManyArgs = false;
            return this;
        }

        public Builder noDisableOnLock() {
            this.disableOnLock = false;
            return this;
        }
    }
}

