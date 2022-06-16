package com.softserve.academy.antifraudsystem6802.repository;

import com.softserve.academy.antifraudsystem6802.model.RegionCodes;
import com.softserve.academy.antifraudsystem6802.model.Result;
import com.softserve.academy.antifraudsystem6802.model.entity.Transaction;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository repository;

    @BeforeEach
    void setUp() {
        repository.save(new Transaction(
                null,
                1000L,
                "10.10.10.10",
                "4735410922991992",
                RegionCodes.ECA,
                LocalDateTime.of(2022, 12, 30, 18, 20),
                Result.MANUAL_PROCESSING.name(),
                "none"));
        repository.save(new Transaction(
                null,
                200L,
                "250.10.180.10",
                "6242146850237385",
                RegionCodes.HIC,
                LocalDateTime.of(2022, 6, 2, 15, 0),
                Result.ALLOWED.name(),
                "none"));
        repository.save(new Transaction(
                null,
                1700L,
                "250.0.180.33",
                "6242146850237385",
                RegionCodes.LAC,
                LocalDateTime.of(2022, 9, 5, 12, 5),
                Result.PROHIBITED.name(),
                "amount"));
    }

    @Test
    @DisplayName("The database must contain three rows")
    void test1() {
        boolean existsFirstId = repository.existsByTransactionId(1L);
        boolean existsSecondId = repository.existsByTransactionId(2L);
        boolean existsThirdId = repository.existsByTransactionId(3L);
        boolean existsFourthId = repository.existsByTransactionId(4L);

        assertTrue(existsFirstId);
        assertTrue(existsSecondId);
        assertTrue(existsThirdId);
        assertFalse(existsFourthId);
    }

    @Test
    @DisplayName("The database must contain credit card number")
    void test2() {
        String cardNumber = "6242146850237385";
        boolean existsNumber = repository.existsByNumber(cardNumber);

        assertTrue(existsNumber);
    }

    @Test
    @DisplayName("The database must contain 2 transactions in date between 2022/5/1-2022/10/1")
    void test3() {
        String cardNumber = "6242146850237385";

        List<Transaction> transactions = repository.findAllByNumberAndDateBetween(
                cardNumber,
                LocalDateTime.of(2022, 5, 1, 0, 0),
                LocalDateTime.of(2022, 10, 1, 0, 0));

        assertEquals(2, transactions.size());
        assertEquals(cardNumber, transactions.get(0).getNumber());
        assertEquals(cardNumber, transactions.get(1).getNumber());
    }

    @Test
    @DisplayName("The database must contain 1 transactions in date between 2022/11/1-2022/12/31")
    void test4() {
        String cardNumber = "4735410922991992";

        List<Transaction> transactions = repository.findAllByNumberAndDateBetween(
                cardNumber,
                LocalDateTime.of(2022, 11, 1, 0, 0),
                LocalDateTime.of(2022, 12, 31, 0, 0));

        assertEquals(1, transactions.size());
        assertEquals(cardNumber, transactions.get(0).getNumber());
    }

    @Test
    @DisplayName("The database must contain 2 transaction with card number 6242146850237385")
    void test5() {
        String cardNumber = "6242146850237385";

        List<Transaction> transactions = repository.findAllByNumber(cardNumber);

        assertEquals(2, transactions.size());
        assertEquals(cardNumber, transactions.get(0).getNumber());
        assertEquals(cardNumber, transactions.get(1).getNumber());
    }

    @Test
    @DisplayName("The database must contain 1 transaction with card number 4735410922991992")
    void test6() {
        String cardNumber = "4735410922991992";

        List<Transaction> transactions = repository.findAllByNumber(cardNumber);

        assertEquals(1, transactions.size());
        assertEquals(cardNumber, transactions.get(0).getNumber());
    }
}
