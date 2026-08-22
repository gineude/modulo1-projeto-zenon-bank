package br.com.zenon.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelTransactionIngestor {

    private static final String URL = "jdbc:mysql://localhost:3306/zenon-fraud-dedector?rewriteBatchedStatements=true";
    private static final String USUARIO = "root";
    private static final String SENHA = "admin123";

    private static final String SQL = """
                INSERT INTO TRANSACTIONS (step, type, amount, nameOrig, oldbalanceOrg, newbalanceOrig,
                nameDest, oldbalanceDest, newbalanceDest, isFraud, isFlaggedFraud)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final int TAMANHO_BATCH = 10_000;
    private static final int THREADS = 10;

    public void readAsStreamParallel() throws IOException, InterruptedException {
        String fileName = "PS_20174392719_1491204439457_log.csv";
        Path caminhoCsv = Paths.get("../data", fileName);

        // Executor com fila delimitada (evita OutOfMemory) e política de throttling
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                THREADS, THREADS,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(20), // Máximo de 20 lotes na fila
                new ThreadPoolExecutor.CallerRunsPolicy() // Freia a leitura se a fila encher
        );

        try (BufferedReader reader = Files.newBufferedReader(caminhoCsv)) {
            reader.readLine(); // Pula o cabeçalho

            String linha;
            List<String> linhasBatch = new ArrayList<>(TAMANHO_BATCH);

            while ((linha = reader.readLine()) != null) {
                linhasBatch.add(linha);

                if (linhasBatch.size() == TAMANHO_BATCH) {
                    List<String> chunk = List.copyOf(linhasBatch);
                    executor.submit(() -> processarChunk(chunk));
                    linhasBatch.clear();
                }
            }

            // Envia o último lote remanescente (que não atingiu 10.000 linhas)
            if (!linhasBatch.isEmpty()) {
                List<String> chunk = List.copyOf(linhasBatch);
                executor.submit(() -> processarChunk(chunk));
            }
        } finally {
            // Encerra e aguarda o término de TODAS as tasks submetidas
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);
        }
    }

    private void processarChunk(List<String> linhas) {
        try (Connection conn = DriverManager.getConnection(URL, USUARIO, SENHA);
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            conn.setAutoCommit(false);

            for (String linha : linhas) {
                String[] campos = linha.split(",");

                ps.setString(1, campos[0].trim());
                ps.setString(2, campos[1].trim());
                ps.setString(3, campos[2].trim());
                ps.setString(4, campos[3].trim());
                ps.setString(5, campos[4].trim());
                ps.setString(6, campos[5].trim());
                ps.setString(7, campos[6].trim());
                ps.setString(8, campos[7].trim());
                ps.setString(9, campos[8].trim());
                ps.setString(10, campos[9].trim());
                ps.setString(11, campos[10].trim());

                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();
            System.out.printf("Thread %s concluiu %d registros%n", Thread.currentThread().getName(), linhas.size());

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao processar chunk", e);
        }
    }
}