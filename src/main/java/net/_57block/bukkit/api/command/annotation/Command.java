package net._57block.bukkit.api.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    /**
     * 子命令名称
     * <p>
     * 读取后会自动转换成全小写
     *
     * @return 子命令名称
     */
    String[] subName() default {};

    /**
     * 执行该命令需要的权限
     * <p>
     * 读取后会自动转换成全小写
     *
     * @return 权限列表
     */
    String[] permission() default {};
}
