/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.tag.FactionTag;
import com.massivecraft.factions.tag.FancyTag;
import com.massivecraft.factions.tag.Tag;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.TL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import moss.factions.shade.net.kyori.adventure.audience.Audience;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CmdShow
extends FCommand {
    final List<String> defaults = new ArrayList<String>();

    public CmdShow() {
        this.aliases.add("show");
        this.aliases.add("who");
        this.defaults.add("{header}");
        this.defaults.add("<a>Description: <i>{description}");
        this.defaults.add("<a>Joining: <i>{joining}    {peaceful}");
        this.defaults.add("<a>Land / Power / Maxpower: <i> {chunks} / {power} / {maxPower}");
        this.defaults.add("<a>Raidable: {raidable}");
        this.defaults.add("<a>Founded: <i>{create-date}");
        this.defaults.add("<a>This faction is permanent, remaining even with no members.");
        this.defaults.add("<a>Land value: <i>{land-value} {land-refund}");
        this.defaults.add("<a>Balance: <i>{faction-balance}");
        this.defaults.add("<a>Bans: <i>{faction-bancount}");
        this.defaults.add("<a>Allies(<i>{allies}<a>/<i>{max-allies}<a>): {allies-list}");
        this.defaults.add("<a>Online: (<i>{online}<a>/<i>{members}<a>): {online-list}");
        this.defaults.add("<a>Offline: (<i>{offline}<a>/<i>{members}<a>): {offline-list}");
        this.optionalArgs.put("faction tag", "yours");
        this.requirements = new CommandRequirements.Builder(Permission.SHOW).noDisableOnLock().build();
    }

    @Override
    public void perform(CommandContext commandContext) {
        Faction faction = commandContext.faction;
        if (commandContext.argIsSet(0)) {
            faction = commandContext.argAsFaction(0);
        }
        if (faction == null) {
            return;
        }
        if (commandContext.fPlayer != null && !commandContext.player.hasPermission(Permission.SHOW_BYPASS_EXEMPT.toString()) && FactionsPlugin.getInstance().conf().commands().show().getExempt().contains(faction.getTag())) {
            commandContext.msg(TL.COMMAND_SHOW_EXEMPT, new Object[0]);
            return;
        }
        if (!commandContext.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostShow(), TL.COMMAND_SHOW_TOSHOW, TL.COMMAND_SHOW_FORSHOW)) {
            return;
        }
        List<String> list = FactionsPlugin.getInstance().conf().commands().show().getFormat();
        if (list == null || list.isEmpty()) {
            list = this.defaults;
        }
        if (!faction.isNormal()) {
            String string = faction.getTag(commandContext.fPlayer);
            String string2 = (String)list.getFirst();
            if (FactionTag.HEADER.foundInString(string2)) {
                commandContext.msg(this.plugin.txt().titleize(string), new Object[0]);
            } else {
                String string3 = string2.replace(FactionTag.FACTION.getTag(), string);
                string3 = Tag.parsePlain(faction, commandContext.fPlayer, string3);
                commandContext.msg(this.plugin.txt().parse(string3), new Object[0]);
            }
            return;
        }
        ArrayList<String> arrayList = new ArrayList<String>();
        for (String string : list) {
            Object object = Tag.parsePlain(faction, commandContext.fPlayer, string);
            if (object == null) continue;
            if (commandContext.fPlayer != null) {
                object = Tag.parsePlaceholders(commandContext.fPlayer.getPlayer(), (String)object);
            }
            if (((String)object).contains("{notFrozen}") || ((String)object).contains("{notPermanent}")) continue;
            if (((String)object).contains("{ig}")) {
                object = ((String)object).substring(0, ((String)object).indexOf("{ig}")) + String.valueOf((Object)TL.COMMAND_SHOW_NOHOME);
            }
            object = ((String)object).replace("%", "");
            arrayList.add((String)object);
        }
        if (commandContext.fPlayer != null && this.groupPresent()) {
            new GroupGetter(arrayList, commandContext.fPlayer, faction).runTaskAsynchronously((Plugin)FactionsPlugin.getInstance());
        } else {
            this.sendMessages(arrayList, commandContext.sender, faction, commandContext.fPlayer);
        }
    }

    private void sendMessages(List<String> list, CommandSender commandSender, Faction faction, FPlayer fPlayer) {
        this.sendMessages(list, commandSender, faction, fPlayer, null);
    }

    private void sendMessages(List<String> list, CommandSender commandSender, Faction faction, FPlayer fPlayer, Map<UUID, String> map) {
        Audience audience = this.plugin.getAdventure().sender(commandSender);
        for (String string : list) {
            FancyTag fancyTag = FancyTag.getMatch(string);
            if (fancyTag != null) {
                List<Component> list2;
                if (fPlayer != null) {
                    list2 = FancyTag.parse(string, faction, fPlayer, map);
                    if (list2 == null) continue;
                    for (Component component : list2) {
                        audience.sendMessage(component);
                    }
                    continue;
                }
                list2 = new StringBuilder();
                ((StringBuilder)((Object)list2)).append(string.replace(fancyTag.getTag(), ""));
                switch (fancyTag) {
                    case ONLINE_LIST: {
                        this.onOffLineMessage((StringBuilder)((Object)list2), commandSender, faction, true);
                        break;
                    }
                    case OFFLINE_LIST: {
                        this.onOffLineMessage((StringBuilder)((Object)list2), commandSender, faction, false);
                        break;
                    }
                    case ALLIES_LIST: {
                        this.relationMessage((StringBuilder)((Object)list2), commandSender, faction, Relation.ALLY);
                        break;
                    }
                    case ENEMIES_LIST: {
                        this.relationMessage((StringBuilder)((Object)list2), commandSender, faction, Relation.ENEMY);
                        break;
                    }
                    case TRUCES_LIST: {
                        this.relationMessage((StringBuilder)((Object)list2), commandSender, faction, Relation.TRUCE);
                        break;
                    }
                }
                continue;
            }
            audience.sendMessage(LegacyComponentSerializer.legacySection().deserialize(FactionsPlugin.getInstance().txt().parse(string)));
        }
    }

    private void onOffLineMessage(StringBuilder stringBuilder, CommandSender commandSender, Faction faction, boolean bl) {
        boolean bl2 = true;
        for (FPlayer fPlayer : MiscUtil.rankOrder(faction.getFPlayersWhereOnline(bl))) {
            String string = fPlayer.getNameAndTitle();
            stringBuilder.append((String)(bl2 ? string : ", " + string));
            bl2 = false;
        }
        commandSender.sendMessage(FactionsPlugin.getInstance().txt().parse(stringBuilder.toString()));
    }

    private void relationMessage(StringBuilder stringBuilder, CommandSender commandSender, Faction faction, Relation relation) {
        boolean bl = true;
        for (Faction faction2 : Factions.getInstance().getAllFactions()) {
            if (faction2 == faction || faction2.getRelationTo(faction) != relation) continue;
            String string = faction2.getTag();
            stringBuilder.append((String)(bl ? string : ", " + string));
            bl = false;
        }
        commandSender.sendMessage(FactionsPlugin.getInstance().txt().parse(stringBuilder.toString()));
    }

    private boolean groupPresent() {
        for (String string : FactionsPlugin.getInstance().conf().commands().toolTips().getPlayer()) {
            if (!string.contains("{group}")) continue;
            return true;
        }
        return false;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SHOW_COMMANDDESCRIPTION;
    }

    private class GroupGetter
    extends BukkitRunnable {
        private final List<String> messageList;
        private final FPlayer sender;
        private final Faction faction;
        private final Set<OfflinePlayer> players;

        private GroupGetter(List<String> list, FPlayer fPlayer2, Faction faction) {
            this.messageList = list;
            this.sender = fPlayer2;
            this.faction = faction;
            this.players = faction.getFPlayers().stream().map(fPlayer -> Bukkit.getOfflinePlayer((UUID)UUID.fromString(fPlayer.getId()))).collect(Collectors.toSet());
        }

        public void run() {
            HashMap<UUID, String> hashMap = new HashMap<UUID, String>();
            for (OfflinePlayer offlinePlayer : this.players) {
                hashMap.put(offlinePlayer.getUniqueId(), FactionsPlugin.getInstance().getPrimaryGroup(offlinePlayer));
            }
            new Sender(this.messageList, this.sender, this.faction, hashMap).runTask((Plugin)FactionsPlugin.getInstance());
        }
    }

    private class Sender
    extends BukkitRunnable {
        private final List<String> messageList;
        private final FPlayer sender;
        private final Faction faction;
        private final Map<UUID, String> map;

        private Sender(List<String> list, FPlayer fPlayer, Faction faction, Map<UUID, String> map) {
            this.messageList = list;
            this.sender = fPlayer;
            this.faction = faction;
            this.map = map;
        }

        public void run() {
            Player player = Bukkit.getPlayerExact((String)this.sender.getName());
            if (player != null) {
                CmdShow.this.sendMessages(this.messageList, (CommandSender)player, this.faction, this.sender, this.map);
            }
        }
    }
}

