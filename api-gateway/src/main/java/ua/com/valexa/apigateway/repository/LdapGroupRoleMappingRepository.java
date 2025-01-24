package ua.com.valexa.apigateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.valexa.apigateway.model.LdapGroupRoleMapping;

@Repository
public interface LdapGroupRoleMappingRepository extends JpaRepository<LdapGroupRoleMapping, Long> {
}
