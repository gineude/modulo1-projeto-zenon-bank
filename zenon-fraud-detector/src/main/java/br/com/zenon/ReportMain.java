package br.com.zenon;

import br.com.zenon.reports.TransactionReport;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.text.MessageFormat.format;

public class ReportMain {

    static void main() {
        String fileName = "PS_20174392719_1491204439457_log.csv";

        var transactionReport = new TransactionReport();
        var st = transactionReport.readTransactionsLazyLoad(fileName);

        Locale localeUs = Locale.US;
        Locale localePtBr = Locale.of("pt", "BR");

        ResourceBundle bundleUs = ResourceBundle.getBundle("report", localeUs);
        NumberFormat numberFormatUsMoeda = DecimalFormat.getCurrencyInstance(localeUs);
        NumberFormat numberFormatUsInteiro = NumberFormat.getIntegerInstance(localeUs);

        String totalAmount = numberFormatUsMoeda.format(st.totalAmount());
        String totalFrauds = numberFormatUsInteiro.format(st.totalFrauds());
        String totalTransactions = numberFormatUsInteiro.format(st.totalTransactions());

        String totalLinhas = format(bundleUs.getString("total.de.linhas"), totalTransactions);
        String totalFraudes = format(bundleUs.getString("total.de.fraudes"), totalFrauds);
        String totalTransacionado = format(bundleUs.getString("valor.total.transacionado"), totalAmount);

        System.out.println("Imprimindo em US");
        System.out.println(totalLinhas);
        System.out.println(totalFraudes);
        System.out.println(totalTransacionado);

        var bundlePtBr = ResourceBundle.getBundle("report", localePtBr);
        NumberFormat numberFormatPtBrMoeda = DecimalFormat.getCurrencyInstance(localePtBr);
        numberFormatPtBrMoeda.setCurrency(Currency.getInstance("USD"));
        NumberFormat numberFormatPtBrInteiro = NumberFormat.getIntegerInstance(localePtBr);

        String totalAmountPt = numberFormatPtBrMoeda.format(st.totalAmount());
        String totalFraudsPt = numberFormatPtBrInteiro.format(st.totalFrauds());
        String totalTransactionsPt = numberFormatPtBrInteiro.format(st.totalTransactions());

        String totalLinhasPt = format(bundlePtBr.getString("total.de.linhas"), totalTransactionsPt);
        String totalFraudesPt = format(bundlePtBr.getString("total.de.fraudes"), totalFraudsPt);
        String totalTransacionadoPt = format(bundlePtBr.getString("valor.total.transacionado"), totalAmountPt);

        System.out.println("Imprimindo em PT");
        System.out.println(totalLinhasPt);
        System.out.println(totalFraudesPt);
        System.out.println(totalTransacionadoPt);


    }
}
