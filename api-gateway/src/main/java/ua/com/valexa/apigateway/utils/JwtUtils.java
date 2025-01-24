package ua.com.valexa.apigateway.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import ua.com.valexa.apigateway.Constants;
import ua.com.valexa.apigateway.security.UserPrincipal;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.XMLFormatter;

@Component
public class JwtUtils {

    @Value("${auth.jwt.secret}")
    private String jwtSecret;

    @Value("${auth.jwt.lifetime}")
    private Integer jwtLifetime; //SECONDS

    private static final int BEARER_INDEX = Constants.HEADER_AUTHORIZATION_PREFIX_BEARER.length();

    public DecodedJWT decode(String token) {
        return JWT
                .require(Algorithm.HMAC256(jwtSecret))
                .build()
                .verify(token);
    }


    public String issue(String username, List<String> roles) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(Instant.now().plus(Duration.of(jwtLifetime, ChronoUnit.SECONDS)))
                .withClaim("roles", roles)
                .sign(Algorithm.HMAC256(jwtSecret));
    }


    public UserPrincipal convert(DecodedJWT token ) {
        return UserPrincipal
                .builder()
                .username( token.getSubject())
                .roles(extractRolesFromClaim(token))
                .build();
    }



    private List<SimpleGrantedAuthority> extractRolesFromClaim(DecodedJWT token){
        var roles = token.getClaim("roles");
        if (roles.isNull() || roles.isMissing()) return List.of();
        return roles.asList( SimpleGrantedAuthority.class);
    }

    public static Optional<String> getTokenWithoutBearer(ServerWebExchange serverWebExchange) {
        Optional<String> token = Optional.ofNullable(serverWebExchange.getRequest().getHeaders().getFirst(Constants.HEADER_AUTHORIZATION));
        return getTokenWithoutBearer(token);
    }

    public static Optional<String> getTokenWithoutBearer(Optional<String> tokenWithBearerPrefix) {
        return tokenWithBearerPrefix.map(s -> StringUtils.substring(s, BEARER_INDEX));
    }

}
