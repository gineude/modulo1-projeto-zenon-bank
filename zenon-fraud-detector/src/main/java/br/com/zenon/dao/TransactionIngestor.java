package br.com.zenon.dao;

import br.com.zenon.fraud.Transaction;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

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

    public void csvBatchInsert(String fileName) {
        String url = "jdbc:mysql://localhost:3306/zenon-fraud-dedector?rewriteBatchedStatements=true";
        String usuario = "root";
        String senha = "admin123";

        Path caminhoCsv = Paths.get("../data", fileName);

        String sql = """
                    INSERT INTO TRANSACTIONS (step, type, amount, nameOrig, oldbalanceOrg, newbalanceOrig,
                    nameDest, oldbalanceDest, newbalanceDest, isFraud, isFlaggedFraud)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        int tamanhoBatch = 1000;  // Lotes menores para otimizar memória e rede
        int limiteTotal = 10000; // Interrompe o arquivo após 10 mil inserções

        try (Connection conn = DriverManager.getConnection(url, usuario, senha);
             PreparedStatement pstmt = conn.prepareStatement(sql.trim());
             Stream<String> linhas = Files.lines(caminhoCsv)) {

            conn.setAutoCommit(false);

            int[] contador = {0};

            linhas.skip(1)
                    .limit(limiteTotal)
                    .forEach(linha -> {
                        try {

                            String[] campos = linha.split(",");

                            if (campos.length >= 3) {
                                pstmt.setString(1,  campos[0].trim());
                                pstmt.setString(2,  campos[1].trim());
                                pstmt.setString(3,  campos[2].trim());
                                pstmt.setString(4,  campos[3].trim());
                                pstmt.setString(5,  campos[4].trim());
                                pstmt.setString(6,  campos[5].trim());
                                pstmt.setString(7,  campos[6].trim());
                                pstmt.setString(8,  campos[7].trim());
                                pstmt.setString(9,  campos[8].trim());
                                pstmt.setString(10, campos[9].trim());
                                pstmt.setString(11, campos[10].trim());

                                pstmt.addBatch();
                                contador[0]++;

                                if (contador[0] % tamanhoBatch == 0) {
                                    pstmt.executeBatch();
                                }
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException("Erro ao adicionar registro ao lote: " + linha, e);
                        }
                    });

            if (contador[0] % tamanhoBatch != 0) {
                pstmt.executeBatch();
            }

            conn.commit();
            System.out.println("Processamento concluído. Total de registros inseridos: " + contador[0]);

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo CSV: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro na transação. Executando rollback... Motivo: " + e.getMessage());
        }

    }

}
