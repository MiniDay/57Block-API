package net._57block.bukkit.api.gui;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;

public class ButtonGroup {
    private final String name;
    private final HashMap<Character, String> buttonNameMap;
    private final HashMap<String, ItemStack> buttonMap;

    public ButtonGroup(ConfigurationSection config, HashMap<String, ItemStack> buttons) {
        this.buttonMap = buttons;
        name = config.getName();
        buttonNameMap = new HashMap<>();
        for (String key : config.getKeys(false)) {
            buttonNameMap.put(key.charAt(0), config.getString(key));
        }
    }

    /**
     * 获取把 graphicKey 映射到 buttonName 的表
     *
     * @return 把 graphicKey 映射到 buttonName 的表
     */
    public HashMap<Character, String> getButtonNameMap() {
        return buttonNameMap;
    }

    public String getName() {
        return name;
    }

    public String getButtonName(Character graphicKey) {
        return buttonNameMap.get(graphicKey);
    }

    public ItemStack getButton(Character graphicKey) {
        return getButton(getButtonName(graphicKey));
    }

    public ItemStack getButton(String buttonName) {
        return buttonMap.get(buttonName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ButtonGroup)) return false;
        ButtonGroup that = (ButtonGroup) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
