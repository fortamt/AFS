package com.softserve.academy.antifraudsystem6802.repository;

import com.softserve.academy.antifraudsystem6802.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByNumberAndDateBetween(@NotEmpty String number, LocalDateTime start, LocalDateTime end);
    boolean existsByTransactionId(Long id);
    boolean existsByNumber(String id);
    Transaction findByTransactionId(Long id);
    List<Transaction> findAllByNumber(String number);
}

