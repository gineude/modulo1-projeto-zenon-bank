package br.com.zenon.dao;

import br.com.zenon.fraud.Transaction;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public List<Transaction> readTransactionsNewSchool(String fileName) {
        Path path = Paths.get("../data", fileName);
        try {
            List<String> lines = Files.readAllLines(path);
            return lines.stream()
                    .skip(1)
                    .map(TransactionRepositoryHelper::extractTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Erro ao ler o arquivo: " + fileName, e);
        }
        return null;
    }

    public List<Transaction> readTransactionsOldSchool(String arquivo) {
        List<Transaction> transactions = new ArrayList<>();

        Path path = Paths.get("../data", arquivo);

        try (InputStream IS = new FileInputStream(path.toFile()); Scanner scanner = new Scanner(IS)) {

            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.contains("step,type,amount,nameOrig")) {
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
