package net.airgame.bukkit.api.object;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SignEditFuture extends CompletableFuture<String[]> {
    private final Player player;
    private final Location location;
    private final byte blockData;
    private final Material material;

    public SignEditFuture(Player player, Location location, Material material, byte blockData) {
        this.player = player;
        this.location = location;
        this.material = material;
        this.blockData = blockData;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }

    public Material getMaterial() {
        return material;
    }

    public byte getBlockData() {
        return blockData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SignEditFuture)) return false;
        SignEditFuture that = (SignEditFuture) o;
        return player.getUniqueId().equals(that.player.getUniqueId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(player.getUniqueId());
    }
}
