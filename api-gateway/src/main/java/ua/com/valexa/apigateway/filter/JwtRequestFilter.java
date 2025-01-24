package ua.com.valexa.apigateway.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import ua.com.valexa.apigateway.exception.UnauthorizedException;
import ua.com.valexa.apigateway.handler.AuthenticationSuccessHandler;
import ua.com.valexa.apigateway.security.CustomUserService;
import ua.com.valexa.apigateway.security.UserPrincipal;
import ua.com.valexa.apigateway.utils.JwtUtils;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter implements WebFilter {

    @Value("${auth.jwt.secret}")
    private String jwtSecret;

    private final CustomUserService customUserService;
    private final JwtUtils jwtUtils;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;


    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        Optional<String> token = jwtUtils.getTokenWithoutBearer(serverWebExchange);

        return token.map(s -> verifyAndAuthenticatePortalUser(s)
                .switchIfEmpty(webFilterChain.filter(serverWebExchange).then(Mono.empty()))
                .flatMap(authentication -> onAuthSuccess(authentication, serverWebExchange, webFilterChain))).orElseGet(() -> webFilterChain.filter(serverWebExchange));
    }


    private Mono<Authentication> verifyAndAuthenticatePortalUser(String token) {

        try {
            DecodedJWT decodedJWT = jwtUtils.decode(token);
            UserPrincipal userPrincipal = jwtUtils.convert(decodedJWT);
            return customUserService.findByUsername(userPrincipal.getUsername())
                    .flatMap(principal -> Mono.just(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())));
        } catch (Exception e) {
            log.error("Exception occured while evaluating token: {}", token, e);
            return Mono.error(new UnauthorizedException("Invalid token. Please authenticate again!"));
        }
    }

    private Mono<Void> onAuthSuccess(Authentication authentication, ServerWebExchange exchange, WebFilterChain webFilterChain) {
        ServerSecurityContextRepository securityContextRepository = new WebSessionServerSecurityContextRepository();
        WebFilterExchange webFilterExchange = new WebFilterExchange(exchange, webFilterChain);
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);
        return securityContextRepository.save(exchange, securityContext)
                .then(authenticationSuccessHandler.onAuthenticationSuccess(webFilterExchange, authentication))
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }


}
