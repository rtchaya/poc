/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chaya.webflux.controller;

import com.chaya.webflux.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.chaya.webflux.repository.PersonRepository;
import com.chaya.webflux.service.PersonService;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.scheduler.Schedulers;

/**
 *
 * @author chaya
 */
@RestController
@RequestMapping("/person/webflux")
public class PersonController {
    Logger log = LoggerFactory.getLogger(PersonService.class);
    
    
    @Autowired
    PersonRepository personRepository;
    
    @Autowired
    PersonService personService;
    
    @Autowired
    ThreadPoolTaskExecutor taskExecutor;

    @GetMapping
    public Flux<Person> getUsers() {
        return personService.getPersonsAsync();
    }
    
    @GetMapping("/name")
    public Mono<Person> getPersonByName(@RequestParam("name") String name) {
        return personService.getPersonByName(name);
    }

    @PostMapping
    public Mono<Person> createPerson(@RequestBody Person person) throws Exception {
        return personService.createPersonAsync(person);
    }
    
    @PostMapping("/list")
    public Flux<Person> createPersonList() throws Exception {
        log.info("Create List");
        long startTime = System.currentTimeMillis();
        //Approach 1
//        Flux<Person> person = Flux.merge(personService.createPersonListAsync(), personService.createPersonListAsync())
//                .flatMap(this::getInfoCallable,2)
//                .limitRate(10)
//                .log();
                  
        //Approach 2
//        ParallelFlux<Person> person = Flux.merge(personService.createPersonListAsync(), personService.createPersonListAsync())
//                .parallel(2)
//                .runOn(Schedulers.fromExecutor(taskExecutor))
//                .log();

        //Approach 3
        //Invoking only one task for now as I am still unable to 
        //set the backpressure : request in the Mono subscription.
        Flux<Person> person1 = personService.createPersonListAsync().subscribeOn(Schedulers.fromExecutor(taskExecutor));
//        Flux<Person> person2 = personService.createPersonListAsync().subscribeOn(Schedulers.fromExecutor(taskExecutor));
//        Flux <Person> person = Flux.concat(person1,person2)
//                                .log(); 
//        Flux person = Flux.zip(person1, person2);

        long endTime = System.currentTimeMillis();
        printDuration(0,startTime,endTime);
        
        return person1;
    }

    
    @PutMapping("/{id}")
    public Mono<Person> updatePerson(@PathVariable Integer id, @RequestBody Person person) {
        return personRepository.findById(id)
                .map(p -> {
                    p.setName(person.getName());
                    return p;
                }).flatMap(p -> personRepository.save(p));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteUsersById(@PathVariable Integer id) {
        return personRepository.deleteById(id);
    }
    
    private void printDuration(int size, long startTime, long endTime) {
            log.info("WebFlux: Processed  tasks in " + (endTime - startTime) + " milliseconds\n");
    }    

    private Mono<Person> getInfoCallable(Person a) {
        // Returns a non-blocking Publisher with a Single Value (Mono)
        Mono blockingWrapper =  Mono
                .fromCallable(() -> {return a;})// Define blocking call
                .subscribeOn(Schedulers.fromExecutor(taskExecutor)); // Define the execution model
        
            blockingWrapper.subscribe(new Subscriber<Person>() {
                    private Subscription s;
                    int  onNextAmount = 0;

                    @Override
                    public void onSubscribe(Subscription s) {
                        this.s = s;
                        s.request(10);
                    }

                    @Override
                    public void onNext(Person person) {
//                        e.add(person);
                        onNextAmount++;
                        if (onNextAmount>=10) {
                            onNextAmount = 0;
                            s.request(10);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {}

                    @Override
                    public void onComplete() {}
            });
        
        return blockingWrapper;
    }
   
}
