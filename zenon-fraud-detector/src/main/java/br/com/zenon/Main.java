package br.com.zenon;

import br.com.zenon.analyses.FraudAnalyzer;
import br.com.zenon.dao.TransactionIngestor;
import br.com.zenon.dao.TransactionRepository;
import br.com.zenon.fraud.Customer;
import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionType;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class Main {

    static void main() {

        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        String nameOrigin = "C1231006815";
        Customer origin1 = new Customer(nameOrigin, new BigDecimal("170136.0"), new BigDecimal("160296.36"));
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

        out.println("Transação 1: " + t1);
        out.println("Transação 2: " + t2);

        out.println("-------------------------------------------------------------------------");

        String fileName = "PS_20174392719_1491204439457_log.csv";
        //String fileName = "paysim_with_bad_data.csv";

        TransactionIngestor transactionIngestor = new TransactionIngestor();

        List<Transaction> transactions = transactionIngestor.readTransactionsOldSchool(fileName);

        extraindoOldSchoolJava(transactions);

        //extraindoNewSchoolJava(transactionsNewSchool);
        /*out.println("------------------------- Buscando por nome --------------------------");

        TransactionRepository repositoryList = new TransactionListRepository(100001);
        TransactionRepository repositoryMap = new TransactionMapRepository(100001);

        System.out.println("Com nome existente list");
        imprimindoBuscaWithList(out, "C1231006815", repositoryList);

        System.out.println("Com nome não existente list");
        imprimindoBuscaWithList(out, "C12345", repositoryList);

        System.out.println("------------------------------------------------------------------------------");

        System.out.println("Com nome existente map");
        imprimindoBuscaWithMap(out, "C1231006815", repositoryMap);

        System.out.println("Com nome não existente map");
        imprimindoBuscaWithMap(out, "C12345", repositoryMap);*/
    }

    private static void imprimindoBuscaWithList(PrintStream out, String nameFind, TransactionRepository repository) {
        benchmarkBusca(out, nameFind, repository);
    }

    private static void imprimindoBuscaWithMap(PrintStream out, String nameFind, TransactionRepository repository) {
        benchmarkBusca(out, nameFind, repository);
    }

    private static void benchmarkBusca(PrintStream out, String nameFind, TransactionRepository repository) {
        long inicio = System.nanoTime();
        Optional<Transaction> transaction = repository.findByOriginName(nameFind, 100001);
        if (transaction.isPresent()) {
            out.println(transaction.get());
            out.println();
        } else {
            out.println("Transação não encontrada para o cliente " + nameFind);
            out.println();
        }
        long fim = System.nanoTime();

        out.println("O tempo de duração da busca se deu em: " + (fim - inicio) + " nanos");
    }

    private static void extraindoNewSchoolJava(List<Transaction> transactions) {

        long inicio2 = System.currentTimeMillis();

        transactions.stream().limit(10).forEach(IO::println);
        long fin2 = System.currentTimeMillis();

        IO.println("Demorou " + (fin2 - inicio2) + " ms");
    }

    private static void extraindoOldSchoolJava(List<Transaction> transactions) {

        IO.println("Total de dados analisados: " + transactions.size());

        IO.println("-------------------------------------------------------------------------");

        FraudAnalyzer fraudAnalyzer = new FraudAnalyzer();
        IO.println("Total de Fraudes: " + fraudAnalyzer.qtdeFraudes(transactions));

        IO.println("Top 3 Fraudes de Maior Valor:");
        fraudAnalyzer.topThreeFrauds(transactions).forEach(IO::println);

        IO.println("Clientes Suspeitos:");
        fraudAnalyzer.topFiveNamesFrauds(transactions).forEach(IO::println);

        BigDecimal totalFrauds = fraudAnalyzer.sumTotalFrauds(transactions);

        IO.println("Prejuízo Total: " + totalFrauds);


        IO.println("Fraudes por Tipo: ");
        fraudAnalyzer.groupingTypeFrauds(transactions)
                .forEach((type, count) -> IO.println(" - " + type.name() + ": " + count));

    }
}
