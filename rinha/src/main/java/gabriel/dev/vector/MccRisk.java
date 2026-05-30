package gabriel.dev.vector;

import java.util.Map;

public final class MccRisk {

    private static final Map<String, Double> RISK = Map.ofEntries(
            Map.entry("5411", 0.15),
            Map.entry("5812", 0.30),
            Map.entry("5912", 0.20),
            Map.entry("5944", 0.45),
            Map.entry("7801", 0.80),
            Map.entry("7802", 0.75),
            Map.entry("7995", 0.85),
            Map.entry("4511", 0.35),
            Map.entry("5311", 0.25),
            Map.entry("5999", 0.50)
    );

    public static final double DEFAULT = 0.5;

    private MccRisk() {}

    public static double of(String mcc) {
        return RISK.getOrDefault(mcc, DEFAULT);
    }
}