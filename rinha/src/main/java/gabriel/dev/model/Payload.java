package gabriel.dev.model;

import java.time.Instant;
import java.util.List;

public record Payload(
        String id,
        Transaction transaction,
        Customer customer,
        Merchant merchant,
        Terminal terminal,
        LastTransaction lastTransaction
) {
    public record Transaction(double amount, int installments, Instant requestedAt) {}
    public record Customer(double avgAmount, int txCount24h, List<String> knownMerchants) {}
    public record Merchant(String id, String mcc, double avgAmount) {}
    public record Terminal(boolean isOnline, boolean cardPresent, double kmFromHome) {}
    public record LastTransaction(Instant timestamp, double kmFromCurrent) {}
}