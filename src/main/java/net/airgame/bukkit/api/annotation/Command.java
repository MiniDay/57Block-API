package net.airgame.bukkit.api.annotation;

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
     * 子命令优先级，默认为 10
     * <p>
     * 该值越小的命令会越先被加载
     * <p>
     * 当两个同名命令的不同参数（例如 Player 和 CommandSender）冲突时
     * <p>
     * 可以更改优先级使 Player 的命令先加载，这样当玩家使用命令时就不会触发 CommandSender 的代码
     *
     * @return 子命令优先级
     */
    int priority() default 10;

    /**
     * 执行该命令需要的权限
     * <p>
     * 读取后会自动转换成全小写
     *
     * @return 权限列表
     */
    String[] permission() default {};
}
