package net.apartium.cocoabeans;

import org.jetbrains.annotations.ApiStatus;

/**
 * A utility class providing mathematical functions.
 * <p>
 * Currently includes linear interpolation (lerp) methods for various primitive types.
 *
 * @since 0.0.39
 */
@ApiStatus.AvailableSince("0.0.39")
public class Mathf {

    public static final double LOG4 = Math.log(4);

    private Mathf () { }

    /**
     * Performs linear interpolation between two {@code int} values.
     *
     * @param a the start value
     * @param b the end value
     * @param t the interpolation factor, typically in the range [0.0, 1.0]
     * @return the interpolated value, cast to {@code int}
     */
    public static int lerp(int a, int b, float t) {
        return (int) (a + (b - a) * t);
    }


    /**
     * Performs linear interpolation between two {@code long} values.
     *
     * @param a the start value
     * @param b the end value
     * @param t the interpolation factor, typically in the range [0.0, 1.0]
     * @return the interpolated value, cast to {@code long}
     */
    public static long lerp(long a, long b, float t) {
        return (long) (a + (b - a) * t);
    }

    /**
     * Performs linear interpolation between two {@code float} values.
     *
     * @param a the start value
     * @param b the end value
     * @param t the interpolation factor, typically in the range [0.0f, 1.0f]
     * @return the interpolated {@code float} value
     */
    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    /**
     * Performs linear interpolation between two {@code double} values.
     *
     * @param a the start value
     * @param b the end value
     * @param t the interpolation factor, typically in the range [0.0, 1.0]
     * @return the interpolated {@code double} value
     */
    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    public static long nextPowerOfFour(long x) {
        if (x <= 1)
            return 1L;

        if (x > (1L << 62))
            return 0L; // overflow guard

        int bitLength = 64 - Long.numberOfLeadingZeros(x - 1);
        int k = (bitLength + 1) / 2;
        int shift = k << 1;
        return 1L << shift;
    }

    public static double log4(long x) {
        return Math.log(x) / LOG4;
    }

}
