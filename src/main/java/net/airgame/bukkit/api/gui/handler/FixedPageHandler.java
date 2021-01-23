package net.airgame.bukkit.api.gui.handler;

import net.airgame.bukkit.api.gui.ButtonGroup;
import net.airgame.bukkit.api.gui.PageConfig;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * 固定页面的 GUI
 */
@SuppressWarnings("unused")
public class FixedPageHandler extends Handler {
    public FixedPageHandler(PageConfig config, HumanEntity player) {
        super(config, player);
        initPage();
    }

    @Override
    public void initPage() {
        HumanEntity player = getPlayer();

        Inventory inventory = getInventory();
        PageConfig config = getPageConfig();
        ButtonGroup group = getButtonGroup();

        List<String> graphic = config.getGraphic();
        for (int i = 0; i < graphic.size(); i++) {
            char[] chars = graphic.get(i).toCharArray();
            for (int j = 0; j < chars.length; j++) {
                char c = chars[j];
                int index = i * 9 + j;
                inventory.setItem(index, group.getButton(c));
            }
        }
    }
}
