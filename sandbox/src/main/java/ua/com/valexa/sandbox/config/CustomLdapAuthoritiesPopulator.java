package ua.com.valexa.sandbox.config;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    private final Map<String, String> roleMapping;

    public CustomLdapAuthoritiesPopulator(Map<String, String> roleMapping) {
        this.roleMapping = roleMapping;
    }

    @Override
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {
        return List.of();
    }
}
