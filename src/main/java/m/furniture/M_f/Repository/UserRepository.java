package m.furniture.M_f.Repository;

import m.furniture.M_f.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);
}