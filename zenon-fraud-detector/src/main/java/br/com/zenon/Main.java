package br.com.zenon;

import br.com.zenon.dao.TransactionIngestor;
import br.com.zenon.fraud.Customer;
import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionType;

import java.math.BigDecimal;
import java.util.List;

public class Main {

    static void main() {

        Customer origin1 = new Customer("C1231006815", new BigDecimal("170136.0"), new BigDecimal("160296.36"));
        Customer dest1 = new Customer("M1979787155", BigDecimal.ZERO, BigDecimal.ZERO);

        Transaction t1 = new Transaction(
                1,
                TransactionType.PAYMENT,
                new BigDecimal("9839.64"),
                origin1,
                dest1,
                false,
                false
        );

        // Transação 2
        Customer origin2 = new Customer("C1280323807", new BigDecimal("850002.52"), BigDecimal.ZERO);
        Customer dest2 = new Customer("C873221189", new BigDecimal("6510099.11"), new BigDecimal("7360101.63"));

        Transaction t2 = new Transaction(
                743,
                TransactionType.CASH_OUT,
                new BigDecimal("850002.52"),
                origin2,
                dest2,
                true,
                false
        );

        IO.println("Transação 1: " + t1);
        IO.println("Transação 2: " + t2);

        List<Transaction> transactions = TransactionIngestor.getTransactions("PS_20174392719_1491204439457_log.csv");
        for (int i = 0; i < 10; i++) {
            IO.println((i + 1) + " " + transactions.get(i));
        }
    }
}
