package gabriel.dev.vector;

import gabriel.dev.model.Payload;

import java.time.Duration;
import java.time.ZoneOffset;

public final class Vectorizer {

    private final Normalization n;

    public Vectorizer(Normalization n) {
        this.n = n;
    }

    public double[] vectorize(Payload p) {
        double[] v = new double[14];

        var tx = p.transaction();
        var cust = p.customer();
        var merch = p.merchant();
        var term = p.terminal();

        v[0] = clamp(tx.amount() / n.maxAmount());
        v[1] = clamp(tx.installments() / n.maxInstallments());
        v[2] = clamp((tx.amount() / cust.avgAmount()) / n.amountVsAvgRatio());


        var utc = tx.requestedAt().atZone(ZoneOffset.UTC);
        v[3] = utc.getHour() / 23.0;
        v[4] = (utc.getDayOfWeek().getValue() - 1) / 6.0;


        var last = p.lastTransaction();
        if (last == null) {
            v[5] = -1;
            v[6] = -1;
        } else {
            long minutes = Duration.between(last.timestamp(), tx.requestedAt()).toMinutes();
            v[5] = clamp(minutes / n.maxMinutes());
            v[6] = clamp(last.kmFromCurrent() / n.maxKm());
        }

        v[7] = clamp(term.kmFromHome() / n.maxKm());

        v[8] = clamp(cust.txCount24h() / n.maxTxCount24h());

        v[9] = term.isOnline() ? 1 : 0;
        v[10] = term.cardPresent() ? 1 : 0;

        v[11] = cust.knownMerchants().contains(merch.id()) ? 0 : 1;

        v[12] = MccRisk.of(merch.mcc());

        v[13] = clamp(merch.avgAmount() / n.maxMerchantAvgAmount());

        return v;
    }

    private static double clamp(double x) {
        if (x < 0.0) return 0.0;
        if (x > 1.0) return 1.0;
        return x;
    }
}