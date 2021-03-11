package net.airgame.bukkit.api.util;

import java.math.BigDecimal;

@SuppressWarnings("unused")
public class FormatUtils {
    private static final BigDecimal K = new BigDecimal(1024);
    private static final BigDecimal M = K.multiply(K);
    private static final BigDecimal G = M.multiply(K);
    private static final BigDecimal T = G.multiply(K);
    private static final BigDecimal P = T.multiply(K);
    private static final BigDecimal E = P.multiply(K);
    private static final BigDecimal Z = E.multiply(K);
    private static final BigDecimal Y = Z.multiply(K);

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

    public static String formatNumber(BigDecimal number) {
        if (number.compareTo(K) < 0) {
            return number.toPlainString();
        }
        if (number.compareTo(M) < 0) {
            return number.divide(K, 2, BigDecimal.ROUND_DOWN).toPlainString() + "K";
        }
        if (number.compareTo(G) < 0) {
            return number.divide(M, 2, BigDecimal.ROUND_DOWN).toPlainString() + "M";
        }
        if (number.compareTo(T) < 0) {
            return number.divide(G, 2, BigDecimal.ROUND_DOWN).toPlainString() + "G";
        }
        if (number.compareTo(P) < 0) {
            return number.divide(T, 2, BigDecimal.ROUND_DOWN).toPlainString() + "T";
        }
        if (number.compareTo(E) < 0) {
            return number.divide(P, 2, BigDecimal.ROUND_DOWN).toPlainString() + "P";
        }
        if (number.compareTo(Z) < 0) {
            return number.divide(E, 2, BigDecimal.ROUND_DOWN).toPlainString() + "E";
        }
        if (number.compareTo(Y) < 0) {
            return number.divide(Z, 2, BigDecimal.ROUND_DOWN).toPlainString() + "Z";
        }
        return number.divide(Y, 2, BigDecimal.ROUND_DOWN).toPlainString() + "Y";
    }
}
