package gabriel.dev.fraud;

public final class KnnIndex {

    private static final int K = 5;
    private static final int DIMS = 14;

    private final int count;
    private final byte[] vectors;
    private final byte[] labels;

    public KnnIndex(int count, byte[] vectors, byte[] labels) {
        this.count = count;
        this.vectors = vectors;
        this.labels = labels;
    }

    public int count() {
        return count;
    }

    public int fraudAmongNearest(byte[] q) {
        int[] best = new int[K];
        boolean[] fraud = new boolean[K];
        for (int i = 0; i < K; i++) {
            best[i] = Integer.MAX_VALUE;
        }
        int worst = Integer.MAX_VALUE;
        int worstIdx = 0;

        byte[] ref = vectors;
        byte[] lab = labels;
        int n = count;

        for (int r = 0, base = 0; r < n; r++, base += DIMS) {
            int d = 0;
            for (int k = 0; k < DIMS; k++) {
                int diff = q[k] - ref[base + k];
                d += diff * diff;
                if (d >= worst) {
                    d = Integer.MAX_VALUE;
                    break;
                }
            }
            if (d < worst) {
                best[worstIdx] = d;
                fraud[worstIdx] = lab[r] != 0;
                worst = best[0];
                worstIdx = 0;
                for (int j = 1; j < K; j++) {
                    if (best[j] > worst) {
                        worst = best[j];
                        worstIdx = j;
                    }
                }
            }
        }

        int fraudCount = 0;
        for (int i = 0; i < K; i++) {
            if (fraud[i]) {
                fraudCount++;
            }
        }
        return fraudCount;
    }
}
