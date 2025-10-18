/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 */
package com.massivecraft.factions.gui;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.gui.SimpleItem;
import com.massivecraft.factions.tag.Tag;
import com.massivecraft.factions.util.TextUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class GUI<Type>
implements InventoryHolder {
    protected Inventory inventory;
    protected final int size;
    protected int back = -1;
    private Map<Integer, Type> slotMap = new HashMap<Integer, Type>();
    protected final FPlayer user;

    public GUI(FPlayer fPlayer, int n) {
        this.size = n * 9;
        this.user = fPlayer;
    }

    protected abstract String getName();

    protected abstract String parse(String var1, Type var2);

    protected abstract void onClick(Type var1, ClickType var2);

    public void click(int n, ClickType clickType) {
        if (this.slotMap.containsKey(n)) {
            this.onClick(this.slotMap.get(n), clickType);
        } else if (this instanceof Backable && this.back == n) {
            ((Backable)((Object)this)).onBack();
        }
    }

    protected abstract Map<Integer, Type> createSlotMap();

    public void build() {
        String string = this.getName();
        this.inventory = Bukkit.createInventory((InventoryHolder)this, (int)this.size, (String)string);
        this.slotMap = this.createSlotMap();
        this.buildDummyItems();
        this.buildItems();
    }

    protected abstract SimpleItem getItem(Type var1);

    protected void buildItems() {
        for (Map.Entry<Integer, Type> entry : this.slotMap.entrySet()) {
            this.setItemAtSlot(entry.getKey(), entry.getValue());
        }
    }

    protected void buildItem(Type Type2) {
        for (Map.Entry<Integer, Type> entry : this.slotMap.entrySet()) {
            if (!entry.getValue().equals(Type2)) continue;
            this.setItemAtSlot(entry.getKey(), entry.getValue());
        }
    }

    private void setItemAtSlot(int n, Type Type2) {
        SimpleItem simpleItem = this.getItem(Type2);
        this.parse(simpleItem, Type2);
        this.inventory.setItem(n, simpleItem.get());
    }

    protected abstract Map<Integer, SimpleItem> createDummyItems();

    protected void buildDummyItems() {
        for (Map.Entry<Integer, SimpleItem> entry : this.createDummyItems().entrySet()) {
            SimpleItem simpleItem = entry.getValue();
            this.parse(simpleItem, null);
            this.inventory.setItem(entry.getKey().intValue(), simpleItem.get());
        }
    }

    protected void parse(SimpleItem simpleItem, Type Type2) {
        if (Type2 != null) {
            simpleItem.setName(this.parse(simpleItem.getName(), Type2));
        }
        simpleItem.setName(this.parseDefault(simpleItem.getName()));
        if (simpleItem.getLore() != null) {
            simpleItem.setLore(this.parseList(simpleItem.getLore(), Type2));
        }
    }

    protected List<String> parseList(List<String> list, Type Type2) {
        ArrayList<String> arrayList = new ArrayList<String>();
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String string;
            String string2 = string = iterator.next();
            if (Type2 != null) {
                string2 = this.parse(string2, Type2);
            }
            string2 = this.parseDefault(string2);
            arrayList.add(string2);
        }
        return arrayList;
    }

    protected String parseDefault(String string) {
        string = TextUtil.parseColor(string);
        string = Tag.parsePlain(this.user, string);
        string = Tag.parsePlain(this.user.getFaction(), string);
        string = Tag.parsePlaceholders(this.user.getPlayer(), string);
        return string;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void open() {
        this.user.getPlayer().openInventory(this.getInventory());
    }

    public static interface Backable {
        public void onBack();
    }
}

