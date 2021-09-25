package android.util;

public class MathUtils {

    public static float lerp(float start, float stop, float amount) {
        throw new RuntimeException("Stub!");
    }

    /**
     * Calculates a value in [rangeMin, rangeMax] that maps value in [valueMin, valueMax] to
     * returnVal in [rangeMin, rangeMax].
     * <p>
     * Always returns a constrained value in the range [rangeMin, rangeMax], even if value is
     * outside [valueMin, valueMax].
     * <p>
     * Eg:
     *    constrainedMap(0f, 100f, 0f, 1f, 0.5f) = 50f
     *    constrainedMap(20f, 200f, 10f, 20f, 20f) = 200f
     *    constrainedMap(20f, 200f, 10f, 20f, 50f) = 200f
     *    constrainedMap(10f, 50f, 10f, 20f, 5f) = 10f
     *
     * @param rangeMin minimum of the range that should be returned.
     * @param rangeMax maximum of the range that should be returned.
     * @param valueMin minimum of range to map {@code value} to.
     * @param valueMax maximum of range to map {@code value} to.
     * @param value to map to the range [{@code valueMin}, {@code valueMax}]. Note, can be outside
     *              this range, resulting in a clamped value.
     * @return the mapped value, constrained to [{@code rangeMin}, {@code rangeMax}.
     */
    public static float constrainedMap(
            float rangeMin, float rangeMax, float valueMin, float valueMax, float value) {
        throw new RuntimeException("Stub!");
    }



}
