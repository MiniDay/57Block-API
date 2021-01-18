package net.airgame.bukkit.api.data;

import me.clip.placeholderapi.PlaceholderAPI;
import net.airgame.bukkit.api.PluginMain;
import net.airgame.bukkit.api.thread.CountdownRunnable;
import net.airgame.bukkit.api.util.MessageUtils;
import net.airgame.bukkit.api.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 方便实用的消息类
 */
@SerializableAs("DisplayMessage")
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class DisplayMessage implements ConfigurationSerializable {
    private final String message;
    private final List<String> messageList;

    private final String actionBar;

    private final String title;
    private final String subTitle;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    private final Sound sound;
    private final int volume;
    private final int pitch;

    private final String bossBarTitle;
    private final BarColor barColor;
    private final BarStyle barStyle;
    private final int bossBarTime;

    /**
     * 从 Configuration 中解码一条消息
     *
     * @param map Configuration 的 map 对象
     */
    @SuppressWarnings("unchecked")
    public DisplayMessage(@NotNull Map<String, Object> map) {
        message = StringUtils.replaceColorCode((String) map.getOrDefault("message", null));
        messageList = StringUtils.replaceColorCode((List<String>) map.getOrDefault("messageList", null));
        actionBar = StringUtils.replaceColorCode((String) map.getOrDefault("actionBar", null));

        title = StringUtils.replaceColorCode((String) map.getOrDefault("title", null));
        subTitle = StringUtils.replaceColorCode((String) map.getOrDefault("subTitle", null));
        fadeIn = (int) map.getOrDefault("fadeIn", 20);
        stay = (int) map.getOrDefault("stay", 60);
        fadeOut = (int) map.getOrDefault("fadeOut", 20);

        String s = (String) map.getOrDefault("sound", null);
        if (s != null) {
            sound = Sound.valueOf(s);
            volume = (int) map.getOrDefault("volume", 1);
            pitch = (int) map.getOrDefault("pitch", 1);
        } else {
            sound = null;
            volume = 0;
            pitch = 0;
        }

        bossBarTitle = (String) map.get("bossBarTitle");

        BarColor color;
        try {
            color = BarColor.valueOf((String) map.getOrDefault("barColor", "BLUE"));
        } catch (IllegalArgumentException e) {
            PluginMain.getLogUtils().error(e, "加载消息设定的 barColor 时出现了一个错误: ");
            color = BarColor.BLUE;
        }
        barColor = color;

        BarStyle style;
        try {
            style = BarStyle.valueOf((String) map.getOrDefault("barStyle", "SEGMENTED_20"));
        } catch (IllegalArgumentException e) {
            PluginMain.getLogUtils().error(e, "加载消息设定的 barStyle 时出现了一个错误: ");
            style = BarStyle.SEGMENTED_20;
        }
        barStyle = style;

        bossBarTime = (int) map.getOrDefault("bossBarTime", -1);
    }

    /**
     * 展示这条消息
     *
     * @param sender  展示对象
     * @param replace 替换文本信息
     */
    public void show(@NotNull CommandSender sender, @Nullable Map<String, String> replace) {
        if (message != null) {
            String sendMessage = message;
            if (replace != null) {
                for (String key : replace.keySet()) {
                    sendMessage = sendMessage.replace(key, replace.get(key));
                }
            }
            sender.sendMessage(sendMessage);
        }
        if (messageList != null) {
            for (String sendMessage : messageList) {
                if (replace != null) {
                    for (String key : replace.keySet()) {
                        sendMessage = sendMessage.replace(key, replace.get(key));
                    }
                }
                sender.sendMessage(sendMessage);
            }
        }
    }

    /**
     * 展示这条消息给玩家
     *
     * @param player  玩家对象
     * @param replace 替换文本信息
     * @return 这个 show 所创建的 BossBar，如果没有设置 BossBar 消息则返回 null
     */
    public BossBar show(@NotNull Player player, @Nullable Map<String, String> replace) {
        String s1, s2;
        if (message != null) {
            s1 = message;
            if (replace != null) {
                for (String key : replace.keySet()) {
                    s1 = s1.replace(key, replace.get(key));
                }
            }
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                s1 = PlaceholderAPI.setPlaceholders(player, s1);
            }
            player.sendMessage(s1);
        }

        if (messageList != null) {
            for (String sendMessage : messageList) {
                if (replace != null) {
                    for (String key : replace.keySet()) {
                        sendMessage = sendMessage.replace(key, replace.get(key));
                    }
                }
                if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                    sendMessage = PlaceholderAPI.setPlaceholders(player, sendMessage);
                }
                player.sendMessage(sendMessage);
            }
        }

        if (actionBar != null) {
            s1 = actionBar;
            if (replace != null) {
                for (String key : replace.keySet()) {
                    s1 = s1.replace(key, replace.get(key));
                }
            }
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                s1 = PlaceholderAPI.setPlaceholders(player, s1);
            }
            MessageUtils.sendActionBar(player, s1);
        }

        if (title != null || subTitle != null) {
            s1 = title == null ? "" : title;
            s2 = subTitle == null ? "" : subTitle;
            if (replace != null) {
                for (String key : replace.keySet()) {
                    s1 = s1.replace(key, replace.get(key));
                    s2 = s2.replace(key, replace.get(key));
                }
            }
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                s1 = PlaceholderAPI.setPlaceholders(player, s1);
                s2 = PlaceholderAPI.setPlaceholders(player, s2);
            }
            player.sendTitle(s1, s2, fadeIn, stay, fadeOut);
        }

        if (sound != null) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }

        if (bossBarTitle != null) {
            s1 = bossBarTitle;
            if (replace != null) {
                for (String key : replace.keySet()) {
                    s1 = s1.replace(key, replace.get(key));
                }
            }
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                s1 = PlaceholderAPI.setPlaceholders(player, s1);
            }
            BossBar bossBar = Bukkit.createBossBar(s1, barColor, barStyle);
            bossBar.addPlayer(player);

            if (bossBarTime > 0) {
                new CountdownRunnable(bossBarTime) {
                    @Override
                    public void onTick(int tick) {
                        String s = bossBarTitle;
                        if (replace != null) {
                            for (String key : replace.keySet()) {
                                s = s.replace(key, replace.get(key));
                            }
                        }

                        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                            s = PlaceholderAPI.setPlaceholders(player, s);
                        }
                        bossBar.setTitle(s);

                        double v = (double) tick / bossBarTime;
                        bossBar.setProgress(v);
                    }

                    @Override
                    public void onFinish() {
                        bossBar.removeAll();
                    }
                }.start(PluginMain.getInstance(), 1, true);
            }
            return bossBar;
        }
        return null;
    }

    /**
     * 展示这条消息给一些玩家
     *
     * @param players 玩家集合
     * @param replace 替换文本信息
     * @return 这个 show 所创建的 BossBar，如果没有设置 BossBar 消息则返回 null
     */
    public ArrayList<BossBar> show(@NotNull List<Player> players, @Nullable Map<String, String> replace) {
        ArrayList<BossBar> bars = new ArrayList<>();
        for (Player player : players) {
            bars.add(show(player, replace));
        }
        return bars;
    }

    /**
     * 广播这条消息
     */
    public ArrayList<BossBar> broadcast() {
        return broadcast(null);
    }

    /**
     * 广播这条消息
     *
     * @param replace 替换文本信息
     * @return 这个 broadcast 所创建的 BossBar，如果没有设置 BossBar 消息则返回 null
     */
    public ArrayList<BossBar> broadcast(@Nullable Map<String, String> replace) {
        ArrayList<BossBar> bars = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            BossBar bar = show(player, replace);
            if (bar == null) {
                continue;
            }
            bars.add(bar);
        }
        show(Bukkit.getConsoleSender(), replace);
        return bars;
    }

    /**
     * @return 这条消息的可替换文本内容对象
     */
    public ReplaceMessage createReplaceMessage() {
        return new ReplaceMessage();
    }

    public String getMessage() {
        return message;
    }

    public List<String> getMessageList() {
        return messageList;
    }

    public String getActionBar() {
        return actionBar;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public int getStay() {
        return stay;
    }

    public int getFadeOut() {
        return fadeOut;
    }

    public Sound getSound() {
        return sound;
    }

    public int getVolume() {
        return volume;
    }

    public int getPitch() {
        return pitch;
    }

    public String getBossBarTitle() {
        return bossBarTitle;
    }

    public BarColor getBarColor() {
        return barColor;
    }

    public BarStyle getBarStyle() {
        return barStyle;
    }

    public int getBossBarTime() {
        return bossBarTime;
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("messageList", messageList);

        map.put("actionBar", actionBar);

        if (title != null) {
            map.put("title", title);
            map.put("fadeIn", fadeIn);
            map.put("stay", stay);
            map.put("fadeOut", fadeOut);
        }

        if (subTitle != null) {
            map.put("subTitle", subTitle);
            map.put("fadeIn", fadeIn);
            map.put("stay", stay);
            map.put("fadeOut", fadeOut);
        }

        if (sound != null) {
            map.put("sound", sound.name());
            map.put("volume", volume);
            map.put("pitch", pitch);
        }

        if (bossBarTitle != null) {
            map.put("bossBarTitle", bossBarTitle);
            map.put("barColor", barColor.name());
            map.put("barStyle", barStyle.name());
            map.put("bossBarTime", bossBarTime);
        }
        return map;
    }

    /**
     * 可替换内容的文本消息
     */
    public class ReplaceMessage {
        private final HashMap<String, String> replace;

        private ReplaceMessage() {
            replace = new HashMap<>();
        }

        /**
         * 替换文本消息
         *
         * @param key   关键字
         * @param value 替换对象
         * @return this
         */
        @NotNull
        public ReplaceMessage replace(String key, String value) {
            replace.put(key, value);
            return this;
        }

        /**
         * 展示这条消息
         *
         * @param sender 展示对象
         */
        public void show(@NotNull CommandSender sender) {
            DisplayMessage.this.show(sender, replace);
        }

        /**
         * 展示这条消息给玩家
         *
         * @param player 玩家对象
         * @return 这个 show 所创建的 BossBar，如果没有设置 BossBar 消息则返回 null
         */
        public BossBar show(@NotNull Player player) {
            return DisplayMessage.this.show(player, replace);
        }

        /**
         * 展示这条消息给一些玩家
         *
         * @param players 玩家集合
         * @return 这个 show 所创建的 BossBar，如果没有设置 BossBar 消息则返回 null
         */
        public ArrayList<BossBar> show(@NotNull List<Player> players) {
            return DisplayMessage.this.show(players, replace);
        }

        /**
         * 广播这条消息
         *
         * @return 这个 broadcast 所创建的 BossBar，如果没有设置 BossBar 消息则返回 null
         */
        public ArrayList<BossBar> broadcast() {
            return DisplayMessage.this.broadcast(replace);
        }

    }

}