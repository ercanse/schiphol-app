package nl.schiphol.schipholapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"nl.schiphol.schipholapp.analyze", "nl.schiphol.schipholapp.collect",
        "nl.schiphol.schipholapp.service", "nl.schiphol.schipholapp.controller"})
@EntityScan("nl.schiphol.schipholapp.entity")
@EnableJpaRepositories("nl.schiphol.schipholapp.repository")
public class Application {
    private static String resource = "";
    private static String apiVersion = "";

    public static void main(String[] args) {
        if (args.length == 1) {
            String mode = args[0];
            switch (mode) {
                case "destinations":
                    Application.setResource("destinations");
                    Application.setApiVersion("v1");
                    break;
                case "flights":
                    Application.setResource("flights");
                    Application.setApiVersion("v3");
                    break;
                default:
                    break;
            }
        }

        SpringApplication.run(Application.class, args);
    }

    public static String getResource() {
        return resource;
    }

    public static void setResource(String resource) {
        Application.resource = resource;
    }

    public static String getApiVersion() {
        return apiVersion;
    }

    public static void setApiVersion(String apiVersion) {
        Application.apiVersion = apiVersion;
    }
}
