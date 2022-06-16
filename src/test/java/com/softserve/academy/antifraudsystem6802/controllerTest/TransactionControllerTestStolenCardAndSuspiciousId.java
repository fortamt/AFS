package com.softserve.academy.antifraudsystem6802.controllerTest;

import com.softserve.academy.antifraudsystem6802.model.Role;
import com.softserve.academy.antifraudsystem6802.model.entity.User;
import com.softserve.academy.antifraudsystem6802.model.request.RequestLock;
import com.softserve.academy.antifraudsystem6802.model.request.RoleRequest;
import com.softserve.academy.antifraudsystem6802.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionControllerTestStolenCardAndSuspiciousId {
    private final String ipApi = "/api/antifraud/suspicious-ip";
    private final String cardApi = "/api/antifraud/stolencard";
    @Autowired
    UserService userService;
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private MockMvc mockMvc;

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

        RequestLock requestLock1 = new RequestLock();
        requestLock1.setUsername("daniel");
        requestLock1.setOperation("UNLOCK");
        userService.lock(requestLock1);
    }

    @Test
    public void addApi() throws Exception {
        String request1 = "{\n" +
                "  \"ip\": \"192.168.1.67\"\n" +
                "}";

        String request2 = "{\n" +
                "  \"ip\": \"192.168.351.66\"\n" +
                "}";
        String request3 = "{\n" +
                "  \"ip\": \"192.168.351.66\"\n" +
                "}";
        String request4 = "{\n" +
                "  \"ip\": \"192.168.1.66\"\n" +
                "}";
        String answer1 = "{\r\n" +
                "  \"id\" : 5,\r\n" +
                "  \"ip\" : \"192.168.1.67\"\r\n" +
                "}";
        String answer2 = "{\r\n" +
                "  \"id\" : 6,\r\n" +
                "  \"ip\" : \"192.168.1.66\"\r\n" +
                "}";

        this.mockMvc.perform(post(ipApi).content(request1).
                        with(httpBasic("admin", "1111")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isForbidden());


        MvcResult result1 = this.mockMvc.perform(post(ipApi).content(request1).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andReturn();
        Assertions.assertEquals(answer1, result1.getResponse().getContentAsString());

        this.mockMvc.perform(post(ipApi).content(request2).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());

        this.mockMvc.perform(post(ipApi).content(request3).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());

        MvcResult result2 = this.mockMvc.perform(post(ipApi).content(request4).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andReturn();
        Assertions.assertEquals(answer2, result2.getResponse().getContentAsString());
    }

    @Test
    public void deleteApi() throws Exception {
        createId("192.168.1.68");
        String ip = "192.168.1.68";
        String answer = "{\r\n" +
                "  \"status\" : \"IP 192.168.1.68 successfully removed!\"\r\n" +
                "}";

        MvcResult result = this.mockMvc.perform(delete(ipApi + "/" + ip).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).andReturn();
        Assertions.assertEquals(answer, result.getResponse().getContentAsString());
        this.mockMvc.perform(delete(ipApi + "/" + ip).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isNotFound());
        this.mockMvc.perform(delete(ipApi + "/192.168.1.").
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());
        this.mockMvc.perform(delete(ipApi + "/192.168.1.").
                        with(httpBasic("john", "2222")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isForbidden());
    }

    @Test
    public void getIp() throws Exception {
        String answer1 = "[ ]";

        MvcResult result1 = this.mockMvc.perform(get(ipApi).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).andReturn();
        Assertions.assertEquals(answer1, result1.getResponse().getContentAsString());
        createId("192.168.1.67");
        createId("192.168.1.66");

        String answer2 = "[ {\r\n" +
                "  \"id\" : 5,\r\n" +
                "  \"ip\" : \"192.168.1.67\"\r\n" +
                "}, {\r\n" +
                "  \"id\" : 6,\r\n" +
                "  \"ip\" : \"192.168.1.66\"\r\n" +
                "} ]";
        this.mockMvc.perform(get(ipApi).
                        with(httpBasic("admin", "1111")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isForbidden());
        MvcResult result2 = this.mockMvc.perform(get(ipApi).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).andReturn();
        Assertions.assertEquals(answer2, result2.getResponse().getContentAsString());
    }

    @Test
    public void addStolenCard() throws Exception {
        String request1 = "{\n" +
                "  \"number\": \"4000003305061034\"\n" +
                "}";
        String request2 = "{\n" +
                "  \"number\": \"400000330506103\"\n" +
                "}";
        String request3 = "{\n" +
                "  \"number\": \"4000003305061033\"\n" +
                "}";
        String answer1 = "{\r\n" +
                "  \"id\" : 5,\r\n" +
                "  \"number\" : \"4000003305061034\"\r\n" +
                "}";
        this.mockMvc.perform(post(cardApi).content(request1).
                        with(httpBasic("admin", "1111")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isForbidden());

        MvcResult result1 = this.mockMvc.perform(post(cardApi).content(request1).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andReturn();
        Assertions.assertEquals(answer1, result1.getResponse().getContentAsString());

        this.mockMvc.perform(post(cardApi).content(request1).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isConflict());

        this.mockMvc.perform(post(cardApi).content(request2).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());
        this.mockMvc.perform(post(cardApi).content(request3).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());
    }

    @Test
    public void deleteCard() throws Exception {
        createCard("4000008449433403");
        String card = "4000008449433403";
        String answer = "{\r\n" +
                "  \"status\" : \"Card 4000008449433403 successfully removed!\"\r\n" +
                "}";

        MvcResult result = this.mockMvc.perform(delete(cardApi + "/" + card).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).andReturn();
        Assertions.assertEquals(answer, result.getResponse().getContentAsString());
        this.mockMvc.perform(delete(cardApi + "/" + card).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isNotFound());
        this.mockMvc.perform(delete(cardApi + "/400000330506103").
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());

    }

    @Test
    public void getCard() throws Exception {
        String answer1 = "[ ]";

        MvcResult result1 = this.mockMvc.perform(get(cardApi).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).andReturn();
        Assertions.assertEquals(answer1, result1.getResponse().getContentAsString());

        createCard("4000008449433403");
        createCard("4000009455296122");

        String answer2 = "[ {\r\n" +
                "  \"id\" : 5,\r\n" +
                "  \"number\" : \"4000008449433403\"\r\n" +
                "}, {\r\n" +
                "  \"id\" : 6,\r\n" +
                "  \"number\" : \"4000009455296122\"\r\n" +
                "} ]";
        this.mockMvc.perform(get(cardApi).
                        with(httpBasic("admin", "1111")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isForbidden());
        MvcResult result2 = this.mockMvc.perform(get(cardApi).
                        with(httpBasic("daniel", "3333")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).andReturn();
        Assertions.assertEquals(answer2, result2.getResponse().getContentAsString());
    }

    private void createCard(String number) throws Exception {
        String request1 = "{\n" +
                "  \"number\": \"" + number + "\"\n" +
                "}";

        this.mockMvc.perform(post(cardApi).content(request1).
                with(httpBasic("daniel", "3333")).
                contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON));
    }

    private void createId(String id) throws Exception {
        String request1 = "{\n" +
                "  \"ip\": \"" + id + "\"\n" +
                "}";
        this.mockMvc.perform(post(ipApi).content(request1).
                with(httpBasic("daniel", "3333")).
                contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON));
    }


}
