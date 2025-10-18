/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.massivecraft.factions.cmd.tnt;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CmdTNTDeposit
extends FCommand {
    public CmdTNTDeposit() {
        this.aliases.add("deposit");
        this.aliases.add("d");
        this.requiredArgs.add("amount");
        this.requirements = new CommandRequirements.Builder(Permission.TNT_DEPOSIT).withAction(PermissibleActions.TNTDEPOSIT).memberOnly().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        Player player = commandContext.player;
        if (!commandContext.faction.equals(Board.getInstance().getFactionAt(new FLocation(player.getLocation())))) {
            commandContext.msg(TL.COMMAND_TNT_TERRITORYONLY, new Object[0]);
            return;
        }
        int n = commandContext.argAsInt(0, -1);
        if (n <= 0) {
            commandContext.msg(TL.COMMAND_TNT_DEPOSIT_FAIL_POSITIVE, n);
            return;
        }
        if (!player.getInventory().containsAtLeast(new ItemStack(Material.TNT), n)) {
            commandContext.msg(TL.COMMAND_TNT_DEPOSIT_FAIL_NOTENOUGH, n);
            return;
        }
        if (FactionsPlugin.getInstance().conf().commands().tnt().isAboveMaxStorage(commandContext.faction.getTNTBank() + n)) {
            if (FactionsPlugin.getInstance().conf().commands().tnt().getMaxStorage() == commandContext.faction.getTNTBank()) {
                commandContext.msg(TL.COMMAND_TNT_DEPOSIT_FAIL_FULL, FactionsPlugin.getInstance().conf().commands().tnt().getMaxStorage());
                return;
            }
            n = FactionsPlugin.getInstance().conf().commands().tnt().getMaxStorage() - commandContext.faction.getTNTBank();
        }
        int n2 = n;
        HashMap hashMap = player.getInventory().all(Material.TNT);
        for (Map.Entry entry : hashMap.entrySet()) {
            int n3 = ((ItemStack)entry.getValue()).getAmount();
            int n4 = Math.max(0, n3 - n2);
            n2 -= n3 - n4;
            if (n4 == 0) {
                player.getInventory().setItem(((Integer)entry.getKey()).intValue(), null);
            } else {
                player.getInventory().getItem(((Integer)entry.getKey()).intValue()).setAmount(n4);
            }
            if (n2 != 0) continue;
            break;
        }
        commandContext.faction.setTNTBank(commandContext.faction.getTNTBank() + n);
        commandContext.msg(TL.COMMAND_TNT_DEPOSIT_SUCCESS, commandContext.faction.getTNTBank());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TNT_DEPOSIT_DESCRIPTION;
    }
}

