package vector;

import gabriel.dev.model.Payload;
import gabriel.dev.model.Payload.Customer;
import gabriel.dev.model.Payload.Merchant;
import gabriel.dev.model.Payload.Terminal;
import gabriel.dev.model.Payload.Transaction;
import gabriel.dev.vector.Normalization;
import gabriel.dev.vector.Vectorizer;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VectorizerTest {

    private final Vectorizer vectorizer = new Vectorizer(Normalization.defaults());

    @Test
    void exemploLegitimo() {
        var payload = new Payload(
                "tx-1329056812",
                new Transaction(41.12, 2, Instant.parse("2026-03-11T18:45:53Z")),
                new Customer(82.24, 3, List.of("MERC-003", "MERC-016")),
                new Merchant("MERC-016", "5411", 60.25),
                new Terminal(false, true, 29.23),
                null
        );

        double[] esperado = {0.0041, 0.1667, 0.05, 0.7826, 0.3333, -1, -1, 0.0292, 0.15, 0, 1, 0, 0.15, 0.006};
        assertVetorIgual(esperado, vectorizer.vectorize(payload));
    }

    @Test
    void exemploFraude() {
        var payload = new Payload(
                "tx-3330991687",
                new Transaction(9505.97, 10, Instant.parse("2026-03-14T05:15:12Z")),
                new Customer(81.28, 20, List.of("MERC-008", "MERC-007", "MERC-005")),
                new Merchant("MERC-068", "7802", 54.86),
                new Terminal(false, true, 952.27),
                null
        );

        double[] esperado = {0.9506, 0.8333, 1.0, 0.2174, 0.8333, -1, -1, 0.9523, 1.0, 0, 1, 1, 0.75, 0.0055};
        assertVetorIgual(esperado, vectorizer.vectorize(payload));
    }

    private static void assertVetorIgual(double[] esperado, double[] obtido) {
        assertEquals(esperado.length, obtido.length, "tamanho do vetor");
        for (int i = 0; i < esperado.length; i++) {
            assertEquals(esperado[i], obtido[i], 1e-3, "índice " + i);
        }
    }
}