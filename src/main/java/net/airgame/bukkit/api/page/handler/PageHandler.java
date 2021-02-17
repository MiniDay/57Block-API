package net.airgame.bukkit.api.page.handler;

import net.airgame.bukkit.api.AirGameAPI;
import net.airgame.bukkit.api.manager.PageConfigManager;
import net.airgame.bukkit.api.page.ButtonGroup;
import net.airgame.bukkit.api.page.PageConfig;
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
public abstract class PageHandler implements InventoryHolder {
    private final PageConfig pageConfig;
    private final HumanEntity player;
    private final Inventory inventory;

    public PageHandler(@NotNull HumanEntity player) {
        pageConfig = PageConfigManager.getPageConfig(getClass());
        if (pageConfig == null) {
            throw new IllegalArgumentException("未注册的界面设定!");
        }
        this.player = player;
        inventory = Bukkit.createInventory(this, pageConfig.getInventory().getSize(), pageConfig.getTitle());
        if (autoInit()) {
            initPage();
        }
    }

    public PageHandler(@NotNull PageConfig pageConfig, @NotNull HumanEntity player) {
        this.pageConfig = pageConfig;
        this.player = player;
        inventory = Bukkit.createInventory(this, pageConfig.getInventory().getSize(), pageConfig.getTitle());
        if (autoInit()) {
            initPage();
        }
    }

    /**
     * 是否在实例化时就自动构建页面
     *
     * @return true 代表在实例化时就自动构建页面
     */
    public boolean autoInit() {
        return true;
    }

    public abstract void initPage();

    public void onOpen(@NotNull InventoryOpenEvent event) {
    }

    public void onClick(@NotNull InventoryClickEvent event) {
    }

    public void onClickInside(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
    }

    public void onClickInside(@NotNull ClickType clickType, @NotNull InventoryAction action, int index) {
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
