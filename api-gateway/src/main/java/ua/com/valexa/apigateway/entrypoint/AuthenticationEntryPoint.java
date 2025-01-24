package ua.com.valexa.apigateway.entrypoint;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ua.com.valexa.apigateway.Constants;
import ua.com.valexa.apigateway.exception.UnauthenticatedException;

@Configuration
@Slf4j
public class AuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange serverWebExchange, AuthenticationException e) {
        if (CollectionUtils.isNotEmpty(serverWebExchange.getRequest().getHeaders().get(Constants.HEADER_AUTHORIZATION))) {
            return Mono.empty();
        } else {
            log.info("Unauthenticated access attempt to resource {} with HttpMethod of {} Token: {}",
                    serverWebExchange.getRequest().getPath(),
                    StringUtils.defaultString(serverWebExchange.getRequest().getMethod().name(), StringUtils.EMPTY),
                    serverWebExchange.getRequest().getHeaders().get(Constants.HEADER_AUTHORIZATION));
            return Mono.error(new UnauthenticatedException("Please login to access this resource!"));
        }
    }
}