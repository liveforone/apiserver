package apiserver.apiserver.entity.repository.role;

import apiserver.apiserver.entity.member.Role;
import apiserver.apiserver.entity.member.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleType(RoleType roleType);
}
