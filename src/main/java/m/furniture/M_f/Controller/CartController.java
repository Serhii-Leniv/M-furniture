package m.furniture.M_f.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import m.furniture.M_f.Entity.Product;
import m.furniture.M_f.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/items")
    public ResponseEntity<Map<String, Object>> getCartItems(HttpServletRequest request) {
        List<Product> items = cartService.getCartItems(request);
        double total = cartService.calculateTotal(request);

        // Додаємо URL зображень до кожного товару
        List<Map<String, Object>> enrichedItems = items.stream()
                .map(item -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("id", item.getId());
                    itemMap.put("name", item.getName());
                    itemMap.put("price", item.getPrice());
                    itemMap.put("imageUrl", item.getImageUrl() != null ? "/" + item.getImageUrl() : "/no-image.png");
                    return itemMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("items", enrichedItems);
        response.put("total", total);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addToCart(@RequestParam Long productId,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) {
        try {
            cartService.addProductToCart(productId, request, response);
            return ResponseEntity.ok(Map.of("message", "Товар додано в кошик"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, String>> removeFromCart(@RequestParam Long productId,
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) {
        try {
            cartService.removeProductFromCart(productId, request, response);
            return ResponseEntity.ok(Map.of("message", "Товар видалено з кошика"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/increase")
    public ResponseEntity<Map<String, String>> increaseQuantity(@RequestParam Long productId,
                                                                HttpServletRequest request,
                                                                HttpServletResponse response) {
        try {
            cartService.increaseProductQuantity(productId, request, response);
            return ResponseEntity.ok(Map.of("message", "Кількість збільшено"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/decrease")
    public ResponseEntity<Map<String, String>> decreaseQuantity(@RequestParam Long productId,
                                                                HttpServletRequest request,
                                                                HttpServletResponse response) {
        try {
            cartService.decreaseProductQuantity(productId, request, response);
            return ResponseEntity.ok(Map.of("message", "Кількість зменшено"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }
}