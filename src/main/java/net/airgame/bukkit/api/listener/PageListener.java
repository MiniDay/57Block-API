package net.airgame.bukkit.api.listener;

import net.airgame.bukkit.api.PluginMain;
import net.airgame.bukkit.api.gui.handler.PageHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public class PageListener implements Listener {
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(inventory.getHolder() instanceof PageHandler)) {
            return;
        }
        PageHandler handler = (PageHandler) inventory.getHolder();
        handler.onOpen(event);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(inventory.getHolder() instanceof PageHandler)) {
            return;
        }
        PageHandler handler = (PageHandler) inventory.getHolder();
        try {
            handler.onClick(event);
        } catch (Exception e) {
            PluginMain.getLogUtils().error(e, "执行 %s 的 onClick(event) 时遇到了一个异常: ", handler.getClass().getName());
        }
        if (event.isCancelled()) {
            return;
        }
        int index = event.getRawSlot();
        if (index < 0) {
            return;
        }
        if (index != event.getSlot()) {
            return;
        }
        try {
            handler.onClickButton(index);
        } catch (Exception e) {
            PluginMain.getLogUtils().error(e, "执行 %s 的 onClickButton(%d) 时遇到了一个异常: ", handler.getClass().getName(), index);
        }
        try {
            handler.onClickInside(event);
        } catch (Exception e) {
            PluginMain.getLogUtils().error(e, "执行 %s 的 onClickInside(event) 时遇到了一个异常: ", handler.getClass().getName());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(inventory.getHolder() instanceof PageHandler)) {
            return;
        }
        PageHandler handler = (PageHandler) inventory.getHolder();
        try {
            handler.onDrag(event);
        } catch (Exception e) {
            PluginMain.getLogUtils().error(e, "执行 %s 的 onDrag(event) 时遇到了一个异常: ", handler.getClass().getName());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(inventory.getHolder() instanceof PageHandler)) {
            return;
        }
        PageHandler handler = (PageHandler) inventory.getHolder();
        handler.onClose(event);
    }
}
