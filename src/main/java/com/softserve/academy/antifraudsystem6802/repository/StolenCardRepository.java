package com.softserve.academy.antifraudsystem6802.repository;

import com.softserve.academy.antifraudsystem6802.model.StolenCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StolenCardRepository extends JpaRepository<StolenCard, Long> {
    StolenCard findByNumber(String number);
    boolean existsByNumber(String number);
}
