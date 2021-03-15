package net.airgame.bukkit.api.listener;

import net.airgame.bukkit.api.AirGamePlugin;
import net.airgame.bukkit.api.page.handler.PageHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

/**
 * GUI 监听器
 */
public class PageListener implements Listener {
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(inventory.getHolder() instanceof PageHandler)) {
            return;
        }
        PageHandler pageHandler = (PageHandler) inventory.getHolder();
        pageHandler.onOpen(event);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(inventory.getHolder() instanceof PageHandler)) {
            return;
        }
        PageHandler pageHandler = (PageHandler) inventory.getHolder();
        try {
            pageHandler.onClick(event);
        } catch (Exception e) {
            AirGamePlugin.getLogUtils().error(e, "执行 %s 的 onClick(event) 时遇到了一个异常: ", pageHandler.getClass().getName());
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
            pageHandler.onClickInside(event);
        } catch (Exception e) {
            AirGamePlugin.getLogUtils().error(e, "执行 %s 的 onClickInside(event) 时遇到了一个异常: ", pageHandler.getClass().getName());
        }
        try {
            pageHandler.onClickInside(event.getClick(), event.getAction(), index);
        } catch (Exception e) {
            AirGamePlugin.getLogUtils().error(e,
                    "执行 %s 的 onClickInside(%s, %s, %d) 时遇到了一个异常: ",
                    pageHandler.getClass().getName(),
                    event.getClick().name(),
                    event.getAction().name(),
                    index
            );
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(inventory.getHolder() instanceof PageHandler)) {
            return;
        }
        PageHandler pageHandler = (PageHandler) inventory.getHolder();
        try {
            pageHandler.onDrag(event);
        } catch (Exception e) {
            AirGamePlugin.getLogUtils().error(e, "执行 %s 的 onDrag(event) 时遇到了一个异常: ", pageHandler.getClass().getName());
        }
        if (event.isCancelled()) {
            return;
        }
        int size = inventory.getSize();
        for (Integer slot : event.getRawSlots()) {
            if (slot < size) {
                pageHandler.onDragInside(event);
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(inventory.getHolder() instanceof PageHandler)) {
            return;
        }
        PageHandler pageHandler = (PageHandler) inventory.getHolder();
        pageHandler.onClose(event);
    }
}
