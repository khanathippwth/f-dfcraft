/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 */
package com.massivecraft.factions.listeners;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.integration.Essentials;
import com.massivecraft.factions.integration.IntegrationManager;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.ChatMode;
import java.util.UnknownFormatConversionException;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class FactionsChatListener
implements Listener {
    public final FactionsPlugin plugin;

    public FactionsChatListener(FactionsPlugin factionsPlugin) {
        this.plugin = factionsPlugin;
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onPlayerEarlyChat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        if (!this.plugin.worldUtil().isEnabled(asyncPlayerChatEvent.getPlayer().getWorld())) {
            return;
        }
        Player player = asyncPlayerChatEvent.getPlayer();
        String string = asyncPlayerChatEvent.getMessage();
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        ChatMode chatMode = fPlayer.getChatMode();
        MainConfig.Factions.Chat chat = FactionsPlugin.getInstance().conf().factions().chat();
        if (chatMode == ChatMode.MOD) {
            Faction faction = fPlayer.getFaction();
            String string2 = String.format(chat.getModChatFormat(), ChatColor.stripColor((String)fPlayer.getNameAndTag()), string);
            for (FPlayer fPlayer2 : FPlayers.getInstance().getOnlinePlayers()) {
                if (faction == fPlayer2.getFaction() && fPlayer2.getRole().isAtLeast(Role.MODERATOR)) {
                    fPlayer2.sendMessage(string2);
                    continue;
                }
                if (!fPlayer2.isSpyingChat() || fPlayer == fPlayer2) continue;
                fPlayer2.sendMessage("[MCspy]: " + string2);
            }
            FactionsPlugin.getInstance().log(Level.INFO, ChatColor.stripColor((String)("ModChat " + faction.getTag() + ": " + string2)));
            asyncPlayerChatEvent.setCancelled(true);
        } else if (chatMode == ChatMode.FACTION) {
            Faction faction = fPlayer.getFaction();
            String string3 = String.format(chat.getFactionChatFormat(), fPlayer.describeTo(faction), string);
            faction.sendMessage(string3);
            FactionsPlugin.getInstance().log(Level.INFO, ChatColor.stripColor((String)("FactionChat " + faction.getTag() + ": " + string3)));
            for (FPlayer fPlayer3 : FPlayers.getInstance().getOnlinePlayers()) {
                if (!fPlayer3.isSpyingChat() || fPlayer3.getFaction() == faction || fPlayer == fPlayer3) continue;
                fPlayer3.sendMessage("[FCspy] " + faction.getTag() + ": " + string3);
            }
            asyncPlayerChatEvent.setCancelled(true);
        } else if (chatMode == ChatMode.ALLIANCE) {
            Faction faction = fPlayer.getFaction();
            String string4 = String.format(chat.getAllianceChatFormat(), ChatColor.stripColor((String)fPlayer.getNameAndTag()), string);
            faction.sendMessage(string4);
            for (FPlayer fPlayer4 : FPlayers.getInstance().getOnlinePlayers()) {
                if (faction.getRelationTo(fPlayer4) == Relation.ALLY && !fPlayer4.isIgnoreAllianceChat()) {
                    fPlayer4.sendMessage(string4);
                    continue;
                }
                if (!fPlayer4.isSpyingChat() || fPlayer == fPlayer4) continue;
                fPlayer4.sendMessage("[ACspy]: " + string4);
            }
            FactionsPlugin.getInstance().log(Level.INFO, ChatColor.stripColor((String)("AllianceChat: " + string4)));
            asyncPlayerChatEvent.setCancelled(true);
        } else if (chatMode == ChatMode.TRUCE) {
            Faction faction = fPlayer.getFaction();
            String string5 = String.format(chat.getTruceChatFormat(), ChatColor.stripColor((String)fPlayer.getNameAndTag()), string);
            faction.sendMessage(string5);
            for (FPlayer fPlayer5 : FPlayers.getInstance().getOnlinePlayers()) {
                if (faction.getRelationTo(fPlayer5) == Relation.TRUCE) {
                    fPlayer5.sendMessage(string5);
                    continue;
                }
                if (!fPlayer5.isSpyingChat() || fPlayer5 == fPlayer) continue;
                fPlayer5.sendMessage("[TCspy]: " + string5);
            }
            FactionsPlugin.getInstance().log(Level.INFO, ChatColor.stripColor((String)("TruceChat: " + string5)));
            asyncPlayerChatEvent.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onPlayerChat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        if (!this.plugin.worldUtil().isEnabled(asyncPlayerChatEvent.getPlayer().getWorld())) {
            return;
        }
        if (FactionsPlugin.getInstance().isAnotherPluginHandlingChat()) {
            return;
        }
        Player player = asyncPlayerChatEvent.getPlayer();
        String string = asyncPlayerChatEvent.getMessage();
        String string2 = asyncPlayerChatEvent.getFormat();
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        MainConfig.Factions.Chat chat = FactionsPlugin.getInstance().conf().factions().chat();
        int n = chat.getTagInsertIndex();
        boolean bl = chat.isTagPadBefore();
        boolean bl2 = chat.isTagPadAfter();
        if (!chat.getTagReplaceString().isEmpty() && string2.contains(chat.getTagReplaceString())) {
            if (string2.contains("[FACTION_TITLE]")) {
                string2 = string2.replace("[FACTION_TITLE]", fPlayer.getTitle());
            }
            n = string2.indexOf(chat.getTagReplaceString());
            string2 = string2.replace(chat.getTagReplaceString(), "");
            bl = false;
            bl2 = false;
        } else if (!chat.getTagInsertAfterString().isEmpty() && string2.contains(chat.getTagInsertAfterString())) {
            n = string2.indexOf(chat.getTagInsertAfterString()) + chat.getTagInsertAfterString().length();
        } else if (!chat.getTagInsertBeforeString().isEmpty() && string2.contains(chat.getTagInsertBeforeString())) {
            n = string2.indexOf(chat.getTagInsertBeforeString());
        } else if (!chat.isAlwaysShowChatTag()) {
            return;
        }
        String string3 = string2.substring(0, n) + (bl && !fPlayer.getChatTag().isEmpty() ? " " : "");
        String string4 = (bl2 && !fPlayer.getChatTag().isEmpty() ? " " : "") + string2.substring(n);
        String string5 = string3 + fPlayer.getChatTag().trim() + string4;
        if (chat.isTagRelationColored()) {
            for (Player player2 : asyncPlayerChatEvent.getRecipients()) {
                if (FactionsPlugin.getInstance().getIntegrationManager().isEnabled(IntegrationManager.Integration.ESS) && Essentials.isIgnored(player2, player)) continue;
                FPlayer fPlayer2 = FPlayers.getInstance().getByPlayer(player2);
                String string6 = string3 + fPlayer.getChatTag(fPlayer2).trim() + string4;
                try {
                    player2.sendMessage(String.format(string6, player.getDisplayName(), string));
                } catch (UnknownFormatConversionException unknownFormatConversionException) {
                    FactionsPlugin.getInstance().log(Level.SEVERE, "Critical error in chat message formatting!");
                    FactionsPlugin.getInstance().log(Level.SEVERE, "NOTE: This can be fixed right now by setting chat tagInsertIndex to 0.");
                    return;
                }
            }
            asyncPlayerChatEvent.getRecipients().clear();
        }
        asyncPlayerChatEvent.setFormat(string5);
    }
}

