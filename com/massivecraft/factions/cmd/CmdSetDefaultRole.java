/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdSetDefaultRole
extends FCommand {
    public CmdSetDefaultRole() {
        this.aliases.add("defaultrole");
        this.aliases.add("defaultrank");
        this.aliases.add("default");
        this.aliases.add("def");
        this.requiredArgs.add("role");
        this.requirements = new CommandRequirements.Builder(Permission.DEFAULTRANK).memberOnly().withRole(Role.ADMIN).build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        Role role = Role.fromString(commandContext.argAsString(0).toUpperCase());
        if (role == null) {
            commandContext.msg(TL.COMMAND_SETDEFAULTROLE_INVALIDROLE, commandContext.argAsString(0));
            return;
        }
        if (role == Role.ADMIN) {
            commandContext.msg(TL.COMMAND_SETDEFAULTROLE_NOTTHATROLE, commandContext.argAsString(0));
            return;
        }
        commandContext.faction.setDefaultRole(role);
        commandContext.msg(TL.COMMAND_SETDEFAULTROLE_SUCCESS, role.nicename);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETDEFAULTROLE_DESCRIPTION;
    }
}

