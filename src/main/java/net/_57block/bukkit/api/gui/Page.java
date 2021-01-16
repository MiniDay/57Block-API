package net._57block.bukkit.api.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;

/**
 * 代表一个箱子 GUI 的页面
 */
public interface Page extends InventoryHolder {

    void onClickPage(InventoryClickEvent event);

    void onClickInside(InventoryClickEvent event);

    void onClickOutside(InventoryClickEvent event);

    void update();

    default InventoryView open(HumanEntity player) {
        return player.openInventory(getInventory());
    }
}
