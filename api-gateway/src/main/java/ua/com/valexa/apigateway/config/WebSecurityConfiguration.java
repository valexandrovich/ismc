package ua.com.valexa.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import ua.com.valexa.apigateway.entrypoint.AuthenticationEntryPoint;
import ua.com.valexa.apigateway.filter.JwtRequestFilter;
import ua.com.valexa.apigateway.handler.AccessDeniedHandler;
import ua.com.valexa.apigateway.handler.AuthenticationSuccessHandler;
import ua.com.valexa.apigateway.model.AccessRule;
import ua.com.valexa.apigateway.repository.AccessRuleRepository;
import ua.com.valexa.apigateway.security.CustomUserService;
import ua.com.valexa.apigateway.utils.JwtUtils;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration {

    private final CustomUserService customUserService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AccessDeniedHandler accessDeniedHandler;
    private final AccessRuleRepository accessRuleRepository;

    private final JwtUtils jwtUtils;

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(new UserDetailsRepositoryReactiveAuthenticationManager(customUserService));
        authenticationWebFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);

        List<AccessRule> rules = accessRuleRepository.findAll();

        http
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.FIRST)
                .authorizeExchange(auth -> {
                    auth.pathMatchers(HttpMethod.POST, "/login").permitAll();

                    auth.pathMatchers(HttpMethod.GET, "/scheduler/unsecured").permitAll();


                    auth.pathMatchers("/uploader/**").permitAll();


                    for (AccessRule accessRule : rules) {
                        List<String> roles = List.of(accessRule.getRole().split("\\|"));
                        auth.pathMatchers(HttpMethod.valueOf(accessRule.getMethod()), accessRule.getPath()).hasAnyRole(roles.toArray(new String[0]));

                    }




                })
                .exceptionHandling(exceptionHandlingSpec -> {
                    exceptionHandlingSpec.authenticationEntryPoint(authenticationEntryPoint);
                    exceptionHandlingSpec.accessDeniedHandler(accessDeniedHandler);
                })
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        ;

        http.addFilterAfter(new JwtRequestFilter(customUserService, jwtUtils, authenticationSuccessHandler), SecurityWebFiltersOrder.FIRST);

        return http.build();
    }

}
