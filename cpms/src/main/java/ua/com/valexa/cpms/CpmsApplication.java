package ua.com.valexa.cpms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CpmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CpmsApplication.class, args);
    }

}
