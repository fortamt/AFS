package com.softserve.academy.antifraudsystem6802;

import com.softserve.academy.antifraudsystem6802.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class AntiFraudSystem6802ApplicationTests {
   @Autowired
   private UserController userController;
    @Test
    void contextLoads() {
        assertThat(userController).isNotNull();
    }

}
