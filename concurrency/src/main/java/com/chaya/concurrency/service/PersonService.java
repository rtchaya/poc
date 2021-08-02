/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chaya.concurrency.service;

import com.chaya.concurrency.entity.Person;
import com.chaya.concurrency.repository.PersonRepository;
import com.chaya.concurrency.utils.ParseFile;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    public CompletableFuture<Person> createPersonAsync(Person person) throws Exception {
        log.info("Async: create person: " +  Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();
        person = personRepository.save(person);
        long endTime = System.currentTimeMillis();
        log.info("Time taken : " + (endTime - startTime));

        return CompletableFuture.completedFuture(person);
    }
    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<Stream<Person>> getPersonsAsync() {
        log.info("Async: get list of persons : " + Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();
        Stream<Person> personList =  personRepository.findAll().stream();
        long endTime = System.currentTimeMillis();
        log.info("Time taken : " + (endTime - startTime));
        
        return CompletableFuture.completedFuture(personList);
    }

    @Async
    public CompletableFuture<List<Person>> createPersonListAsync() throws Exception {
        List<Person> persons = createPersonListSync("CompletableFuture");

        return CompletableFuture.completedFuture(persons);
    }

    public List<Person> createPersonListSync(String from) throws Exception {
        long startTime = System.currentTimeMillis();
        List<Person> persons = parseFile.parseInputFile();
        log.info(from + ": saving list of  persons of size : " + persons.size() + " Thread : "  + Thread.currentThread().getName());
        persons = personRepository.saveAll(persons);
        long endTime = System.currentTimeMillis();
        log.info(from + ": Time taken for db: " + (endTime - startTime));

        return persons;
    }

    @Transactional(readOnly = true)
    public Stream<Person> getPersonsSync() {
        log.info("Sync: get list of persons : " + Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();
        Stream<Person> personList =  personRepository.findAll().stream();
        long endTime = System.currentTimeMillis();
           
        log.info("Time taken : " + (endTime - startTime));
        return personList;
    }
    
    @Transactional()
    public Stream<Person> getPersonByNameSync(String name) {
        log.info("Sync : get list person by name: " + Thread.currentThread().getName());
        long startTime = System.currentTimeMillis();
        Stream<Person> personList =  personRepository.findAllByName(name).stream();
        long endTime = System.currentTimeMillis();
        log.info("Time taken : " + (endTime - startTime));
        
        return personList;
    }

}

