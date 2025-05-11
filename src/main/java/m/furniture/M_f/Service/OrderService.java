package m.furniture.M_f.Service;

import m.furniture.M_f.Entity.Order;
import m.furniture.M_f.Entity.User;
import m.furniture.M_f.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public void saveOrder(Order order) {
        order.getOrderItems().forEach(item -> item.setOrder(order));
        orderRepository.save(order);
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    @Transactional
    public void deleteOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Cannot delete another user's order");
        }

        orderRepository.delete(order);
    }

}