package net.airgame.bukkit.api.gui.holder;

import net.airgame.bukkit.api.gui.ButtonGroup;
import net.airgame.bukkit.api.gui.PageConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

/**
 * GUI
 */
@SuppressWarnings("unused")
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
    }

    public abstract void initPage();

    public void onOpen(InventoryOpenEvent event) {
    }

    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    public void onClickInside(InventoryClickEvent event) {
    }

    public void onClose(InventoryCloseEvent event) {
    }

    public void onClickButton(int index) {
    }

    public void onClickButton(String buttonName) {
    }

    public InventoryView show() {
        initPage();
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