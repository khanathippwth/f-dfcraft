/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.config.file;

import com.google.common.collect.ImmutableMap;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.annotation.Comment;
import com.massivecraft.factions.config.annotation.WipeOnReload;
import com.massivecraft.factions.perms.PermSelector;
import com.massivecraft.factions.perms.PermSelectorRegistry;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.PermissibleActionRegistry;
import com.massivecraft.factions.perms.PermissibleActions;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.perms.selector.RelationAtLeastSelector;
import com.massivecraft.factions.perms.selector.RoleAtLeastSelector;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PermissionsConfig {
    @Comment(value="This is an auto-updating list of known actions and descriptions.\nEditing this list has no effect.")
    @WipeOnReload
    private List<String> availableActions = new ArrayList<String>(){
        {
            for (PermissibleAction permissibleAction : PermissibleActionRegistry.getActions()) {
                this.add(permissibleAction.getName() + " - " + permissibleAction.getDescription());
            }
        }
    };
    @Comment(value="This is an auto-updating list of known selectors.\nEditing this list has no effect.")
    @WipeOnReload
    private List<String> availableSelectors = new ArrayList<String>(){
        {
            this.addAll(PermSelectorRegistry.getSelectors());
        }
    };
    @Comment(value="List actions here to hide from visibility. Comma separated, in quotes.\nDefaults and forced perms defined here will still be used.")
    private List<String> hiddenActions = new ArrayList<String>();
    private Map<String, Map<String, Boolean>> defaultPermissions = new LinkedHashMap<String, Map<String, Boolean>>();
    @WipeOnReload
    private transient Map<PermSelector, Map<String, Boolean>> defaultPermissionsMap = null;
    private List<String> defaultPermissionsOrder = new ArrayList<String>();
    @WipeOnReload
    private transient List<PermSelector> defaultPermissionsOrderList = null;
    private Map<String, Map<String, Boolean>> overridePermissions = new LinkedHashMap<String, Map<String, Boolean>>();
    @WipeOnReload
    private transient Map<PermSelector, Map<String, Boolean>> overridePermissionsMap = null;
    private List<String> overridePermissionsOrder = new ArrayList<String>();
    @WipeOnReload
    private transient List<PermSelector> overridePermissionsOrderList = null;

    public PermissionsConfig() {
        this.defaultPermissionsOrder.add(new RoleAtLeastSelector(Role.COLEADER).serialize());
        this.defaultPermissions.put(new RoleAtLeastSelector(Role.COLEADER).serialize(), ImmutableMap.builder().put(PermissibleActions.SETHOME.name(), true).put(PermissibleActions.ECONOMY.name(), true).build());
        this.defaultPermissionsOrder.add(new RoleAtLeastSelector(Role.MODERATOR).serialize());
        this.defaultPermissions.put(new RoleAtLeastSelector(Role.MODERATOR).serialize(), ImmutableMap.builder().put(PermissibleActions.BAN.name(), true).put(PermissibleActions.INVITE.name(), true).put(PermissibleActions.KICK.name(), true).put(PermissibleActions.LISTCLAIMS.name(), true).put(PermissibleActions.PROMOTE.name(), true).put(PermissibleActions.SETWARP.name(), true).put(PermissibleActions.TERRITORY.name(), true).put(PermissibleActions.TNTWITHDRAW.name(), true).build());
        this.defaultPermissionsOrder.add(new RoleAtLeastSelector(Role.RECRUIT).serialize());
        this.defaultPermissions.put(new RoleAtLeastSelector(Role.RECRUIT).serialize(), ImmutableMap.builder().put(PermissibleActions.BUILD.name(), true).put(PermissibleActions.BUTTON.name(), true).put(PermissibleActions.CONTAINER.name(), true).put(PermissibleActions.DESTROY.name(), true).put(PermissibleActions.DOOR.name(), true).put(PermissibleActions.FLY.name(), true).put(PermissibleActions.FROSTWALK.name(), true).put(PermissibleActions.HOME.name(), true).put(PermissibleActions.ITEM.name(), true).put(PermissibleActions.LEVER.name(), true).put(PermissibleActions.PLATE.name(), true).put(PermissibleActions.TNTDEPOSIT.name(), true).put(PermissibleActions.WARP.name(), true).build());
        this.defaultPermissionsOrder.add(new RelationAtLeastSelector(Relation.ALLY).serialize());
        this.defaultPermissions.put(new RelationAtLeastSelector(Relation.ALLY).serialize(), ImmutableMap.builder().put(PermissibleActions.BUTTON.name(), true).put(PermissibleActions.DOOR.name(), true).put(PermissibleActions.FLY.name(), true).put(PermissibleActions.LEVER.name(), true).put(PermissibleActions.PLATE.name(), true).build());
    }

    public List<String> getHiddenActions() {
        return this.hiddenActions;
    }

    public Map<PermSelector, Map<String, Boolean>> getDefaultPermissions() {
        if (this.defaultPermissionsMap == null) {
            this.defaultPermissionsMap = new LinkedHashMap<PermSelector, Map<String, Boolean>>();
            for (Map.Entry<String, Map<String, Boolean>> entry : this.defaultPermissions.entrySet()) {
                if (!this.defaultPermissionsOrder.contains(entry.getKey())) {
                    FactionsPlugin.getInstance().getLogger().warning("Found '" + entry.getKey() + "' in defaultPermissions but not in defaultPermissionsOrder. Ignoring.");
                    continue;
                }
                this.defaultPermissionsMap.put(PermSelectorRegistry.create(entry.getKey(), true), entry.getValue());
            }
        }
        return this.defaultPermissionsMap;
    }

    public List<PermSelector> getDefaultPermissionsOrder() {
        if (this.defaultPermissionsOrderList == null) {
            this.defaultPermissionsOrderList = new ArrayList<PermSelector>();
            for (String string : this.defaultPermissionsOrder) {
                if (!this.defaultPermissions.containsKey(string)) {
                    FactionsPlugin.getInstance().getLogger().warning("Found '" + string + "' in defaultPermissionsOrder but not in defaultPermissions. Ignoring.");
                    continue;
                }
                this.defaultPermissionsOrderList.add(PermSelectorRegistry.create(string, true));
            }
        }
        return this.defaultPermissionsOrderList;
    }

    public Map<PermSelector, Map<String, Boolean>> getOverridePermissions() {
        if (this.overridePermissionsMap == null) {
            this.overridePermissionsMap = new LinkedHashMap<PermSelector, Map<String, Boolean>>();
            for (Map.Entry<String, Map<String, Boolean>> entry : this.overridePermissions.entrySet()) {
                if (!this.overridePermissionsOrder.contains(entry.getKey())) {
                    FactionsPlugin.getInstance().getLogger().warning("Found '" + entry.getKey() + "' in overridePermissions but not in overridePermissionsOrder. Ignoring.");
                    continue;
                }
                this.overridePermissionsMap.put(PermSelectorRegistry.create(entry.getKey(), true), entry.getValue());
            }
        }
        return this.overridePermissionsMap;
    }

    public List<PermSelector> getOverridePermissionsOrder() {
        if (this.overridePermissionsOrderList == null) {
            this.overridePermissionsOrderList = new ArrayList<PermSelector>();
            for (String string : this.overridePermissionsOrder) {
                if (!this.overridePermissions.containsKey(string)) {
                    FactionsPlugin.getInstance().getLogger().warning("Found '" + string + "' in overridePermissionsOrder but not in overridePermissions. Ignoring.");
                    continue;
                }
                this.overridePermissionsOrderList.add(PermSelectorRegistry.create(string, true));
            }
        }
        return this.overridePermissionsOrderList;
    }
}

