package net.airgame.bukkit.api.page.handler;

import net.airgame.bukkit.api.page.ButtonGroup;
import net.airgame.bukkit.api.page.PageConfig;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 固定页面的 GUI
 */
@SuppressWarnings("unused")
public class FixedPageHandler extends PageHandler {
    public FixedPageHandler(@NotNull HumanEntity player) {
        super(player);
    }

    public FixedPageHandler(@NotNull HumanEntity player, @NotNull String title) {
        super(player, title);
    }

    public FixedPageHandler(@NotNull PageConfig pageConfig, @NotNull HumanEntity player) {
        super(pageConfig, player);
    }

    public FixedPageHandler(@NotNull PageConfig pageConfig, @NotNull String title, @NotNull HumanEntity player) {
        super(pageConfig, title, player);
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
