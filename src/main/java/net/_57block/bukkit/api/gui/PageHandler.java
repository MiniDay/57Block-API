package net._57block.bukkit.api.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class PageHandler implements InventoryHolder {
    private final PageConfig config;
    private final HumanEntity player;
    private final Inventory inventory;

    public PageHandler(PageConfig config, HumanEntity player) {
        this.config = config;
        this.player = player;
        inventory = Bukkit.createInventory(this, config.getInventory().getSize(), config.getTitle());
    }

    public void onClick(InventoryClickEvent event) {
        int rawSlot = event.getRawSlot();
        if (rawSlot < 0) {
            onClickOutside(event);
        } else {
            onClickInside(event);
        }
    }

    public void onClickInside(InventoryClickEvent event) {
    }

    public void onClickOutside(InventoryClickEvent event) {
    }

    public InventoryView show() {
        return player.openInventory(getInventory());
    }

    public PageConfig getConfig() {
        return config;
    }

    public HumanEntity getPlayer() {
        return player;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
