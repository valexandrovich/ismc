package ua.com.valexa.importer;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableBatchProcessing(tablePrefix = "IMPORTER.BATCH_")
@EnableDiscoveryClient
public class ImporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImporterApplication.class, args);
    }

}
