package ua.com.valexa.apigateway.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ua.com.valexa.apigateway.model.LoginRequest;
import ua.com.valexa.apigateway.security.CustomUserService;
import ua.com.valexa.apigateway.security.UserPrincipal;

import javax.naming.NamingException;

@RestController
@RequiredArgsConstructor
public class ApiGatewayController {

    private static final Logger log = LoggerFactory.getLogger(ApiGatewayController.class);
    private final CustomUserService customUserService;

    @GetMapping("/hello")
    Mono<String> sayHello(){
        return Mono.just("Hello world!");
    }

    @GetMapping("/secured")
    Mono<String> saySecured(@AuthenticationPrincipal Mono<UserPrincipal> principal){

        log.info("Principal: {}", principal);

        return Mono.just("Hello world from secured! ");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws NamingException {
        log.info("Login request: {}", loginRequest.getUsername());
        try {
            return new ResponseEntity<>(customUserService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword()), HttpStatus.OK);
        } catch (BadCredentialsException e){
//            throw new RuntimeException("Bad cred");
            return new ResponseEntity<>("Невірний пароль!", HttpStatus.UNAUTHORIZED);
//            throw e;
        } catch (UsernameNotFoundException e){
            return new ResponseEntity<>("Користувача не знайдено!", HttpStatus.NOT_FOUND);
//          throw e;
        } catch (Exception e){
            throw e;
        }
    }



}
