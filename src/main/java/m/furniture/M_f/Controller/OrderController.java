package m.furniture.M_f.Controller;

import m.furniture.M_f.Entity.Order;
import m.furniture.M_f.Entity.User;
import m.furniture.M_f.Service.OrderService;
import m.furniture.M_f.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

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
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            Order order = new Order();
            order.setUser(user);
            order.setStatus("PENDING");
            orderService.saveOrder(order);

            return "redirect:/main";
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
}