/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.conversations.Conversable
 *  org.bukkit.conversations.ConversationAbandonedEvent
 *  org.bukkit.conversations.ConversationAbandonedListener
 *  org.bukkit.conversations.ConversationContext
 *  org.bukkit.conversations.ConversationFactory
 *  org.bukkit.conversations.InactivityConversationCanceller
 *  org.bukkit.conversations.ManuallyAbandonedConversationCanceller
 *  org.bukkit.conversations.Prompt
 *  org.bukkit.conversations.StringPrompt
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.plugin.Plugin
 */
package com.massivecraft.factions.gui;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.gui.GUI;
import com.massivecraft.factions.gui.SimpleItem;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.util.material.MaterialDb;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.InactivityConversationCanceller;
import org.bukkit.conversations.ManuallyAbandonedConversationCanceller;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.Plugin;

public class WarpGUI
extends GUI<Integer> {
    private static final SimpleItem warpItem = SimpleItem.builder().setName("&8[&7{warp}&8]").setMaterial(Material.LIME_STAINED_GLASS).build();
    private static final SimpleItem passwordModifier = SimpleItem.builder().setMaterial(Material.BLACK_STAINED_GLASS).setLore(Collections.singletonList("&8Password Protected")).build();
    private final List<String> warps;
    private final String name;
    private final int page;
    private final Faction faction;

    public WarpGUI(FPlayer fPlayer, Faction faction) {
        this(fPlayer, -1, faction);
    }

    private WarpGUI(FPlayer fPlayer, int n, Faction faction) {
        super(fPlayer, WarpGUI.getRows(faction));
        this.faction = faction;
        this.warps = new ArrayList<String>(faction.getWarps().keySet());
        if (n == -1 && this.warps.size() > 45) {
            n = 0;
        }
        this.page = n;
        this.name = n == -1 ? TL.GUI_WARPS_ONE_PAGE.format(faction.getTag()) : TL.GUI_WARPS_PAGE.format(faction.getTag(), n + 1);
        this.build();
    }

    @Override
    public String getName() {
        return this.name;
    }

    private static int getRows(Faction faction) {
        int n = faction.getWarps().size();
        if (n == 0) {
            return 1;
        }
        if (n > 45) {
            return 6;
        }
        return (int)Math.ceil((double)n / 9.0);
    }

    @Override
    protected String parse(String string, Integer n) {
        if (n < 0) {
            return string;
        }
        if (this.warps.size() > n) {
            string = string.replace("{warp}", this.warps.get(n));
        }
        return string;
    }

    @Override
    protected void onClick(Integer n, ClickType clickType) {
        if (!this.faction.hasAccess(this.user, PermissibleActions.WARP, this.user.getLastStoodAt())) {
            this.user.msg(TL.COMMAND_FWARP_NOACCESS, this.faction.getTag(this.user));
            this.user.getPlayer().closeInventory();
            return;
        }
        if (n == -1) {
            int n2 = this.page + 1;
            new WarpGUI(this.user, n2, this.faction).open();
            return;
        }
        if (n == -2) {
            int n3 = this.page - 1;
            new WarpGUI(this.user, n3, this.faction).open();
            return;
        }
        if (this.warps.size() > n) {
            String string = this.warps.get(n);
            if (!this.faction.hasWarpPassword(string)) {
                if (this.transact()) {
                    this.doWarmup(string);
                }
            } else {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("warp", string);
                PasswordPrompt passwordPrompt = new PasswordPrompt();
                ConversationFactory conversationFactory = new ConversationFactory((Plugin)FactionsPlugin.getInstance()).withModality(false).withLocalEcho(false).withInitialSessionData(hashMap).withFirstPrompt((Prompt)passwordPrompt).addConversationAbandonedListener((ConversationAbandonedListener)passwordPrompt).withTimeout(5);
                this.user.getPlayer().closeInventory();
                conversationFactory.buildConversation((Conversable)this.user.getPlayer()).begin();
            }
        }
    }

    @Override
    protected Map<Integer, Integer> createSlotMap() {
        int n;
        int n2;
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        if (this.page == -1) {
            n2 = this.warps.size();
            n = 0;
        } else {
            n2 = Math.min(this.warps.size() - 45 * this.page, 45);
            n = 45 * this.page;
            if (this.page > 0) {
                hashMap.put(45, -2);
            }
            if (this.page < this.getMaxPages() - 1) {
                hashMap.put(53, -1);
            }
        }
        int n3 = 0;
        int n4 = n;
        for (int i = 0; i < n2; ++i) {
            int n5;
            if (i % 9 == 0 && (n5 = n2 - i) < 9) {
                n3 = (9 - n5) / 2;
            }
            hashMap.put(i + n3, n4);
            ++n4;
        }
        return hashMap;
    }

    @Override
    protected SimpleItem getItem(Integer n) {
        if (n == -1) {
            return SimpleItem.builder().setName(TL.GUI_BUTTON_NEXT.toString()).setMaterial(MaterialDb.get("ARROW")).build();
        }
        if (n == -2) {
            return SimpleItem.builder().setName(TL.GUI_BUTTON_PREV.toString()).setMaterial(MaterialDb.get("ARROW")).build();
        }
        SimpleItem simpleItem = new SimpleItem(warpItem);
        if (this.faction.hasWarpPassword(this.warps.get(n))) {
            simpleItem.merge(passwordModifier);
        }
        return simpleItem;
    }

    private int getMaxPages() {
        if (this.warps.size() <= 45) {
            return 0;
        }
        return (int)Math.ceil((double)this.warps.size() / 45.0);
    }

    @Override
    protected Map<Integer, SimpleItem> createDummyItems() {
        return Collections.emptyMap();
    }

    private void doWarmup(String string) {
        WarmUpUtil.process(this.user, WarmUpUtil.Warmup.WARP, TL.WARMUPS_NOTIFY_TELEPORT, string, () -> {
            Player player = Bukkit.getPlayer((UUID)this.user.getPlayer().getUniqueId());
            if (player != null) {
                if (!this.faction.hasAccess(this.user, PermissibleActions.WARP, this.user.getLastStoodAt())) {
                    this.user.msg(TL.COMMAND_FWARP_NOACCESS, this.faction.getTag(this.user));
                    return;
                }
                FactionsPlugin.getInstance().teleport(player, this.faction.getWarp(string).getLocation()).thenAccept(bl -> {
                    if (bl.booleanValue()) {
                        this.user.msg(TL.COMMAND_FWARP_WARPED, string);
                    }
                });
            }
        }, FactionsPlugin.getInstance().conf().commands().warp().getDelay());
    }

    private boolean transact() {
        if (!this.user.isAdminBypassing()) {
            return true;
        }
        double d = FactionsPlugin.getInstance().conf().economy().getCostWarp();
        if (!Econ.shouldBeUsed() || this.user == null || d == 0.0 || this.user.isAdminBypassing()) {
            return true;
        }
        if (FactionsPlugin.getInstance().conf().economy().isBankEnabled() && FactionsPlugin.getInstance().conf().economy().isBankFactionPaysCosts() && this.user.hasFaction() && this.user.getFaction().hasAccess(this.user, PermissibleActions.ECONOMY, this.user.getLastStoodAt())) {
            return Econ.modifyMoney(this.user.getFaction(), -d, TL.COMMAND_FWARP_TOWARP.toString(), TL.COMMAND_FWARP_FORWARPING.toString());
        }
        return Econ.modifyMoney(this.user, -d, TL.COMMAND_FWARP_TOWARP.toString(), TL.COMMAND_FWARP_FORWARPING.toString());
    }

    private class PasswordPrompt
    extends StringPrompt
    implements ConversationAbandonedListener {
        private PasswordPrompt() {
        }

        public String getPromptText(ConversationContext conversationContext) {
            return TL.COMMAND_FWARP_PASSWORD_REQUIRED.toString();
        }

        public Prompt acceptInput(ConversationContext conversationContext, String string) {
            String string2 = (String)conversationContext.getSessionData((Object)"warp");
            if (WarpGUI.this.faction.isWarpPassword(string2, string)) {
                if (WarpGUI.this.transact()) {
                    WarpGUI.this.doWarmup(string2);
                }
            } else {
                WarpGUI.this.user.msg(TL.COMMAND_FWARP_INVALID_PASSWORD, new Object[0]);
            }
            return END_OF_CONVERSATION;
        }

        public void conversationAbandoned(ConversationAbandonedEvent conversationAbandonedEvent) {
            if (conversationAbandonedEvent.getCanceller() instanceof ManuallyAbandonedConversationCanceller || conversationAbandonedEvent.getCanceller() instanceof InactivityConversationCanceller) {
                WarpGUI.this.user.msg(TL.COMMAND_FWARP_PASSWORD_CANCEL, new Object[0]);
                WarpGUI.this.open();
            }
        }
    }
}

