package net.airgame.bukkit.api.util;

@SuppressWarnings("unused")
public class MathUtils {
    private MathUtils() {
    }

    /**
     * x 是否在 [a,b] 区间内
     *
     * @param x x
     * @param a a
     * @param b b
     * @return x 是否在 [a,b] 区间内
     */
    public static boolean numberInAB(int x, int a, int b) {
        return x >= a && x <= b;
    }

    /**
     * x 是否在 [a,b] 区间内
     *
     * @param x x
     * @param a a
     * @param b b
     * @return x 是否在 [a,b] 区间内
     */
    public static boolean numberInAB(long x, long a, long b) {
        return x >= a && x <= b;
    }

    /**
     * x 是否在 [a,b] 区间内
     *
     * @param x x
     * @param a a
     * @param b b
     * @return x 是否在 [a,b] 区间内
     */
    public static boolean numberInAB(double x, double a, double b) {
        return x >= a && x <= b;
    }
}
