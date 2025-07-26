package com.example.demologin;

import com.example.demologin.config.DotenvLoader;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SecurityScheme(name = "api", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
// http://localhost:8080/swagger-ui/index.html#/
public class DemoLoginApplication {

    public static void main(String[] args) {
        DotenvLoader.loadEnv();
        SpringApplication.run(DemoLoginApplication.class, args);
    }

}
