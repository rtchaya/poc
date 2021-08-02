/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chaya.webflux.repository;

import com.chaya.webflux.entity.Person;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


/**
 *
 * @author chaya
 */
@Repository
public interface PersonRepository extends ReactiveCrudRepository<Person,Integer> { 
    Mono<Person> findByName(String name);
}
