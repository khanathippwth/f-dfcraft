/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.dynmap.markers.MarkerAPI
 *  org.dynmap.markers.MarkerSet
 */
package com.massivecraft.factions.integration.dynmap;

import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

public class TempMarkerSet {
    public String label;
    public int minimumZoom;
    public int priority;
    public boolean hideByDefault;

    public MarkerSet create(MarkerAPI markerAPI, String string) {
        MarkerSet markerSet = markerAPI.createMarkerSet(string, this.label, null, false);
        if (markerSet == null) {
            return null;
        }
        if (this.minimumZoom > 0) {
            markerSet.setMinZoom(this.minimumZoom);
        }
        markerSet.setLayerPriority(this.priority);
        markerSet.setHideByDefault(this.hideByDefault);
        return markerSet;
    }

    public void update(MarkerSet markerSet) {
        if (!markerSet.getMarkerSetLabel().equals(this.label)) {
            markerSet.setMarkerSetLabel(this.label);
        }
        if (this.minimumZoom > 0 && markerSet.getMinZoom() != this.minimumZoom) {
            markerSet.setMinZoom(this.minimumZoom);
        }
        if (markerSet.getLayerPriority() != this.priority) {
            markerSet.setLayerPriority(this.priority);
        }
        if (markerSet.getHideByDefault() != this.hideByDefault) {
            markerSet.setHideByDefault(this.hideByDefault);
        }
    }
}

