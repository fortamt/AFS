package com.softserve.academy.antifraudsystem6802.repository;

import com.softserve.academy.antifraudsystem6802.model.StolenCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StolenCardRepositoryTest {

    @Autowired
    private StolenCardRepository repository;

    String stolenCardNumber1 = "4735410922991992";
    String stolenCardNumber2 = "6242146850237385";

    @BeforeEach
    void setUp() {
        repository.save(new StolenCard(null, stolenCardNumber1));
        repository.save(new StolenCard(null, stolenCardNumber2));
    }

    @Test
    @DisplayName("The stolen card must be found by credit card number 4735410922991992")
    void test1() {
        StolenCard stolenCard = repository.findByNumber(stolenCardNumber1);

        assertEquals(1L, stolenCard.getId());
        assertEquals(stolenCardNumber1, stolenCard.getNumber());
    }

    @Test
    @DisplayName("The stolen card must not be found because credit card doesnt exists in database")
    void test2() {
        String cardNumber = "4735410922991995";
        StolenCard stolenCard = repository.findByNumber(cardNumber);

        assertNull(stolenCard);
    }

    @Test
    @DisplayName("The stolen card must exists in database")
    void test3() {
        boolean existsCard = repository.existsByNumber(stolenCardNumber1);

        assertTrue(existsCard);
    }

    @Test
    @DisplayName("The stolen card must not exists in database")
    void test4() {
        String cardNumber = "4735410922991995";
        boolean existsCard = repository.existsByNumber(cardNumber);

        assertFalse(existsCard);
    }

}
