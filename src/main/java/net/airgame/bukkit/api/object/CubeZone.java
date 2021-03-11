package net.airgame.bukkit.api.object;

import net.airgame.bukkit.api.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 立方体区域
 */
@SerializableAs("CubeZone")
@SuppressWarnings({"unused", "ConstantConditions"})
public class CubeZone implements ConfigurationSerializable, Cloneable {
    private String worldName;

    private int minX;
    private int minY;
    private int minZ;

    private int maxX;
    private int maxY;
    private int maxZ;

    public CubeZone(@NotNull Location location1, @NotNull Location location2) {
        if (location1.getWorld() != location2.getWorld()) {
            throw new IllegalStateException("两个坐标必须在同一个世界!");
        }

        worldName = location1.getWorld().getName();

        minX = Math.min(location1.getBlockX(), location2.getBlockX());
        maxX = Math.max(location1.getBlockX(), location2.getBlockX());

        minY = Math.min(location1.getBlockY(), location2.getBlockY());
        maxY = Math.max(location1.getBlockY(), location2.getBlockY());

        minZ = Math.min(location1.getBlockZ(), location2.getBlockZ());
        maxZ = Math.max(location1.getBlockZ(), location2.getBlockZ());
    }

    public CubeZone(@NotNull World world, @NotNull Vector vector1, @NotNull Vector vector2) {
        this.worldName = world.getName();

        minX = Math.min(vector1.getBlockX(), vector2.getBlockX());
        maxX = Math.max(vector1.getBlockX(), vector2.getBlockX());

        minY = Math.min(vector1.getBlockY(), vector2.getBlockY());
        maxY = Math.max(vector1.getBlockY(), vector2.getBlockY());

        minZ = Math.min(vector1.getBlockZ(), vector2.getBlockZ());
        maxZ = Math.max(vector1.getBlockZ(), vector2.getBlockZ());

    }

    public CubeZone(@NotNull World world, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        this.worldName = world.getName();

        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);

        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
    }

    public CubeZone(Map<String, Object> map) {
        worldName = (String) map.get("worldName");

        int minX = (int) map.get("minX");
        int minY = (int) map.get("minY");
        int minZ = (int) map.get("minZ");

        int maxX = (int) map.get("maxX");
        int maxY = (int) map.get("maxY");
        int maxZ = (int) map.get("maxZ");

        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);

        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
    }

    public ArrayList<Block> getZoneBlocks() {
        World world = getWorld();
        ArrayList<Block> blocks = new ArrayList<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.add(world.getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    public ArrayList<Location> getZoneLocations() {
        World world = getWorld();
        ArrayList<Location> locations = new ArrayList<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    locations.add(new Location(world, x, y, z));
                }
            }
        }
        return locations;
    }

    @NotNull
    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    public void setWorld(@NotNull World world) {
        worldName = world.getName();
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int x) {
        if (maxX < x) {
            maxX = x;
        } else {
            minX = x;
        }
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int y) {
        if (maxY < y) {
            maxY = y;
        } else {
            minY = y;
        }
    }

    public int getMinZ() {
        return minZ;
    }

    public void setMinZ(int z) {
        if (maxZ < z) {
            maxZ = z;
        } else {
            minZ = z;
        }
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int x) {
        if (minX > x) {
            minX = x;
        } else {
            maxX = x;
        }
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int y) {
        if (minY > y) {
            minY = y;
        } else {
            maxY = y;
        }
    }

    public int getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(int z) {
        if (minZ > z) {
            minZ = z;
        } else {
            maxZ = z;
        }
    }

    /**
     * 检查这个坐标是否在该区域内
     *
     * @param location 坐标
     * @return 这个坐标是否在该区域内
     * @since 1.1.22
     */
    public boolean isLocationInZone(Location location) {
        if (location.getWorld() != getWorld()) {
            return false;
        }
        if (!MathUtils.numberInAB(location.getX(), getMinX(), getMaxX())) {
            return false;
        }
        if (!MathUtils.numberInAB(location.getY(), getMinY(), getMaxY())) {
            return false;
        }
        return MathUtils.numberInAB(location.getZ(), getMinZ(), getMaxZ());
    }

    /**
     * 添加坐标偏移量
     *
     * @param offsetX x
     * @param offsetY y
     * @param offsetZ z
     * @since 1.1.22
     */
    public void add(int offsetX, int offsetY, int offsetZ) {
        minX += offsetX;
        maxX += offsetX;

        minY += offsetY;
        maxY += offsetY;

        minZ += offsetZ;
        maxZ += offsetZ;
    }

    /**
     * 减去坐标偏移量
     *
     * @param offsetX x
     * @param offsetY y
     * @param offsetZ z
     * @since 1.1.22
     */
    public void subtract(int offsetX, int offsetY, int offsetZ) {
        minX -= offsetX;
        maxX -= offsetX;

        minY -= offsetY;
        maxY -= offsetY;

        minZ -= offsetZ;
        maxZ -= offsetZ;
    }

    /**
     * 乘以坐标偏移量
     *
     * @param offsetX x
     * @param offsetY y
     * @param offsetZ z
     * @since 1.1.22
     */
    public void multiply(int offsetX, int offsetY, int offsetZ) {
        minX *= offsetX;
        maxX *= offsetX;

        minY *= offsetY;
        maxY *= offsetY;

        minZ *= offsetZ;
        maxZ *= offsetZ;
    }

    /**
     * 除去坐标偏移量
     *
     * @param offsetX x
     * @param offsetY y
     * @param offsetZ z
     * @since 1.1.22
     */
    public void division(int offsetX, int offsetY, int offsetZ) {
        minX /= offsetX;
        maxX /= offsetX;

        minY /= offsetY;
        maxY /= offsetY;

        minZ /= offsetZ;
        maxZ /= offsetZ;
    }

    @Override
    protected CubeZone clone() throws CloneNotSupportedException {
        return (CubeZone) super.clone();
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("worldName", worldName);

        map.put("minX", minX);
        map.put("minY", minY);
        map.put("maxX", maxX);

        map.put("minZ", minZ);
        map.put("maxY", maxY);
        map.put("maxZ", maxZ);
        return map;
    }

    @Override
    public String toString() {
        return "CubeZone{" +
                "worldName='" + worldName + '\'' +
                ", minX=" + minX +
                ", maxX=" + maxX +
                ", minY=" + minY +
                ", maxY=" + maxY +
                ", minZ=" + minZ +
                ", maxZ=" + maxZ +
                '}';
    }

}
