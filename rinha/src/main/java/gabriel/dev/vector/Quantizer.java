package gabriel.dev.vector;

public final class Quantizer {

    public static final int DIMS = 14;
    public static final int SCALE = 100;
    public static final byte SENTINEL = -100;

    private Quantizer() {}

    public static byte quantize(double value) {
        if (value < 0.0) {
            return SENTINEL;
        }
        double clamped = value > 1.0 ? 1.0 : value;
        return (byte) Math.round(clamped * SCALE);
    }

    public static byte[] quantize(double[] vector) {
        byte[] q = new byte[DIMS];
        for (int i = 0; i < DIMS; i++) {
            q[i] = quantize(vector[i]);
        }
        return q;
    }
}
