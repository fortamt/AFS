package com.softserve.academy.antifraudsystem6802.controllerTest;

import com.softserve.academy.antifraudsystem6802.controller.TransactionController;
import com.softserve.academy.antifraudsystem6802.model.RegionCodes;
import com.softserve.academy.antifraudsystem6802.model.Result;
import com.softserve.academy.antifraudsystem6802.model.Role;
import com.softserve.academy.antifraudsystem6802.model.entity.Transaction;
import com.softserve.academy.antifraudsystem6802.model.entity.User;
import com.softserve.academy.antifraudsystem6802.model.request.RequestLock;
import com.softserve.academy.antifraudsystem6802.model.request.RoleRequest;
import com.softserve.academy.antifraudsystem6802.repository.TransactionRepository;
import com.softserve.academy.antifraudsystem6802.service.TransactionService;
import com.softserve.academy.antifraudsystem6802.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TransactionControllerTestFeedback {

    private final String transactionApi = "/api/antifraud/transaction";
    private final String historyApi = "/api/antifraud/history";

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private MockMvc mockMvc;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    UserService userService;

    @Autowired
    TransactionService transactionService;

    private final String tr1 = "{\n" +
            "  \"amount\": 210,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:04:00\"\n" +
            "}";
    private final String tr2 = "{\n" +
            "  \"amount\": 100,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:05:00\"\n" +
            "}";

    @BeforeEach
    void addUsers() throws Exception {
        User user1 = new User();
        user1.setName("Administrator");
        user1.setUsername("admin");
        user1.setPassword("1111");
        userService.register(user1);

        User user2 = new User();
        user2.setName("John Black");
        user2.setUsername("john");
        user2.setPassword("2222");
        userService.register(user2);

        User user3 = new User();
        user3.setName("Daniel White");
        user3.setUsername("daniel");
        user3.setPassword("3333");
        userService.register(user3);

        User user4 = new User();
        user4.setName("Brenda Walsh");
        user4.setUsername("brenda");
        user4.setPassword("4444");
        userService.register(user4);

        RequestLock requestLock = new RequestLock();
        requestLock.setUsername("john");
        requestLock.setOperation("UNLOCK");
        userService.lock(requestLock);

        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setUsername("daniel");
        roleRequest.setRole(Role.SUPPORT);
        userService.changeRole(roleRequest);

        RequestLock requestLock1 = new RequestLock();
        requestLock1.setUsername("daniel");
        requestLock1.setOperation("UNLOCK");
        userService.lock(requestLock1);
    }

    @Test
    public void transactionFeedback() throws Exception {
        createTransaction(tr1);
        createTransaction(tr2);

        String req1 = "{\n" +
                "  \"transactionId\": \"5\",\n" +
                "  \"feedback\": \"ALLOWED\"\n" +
                "}";

        String req2 = "{\n" +
                "  \"transactionId\": \"6\",\n" +
                "  \"feedback\": \"ALLOWED\"\n" +
                "}";

        String req3 = "{\n" +
                "  \"transactionId\": \"6\",\n" +
                "  \"feedback\": \"MAY BE OK\"\n" +
                "}";
        String req4 = "{\n" +
                "  \"transactionId\": \"5\",\n" +
                "  \"feedback\": \"PROHIBITED\"\n" +
                "}";
        String response1 = "{\r\n" +
                "  \"transactionId\" : 5,\r\n" +
                "  \"amount\" : 210,\r\n" +
                "  \"ip\" : \"192.168.1.1\",\r\n" +
                "  \"number\" : \"4000008449433403\",\r\n" +
                "  \"region\" : \"EAP\",\r\n" +
                "  \"date\" : \"2022-01-22T16:04:00\",\r\n" +
                "  \"result\" : \"MANUAL_PROCESSING\",\r\n" +
                "  \"feedback\" : \"ALLOWED\"\r\n" +
                "}";

        this.mockMvc.perform(put(transactionApi).content(req1).
                        with(httpBasic("john", "2222")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isForbidden());

        MvcResult result1 = this.mockMvc.perform(put(transactionApi).content(req1).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).andReturn();
        Assertions.assertEquals(response1, result1.getResponse().getContentAsString());

        this.mockMvc.perform(put(transactionApi).content(req2).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnprocessableEntity());

        this.mockMvc.perform(put(transactionApi).content(req3).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());

        this.mockMvc.perform(put(transactionApi).content(req4).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isConflict());
    }


    @Test
    public void getHistory() throws Exception {
              createTransaction(tr1);
              createTransaction(tr2);
        String response1 = "[ {\r\n" +
                "  \"transactionId\" : 5,\r\n" +
                "  \"amount\" : 210,\r\n" +
                "  \"ip\" : \"192.168.1.1\",\r\n" +
                "  \"number\" : \"4000008449433403\",\r\n" +
                "  \"region\" : \"EAP\",\r\n" +
                "  \"date\" : \"2022-01-22T16:04:00\",\r\n" +
                "  \"result\" : \"MANUAL_PROCESSING\",\r\n" +
                "  \"feedback\" : \"\"\r\n" +
                "}, {\r\n" +
                "  \"transactionId\" : 6,\r\n" +
                "  \"amount\" : 100,\r\n" +
                "  \"ip\" : \"192.168.1.1\",\r\n" +
                "  \"number\" : \"4000008449433403\",\r\n" +
                "  \"region\" : \"EAP\",\r\n" +
                "  \"date\" : \"2022-01-22T16:05:00\",\r\n" +
                "  \"result\" : \"ALLOWED\",\r\n" +
                "  \"feedback\" : \"\"\r\n" +
                "} ]";

       MvcResult result1 = this.mockMvc.perform(get(historyApi).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).andReturn();
        Assertions.assertEquals(response1, result1.getResponse().getContentAsString());

        this.mockMvc.perform(get(historyApi).
                        with(httpBasic("john", "2222")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isForbidden());

        this.mockMvc.perform(get(historyApi  + "/4000008449433402").
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());


    }

    private void createTransaction(String tr) throws Exception {
        this.mockMvc.perform(post(transactionApi).content(tr).
                with(httpBasic("john", "2222")).
                contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON));
    }
}
