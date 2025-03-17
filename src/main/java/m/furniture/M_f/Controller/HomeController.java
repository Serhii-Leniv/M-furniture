package m.furniture.M_f.Controller;

import m.furniture.M_f.Entity.Product;
import m.furniture.M_f.Service.CartService;
import m.furniture.M_f.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @GetMapping("/main")
    public String home(Model model, HttpServletRequest request) {
        // Отримати всі продукти
        List<Product> products = productService.getAllProducts();

        // Отримати унікальні категорії
        List<String> categories = products.stream()
                .map(Product::getCategory)
                .distinct()
                .toList();

        // Отримати товари з кошика з кук
        List<Product> cartItems = cartService.getCartItems(request);

        // Передати дані на сторінку
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("cartItems", cartItems);

        return "index";
    }
}