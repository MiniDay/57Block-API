package net._57block.bukkit.api.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    /**
     * 子命令名称，会自动转换成小写
     *
     * @return 子命令
     */
    String[] subName() default {};

    String[] permission() default {};
}
