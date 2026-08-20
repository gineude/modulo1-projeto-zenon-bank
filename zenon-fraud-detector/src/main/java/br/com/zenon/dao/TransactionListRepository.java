package br.com.zenon.dao;

import br.com.zenon.fraud.Transaction;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class TransactionListRepository implements TransactionRepository {

    private final List<Transaction> transactions;

    public TransactionListRepository(int limit) {
        String fileName = "PS_20174392719_1491204439457_log.csv";
        this.transactions = getTransactionsOldSchool(fileName, limit);
    }

    @Override
    public Optional<Transaction> findByOriginName(String name) {
        return transactions
                .stream()
                .filter(t -> t.origin().name().equals(name))
                .findFirst();
    }

    @Override
    public void save(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public List<Transaction> getTransactionsOldSchool(String arquivo, int quantidade) {
        List<Transaction> transactions = new ArrayList<>();

        Path path = Paths.get("../data", arquivo);

        try (InputStream IS = new FileInputStream(path.toFile()); Scanner scanner = new Scanner(IS)) {

            int quantidadeOriginal = quantidade;
            while (scanner.hasNext() && quantidade > 0) {
                String line = scanner.nextLine();
                quantidade--;
                if (quantidade == (quantidadeOriginal - 1)) {
                    continue;
                }
                Optional<Transaction> transactionOptional = TransactionRepositoryHelper.extractTransaction(line);
                transactionOptional.ifPresent(transactions::add);
            }

        } catch (IOException e) {
            System.out.println("Error : " + arquivo);
        }

        return transactions;
    }
}
