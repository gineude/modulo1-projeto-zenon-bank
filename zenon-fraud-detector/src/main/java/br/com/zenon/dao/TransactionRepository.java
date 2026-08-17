package br.com.zenon.dao;

import br.com.zenon.fraud.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Optional<Transaction> findByOriginName(String name, int limit);
}
