package net.airgame.bukkit.api.gui;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SuppressWarnings("unused")
public class ButtonGroup {
    private final String name;
    private final PageConfig config;
    private final HashMap<Character, String> buttonNameMap;

    /**
     * 实例化这个按钮组
     *
     * @param pageConfig Page 设定
     * @param config     按钮组设定
     */
    public ButtonGroup(PageConfig pageConfig, ConfigurationSection config) {
        this.config = pageConfig;
        name = config.getName();
        buttonNameMap = new HashMap<>();
        for (String key : config.getKeys(false)) {
            buttonNameMap.put(key.charAt(0), config.getString(key));
        }
    }

    /**
     * 以图形字符来获取按钮名称
     *
     * @param graphicKey 图形字符
     * @return 按钮名称
     */
    public String getButtonName(Character graphicKey) {
        return buttonNameMap.get(graphicKey);
    }

    /**
     * 以索引位置来获取按钮名称
     *
     * @param index 索引位置
     * @return 按钮名称
     */
    public String getButtonName(int index) {
        return buttonNameMap.get(config.getButtonKey(index));
    }

    /**
     * 以按钮名称来获取图形字符
     *
     * @param buttonName 按钮名称
     * @return 图形中的字符
     */
    public Character getGraphicKey(String buttonName) {
        for (Map.Entry<Character, String> entry : buttonNameMap.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(buttonName)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 以图形字符来获取按钮物品
     *
     * @param graphicKey 图形字符
     * @return 按钮物品
     */
    public ItemStack getButton(Character graphicKey) {
        return getButton(getButtonName(graphicKey));
    }

    /**
     * 以按钮名称来获取按钮物品
     *
     * @param buttonName 按钮名称
     * @return 按钮物品
     */
    public ItemStack getButton(String buttonName) {
        ItemStack stack = config.getButtonMap().get(buttonName);
        if (stack != null) {
            stack = stack.clone();
        }
        return stack;
    }

    public int getButtonIndex(String buttonName) {
        Character graphicKey = getGraphicKey(buttonName);
        List<String> graphic = config.getGraphic();
        for (int i = 0; i < graphic.size(); i++) {
            char[] chars = graphic.get(i).toCharArray();
            for (int j = 0; j < chars.length; j++) {
                if (chars[j] == graphicKey) {
                    return i * 9 + j;
                }
            }
        }
        return -1;
    }

    /**
     * 获得按钮在GUI中全部的索引位置
     *
     * @param buttonName 按钮名称
     * @return 索引位置
     */
    public ArrayList<Integer> getButtonAllIndex(String buttonName) {
        Character graphicKey = getGraphicKey(buttonName);
        List<String> graphic = config.getGraphic();
        ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < graphic.size(); i++) {
            char[] chars = graphic.get(i).toCharArray();
            for (int j = 0; j < chars.length; j++) {
                if (chars[j] == graphicKey) {
                    integers.add(i * 9 + j);
                }
            }
        }
        return integers;
    }

    /**
     * 获取这个按钮组的名称
     *
     * @return 按钮组名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取把图形字符映射到按钮名称的表
     *
     * @return 把图形字符映射到按钮名称的表
     */
    public HashMap<Character, String> getButtonNameMap() {
        return buttonNameMap;
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
