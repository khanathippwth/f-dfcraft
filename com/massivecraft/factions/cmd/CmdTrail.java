/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Particle
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import org.bukkit.Particle;

public class CmdTrail
extends FCommand {
    public CmdTrail() {
        this.aliases.add("trail");
        this.aliases.add("trails");
        this.optionalArgs.put("on/off/effect", "flip");
        this.optionalArgs.put("particle", "particle");
        this.requirements = new CommandRequirements.Builder(Permission.FLY_TRAILS).memberOnly().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        if (!commandContext.argIsSet(0)) {
            commandContext.fPlayer.setFlyTrailsState(!commandContext.fPlayer.getFlyTrailsState());
        } else if (commandContext.argAsString(0).equalsIgnoreCase("effect")) {
            if (commandContext.argIsSet(1)) {
                String string = commandContext.argAsString(1);
                Particle particle = this.plugin.getParticleProvider().effectFromString(string);
                if (particle == null) {
                    commandContext.fPlayer.msg(TL.COMMAND_FLYTRAILS_PARTICLE_INVALID, new Object[0]);
                    return;
                }
                if (commandContext.player.hasPermission(Permission.FLY_TRAILS.node + "." + string)) {
                    commandContext.fPlayer.setFlyTrailsEffect(string);
                } else {
                    commandContext.fPlayer.msg(TL.COMMAND_FLYTRAILS_PARTICLE_PERMS, string);
                }
            } else {
                commandContext.msg(this.getUsageTranslation(), new Object[0]);
            }
        } else {
            boolean bl = commandContext.argAsBool(0);
            commandContext.fPlayer.setFlyTrailsState(bl);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_FLY_DESCRIPTION;
    }
}

