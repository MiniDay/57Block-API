package net.airgame.bukkit.api.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.airgame.bukkit.api.AirGameAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

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

    /**
     * 从字符串中解码一条坐标
     * <p>
     * 字符串格式: "世界名;x;y;z;yaw;pitch"
     * <p>
     * yaw和pitch可以省略
     * <p>
     * 即: "世界名;x;y;z" 也是可以接受的
     *
     * @param string 要解码的字符串. 例如:
     * @return 解码后的坐标
     */
    @NotNull
    public static Location deserializeLocation(@NotNull String string) {
        String[] args = string.split(";");
        try {
            if (args.length > 4) {
                return new Location(
                        Bukkit.getWorld(args[0]),
                        Double.parseDouble(args[1]),
                        Double.parseDouble(args[2]),
                        Double.parseDouble(args[3]),
                        Float.parseFloat(args[4]),
                        Float.parseFloat(args[5])
                );
            } else {
                return new Location(
                        Bukkit.getWorld(args[0]),
                        Double.parseDouble(args[1]),
                        Double.parseDouble(args[2]),
                        Double.parseDouble(args[3])
                );
            }
        } catch (Exception e) {
            AirGameAPI.getLogUtils().error(e, "解析坐标字符串 %s 时出现了一个错误: ", string);
        }
        return new Location(Bukkit.getWorlds().get(0), 0, 64, 0);
    }

    /**
     * 从一个字符串集合中解码坐标
     *
     * @param strings 字符串集合
     * @return 坐标集合
     * @see SerializeUtils#deserializeLocation(String)
     */
    @NotNull
    public static ArrayList<Location> deserializeLocation(@NotNull Collection<String> strings) {
        ArrayList<Location> locations = new ArrayList<>();
        for (String string : strings) {
            locations.add(deserializeLocation(string));
        }
        return locations;
    }

    public static JsonArray serializeUUIDCollectionToJson(Collection<UUID> collection) {
        JsonArray array = new JsonArray();
        for (UUID uuid : collection) {
            array.add(uuid.toString());
        }
        return array;
    }

    public static HashSet<UUID> deserializeUUID(JsonObject object, String name) {
        return deserializeUUID(object.getAsJsonArray(name));
    }

    public static HashSet<UUID> deserializeUUID(JsonArray array) {
        HashSet<UUID> set = new HashSet<>();
        for (JsonElement element : array) {
            set.add(UUID.fromString(element.getAsString()));
        }
        return set;
    }

}
