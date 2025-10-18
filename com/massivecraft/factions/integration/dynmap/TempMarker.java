/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.dynmap.markers.Marker
 *  org.dynmap.markers.MarkerAPI
 *  org.dynmap.markers.MarkerIcon
 *  org.dynmap.markers.MarkerSet
 */
package com.massivecraft.factions.integration.dynmap;

import com.massivecraft.factions.FactionsPlugin;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

public class TempMarker {
    public String label;
    public String world;
    public double x;
    public double y;
    public double z;
    public String iconName;
    public String description;

    public Marker create(MarkerAPI markerAPI, MarkerSet markerSet, String string) {
        Marker marker = markerSet.createMarker(string, this.label, this.world, this.x, this.y, this.z, TempMarker.getMarkerIcon(markerAPI, this.iconName), false);
        if (marker == null) {
            return null;
        }
        marker.setDescription(this.description);
        return marker;
    }

    public void update(MarkerAPI markerAPI, Marker marker) {
        if (!this.world.equals(marker.getWorld()) || this.x != marker.getX() || this.y != marker.getY() || this.z != marker.getZ()) {
            marker.setLocation(this.world, this.x, this.y, this.z);
        }
        if (!marker.getLabel().equals(this.label)) {
            marker.setLabel(this.label);
        }
        MarkerIcon markerIcon = TempMarker.getMarkerIcon(markerAPI, this.iconName);
        if (marker.getMarkerIcon() == null || !marker.getMarkerIcon().equals((Object)markerIcon)) {
            marker.setMarkerIcon(markerIcon);
        }
        if (!marker.getDescription().equals(this.description)) {
            marker.setDescription(this.description);
        }
    }

    public static MarkerIcon getMarkerIcon(MarkerAPI markerAPI, String string) {
        MarkerIcon markerIcon = markerAPI.getMarkerIcon(string);
        if (markerIcon == null) {
            markerIcon = markerAPI.getMarkerIcon(FactionsPlugin.getInstance().getConfigManager().getDynmapConfig().style().getHomeMarker());
        }
        return markerIcon;
    }
}

