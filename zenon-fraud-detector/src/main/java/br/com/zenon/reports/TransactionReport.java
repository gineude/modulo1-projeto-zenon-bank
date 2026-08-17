package br.com.zenon.reports;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class TransactionReport {

    public Stream<String> readTransactionsLazyLoad(String arquivo) {
        Path path = Paths.get("../data", arquivo);
        try {
            return Files.lines(path);
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo: " + arquivo + " " + e.getMessage());
            return Stream.empty();
        }
    }
}
