package net.airgame.bukkit.api.util;

import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class SerializeUtils {
    private SerializeUtils() {
    }

    /**
     * 从字符串中反序列化一个 BlockFace 对象
     *
     * @param string 字符串
     * @return BlockFace 对象
     */
    @Nullable
    public static BlockFace deserializeBlockFace(String string) {
        return deserializeBlockFace(string, null);
    }

    /**
     * 从字符串中反序列化一个 BlockFace 对象
     *
     * @param string       字符串
     * @param defaultValue 若未匹配到任何 BlockFace 则返回该默认值
     * @return BlockFace 对象
     */
    public static BlockFace deserializeBlockFace(String string, BlockFace defaultValue) {
        switch (string.toLowerCase()) {
            case "north":
            case "n":
                return BlockFace.NORTH;
            case "east":
            case "e":
                return BlockFace.EAST;
            case "south":
            case "s":
                return BlockFace.SOUTH;
            case "west":
            case "w":
                return BlockFace.WEST;
            case "up":
            case "top":
                return BlockFace.UP;
            case "down":
            case "bottom":
                return BlockFace.DOWN;
            case "north_east":
            case "northeast":
            case "ne":
                return BlockFace.NORTH_EAST;
            case "north_west":
            case "northwest":
            case "nw":
                return BlockFace.NORTH_WEST;
            case "south_east":
            case "southeast":
            case "se":
                return BlockFace.SOUTH_EAST;
            case "south_west":
            case "southwest":
            case "sw":
                return BlockFace.SOUTH_WEST;
            case "self":
                return BlockFace.SELF;
            default:
                return defaultValue;
        }
    }

    /**
     * 从字符串中反序列化一个 boolean 变量
     *
     * @param string 字符串
     * @return boolean 变量
     */
    @Nullable
    public static Boolean deserializeBoolean(String string) {
        return deserializeBoolean(string, null);
    }

    /**
     * 从字符串中反序列化一个 boolean 变量
     *
     * @param string       字符串
     * @param defaultValue 若未匹配到任何 boolean 则返回该默认值
     * @return boolean 变量
     */
    public static Boolean deserializeBoolean(String string, Boolean defaultValue) {
        switch (string.toLowerCase()) {
            case "on":
            case "true":
            case "1":
            case "yes":
                return true;
            case "off":
            case "false":
            case "0":
            case "no":
                return false;
            default:
                return defaultValue;
        }
    }

    /**
     * 从字符串中反序列化一个 GameMode 对象
     *
     * @param string 字符串
     * @return GameMode 对象
     */
    @Nullable
    public static GameMode deserializeGameMode(String string) {
        return deserializeGameMode(string, null);
    }

    /**
     * 从字符串中反序列化一个 GameMode 对象
     *
     * @param string       字符串
     * @param defaultValue 若未匹配到任何 GameMode 则返回该默认值
     * @return GameMode 对象
     */
    public static GameMode deserializeGameMode(String string, GameMode defaultValue) {
        switch (string.toLowerCase()) {
            case "survival":
            case "survive":
            case "0":
                return GameMode.SURVIVAL;
            case "creative":
            case "create":
            case "1":
                return GameMode.CREATIVE;
            case "adventure":
            case "2":
                return GameMode.ADVENTURE;
            case "spectator":
            case "spec":
            case "3":
                return GameMode.SPECTATOR;
            default:
                return defaultValue;
        }
    }

}
