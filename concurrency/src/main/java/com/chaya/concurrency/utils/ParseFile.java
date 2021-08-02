/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chaya.concurrency.utils;

import com.chaya.concurrency.entity.Person;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 *
 * @author chaya
 */
@Component
@PropertySource("classpath:application.properties")
public class ParseFile {

    Logger log = LoggerFactory.getLogger(ParseFile.class);
    
    @Value("${mockDataFile}")
    String mockDataFile;
    
    public List<Person> parseInputFile() throws Exception{
        List<Person> persons = new ArrayList<>();
        
        FileReader fr=new FileReader(mockDataFile);
        BufferedReader br=new BufferedReader(fr); 
        String line;
        while((line=br.readLine())!=null){  
            String[] data = line.split(",");
            Person person = new Person();
            person.setName(data[0]);
            person.setEmailId(data[1]);
            persons.add(person);
        }
        br.close();
        fr.close();
        
        return persons;
    }
                
}
