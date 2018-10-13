package nl.schiphol.schipholapp;

import nl.schiphol.schipholapp.analyze.Analyzer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@EntityScan("entity")
@EnableJpaRepositories("repository")
@ComponentScan(basePackages = {"controller", "service"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        new Analyzer();
    }
}
