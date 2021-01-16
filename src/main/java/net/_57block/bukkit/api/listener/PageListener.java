package net._57block.bukkit.api.listener;

import net._57block.bukkit.api.gui.PageHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class PageListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(inventory.getHolder() instanceof PageHandler)) {
            return;
        }
        PageHandler handler = (PageHandler) inventory.getHolder();
        handler.onClick(event);
    }
}
