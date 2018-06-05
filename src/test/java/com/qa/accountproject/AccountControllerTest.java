package com.qa.accountproject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void whenGetMethodOnAccountPath_andNoDataStored_thenEmptyJsonIsReturned() throws Exception {
        mvc.perform(get(AccountController.ACCOUNT_ROOT_PATH))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DirtiesContext
    public void whenPostMethodOnAccountPath_thenSuccessMessageIsReturned_andAccountIsCreated() throws Exception {
        expectAccountCreatedSuccessfully("Steven", "Doe", "1238");

        mvc.perform(get(AccountController.ACCOUNT_ROOT_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[0].firstName", is("Steven")))
                .andExpect(jsonPath("$.[0].lastName", is("Doe")))
                .andExpect(jsonPath("$.[0].accountNumber", is("1238")));
    }

    @Test
    @DirtiesContext
    public void whenPostMethodOnAccountPath_withTwoAccounts_thenTwoAccountsAreCreated() throws Exception {
        expectAccountCreatedSuccessfully("Steven", "Doe", "1238");
        expectAccountCreatedSuccessfully("John", "Smith", "8321");

        mvc.perform(get(AccountController.ACCOUNT_ROOT_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)));
    }

    @Test
    public void whenNoAccounts_andDeleteMethodOnAccountPath_thenNotFoundIsReturned() throws Exception {
        mvc.perform(delete(AccountController.ACCOUNT_ROOT_PATH + "/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\": \"account does not exist\"}"));
    }

    @Test
    @DirtiesContext
    public void whenTwoAccountsAdded_andDeleteMethodOnAccountPath_thenCorrectAccountDeleted_andOneIsLeft() throws Exception {
        expectAccountCreatedSuccessfully("Steven", "Doe", "1238");
        expectAccountCreatedSuccessfully("John", "Smith", "8321");

        mvc.perform(delete(AccountController.ACCOUNT_ROOT_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\": \"account has been successfully deleted\"}"));

        mvc.perform(get(AccountController.ACCOUNT_ROOT_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(2)))
                .andExpect(jsonPath("$.[0].firstName", is("John")))
                .andExpect(jsonPath("$.[0].lastName", is("Smith")))
                .andExpect(jsonPath("$.[0].accountNumber", is("8321")));
    }

    private void expectAccountCreatedSuccessfully(String firstName, String lastName, String accountNumber) throws Exception {
        String newAccountJson = getJsonFor(firstName, lastName, accountNumber);

        mvc.perform(post(AccountController.ACCOUNT_ROOT_PATH)
                .contentType(APPLICATION_JSON)
                .content(newAccountJson))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\": \"account has been successfully added\"}"));
    }

    private String getJsonFor(String firstName, String lastName, String accountNumber) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"firstName\": \"");
        sb.append(firstName);
        sb.append("\", \"lastName\": \"");
        sb.append(lastName);
        sb.append("\", \"accountNumber\": \"");
        sb.append(accountNumber);
        sb.append("\"}");
        return sb.toString();
    }
}