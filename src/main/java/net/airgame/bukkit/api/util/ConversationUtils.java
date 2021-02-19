package net.airgame.bukkit.api.util;

import net.airgame.bukkit.api.listener.ConversationListener;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class ConversationUtils {
    private ConversationUtils() {
    }

    public static CompletableFuture<String> getPlayerInput(Player player) {
        return ConversationListener.getPlayerInput(player);
    }

    public static void internalPlayerInput(Player player) {
        ConversationListener.internalPlayerInput(player);
    }
}
