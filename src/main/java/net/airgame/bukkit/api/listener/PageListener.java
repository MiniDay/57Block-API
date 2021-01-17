package net.airgame.bukkit.api.listener;

import net.airgame.bukkit.api.gui.PageHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class PageListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(inventory.getHolder() instanceof PageHolder)) {
            return;
        }
        PageHolder handler = (PageHolder) inventory.getHolder();
        handler.onClick(event);
    }
}
