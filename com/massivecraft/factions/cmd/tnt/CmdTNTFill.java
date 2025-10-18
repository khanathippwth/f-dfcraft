/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.Dispenser
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
import com.massivecraft.factions.util.Pair;
import com.massivecraft.factions.util.TL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.ItemStack;

public class CmdTNTFill
extends FCommand {
    public CmdTNTFill() {
        this.aliases.add("fill");
        this.aliases.add("f");
        this.requiredArgs.add("radius");
        this.requiredArgs.add("amount");
        this.requirements = new CommandRequirements.Builder(Permission.TNT_FILL).withAction(PermissibleActions.TNTWITHDRAW).memberOnly().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        if (!commandContext.faction.equals(Board.getInstance().getFactionAt(new FLocation(commandContext.player.getLocation())))) {
            commandContext.msg(TL.COMMAND_TNT_TERRITORYONLY, new Object[0]);
            return;
        }
        int n = commandContext.argAsInt(0, -1);
        int n2 = commandContext.argAsInt(1, -1);
        if (n <= 0 || n2 <= 0) {
            commandContext.msg(TL.COMMAND_TNT_FILL_FAIL_POSITIVE, new Object[0]);
            return;
        }
        if (n2 > commandContext.faction.getTNTBank()) {
            commandContext.msg(TL.COMMAND_TNT_FILL_FAIL_NOTENOUGH, n2);
            return;
        }
        if (n > FactionsPlugin.getInstance().conf().commands().tnt().getMaxRadius()) {
            commandContext.msg(TL.COMMAND_TNT_FILL_FAIL_MAXRADIUS, n, FactionsPlugin.getInstance().conf().commands().tnt().getMaxRadius());
            return;
        }
        List<Dispenser> list = CmdTNTFill.getDispensers(commandContext.player.getLocation(), n, commandContext.faction.getIntId());
        Collections.reverse(list);
        int n3 = n2;
        int n4 = 0;
        boolean bl = true;
        while (n3 > 0 && !list.isEmpty()) {
            int n5 = Math.max(1, n3 / list.size());
            Iterator<Dispenser> iterator = list.iterator();
            while (iterator.hasNext() && n3 >= n5) {
                int n6 = CmdTNTFill.getCount(iterator.next().getInventory().addItem(CmdTNTFill.getStacks(n5)).values());
                n3 -= n5 - n6;
                if (bl && n5 - n6 > 0) {
                    ++n4;
                }
                if (n6 <= 0) continue;
                iterator.remove();
            }
            bl = false;
        }
        commandContext.faction.setTNTBank(commandContext.faction.getTNTBank() - n2 + n3);
        commandContext.msg(TL.COMMAND_TNT_FILL_MESSAGE, n2 - n3, n4, commandContext.faction.getTNTBank());
    }

    static ItemStack[] getStacks(int n) {
        if (n < 65) {
            return new ItemStack[]{new ItemStack(Material.TNT, n)};
        }
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        while (n > 0) {
            arrayList.add(new ItemStack(Material.TNT, Math.min(64, n)));
            n -= Math.min(64, n);
        }
        return arrayList.toArray(new ItemStack[0]);
    }

    static List<Dispenser> getDispensers(Location location, int n, int n2) {
        ArrayList<Pair> arrayList = new ArrayList<Pair>();
        for (int i = location.getBlockX() - n; i <= location.getBlockX() + n; ++i) {
            for (int j = location.getBlockY() - n; j <= location.getBlockY() + n; ++j) {
                for (int k = location.getBlockZ() - n; k <= location.getBlockZ() + n; ++k) {
                    Block block = location.getWorld().getBlockAt(i, j, k);
                    if (Board.getInstance().getIntIdAt(new FLocation(block)) != n2 || block.getType() != Material.DISPENSER) continue;
                    arrayList.add(Pair.of((Dispenser)block.getState(), Math.sqrt((location.getBlockX() - i ^ 2) + (location.getBlockY() - j ^ 2) + (location.getBlockZ() - k ^ 2))));
                }
            }
        }
        arrayList.sort(Comparator.comparing(Pair::getRight));
        return arrayList.stream().map(Pair::getLeft).collect(Collectors.toCollection(ArrayList::new));
    }

    static int getCount(Collection<? extends ItemStack> collection) {
        int n = 0;
        for (ItemStack itemStack : collection) {
            n += itemStack.getAmount();
        }
        return n;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TNT_FILL_DESCRIPTION;
    }
}

