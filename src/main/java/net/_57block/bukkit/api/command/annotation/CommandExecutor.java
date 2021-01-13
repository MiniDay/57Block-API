package net._57block.bukkit.api.command.annotation;

import org.bukkit.command.Command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandExecutor {
    /**
     * 命令名称
     * <p>
     * 读取后会自动转换成全小写
     *
     * @return 命令名称
     */
    String name();

    /**
     * 命令描述
     *
     * @return 命令描述
     */
    String[] description() default {};

    /**
     * 命令别名
     * <p>
     * 读取后会自动转换成全小写
     *
     * @return 命令别名
     */
    String[] aliases() default {};

    /**
     * 命令使用帮助
     * <p>
     * 一般情况下用不到，除非使用 command.getUsage()
     *
     * @return 使用帮助
     * @see Command#getUsage()
     */
    String[] usage() default {};

    /**
     * 执行该命令需要的权限
     * <p>
     * 读取后会自动转换成全小写
     *
     * @return 权限列表
     */
    String[] permission() default {};
}
