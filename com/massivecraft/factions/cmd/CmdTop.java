/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.top.FTopBalanceValue;
import com.massivecraft.factions.cmd.top.FTopFacValPair;
import com.massivecraft.factions.cmd.top.FTopFoundedValue;
import com.massivecraft.factions.cmd.top.FTopGTIntValue;
import com.massivecraft.factions.cmd.top.FTopValue;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.landraidcontrol.PowerControl;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.bukkit.entity.Player;

public class CmdTop
extends FCommand {
    private static final Map<String, Function<Faction, FTopValue<?>>> topValueGenerators = new HashMap();

    public CmdTop() {
        this.aliases.add("top");
        this.aliases.add("t");
        this.requiredArgs.add("criteria");
        this.optionalArgs.put("page", "1");
        this.requirements = new CommandRequirements.Builder(Permission.TOP).noDisableOnLock().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        int n;
        String string = commandContext.argAsString(0);
        Function<Faction, FTopValue<?>> function = topValueGenerators.get(string);
        if (function == null || !(FactionsPlugin.getInstance().getLandRaidControl() instanceof PowerControl) && string.equalsIgnoreCase("power")) {
            commandContext.msg(TL.COMMAND_TOP_INVALID, string);
            return;
        }
        List list = Factions.getInstance().getAllFactions().stream().filter(Faction::isNormal).map(faction -> new FTopFacValPair((Faction)faction, (FTopValue)function.apply((Faction)faction))).sorted().toList();
        int n2 = list.size();
        int n3 = 9;
        int n4 = commandContext.argAsInt(1, 1);
        if (n4 > (n = n2 / 9 + 1)) {
            n4 = n;
        } else if (n4 < 1) {
            n4 = 1;
        }
        int n5 = (n4 - 1) * 9;
        int n6 = n5 + 9;
        if (n6 > n2) {
            n6 = n2;
        }
        ArrayList<String> arrayList = new ArrayList<String>(1 + n6 - n5);
        arrayList.add(TL.COMMAND_TOP_TOP.format(string.toUpperCase(), n4, n));
        int n7 = n5 + 1;
        for (FTopFacValPair fTopFacValPair : list.subList(n5, n6)) {
            Faction faction2 = fTopFacValPair.faction;
            String string2 = commandContext.sender instanceof Player ? String.valueOf(faction2.getRelationTo(commandContext.fPlayer).getColor()) + faction2.getTag() : faction2.getTag();
            arrayList.add(TL.COMMAND_TOP_LINE.format(n7, string2, fTopFacValPair.value.getDisplayString()));
            ++n7;
        }
        commandContext.sendMessage(arrayList);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TOP_DESCRIPTION;
    }

    static {
        topValueGenerators.put("members", faction -> new FTopGTIntValue(faction.getFPlayers().size()));
        topValueGenerators.put("start", faction -> new FTopFoundedValue(faction.getFoundedDate()));
        topValueGenerators.put("power", faction -> new FTopGTIntValue(faction.getPowerRounded()));
        topValueGenerators.put("land", faction -> new FTopGTIntValue(faction.getLandRounded()));
        topValueGenerators.put("online", faction -> new FTopGTIntValue(faction.getFPlayersWhereOnline(true).size()));
        Function<Faction, FTopValue> function = faction -> {
            double d = FactionsPlugin.getInstance().conf().economy().isEnabled() ? Econ.getBalance(faction) : 0.0;
            return new FTopBalanceValue(d += faction.getFPlayers().stream().mapToDouble(Econ::getBalance).sum());
        };
        topValueGenerators.put("money", function);
        topValueGenerators.put("balance", function);
        topValueGenerators.put("bal", function);
    }
}

