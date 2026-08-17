package br.com.zenon;

import br.com.zenon.reports.TransactionReport;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class ReportMain {

    static void main() {
        String fileName = "PS_20174392719_1491204439457_log.csv";

        TransactionReport transactionReport = new TransactionReport();

        Stream<String> lines = transactionReport.readTransactionsLazyLoad(fileName);

        System.out.println("Total de linhas: " + lines.skip(1).count());

        lines = transactionReport.readTransactionsLazyLoad(fileName);

        long totalFrauds = lines.skip(1).filter(line -> {
            String[] fields = line.split(",");
            return "1".trim().equals(fields[9]);
        }).count();
        System.out.println("Total de fraudes: " + totalFrauds);


        lines = transactionReport.readTransactionsLazyLoad(fileName);

        BigDecimal totalTransactions = lines.skip(1).map(l -> {
            String[] fields = l.split(",");
            return new BigDecimal(fields[2]);
        }).reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("Valor total transacionado: "  + totalTransactions);
    }
}
