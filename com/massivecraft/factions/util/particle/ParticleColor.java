/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Color
 */
package com.massivecraft.factions.util.particle;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public class ParticleColor {
    private final Color color;
    private final float red;
    private final float green;
    private final float blue;

    ParticleColor(Color color) {
        this.color = color;
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
    }

    public float getOffsetX() {
        if (this.red == 0.0f) {
            return Float.MIN_VALUE;
        }
        return this.red / 255.0f;
    }

    public float getOffsetY() {
        return this.green / 255.0f;
    }

    public float getOffsetZ() {
        return this.blue / 255.0f;
    }

    public static ParticleColor fromChatColor(ChatColor chatColor) {
        return switch (chatColor) {
            case ChatColor.AQUA -> new ParticleColor(Color.AQUA);
            case ChatColor.BLACK -> new ParticleColor(Color.BLACK);
            case ChatColor.BLUE, ChatColor.DARK_AQUA, ChatColor.DARK_BLUE -> new ParticleColor(Color.BLUE);
            case ChatColor.DARK_GRAY, ChatColor.GRAY -> new ParticleColor(Color.GRAY);
            case ChatColor.DARK_GREEN -> new ParticleColor(Color.GREEN);
            case ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE -> new ParticleColor(Color.PURPLE);
            case ChatColor.DARK_RED, ChatColor.RED -> new ParticleColor(Color.RED);
            case ChatColor.GOLD, ChatColor.YELLOW -> new ParticleColor(Color.YELLOW);
            case ChatColor.GREEN -> new ParticleColor(Color.LIME);
            case ChatColor.WHITE -> new ParticleColor(Color.WHITE);
            default -> null;
        };
    }

    public Color getColor() {
        return this.color;
    }
}

