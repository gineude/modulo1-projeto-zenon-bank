package br.com.zenon;

import br.com.zenon.reports.Statistics;
import br.com.zenon.reports.TransactionReport;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class ReportMain {

    static void main() {
        String fileName = "PS_20174392719_1491204439457_log.csv";

        var transactionReport = new TransactionReport();
        var st = transactionReport.readTransactionsLazyLoad(fileName);

        System.out.printf("""
                Total de linhas: %d
                Total de fraudes: %d
                Valor total transacionado: %.2f
                %n""", st.totalTransactions(), st.totalFrauds(), st.totalAmount());
    }
}
