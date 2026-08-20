package br.com.zenon.dao;

import br.com.zenon.fraud.Customer;
import br.com.zenon.fraud.Transaction;
import br.com.zenon.fraud.TransactionType;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Optional;

public class TransactionSQLRepository implements TransactionRepository {

    @Override
    public Optional<Transaction> findByOriginName(String name, int limit) {
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
