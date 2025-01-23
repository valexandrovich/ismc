package ua.com.valexa.sandbox.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ua.com.valexa.sandbox.security.JwtAuthenticationFilter;
import ua.com.valexa.sandbox.security.JwtRequestFilter;
import ua.com.valexa.sandbox.utils.JwtUtils;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig  {

    private final DefaultSpringSecurityContextSource defaultSpringSecurityContextSource;
    private final LdapAuthoritiesPopulator customAuthoritiesPopulator;

    @Value("${AUTH_LDAP_USER_SEARCH_BASE}")
    private String userSearchBase;

    @Value("${AUTH_LDAP_USER_SEARCH_FILTER}")
    private String userSearchFilter;




    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    JwtRequestFilter jwtRequestFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtUtils);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> {
                    authorize
                            .requestMatchers(HttpMethod.POST,"/login").permitAll()
                            .requestMatchers("/hello").permitAll()
                            .anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, LdapAuthoritiesPopulator ldapAuthoritiesPopulator) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth
                .ldapAuthentication()
                .userSearchBase(userSearchBase)
                .userSearchFilter(userSearchFilter)
                .contextSource(defaultSpringSecurityContextSource)
                .ldapAuthoritiesPopulator(customAuthoritiesPopulator);
//        auth.userDetailsService(userDetailsService);
        return auth.build();
    }


//    @Autowired
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .ldapAuthentication()
//                .userSearchBase(userSearchBase)
//                .userSearchFilter(userSearchFilter)
//                .contextSource(defaultSpringSecurityContextSource)
//                .ldapAuthoritiesPopulator(customAuthoritiesPopulator);
//
//    }

}
