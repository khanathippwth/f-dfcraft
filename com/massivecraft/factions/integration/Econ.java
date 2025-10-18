/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.economy.Economy
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.RegisteredServiceProvider
 */
package com.massivecraft.factions.integration;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.integration.Essentials;
import com.massivecraft.factions.integration.IntegrationManager;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.factions.util.TL;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Econ {
    private static Economy econ;
    private static final Pattern FACTION_PATTERN;
    private static final DecimalFormat format;

    public static void setup() {
        if (Econ.isSetup()) {
            return;
        }
        String string = "Economy integration is " + (FactionsPlugin.getInstance().conf().economy().isEnabled() ? "enabled, but" : "disabled, and") + " the plugin \"Vault\" ";
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            FactionsPlugin.getInstance().getLogger().info(string + "is not installed.");
            return;
        }
        RegisteredServiceProvider registeredServiceProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (registeredServiceProvider == null) {
            FactionsPlugin.getInstance().getLogger().info(string + "is not hooked into an economy plugin.");
            return;
        }
        econ = (Economy)registeredServiceProvider.getProvider();
        FactionsPlugin.getInstance().getLogger().info("Found economy plugin through Vault: " + econ.getName());
        if (!FactionsPlugin.getInstance().conf().economy().isEnabled()) {
            FactionsPlugin.getInstance().getLogger().info("NOTE: Economy is disabled. You can enable it in config/main.conf");
        }
    }

    public static boolean shouldBeUsed() {
        return FactionsPlugin.getInstance().conf().economy().isEnabled() && econ != null && econ.isEnabled();
    }

    public static boolean isSetup() {
        return econ != null;
    }

    public static Economy getEcon() {
        return econ;
    }

    private static String getWorld(OfflinePlayer offlinePlayer) {
        return offlinePlayer instanceof Player ? ((Player)offlinePlayer).getWorld().getName() : FactionsPlugin.getInstance().conf().economy().getDefaultWorld();
    }

    public static void modifyUniverseMoney(double d) {
        if (!Econ.shouldBeUsed()) {
            return;
        }
        if (FactionsPlugin.getInstance().conf().economy().getUniverseAccount() == null) {
            return;
        }
        if (FactionsPlugin.getInstance().conf().economy().getUniverseAccount().isEmpty()) {
            return;
        }
        if (!Econ.hasAccount(Econ.getOfflinePlayerForName(FactionsPlugin.getInstance().conf().economy().getUniverseAccount()))) {
            return;
        }
        Econ.modifyBalance(Econ.getOfflinePlayerForName(FactionsPlugin.getInstance().conf().economy().getUniverseAccount()), d);
    }

    public static void sendBalanceInfo(FPlayer fPlayer, EconomyParticipator economyParticipator) {
        if (!Econ.shouldBeUsed()) {
            FactionsPlugin.getInstance().log(Level.WARNING, "Vault does not appear to be hooked into an economy plugin.");
            return;
        }
        fPlayer.msg(TL.ECON_BALANCE, economyParticipator.describeTo(fPlayer, true), Econ.moneyString(Econ.getBalance(economyParticipator)));
    }

    public static void sendBalanceInfo(CommandSender commandSender, Faction faction) {
        if (!Econ.shouldBeUsed()) {
            FactionsPlugin.getInstance().log(Level.WARNING, "Vault does not appear to be hooked into an economy plugin.");
            return;
        }
        commandSender.sendMessage(ChatColor.stripColor((String)String.format(TL.ECON_BALANCE.toString(), faction.getTag(), Econ.moneyString(Econ.getBalance(faction)))));
    }

    public static boolean canIControlYou(EconomyParticipator economyParticipator, EconomyParticipator economyParticipator2) {
        Faction faction = RelationUtil.getFaction(economyParticipator);
        Faction faction2 = RelationUtil.getFaction(economyParticipator2);
        if (faction == null) {
            return true;
        }
        if (economyParticipator instanceof FPlayer && ((FPlayer)economyParticipator).isAdminBypassing()) {
            return true;
        }
        if (economyParticipator instanceof FPlayer && Permission.MONEY_WITHDRAW_ANY.has((CommandSender)((FPlayer)economyParticipator).getPlayer())) {
            return true;
        }
        if (economyParticipator == economyParticipator2) {
            return true;
        }
        if (economyParticipator == faction && faction == faction2) {
            return true;
        }
        if (economyParticipator2 instanceof Faction && faction == faction2 && (FactionsPlugin.getInstance().conf().economy().isBankMembersCanWithdraw() || ((FPlayer)economyParticipator).getRole().value >= Role.MODERATOR.value)) {
            return true;
        }
        economyParticipator.msg(TL.ECON_NOPERM, economyParticipator.describeTo(economyParticipator, true), economyParticipator2.describeTo(economyParticipator));
        return false;
    }

    public static boolean transferMoney(EconomyParticipator economyParticipator, EconomyParticipator economyParticipator2, EconomyParticipator economyParticipator3, double d) {
        return Econ.transferMoney(economyParticipator, economyParticipator2, economyParticipator3, d, true);
    }

    public static boolean transferMoney(EconomyParticipator economyParticipator, EconomyParticipator economyParticipator2, EconomyParticipator economyParticipator3, double d, boolean bl) {
        EconomyParticipator economyParticipator4;
        if (!Econ.shouldBeUsed()) {
            economyParticipator.msg(TL.ECON_DISABLED, new Object[0]);
            return false;
        }
        if (d < 0.0) {
            d *= -1.0;
            economyParticipator4 = economyParticipator2;
            economyParticipator2 = economyParticipator3;
            economyParticipator3 = economyParticipator4;
        }
        if (!Econ.canIControlYou(economyParticipator, economyParticipator2)) {
            return false;
        }
        economyParticipator4 = Econ.checkStatus(economyParticipator2.getOfflinePlayer());
        OfflinePlayer offlinePlayer = Econ.checkStatus(economyParticipator3.getOfflinePlayer());
        if (!Econ.has((OfflinePlayer)economyParticipator4, d)) {
            if (economyParticipator != null && bl) {
                economyParticipator.msg(TL.ECON_CANTAFFORD_TRANSFER, economyParticipator2.describeTo(economyParticipator, true), Econ.moneyString(d), economyParticipator3.describeTo(economyParticipator));
            }
            return false;
        }
        if (FactionsPlugin.getInstance().getIntegrationManager().isEnabled(IntegrationManager.Integration.ESS) && Essentials.isOverBalCap(Econ.getBalance(offlinePlayer) + d)) {
            economyParticipator.msg(TL.ECON_OVER_BAL_CAP, d);
            return false;
        }
        if (Econ.withdraw((OfflinePlayer)economyParticipator4, d)) {
            if (Econ.deposit(offlinePlayer, d)) {
                if (bl) {
                    Econ.sendTransferInfo(economyParticipator, economyParticipator2, economyParticipator3, d);
                }
                return true;
            }
            Econ.deposit((OfflinePlayer)economyParticipator4, d);
        }
        if (bl) {
            economyParticipator.msg(TL.ECON_TRANSFER_UNABLE, Econ.moneyString(d), economyParticipator3.describeTo(economyParticipator), economyParticipator2.describeTo(economyParticipator, true));
        }
        return false;
    }

    public static Set<FPlayer> getFplayers(EconomyParticipator economyParticipator) {
        HashSet<FPlayer> hashSet = new HashSet<FPlayer>();
        if (economyParticipator != null) {
            if (economyParticipator instanceof FPlayer) {
                hashSet.add((FPlayer)economyParticipator);
            } else if (economyParticipator instanceof Faction) {
                hashSet.addAll(((Faction)economyParticipator).getFPlayers());
            }
        }
        return hashSet;
    }

    public static void sendTransferInfo(EconomyParticipator economyParticipator, EconomyParticipator economyParticipator2, EconomyParticipator economyParticipator3, double d) {
        HashSet<FPlayer> hashSet = new HashSet<FPlayer>();
        hashSet.addAll(Econ.getFplayers(economyParticipator));
        hashSet.addAll(Econ.getFplayers(economyParticipator2));
        hashSet.addAll(Econ.getFplayers(economyParticipator3));
        if (economyParticipator == null) {
            for (FPlayer fPlayer : hashSet) {
                fPlayer.msg(TL.ECON_TRANSFER_NOINVOKER, Econ.moneyString(d), economyParticipator2.describeTo(fPlayer), economyParticipator3.describeTo(fPlayer));
            }
        } else if (economyParticipator == economyParticipator2) {
            for (FPlayer fPlayer : hashSet) {
                fPlayer.msg(TL.ECON_TRANSFER_GAVE, economyParticipator2.describeTo(fPlayer, true), Econ.moneyString(d), economyParticipator3.describeTo(fPlayer));
            }
        } else if (economyParticipator == economyParticipator3) {
            for (FPlayer fPlayer : hashSet) {
                fPlayer.msg(TL.ECON_TRANSFER_TOOK, economyParticipator3.describeTo(fPlayer, true), Econ.moneyString(d), economyParticipator2.describeTo(fPlayer));
            }
        } else {
            for (FPlayer fPlayer : hashSet) {
                fPlayer.msg(TL.ECON_TRANSFER_TRANSFER, economyParticipator.describeTo(fPlayer, true), Econ.moneyString(d), economyParticipator2.describeTo(fPlayer), economyParticipator3.describeTo(fPlayer));
            }
        }
    }

    public static boolean hasAtLeast(EconomyParticipator economyParticipator, double d, String string) {
        if (!Econ.shouldBeUsed()) {
            return true;
        }
        boolean bl = false;
        double d2 = Econ.getBalance(economyParticipator);
        if (d2 >= d) {
            bl = true;
        }
        if (!bl) {
            if (string != null && !string.isEmpty()) {
                economyParticipator.msg(TL.ECON_CANTAFFORD_AMOUNT, economyParticipator.describeTo(economyParticipator, true), Econ.moneyString(d), string);
            }
            return false;
        }
        return true;
    }

    public static boolean modifyMoney(EconomyParticipator economyParticipator, double d, String string, String string2) {
        if (!Econ.shouldBeUsed()) {
            return false;
        }
        if (d == 0.0) {
            return true;
        }
        OfflinePlayer offlinePlayer = Econ.checkStatus(economyParticipator.getOfflinePlayer());
        String string3 = economyParticipator.describeTo(economyParticipator, true);
        if (d > 0.0) {
            if (Econ.deposit(offlinePlayer, d)) {
                Econ.modifyUniverseMoney(-d);
                if (string2 != null && !string2.isEmpty()) {
                    economyParticipator.msg(TL.ECON_GAIN_SUCCESS, string3, Econ.moneyString(d), string2);
                }
                return true;
            }
            if (string2 != null && !string2.isEmpty()) {
                economyParticipator.msg(TL.ECON_GAIN_FAILURE, string3, Econ.moneyString(d), string2);
            }
            return false;
        }
        if (Econ.has(offlinePlayer, -d) && Econ.withdraw(offlinePlayer, -d)) {
            Econ.modifyUniverseMoney(-d);
            if (string2 != null && !string2.isEmpty()) {
                economyParticipator.msg(TL.ECON_LOST_SUCCESS, string3, Econ.moneyString(-d), string2);
            }
            return true;
        }
        if (string != null && !string.isEmpty()) {
            economyParticipator.msg(TL.ECON_LOST_FAILURE, string3, Econ.moneyString(-d), string);
        }
        return false;
    }

    public static String moneyString(double d) {
        return format.format(d);
    }

    public static double calculateClaimCost(int n, boolean bl) {
        if (!Econ.shouldBeUsed()) {
            return 0.0;
        }
        return FactionsPlugin.getInstance().conf().economy().getCostClaimWilderness() + FactionsPlugin.getInstance().conf().economy().getCostClaimWilderness() * FactionsPlugin.getInstance().conf().economy().getClaimAdditionalMultiplier() * (double)n - (bl ? FactionsPlugin.getInstance().conf().economy().getCostClaimFromFactionBonus() : 0.0);
    }

    public static double calculateClaimRefund(int n) {
        return Econ.calculateClaimCost(n - 1, false) * FactionsPlugin.getInstance().conf().economy().getClaimRefundMultiplier();
    }

    public static double calculateTotalLandValue(int n) {
        double d = 0.0;
        for (int i = 0; i < n; ++i) {
            d += Econ.calculateClaimCost(i, false);
        }
        return d;
    }

    public static double calculateTotalLandRefund(int n) {
        return Econ.calculateTotalLandValue(n) * FactionsPlugin.getInstance().conf().economy().getClaimRefundMultiplier();
    }

    @Deprecated
    private static OfflinePlayer getOfflinePlayerForName(String string) {
        try {
            Matcher matcher = FACTION_PATTERN.matcher(string);
            if (matcher.find()) {
                return Factions.getInstance().getFactionById(Integer.parseInt(matcher.group(1))).getOfflinePlayer();
            }
            return Bukkit.getOfflinePlayer((UUID)UUID.fromString(string));
        } catch (Exception exception) {
            return Bukkit.getOfflinePlayer((String)string);
        }
    }

    @Deprecated
    public static boolean hasAccount(String string) {
        return Econ.hasAccount(Econ.getOfflinePlayerForName(string));
    }

    public static boolean hasAccount(EconomyParticipator economyParticipator) {
        return Econ.hasAccount(economyParticipator.getOfflinePlayer());
    }

    private static boolean hasAccount(OfflinePlayer offlinePlayer) {
        return econ.hasAccount(offlinePlayer, Econ.getWorld(offlinePlayer));
    }

    @Deprecated
    public static double getBalance(String string) {
        return Econ.getBalance(Econ.getOfflinePlayerForName(string));
    }

    public static double getBalance(EconomyParticipator economyParticipator) {
        return Econ.getBalance(economyParticipator.getOfflinePlayer());
    }

    private static double getBalance(OfflinePlayer offlinePlayer) {
        return econ.getBalance(Econ.checkStatus(offlinePlayer), Econ.getWorld(offlinePlayer));
    }

    public static boolean has(EconomyParticipator economyParticipator, double d) {
        return Econ.has(economyParticipator.getOfflinePlayer(), d);
    }

    private static boolean has(OfflinePlayer offlinePlayer, double d) {
        return econ.has(Econ.checkStatus(offlinePlayer), Econ.getWorld(offlinePlayer), d);
    }

    @Deprecated
    public static String getFriendlyBalance(UUID uUID) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer((UUID)uUID);
        if (offlinePlayer.getName() == null) {
            return "0";
        }
        return format.format(Econ.getBalance(offlinePlayer));
    }

    public static String getFriendlyBalance(FPlayer fPlayer) {
        Player player = fPlayer.getPlayer();
        if (player == null) {
            return "0";
        }
        return format.format(Econ.getBalance((OfflinePlayer)player));
    }

    @Deprecated
    public static boolean setBalance(String string, double d) {
        return Econ.setBalance(Econ.getOfflinePlayerForName(string), d);
    }

    public static boolean setBalance(EconomyParticipator economyParticipator, double d) {
        return Econ.setBalance(economyParticipator.getOfflinePlayer(), d);
    }

    private static boolean setBalance(OfflinePlayer offlinePlayer, double d) {
        double d2 = Econ.getBalance(offlinePlayer);
        if (d2 > d) {
            return econ.withdrawPlayer(offlinePlayer, Econ.getWorld(offlinePlayer), d2 - d).transactionSuccess();
        }
        return econ.depositPlayer(offlinePlayer, Econ.getWorld(offlinePlayer), d - d2).transactionSuccess();
    }

    @Deprecated
    public static boolean modifyBalance(String string, double d) {
        return Econ.modifyBalance(Econ.getOfflinePlayerForName(string), d);
    }

    public static boolean modifyBalance(EconomyParticipator economyParticipator, double d) {
        return Econ.modifyBalance(economyParticipator.getOfflinePlayer(), d);
    }

    private static boolean modifyBalance(OfflinePlayer offlinePlayer, double d) {
        if (d < 0.0) {
            return econ.withdrawPlayer(Econ.checkStatus(offlinePlayer), Econ.getWorld(offlinePlayer), -d).transactionSuccess();
        }
        return econ.depositPlayer(Econ.checkStatus(offlinePlayer), Econ.getWorld(offlinePlayer), d).transactionSuccess();
    }

    @Deprecated
    public static boolean deposit(String string, double d) {
        return Econ.deposit(Econ.getOfflinePlayerForName(string), d);
    }

    public static boolean deposit(EconomyParticipator economyParticipator, double d) {
        return Econ.deposit(economyParticipator.getOfflinePlayer(), d);
    }

    private static boolean deposit(OfflinePlayer offlinePlayer, double d) {
        return econ.depositPlayer(Econ.checkStatus(offlinePlayer), Econ.getWorld(offlinePlayer), d).transactionSuccess();
    }

    @Deprecated
    public static boolean withdraw(String string, double d) {
        return Econ.withdraw(Econ.getOfflinePlayerForName(string), d);
    }

    public static boolean withdraw(EconomyParticipator economyParticipator, double d) {
        return Econ.withdraw(economyParticipator.getOfflinePlayer(), d);
    }

    private static boolean withdraw(OfflinePlayer offlinePlayer, double d) {
        return econ.withdrawPlayer(Econ.checkStatus(offlinePlayer), Econ.getWorld(offlinePlayer), d).transactionSuccess();
    }

    @Deprecated
    public static void createAccount(String string) {
        Econ.createAccount(Econ.getOfflinePlayerForName(string));
    }

    public static void createAccount(EconomyParticipator economyParticipator) {
        Econ.createAccount(economyParticipator.getOfflinePlayer());
    }

    private static void createAccount(OfflinePlayer offlinePlayer) {
        if (!econ.createPlayerAccount(offlinePlayer, Econ.getWorld(offlinePlayer))) {
            FactionsPlugin.getInstance().getLogger().warning("FAILED TO CREATE ECONOMY ACCOUNT " + offlinePlayer.getName() + "/" + String.valueOf(offlinePlayer.getUniqueId()));
        }
    }

    public static OfflinePlayer checkStatus(OfflinePlayer offlinePlayer) {
        if (offlinePlayer.getName() == null || !offlinePlayer.getName().startsWith("faction-")) {
            return offlinePlayer;
        }
        String string = Econ.getWorld(offlinePlayer);
        if (!Econ.hasAccount(offlinePlayer)) {
            Econ.createAccount(offlinePlayer);
            Econ.setBalance(offlinePlayer, 0.0);
        }
        return offlinePlayer;
    }

    @Deprecated
    public static boolean isUUID(String string) {
        try {
            UUID.fromString(string);
            return true;
        } catch (IllegalArgumentException illegalArgumentException) {
            return false;
        }
    }

    static {
        DecimalFormat decimalFormat;
        econ = null;
        FACTION_PATTERN = Pattern.compile("^faction-(\\d+)$");
        try {
            decimalFormat = new DecimalFormat(TL.ECON_FORMAT.toString());
        } catch (Exception exception) {
            FactionsPlugin.getInstance().getLogger().warning("Fell over on invalid default econ format '" + String.valueOf((Object)TL.ECON_FORMAT) + "'");
            decimalFormat = new DecimalFormat("###,###.###");
        }
        format = decimalFormat;
    }
}

