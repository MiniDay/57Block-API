package net.airgame.bukkit.api.command.parameter;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Stack;

public abstract class ParameterParser {
    private ParameterParser next;

    @NotNull
    public ParameterParser getNext() {
        return next;
    }

    public void setNext(@NotNull ParameterParser next) {
        this.next = next;
    }

    /**
     * 尝试通过传入的字符串解析命令参数对象
     * <p>
     * 流程为：
     * <p>
     * 1. 判断自己的参数是否足够，若不足则返回 false
     * <p>
     * 2. 判断自己的参数是否能够成功解析对象，若不能则返回 false
     * <p>
     * 3. 判断自己占用了需要的参数个数之后，之后的 parser 是否能够返回 true
     * <p>
     * 4. 若之后的 parser 返回 true，则把解析的参数 push 进 parameters 并返回 true
     * <p>
     * 5. 若之后的 parser 返回 false，则不 push 进 parameters 并返回 false
     *
     * @param parameters 已解析的参数
     * @param sender     命令执行者
     * @param command    命令对象
     * @param label      命令别名
     * @param args       参数列表
     * @param index      这个解析器从第几个参数开始
     * @return 返回 true 则代表解析成功
     */
    public abstract boolean parser(@NotNull Stack<Object> parameters, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, int index);

    /**
     * 生成并返回 tab 自动补全的参数
     * <p>
     * 流程为：
     * <p>
     * 1. 根据 args 和 index 尝试填补参数，填补的参数 add 进 parameters 集合中
     * <p>
     * 2. 若填补成功则 return
     * <p>
     * 3. 若发现 index 处已有匹配的参数则调用 next 的填补（index + 1）
     * <p>
     * 4. 若发现 index 处已有参数，且不匹配该解析器则直接 return null
     *
     * @param sender   命令执行者
     * @param command  命令对象
     * @param label    命令别名
     * @param args     已输入的参数
     * @param location 命令执行者所在的位置
     * @param index    这个解析器从第几个参数开始，不会大于 args 的 length
     * @return 补全内容
     */
    @Nullable
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @Nullable Location location, int index) {
        if (index == args.length - 1) {
            List<String> list = onTabComplete(sender, command, label, args, location, index);
            if (list == null || list.isEmpty()) {
                return null;
            }
            return list;
        }
        if (index + 1 >= args.length) {
            return null;
        }
        return getNext().tabComplete(sender, command, label, args, location, index + 1);
    }

    /**
     * 生成并返回 tab 自动补全的参数
     *
     * @param sender   命令执行者
     * @param command  命令对象
     * @param label    命令别名
     * @param args     已输入的参数
     * @param location 命令执行者所在的位置
     * @param index    这个解析器从第几个参数开始，不会大于 args 的 length
     * @return 补全内容
     */
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args, @Nullable Location location, int index) {
        return null;
    }

}
