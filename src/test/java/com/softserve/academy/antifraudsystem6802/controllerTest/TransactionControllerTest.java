package com.softserve.academy.antifraudsystem6802.controllerTest;

import com.softserve.academy.antifraudsystem6802.model.Role;
import com.softserve.academy.antifraudsystem6802.model.entity.User;
import com.softserve.academy.antifraudsystem6802.model.request.RequestLock;
import com.softserve.academy.antifraudsystem6802.model.request.RoleRequest;
import com.softserve.academy.antifraudsystem6802.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TransactionControllerTest {
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private MockMvc mockMvc;

    @Autowired
    UserService userService;
    private final String transactionApi = "/api/antifraud/transaction";
    private final String userApi = "/api/auth/user";
    private final String deleteApi = "/api/auth/user/*";
    private final String userListApi = "/api/auth/list";
    private final String lockApi = "/api/auth/access";
    private final String roleApi = "/api/auth/role";
    private final String ipApi = "/api/antifraud/suspicious-ip";
    private final String cardApi = "/api/antifraud/stolencard";
    private final String historyApi = "/api/antifraud/history";


    @BeforeEach
    void addUsers() {
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
    }

    @Test
    void transactionPostAmount() throws Exception {
        String tr1 = "{\n" +
                "  \"amount\": 1,\n" +
                "  \"ip\": \"192.168.1.1\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"EAP\",\n" +
                "  \"date\": \"2022-01-22T16:00:00\"\n" +
                "}";
        String tr4 = "{\n" +
                "  \"amount\": 201,\n" +
                "  \"ip\": \"192.168.1.1\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"EAP\",\n" +
                "  \"date\": \"2022-01-22T16:03:00\"\n" +
                "}";
        String tr7 = "{\n" +
                "  \"amount\": 1501,\n" +
                "  \"ip\": \"192.168.1.1\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"EAP\",\n" +
                "  \"date\": \"2022-01-22T16:06:00\"\n" +
                "}";



        String corr2IP = "{\n" +
                "  \"amount\": 1,\n" +
                "  \"ip\": \"192.168.1.3\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"EAP\",\n" +
                "  \"date\": \"2022-01-22T17:11:00\"\n" +
                "}";
        String corr22IP = "{\n" +
                "  \"amount\": 1,\n" +
                "  \"ip\": \"192.168.1.3\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"EAP\",\n" +
                "  \"date\": \"2022-01-22T17:12:00\"\n" +
                "}";
        String corr3IP = "{\n" +
                "  \"amount\": 1,\n" +
                "  \"ip\": \"192.168.1.4\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"EAP\",\n" +
                "  \"date\": \"2022-01-22T17:12:00\"\n" +
                "}";
        String corr4IP = "{\n" +
                "  \"amount\": 1,\n" +
                "  \"ip\": \"192.168.1.5\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"EAP\",\n" +
                "  \"date\": \"2022-01-22T17:13:00\"\n" +
                "}";
        String corr5IP = "{\n" +
                "  \"amount\": 2000,\n" +
                "  \"ip\": \"192.168.1.5\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"EAP\",\n" +
                "  \"date\": \"2022-01-22T17:14:00\"\n" +
                "}";
        String corr1Reg = "{\n" +
                "  \"amount\": 1,\n" +
                "  \"ip\": \"192.168.1.2\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"EAP\",\n" +
                "  \"date\": \"2022-01-21T17:10:00\"\n" +
                "}";
        String corr2Reg = "{\n" +
                "  \"amount\": 1,\n" +
                "  \"ip\": \"192.168.1.2\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"ECA\",\n" +
                "  \"date\": \"2022-01-21T17:11:00\"\n" +
                "}";
        String corr22Reg = "{\n" +
                "  \"amount\": 1,\n" +
                "  \"ip\": \"192.168.1.2\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"ECA\",\n" +
                "  \"date\": \"2022-01-21T17:12:00\"\n" +
                "}";
        String corr3Reg = "{\n" +
                "  \"amount\": 1,\n" +
                "  \"ip\": \"192.168.1.2\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"HIC\",\n" +
                "  \"date\": \"2022-01-21T17:13:00\"\n" +
                "}";
        String corr4Reg = "{\n" +
                "  \"amount\": 1,\n" +
                "  \"ip\": \"192.168.1.2\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"SSA\",\n" +
                "  \"date\": \"2022-01-21T17:14:00\"\n" +
                "}";
        String corr5Reg = "{\n" +
                "  \"amount\": 2000,\n" +
                "  \"ip\": \"192.168.1.2\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"SA\",\n" +
                "  \"date\": \"2022-01-21T17:15:00\"\n" +
                "}";

        String answerAllowed = "{\r\n" +
                "  \"result\" : \"ALLOWED\",\r\n" +
                "  \"info\" : \"none\"\r\n" +
                "}";

        String answerManualAmount = "{\r\n" +
                "  \"result\" : \"MANUAL_PROCESSING\",\r\n" +
                "  \"info\" : \"amount\"\r\n" +
                "}";

        String answerProhibitedAmount = "{\r\n" +
                "  \"result\" : \"PROHIBITED\",\r\n" +
                "  \"info\" : \"amount\"\r\n" +
                "}";



        String answerManualIpCorr = "{\r\n" +
                "  \"result\" : \"MANUAL_PROCESSING\",\r\n" +
                "  \"info\" : \"ip-correlation\"\r\n" +
                "}";

        String answerProhibitedIpCorr = "{\r\n" +
                "  \"result\" : \"PROHIBITED\",\r\n" +
                "  \"info\" : \"ip-correlation\"\r\n" +
                "}";
        String answerProhibitedCardNumberAndIpAndAmountAndIpCorr = "{\r\n" +
                "  \"result\" : \"PROHIBITED\",\r\n" +
                "  \"info\" : \"amount, card-number, ip, ip-correlation\"\r\n" +
                "}";
        String answerManualRegCorr = "{\r\n" +
                "  \"result\" : \"MANUAL_PROCESSING\",\r\n" +
                "  \"info\" : \"region-correlation\"\r\n" +
                "}";
        String answerProhibitedRegCorr = "{\r\n" +
                "  \"result\" : \"PROHIBITED\",\r\n" +
                "  \"info\" : \"region-correlation\"\r\n" +
                "}";

        String answerProhibitedCardNumberAndIpAndAmountAndRegCorr = "{\r\n" +
                "  \"result\" : \"PROHIBITED\",\r\n" +
                "  \"info\" : \"amount, card-number, ip, region-correlation\"\r\n" +
                "}";


        MvcResult result1 = this.mockMvc.perform(post(transactionApi).content(tr1).
                        with(httpBasic("john", "2222")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andReturn();
        Assertions.assertEquals(answerAllowed, result1.getResponse().getContentAsString());


        MvcResult result2 = this.mockMvc.perform(post(transactionApi).content(tr4).
                        with(httpBasic("john", "2222")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andReturn();
        Assertions.assertEquals(answerManualAmount, result2.getResponse().getContentAsString());

        MvcResult result3 = this.mockMvc.perform(post(transactionApi).content(tr7).
                        with(httpBasic("john", "2222")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andReturn();
        Assertions.assertEquals(answerProhibitedAmount, result3.getResponse().getContentAsString());

        this.mockMvc.perform(post(transactionApi).content(tr7).
                        with(httpBasic("admin", "1111")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isForbidden());

    }

    @Test
    void transactionPostWrongData() throws Exception {
        String trW1 = "{\n" +
                "  \"amount\": -1,\n" +
                "  \"ip\": \"192.168.1.1\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"EAP\",\n" +
                "  \"date\": \"2022-01-22T16:00:00\"\n" +
                "}";
        String trW2 = "{\n" +
                "  \"amount\": 0,\n" +
                "  \"ip\": \"192.168.1.1\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"EAP\",\n" +
                "  \"date\": \"2022-01-22T16:00:00\"\n" +
                "}";
        String trW3 = "{\n" +
                "  \"ip\": \"192.168.1.1\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"EAP\",\n" +
                "  \"date\": \"2022-01-22T16:00:00\"\n" +
                "}";
        String trW4 = "{\n" +
                "  \"amount\": \" \",\n" +
                "  \"ip\": \"192.168.1.1\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"EAP\",\n" +
                "  \"date\": \"2022-01-22T16:00:00\"\n" +
                "}";
        String trW5 = "{\n" +
                "  \"amount\": \"\",\n" +
                "  \"ip\": \"192.168.1.1\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"EAP\",\n" +
                "  \"date\": \"2022-01-22T16:00:00\"\n" +
                "}";
        String trW6 = "{ }";

        this.mockMvc.perform(post(transactionApi).content(trW1).
                        with(httpBasic("john", "2222")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());
    this.mockMvc.perform(post(transactionApi).content(trW2).
                        with(httpBasic("john", "2222")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());
    this.mockMvc.perform(post(transactionApi).content(trW3).
                        with(httpBasic("john", "2222")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());
    this.mockMvc.perform(post(transactionApi).content(trW4).
                        with(httpBasic("john", "2222")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());
    this.mockMvc.perform(post(transactionApi).content(trW5).
                        with(httpBasic("john", "2222")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());
    this.mockMvc.perform(post(transactionApi).content(trW6).
                        with(httpBasic("john", "2222")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());


    }

    @Test
    void transactionPostBlackList() throws Exception {
        String trP1 = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"ip\": \"192.168.1.67\",\n" +
                "  \"number\": \"4000008449433403\",\n" +
                "  \"region\": \"EAP\",\n" +
                "  \"date\": \"2022-01-22T16:00:00\"\n" +
                "}";
        String trP2 = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"ip\": \"192.168.1.1\",\n" +
                "  \"number\": \"4000003305160034\",\n" +
                "  \"region\": \"EAP\",\n" +
                "  \"date\": \"2022-01-22T16:00:00\"\n" +
                "}";
        String trP3 = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"ip\": \"192.168.1.67\",\n" +
                "  \"number\": \"4000003305160034\",\n" +
                "  \"region\": \"EAP\",\n" +
                "  \"date\": \"2022-01-22T16:00:00\"\n" +
                "}";
        String trP4 = "{\n" +
                "  \"amount\": 2000,\n" +
                "  \"ip\": \"192.168.1.67\",\n" +
                "  \"number\": \"4000003305160034\",\n" +
                "  \"region\": \"EAP\",\n" +
                "  \"date\": \"2022-01-22T16:00:00\"\n" +
                "}";
        String answerProhibitedIp = "{\r\n" +
                "  \"result\" : \"PROHIBITED\",\r\n" +
                "  \"info\" : \"ip\"\r\n" +
                "}";
        String answerProhibitedCardNumber = "{\r\n" +
                "  \"result\" : \"PROHIBITED\",\r\n" +
                "  \"info\" : \"card-number\"\r\n" +
                "}";
        String answerProhibitedCardNumberAndIp = "{\r\n" +
                "  \"result\" : \"PROHIBITED\",\r\n" +
                "  \"info\" : \"card-number, ip\"\r\n" +
                "}";
        String answerProhibitedCardNumberAndIpAndAmount = "{\r\n" +
                "  \"result\" : \"PROHIBITED\",\r\n" +
                "  \"info\" : \"amount, card-number, ip\"\r\n" +
                "}";
        MvcResult result1 = this.mockMvc.perform(post(transactionApi).content(trP1).
                        with(httpBasic("john", "2222")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andReturn();
        Assertions.assertEquals(answerProhibitedIp, result1.getResponse().getContentAsString());

        MvcResult result2 = this.mockMvc.perform(post(transactionApi).content(trP2).
                        with(httpBasic("john", "2222")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andReturn();
        Assertions.assertEquals(answerProhibitedCardNumber, result2.getResponse().getContentAsString());

        MvcResult result3 = this.mockMvc.perform(post(transactionApi).content(trP3).
                        with(httpBasic("john", "2222")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andReturn();
        Assertions.assertEquals(answerProhibitedCardNumberAndIp, result3.getResponse().getContentAsString());

        MvcResult result4 = this.mockMvc.perform(post(transactionApi).content(trP4).
                        with(httpBasic("john", "2222")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andReturn();
        Assertions.assertEquals(answerProhibitedCardNumberAndIpAndAmount, result4.getResponse().getContentAsString());


    }


}
