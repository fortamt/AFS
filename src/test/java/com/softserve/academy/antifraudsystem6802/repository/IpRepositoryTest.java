package com.softserve.academy.antifraudsystem6802.repository;

import com.softserve.academy.antifraudsystem6802.model.IpHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class IpRepositoryTest {

    @Autowired
    private IpRepository repository;

    String ip1 = "10.10.10.10";
    String ip2 = "240.20.21.20";

    @BeforeEach
    void setUp() {
        repository.save(new IpHolder(null, ip1));
        repository.save(new IpHolder(null, ip2));
    }

    @Test
    @DisplayName("The repository must return Ip in optional")
    void test1() {
        Optional<IpHolder> optionalIp = repository.findByIp(ip1);

        assertTrue(optionalIp.isPresent());
    }

    @Test
    @DisplayName("The repository must return empty optional")
    void test2() {
        String ip = "10.10.10.20";
        Optional<IpHolder> optionalIp = repository.findByIp(ip);

        assertFalse(optionalIp.isPresent());
    }

    @Test
    @DisplayName("The repository must return boolean value if ip exists in database")
    void test3() {
        boolean existIp = repository.existsByIp(ip2);

        assertTrue(existIp);
    }

    @Test
    @DisplayName("The repository must return empty optional")
    void test4() {
        int delete = repository.deleteByIp(ip2);

        assertThat(delete).isNotZero();
    }
}
