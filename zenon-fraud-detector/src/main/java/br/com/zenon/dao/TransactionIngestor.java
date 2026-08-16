package br.com.zenon.dao;

import br.com.zenon.fraud.Customer;
import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionIngestor {

    private static final Logger logger = Logger.getLogger(TransactionIngestor.class.getName());

    /*public List<Transaction> readTransactions(String fileName) {
        Path path = Paths.get("../data", fileName);
        try {
            List<String> lines = Files.readAllLines(path);
            return lines.stream()
                    .skip(1)
                    .limit(1000)
                    .map(this::extractTransaction)
                    .toList();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Erro ao ler o arquivo: " + fileName, e);
        }
        return null;
    }*/

    public List<Transaction> getTransactionsOldSchool(String arquivo) {
        List<Transaction> transactions = new ArrayList<>();

        int milLinhas = 0;
        Path path = Paths.get("../data", arquivo);

        try (InputStream IS = new FileInputStream(path.toFile()); Scanner scanner = new Scanner(IS)) {

            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                milLinhas++;
                if (milLinhas == 1) {
                    continue;
                }
                Optional<Transaction> transactionOptional = extractTransaction(line);
                transactionOptional.ifPresent(transactions::add);
            }

        } catch (IOException e) {
            System.out.println("Error : " + arquivo);
        }

        return transactions;
    }

    private Optional<Transaction> extractTransaction(String line) {

        try {

            String[] fields = line.split(",");

            int step = Integer.parseInt(validarStep(fields[0]));
            BigDecimal amount = new BigDecimal(validaBigDecimal(fields[2]));

            boolean isFraud = Boolean.parseBoolean(fields[9]);
            boolean isFlaggedFraud = Boolean.parseBoolean(fields[10]);

            TransactionType type = TransactionType.valueOf(fields[1]);

            BigDecimal oldBalance = new BigDecimal(validaBigDecimal(fields[4]));
            BigDecimal newBalance = new BigDecimal(validaBigDecimal(fields[5]));

            var origin = new Customer(fields[3], oldBalance, newBalance);

            BigDecimal oldBalanceRecipient = new BigDecimal(validaBigDecimal(fields[7]));
            BigDecimal newBalanceRecipient = new BigDecimal(validaBigDecimal(fields[8]));

            var recipient = new Customer(fields[6], oldBalanceRecipient, newBalanceRecipient);

            return Optional.of(new Transaction(step, type, amount, origin, recipient, isFraud, isFlaggedFraud));
        } catch (IllegalArgumentException e) {
            System.err.println("Error : " + line);
            return Optional.empty();
        }
    }

    private String validarStep(String field) throws IllegalArgumentException {
        try {
            int valor = Integer.parseInt(field);
            if (valor <= 0) {
                throw new IllegalArgumentException();
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }
        return field;
    }

    private String validaBigDecimal(String field) throws IllegalArgumentException {
        try {
            BigDecimal valor = new BigDecimal(field);
            if (valor.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException();
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }
        return field;
    }

}
