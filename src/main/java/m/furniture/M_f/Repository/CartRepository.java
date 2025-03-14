package m.furniture.M_f.Repository;

import m.furniture.M_f.Entity.Cart;
import m.furniture.M_f.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUser(User user);
}