package net.airgame.bukkit.api.util;

@SuppressWarnings("unused")
public class FormatUtils {

    private FormatUtils() {
    }

    public static String formatNumber(long number) {
        if (number < 1000) {
            return String.valueOf(number);
        }
        if (number < 1000 * 1000) {
            return number / 1000 + "K";
        }
        return number / 1000 / 1000 + "M";
    }

    public static String formatNumber(double number) {
        if (number < 1000) {
            return String.format("%.2f", number);
        }
        if (number < 1000 * 1000) {
            return String.format("%.2fK", number / 1000);
        }
        return String.format("%.2fM", number / 1000 / 1000);
    }
}
