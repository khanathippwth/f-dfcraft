/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.config.file;

import com.google.common.reflect.TypeToken;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.annotation.Comment;
import com.massivecraft.factions.config.annotation.DefinedType;
import com.massivecraft.factions.config.annotation.WipeOnReload;
import com.massivecraft.factions.integration.dynmap.DynmapStyle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.Setting;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

public class DynmapConfig {
    private Dynmap dynmap = new Dynmap();
    private Style style = new Style(this);

    public Dynmap dynmap() {
        return this.dynmap;
    }

    public Style style() {
        return this.style;
    }

    public class Dynmap {
        @Comment(value="Should the dynmap integration be used?")
        private boolean enabled = true;
        @Comment(value="Name of the Factions layer")
        private String layerName = "Factions";
        @Comment(value="Should the layer be visible per default")
        private boolean layerVisible = true;
        @Comment(value="Ordering priority in layer menu (low goes before high - default is 0)")
        private int layerPriority = 2;
        @Comment(value="(optional) set minimum zoom level before layer is visible (0 = default, always visible)")
        private int layerMinimumZoom = 0;
        @Comment(value="Format for popup")
        private String description = "<div class=\"infowindow\">\n<span style=\"font-weight: bold; font-size: 150%;\">{faction}</span><br>\n<span style=\"font-style: italic; font-size: 110%;\">{description}</span><br><br>\n<span style=\"font-weight: bold;\">Leader:</span> %players.leader%<br>\n<span style=\"font-weight: bold;\">Admins:</span> %players.admins.count%<br>\n<span style=\"font-weight: bold;\">Moderators:</span> %players.moderators.count%<br>\n<span style=\"font-weight: bold;\">Members:</span> %players.normals.count%<br>\n<span style=\"font-weight: bold;\">TOTAL:</span> %players.count%<br>\n</br>\n<span style=\"font-weight: bold;\">Bank:</span> %money%<br>\n<br>\n</div>";
        @Comment(value="Warp popup")
        private String warpDescription = "Warp: %warpname%";
        @Comment(value="Enable the %money% macro. Only do this if you know your economy manager is thread-safe.")
        private boolean descriptionMoney = false;
        @Comment(value="Allow players in faction to see one another on Dynmap (only relevant if Dynmap has 'player-info-protected' enabled)")
        private boolean visibilityByFaction = true;
        @Comment(value="If not empty, *only* listed factions (by name or ID) will be shown.\nTo show all factions in a world, use 'world:worldnamehere'")
        private Set<String> visibleFactions = new HashSet<String>();
        @Comment(value="To hide all factions in a world, use 'world:worldnamehere'")
        private Set<String> hiddenFactions = new HashSet<String>();
        @Comment(value="Set to true if you do a permanent country map")
        private boolean onlyUpdateWorldOnce = false;
        @Comment(value="Update claims and homes with dynmap every X seconds. Default is 300 (5 minutes)\nMinimum value: 1. To disable updating claims, edit onlyUpdateWorldOnce instead")
        private int claimUpdatePeriod = 300;
        @Comment(value="Should home markers be shown?")
        private boolean showMarkers = true;
        @Comment(value="Should warp markers be shown?")
        private boolean showWarpMarkers = false;
        private transient TypeToken<Map<String, Style>> factionStylesToken = new TypeToken<Map<String, Style>>(){};
        @Comment(value="Per-faction overrides")
        @DefinedType
        private Map<String, Style> factionStyles = new HashMap<String, Style>(){
            {
                this.put("-1", new Style(DynmapConfig.this, "#FF00FF", "#FF00FF"));
                this.put("-2", new Style(DynmapConfig.this, "#FF0000", "#FF0000"));
            }
        };
        @WipeOnReload
        private transient Map<String, DynmapStyle> styles;

        public boolean isEnabled() {
            return this.enabled;
        }

        public String getLayerName() {
            return this.layerName;
        }

        public boolean isLayerVisible() {
            return this.layerVisible;
        }

        public int getLayerPriority() {
            return this.layerPriority;
        }

        public int getLayerMinimumZoom() {
            return this.layerMinimumZoom;
        }

        public String getDescription() {
            return this.description;
        }

        public String getWarpDescription() {
            return this.warpDescription;
        }

        public boolean isDescriptionMoney() {
            return this.descriptionMoney;
        }

        public boolean isVisibilityByFaction() {
            return this.visibilityByFaction;
        }

        public Set<String> getVisibleFactions() {
            return this.visibleFactions;
        }

        public Set<String> getHiddenFactions() {
            return this.hiddenFactions;
        }

        public boolean isOnlyUpdateWorldOnce() {
            return this.onlyUpdateWorldOnce;
        }

        public int getClaimUpdatePeriod() {
            return this.claimUpdatePeriod;
        }

        public boolean isShowMarkers() {
            return this.showMarkers;
        }

        public boolean isShowWarpMarkers() {
            return this.showWarpMarkers;
        }

        public Map<String, DynmapStyle> getFactionStyles() {
            if (this.styles == null) {
                this.styles = new HashMap<String, DynmapStyle>();
                Map<String, Style> map = this.factionStyles;
                for (Map.Entry<String, Style> entry : map.entrySet()) {
                    String string = entry.getKey();
                    Style style = entry.getValue();
                    if (style instanceof Style) {
                        Style style2 = style;
                        this.styles.put(string, new DynmapStyle().setLineColor(style2.getLineColor()).setLineOpacity(style2.getLineOpacity()).setLineWeight(style2.getLineWeight()).setFillColor(style2.getFillColor()).setFillOpacity(style2.getFillOpacity()).setHomeMarker(style2.getHomeMarker()).setBoost(style2.isStyleBoost()));
                        continue;
                    }
                    if (style instanceof Map) {
                        DynmapStyle dynmapStyle = new DynmapStyle();
                        Map map2 = (Map)((Object)style);
                        if (map2.containsKey("homeMarker")) {
                            dynmapStyle.setHomeMarker(map2.get("homeMarker").toString());
                        }
                        if (map2.containsKey("fillOpacity")) {
                            dynmapStyle.setFillOpacity(this.getDouble(map2.get("fillOpacity").toString()));
                        }
                        if (map2.containsKey("lineWeight")) {
                            dynmapStyle.setLineWeight(this.getInt(map2.get("lineWeight").toString()));
                        }
                        if (map2.containsKey("lineColor")) {
                            dynmapStyle.setLineColor(map2.get("lineColor").toString());
                        }
                        if (map2.containsKey("styleBoost")) {
                            dynmapStyle.setBoost(Boolean.parseBoolean(map2.get("styleBoost").toString()));
                        }
                        if (map2.containsKey("fillColor")) {
                            dynmapStyle.setFillColor(map2.get("fillColor").toString());
                        }
                        if (map2.containsKey("lineOpacity")) {
                            dynmapStyle.setLineOpacity(this.getDouble(map2.get("lineOpacity").toString()));
                        }
                        this.styles.put(string, dynmapStyle);
                        continue;
                    }
                    FactionsPlugin.getInstance().getLogger().severe("Found broken Dynmap style entry for faction '" + string + "'");
                }
            }
            return this.styles;
        }

        private int getInt(String string) {
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException numberFormatException) {
                return 1;
            }
        }

        private double getDouble(String string) {
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException numberFormatException) {
                return 1.0;
            }
        }
    }

    @ConfigSerializable
    public class Style {
        @Setting
        private String lineColor = "#00FF00";
        @Setting
        private double lineOpacity = 0.8;
        @Setting
        private int lineWeight = 3;
        @Setting
        private String fillColor = "#00FF00";
        @Setting
        private double fillOpacity = 0.35;
        @Setting
        private String homeMarker = "greenflag";
        @Setting
        private String warpMarker = "blueflag";
        @Setting
        private boolean styleBoost = false;

        private Style(DynmapConfig dynmapConfig) {
        }

        private Style(DynmapConfig dynmapConfig, String string, String string2) {
            this.lineColor = string;
            this.fillColor = string2;
        }

        public String getLineColor() {
            return this.lineColor;
        }

        public double getLineOpacity() {
            return this.lineOpacity;
        }

        public int getLineWeight() {
            return this.lineWeight;
        }

        public String getFillColor() {
            return this.fillColor;
        }

        public double getFillOpacity() {
            return this.fillOpacity;
        }

        public String getHomeMarker() {
            return this.homeMarker;
        }

        public String getWarpMarker() {
            return this.warpMarker;
        }

        public boolean isStyleBoost() {
            return this.styleBoost;
        }

        public String toString() {
            return "Style{lineColor='" + this.lineColor + "', lineOpacity=" + this.lineOpacity + ", lineWeight=" + this.lineWeight + ", fillColor='" + this.fillColor + "', fillOpacity=" + this.fillOpacity + ", homeMarker='" + this.homeMarker + "', warpMarker='" + this.warpMarker + "', styleBoost=" + this.styleBoost + "}";
        }
    }
}

