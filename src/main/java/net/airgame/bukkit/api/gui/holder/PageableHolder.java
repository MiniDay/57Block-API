package net.airgame.bukkit.api.gui.holder;

import net.airgame.bukkit.api.gui.ButtonGroup;
import net.airgame.bukkit.api.gui.PageConfig;
import net.airgame.bukkit.api.gui.PageElement;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 支持翻页的 GUI
 *
 * @param <E> 页面元素
 */
@SuppressWarnings("unused")
public abstract class PageableHolder<E extends PageElement> extends PageHolder {
    private final int page;
    private HashMap<Integer, E> elementSlot;

    public PageableHolder(@NotNull PageConfig pageConfig, @NotNull HumanEntity player, int page) {
        super(pageConfig, player);
        this.page = page;
    }

    public abstract void showNextPage();

    public abstract void showPreviewPage();

    @NotNull
    public abstract ArrayList<E> getPageElements();

    public abstract void onClickElement(@NotNull InventoryClickEvent event, @NotNull E element);

    @NotNull
    public String getElementButtonName() {
        return "element";
    }

    @NotNull
    public String getPreviewButtonName() {
        return "preview";
    }

    @NotNull
    public String getNextButtonName() {
        return "next";
    }

    public void initPage() {
        ArrayList<E> elements = getPageElements();
        ButtonGroup group = getButtonGroup();
        Inventory inventory = getInventory();
        HumanEntity player = getPlayer();

        ArrayList<Integer> buttonIndexes = group.getButtonAllIndex(getElementButtonName());
        int pageSize = buttonIndexes.size(); // 一页有多少个按钮
        ItemStack button = group.getButton(getElementButtonName());
        elementSlot = new HashMap<>();
        for (int i = 0; i < pageSize; i++) {
            int elementIndex = page * pageSize + i; // 元素的索引位置
            int buttonIndex = buttonIndexes.get(i);  // 按钮在 GUI 中的索引位置

            if (elementIndex >= elements.size() || button == null) {
                inventory.setItem(buttonIndex, null);
                continue;
            }

            E element = elements.get(elementIndex);
            ItemStack elementItem = button.clone();
            ItemMeta meta = elementItem.getItemMeta();
            if (meta != null) {
                if (meta.hasDisplayName()) {
                    meta.setDisplayName(element.replaceDisplayName(player, meta.getDisplayName()));
                }
                if (meta.hasLore()) {
                    meta.setLore(element.replaceLore(player, meta.getLore()));
                }
                element.replaceMeta(meta);
                elementItem.setItemMeta(meta);
            }
            element.replaceItem(elementItem);
            inventory.setItem(buttonIndex, elementItem);
            this.elementSlot.put(buttonIndex, element);
        }

        if (page == 0) {
            // 如果页面已在首页则撤掉上一页按钮
            inventory.setItem(group.getButtonIndex(getPreviewButtonName()), null);
        }
        if (elements.size() <= (page + 1) * pageSize) {
            // 如果页面显示超出已有元素数量则撤掉下一页按钮
            inventory.setItem(group.getButtonIndex(getNextButtonName()), null);
        }
    }

    @Override
    public void onClickInside(@NotNull InventoryClickEvent event) {
        int slot = event.getSlot();
        E e = elementSlot.get(slot);
        if (e != null) {
            onClickElement(event, e);
            return;
        }
        String name = getPageConfig().getButtonName(event.getCurrentItem());
        if (name.equalsIgnoreCase(getNextButtonName())) {
            showNextPage();
            return;
        }
        if (name.equalsIgnoreCase(getPreviewButtonName())) {
            showPreviewPage();
        }
    }

    @Override
    public void onClickButton(int index) {
    }

    @NotNull
    public HashMap<Integer, E> getElementSlot() {
        return elementSlot;
    }
}
