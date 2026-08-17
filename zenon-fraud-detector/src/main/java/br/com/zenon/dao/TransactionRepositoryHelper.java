package br.com.zenon.dao;

import br.com.zenon.fraud.Customer;
import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionType;

import java.math.BigDecimal;
import java.util.Optional;

public class TransactionRepositoryHelper {

    public static Optional<Transaction> extractTransaction(String line) {

        try {

            String[] fields = line.split(",");

            int step = Integer.parseInt(validarStep(fields[0]));
            BigDecimal amount = new BigDecimal(validaBigDecimal(fields[2]));

            boolean isFraud = "1".trim().equals(fields[9]);
            boolean isFlaggedFraud = "1".trim().equals(fields[10]);

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

    private static String validarStep(String field) throws IllegalArgumentException {
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

    private static String validaBigDecimal(String field) throws IllegalArgumentException {
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
