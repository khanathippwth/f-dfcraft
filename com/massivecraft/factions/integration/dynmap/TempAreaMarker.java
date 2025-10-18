/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.dynmap.markers.AreaMarker
 *  org.dynmap.markers.MarkerSet
 */
package com.massivecraft.factions.integration.dynmap;

import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerSet;

public class TempAreaMarker {
    public String label;
    public String world;
    public double[] x;
    public double[] z;
    public String description;
    public int lineColor;
    public double lineOpacity;
    public int lineWeight;
    public int fillColor;
    public double fillOpacity;
    public boolean boost;

    public AreaMarker create(MarkerSet markerSet, String string) {
        AreaMarker areaMarker = markerSet.createAreaMarker(string, this.label, false, this.world, this.x, this.z, false);
        if (areaMarker == null) {
            return null;
        }
        areaMarker.setDescription(this.description);
        areaMarker.setLineStyle(this.lineWeight, this.lineOpacity, this.lineColor);
        areaMarker.setFillStyle(this.fillOpacity, this.fillColor);
        areaMarker.setBoostFlag(this.boost);
        return areaMarker;
    }

    public void update(AreaMarker areaMarker) {
        if (!TempAreaMarker.equals(areaMarker, this.x, this.z)) {
            areaMarker.setCornerLocations(this.x, this.z);
        }
        if (!areaMarker.getLabel().equals(this.label)) {
            areaMarker.setLabel(this.label);
        }
        if (!areaMarker.getDescription().equals(this.description)) {
            areaMarker.setDescription(this.description);
        }
        if (areaMarker.getLineWeight() != this.lineWeight || areaMarker.getLineOpacity() != this.lineOpacity || areaMarker.getLineColor() != this.lineColor) {
            areaMarker.setLineStyle(this.lineWeight, this.lineOpacity, this.lineColor);
        }
        if (areaMarker.getFillOpacity() != this.fillOpacity || areaMarker.getFillColor() != this.fillColor) {
            areaMarker.setFillStyle(this.fillOpacity, this.fillColor);
        }
        if (areaMarker.getBoostFlag() != this.boost) {
            areaMarker.setBoostFlag(this.boost);
        }
    }

    public static boolean equals(AreaMarker areaMarker, double[] dArray, double[] dArray2) {
        int n = areaMarker.getCornerCount();
        if (dArray.length != n) {
            return false;
        }
        if (dArray2.length != n) {
            return false;
        }
        for (int i = 0; i < n; ++i) {
            if (areaMarker.getCornerX(i) != dArray[i]) {
                return false;
            }
            if (areaMarker.getCornerZ(i) == dArray2[i]) continue;
            return false;
        }
        return true;
    }
}

