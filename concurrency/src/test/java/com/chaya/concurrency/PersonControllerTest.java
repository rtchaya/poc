/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chaya.concurrency;

import com.chaya.concurrency.entity.Person;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 *
 * @author chaya
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
//@AutoConfigureWebTestClient
public class PersonControllerTest {
    Logger log = LoggerFactory.getLogger(PersonControllerTest.class);
    @Autowired
    WebTestClient webTestClient;
    
    @Test
    public void CmpletableFutureTest() {
        EntityExchangeResult<List<Person>> entityExchangeResult1 =  webTestClient.get().uri("/person/cf")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Person.class)
                .returnResult();

        long startTime = System.currentTimeMillis();
        EntityExchangeResult<List<Person>> entityExchangeResult =  webTestClient.post().uri("/person/cf/list")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Person.class)
                .returnResult();
        long endTime = System.currentTimeMillis();
        
        log.info("CompletableFuture Test: Processed " + entityExchangeResult.getResponseBody() + " tasks in " + (endTime - startTime) + " milliseconds\n");
    }
    
   @Test
    public void SyncTest() {
        long startTime = System.currentTimeMillis();
        EntityExchangeResult<List<Person>> entityExchangeResult =  webTestClient.post().uri("/person/sync/list")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Person.class)
                .returnResult();
        long endTime = System.currentTimeMillis();
        int personCount = entityExchangeResult.getResponseBody().size();
        
        log.info("CompletableFuture Test: Processed " + personCount + " tasks in " + (endTime - startTime) + " milliseconds\n");
    }    
}
