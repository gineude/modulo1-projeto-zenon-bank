package br.com.zenon.reports;

import java.math.BigDecimal;

public record Statistics(long totalTransactions, long totalFrauds, BigDecimal totalAmount) {

    public final static Statistics ZERO = new Statistics(0, 0, BigDecimal.ZERO);

    public Statistics addReportTransaction(ReportTransaction rt) {
        return new Statistics(totalTransactions + 1,
                totalFrauds + (rt.isFraud() ? 1 : 0),
                totalAmount.add(rt.amount()));
    }

    public Statistics add(Statistics other) {
        return new Statistics(
                totalTransactions + other.totalTransactions(),
                totalFrauds + other.totalFrauds(),
                totalAmount.add(other.totalAmount())
        );
    }
}
