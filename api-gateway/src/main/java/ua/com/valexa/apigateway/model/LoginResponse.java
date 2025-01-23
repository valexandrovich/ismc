package ua.com.valexa.apigateway.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class LoginResponse {
    private String accessToken;
    private String username;
    private List<String> roles;
}
