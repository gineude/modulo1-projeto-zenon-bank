package br.com.zenon;

import br.com.zenon.dao.TransactionIngestor;
import br.com.zenon.dao.TransactionRepository;
import br.com.zenon.dao.TransactionSQLRepository;
import br.com.zenon.fraud.Transaction;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class DBMain {

    static void main() {
        String fileName = "PS_20174392719_1491204439457_log.csv";

        TransactionIngestor transactionIngestor = new TransactionIngestor();

        long inicio = System.nanoTime();
        //transactionIngestor.csvBatchInsert(fileName);
        long fim = System.nanoTime();

        long result = fim - inicio;

        long emMiliSegundos = TimeUnit.NANOSECONDS.toMillis(result);
        System.out.println("A inserção do dados demorou: " + emMiliSegundos + "ms");

        System.out.println("---------------------------------------------------------------------------------------");
        TransactionRepository repository = new TransactionSQLRepository();

        Optional<Transaction> transaction = repository.findByOriginName("C1231006815");
        transaction.ifPresent(System.out::println);
        System.out.println();

        System.out.println("Buscando valor inexistente");
        transaction = repository.findByOriginName("C12345");
        transaction.ifPresent(System.out::println);
    }
}
