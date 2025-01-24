package ua.com.valexa.apigateway.security;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
//@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {

    private final String username;
    private final List<SimpleGrantedAuthority> roles;

//    private String sAt;

    public UserPrincipal(String username, List<SimpleGrantedAuthority> authorities) {
        this.username = username;
        this.roles = new ArrayList<>(authorities);
    }

    @Override
    public Collection<SimpleGrantedAuthority> getAuthorities() {
        return roles ;
    }

    public void setAuthorities(Collection<? extends SimpleGrantedAuthority> authorities) {
        this.roles.clear();
        this.roles.addAll(authorities);
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return username;
    }

}
