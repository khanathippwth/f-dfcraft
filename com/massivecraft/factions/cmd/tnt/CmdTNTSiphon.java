/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Dispenser
 */
package com.massivecraft.factions.cmd.tnt;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.tnt.CmdTNTFill;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;

public class CmdTNTSiphon
extends FCommand {
    public CmdTNTSiphon() {
        this.aliases.add("siphon");
        this.aliases.add("s");
        this.requiredArgs.add("radius");
        this.optionalArgs.put("amount", "all");
        this.requirements = new CommandRequirements.Builder(Permission.TNT_SIPHON).withAction(PermissibleActions.TNTDEPOSIT).memberOnly().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        if (!commandContext.faction.equals(Board.getInstance().getFactionAt(new FLocation(commandContext.player.getLocation())))) {
            commandContext.msg(TL.COMMAND_TNT_TERRITORYONLY, new Object[0]);
            return;
        }
        int n = commandContext.argAsInt(0, -1);
        int n2 = commandContext.argAsInt(1, -1);
        if (n <= 0) {
            commandContext.msg(TL.COMMAND_TNT_SIPHON_FAIL_POSITIVE, new Object[0]);
            return;
        }
        if (FactionsPlugin.getInstance().conf().commands().tnt().isAboveMaxStorage(commandContext.faction.getTNTBank() + 1)) {
            commandContext.msg(TL.COMMAND_TNT_SIPHON_FAIL_FULL, new Object[0]);
            return;
        }
        if (n > FactionsPlugin.getInstance().conf().commands().tnt().getMaxRadius()) {
            commandContext.msg(TL.COMMAND_TNT_SIPHON_FAIL_MAXRADIUS, n, FactionsPlugin.getInstance().conf().commands().tnt().getMaxRadius());
            return;
        }
        List<Dispenser> list = CmdTNTFill.getDispensers(commandContext.player.getLocation(), n, commandContext.faction.getIntId());
        int n3 = FactionsPlugin.getInstance().conf().commands().tnt().getMaxStorage() < 0 ? Integer.MAX_VALUE : FactionsPlugin.getInstance().conf().commands().tnt().getMaxStorage();
        if (n2 > 0 && n2 < (n3 -= commandContext.faction.getTNTBank())) {
            n3 = n2;
        }
        int n4 = n3;
        for (Dispenser dispenser : list) {
            if (n4 > 576) {
                n4 -= CmdTNTFill.getCount(dispenser.getInventory().all(Material.TNT).values());
                dispenser.getInventory().remove(Material.TNT);
            } else {
                n4 = CmdTNTFill.getCount(dispenser.getInventory().removeItem(CmdTNTFill.getStacks(n4)).values());
            }
            if (n4 != 0) continue;
            break;
        }
        int n5 = n3 - n4;
        commandContext.faction.setTNTBank(commandContext.faction.getTNTBank() + n5);
        commandContext.msg(TL.COMMAND_TNT_SIPHON_MESSAGE, n5, commandContext.faction.getTNTBank());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TNT_SIPHON_DESCRIPTION;
    }
}

