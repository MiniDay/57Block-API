package net.airgame.bukkit.api.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import net.airgame.bukkit.api.AirGameAPI;
import net.airgame.bukkit.api.object.SignEditFuture;
import net.airgame.bukkit.api.util.MessageUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * 牌子编辑监听器
 * <p>
 * 需要 ProtocolLib 前置支持
 */
public class SignEditListener extends PacketAdapter {
    public static final HashSet<SignEditFuture> SIGN_EDIT_FUTURES = new HashSet<>();

    public SignEditListener() {
        super(
                AirGameAPI.getInstance(),
                PacketType.Play.Client.UPDATE_SIGN,
                PacketType.Play.Server.OPEN_SIGN_EDITOR,
                PacketType.Play.Server.TILE_ENTITY_DATA,
                PacketType.Play.Server.BLOCK_CHANGE
        );
    }

    @SuppressWarnings("deprecation")
    public static SignEditFuture getPlayerInput(Player player, String[] lines) {
        Location playerLocation = player.getLocation();
        int y = playerLocation.getBlockY() + 8;
        if (y > 255) {
            y = 255;
        }

//        将玩家所处位置的 y+8 位置方块设置为牌子
        Location location = new Location(player.getWorld(), playerLocation.getBlockX(), y, playerLocation.getBlockZ());

        Block block = location.getBlock();
//        记录方块的原始数据，便于在玩家完成输入后，将该位置的方块修改回来
        SignEditFuture result = new SignEditFuture(player, location, block.getType(), block.getData());

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        Material material;
        try {
            material = Material.valueOf("OAK_WALL_SIGN");
        } catch (Exception e) {
            material = Material.valueOf("WALL_SIGN");
        }

//        使用 ProtocolLib 发包 欺骗客户端修改方块为牌子
//        PacketContainer blockChange = protocolManager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
//        blockChange.getBlockPositionModifier().write(0, blockPosition);
//        blockChange.getBlockData().write(0,
//                WrappedBlockData.createData(Material.WALL_SIGN)
//        );
//        try {
//            protocolManager.sendServerPacket(player, blockChange);
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        使用 Bukkit 自带的 API 欺骗客户端修改方块为牌子
        player.sendBlockChange(location, material, (byte) 0);

        BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        switch (Bukkit.getBukkitVersion().split("-")[0]) {
            case "1.7.10":
            case "1.8.8": {
                // 1.8.8 使用 UPDATE_SIGN 数据包来修改客户显示的牌子内容
                PacketContainer updateSign = protocolManager.createPacket(PacketType.Play.Server.UPDATE_SIGN);
                WrappedChatComponent[] updateLines = new WrappedChatComponent[4];
                for (int i = 0; i < updateLines.length; i++) {
                    updateLines[i] = WrappedChatComponent.fromText(lines[i]);
                }
                updateSign.getBlockPositionModifier().write(0, blockPosition);
                updateSign.getChatComponentArrays().write(0, updateLines);
                try {
                    protocolManager.sendServerPacket(player, updateSign);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            }
            default: {
                // 1.12.2 使用 TILE_ENTITY_DATA 数据包来修改客户显示的牌子内容
                PacketContainer updateSign = protocolManager.createPacket(PacketType.Play.Server.TILE_ENTITY_DATA);
                updateSign.getBlockPositionModifier().write(0, blockPosition);
                updateSign.getIntegers().write(0, 9);
                ArrayList<NbtBase<?>> list = new ArrayList<>();
                list.add(NbtFactory.of("x", blockPosition.getX()));
                list.add(NbtFactory.of("y", blockPosition.getY()));
                list.add(NbtFactory.of("z", blockPosition.getZ()));
                list.add(NbtFactory.of("id", "minecraft:sign"));
                list.add(NbtFactory.of("Text1", "{\"extra\":[{\"text\":\"" + lines[0] + "\"}],\"text\":\"\"}"));
                list.add(NbtFactory.of("Text2", "{\"extra\":[{\"text\":\"" + lines[1] + "\"}],\"text\":\"\"}"));
                list.add(NbtFactory.of("Text3", "{\"extra\":[{\"text\":\"" + lines[2] + "\"}],\"text\":\"\"}"));
                list.add(NbtFactory.of("Text4", "{\"extra\":[{\"text\":\"" + lines[3] + "\"}],\"text\":\"\"}"));
                NbtCompound compound = NbtFactory.ofCompound("", list);
                updateSign.getNbtModifier().write(0, compound);
                try {
                    protocolManager.sendServerPacket(player, updateSign);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        PacketContainer openSignEditor = protocolManager.createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);
        openSignEditor.getBlockPositionModifier().write(0, blockPosition);
        try {
            protocolManager.sendServerPacket(player, openSignEditor);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        internalPlayerInput(player);
        SignEditListener.SIGN_EDIT_FUTURES.add(result);
        return result;
    }

    public static void internalPlayerInput(Player player) {
        SignEditFuture future = SignEditListener.getSignEditFuture(player);
        if (future == null) {
            return;
        }
        future.cancel(true);
        SignEditListener.SIGN_EDIT_FUTURES.remove(future);
    }

    public static SignEditFuture getSignEditFuture(Player player) {
        for (SignEditFuture future : SIGN_EDIT_FUTURES) {
            if (future.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return future;
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onPacketReceiving(PacketEvent event) {
        try {
            Player player = event.getPlayer();
            PacketContainer packet = event.getPacket();
            if (packet.getType() != PacketType.Play.Client.UPDATE_SIGN) {
                return;
            }

            SignEditFuture future = getSignEditFuture(player);
            if (future == null) {
                return;
            }
            SIGN_EDIT_FUTURES.remove(future);

            BlockPosition blockPosition = packet.getBlockPositionModifier().read(0);
            int x = blockPosition.getX();
            int y = blockPosition.getY();
            int z = blockPosition.getZ();
            Location location = new Location(player.getWorld(), x, y, z);
            String[] lines;
            switch (Bukkit.getBukkitVersion().split("-")[0]) {
                case "1.7.10":
                case "1.8.8": {
                    lines = new String[4];
                    WrappedChatComponent[] chatComponents = packet.getChatComponentArrays().read(0);
                    for (int i = 0; i < chatComponents.length; i++) {
                        lines[i] = new TextComponent(MessageUtils.parseComponentFromJson(chatComponents[i].getJson())).toLegacyText();
                    }
                    break;
                }
                default: {
                    lines = packet.getStringArrays().read(0);
                }
            }
            event.setCancelled(true);
            future.complete(lines);
            Bukkit.getScheduler().runTask(getPlugin(), () -> player.sendBlockChange(location, future.getMaterial(), future.getBlockData()));
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onPacketSending(PacketEvent event) {

    }
}
