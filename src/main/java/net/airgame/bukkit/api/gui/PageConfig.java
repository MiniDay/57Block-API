package net.airgame.bukkit.api.gui;

import net.airgame.bukkit.api.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PageConfig implements InventoryHolder {
    private final ConfigurationSection config;

    private final String title;
    private final List<String> graphic;
    private final Inventory inventory;

    private final HashMap<String, ItemStack> buttonMap;
    private final ArrayList<ButtonGroup> buttonGroups;

    @SuppressWarnings("ConstantConditions")
    public PageConfig(@NotNull ConfigurationSection config) {
        this.config = config;
        title = StringUtils.replaceColorCode(config.getString("title"), "");

        List<String> graphic = config.getStringList("graphic");
        if (graphic.size() > 6) {
            graphic = graphic.subList(0, 6);
        }
        for (int i = 0; i < graphic.size(); i++) {
            String s = graphic.get(i);
            if (s.length() > 9) {
                s = s.substring(0, 9);
            }
            graphic.set(i, s);
        }
        this.graphic = graphic;

        buttonMap = new HashMap<>();
        ConfigurationSection buttonsConfig = config.getConfigurationSection("buttons");
        for (String key : buttonsConfig.getKeys(false)) {
            buttonMap.put(key, buttonsConfig.getItemStack("buttons"));
        }

        buttonGroups = new ArrayList<>();
        ConfigurationSection buttonGroupsConfig = config.getConfigurationSection("buttonGroups");
        for (String key : buttonGroupsConfig.getKeys(false)) {
            buttonGroups.add(
                    new ButtonGroup(this, buttonGroupsConfig.getConfigurationSection(key))
            );
        }

        inventory = Bukkit.createInventory(this, graphic.size() * 9, title);

        ButtonGroup group = getButtonGroup("default");
        for (int i = 0; i < graphic.size(); i++) {
            String s = graphic.get(i);
            for (int j = 0; j < Math.min(s.length(), 9); j++) {
                int index = i * 9 + j;
                inventory.setItem(index, group.getButton(getButtonKey(index)));
            }
        }
    }

    /**
     * 获取把 buttonName 映射到 展示物品 的表
     *
     * @return 把 buttonName 映射到 展示物品 的表
     */
    public HashMap<String, ItemStack> getButtonMap() {
        return buttonMap;
    }

    /**
     * 获取 graphicKey
     *
     * @param index 索引
     * @return 若超出 graphic 范围则返回 null
     */
    public Character getButtonKey(int index) {
        if (index < 0) return null;
        if (index / 9 >= graphic.size()) return null;
        String s = graphic.get(index / 9);
        return s.charAt(index % 9);
    }

    @NotNull
    public ButtonGroup getButtonGroup(@NotNull HumanEntity player) {
        return getButtonGroup("default");
    }

    @NotNull
    public ButtonGroup getButtonGroup(@NotNull String groupName) {
        for (ButtonGroup group : buttonGroups) {
            if (group.getName().equalsIgnoreCase(groupName)) {
                return group;
            }
        }
        return buttonGroups.get(0);
    }

    @NotNull
    public ConfigurationSection getConfig() {
        return config;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @NotNull
    public List<String> getGraphic() {
        return graphic;
    }

    @NotNull
    public ArrayList<ButtonGroup> getButtonGroups() {
        return buttonGroups;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

}
