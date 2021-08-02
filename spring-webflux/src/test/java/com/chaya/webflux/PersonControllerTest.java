/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chaya.webflux;

import com.chaya.webflux.entity.Person;
import com.chaya.webflux.service.PersonService;
import java.util.Collections;
import java.util.List;
import org.junit.runner.RunWith;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/**
 *
 * @author chaya
 */
@SpringBootTest
public class PersonControllerTest {
    Logger log = LoggerFactory.getLogger(PersonControllerTest.class);
    final WebClient client = WebClient.builder().baseUrl("http://localhost:8080").build();
    
    @Test
    public void FluxTest() {
        long startTime = System.currentTimeMillis();
        Flux<Person> persons =  client.post().uri("/person/webflux/list")
                .retrieve().bodyToFlux(Person.class);
        persons.blockLast();
        long personCount = persons.count().block();        
        long endTime = System.currentTimeMillis();
       
        log.info("WebFlux Test: Processed " + personCount + " tasks in " + (endTime - startTime) + " milliseconds\n");

    }
}
