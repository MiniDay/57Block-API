package net.airgame.bukkit.api.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class FixedPage extends PageHolder {
    public FixedPage(PageConfig config, HumanEntity player) {
        super(config, player);
    }

    @Override
    public void initPage() {
        HumanEntity player = getPlayer();

        Inventory inventory = getInventory();
        PageConfig config = getPageConfig();
        ButtonGroup group = config.getButtonGroup(player);

        List<String> graphic = config.getGraphic();
        for (int i = 0; i < graphic.size(); i++) {
            String s = graphic.get(i);
            char[] chars = s.toCharArray();
            for (int j = 0; j < chars.length; j++) {
                char c = chars[j];
                int index = i * 9 + j;
                inventory.setItem(index, group.getButton(c));
            }
        }
    }
}
