package ua.com.valexa.sandbox.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Component;
import ua.com.valexa.sandbox.utils.LdapUtils;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

@Component
public class CustomUserDetailsService implements UserDetailsService {


    @Autowired
    private LdapTemplate ldapTemplate;

    @Value("${AUTH_LDAP_BASE}")
    private String ldapBase;

    @Value("${auth.ldap.user.search.filter}")
    private String ldapUserSearchFilter;

    @Value("${auth.ldap.user.search.base}")
    private String ldapUserSearchBase;

    @Value("${AUTH_LDAP_GROUP_SEARCH_FILTER}")
    private String groupSearchFilter;

    @Value("${AUTH_LDAP_GROUP_SEARCH_BASE}")
    private String groupSearchBase;



    @Value("${auth.ldap.rbac.map}")
    private String ldapRbacMap;

    private final Map<String, String> groupRoleMap = new HashMap<>();

    @PostConstruct
    public void loadRolesFromCsv() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(ldapRbacMap))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    groupRoleMap.put(parts[0], parts[1]);
                }
            }
        }
    }

    @Autowired
    private LdapAuthoritiesPopulator ldapAuthoritiesPopulator;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String formattedFilter = MessageFormat.format(ldapUserSearchFilter, username);
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        List<UserPrincipal> users = ldapTemplate.search(ldapUserSearchBase, formattedFilter, new UserPrincipalAttributesMapper());
        List<String> usersCn = ldapTemplate.search(
                ldapUserSearchBase,
                formattedFilter,
                searchControls,
                (ContextMapper<String>) ctx -> {
                    DirContextAdapter contextAdapter = (DirContextAdapter) ctx;
                    return contextAdapter.getDn().toString();
                }
        );


        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found in LDAP: " + username);
        }


        UserPrincipal user = users.get(0);
        String formattedGroupFilter = MessageFormat.format(groupSearchFilter, LdapUtils.escapeLDAPSearchFilter(usersCn.get(0) + "," + ldapBase));

        List<String> groupDns = ldapTemplate.search(
                groupSearchBase,
                formattedGroupFilter,
                searchControls,
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
        user.setAuthorities(authorities);
        return user;
    }



    private static class UserPrincipalAttributesMapper implements AttributesMapper<UserPrincipal> {
        @Override
        public UserPrincipal mapFromAttributes(Attributes attrs) throws NamingException {
            Attribute uid =  attrs.get("uid");
            Attribute sAMAccountName = attrs.get("sAMAccountName");

            if (uid != null) {
                return  new UserPrincipal(uid.get().toString(), Collections.emptyList());
            }

            if (sAMAccountName != null){
                return  new UserPrincipal(sAMAccountName.get().toString(), Collections.emptyList());
            }

            return new UserPrincipal("", Collections.emptyList());

        }
    }
}
