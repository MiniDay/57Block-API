package net.airgame.bukkit.api.gui.handler;

import net.airgame.bukkit.api.gui.ButtonGroup;
import net.airgame.bukkit.api.gui.PageConfig;
import net.airgame.bukkit.api.gui.PageElement;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
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
public abstract class PageableHandler<E extends PageElement> extends FixedPageHandler {
    private final int page;
    private HashMap<Integer, E> elementSlot;

    public PageableHandler(@NotNull PageConfig pageConfig, @NotNull HumanEntity player, int page) {
        super(pageConfig, player);
        this.page = page;
        initPage();
    }

    public abstract void showPreviewPage();

    public abstract void showNextPage();

    @NotNull
    public abstract ArrayList<E> getPageElements();

    public abstract void onClickElement(@NotNull ClickType type, @NotNull InventoryAction action, @NotNull E element);

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

    @NotNull
    public String getBarrierButtonName() {
        return "barrier";
    }

    public void initPage() {
        super.initPage();
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
            elementSlot.put(buttonIndex, element);

            ItemStack elementItem = button.clone();
            if (element.replaceItem(elementItem)) {
                inventory.setItem(buttonIndex, elementItem);
                continue;
            }

            ItemMeta meta = elementItem.getItemMeta();
            if (meta == null) {
                inventory.setItem(buttonIndex, elementItem);
                continue;
            }
            if (element.replaceMeta(meta)) {
                element.replaceMeta(meta);
                inventory.setItem(buttonIndex, elementItem);
                continue;
            }
            if (meta.hasDisplayName()) {
                meta.setDisplayName(element.replaceDisplayName(player, meta.getDisplayName()));
            }
            if (meta.hasLore()) {
                meta.setLore(element.replaceLore(player, meta.getLore()));
            }
            elementItem.setItemMeta(meta);
            inventory.setItem(buttonIndex, elementItem);
        }

        if (page == 0) {
            // 如果页面已在首页则撤掉上一页按钮
            inventory.setItem(group.getButtonIndex(getPreviewButtonName()), group.getButton(getBarrierButtonName()));
        }
        if (elements.size() <= (page + 1) * pageSize) {
            // 如果页面显示超出已有元素数量则撤掉下一页按钮
            inventory.setItem(group.getButtonIndex(getNextButtonName()), group.getButton(getBarrierButtonName()));
        }
    }

    @Override
    public void onClickInside(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        int slot = event.getSlot();
        E e = elementSlot.get(slot);
        if (e != null) {
            onClickElement(event.getClick(), event.getAction(), e);
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

    public int getPage() {
        return page;
    }

    @NotNull
    public HashMap<Integer, E> getElementSlot() {
        return elementSlot;
    }
}
