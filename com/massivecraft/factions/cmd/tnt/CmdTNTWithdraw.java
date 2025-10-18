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
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CmdTNTWithdraw
extends FCommand {
    public CmdTNTWithdraw() {
        this.aliases.add("withdraw");
        this.aliases.add("w");
        this.requiredArgs.add("amount");
        this.requirements = new CommandRequirements.Builder(Permission.TNT_WITHDRAW).withAction(PermissibleActions.TNTWITHDRAW).memberOnly().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        int n;
        Player player = commandContext.player;
        if (!commandContext.faction.equals(Board.getInstance().getFactionAt(new FLocation(player.getLocation())))) {
            commandContext.msg(TL.COMMAND_TNT_TERRITORYONLY, new Object[0]);
            return;
        }
        int n2 = commandContext.argAsInt(0, -1);
        if (n2 <= 0) {
            commandContext.msg(TL.COMMAND_TNT_WITHDRAW_FAIL_POSITIVE, n2);
            return;
        }
        if (commandContext.faction.getTNTBank() < n2) {
            commandContext.msg(TL.COMMAND_TNT_WITHDRAW_FAIL_NOTENOUGH, n2);
            return;
        }
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        for (int i = n2; i > 0; i -= n) {
            n = Math.min(i, 64);
            arrayList.add(new ItemStack(Material.TNT, n));
        }
        HashMap hashMap = player.getInventory().addItem(arrayList.toArray(new ItemStack[0]));
        int n3 = 0;
        for (ItemStack itemStack : hashMap.values()) {
            n3 += itemStack.getAmount();
        }
        commandContext.faction.setTNTBank(commandContext.faction.getTNTBank() - n2 + n3);
        commandContext.msg(TL.COMMAND_TNT_WITHDRAW_MESSAGE, n2 - n3, commandContext.faction.getTNTBank());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TNT_WITHDRAW_DESCRIPTION;
    }
}

