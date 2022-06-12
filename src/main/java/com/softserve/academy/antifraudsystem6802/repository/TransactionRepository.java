package com.softserve.academy.antifraudsystem6802.repository;

import com.softserve.academy.antifraudsystem6802.model.request.TransactionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionRequest, Long> {
//    @Query("SELECT count(tr.region) FROM TransactionRequest AS tr WHERE tr.number = :number AND " +
//            "tr.date BETWEEN :startDate AND :endDate GROUP BY tr.region")
//    long calculateRegions(@Param("startDate") LocalDateTime startDate,
//                              @Param("endDate") LocalDateTime endDate,
//                              @Param("number") String number);
//
//    @Query("SELECT count(tr.id) FROM TransactionRequest AS tr WHERE tr.number = :number AND " +
//            "tr.date BETWEEN :startDate AND :endDate GROUP BY tr.id")
//    long calculateId(@Param("startDate") LocalDateTime startDate,
//                     @Param("endDate") LocalDateTime endDate,
//                     @Param("number") String number);

    List<TransactionRequest> findAllByNumberAndDateBetween(String number, LocalDateTime startDate, LocalDateTime endDate);
}
