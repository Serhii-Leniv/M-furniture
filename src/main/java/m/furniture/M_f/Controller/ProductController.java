package m.furniture.M_f.Controller;

import m.furniture.M_f.Entity.Product;
import m.furniture.M_f.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/product/{id}")
    public String getProductDetails(@PathVariable Long id, Model model) {
        // Отримати продукт за ID
        Product product = productService.getProductById(id);
        if (product == null) {
            return "error/404"; // Повернути сторінку помилки, якщо продукт не знайдено
        }

        // Передати продукт на сторінку
        model.addAttribute("product", product);
        return "product-details"; // Назва шаблону для сторінки товару
    }
}