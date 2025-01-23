package ua.com.valexa.sandbox.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

@Configuration
public class LdapConfig {

    @Value("${AUTH_LDAP_URL}")
    private String ldapUrl;

    @Value("${AUTH_LDAP_BASE}")
    private String ldapBase;

    @Value("${AUTH_LDAP_MANAGER_DN}")
    private String userManagerDn;

    @Value("${AUTH_LDAP_MANAGER_PASSWORD}")
    private String userManagerPassword;

    @Value("${AUTH_LDAP_GROUP_SEARCH_BASE}")
    private String groupSearchBase;

    @Value("${AUTH_LDAP_GROUP_SEARCH_FILTER}")
    private String groupSearchFilter;

    @Bean
    public DefaultSpringSecurityContextSource contextSource() {
        DefaultSpringSecurityContextSource contextSource =
                new DefaultSpringSecurityContextSource(ldapUrl + "/" + ldapBase);
        contextSource.setUserDn(userManagerDn);
        contextSource.setPassword(userManagerPassword);
        contextSource.setReferral("follow");
        return contextSource;
    }

    @Bean
    public LdapAuthoritiesPopulator ldapAuthoritiesPopulator() {
        DefaultLdapAuthoritiesPopulator populator = new DefaultLdapAuthoritiesPopulator(contextSource(), "ou=groups");
        populator.setGroupSearchFilter(groupSearchFilter);
        populator.setIgnorePartialResultException(true);
        return populator;
    }

    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        SimpleAuthorityMapper authorityMapper = new SimpleAuthorityMapper();
        authorityMapper.setConvertToUpperCase(true);
        authorityMapper.setDefaultAuthority("ROLE_USER");
        return authorityMapper;
    }

    @Bean
    public LdapTemplate ldapTemplate(){
        return new LdapTemplate(contextSource());
    }

    @Bean
    public LdapAuthoritiesPopulator customAuthoritiesPopulator(){
        return new CustomAuthoritiesPopulator(ldapTemplate(), groupSearchBase);
    }

}
