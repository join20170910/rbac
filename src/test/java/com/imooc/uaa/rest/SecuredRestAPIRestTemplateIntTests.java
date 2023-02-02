package com.imooc.uaa.rest;

import com.imooc.uaa.domain.User;
import lombok.With;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecuredRestAPIRestTemplateIntTests {
    @Autowired
    private TestRestTemplate template;
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }
    @Test
    public void givenAuthRequest_shouldSucceedWith200() throws Exception {
        ResponseEntity<User> result = template
            .withBasicAuth("user", "12345678")
            .getForEntity("/api/me", User.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @WithMockUser(username = "zhangsan" , roles = {"TEST"})
    @Test
    public void givenRoleUserOrAdmin_thenAccessSuccess() throws Exception {
        mvc.perform(
            get("/api/users{username}","user")

        ).andDo(print())
            .andExpect(status().isOk());
    }
    @WithMockUser
    @Test
    public void givenUserRole_whenQueryUserByEmail_shouldSuccess() throws Exception {
        mvc.perform(get("/users/my-email/{email}","user@local.dev"))
            .andDo(print())
            .andExpect(status().isOk());
    }
}
