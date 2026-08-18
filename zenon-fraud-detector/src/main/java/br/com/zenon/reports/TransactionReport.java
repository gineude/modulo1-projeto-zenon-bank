package br.com.zenon.reports;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

public class TransactionReport {

    public Statistics readTransactionsLazyLoad(String arquivo) {
        Path path = Paths.get("../data", arquivo);
        try (Stream<String> lines = Files.lines(path)) {
            return lines
                    .skip(1)
                    .map(this::parseTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .reduce(Statistics.ZERO, Statistics::addReportTransaction, Statistics::add);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo: " + arquivo, e);
        }
    }

    public Optional<ReportTransaction> parseTransaction(String line) {
        try {
            String[] parts = line.split(",");
            if (parts[2] == null || parts[2].trim().isEmpty())
                throw new IllegalArgumentException("A valor de amount não pode ser nulo ou vazio");
            BigDecimal amount = new BigDecimal(parts[2]);
            boolean isFraude = "1".equals(parts[9]);
            return Optional.of(new ReportTransaction(amount, isFraude));
        } catch (Exception e) {
            System.out.println("Erro ao ler arquivo: " + line);
            return Optional.empty();
        }
    }
}
