package br.com.zenon;

import br.com.zenon.dao.ParallelTransactionIngestor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IngestionMain {

    private final static Logger LOGGER = Logger.getLogger(IngestionMain.class.getName());

    static void main() {

        try {
            ParallelTransactionIngestor parallelTransactionIngestor = new ParallelTransactionIngestor();
            long inicio = System.nanoTime();
            parallelTransactionIngestor.readAsStreamParallel();
            long fim = System.nanoTime();

            long result = fim - inicio;

            long emMiliSegundos = TimeUnit.NANOSECONDS.toMillis(result);
            System.out.println("A inserção do dados demorou: " + emMiliSegundos + "ms");
        } catch (IOException | InterruptedException ex) {
            LOGGER.log(Level.SEVERE, "Erro ao inserir dados na base.", ex);
        }
    }
}
