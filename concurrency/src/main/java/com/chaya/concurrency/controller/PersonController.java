/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chaya.concurrency.controller;

import com.chaya.concurrency.config.AsyncConfig;
import com.chaya.concurrency.entity.Person;
import com.chaya.concurrency.service.PersonService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author chaya
 */
@RestController
@RequestMapping("/person")
public class PersonController {
    Logger log = LoggerFactory.getLogger(PersonService.class);
    
    @Autowired
    PersonService personService;
    
    @Autowired
    AsyncConfig asyncConfig;
    
    @PostMapping("/cf")
    public CompletableFuture<ResponseEntity> createPersonAsync(@RequestBody Person person) throws Exception {
        
        return personService.createPersonAsync(person).thenApply(ResponseEntity::ok);
    }

    @PostMapping("/cf/list")
    public ResponseEntity createPersonListAsync() throws Exception {
        long startTime = System.currentTimeMillis();
        CompletableFuture<List<Person>> person1 = personService.createPersonListAsync();
        CompletableFuture<List<Person>> person2 = personService.createPersonListAsync();
        CompletableFuture<List<Person>> person3 = personService.createPersonListAsync();
        //allOf returns void
        //CompletableFuture<ResponseEntity> returnPerson = CompletableFuture.allOf(person1,person2).thenApply(ResponseEntity::ok);
                
        List<Object> returnPerson1 = Stream.of(person1,person2,person3)
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        
        long endTime = System.currentTimeMillis();
        printDuration(returnPerson1.size(),startTime,endTime );
        
        return new ResponseEntity(returnPerson1,HttpStatus.OK);
    }

    @PostMapping("/cf/listsypplyasync")
    public ResponseEntity createPersonListSypplyAsync() throws Exception {
        long startTime = System.currentTimeMillis();
        CompletableFuture<List<Person>> person1 = CompletableFuture.supplyAsync(() -> {
                                                try { return personService.createPersonListSync("SypplyAsync"); }
                                                    catch(Exception ex) { throw new CompletionException(ex); }
                                                }, asyncConfig.taskExecutor());
        CompletableFuture<List<Person>> person2 = CompletableFuture.supplyAsync(() -> {
                                                try { return personService.createPersonListSync("SypplyAsync"); }
                                                    catch(Exception ex) { throw new CompletionException(ex); }
                                                },asyncConfig.taskExecutor());
        CompletableFuture<List<Person>> person3 = CompletableFuture.supplyAsync(() -> {
                                                try { return personService.createPersonListSync("SypplyAsync"); }
                                                    catch(Exception ex) { throw new CompletionException(ex); }
                                                },asyncConfig.taskExecutor());
                
        List<Object> returnPerson1 = Stream.of(person1,person2,person3)
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        
        long endTime = System.currentTimeMillis();
        printDuration(returnPerson1.size(),startTime,endTime );
        
        return new ResponseEntity(returnPerson1,HttpStatus.OK);
    }

    
    @GetMapping("/cf")
    public CompletableFuture<ResponseEntity> getPersonsAsync() {
        
        return personService.getPersonsAsync().thenApply(ResponseEntity::ok);
    }

    @PostMapping("/sync/list")
    public ResponseEntity createPersonSync() throws Exception {
        long startTime = System.currentTimeMillis();
        List<Person> persons = personService.createPersonListSync("Sync");
        persons.addAll(personService.createPersonListSync("Sync"));
        persons.addAll(personService.createPersonListSync("Sync"));
        long endTime = System.currentTimeMillis();
        printDuration(persons.size(),startTime,endTime );
        
        return new ResponseEntity(persons, HttpStatus.OK);
    }

    @GetMapping("/sync")
    public ResponseEntity getPersonsSync() {
        
        return new ResponseEntity(personService.getPersonsSync(),HttpStatus.OK);
        
    }

    @GetMapping("/sync/name")
    public ResponseEntity getPersonByNameSync(@RequestParam("name") String name) {
        
        return new ResponseEntity(personService.getPersonByNameSync(name),HttpStatus.OK);

    }
    
    private void printDuration(int size, long startTime, long endTime) {
            log.info("Processed " + size + " tasks in " + (endTime - startTime) + " milliseconds\n");
    }    
}
