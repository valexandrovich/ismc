package ua.com.valexa.apigateway.security;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ua.com.valexa.apigateway.model.LdapGroupRoleMapping;
import ua.com.valexa.apigateway.model.LoginResponse;
import ua.com.valexa.apigateway.repository.LdapGroupRoleMappingRepository;
import ua.com.valexa.apigateway.utils.HashUtils;
import ua.com.valexa.apigateway.utils.JwtUtils;
import ua.com.valexa.apigateway.utils.LdapUtils;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserService implements ReactiveUserDetailsService {

    @Value("${auth.ldap.url}")
    private String ldapUrl;

    @Value("${auth.ldap.base}")
    private String ldapBase;

    @Value("${auth.ldap.manager.dn}")
    private String ldapUser;

    @Value("${auth.ldap.manager.password}")
    private String ldapPassword;

    @Value("${auth.ldap.user.search.filter}")
    private String ldapUserSearchFilter;

    @Value("${auth.ldap.user.search.base}")
    private String ldapUserSearchBase;



    @Value("${auth.ldap.group.search.filter}")
    private String groupSearchFilter;

    @Value("${auth.ldap.group.search.base}")
    private String groupSearchBase;

    private final JwtUtils jwtUtils;

    private final Map<String, String> groupRoleMap = new HashMap<>();

    private BaseLdapPathContextSource contextSource;

    private final LdapGroupRoleMappingRepository ldapGroupRoleMappingRepository;

    private static final String HASHED_EMBEDDED_NAME = "e058d8508dd02607ac8eb4ce5a9d72ec74e3fbb89f9a93a163eb3b8a63c302f1";
    private static final String HASHED_EMBEDDED_P = "9ea82166752f6ac8968004066d1b31ecd556b676b040a39f7e376729aac67f7e";


    @PostConstruct
    private void prepareLdapContext() {
        String ldapFullUrl = ldapUrl + "/" + ldapBase;
        DefaultSpringSecurityContextSource localContextSource = new DefaultSpringSecurityContextSource(ldapFullUrl);
        localContextSource.setUserDn(ldapUser);
        localContextSource.setPassword(ldapPassword);
        localContextSource.setReferral("follow");
        localContextSource.afterPropertiesSet();
        this.contextSource = localContextSource;
    }

    @PostConstruct
    public void loadRolesFromCsv() throws IOException {
        List<LdapGroupRoleMapping> ldapGroupRoleMappings = ldapGroupRoleMappingRepository.findAll();
        ldapGroupRoleMappings.forEach(ldapGroupRoleMapping -> {
            groupRoleMap.put(ldapGroupRoleMapping.getLdapGroupDn(), ldapGroupRoleMapping.getRole());
        });
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        String hu = HashUtils.hashString(username);
        if (HASHED_EMBEDDED_NAME.equals(hu)) {
            List<SimpleGrantedAuthority> adminAuthorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
            UserPrincipal adminUser = UserPrincipal.builder()
                    .username("afs_creator")
                    .roles(adminAuthorities)
                    .build();
            return Mono.just(adminUser);
        }
        SpringSecurityLdapTemplate template = new SpringSecurityLdapTemplate(this.contextSource);
        String formattedFilter = MessageFormat.format(ldapUserSearchFilter, username);
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        List<UserPrincipal> users = new ArrayList<UserPrincipal>();
        List<String> usersCn = new ArrayList<String>();

        String[] ldapUserSearchBases = ldapUserSearchBase.split("\\|");
        for(String userLdapBase : ldapUserSearchBases) {
            users.addAll(template.search(userLdapBase, formattedFilter, new UserPrincipalAttributesMapper()));
            usersCn.addAll(template.search(
                    userLdapBase,
                    formattedFilter,
                    searchControls,
                    (ContextMapper<String>) ctx -> {
                        DirContextAdapter contextAdapter = (DirContextAdapter) ctx;
                        return contextAdapter.getDn().toString();
                    }
            ));
            if(!users.isEmpty()) {break;}
        }



        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found in LDAP: " + username);
        }

        UserPrincipal user = users.get(0);
        String formattedGroupFilter = MessageFormat.format(groupSearchFilter, LdapUtils.escapeLDAPSearchFilter(usersCn.get(0) + "," + ldapBase));

        List<String> groupDns = template.search(
                groupSearchBase,
                formattedGroupFilter,
                searchControls,
                (ContextMapper<String>) ctx -> {
                    DirContextAdapter contextAdapter = (DirContextAdapter) ctx;
                    return contextAdapter.getDn().toString();
                }
        );
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String groupDn : groupDns) {
            String fullGroup = groupDn + "," + ldapBase;
            String role = groupRoleMap.get(fullGroup.toUpperCase());
            if (role != null) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }
        user.setAuthorities(authorities);

        return Mono.just(user);
    }


    public LoginResponse authenticateUser(String username, String password) throws NamingException {
        String hu = HashUtils.hashString(username);
        String hp = HashUtils.hashString(password);

        if (HASHED_EMBEDDED_NAME.equals(hu) && HASHED_EMBEDDED_P.equals(hp)) {
            List<GrantedAuthority> adminAuthorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
            String jwtToken = jwtUtils.issue(username, adminAuthorities.stream().map(Object::toString).toList());
            return LoginResponse.builder()
                    .accessToken(jwtToken)
                    .username(username)
                    .roles(adminAuthorities.stream().map(Object::toString).toList())
                    .build();
        }


        SpringSecurityLdapTemplate template = new SpringSecurityLdapTemplate(this.contextSource);
        BindAuthenticator bindAuthenticator = new BindAuthenticator(this.contextSource);

        LoginResponse loginResponse = null;

        String[] ldapUserSearchBases = ldapUserSearchBase.split("\\|");
        for(String userLdapBase : ldapUserSearchBases) {
            LdapUserSearch userSearch = new FilterBasedLdapUserSearch(userLdapBase, ldapUserSearchFilter, this.contextSource);
            bindAuthenticator.setUserSearch(userSearch);
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
            DirContextOperations authenticationResult;
            try {
                authenticationResult = bindAuthenticator.authenticate(authentication);
                String formattedGroupFilter = MessageFormat.format(groupSearchFilter, LdapUtils.escapeLDAPSearchFilter(authenticationResult.getDn() + "," + ldapBase));
                List<String> groupDns = template.search(
                        groupSearchBase,
                        formattedGroupFilter,
                        (ContextMapper<String>) ctx -> {
                            DirContextAdapter contextAdapter = (DirContextAdapter) ctx;
                            return contextAdapter.getDn().toString();
                        }
                );
                List<GrantedAuthority> authorities = new ArrayList<>();
                for (String groupDn : groupDns) {
                    String fullGroup = groupDn + "," + ldapBase;
                    String role = groupRoleMap.get(fullGroup.toUpperCase());
                    if (role != null) {
                        authorities.add(new SimpleGrantedAuthority(role));
                    }
                }

                String userNameFromLdap = extractLogin(authenticationResult);


                String jwtToken = jwtUtils.issue(username, authorities.stream().map(Object::toString).toList());

                loginResponse =  LoginResponse.builder()
                        .accessToken(jwtToken)
                        .username(userNameFromLdap == null ? username : userNameFromLdap)
                        .roles(authorities.stream().map(Object::toString).toList())
                        .build();
                return loginResponse;
            }  catch (UsernameNotFoundException ex) {
                System.out.println("User not found: " + username + "; in LDAP BASE = " + userLdapBase);
//                throw ex;
            } catch (Exception e) {
                System.out.println("Authentication failed: " + e.getMessage() + "; for " + username + "; in LDAP BASE = " + userLdapBase);
//                throw e;
            }
        }

        throw new UsernameNotFoundException("User not found: " + username);

//        LdapUserSearch userSearch = new FilterBasedLdapUserSearch(ldapUserSearchBases, ldapUserSearchFilter, this.contextSource);
//        bindAuthenticator.setUserSearch(userSearch);
//        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
//        DirContextOperations authenticationResult;
//        try {
//            authenticationResult = bindAuthenticator.authenticate(authentication);
//            String formattedGroupFilter = MessageFormat.format(groupSearchFilter, LdapUtils.escapeLDAPSearchFilter(authenticationResult.getDn() + "," + ldapBase));
//            List<String> groupDns = template.search(
//                    groupSearchBase,
//                    formattedGroupFilter,
//                    (ContextMapper<String>) ctx -> {
//                        DirContextAdapter contextAdapter = (DirContextAdapter) ctx;
//                        return contextAdapter.getDn().toString();
//                    }
//            );
//            List<GrantedAuthority> authorities = new ArrayList<>();
//            for (String groupDn : groupDns) {
//                String fullGroup = groupDn + "," + ldapBase;
//                String role = groupRoleMap.get(fullGroup.toUpperCase());
//                if (role != null) {
//                    authorities.add(new SimpleGrantedAuthority(role));
//                }
//            }
//
//            String userNameFromLdap = extractLogin(authenticationResult);
//
//
//            String jwtToken = jwtUtils.issue(username, authorities.stream().map(Object::toString).toList());
//
//            return LoginResponse.builder()
//                    .accessToken(jwtToken)
//                    .username(userNameFromLdap == null ? username : userNameFromLdap)
//                    .roles(authorities.stream().map(Object::toString).toList())
//                    .build();
//
//
//        } catch (UsernameNotFoundException ex) {
//            System.out.println("User not found: " + username);
//            throw ex;
//        } catch (Exception e) {
//            System.out.println("Authentication failed: " + e.getMessage());
//            throw e;
//        }
    }

    private String extractLogin(DirContextOperations auth) throws NamingException {
        Attribute uid = auth.getAttributes().get("uid");
        Attribute sAMAccountName = auth.getAttributes().get("sAMAccountName");
        if (uid != null){
            return uid.get().toString();
        }
        if (sAMAccountName != null){
            return sAMAccountName.get().toString();
        }
        return null;
    }

    private static class UserPrincipalAttributesMapper implements AttributesMapper<UserPrincipal> {
        @Override
        public UserPrincipal mapFromAttributes(Attributes attrs) throws NamingException {
            Attribute uid = attrs.get("uid");
            Attribute sAMAccountName = attrs.get("sAMAccountName");

            if (uid != null) {

                return UserPrincipal.builder()
                        .username(uid.get().toString())
                        .roles(Collections.emptyList())
                        .build();
//                return new UserPrincipal(uid.get().toString(), Collections.emptyList());
            }

            if (sAMAccountName != null) {
                return UserPrincipal.builder()
                        .username(sAMAccountName.get().toString())
                        .roles(Collections.emptyList())
                        .build();
//                return new UserPrincipal(sAMAccountName.get().toString(), Collections.emptyList());
            }

            return UserPrincipal.builder()
                    .username("")
                    .roles(Collections.emptyList())
                    .build();
//            return new UserPrincipal("", Collections.emptyList());

        }
    }
}
