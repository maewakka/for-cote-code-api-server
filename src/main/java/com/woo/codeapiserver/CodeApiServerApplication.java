package com.woo.codeapiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CodeApiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeApiServerApplication.class, args);
    }

}
