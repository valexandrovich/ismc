package ua.com.valexa.apigateway.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ua.com.valexa.apigateway.security.UserPrincipal;

@Slf4j
@Component
public class AuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        log.info("Authentication success for user {} to {}", ((UserPrincipal)authentication.getPrincipal()).getUsername(), webFilterExchange.getExchange().getRequest().getPath());

        return webFilterExchange.getChain().filter(exchange);
    }
}
