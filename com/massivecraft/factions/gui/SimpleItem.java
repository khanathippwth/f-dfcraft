/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package com.massivecraft.factions.gui;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SimpleItem {
    private String name;
    private List<String> lore;
    private Material material;
    private boolean enchant;

    SimpleItem(Builder builder) {
        this.name = builder.name;
        this.lore = builder.lore;
        this.material = builder.material;
    }

    public SimpleItem(SimpleItem simpleItem) {
        this.name = simpleItem.name;
        this.lore = simpleItem.lore;
        this.material = simpleItem.material;
        this.enchant = simpleItem.enchant;
    }

    public ItemStack get() {
        if (this.isValid()) {
            ItemStack itemStack = new ItemStack(this.material);
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (this.name != null) {
                itemMeta.setDisplayName(this.name);
            }
            itemMeta.setLore(this.lore);
            itemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES});
            if (this.enchant) {
                itemMeta.addEnchant(Enchantment.LUCK, 1, true);
            }
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        return new ItemStack(Material.AIR);
    }

    public void merge(SimpleItem simpleItem) {
        if (simpleItem.material != null) {
            this.material = simpleItem.material;
        }
        if (simpleItem.name != null) {
            this.name = simpleItem.name;
        }
        if (!simpleItem.lore.isEmpty()) {
            this.lore = simpleItem.lore;
        }
    }

    public boolean isValid() {
        return this.material != null;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String string) {
        this.name = string;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public void setLore(List<String> list) {
        this.lore = list;
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public boolean isEnchant() {
        return this.enchant;
    }

    public void setEnchant(boolean bl) {
        this.enchant = bl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Material material;
        private String name;
        private List<String> lore;

        private Builder() {
        }

        public Builder setLore(List<String> list) {
            this.lore = list;
            return this;
        }

        public Builder setName(String string) {
            this.name = string;
            return this;
        }

        public Builder setMaterial(Material material) {
            this.material = material;
            return this;
        }

        public SimpleItem build() {
            return new SimpleItem(this);
        }
    }
}

