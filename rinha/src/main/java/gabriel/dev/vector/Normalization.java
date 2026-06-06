package gabriel.dev.vector;

public record Normalization(
        double maxAmount,
        double maxInstallments,
        double amountVsAvgRatio,
        double maxMinutes,
        double maxKm,
        double maxTxCount24h,
        double maxMerchantAvgAmount
) {
    public static Normalization defaults() {
        return new Normalization(10_000, 12, 10, 1_440, 1_000, 20, 10_000);
    }
}