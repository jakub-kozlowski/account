package com.qa.accountproject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class AccountController {

    public static final String ACCOUNT_ROOT_PATH = "/account-project/rest/account/json";

    private Map<Integer, Account> accounts = new HashMap<>();

    @GetMapping(path = ACCOUNT_ROOT_PATH, produces = APPLICATION_JSON_VALUE)
    public Collection<Account> getAccounts() {
        return accounts.values();
    }

    @PostMapping(path = ACCOUNT_ROOT_PATH, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Message createAccount(@RequestBody String json) throws IOException {
        final int id = getUniqueId();
        Map<String, String> jsonMap = jsonToMap(json);
        accounts.put(id, new Account(id, jsonMap.get("accountNumber"), jsonMap.get("firstName"),
                jsonMap.get("lastName")));

        return new Message("account has been successfully added");
    }

    @DeleteMapping(ACCOUNT_ROOT_PATH + "/{accountId}")
    public Message deleteAccount(@PathVariable int accountId, HttpServletResponse response) {
        if( accounts.containsKey(accountId) ) {
            accounts.remove(accountId);
            return new Message("account has been successfully deleted");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return new Message("account does not exist");
        }
    }

    private Map<String,String> jsonToMap(String json) throws IOException {
        return new ObjectMapper().readValue(json, new TypeReference<Map<String, String>>(){});
    }

    private int getUniqueId() {
        return accounts.keySet().stream().max(Comparator.naturalOrder()).orElse(0) + 1;
    }
}
