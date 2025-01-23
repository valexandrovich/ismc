package ua.com.valexa.sandbox.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import ua.com.valexa.sandbox.utils.LdapUtils;

import javax.naming.directory.SearchControls;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

@RequiredArgsConstructor
public class CustomAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    @Value("${AUTH_LDAP_BASE}")
    private String ldapBase;

    @Value("${AUTH_LDAP_GROUP_SEARCH_FILTER}")
    private String ldapGroupSearchFilter;

    @Value("${auth.ldap.rbac.map}")
    private String ldapRbacMap;

    private final LdapTemplate ldapTemplate;

    private final String ldapGroupSearchBase;

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

    @Override
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        String formattedFilter = MessageFormat.format(ldapGroupSearchFilter, LdapUtils.escapeLDAPSearchFilter(userData.getDn().toString() + "," + ldapBase));
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        List<String> groupDns = ldapTemplate.search(
                ldapGroupSearchBase,
                formattedFilter,
                searchControls,
                (ContextMapper<String>) ctx -> {
                    DirContextAdapter contextAdapter = (DirContextAdapter) ctx;
                    return contextAdapter.getDn().toString();
                }
        );

        for (String groupDn : groupDns) {
            String fullGroup = groupDn + "," + ldapBase;
            String role = groupRoleMap.get(fullGroup.toUpperCase());
            if (role != null) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }
            return authorities;
    }

}
