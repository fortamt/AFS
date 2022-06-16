package com.softserve.academy.antifraudsystem6802.repository;

import com.softserve.academy.antifraudsystem6802.model.RegionCodes;
import com.softserve.academy.antifraudsystem6802.model.entity.Transaction;
import com.softserve.academy.antifraudsystem6802.model.validator.Regexp;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByNumberAndDateBetween(@NotEmpty String number, LocalDateTime start, LocalDateTime end);
    boolean existsByTransactionId(Long id);
    boolean existsByNumber(String id);
    Optional<Transaction> findByTransactionId(Long id);
    List<Transaction> findAllByNumber(String number);
    long countDistinctByRegionAndDateBetween(@NotNull RegionCodes region, LocalDateTime date, LocalDateTime date2);
    long countDistinctByIpAndDateBetween(@NotEmpty @Pattern(regexp = Regexp.IP) String ip, LocalDateTime date, LocalDateTime date2);

}

