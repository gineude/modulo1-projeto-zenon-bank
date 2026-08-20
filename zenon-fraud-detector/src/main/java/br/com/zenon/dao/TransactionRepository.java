package br.com.zenon.dao;

import br.com.zenon.fraud.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    void save(Transaction transaction);
    Optional<Transaction> findByOriginName(String name);
}
