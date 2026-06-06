package gabriel.dev.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gabriel.dev.fraud.KnnIndex;
import gabriel.dev.io.ReferenceLoader;
import gabriel.dev.model.Payload;
import gabriel.dev.vector.Normalization;
import gabriel.dev.vector.Quantizer;
import gabriel.dev.vector.Vectorizer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public final class Server {

    private static final String[] SCORES = {"0.0", "0.2", "0.4", "0.6", "0.8", "1.0"};

    private static final String FALLBACK = "{\"approved\":false,\"fraud_score\":1.0}";

    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    public static void main(String[] args) throws Exception {
        String dataPath = System.getenv().getOrDefault("REFERENCES_BIN", "references.bin");
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "9999"));

        Normalization normalization = Normalization.defaults();
        Vectorizer vectorizer = new Vectorizer(normalization);
        KnnIndex index = ReferenceLoader.load(dataPath);

        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

        server.createContext("/ready", exchange -> respond(exchange, 200, "OK"));

        server.createContext("/fraud-score", exchange -> {
            try {
                byte[] body = exchange.getRequestBody().readAllBytes();
                Payload payload = MAPPER.readValue(body, Payload.class);
                double[] vector = vectorizer.vectorize(payload);
                byte[] q = Quantizer.quantize(vector);
                int fraud = index.fraudAmongNearest(q);
                boolean approved = fraud < 3;
                String json = "{\"approved\":" + approved + ",\"fraud_score\":" + SCORES[fraud] + "}";
                respondJson(exchange, 200, json);
            } catch (Exception e) {
                respondJson(exchange, 200, FALLBACK);
            }
        });

        server.start();
        System.out.println("ready port=" + port + " references=" + index.count());
    }

    private static void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
        if (bytes.length > 0) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } else {
            exchange.close();
        }
    }

    private static void respondJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
