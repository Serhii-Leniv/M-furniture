package m.furniture.M_f.Repository;

import m.furniture.M_f.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}