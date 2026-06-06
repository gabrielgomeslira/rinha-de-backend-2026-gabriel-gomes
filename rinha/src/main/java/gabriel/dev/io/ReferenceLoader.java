package gabriel.dev.io;

import gabriel.dev.fraud.KnnIndex;
import gabriel.dev.vector.Quantizer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public final class ReferenceLoader {

    private ReferenceLoader() {}

    public static KnnIndex load(String path) throws IOException {
        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(new FileInputStream(path), 1 << 20))) {
            int count = in.readInt();
            byte[] vectors = new byte[count * Quantizer.DIMS];
            byte[] labels = new byte[count];
            in.readFully(vectors);
            in.readFully(labels);
            return new KnnIndex(count, vectors, labels);
        }
    }
}
