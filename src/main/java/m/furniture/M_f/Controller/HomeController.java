package m.furniture.M_f.Controller;

import m.furniture.M_f.Entity.Product;
import m.furniture.M_f.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final ProductService productService;

    @Autowired
    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/main")
    public String home(Model model) {
        // Отримати всі продукти
        List<Product> products = productService.getAllProducts();

        // Отримати унікальні категорії
        List<String> categories = products.stream()
                .map(Product::getCategory)
                .distinct()
                .toList();

        // Передати дані на сторінку
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "index";
    }
}