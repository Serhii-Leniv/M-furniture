package m.furniture.M_f.Controller;

import m.furniture.M_f.Entity.Order;
import m.furniture.M_f.Entity.User;
import m.furniture.M_f.Service.OrderService;
import m.furniture.M_f.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping("/order")
    public String placeOrder() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // Отримуємо поточного користувача
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            // Створюємо нове замовлення
            Order order = new Order();
            order.setUser(user);
            // Додаємо продукти з кошика (логіка для кошика)
            order.setStatus("PENDING");

            // Зберігаємо замовлення
            orderService.saveOrder(order);

            return "redirect:/main";
        } else {
            return "redirect:/login"; // Перенаправлення на сторінку входу
        }
    }
}