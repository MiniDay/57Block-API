package net.airgame.bukkit.api.prompt;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EndStringPrompt extends MessagePrompt {
    private final String message;

    public EndStringPrompt(String message) {
        this.message = message;
    }

    @Nullable
    @Override
    protected Prompt getNextPrompt(@NotNull ConversationContext context) {
        return END_OF_CONVERSATION;
    }

    @NotNull
    @Override
    public String getPromptText(@NotNull ConversationContext context) {
        return message;
    }
}
