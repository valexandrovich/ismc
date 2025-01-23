package ua.com.valexa.sandbox.security;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class UserPrincipal implements UserDetails {

    private final String username;
    private final List<GrantedAuthority> roles;

    public UserPrincipal(String username, List<GrantedAuthority> authorities) {
        this.username = username;
        this.roles = new ArrayList<>(authorities);
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return roles ;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
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
