package br.com.zenon.dao;

import br.com.zenon.fraud.Customer;
import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionIngestor {

    private static final Logger logger = Logger.getLogger(TransactionIngestor.class.getName());

    private TransactionIngestor() {
        // nao instanciavel
    }

    public static List<Transaction> getTransactions(String arquivo) {
        List<Transaction> transactions = new ArrayList<>();

        int milLinhas = 1000;
        Path path = Paths.get("../data", arquivo);

        try (InputStream IS = new FileInputStream(path.toFile())) {

            Scanner scanner = new Scanner(IS);
            while (scanner.hasNext() && milLinhas > 0) {
                String line = scanner.nextLine();
                Transaction transaction = etractTransaction(line);
                if (transaction != null) {
                    transactions.add(transaction);
                }
                milLinhas--;
            }

        } catch (IOException e) {
            logger.log(Level.WARNING, "Erro ao ler o arquivo: " + arquivo, e);
        }

        return transactions;
    }

    private static Transaction etractTransaction(String line) {
        String[] fields = line.split(",");
        if (!"step".equals(fields[0])) {
            int step = Integer.parseInt(fields[0]);
            BigDecimal amount = new BigDecimal(fields[2]);
            boolean isFraud = Boolean.parseBoolean(fields[9]);
            boolean isFlaggedFraud = Boolean.parseBoolean(fields[10]);

            TransactionType type = TransactionType.valueOf(fields[1]);

            Customer origin = new Customer(fields[3], new BigDecimal(fields[4]), new BigDecimal(fields[5]));
            Customer recipient = new Customer(fields[6], new BigDecimal(fields[7]), new BigDecimal(fields[8]));

            return new Transaction(step, type, amount, origin, recipient, isFraud, isFlaggedFraud);
        }
        return null;
    }
}
