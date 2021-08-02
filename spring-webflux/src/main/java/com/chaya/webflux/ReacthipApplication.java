package com.chaya.webflux;

import com.chaya.webflux.service.PersonService;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReacthipApplication {
    Logger log = LoggerFactory.getLogger(PersonService.class);

    public static void main(String[] args) {
            SpringApplication.run(ReacthipApplication.class, args);
    }

    @PostConstruct
    public void init() {
        log.info("CPU: {}", Runtime.getRuntime().availableProcessors());
    }

        
}
