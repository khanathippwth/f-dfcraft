/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.integration.dynmap;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.DynmapConfig;

public class DynmapStyle {
    public static final String DEFAULT_LINE_COLOR = "#00FF00";
    public static final double DEFAULT_LINE_OPACITY = 0.8;
    public static final int DEFAULT_LINE_WEIGHT = 3;
    public static final String DEFAULT_FILL_COLOR = "#00FF00";
    public static final double DEFAULT_FILL_OPACITY = 0.35;
    public static final String DEFAULT_HOME_MARKER = "greenflag";
    public static final String DEFAULT_WARP_MARKER = "blueflag";
    public static final boolean DEFAULT_BOOST = false;
    private static final DynmapStyle defaultStyle = new DynmapStyle().setLineColor("#00FF00").setLineOpacity(0.8).setLineWeight(3).setFillColor("#00FF00").setFillOpacity(0.35).setHomeMarker("greenflag").setBoost(false);
    private static final DynmapStyle empty = new DynmapStyle();
    private String lineColor = null;
    private Double lineOpacity = null;
    private Integer lineWeight = null;
    private String fillColor = null;
    private Double fillOpacity = null;
    private String homeMarker = null;
    private String warpMarker = null;
    private Boolean boost = null;

    public static DynmapStyle getDefault() {
        return defaultStyle;
    }

    public static DynmapStyle getEmpty() {
        return empty;
    }

    private static DynmapConfig.Style styleConf() {
        return FactionsPlugin.getInstance().getConfigManager().getDynmapConfig().style();
    }

    public int getLineColor() {
        return DynmapStyle.getColor(DynmapStyle.coalesce(this.lineColor, DynmapStyle.styleConf().getLineColor(), "#00FF00"));
    }

    public DynmapStyle setLineColor(String string) {
        this.lineColor = string;
        return this;
    }

    public double getLineOpacity() {
        return DynmapStyle.coalesce(this.lineOpacity, DynmapStyle.styleConf().getLineOpacity(), 0.8);
    }

    public DynmapStyle setLineOpacity(Double d) {
        this.lineOpacity = d;
        return this;
    }

    public int getLineWeight() {
        return DynmapStyle.coalesce(this.lineWeight, DynmapStyle.styleConf().getLineWeight(), 3);
    }

    public DynmapStyle setLineWeight(Integer n) {
        this.lineWeight = n;
        return this;
    }

    public int getFillColor() {
        return DynmapStyle.getColor(DynmapStyle.coalesce(this.fillColor, DynmapStyle.styleConf().getFillColor(), "#00FF00"));
    }

    public DynmapStyle setFillColor(String string) {
        this.fillColor = string;
        return this;
    }

    public double getFillOpacity() {
        return DynmapStyle.coalesce(this.fillOpacity, DynmapStyle.styleConf().getFillOpacity(), 0.35);
    }

    public DynmapStyle setFillOpacity(Double d) {
        this.fillOpacity = d;
        return this;
    }

    public String getHomeMarker() {
        return DynmapStyle.coalesce(this.homeMarker, DynmapStyle.styleConf().getHomeMarker(), DEFAULT_HOME_MARKER);
    }

    public DynmapStyle setHomeMarker(String string) {
        this.homeMarker = string;
        return this;
    }

    public String getWarpMarker() {
        return DynmapStyle.coalesce(this.warpMarker, DynmapStyle.styleConf().getWarpMarker(), DEFAULT_WARP_MARKER);
    }

    public DynmapStyle setWarpMarker(String string) {
        this.warpMarker = string;
        return this;
    }

    public boolean getBoost() {
        return DynmapStyle.coalesce(this.boost, DynmapStyle.styleConf().isStyleBoost(), false);
    }

    public DynmapStyle setBoost(Boolean bl) {
        this.boost = bl;
        return this;
    }

    private static <T> T coalesce(T t, T t2, T t3) {
        return t != null ? t : (t2 != null ? t2 : t3);
    }

    public static int getColor(String string) {
        int n = 65280;
        try {
            n = Integer.parseInt(string.substring(1), 16);
        } catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return n;
    }
}

