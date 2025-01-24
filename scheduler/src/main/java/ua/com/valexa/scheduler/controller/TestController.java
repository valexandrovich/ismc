package ua.com.valexa.scheduler.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class TestController {

    @GetMapping("/secured")
    public Mono<String> secured() {
        return Mono.just("Secured");
    }

    @GetMapping("/unsecured")
    public Mono<String> unsecured() {
        return Mono.just("Unsecured");
    }

}
