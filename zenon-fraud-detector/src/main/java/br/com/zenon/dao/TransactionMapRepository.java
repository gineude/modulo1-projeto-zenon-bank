package br.com.zenon.dao;

import br.com.zenon.fraud.Customer;
import br.com.zenon.fraud.Transaction;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class TransactionMapRepository implements TransactionRepository {

    private final Map<String, Transaction> transactions;

    public TransactionMapRepository(int limit) {
        String fileName = "PS_20174392719_1491204439457_log.csv";
        transactions = transactionsOldSchool(fileName, limit);
    }

    @Override
    public Optional<Transaction> findByOriginName(String name) {
        Transaction transaction = transactions.get(name);
        if (transaction != null) {
            return Optional.of(transaction);
        }
        return Optional.empty();
    }

    @Override
    public void save(Transaction transaction) {
        this.transactions.putIfAbsent(transaction.origin().name(), transaction);
    }

    private Map<String, Transaction> transactionsOldSchool(String arquivo, int quantidade) {
        Path path = Paths.get("../data", arquivo);

        Map<String, Transaction> transactionMap = new HashMap<>();
        try (InputStream IS = new FileInputStream(path.toFile()); Scanner scanner = new Scanner(IS)) {

            int quantidadeOriginal = quantidade;
            while (scanner.hasNext() && quantidade > 0) {
                String line = scanner.nextLine();
                quantidade--;
                if (quantidade == (quantidadeOriginal - 1)) {
                    continue;
                }
                Optional<Transaction> transactionOptional = TransactionRepositoryHelper.extractTransaction(line);
                if (transactionOptional.isPresent()) {
                    Transaction transaction = transactionOptional.get();
                    Customer origin = transaction.origin();
                    transactionMap.put(origin.name(), transaction);
                }
            }

        } catch (IOException e) {
            System.out.println("Error : " + arquivo);
        }
        return transactionMap;
    }
}
