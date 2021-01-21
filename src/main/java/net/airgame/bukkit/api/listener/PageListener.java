package net.airgame.bukkit.api.listener;

import net.airgame.bukkit.api.gui.holder.PageHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public class PageListener implements Listener {
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(inventory.getHolder() instanceof PageHolder)) {
            return;
        }
        PageHolder handler = (PageHolder) inventory.getHolder();
        handler.onOpen(event);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(inventory.getHolder() instanceof PageHolder)) {
            return;
        }
        PageHolder handler = (PageHolder) inventory.getHolder();
        handler.onClick(event);
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
        handler.onClickButton(index);
        handler.onClickInside(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(inventory.getHolder() instanceof PageHolder)) {
            return;
        }
        PageHolder handler = (PageHolder) inventory.getHolder();
        handler.onClose(event);
    }
}
