package m.furniture.M_f.Controller;

import m.furniture.M_f.Entity.Product;
import m.furniture.M_f.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/product/{id}")
    public String getProductDetails(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "error/404";
        }
        model.addAttribute("product", product);
        return "product-details";
    }

    @GetMapping("/get-subcategories/{category}")
    @ResponseBody
    public List<String> getSubcategories(@PathVariable String category) {
        return productService.getProductsByCategory(category).stream()
                .map(Product::getSubcategory)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @GetMapping("/api/products/{category}")
    @ResponseBody
    public List<Product> getProductsByCategoryApi(
            @PathVariable String category
    ) {
        return productService.getProductsByCategory(category);
    }

    @GetMapping("/api/products/{category}/{subcategory}")
    @ResponseBody
    public List<Product> getProductsBySubcategoryApi(
            @PathVariable String category,
            @PathVariable String subcategory
    ) {
        System.out.println("Request received for category: " + category + ", subcategory: " + subcategory);
        List<Product> products = productService.getProductsByCategoryAndSubcategory(category, subcategory);
        System.out.println("Found products: " + products.size());
        return products;
    }
}