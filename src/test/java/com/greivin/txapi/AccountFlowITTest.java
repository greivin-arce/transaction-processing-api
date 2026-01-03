package com.greivin.txapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greivin.txapi.dto.CreateAccountRequest;
import com.greivin.txapi.dto.TransactionRequest;
import com.greivin.txapi.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountFlowITTest extends AbstractIntegrationTest {

        @Autowired
        MockMvc mvc;
        @Autowired
        ObjectMapper om;

        @Test
        void createAccount_thenDeposit_thenWithdraw() throws Exception {
                // 1) Create account
                var createReq = new CreateAccountRequest("Greivin Arce");

                String createJson = om.writeValueAsString(createReq);

                String accountResponse = mvc.perform(post("/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(createJson))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.externalId").isNotEmpty())
                                .andExpect(jsonPath("$.balanceCents").value(0))
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                String externalId = om.readTree(accountResponse).get("externalId").asText();
                assertThat(externalId).isNotBlank();

                // 2) Deposit 5000
                var depReq = new TransactionRequest(5000L, "dep-001", "first deposit");
                mvc.perform(post("/accounts/{externalId}/deposit", externalId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(depReq)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.amountCents").value(5000))
                                .andExpect(jsonPath("$.balanceCents").value(5000));

                // 3) Withdraw 2000
                var wReq = new TransactionRequest(2000L, "wd-001", "withdraw");
                mvc.perform(post("/accounts/{externalId}/withdraw", externalId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(wReq)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.amountCents").value(2000))
                                .andExpect(jsonPath("$.balanceCents").value(3000));
        }

        @Test
        void deposit_isIdempotent_whenSameKey() throws Exception {
                // Create account
                var createReq = new CreateAccountRequest("Greivin Arce");
                String accountResponse = mvc.perform(post("/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(createReq)))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

                String externalId = om.readTree(accountResponse).get("externalId").asText();

                // Same deposit twice with same key
                var depReq = new TransactionRequest(5000L, "dep-777", "idempotent");

                String r1 = mvc.perform(post("/accounts/{externalId}/deposit", externalId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(depReq)))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

                String r2 = mvc.perform(post("/accounts/{externalId}/deposit", externalId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(depReq)))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

                String txId1 = om.readTree(r1).get("id").asText();
                String txId2 = om.readTree(r2).get("id").asText();

                // Must be same transaction
                assertThat(txId2).isEqualTo(txId1);

                // Balance should NOT double: still 5000
                long balance2 = om.readTree(r2).get("balanceCents").asLong();
                assertThat(balance2).isEqualTo(5000L);
        }

        @Test
        void deposit_nonExistingAccount_returns404() throws Exception {
                var depReq = new TransactionRequest(5000L, "dep-404", "no account");

                mvc.perform(post("/accounts/{externalId}/deposit", "does-not-exist")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(depReq)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"));
        }

        @Test
        void withdraw_insufficientFunds_returns409() throws Exception {
                // 1) Create account (balance = 0)
                var createReq = new CreateAccountRequest("Greivin Arce");
                String accountResponse = mvc.perform(post("/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(createReq)))
                                .andExpect(status().isCreated())
                                .andReturn()
                                .getResponse()
                                .getContentAsString();

                String externalId = om.readTree(accountResponse).get("externalId").asText();
                assertThat(externalId).isNotBlank();

                // 2) Try to withdraw without funds
                var wReq = new TransactionRequest(1000L, "wd-insufficient-001", "attempt withdraw");

                mvc.perform(post("/accounts/{externalId}/withdraw", externalId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(wReq)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.code").value("INSUFFICIENT_FUNDS"))
                                .andExpect(jsonPath("$.message").isNotEmpty());
        }
}