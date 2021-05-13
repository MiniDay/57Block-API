package net.airgame.bukkit.api.annotation;

public @interface CommandParameter {
    String name();

    String[] tabList() default {};
}
