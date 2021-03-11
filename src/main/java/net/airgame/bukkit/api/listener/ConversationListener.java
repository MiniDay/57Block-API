package net.airgame.bukkit.api.listener;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 会话监听器
 */
public class ConversationListener implements Listener {
    public static HashMap<UUID, CompletableFuture<String>> PLAYER_CONVERSATIONS = new HashMap<>();

    public static CompletableFuture<String> getPlayerInput(HumanEntity player) {
        internalPlayerInput(player);
        CompletableFuture<String> future = new CompletableFuture<>();
        PLAYER_CONVERSATIONS.put(player.getUniqueId(), future);
        return future;
    }

    public static void internalPlayerInput(HumanEntity player) {
        CompletableFuture<String> future = PLAYER_CONVERSATIONS.remove(player.getUniqueId());
        if (future == null) {
            return;
        }
        future.cancel(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        CompletableFuture<String> future = PLAYER_CONVERSATIONS.remove(player.getUniqueId());
        if (future == null) {
            return;
        }
        future.complete(event.getMessage());
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CompletableFuture<String> future = PLAYER_CONVERSATIONS.remove(player.getUniqueId());
        if (future == null) {
            return;
        }
        future.completeExceptionally(new IllegalStateException("玩家退出了服务器."));
    }
}
