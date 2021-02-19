package net.airgame.bukkit.api.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ConversationListener implements Listener {
    public static HashMap<UUID, CompletableFuture<String>> playerConversation;

    public static CompletableFuture<String> getPlayerInput(Player player) {
        internalPlayerInput(player);
        return playerConversation.put(player.getUniqueId(), new CompletableFuture<>());
    }

    public static void internalPlayerInput(Player player) {
        CompletableFuture<String> future = playerConversation.remove(player.getUniqueId());
        if (future == null) {
            return;
        }
        future.cancel(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        CompletableFuture<String> future = playerConversation.remove(player.getUniqueId());
        if (future == null) {
            return;
        }
        future.complete(event.getMessage());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CompletableFuture<String> future = playerConversation.remove(player.getUniqueId());
        if (future == null) {
            return;
        }
        future.completeExceptionally(new IllegalStateException("玩家退出了服务器."));
    }
}
