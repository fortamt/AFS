package com.softserve.academy.antifraudsystem6802.controllerTest;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.springframework.test.web.servlet.MvcResult;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private MockMvc mockMvc;


    private final String transactionApi = "/api/antifraud/transaction";
    private final String userApi = "/api/auth/user";
    private final String userListApi = "/api/auth/list";
    private final String lockApi = "/api/auth/access";
    private final String roleApi = "/api/auth/role";


    private String admin = "{\n" +
            "   \"name\": \"administrator\",\n" +
            "   \"username\": \"admin\",\n" +
            "   \"password\": \"1111\"\n" +
            "}";
    private String johndoe1 = "{\n" +
            "   \"name\": \"John Doe 1\",\n" +
            "   \"username\": \"johndoe1\",\n" +
            "   \"password\": \"oMoa3VvqnLxW\"\n" +
            "}";
    private final String johndoe2 = "{\n" +
            "   \"name\": \"John Doe 2\",\n" +
            "   \"username\": \"johndoe2\",\n" +
            "   \"password\": \"oMoa3VvqnLxW\"\n" +
            "}";

    private final String marry = "{\n" +
            "   \"name\": \"Maria Chernysh\",\n" +
            "   \"username\": \"marry\",\n" +
            "   \"password\": \"1234\"\n" +
            "}";

    private final String johndoe2Upper = "{\n" +
            "   \"name\": \"John Doe 2\",\n" +
            "   \"username\": \"Johnddoe2\",\n" +
            "   \"password\": \"oMoa3VvqnLxW\"\n" +
            "}";
    private final String wronguser1 = "{\n" +
            "   \"name\": \"John Doe 1\",\n" +
            "   \"password\": \"oa3VvqnLxW\"\n" +
            "}";

    private final String wronguser2 = "{\n" +
            "   \"name\": \"John Doe 2\",\n" +
            "   \"username\": \"johndoe1\"\n" +
            "}";


    @Test
    @Order(1)
    void registerUser() throws Exception {
        String resultResponse = "{\r\n" +
                "  \"id\" : 1,\r\n" +
                "  \"name\" : \"administrator\",\r\n" +
                "  \"username\" : \"admin\",\r\n" +
                "  \"role\" : \"ADMINISTRATOR\"\r\n" +
                "}";
        String resultResponse1 = "{\r\n" +
                "  \"id\" : 2,\r\n" +
                "  \"name\" : \"John Doe 1\",\r\n" +
                "  \"username\" : \"johndoe1\",\r\n" +
                "  \"role\" : \"MERCHANT\"\r\n" +
                "}";

        String resultResponse2 = "{\r\n" +
                "  \"id\" : 3,\r\n" +
                "  \"name\" : \"John Doe 2\",\r\n" +
                "  \"username\" : \"johndoe2\",\r\n" +
                "  \"role\" : \"MERCHANT\"\r\n" +
                "}";

        String resultResponse3 = "{\r\n" +
                "  \"id\" : 4,\r\n" +
                "  \"name\" : \"Maria Chernysh\",\r\n" +
                "  \"username\" : \"marry\",\r\n" +
                "  \"role\" : \"MERCHANT\"\r\n" +
                "}";
        MvcResult result = this.mockMvc.perform(post(userApi).content(admin).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isCreated()).
                andReturn();
        Assertions.assertEquals(resultResponse, result.getResponse().getContentAsString());

        MvcResult result1 = this.mockMvc.perform(post(userApi).content(johndoe1).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isCreated()).
                andReturn();
        Assertions.assertEquals(resultResponse1, result1.getResponse().getContentAsString());

        this.mockMvc.perform(post(userApi).content(johndoe1).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isConflict());

        MvcResult result2 = this.mockMvc.perform(post(userApi).content(johndoe2).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isCreated()).
                andReturn();
        Assertions.assertEquals(resultResponse2, result2.getResponse().getContentAsString());

        MvcResult result3 = this.mockMvc.perform(post(userApi).content(marry).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isCreated()).
                andReturn();
        Assertions.assertEquals(resultResponse3, result3.getResponse().getContentAsString());

        this.mockMvc.perform(post(userApi).content(wronguser1).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());
        this.mockMvc.perform(post(userApi).content(wronguser2).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());

    }

    @Test
    @Order(2)
    void lock() throws Exception {
        String johndoe1Request1 = "{\r\n" +
                "  \"username\" : \"johndoe1\",\r\n" +
                "  \"operation\" : \"UNLOCK\"\r\n" +
                "}";

        String johndoe1Request2 = "{\r\n" +
                "  \"username\" : \"johndoe1\",\r\n" +
                "  \"operation\" : \"LOCK\"\r\n" +
                "}";
        String johndoe2Request = "{\r\n" +
                "  \"username\" : \"johndoe2\",\r\n" +
                "  \"operation\" : \"UNLOCK\"\r\n" +
                "}";
        String johndoe1Result1 = "{\r\n" +
                "  \"status\" : \"User johndoe1 unlocked!\"\r\n" +
                "}";
        String johndoe1Result2 = "{\r\n" +
                "  \"status\" : \"User johndoe1 locked!\"\r\n" +
                "}";
        String johndoe2Result1 = "{\r\n" +
                "  \"status\" : \"User johndoe2 unlocked!\"\r\n" +
                "}";
        String adminRequest = "{\r\n" +
                "  \"username\" : \"admin\",\r\n" +
                "  \"operation\" : \"LOCK\"\r\n" +
                "}";
        MvcResult result1 = this.mockMvc.perform(put(lockApi).content(johndoe1Request1).
                        with(httpBasic("admin", "1111")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andReturn();
        Assertions.assertEquals(johndoe1Result1, result1.getResponse().getContentAsString());

        MvcResult result2 = this.mockMvc.perform(put(lockApi).content(johndoe1Request2).
                        with(httpBasic("admin", "1111")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andReturn();
        Assertions.assertEquals(johndoe1Result2, result2.getResponse().getContentAsString());

        MvcResult result3 = this.mockMvc.perform(put(lockApi).content(johndoe2Request).
                        with(httpBasic("admin", "1111")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andReturn();
        Assertions.assertEquals(johndoe2Result1, result3.getResponse().getContentAsString());

        this.mockMvc.perform(put(lockApi).content(adminRequest).
                        with(httpBasic("admin", "1111")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());

        this.mockMvc.perform(put(lockApi).content(adminRequest).
                        with(httpBasic("johndoe1", "oMoa3VvqnLxW")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnauthorized());


    }


    @Test
    @Order(3)
    void changeRole() throws Exception {
        String requestMarry = "{\r\n" +
                "  \"username\" : \"marry\",\r\n" +
                "  \"role\" : \"SUPPORT\"\r\n" +
                "}";
        String requestJohnDoe1 = "{\r\n" +
                "  \"username\" : \"johndoe1\",\r\n" +
                "  \"role\" : \"ADMINISTRATOR\"\r\n" +
                "}";
        String requestJohnDoe2 = "{\r\n" +
                "  \"username\" : \"johndoe2\",\r\n" +
                "  \"role\" : \"USER\"\r\n" +
                "}";


        String resultResponseMarry = "{\r\n" +
                "  \"id\" : 4,\r\n" +
                "  \"name\" : \"Maria Chernysh\",\r\n" +
                "  \"username\" : \"marry\",\r\n" +
                "  \"role\" : \"SUPPORT\"\r\n" +
                "}";

        this.mockMvc.perform(put(roleApi).content(requestMarry).
                        with(httpBasic("johndoe1", "oMoa3VvqnLxW")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnauthorized());

        MvcResult result1 = this.mockMvc.perform(put(roleApi).content(requestMarry).
                        with(httpBasic("admin", "1111")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andReturn();
        Assertions.assertEquals(resultResponseMarry, result1.getResponse().getContentAsString());

        this.mockMvc.perform(put(roleApi).content(requestMarry).
                        with(httpBasic("admin", "1111")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isConflict());

        this.mockMvc.perform(put(roleApi).content(requestJohnDoe1).
                        with(httpBasic("admin", "1111")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());

        this.mockMvc.perform(put(roleApi).content(requestJohnDoe2).
                        with(httpBasic("admin", "1111")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());
    }


    @Test
    @Order(4)
    void deleteUser() throws Exception {
        String resultResponse1 = "{\r\n" +
                "  \"username\" : \"johndoe1\",\r\n" +
                "  \"status\" : \"Deleted successfully!\"\r\n" +
                "}";


        MvcResult result1 = this.mockMvc.perform(delete(userApi + "/" + "johndoe1").
                        with(httpBasic("admin", "1111")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andReturn();
        Assertions.assertEquals(resultResponse1, result1.getResponse().getContentAsString());

        this.mockMvc.perform(delete(userApi + "/katherine").
                        with(httpBasic("admin", "1111")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isNotFound());

    }

    @Test
    @Order(5)
    void getUsers() throws Exception {
        String listAnswer1 = "[ {\r\n" +
                "  \"id\" : 1,\r\n" +
                "  \"name\" : \"administrator\",\r\n" +
                "  \"username\" : \"admin\",\r\n" +
                "  \"role\" : \"ADMINISTRATOR\"\r\n" +
                "}, {\r\n" +
                "  \"id\" : 3,\r\n" +
                "  \"name\" : \"John Doe 2\",\r\n" +
                "  \"username\" : \"johndoe2\",\r\n" +
                "  \"role\" : \"MERCHANT\"\r\n" +
                "}, {\r\n" +
                "  \"id\" : 4,\r\n" +
                "  \"name\" : \"Maria Chernysh\",\r\n" +
                "  \"username\" : \"marry\",\r\n" +
                "  \"role\" : \"SUPPORT\"\r\n" +
                "} ]";
        MvcResult result1 = this.mockMvc.perform(get(userListApi).
                        with(httpBasic("admin", "1111")).
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andReturn();

        Assertions.assertEquals(listAnswer1, result1.getResponse().getContentAsString());
    }

}
