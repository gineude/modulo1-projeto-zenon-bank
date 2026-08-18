package br.com.zenon.reports;

import java.math.BigDecimal;

public record ReportTransaction(BigDecimal amount, boolean isFraud) {
}
