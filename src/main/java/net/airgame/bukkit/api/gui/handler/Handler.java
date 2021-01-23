package net.airgame.bukkit.api.gui.handler;

import net.airgame.bukkit.api.AirGameAPI;
import net.airgame.bukkit.api.gui.ButtonGroup;
import net.airgame.bukkit.api.gui.PageConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * GUI 处理类
 */
@SuppressWarnings("unused")
public abstract class Handler implements InventoryHolder {
    private final PageConfig pageConfig;
    private final HumanEntity player;
    private final Inventory inventory;

    public Handler(@NotNull PageConfig pageConfig, @NotNull HumanEntity player) {
        this.pageConfig = pageConfig;
        this.player = player;
        inventory = Bukkit.createInventory(this, pageConfig.getInventory().getSize(), pageConfig.getTitle());
    }

    public abstract void initPage();

    public void onOpen(@NotNull InventoryOpenEvent event) {
    }

    public void onClick(@NotNull InventoryClickEvent event) {
    }

    public void onClickInside(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
    }

    public void onClickButton(@NotNull ClickType clickType, @NotNull InventoryAction action, int index) {
    }

    public void onDrag(@NotNull InventoryDragEvent event) {
    }

    public void onDragInside(@NotNull InventoryDragEvent event) {
        event.setCancelled(true);
    }

    public void onClose(@NotNull InventoryCloseEvent event) {
    }

    public void show() {
        AirGameAPI.sync(() -> player.openInventory(getInventory()));
    }

    @NotNull
    public PageConfig getPageConfig() {
        return pageConfig;
    }

    @NotNull
    public HumanEntity getPlayer() {
        return player;
    }

    @NotNull
    public ButtonGroup getButtonGroup() {
        return getPageConfig().getButtonGroup("default");
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
