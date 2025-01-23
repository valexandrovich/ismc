package ua.com.valexa.apigateway.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(schema = "sys", name = "ldap_group_role_mapping")
@Getter
@Setter
public class LdapGroupRoleMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ldapGroupDn;
    private String role;
}
