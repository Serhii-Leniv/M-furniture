package m.furniture.M_f.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import m.furniture.M_f.Entity.Order;
import m.furniture.M_f.Entity.OrderItem;
import m.furniture.M_f.Entity.Product;
import m.furniture.M_f.Entity.User;
import m.furniture.M_f.Repository.ProductRepository;
import m.furniture.M_f.Service.CartService;
import m.furniture.M_f.Service.OrderService;
import m.furniture.M_f.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;


    @PostMapping("/order")
    public String placeOrder(HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            // Отримуємо товари з кошика
            Map<Long, Integer> cartItems = cartService.getCartMap(request);

            // Створюємо нове замовлення
            Order order = new Order();
            order.setUser(user);
            order.setStatus("Очікує підтвердження");
            order.setCreatedAt(LocalDateTime.now());

            // Додаємо товари до замовлення
            for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
                Product product = productRepository.findById(entry.getKey())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(entry.getValue());
                orderItem.setPriceAtPurchase(product.getPrice());

                order.getOrderItems().add(orderItem);
            }

            // Зберігаємо замовлення
            orderService.saveOrder(order);

            // Очищаємо кошик
            cartService.clearCart(response);

            return "redirect:/orders";
        }
        return "redirect:/login";
    }

    @GetMapping("/orders")
    public String getUserOrders(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        List<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("orders", orders);
        return "orders";
    }

    @PostMapping("/orders/{id}/delete")
    public String deleteOrder(@PathVariable Long id, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        orderService.deleteOrder(id, user);
        return "redirect:/orders?delete_success";
    }

}