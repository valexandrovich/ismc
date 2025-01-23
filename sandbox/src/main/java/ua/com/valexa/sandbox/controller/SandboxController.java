package ua.com.valexa.sandbox.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ua.com.valexa.sandbox.model.LoginRequest;
import ua.com.valexa.sandbox.model.LoginResponse;
import ua.com.valexa.sandbox.security.UserPrincipal;
import ua.com.valexa.sandbox.utils.JwtUtils;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class SandboxController {

    @Autowired
    JwtUtils jwtUtils;



    @GetMapping("/hello")
    public String sayHello(){
        return "Hello ";
    }

    @GetMapping("/secure")
    public String saySecure(@AuthenticationPrincipal UserPrincipal principal){
        String p = principal == null ?  "null" : principal.toString();
        return "Secure! Logged as: " + p;
    }

    @Autowired
    private final AuthenticationManager authenticationManager;
//
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            String token = jwtUtils.issue(authentication.getName(), (Collection<GrantedAuthority>) authentication.getAuthorities());

            return ResponseEntity.ok(LoginResponse.builder().accessToken(token).build());
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }

//    @PostMapping("/login")
//    public LoginResponse login(@RequestBody @Validated LoginRequest loginRequest ){
//            return LoginResponse.builder().accessToken(
//                    jwtIssuer.issue(loginRequest.getUsername(), List.of("USER"))
//            ).build();
//    }

//    @PostMapping("/login")
//    public Map<String, String> authenticateUser(@RequestBody Map<String, String> loginRequest) {
//        String username = loginRequest.get("username");
//        String password = loginRequest.get("password");
//
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(username, password));
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        String jwt = jwtUtils.generateJwtToken(authentication);
//
//        Map<String, String> response = new HashMap<>();
//        response.put("token", jwt);
//
//        return response;
//    }

}
