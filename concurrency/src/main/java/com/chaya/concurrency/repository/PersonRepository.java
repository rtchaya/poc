/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chaya.concurrency.repository;

import com.chaya.concurrency.entity.Person;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author chaya
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Integer>{
    
    //streamAll is not working. Need to check version.
    //Stream<Person> streamAll();
    
    Person findByName(String name);
    List<Person> findAllByName(String name);
    
    //Can make this layer also async
    //Spring Data JPA - return Future
    //@Async the repository code runs in a separate thread within it's own transaction
    //@Async
    //CompletableFuture<Person> findByEmailId(String emailId);
}
