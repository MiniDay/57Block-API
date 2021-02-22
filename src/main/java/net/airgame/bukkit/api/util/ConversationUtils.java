package net.airgame.bukkit.api.util;

import net.airgame.bukkit.api.listener.ConversationListener;
import org.bukkit.entity.HumanEntity;

import java.util.concurrent.CompletableFuture;

public class ConversationUtils {
    private ConversationUtils() {
    }

    public static CompletableFuture<String> getPlayerInput(HumanEntity player) {
        return ConversationListener.getPlayerInput(player);
    }

    public static void internalPlayerInput(HumanEntity player) {
        ConversationListener.internalPlayerInput(player);
    }
}
