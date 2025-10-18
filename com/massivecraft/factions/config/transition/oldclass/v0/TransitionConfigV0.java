/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.config.transition.oldclass.v0;

import com.massivecraft.factions.config.annotation.Comment;
import com.massivecraft.factions.config.transition.oldclass.v0.OldConfV0;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TransitionConfigV0 {
    @Comment(value="The command base (by default f, making the command /f)")
    private List<String> commandBase;
    @Comment(value="Colors for relationships and default factions")
    private Colors colors = new Colors(this);
    private Factions factions = new Factions(this);
    @Comment(value="What should be logged?")
    private Logging logging = new Logging(this);
    @Comment(value="Controls certain exploit preventions")
    private Exploits exploits = new Exploits(this);
    @Comment(value="Economy support requires Vault and a compatible economy plugin")
    private Economy economy = new Economy(this);
    @Comment(value="Control for the default settings of /f map")
    private Map map = new Map(this);
    @Comment(value="PlayerVaults faction vault settings")
    private PlayerVaults playerVaults = new PlayerVaults(this);
    @Comment(value="WorldGuard settings")
    private WorldGuard worldGuard = new WorldGuard(this);

    public TransitionConfigV0(OldConfV0 oldConfV0) {
        this.commandBase = new ArrayList<String>(oldConfV0.baseCommandAliases);
        this.worldGuard.buildPriority = oldConfV0.worldGuardBuildPriority;
        this.worldGuard.checking = oldConfV0.worldGuardChecking;
        this.playerVaults.defaultMaxVaults = oldConfV0.defaultMaxVaults;
        this.playerVaults.vaultPrefix = oldConfV0.vaultPrefix;
        this.colors.relations.member = oldConfV0.colorMember.name();
        this.colors.relations.ally = oldConfV0.colorAlly.name();
        this.colors.relations.truce = oldConfV0.colorTruce.name();
        this.colors.relations.neutral = oldConfV0.colorNeutral.name();
        this.colors.relations.enemy = oldConfV0.colorEnemy.name();
        this.colors.relations.peaceful = oldConfV0.colorPeaceful.name();
        this.colors.factions.wilderness = oldConfV0.colorWilderness.name();
        this.colors.factions.safezone = oldConfV0.colorSafezone.name();
        this.colors.factions.warzone = oldConfV0.colorWar.name();
        this.factions.landRaidControl.power.playerMin = oldConfV0.powerPlayerMin;
        this.factions.landRaidControl.power.playerMax = oldConfV0.powerPlayerMax;
        this.factions.landRaidControl.power.playerStarting = oldConfV0.powerPlayerStarting;
        this.factions.landRaidControl.power.powerPerMinute = oldConfV0.powerPerMinute;
        this.factions.landRaidControl.power.lossPerDeath = oldConfV0.powerPerDeath;
        this.factions.landRaidControl.power.regenOffline = oldConfV0.powerRegenOffline;
        this.factions.landRaidControl.power.offlineLossPerDay = oldConfV0.powerOfflineLossPerDay;
        this.factions.landRaidControl.power.offlineLossLimit = oldConfV0.powerOfflineLossLimit;
        this.factions.landRaidControl.power.factionMax = oldConfV0.powerFactionMax;
        this.factions.landRaidControl.power.respawnHomeFromNoPowerLossWorlds = oldConfV0.homesRespawnFromNoPowerLossWorlds;
        this.factions.landRaidControl.power.worldsNoPowerLoss = oldConfV0.worldsNoPowerLoss;
        this.factions.landRaidControl.power.peacefulMembersDisablePowerLoss = oldConfV0.peacefulMembersDisablePowerLoss;
        this.factions.landRaidControl.power.warZonePowerLoss = oldConfV0.warZonePowerLoss;
        this.factions.landRaidControl.power.wildernessPowerLoss = oldConfV0.wildernessPowerLoss;
        this.factions.landRaidControl.power.canLeaveWithNegativePower = oldConfV0.canLeaveWithNegativePower;
        this.factions.prefixes.admin = oldConfV0.prefixAdmin;
        this.factions.prefixes.coleader = oldConfV0.prefixColeader;
        this.factions.prefixes.mod = oldConfV0.prefixMod;
        this.factions.prefixes.normal = oldConfV0.prefixNormal;
        this.factions.prefixes.recruit = oldConfV0.prefixRecruit;
        this.factions.chat.factionOnlyChat = oldConfV0.factionOnlyChat;
        this.factions.chat.tagHandledByAnotherPlugin = oldConfV0.chatTagHandledByAnotherPlugin || oldConfV0.chatTagEnabled;
        this.factions.chat.tagRelationColored = oldConfV0.chatTagRelationColored;
        this.factions.chat.tagReplaceString = oldConfV0.chatTagReplaceString;
        this.factions.chat.tagInsertAfterString = oldConfV0.chatTagInsertAfterString;
        this.factions.chat.tagInsertBeforeString = oldConfV0.chatTagInsertBeforeString;
        this.factions.chat.tagInsertIndex = oldConfV0.chatTagInsertIndex;
        this.factions.chat.tagPadBefore = oldConfV0.chatTagPadBefore;
        this.factions.chat.tagPadAfter = oldConfV0.chatTagPadAfter;
        this.factions.chat.tagFormat = oldConfV0.chatTagFormat;
        this.factions.chat.alwaysShowChatTag = oldConfV0.alwaysShowChatTag;
        this.factions.chat.factionChatFormat = oldConfV0.factionChatFormat;
        this.factions.chat.allianceChatFormat = oldConfV0.allianceChatFormat;
        this.factions.chat.truceChatFormat = oldConfV0.truceChatFormat;
        this.factions.chat.modChatFormat = oldConfV0.modChatFormat;
        this.factions.chat.broadcastDescriptionChanges = oldConfV0.broadcastDescriptionChanges;
        this.factions.chat.broadcastTagChanges = oldConfV0.broadcastTagChanges;
        this.factions.homes.enabled = oldConfV0.homesEnabled;
        this.factions.homes.mustBeInClaimedTerritory = oldConfV0.homesMustBeInClaimedTerritory;
        this.factions.homes.teleportToOnDeath = oldConfV0.homesTeleportToOnDeath;
        this.factions.homes.teleportCommandEnabled = oldConfV0.homesTeleportCommandEnabled;
        this.factions.homes.teleportCommandEssentialsIntegration = oldConfV0.homesTeleportCommandEssentialsIntegration;
        this.factions.homes.teleportCommandSmokeEffectEnabled = oldConfV0.homesTeleportCommandSmokeEffectEnabled;
        this.factions.homes.teleportCommandSmokeEffectThickness = oldConfV0.homesTeleportCommandSmokeEffectThickness;
        this.factions.homes.teleportAllowedFromEnemyTerritory = oldConfV0.homesTeleportAllowedFromEnemyTerritory;
        this.factions.homes.teleportAllowedFromDifferentWorld = oldConfV0.homesTeleportAllowedFromDifferentWorld;
        this.factions.homes.teleportAllowedEnemyDistance = oldConfV0.homesTeleportAllowedEnemyDistance;
        this.factions.homes.teleportIgnoreEnemiesIfInOwnTerritory = oldConfV0.homesTeleportIgnoreEnemiesIfInOwnTerritory;
        this.factions.pvp.disablePVPBetweenNeutralFactions = oldConfV0.disablePVPBetweenNeutralFactions;
        this.factions.pvp.disablePVPForFactionlessPlayers = oldConfV0.disablePVPForFactionlessPlayers;
        this.factions.pvp.enablePVPAgainstFactionlessInAttackersLand = oldConfV0.enablePVPAgainstFactionlessInAttackersLand;
        this.factions.pvp.noPVPDamageToOthersForXSecondsAfterLogin = oldConfV0.noPVPDamageToOthersForXSecondsAfterLogin;
        this.factions.pvp.worldsIgnorePvP = oldConfV0.worldsIgnorePvP;
        this.factions.specialCase.peacefulTerritoryDisablePVP = oldConfV0.peacefulTerritoryDisablePVP;
        this.factions.specialCase.peacefulTerritoryDisableMonsters = oldConfV0.peacefulTerritoryDisableMonsters;
        this.factions.specialCase.peacefulTerritoryDisableBoom = oldConfV0.peacefulTerritoryDisableBoom;
        this.factions.specialCase.permanentFactionsDisableLeaderPromotion = oldConfV0.permanentFactionsDisableLeaderPromotion;
        this.factions.claims.mustBeConnected = oldConfV0.claimsMustBeConnected;
        this.factions.claims.canBeUnconnectedIfOwnedByOtherFaction = oldConfV0.claimsCanBeUnconnectedIfOwnedByOtherFaction;
        this.factions.claims.requireMinFactionMembers = oldConfV0.claimsRequireMinFactionMembers;
        this.factions.claims.landsMax = oldConfV0.claimedLandsMax;
        this.factions.claims.lineClaimLimit = oldConfV0.lineClaimLimit;
        this.factions.claims.radiusClaimFailureLimit = oldConfV0.radiusClaimFailureLimit;
        this.factions.claims.worldsNoClaiming = oldConfV0.worldsNoClaiming;
        this.factions.protection.permanentFactionMemberDenyCommands = oldConfV0.permanentFactionMemberDenyCommands;
        this.factions.protection.territoryNeutralDenyCommands = oldConfV0.territoryNeutralDenyCommands;
        this.factions.protection.territoryEnemyDenyCommands = oldConfV0.territoryEnemyDenyCommands;
        this.factions.protection.territoryAllyDenyCommands = oldConfV0.territoryAllyDenyCommands;
        this.factions.protection.warzoneDenyCommands = oldConfV0.warzoneDenyCommands;
        this.factions.protection.wildernessDenyCommands = oldConfV0.wildernessDenyCommands;
        this.factions.protection.territoryBlockCreepers = oldConfV0.territoryBlockCreepers;
        this.factions.protection.territoryBlockCreepersWhenOffline = oldConfV0.territoryBlockCreepersWhenOffline;
        this.factions.protection.territoryBlockFireballs = oldConfV0.territoryBlockFireballs;
        this.factions.protection.territoryBlockFireballsWhenOffline = oldConfV0.territoryBlockFireballsWhenOffline;
        this.factions.protection.territoryBlockTNT = oldConfV0.territoryBlockTNT;
        this.factions.protection.territoryBlockTNTWhenOffline = oldConfV0.territoryBlockTNTWhenOffline;
        this.factions.protection.territoryDenyEndermanBlocks = oldConfV0.territoryDenyEndermanBlocks;
        this.factions.protection.territoryDenyEndermanBlocksWhenOffline = oldConfV0.territoryDenyEndermanBlocksWhenOffline;
        this.factions.protection.safeZoneDenyBuild = oldConfV0.safeZoneDenyBuild;
        this.factions.protection.safeZoneDenyUsage = oldConfV0.safeZoneDenyUseage;
        this.factions.protection.safeZoneBlockTNT = oldConfV0.safeZoneBlockTNT;
        this.factions.protection.safeZonePreventAllDamageToPlayers = oldConfV0.safeZonePreventAllDamageToPlayers;
        this.factions.protection.safeZoneDenyEndermanBlocks = oldConfV0.safeZoneDenyEndermanBlocks;
        this.factions.protection.warZoneDenyBuild = oldConfV0.warZoneDenyBuild;
        this.factions.protection.warZoneDenyUsage = oldConfV0.warZoneDenyUseage;
        this.factions.protection.warZoneBlockCreepers = oldConfV0.warZoneBlockCreepers;
        this.factions.protection.warZoneBlockFireballs = oldConfV0.warZoneBlockFireballs;
        this.factions.protection.warZoneBlockTNT = oldConfV0.warZoneBlockTNT;
        this.factions.protection.warZoneFriendlyFire = oldConfV0.warZoneFriendlyFire;
        this.factions.protection.warZoneDenyEndermanBlocks = oldConfV0.warZoneDenyEndermanBlocks;
        this.factions.protection.wildernessDenyBuild = oldConfV0.wildernessDenyBuild;
        this.factions.protection.wildernessDenyUsage = oldConfV0.wildernessDenyUseage;
        this.factions.protection.wildernessBlockCreepers = oldConfV0.wildernessBlockCreepers;
        this.factions.protection.wildernessBlockFireballs = oldConfV0.wildernessBlockFireballs;
        this.factions.protection.wildernessBlockTNT = oldConfV0.wildernessBlockTNT;
        this.factions.protection.wildernessDenyEndermanBlocks = oldConfV0.wildernessDenyEndermanBlocks;
        this.factions.protection.pistonProtectionThroughDenyBuild = oldConfV0.pistonProtectionThroughDenyBuild;
        this.factions.protection.territoryProtectedMaterials = oldConfV0.territoryProtectedMaterials.stream().filter(Objects::nonNull).map(Enum::name).collect(Collectors.toSet());
        this.factions.protection.territoryDenyUsageMaterials = oldConfV0.territoryDenyUseageMaterials.stream().filter(Objects::nonNull).map(Enum::name).collect(Collectors.toSet());
        this.factions.protection.territoryProtectedMaterialsWhenOffline = oldConfV0.territoryProtectedMaterialsWhenOffline.stream().filter(Objects::nonNull).map(Enum::name).collect(Collectors.toSet());
        this.factions.protection.territoryDenyUsageMaterialsWhenOffline = oldConfV0.territoryDenyUseageMaterialsWhenOffline.stream().filter(Objects::nonNull).map(Enum::name).collect(Collectors.toSet());
        this.factions.protection.playersWhoBypassAllProtection = oldConfV0.playersWhoBypassAllProtection;
        this.factions.protection.worldsNoWildernessProtection = oldConfV0.worldsNoWildernessProtection;
        this.factions.ownedArea.enabled = oldConfV0.ownedAreasEnabled;
        this.factions.ownedArea.limitPerFaction = oldConfV0.ownedAreasLimitPerFaction;
        this.factions.ownedArea.moderatorsCanSet = oldConfV0.ownedAreasModeratorsCanSet;
        this.factions.ownedArea.moderatorsBypass = oldConfV0.ownedAreaModeratorsBypass;
        this.factions.ownedArea.denyBuild = oldConfV0.ownedAreaDenyBuild;
        this.factions.ownedArea.painBuild = oldConfV0.ownedAreaPainBuild;
        this.factions.ownedArea.protectMaterials = oldConfV0.ownedAreaProtectMaterials;
        this.factions.ownedArea.denyUsage = oldConfV0.ownedAreaDenyUseage;
        this.factions.ownedArea.messageOnBorder = oldConfV0.ownedMessageOnBorder;
        this.factions.ownedArea.messageInsideTerritory = oldConfV0.ownedMessageInsideTerritory;
        this.factions.ownedArea.messageByChunk = oldConfV0.ownedMessageByChunk;
        this.factions.allowMultipleColeaders = oldConfV0.allowMultipleColeaders;
        this.factions.tagLengthMin = oldConfV0.factionTagLengthMin;
        this.factions.tagLengthMax = oldConfV0.factionTagLengthMax;
        this.factions.tagForceUpperCase = oldConfV0.factionTagForceUpperCase;
        this.factions.newFactionsDefaultOpen = oldConfV0.newFactionsDefaultOpen;
        this.factions.factionMemberLimit = oldConfV0.factionMemberLimit;
        this.factions.newPlayerStartingFactionID = oldConfV0.newPlayerStartingFactionID;
        this.factions.saveToFileEveryXMinutes = oldConfV0.saveToFileEveryXMinutes;
        this.factions.autoLeaveAfterDaysOfInactivity = oldConfV0.autoLeaveAfterDaysOfInactivity;
        this.factions.autoLeaveRoutineRunsEveryXMinutes = oldConfV0.autoLeaveRoutineRunsEveryXMinutes;
        this.factions.autoLeaveRoutineMaxMillisecondsPerTick = oldConfV0.autoLeaveRoutineMaxMillisecondsPerTick;
        this.factions.removePlayerDataWhenBanned = oldConfV0.removePlayerDataWhenBanned;
        this.factions.autoLeaveDeleteFPlayerData = oldConfV0.autoLeaveDeleteFPlayerData;
        this.factions.considerFactionsReallyOfflineAfterXMinutes = oldConfV0.considerFactionsReallyOfflineAfterXMinutes;
        this.factions.actionDeniedPainAmount = oldConfV0.actionDeniedPainAmount;
        this.logging.factionCreate = oldConfV0.logFactionCreate;
        this.logging.factionDisband = oldConfV0.logFactionDisband;
        this.logging.factionJoin = oldConfV0.logFactionJoin;
        this.logging.factionKick = oldConfV0.logFactionKick;
        this.logging.factionLeave = oldConfV0.logFactionLeave;
        this.logging.landClaims = oldConfV0.logLandClaims;
        this.logging.landUnclaims = oldConfV0.logLandUnclaims;
        this.logging.moneyTransactions = oldConfV0.logMoneyTransactions;
        this.logging.playerCommands = oldConfV0.logPlayerCommands;
        this.exploits.obsidianGenerators = oldConfV0.handleExploitObsidianGenerators;
        this.exploits.enderPearlClipping = oldConfV0.handleExploitEnderPearlClipping;
        this.exploits.interactionSpam = oldConfV0.handleExploitInteractionSpam;
        this.exploits.tntWaterlog = oldConfV0.handleExploitTNTWaterlog;
        this.exploits.liquidFlow = oldConfV0.handleExploitLiquidFlow;
        this.economy.enabled = oldConfV0.econEnabled;
        this.economy.universeAccount = oldConfV0.econUniverseAccount;
        this.economy.costClaimWilderness = oldConfV0.econCostClaimWilderness;
        this.economy.costClaimFromFactionBonus = oldConfV0.econCostClaimFromFactionBonus;
        this.economy.overclaimRewardMultiplier = oldConfV0.econOverclaimRewardMultiplier;
        this.economy.claimAdditionalMultiplier = oldConfV0.econClaimAdditionalMultiplier;
        this.economy.claimRefundMultiplier = oldConfV0.econClaimRefundMultiplier;
        this.economy.claimUnconnectedFee = oldConfV0.econClaimUnconnectedFee;
        this.economy.costCreate = oldConfV0.econCostCreate;
        this.economy.costOwner = oldConfV0.econCostOwner;
        this.economy.costSethome = oldConfV0.econCostSethome;
        this.economy.costJoin = oldConfV0.econCostJoin;
        this.economy.costLeave = oldConfV0.econCostLeave;
        this.economy.costKick = oldConfV0.econCostKick;
        this.economy.costInvite = oldConfV0.econCostInvite;
        this.economy.costHome = oldConfV0.econCostHome;
        this.economy.costTag = oldConfV0.econCostTag;
        this.economy.costDesc = oldConfV0.econCostDesc;
        this.economy.costTitle = oldConfV0.econCostTitle;
        this.economy.costList = oldConfV0.econCostList;
        this.economy.costMap = oldConfV0.econCostMap;
        this.economy.costPower = oldConfV0.econCostPower;
        this.economy.costShow = oldConfV0.econCostShow;
        this.economy.costStuck = oldConfV0.econCostStuck;
        this.economy.costOpen = oldConfV0.econCostOpen;
        this.economy.costAlly = oldConfV0.econCostAlly;
        this.economy.costTruce = oldConfV0.econCostTruce;
        this.economy.costEnemy = oldConfV0.econCostEnemy;
        this.economy.costNeutral = oldConfV0.econCostNeutral;
        this.economy.costNoBoom = oldConfV0.econCostNoBoom;
        this.economy.bankEnabled = oldConfV0.bankEnabled;
        this.economy.bankMembersCanWithdraw = oldConfV0.bankMembersCanWithdraw;
        this.economy.bankFactionPaysCosts = oldConfV0.bankFactionPaysCosts;
        this.economy.bankFactionPaysLandCosts = oldConfV0.bankFactionPaysLandCosts;
        this.map.height = oldConfV0.mapHeight;
        this.map.width = oldConfV0.mapWidth;
        this.map.showFactionKey = oldConfV0.showMapFactionKey;
        this.map.showNeutralFactionsOnMap = oldConfV0.showNeutralFactionsOnMap;
        this.map.showEnemyFactions = oldConfV0.showEnemyFactionsOnMap;
        this.map.showTruceFactions = oldConfV0.showTruceFactionsOnMap;
        this.colors.factions.safezone = oldConfV0.colorSafezone.name();
        this.colors.factions.warzone = oldConfV0.colorWar.name();
        this.colors.factions.wilderness = oldConfV0.colorWilderness.name();
        this.colors.relations.ally = oldConfV0.colorAlly.name();
        this.colors.relations.enemy = oldConfV0.colorEnemy.name();
        this.colors.relations.member = oldConfV0.colorMember.name();
        this.colors.relations.neutral = oldConfV0.colorNeutral.name();
        this.colors.relations.peaceful = oldConfV0.colorPeaceful.name();
        this.colors.relations.truce = oldConfV0.colorTruce.name();
    }

    public class Colors {
        private Factions factions = new Factions(this);
        private Relations relations = new Relations(this);

        public Colors(TransitionConfigV0 transitionConfigV0) {
        }

        public class Factions {
            private String wilderness = "GRAY";
            private String safezone = "GOLD";
            private String warzone = "DARK_RED";

            public Factions(Colors colors) {
            }
        }

        public class Relations {
            private String member = "GREEN";
            private String ally = "LIGHT_PURPLE";
            private String truce = "DARK_PURPLE";
            private String neutral = "WHITE";
            private String enemy = "RED";
            private String peaceful = "GOLD";

            public Relations(Colors colors) {
            }
        }
    }

    public class Factions {
        private Chat chat = new Chat(this);
        private Homes homes = new Homes(this);
        private PVP pvp = new PVP(this);
        private SpecialCase specialCase = new SpecialCase(this);
        private Claims claims = new Claims(this);
        private Protection protection = new Protection(this);
        @Comment(value="For claimed areas where further faction-member ownership can be defined")
        private OwnedArea ownedArea = new OwnedArea(this);
        @Comment(value="Displayed prefixes for different roles within a faction")
        private Prefix prefixes = new Prefix(this);
        private LandRaidControl landRaidControl = new LandRaidControl(this);
        private boolean allowMultipleColeaders = false;
        @Comment(value="Minimum faction tag length")
        private int tagLengthMin = 3;
        @Comment(value="Maximum faction tag length")
        private int tagLengthMax = 10;
        private boolean tagForceUpperCase = false;
        private boolean newFactionsDefaultOpen = false;
        @Comment(value="When faction membership hits this limit, players will no longer be able to join using /f join; default is 0, no limit")
        private int factionMemberLimit = 0;
        @Comment(value="What faction ID to start new players in when they first join the server; default is 0, \"no faction\"")
        private String newPlayerStartingFactionID = "0";
        private double saveToFileEveryXMinutes = 30.0;
        private double autoLeaveAfterDaysOfInactivity = 10.0;
        private double autoLeaveRoutineRunsEveryXMinutes = 5.0;
        private int autoLeaveRoutineMaxMillisecondsPerTick = 5;
        private boolean removePlayerDataWhenBanned = true;
        private boolean autoLeaveDeleteFPlayerData = true;
        private double considerFactionsReallyOfflineAfterXMinutes = 0.0;
        private int actionDeniedPainAmount = 1;
        @Comment(value="If enabled, perms can be managed separately for when the faction is offline")
        private boolean separateOfflinePerms = false;

        public Factions(TransitionConfigV0 transitionConfigV0) {
        }

        public class Chat {
            @Comment(value="Allow for players to chat only within their faction, with allies, etc.\nSet to false to only allow public chats through this plugin.")
            private boolean factionOnlyChat = true;
            @Comment(value="If true, disables adding of faction tag so another plugin can manage this")
            private transient boolean tagHandledByAnotherPlugin = false;
            private boolean tagRelationColored = true;
            private String tagReplaceString = "[FACTION]";
            private String tagInsertAfterString = "";
            private String tagInsertBeforeString = "";
            private int tagInsertIndex = 0;
            private boolean tagPadBefore = false;
            private boolean tagPadAfter = true;
            private String tagFormat = "%s\u00a7f";
            private boolean alwaysShowChatTag = true;
            private String factionChatFormat = "%s:\u00a7f %s";
            private String allianceChatFormat = "\u00a7d%s:\u00a7f %s";
            private String truceChatFormat = "\u00a75%s:\u00a7f %s";
            private String modChatFormat = "\u00a7c%s:\u00a7f %s";
            private boolean broadcastDescriptionChanges = false;
            private boolean broadcastTagChanges = false;

            public Chat(Factions factions) {
            }
        }

        public class Homes {
            private boolean enabled = true;
            private boolean mustBeInClaimedTerritory = true;
            private boolean teleportToOnDeath = true;
            private boolean teleportCommandEnabled = true;
            private boolean teleportCommandEssentialsIntegration = true;
            private boolean teleportCommandSmokeEffectEnabled = true;
            private float teleportCommandSmokeEffectThickness = 3.0f;
            private boolean teleportAllowedFromEnemyTerritory = true;
            private boolean teleportAllowedFromDifferentWorld = true;
            private double teleportAllowedEnemyDistance = 32.0;
            private boolean teleportIgnoreEnemiesIfInOwnTerritory = true;

            public Homes(Factions factions) {
            }
        }

        public class PVP {
            private boolean disablePVPBetweenNeutralFactions = false;
            private boolean disablePVPForFactionlessPlayers = false;
            private boolean enablePVPAgainstFactionlessInAttackersLand = false;
            private int noPVPDamageToOthersForXSecondsAfterLogin = 3;
            private Set<String> worldsIgnorePvP = new HashSet<String>();

            public PVP(Factions factions) {
            }
        }

        public class SpecialCase {
            private boolean peacefulTerritoryDisablePVP = true;
            private boolean peacefulTerritoryDisableMonsters = false;
            private boolean peacefulTerritoryDisableBoom = false;
            private boolean permanentFactionsDisableLeaderPromotion = false;

            public SpecialCase(Factions factions) {
            }
        }

        public class Claims {
            private boolean mustBeConnected = false;
            private boolean canBeUnconnectedIfOwnedByOtherFaction = true;
            private int requireMinFactionMembers = 1;
            private int landsMax = 0;
            private int lineClaimLimit = 5;
            @Comment(value="If someone is doing a radius claim and the process fails to claim land this many times in a row, it will exit")
            private int radiusClaimFailureLimit = 9;
            private Set<String> worldsNoClaiming = new HashSet<String>();

            public Claims(Factions factions) {
            }
        }

        public class Protection {
            @Comment(value="Commands which will be prevented if the player is a member of a permanent faction")
            private Set<String> permanentFactionMemberDenyCommands = new HashSet<String>();
            @Comment(value="Commands which will be prevented when in claimed territory of a neutral faction")
            private Set<String> territoryNeutralDenyCommands = new HashSet<String>();
            @Comment(value="Commands which will be prevented when in claimed territory of an enemy faction")
            private Set<String> territoryEnemyDenyCommands = new HashSet<String>(){
                {
                    this.add("home");
                    this.add("sethome");
                    this.add("spawn");
                    this.add("tpahere");
                    this.add("tpaccept");
                    this.add("tpa");
                }
            };
            @Comment(value="Commands which will be prevented when in claimed territory of an ally faction")
            private Set<String> territoryAllyDenyCommands = new HashSet<String>();
            @Comment(value="Commands which will be prevented when in warzone")
            private Set<String> warzoneDenyCommands = new HashSet<String>();
            @Comment(value="Commands which will be prevented when in wilderness")
            private Set<String> wildernessDenyCommands = new HashSet<String>();
            private boolean territoryBlockCreepers = false;
            private boolean territoryBlockCreepersWhenOffline = false;
            private boolean territoryBlockFireballs = false;
            private boolean territoryBlockFireballsWhenOffline = false;
            private boolean territoryBlockTNT = false;
            private boolean territoryBlockTNTWhenOffline = false;
            private boolean territoryDenyEndermanBlocks = true;
            private boolean territoryDenyEndermanBlocksWhenOffline = true;
            private boolean safeZoneDenyBuild = true;
            private boolean safeZoneDenyUsage = true;
            private boolean safeZoneBlockTNT = true;
            private boolean safeZonePreventAllDamageToPlayers = false;
            private boolean safeZoneDenyEndermanBlocks = true;
            private boolean warZoneDenyBuild = true;
            private boolean warZoneDenyUsage = true;
            private boolean warZoneBlockCreepers = false;
            private boolean warZoneBlockFireballs = false;
            private boolean warZoneBlockTNT = true;
            private boolean warZoneFriendlyFire = false;
            private boolean warZoneDenyEndermanBlocks = true;
            private boolean wildernessDenyBuild = false;
            private boolean wildernessDenyUsage = false;
            private boolean wildernessBlockCreepers = false;
            private boolean wildernessBlockFireballs = false;
            private boolean wildernessBlockTNT = false;
            private boolean wildernessDenyEndermanBlocks = false;
            private boolean pistonProtectionThroughDenyBuild = true;
            private Set<String> territoryProtectedMaterials = new HashSet<String>();
            private Set<String> territoryDenyUsageMaterials = new HashSet<String>();
            private Set<String> territoryProtectedMaterialsWhenOffline = new HashSet<String>();
            private Set<String> territoryDenyUsageMaterialsWhenOffline = new HashSet<String>();
            @Comment(value="Mainly for other plugins/mods that use a fake player to take actions, which shouldn't be subject to our protections")
            private Set<String> playersWhoBypassAllProtection = new HashSet<String>();
            private Set<String> worldsNoWildernessProtection = new HashSet<String>();

            private Protection(Factions factions) {
                this.protectMaterial("DARK_OAK_DOOR");
                this.protectMaterial("BIRCH_DOOR");
                this.protectMaterial("ACACIA_DOOR");
                this.protectMaterial("IRON_DOOR");
                this.protectMaterial("JUNGLE_DOOR");
                this.protectMaterial("OAK_DOOR");
                this.protectMaterial("SPRUCE_DOOR");
                this.protectMaterial("ACACIA_TRAPDOOR");
                this.protectMaterial("BIRCH_TRAPDOOR");
                this.protectMaterial("DARK_OAK_TRAPDOOR");
                this.protectMaterial("IRON_TRAPDOOR");
                this.protectMaterial("JUNGLE_TRAPDOOR");
                this.protectMaterial("OAK_TRAPDOOR");
                this.protectMaterial("SPRUCE_TRAPDOOR");
                this.protectMaterial("ACACIA_FENCE");
                this.protectMaterial("BIRCH_FENCE");
                this.protectMaterial("DARK_OAK_FENCE");
                this.protectMaterial("OAK_FENCE");
                this.protectMaterial("NETHER_BRICK_FENCE");
                this.protectMaterial("SPRUCE_FENCE");
                this.protectMaterial("OAK_FENCE_GATE");
                this.protectMaterial("SPRUCE_FENCE_GATE");
                this.protectMaterial("BIRCH_FENCE_GATE");
                this.protectMaterial("JUNGLE_FENCE_GATE");
                this.protectMaterial("ACACIA_FENCE_GATE");
                this.protectMaterial("DARK_OAK_FENCE_GATE");
                this.protectMaterial("DISPENSER");
                this.protectMaterial("CHEST");
                this.protectMaterial("FURNACE");
                this.protectMaterial("REPEATER");
                this.protectMaterial("JUKEBOX");
                this.protectMaterial("BREWING_STAND");
                this.protectMaterial("ENCHANTING_TABLE");
                this.protectMaterial("CAULDRON");
                this.protectMaterial("FARMLAND");
                this.protectMaterial("BEACON");
                this.protectMaterial("ANVIL");
                this.protectMaterial("TRAPPED_CHEST");
                this.protectMaterial("DROPPER");
                this.protectMaterial("HOPPER");
                this.protectUsage("FIRE_CHARGE");
                this.protectUsage("FLINT_AND_STEEL");
                this.protectUsage("BUCKET");
                this.protectUsage("WATER_BUCKET");
                this.protectUsage("LAVA_BUCKET");
            }

            private void protectMaterial(String string) {
                this.territoryProtectedMaterials.add(string);
                this.territoryProtectedMaterialsWhenOffline.add(string);
            }

            private void protectUsage(String string) {
                this.territoryDenyUsageMaterials.add(string);
                this.territoryDenyUsageMaterialsWhenOffline.add(string);
            }
        }

        public class OwnedArea {
            private boolean enabled = true;
            private int limitPerFaction = 0;
            private boolean moderatorsCanSet = false;
            private boolean moderatorsBypass = true;
            private boolean denyBuild = true;
            private boolean painBuild = false;
            private boolean protectMaterials = true;
            private boolean denyUsage = true;
            private boolean messageOnBorder = true;
            private boolean messageInsideTerritory = true;
            private boolean messageByChunk = false;

            public OwnedArea(Factions factions) {
            }
        }

        public class Prefix {
            private String admin = "***";
            private String coleader = "**";
            private String mod = "*";
            private String normal = "+";
            private String recruit = "-";

            public Prefix(Factions factions) {
            }
        }

        public class LandRaidControl {
            @Comment(value="Sets the mode of land/raid control")
            private String system = "power";
            @Comment(value="Controls the power system of land/raid control\nSet the 'system' value to 'power' to use this system")
            private Power power = new Power(this);

            public LandRaidControl(Factions factions) {
            }

            public class Power {
                private double playerMin = -10.0;
                private double playerMax = 10.0;
                private double playerStarting = 0.0;
                @Comment(value="Default health rate of 0.2 takes 5 minutes to recover one power")
                private double powerPerMinute = 0.2;
                @Comment(value="How much is lost on death")
                private double lossPerDeath = 4.0;
                @Comment(value="Does a player regenerate power while offline?")
                private boolean regenOffline = false;
                @Comment(value="A player loses this much per day offline")
                private double offlineLossPerDay = 0.0;
                @Comment(value="A player stops losing power from being offline once they reach this amount")
                private double offlineLossLimit = 0.0;
                @Comment(value="If greater than 0, used as a cap for how much power a faction can have\nAdditional power from players beyond this acts as a \"buffer\" of sorts")
                private double factionMax = 0.0;
                private boolean respawnHomeFromNoPowerLossWorlds = true;
                private Set<String> worldsNoPowerLoss = new HashSet<String>();
                private boolean peacefulMembersDisablePowerLoss = true;
                private boolean warZonePowerLoss = true;
                private boolean wildernessPowerLoss = true;
                @Comment(value="Disallow joining/leaving/kicking while power is negative")
                private boolean canLeaveWithNegativePower = true;

                public Power(LandRaidControl landRaidControl) {
                }
            }
        }
    }

    public class Logging {
        private boolean factionCreate = true;
        private boolean factionDisband = true;
        private boolean factionJoin = true;
        private boolean factionKick = true;
        private boolean factionLeave = true;
        private boolean landClaims = true;
        private boolean landUnclaims = true;
        private boolean moneyTransactions = true;
        private boolean playerCommands = true;

        public Logging(TransitionConfigV0 transitionConfigV0) {
        }
    }

    public class Exploits {
        private boolean obsidianGenerators = true;
        private boolean enderPearlClipping = true;
        private boolean interactionSpam = true;
        private boolean tntWaterlog = false;
        private boolean liquidFlow = false;

        public Exploits(TransitionConfigV0 transitionConfigV0) {
        }
    }

    public class Economy {
        private boolean enabled = false;
        private String universeAccount = "";
        private double costClaimWilderness = 30.0;
        private double costClaimFromFactionBonus = 30.0;
        private double overclaimRewardMultiplier = 0.0;
        private double claimAdditionalMultiplier = 0.5;
        private double claimRefundMultiplier = 0.7;
        private double claimUnconnectedFee = 0.0;
        private double costCreate = 100.0;
        private double costOwner = 15.0;
        private double costSethome = 30.0;
        private double costJoin = 0.0;
        private double costLeave = 0.0;
        private double costKick = 0.0;
        private double costInvite = 0.0;
        private double costHome = 0.0;
        private double costTag = 0.0;
        private double costDesc = 0.0;
        private double costTitle = 0.0;
        private double costList = 0.0;
        private double costMap = 0.0;
        private double costPower = 0.0;
        private double costShow = 0.0;
        private double costStuck = 0.0;
        private double costOpen = 0.0;
        private double costAlly = 0.0;
        private double costTruce = 0.0;
        private double costEnemy = 0.0;
        private double costNeutral = 0.0;
        private double costNoBoom = 0.0;
        @Comment(value="Faction banks, to pay for land claiming and other costs instead of individuals paying for them")
        private boolean bankEnabled = true;
        @Comment(value="Have to be at least moderator to withdraw or pay money to another faction")
        private boolean bankMembersCanWithdraw = false;
        @Comment(value="The faction pays for faction command costs, such as sethome")
        private boolean bankFactionPaysCosts = true;
        @Comment(value="The faction pays for land claiming costs.")
        private boolean bankFactionPaysLandCosts = true;

        public Economy(TransitionConfigV0 transitionConfigV0) {
        }
    }

    public class Map {
        private int height = 17;
        private int width = 49;
        private boolean showFactionKey = true;
        private boolean showNeutralFactionsOnMap = true;
        private boolean showEnemyFactions = true;
        private boolean showTruceFactions = true;

        public Map(TransitionConfigV0 transitionConfigV0) {
        }
    }

    public class PlayerVaults {
        @Comment(value="The %s is for the faction id")
        private String vaultPrefix = "faction-%s";
        private int defaultMaxVaults = 0;

        public PlayerVaults(TransitionConfigV0 transitionConfigV0) {
        }
    }

    public class WorldGuard {
        private boolean checking;
        private boolean buildPriority;

        public WorldGuard(TransitionConfigV0 transitionConfigV0) {
        }
    }
}

