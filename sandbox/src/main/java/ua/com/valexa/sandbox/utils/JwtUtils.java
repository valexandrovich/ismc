package ua.com.valexa.sandbox.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import ua.com.valexa.sandbox.security.UserPrincipal;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

@Component
public class JwtUtils {

    public DecodedJWT decode(String token) {
        return JWT
                .require(Algorithm.HMAC256("secret"))
                .build()
                .verify(token);
    }

    public String issue(String username, Collection<GrantedAuthority> roles) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(Instant.now().plus(Duration.of(1, ChronoUnit.DAYS)))
                .withClaim("roles", roles.toString() )
                .withClaim("username", username )
                .sign(Algorithm.HMAC256("secret"));
    }

    public UserPrincipal convert(DecodedJWT token ) {
        return UserPrincipal
                .builder()
                .username( token.getSubject())
                .roles(extractRolesFromClaim(token))
                .build();
    }

    private List<GrantedAuthority> extractRolesFromClaim(DecodedJWT token){
        var roles = token.getClaim("roles");
        if (roles.isNull() || roles.isMissing()) return List.of();
        return roles.asList( GrantedAuthority.class);
    }

}
