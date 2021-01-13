package net._57block.bukkit.api.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public interface Page extends InventoryHolder {

    void onClickInside(InventoryClickEvent event);

    void onClickOutside(InventoryClickEvent event);
}
