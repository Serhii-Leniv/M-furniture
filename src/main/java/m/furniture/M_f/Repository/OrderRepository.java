package m.furniture.M_f.Repository;

import m.furniture.M_f.Entity.Order;
import m.furniture.M_f.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}