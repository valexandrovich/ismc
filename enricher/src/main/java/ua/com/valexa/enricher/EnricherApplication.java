package ua.com.valexa.enricher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EnricherApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnricherApplication.class, args);
    }

}
