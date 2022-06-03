package uaa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uaa.domain.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role,Long> {

}
