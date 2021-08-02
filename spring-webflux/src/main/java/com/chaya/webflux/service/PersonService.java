/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chaya.webflux.service;

import com.chaya.webflux.entity.Person;
import com.chaya.webflux.repository.PersonRepository;
import com.chaya.webflux.utils.ParseFile;
import java.util.List;
import java.lang.Iterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author chaya
 */
@Component
public class PersonService {
    
    Logger log = LoggerFactory.getLogger(PersonService.class);
    
    @Autowired
    PersonRepository personRepository;
    
    @Autowired
    ParseFile parseFile;
    
    @Async
    public Mono<Person> createPersonAsync(Person person) throws Exception {
        log.info("WebFlux: create person: " +  Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();
        Mono<Person> person1 = personRepository.save(person);
        long endTime = System.currentTimeMillis();
        log.info("WebFlux : Time taken for db : " + (endTime - startTime));

        return person1;
    }

    @Async("taskExecutor")
    public Flux<Person> createPersonListAsync() throws Exception {
        long startTime = System.currentTimeMillis();
        //read from csv and save
        List<Person> persons = parseFile.parseInputFile();
        log.info("WebFlux: saving list of  persons of size : " + persons.size() + " Thread : "  + Thread.currentThread().getName());
        Flux<Person> persons1 = personRepository.saveAll((Iterable<Person>) persons);
        long endTime = System.currentTimeMillis();
        log.info("WebFlux : Time taken for db : " + (endTime - startTime));

        return persons1;
    }

    @Async
    @Transactional(readOnly = true)
    public Flux<Person> getPersonsAsync() {
        log.info("WebFlux: get list of persons : " + Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();
        Flux<Person> personList =  personRepository.findAll();
        long endTime = System.currentTimeMillis();
        log.info("WebFlux : Time taken for db : " + (endTime - startTime));
        
        return personList;
    }
    
    @Async
    @Transactional(readOnly = true)
    public Mono<Person> getPersonByName(String name) {
        log.info("WebFlux: get person by name : " + Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();
        Mono<Person> personList =  personRepository.findByName(name);
        long endTime = System.currentTimeMillis();
        log.info("WebFlux : Time taken for db : " + (endTime - startTime));
        
        return personList;
    }
    
}
