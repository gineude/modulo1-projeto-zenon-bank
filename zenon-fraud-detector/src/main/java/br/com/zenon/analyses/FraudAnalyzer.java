package br.com.zenon.analyses;

import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionType;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FraudAnalyzer {

    public long qtdeFraudes(List<Transaction> transactions) {
        return transactions.stream().filter(Transaction::isFraud).count();
    }

    public List<String> topThreeFrauds(List<Transaction> transactions) {
        return transactions
                .stream()
                .filter(Transaction::isFraud)
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .limit(3)
                .map(t -> t.amount().toPlainString())
                .toList();
    }

    public List<String> topFiveNamesFrauds(List<Transaction> transactions) {
        return transactions
                .stream()
                .filter(Transaction::isFraud)
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .limit(5)
                .map(t -> t.origin().name())
                .toList();
    }

    public BigDecimal sumTotalFrauds(List<Transaction> transactions) {
        return transactions
                .stream()
                .filter(Transaction::isFraud)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<TransactionType, Long> groupingTypeFrauds(List<Transaction> transactions) {
        return transactions
                .stream()
                .filter(Transaction::isFraud)
                .collect(Collectors.groupingBy(
                        Transaction::type,
                        Collectors.counting()));
    }
}
