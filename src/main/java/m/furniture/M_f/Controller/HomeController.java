package m.furniture.M_f.Controller;

import m.furniture.M_f.Entity.Call;
import m.furniture.M_f.Entity.Product;
import m.furniture.M_f.Service.CallService;
import m.furniture.M_f.Service.CartService;
import m.furniture.M_f.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CallService callService;

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
        model.addAttribute("allProducts", products); // Для пошуку

        return "index";
    }

    @PostMapping("/save-phone")
    @ResponseBody
    public ResponseEntity<String> savePhoneNumber(@RequestParam String phoneNumber) {
        try {
            Call call = new Call();
            call.setPhoneNumber(phoneNumber);
            call.setTimestamp(String.valueOf(LocalDateTime.now()));

            callService.savePhone(call);
            return ResponseEntity.ok("Дякуємо! Ми зв'яжемося з вами найближчим часом.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Помилка при збереженні номера: " + e.getMessage());
        }
    }
    }

