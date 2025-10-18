/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 */
package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.BrigadierManager;
import com.massivecraft.factions.cmd.CmdAHome;
import com.massivecraft.factions.cmd.CmdAdmin;
import com.massivecraft.factions.cmd.CmdAnnounce;
import com.massivecraft.factions.cmd.CmdAutoHelp;
import com.massivecraft.factions.cmd.CmdBan;
import com.massivecraft.factions.cmd.CmdBanlist;
import com.massivecraft.factions.cmd.CmdBoom;
import com.massivecraft.factions.cmd.CmdBypass;
import com.massivecraft.factions.cmd.CmdChat;
import com.massivecraft.factions.cmd.CmdChatSpy;
import com.massivecraft.factions.cmd.CmdColeader;
import com.massivecraft.factions.cmd.CmdCoords;
import com.massivecraft.factions.cmd.CmdCreate;
import com.massivecraft.factions.cmd.CmdDTR;
import com.massivecraft.factions.cmd.CmdDeinvite;
import com.massivecraft.factions.cmd.CmdDelWarp;
import com.massivecraft.factions.cmd.CmdDelhome;
import com.massivecraft.factions.cmd.CmdDescription;
import com.massivecraft.factions.cmd.CmdDisband;
import com.massivecraft.factions.cmd.CmdFly;
import com.massivecraft.factions.cmd.CmdHelp;
import com.massivecraft.factions.cmd.CmdHome;
import com.massivecraft.factions.cmd.CmdInvite;
import com.massivecraft.factions.cmd.CmdJoin;
import com.massivecraft.factions.cmd.CmdKick;
import com.massivecraft.factions.cmd.CmdLeave;
import com.massivecraft.factions.cmd.CmdLink;
import com.massivecraft.factions.cmd.CmdList;
import com.massivecraft.factions.cmd.CmdListClaims;
import com.massivecraft.factions.cmd.CmdLock;
import com.massivecraft.factions.cmd.CmdLogins;
import com.massivecraft.factions.cmd.CmdMap;
import com.massivecraft.factions.cmd.CmdMapHeight;
import com.massivecraft.factions.cmd.CmdMod;
import com.massivecraft.factions.cmd.CmdModifyPower;
import com.massivecraft.factions.cmd.CmdNear;
import com.massivecraft.factions.cmd.CmdOpen;
import com.massivecraft.factions.cmd.CmdOwner;
import com.massivecraft.factions.cmd.CmdOwnerList;
import com.massivecraft.factions.cmd.CmdPeaceful;
import com.massivecraft.factions.cmd.CmdPerm;
import com.massivecraft.factions.cmd.CmdPermanent;
import com.massivecraft.factions.cmd.CmdPermanentPower;
import com.massivecraft.factions.cmd.CmdPower;
import com.massivecraft.factions.cmd.CmdPowerBoost;
import com.massivecraft.factions.cmd.CmdReload;
import com.massivecraft.factions.cmd.CmdSB;
import com.massivecraft.factions.cmd.CmdSaveAll;
import com.massivecraft.factions.cmd.CmdSeeChunk;
import com.massivecraft.factions.cmd.CmdSetDefaultRole;
import com.massivecraft.factions.cmd.CmdSetMaxVaults;
import com.massivecraft.factions.cmd.CmdSetWarp;
import com.massivecraft.factions.cmd.CmdSethome;
import com.massivecraft.factions.cmd.CmdShow;
import com.massivecraft.factions.cmd.CmdShowInvites;
import com.massivecraft.factions.cmd.CmdStatus;
import com.massivecraft.factions.cmd.CmdStuck;
import com.massivecraft.factions.cmd.CmdTNT;
import com.massivecraft.factions.cmd.CmdTag;
import com.massivecraft.factions.cmd.CmdTicketInfo;
import com.massivecraft.factions.cmd.CmdTitle;
import com.massivecraft.factions.cmd.CmdToggleAllianceChat;
import com.massivecraft.factions.cmd.CmdTop;
import com.massivecraft.factions.cmd.CmdTrail;
import com.massivecraft.factions.cmd.CmdUnban;
import com.massivecraft.factions.cmd.CmdVault;
import com.massivecraft.factions.cmd.CmdVersion;
import com.massivecraft.factions.cmd.CmdWarp;
import com.massivecraft.factions.cmd.CmdWarpOther;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.cmd.claim.CmdAutoClaim;
import com.massivecraft.factions.cmd.claim.CmdAutoUnclaim;
import com.massivecraft.factions.cmd.claim.CmdClaim;
import com.massivecraft.factions.cmd.claim.CmdClaimAt;
import com.massivecraft.factions.cmd.claim.CmdClaimFill;
import com.massivecraft.factions.cmd.claim.CmdClaimLine;
import com.massivecraft.factions.cmd.claim.CmdSafeunclaimall;
import com.massivecraft.factions.cmd.claim.CmdUnclaim;
import com.massivecraft.factions.cmd.claim.CmdUnclaimall;
import com.massivecraft.factions.cmd.claim.CmdUnclaimfill;
import com.massivecraft.factions.cmd.claim.CmdWarunclaimall;
import com.massivecraft.factions.cmd.money.CmdMoney;
import com.massivecraft.factions.cmd.relations.CmdRelationAlly;
import com.massivecraft.factions.cmd.relations.CmdRelationEnemy;
import com.massivecraft.factions.cmd.relations.CmdRelationNeutral;
import com.massivecraft.factions.cmd.relations.CmdRelationTruce;
import com.massivecraft.factions.cmd.role.CmdDemote;
import com.massivecraft.factions.cmd.role.CmdPromote;
import com.massivecraft.factions.landraidcontrol.DTRControl;
import com.massivecraft.factions.landraidcontrol.PowerControl;
import com.massivecraft.factions.util.TL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import moss.factions.shade.me.lucko.commodore.CommodoreProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FCmdRoot
extends FCommand
implements CommandExecutor {
    private static FCmdRoot cmdBase;
    public final CmdAutoHelp cmdAutoHelp = new CmdAutoHelp();
    public BrigadierManager brigadierManager;
    public final CmdAdmin cmdAdmin = new CmdAdmin();
    public final CmdAutoClaim cmdAutoClaim = new CmdAutoClaim();
    public final CmdAutoUnclaim cmdAutoUnclaim = new CmdAutoUnclaim();
    public final CmdBoom cmdBoom = new CmdBoom();
    public final CmdBypass cmdBypass = new CmdBypass();
    public final CmdChat cmdChat = new CmdChat();
    public final CmdChatSpy cmdChatSpy = new CmdChatSpy();
    public final CmdClaim cmdClaim = new CmdClaim();
    public final CmdCoords cmdCoords = new CmdCoords();
    public final CmdCreate cmdCreate = new CmdCreate();
    public final CmdDeinvite cmdDeinvite = new CmdDeinvite();
    public final CmdDescription cmdDescription = new CmdDescription();
    public final CmdDisband cmdDisband = new CmdDisband();
    public final CmdFly cmdFly = new CmdFly();
    public final CmdHelp cmdHelp = new CmdHelp();
    public final CmdHome cmdHome = new CmdHome();
    public final CmdInvite cmdInvite = new CmdInvite();
    public final CmdJoin cmdJoin = new CmdJoin();
    public final CmdKick cmdKick = new CmdKick();
    public final CmdLeave cmdLeave = new CmdLeave();
    public final CmdList cmdList = new CmdList();
    public final CmdLink cmdLink = new CmdLink();
    public final CmdLock cmdLock = new CmdLock();
    public final CmdMap cmdMap = new CmdMap();
    public final CmdMod cmdMod = new CmdMod();
    public final CmdMoney cmdMoney = new CmdMoney();
    public final CmdOpen cmdOpen = new CmdOpen();
    public final CmdOwner cmdOwner = new CmdOwner();
    public final CmdOwnerList cmdOwnerList = new CmdOwnerList();
    public final CmdPeaceful cmdPeaceful = new CmdPeaceful();
    public final CmdPermanent cmdPermanent = new CmdPermanent();
    public final CmdPermanentPower cmdPermanentPower = new CmdPermanentPower();
    public final CmdPowerBoost cmdPowerBoost = new CmdPowerBoost();
    public final CmdPower cmdPower = new CmdPower();
    public final CmdDTR cmdDTR = new CmdDTR();
    public final CmdRelationAlly cmdRelationAlly = new CmdRelationAlly();
    public final CmdRelationEnemy cmdRelationEnemy = new CmdRelationEnemy();
    public final CmdRelationNeutral cmdRelationNeutral = new CmdRelationNeutral();
    public final CmdRelationTruce cmdRelationTruce = new CmdRelationTruce();
    public final CmdReload cmdReload = new CmdReload();
    public final CmdSafeunclaimall cmdSafeunclaimall = new CmdSafeunclaimall();
    public final CmdSaveAll cmdSaveAll = new CmdSaveAll();
    public final CmdSethome cmdSethome = new CmdSethome();
    public final CmdDelhome cmdDelhome = new CmdDelhome();
    public final CmdShow cmdShow = new CmdShow();
    public final CmdStatus cmdStatus = new CmdStatus();
    public final CmdStuck cmdStuck = new CmdStuck();
    public final CmdTag cmdTag = new CmdTag();
    public final CmdTitle cmdTitle = new CmdTitle();
    public final CmdToggleAllianceChat cmdToggleAllianceChat = new CmdToggleAllianceChat();
    public final CmdUnclaim cmdUnclaim = new CmdUnclaim();
    public final CmdUnclaimall cmdUnclaimall = new CmdUnclaimall();
    public final CmdVersion cmdVersion = new CmdVersion();
    public final CmdWarunclaimall cmdWarunclaimall = new CmdWarunclaimall();
    public final CmdSB cmdSB = new CmdSB();
    public final CmdShowInvites cmdShowInvites = new CmdShowInvites();
    public final CmdAnnounce cmdAnnounce = new CmdAnnounce();
    public final CmdSeeChunk cmdSeeChunk = new CmdSeeChunk();
    public final CmdWarp cmdWarp = new CmdWarp();
    public final CmdWarpOther cmdWarpOther = new CmdWarpOther();
    public final CmdSetWarp cmdSetWarp = new CmdSetWarp();
    public final CmdDelWarp cmdDelWarp = new CmdDelWarp();
    public final CmdModifyPower cmdModifyPower = new CmdModifyPower();
    public final CmdLogins cmdLogins = new CmdLogins();
    public final CmdClaimLine cmdClaimLine = new CmdClaimLine();
    public final CmdClaimFill cmdClaimFill = new CmdClaimFill();
    public final CmdUnclaimfill cmdUnclaimfill = new CmdUnclaimfill();
    public final CmdTop cmdTop = new CmdTop();
    public final CmdAHome cmdAHome = new CmdAHome();
    public final CmdPerm cmdPerm = new CmdPerm();
    public final CmdPromote cmdPromote = new CmdPromote();
    public final CmdDemote cmdDemote = new CmdDemote();
    public final CmdSetDefaultRole cmdSetDefaultRole = new CmdSetDefaultRole();
    public final CmdMapHeight cmdMapHeight = new CmdMapHeight();
    public final CmdClaimAt cmdClaimAt = new CmdClaimAt();
    public final CmdBan cmdban = new CmdBan();
    public final CmdUnban cmdUnban = new CmdUnban();
    public final CmdBanlist cmdbanlist = new CmdBanlist();
    public final CmdColeader cmdColeader = new CmdColeader();
    public final CmdNear cmdNear = new CmdNear();
    public final CmdTrail cmdTrail = new CmdTrail();
    public final CmdTicketInfo cmdTicketInfo = new CmdTicketInfo();
    public final CmdTNT cmdTNT = new CmdTNT();
    public final CmdListClaims cmdListClaims = new CmdListClaims();

    public static FCmdRoot getInstance() {
        return cmdBase;
    }

    public FCmdRoot() {
        cmdBase = this;
        if (this.canCommodore()) {
            this.brigadierManager = new BrigadierManager();
        }
        this.aliases.addAll(FactionsPlugin.getInstance().conf().getCommandBase());
        this.aliases.removeAll(Collections.singletonList(null));
        this.setHelpShort("The faction base command");
        this.helpLong.add(FactionsPlugin.getInstance().txt().parseTags("<i>This command contains all faction stuff."));
        this.addSubCommand(this.cmdAdmin);
        this.addSubCommand(this.cmdAutoUnclaim);
        this.addSubCommand(this.cmdAutoClaim);
        this.addSubCommand(this.cmdBoom);
        this.addSubCommand(this.cmdBypass);
        this.addSubCommand(this.cmdChat);
        this.addSubCommand(this.cmdToggleAllianceChat);
        this.addSubCommand(this.cmdChatSpy);
        this.addSubCommand(this.cmdClaim);
        this.addSubCommand(this.cmdCoords);
        this.addSubCommand(this.cmdCreate);
        this.addSubCommand(this.cmdDeinvite);
        this.addSubCommand(this.cmdDescription);
        this.addSubCommand(this.cmdDisband);
        this.addSubCommand(this.cmdHelp);
        this.addSubCommand(this.cmdHome);
        this.addSubCommand(this.cmdInvite);
        this.addSubCommand(this.cmdJoin);
        this.addSubCommand(this.cmdKick);
        this.addSubCommand(this.cmdLeave);
        this.addSubCommand(this.cmdList);
        this.addSubCommand(this.cmdLink);
        this.addSubCommand(this.cmdLock);
        this.addSubCommand(this.cmdMap);
        this.addSubCommand(this.cmdMod);
        this.addSubCommand(this.cmdMoney);
        this.addSubCommand(this.cmdOpen);
        this.addSubCommand(this.cmdOwner);
        this.addSubCommand(this.cmdOwnerList);
        this.addSubCommand(this.cmdPeaceful);
        this.addSubCommand(this.cmdPermanent);
        this.addSubCommand(this.cmdRelationAlly);
        this.addSubCommand(this.cmdRelationEnemy);
        this.addSubCommand(this.cmdRelationNeutral);
        this.addSubCommand(this.cmdRelationTruce);
        this.addSubCommand(this.cmdReload);
        this.addSubCommand(this.cmdSafeunclaimall);
        this.addSubCommand(this.cmdSaveAll);
        this.addSubCommand(this.cmdSethome);
        this.addSubCommand(this.cmdDelhome);
        this.addSubCommand(this.cmdShow);
        this.addSubCommand(this.cmdStatus);
        this.addSubCommand(this.cmdStuck);
        this.addSubCommand(this.cmdTag);
        this.addSubCommand(this.cmdTitle);
        this.addSubCommand(this.cmdUnclaim);
        this.addSubCommand(this.cmdUnclaimall);
        this.addSubCommand(this.cmdVersion);
        this.addSubCommand(this.cmdWarunclaimall);
        this.addSubCommand(this.cmdSB);
        this.addSubCommand(this.cmdShowInvites);
        this.addSubCommand(this.cmdAnnounce);
        this.addSubCommand(this.cmdSeeChunk);
        this.addSubCommand(this.cmdWarp);
        this.addSubCommand(this.cmdWarpOther);
        this.addSubCommand(this.cmdSetWarp);
        this.addSubCommand(this.cmdDelWarp);
        this.addSubCommand(this.cmdLogins);
        this.addSubCommand(this.cmdClaimLine);
        this.addSubCommand(this.cmdClaimFill);
        this.addSubCommand(this.cmdUnclaimfill);
        this.addSubCommand(this.cmdAHome);
        this.addSubCommand(this.cmdPerm);
        this.addSubCommand(this.cmdPromote);
        this.addSubCommand(this.cmdDemote);
        this.addSubCommand(this.cmdSetDefaultRole);
        this.addSubCommand(this.cmdMapHeight);
        this.addSubCommand(this.cmdClaimAt);
        this.addSubCommand(this.cmdban);
        this.addSubCommand(this.cmdUnban);
        this.addSubCommand(this.cmdbanlist);
        this.addSubCommand(this.cmdColeader);
        this.addSubCommand(this.cmdNear);
        this.addSubCommand(this.cmdTicketInfo);
        this.addSubCommand(this.cmdListClaims);
        if (FactionsPlugin.getInstance().getLandRaidControl() instanceof PowerControl) {
            FactionsPlugin.getInstance().getLogger().info("Using POWER for land/raid control. Enabling power commands.");
            this.addSubCommand(this.cmdPermanentPower);
            this.addSubCommand(this.cmdPower);
            this.addSubCommand(this.cmdPowerBoost);
            this.addSubCommand(this.cmdModifyPower);
        } else if (FactionsPlugin.getInstance().getLandRaidControl() instanceof DTRControl) {
            FactionsPlugin.getInstance().getLogger().info("Using DTR for land/raid control. Enabling DTR commands.");
            this.addSubCommand(this.cmdDTR);
        }
        if (FactionsPlugin.getInstance().conf().commands().tnt().isEnable()) {
            this.addSubCommand(this.cmdTNT);
            FactionsPlugin.getInstance().getLogger().info("Enabling TNT bank management");
        }
        if (FactionsPlugin.getInstance().conf().commands().fly().isEnable()) {
            this.addSubCommand(this.cmdFly);
            this.addSubCommand(this.cmdTrail);
            FactionsPlugin.getInstance().getLogger().info("Enabling /f fly command");
        } else {
            FactionsPlugin.getInstance().getLogger().info("Faction flight set to false in main.conf. Not enabling /f fly command.");
        }
    }

    public void done() {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("FactionsTop")) {
            FactionsPlugin.getInstance().getLogger().info("Found FactionsTop plugin. Disabling our own /f top command.");
        } else {
            this.addSubCommand(this.cmdTop);
        }
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlayerVaults")) {
            FactionsPlugin.getInstance().getLogger().info("Found PlayerVaults hook, adding /f vault and /f setmaxvault commands.");
            this.addSubCommand(new CmdSetMaxVaults());
            this.addSubCommand(new CmdVault());
        }
        if (this.canCommodore()) {
            this.brigadierManager.build();
        }
    }

    @Override
    public void perform(CommandContext commandContext) {
        commandContext.commandChain.add(this);
        this.cmdHelp.execute(commandContext);
    }

    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] stringArray) {
        if (!this.plugin.worldUtil().isEnabled(commandSender)) {
            commandSender.sendMessage(TL.GENERIC_DISABLEDWORLD.toString());
            return false;
        }
        this.execute(new CommandContext(commandSender, new ArrayList<String>(Arrays.asList(stringArray)), string));
        return true;
    }

    @Override
    public void addSubCommand(FCommand fCommand) {
        super.addSubCommand(fCommand);
        if (this.canCommodore()) {
            this.brigadierManager.addSubCommand(fCommand);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.GENERIC_PLACEHOLDER;
    }

    private boolean canCommodore() {
        return CommodoreProvider.isSupported();
    }
}

