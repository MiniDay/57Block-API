package net.airgame.bukkit.api.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public abstract class PageHolder implements InventoryHolder {
    private final PageConfig pageConfig;
    private final HumanEntity player;
    private final ButtonGroup buttonGroup;
    private final Inventory inventory;

    public PageHolder(PageConfig pageConfig, HumanEntity player) {
        this.pageConfig = pageConfig;
        this.player = player;
        buttonGroup = pageConfig.getButtonGroup(player);
        inventory = Bukkit.createInventory(this, pageConfig.getInventory().getSize(), pageConfig.getTitle());
        initPage();
    }

    public abstract void initPage();

    public void onClick(InventoryClickEvent event) {
        int rawSlot = event.getRawSlot();
        if (rawSlot < 0) {
            return;
        }

    }

    public void onClickInside(int index) {

    }

    public void onClickButton(String buttonName) {
    }

    public InventoryView show() {
        return player.openInventory(getInventory());
    }

    public PageConfig getPageConfig() {
        return pageConfig;
    }

    public HumanEntity getPlayer() {
        return player;
    }

    public ButtonGroup getButtonGroup() {
        return buttonGroup;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
