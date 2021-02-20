package net.airgame.bukkit.api.util;

@SuppressWarnings("unused")
public class MathUtils {
    private MathUtils() {
    }

    public static int locationToChunk(int a) {
        if (a >= 0) {
            a /= 16;
        } else {
            a = (a + 1) / 16 - 1;
        }
        return a;
    }

    public static int locationToChunk(double a) {
        if (a >= 0) {
            a /= 16;
        } else {
            a = (a + 1) / 16 - 1;
        }
        return (int) a;
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
