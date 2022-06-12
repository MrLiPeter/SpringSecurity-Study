package uaa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uaa.domain.User;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findOptionalByUsername(String username);
    long countByUsername(String username);
    long countByEmail(String email);
    long countByMobile(String mobile);
}
