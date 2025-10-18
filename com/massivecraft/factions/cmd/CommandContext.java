/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.WarmUpUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandContext {
    public final CommandSender sender;
    public final Player player;
    public final FPlayer fPlayer;
    public final Faction faction;
    public final List<String> args;
    public final String alias;
    public final List<FCommand> commandChain = new ArrayList<FCommand>();

    public CommandContext(CommandSender commandSender, List<String> list, String string) {
        this.sender = commandSender;
        this.args = list;
        this.alias = string;
        if (commandSender instanceof Player) {
            this.player = (Player)commandSender;
            this.fPlayer = FPlayers.getInstance().getByPlayer(this.player);
            this.faction = this.fPlayer.getFaction();
        } else {
            this.player = null;
            this.fPlayer = null;
            this.faction = null;
        }
    }

    public void msg(String string, Object ... objectArray) {
        this.sender.sendMessage(FactionsPlugin.getInstance().txt().parse(string, objectArray));
    }

    public void msg(TL tL, Object ... objectArray) {
        this.sender.sendMessage(FactionsPlugin.getInstance().txt().parse(tL.toString(), objectArray));
    }

    public void sendMessage(String string) {
        this.sender.sendMessage(string);
    }

    public void sendMessage(List<String> list) {
        for (String string : list) {
            this.sendMessage(string);
        }
    }

    public boolean argIsSet(int n) {
        return this.args.size() >= n + 1;
    }

    public String argAsString(int n, String string) {
        if (this.args.size() < n + 1) {
            return string;
        }
        return this.args.get(n);
    }

    public String argAsString(int n) {
        return this.argAsString(n, null);
    }

    public Integer strAsInt(String string, Integer n) {
        if (string == null) {
            return n;
        }
        try {
            return Integer.parseInt(string);
        } catch (Exception exception) {
            return n;
        }
    }

    public Integer argAsInt(int n, Integer n2) {
        return this.strAsInt(this.argAsString(n), n2);
    }

    public Integer argAsInt(int n) {
        return this.argAsInt(n, null);
    }

    public Double strAsDouble(String string, Double d) {
        if (string == null) {
            return d;
        }
        try {
            return Double.parseDouble(string);
        } catch (Exception exception) {
            return d;
        }
    }

    public Double argAsDouble(int n, Double d) {
        return this.strAsDouble(this.argAsString(n), d);
    }

    public Double argAsDouble(int n) {
        return this.argAsDouble(n, null);
    }

    public Boolean strAsBool(String string) {
        return (string = string.toLowerCase()).startsWith("y") || string.startsWith("t") || string.startsWith("on") || string.startsWith("+") || string.startsWith("1");
    }

    public Boolean argAsBool(int n, boolean bl) {
        String string = this.argAsString(n);
        if (string == null) {
            return bl;
        }
        return this.strAsBool(string);
    }

    public Boolean argAsBool(int n) {
        return this.argAsBool(n, false);
    }

    public Player strAsPlayer(String string, Player player, boolean bl) {
        Player player2;
        Player player3 = player;
        if (string != null && (player2 = Bukkit.getServer().getPlayer(string)) != null) {
            player3 = player2;
        }
        if (bl && player3 == null) {
            this.sender.sendMessage(TL.GENERIC_NOPLAYERFOUND.format(string));
        }
        return player3;
    }

    public Player argAsPlayer(int n, Player player, boolean bl) {
        return this.strAsPlayer(this.argAsString(n), player, bl);
    }

    public Player argAsPlayer(int n, Player player) {
        return this.argAsPlayer(n, player, true);
    }

    public Player argAsPlayer(int n) {
        return this.argAsPlayer(n, null);
    }

    public Player strAsBestPlayerMatch(String string, Player player, boolean bl) {
        List list;
        Player player2 = player;
        if (string != null && !(list = Bukkit.getServer().matchPlayer(string)).isEmpty()) {
            player2 = (Player)list.getFirst();
        }
        if (bl && player2 == null) {
            this.sender.sendMessage(TL.GENERIC_NOPLAYERMATCH.format(string));
        }
        return player2;
    }

    public Player argAsBestPlayerMatch(int n, Player player, boolean bl) {
        return this.strAsBestPlayerMatch(this.argAsString(n), player, bl);
    }

    public Player argAsBestPlayerMatch(int n, Player player) {
        return this.argAsBestPlayerMatch(n, player, true);
    }

    public Player argAsBestPlayerMatch(int n) {
        return this.argAsPlayer(n, null);
    }

    public FPlayer strAsFPlayer(String string, FPlayer fPlayer, boolean bl) {
        FPlayer fPlayer2 = fPlayer;
        if (string != null) {
            for (FPlayer fPlayer3 : FPlayers.getInstance().getAllFPlayers()) {
                if (!fPlayer3.getName().equalsIgnoreCase(string)) continue;
                fPlayer2 = fPlayer3;
                break;
            }
        }
        if (bl && fPlayer2 == null) {
            this.sender.sendMessage(TL.GENERIC_NOPLAYERFOUND.format(string));
        }
        return fPlayer2;
    }

    public FPlayer argAsFPlayer(int n, FPlayer fPlayer, boolean bl) {
        return this.strAsFPlayer(this.argAsString(n), fPlayer, bl);
    }

    public FPlayer argAsFPlayer(int n, FPlayer fPlayer) {
        return this.argAsFPlayer(n, fPlayer, true);
    }

    public FPlayer argAsFPlayer(int n) {
        return this.argAsFPlayer(n, null);
    }

    public FPlayer strAsBestFPlayerMatch(String string, FPlayer fPlayer, boolean bl) {
        return this.strAsFPlayer(string, fPlayer, bl);
    }

    public FPlayer argAsBestFPlayerMatch(int n, FPlayer fPlayer, boolean bl) {
        return this.strAsBestFPlayerMatch(this.argAsString(n), fPlayer, bl);
    }

    public FPlayer argAsBestFPlayerMatch(int n, FPlayer fPlayer) {
        return this.argAsBestFPlayerMatch(n, fPlayer, true);
    }

    public FPlayer argAsBestFPlayerMatch(int n) {
        return this.argAsBestFPlayerMatch(n, null);
    }

    public Faction strAsFaction(String string, Faction faction, boolean bl) {
        Faction faction2 = faction;
        if (string != null) {
            FPlayer fPlayer;
            Faction faction3 = Factions.getInstance().getByTag(string);
            if (faction3 == null) {
                if (string.equalsIgnoreCase("warzone")) {
                    faction3 = Factions.getInstance().getWarZone();
                } else if (string.equalsIgnoreCase("safezone")) {
                    faction3 = Factions.getInstance().getSafeZone();
                }
            }
            if (faction3 == null) {
                faction3 = Factions.getInstance().getBestTagMatch(string);
            }
            if (faction3 == null && (fPlayer = this.strAsFPlayer(string, null, false)) != null) {
                faction3 = fPlayer.getFaction();
            }
            if (faction3 != null) {
                faction2 = faction3;
            }
        }
        if (bl && faction2 == null) {
            this.sender.sendMessage(TL.GENERIC_NOFACTIONMATCH.format(string));
        }
        return faction2;
    }

    public Faction argAsFaction(int n, Faction faction, boolean bl) {
        return this.strAsFaction(this.argAsString(n), faction, bl);
    }

    public Faction argAsFaction(int n, Faction faction) {
        return this.argAsFaction(n, faction, true);
    }

    public Faction argAsFaction(int n) {
        return this.argAsFaction(n, null);
    }

    public boolean assertHasFaction() {
        if (this.player == null) {
            return true;
        }
        if (!this.fPlayer.hasFaction()) {
            this.sendMessage("You are not member of any faction.");
            return false;
        }
        return true;
    }

    public boolean assertMinRole(Role role) {
        if (this.player == null) {
            return true;
        }
        if (this.fPlayer.getRole().value < role.value) {
            this.msg("<b>You <h>must be " + String.valueOf(role), new Object[0]);
            return false;
        }
        return true;
    }

    public boolean canIAdministerYou(FPlayer fPlayer, FPlayer fPlayer2) {
        if (!fPlayer.getFaction().equals(fPlayer2.getFaction())) {
            fPlayer.sendMessage(FactionsPlugin.getInstance().txt().parse("%s <b>is not in the same faction as you.", fPlayer2.describeTo(fPlayer, true)));
            return false;
        }
        if (fPlayer.getRole().value >= fPlayer2.getRole().value || fPlayer.getRole() == Role.ADMIN) {
            return true;
        }
        fPlayer.sendMessage(FactionsPlugin.getInstance().txt().parse("%s <b>has a higher rank than you.", fPlayer2.describeTo(fPlayer, true)));
        return false;
    }

    public boolean payForCommand(double d, String string, String string2) {
        if (!Econ.shouldBeUsed() || this.fPlayer == null || d == 0.0 || this.fPlayer.isAdminBypassing()) {
            return true;
        }
        if (FactionsPlugin.getInstance().conf().economy().isBankEnabled() && FactionsPlugin.getInstance().conf().economy().isBankFactionPaysCosts() && this.fPlayer.hasFaction() && this.fPlayer.getFaction().hasAccess(this.fPlayer, PermissibleActions.ECONOMY, this.fPlayer.getLastStoodAt())) {
            return Econ.modifyMoney(this.faction, -d, string, string2);
        }
        return Econ.modifyMoney(this.fPlayer, -d, string, string2);
    }

    public boolean payForCommand(double d, TL tL, TL tL2) {
        return this.payForCommand(d, tL.toString(), tL2.toString());
    }

    public boolean canAffordCommand(double d, String string) {
        if (!Econ.shouldBeUsed() || this.fPlayer == null || d == 0.0 || this.fPlayer.isAdminBypassing()) {
            return true;
        }
        if (FactionsPlugin.getInstance().conf().economy().isBankEnabled() && FactionsPlugin.getInstance().conf().economy().isBankFactionPaysCosts() && this.fPlayer.hasFaction()) {
            return Econ.hasAtLeast(this.faction, d, string);
        }
        return Econ.hasAtLeast(this.fPlayer, d, string);
    }

    public void doWarmUp(WarmUpUtil.Warmup warmup, TL tL, String string, Runnable runnable, long l) {
        this.doWarmUp(this.fPlayer, warmup, tL, string, runnable, l);
    }

    public void doWarmUp(FPlayer fPlayer, WarmUpUtil.Warmup warmup, TL tL, String string, Runnable runnable, long l) {
        WarmUpUtil.process(fPlayer, warmup, tL, string, runnable, l);
    }
}

