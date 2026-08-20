package br.com.zenon.dao;

import br.com.zenon.fraud.Customer;
import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionType;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Optional;

public class TransactionSQLRepository implements TransactionRepository {

    @Override
    public void save(Transaction transaction) {
        String url = "jdbc:mysql://localhost:3306/zenon-fraud-dedector?rewriteBatchedStatements=true";
        String usuario = "root";
        String senha = "admin123";

        String sql = """
                    INSERT INTO transactions (step, `type`, amount, nameOrig, oldbalanceOrg, newbalanceOrig,
                        nameDest, oldbalanceDest, newbalanceDest, isFraud, isFlaggedFraud)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    WHERE nameOrig = ?
                """;

        try (Connection conn = DriverManager.getConnection(url, usuario, senha);
             PreparedStatement ps = conn.prepareStatement(sql.trim())) {
            ps.setInt(1, transaction.step());
            ps.setString(2, transaction.type().name());
            ps.setBigDecimal(3, transaction.amount());
            ps.setString(4, transaction.origin().name());
            ps.setBigDecimal(5, transaction.origin().newBalance());
            ps.setBigDecimal(6, transaction.origin().oldBalance());
            ps.setString(7, transaction.destination().name());
            ps.setBigDecimal(8, transaction.destination().newBalance());
            ps.setBigDecimal(9, transaction.destination().oldBalance());
            ps.setBoolean(10, transaction.isFraud());
            ps.setBoolean(11, transaction.isFlaggedFraud());

            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar registro na base de dados", e);
        }
    }

    @Override
    public Optional<Transaction> findByOriginName(String name) {
        String url = "jdbc:mysql://localhost:3306/zenon-fraud-dedector?rewriteBatchedStatements=true";
        String usuario = "root";
        String senha = "admin123";

        String sql = """
                    SELECT step, type, amount, nameOrig, oldbalanceOrg, newbalanceOrig,
                    nameDest, oldbalanceDest, newbalanceDest, isFraud, isFlaggedFraud FROM TRANSACTIONS
                    WHERE nameOrig = ?
                """;

        try (Connection conn = DriverManager.getConnection(url, usuario, senha);
             PreparedStatement ps = conn.prepareStatement(sql.trim())) {
            ps.setString(1, name);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int step = rs.getInt("step");
                TransactionType type = TransactionType.valueOf(rs.getString("type"));
                BigDecimal amount = rs.getBigDecimal("amount");

                String nameOrig = rs.getString("nameOrig");
                BigDecimal oldbalanceOrg = rs.getBigDecimal("oldbalanceOrg");
                BigDecimal newbalanceOrig = rs.getBigDecimal("newbalanceOrig");
                Customer origin = new Customer(nameOrig, oldbalanceOrg, newbalanceOrig);

                String nameDest = rs.getString("nameDest");
                BigDecimal oldbalanceDest = rs.getBigDecimal("oldbalanceDest");
                BigDecimal newbalanceDest = rs.getBigDecimal("newbalanceDest");
                Customer recipient = new Customer(nameDest, oldbalanceDest, newbalanceDest);

                boolean isFraud = rs.getBoolean("isFraud");
                boolean isFlaggedFraud = rs.getBoolean("isFlaggedFraud");
                return Optional.of(new Transaction(step, type, amount, origin, recipient, isFraud, isFlaggedFraud));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar registro na base de dados", e);
        }
        return Optional.empty();
    }
}
