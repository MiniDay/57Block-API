package net.airgame.bukkit.api.gui.handler;

import net.airgame.bukkit.api.gui.ButtonGroup;
import net.airgame.bukkit.api.gui.PageConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

/**
 * GUI
 */
@SuppressWarnings("unused")
public abstract class PageHandler implements InventoryHolder {
    private final PageConfig pageConfig;
    private final HumanEntity player;
    private final ButtonGroup buttonGroup;
    private final Inventory inventory;

    public PageHandler(@NotNull PageConfig pageConfig, @NotNull HumanEntity player) {
        this.pageConfig = pageConfig;
        this.player = player;
        buttonGroup = pageConfig.getButtonGroup(player);
        inventory = Bukkit.createInventory(this, pageConfig.getInventory().getSize(), pageConfig.getTitle());
    }

    public abstract void initPage();

    public void onOpen(@NotNull InventoryOpenEvent event) {
    }

    public void onClickButton(int index) {
    }

    public void onClick(@NotNull InventoryClickEvent event) {
    }

    public void onClickInside(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
    }

    public void onDrag(@NotNull InventoryDragEvent event) {
    }

    public void onDragInside(@NotNull InventoryDragEvent event) {
        event.setCancelled(true);
    }

    public void onClose(@NotNull InventoryCloseEvent event) {
    }

    public InventoryView show() {
        return player.openInventory(getInventory());
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
        return buttonGroup;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
