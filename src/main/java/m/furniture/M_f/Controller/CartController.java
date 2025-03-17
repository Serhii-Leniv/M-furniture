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

@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/cart/items")
    public List<Product> getCartItems(HttpServletRequest request) {
        return cartService.getCartItems(request);
    }

    @PostMapping("/cart/add")
    public ResponseEntity<Map<String, String>> addToCart(@RequestParam Long productId, HttpServletRequest request, HttpServletResponse response) {
        try {
            cartService.addProductToCart(productId, request, response);

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Товар додано в кошик");
            return ResponseEntity.ok(responseBody);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}