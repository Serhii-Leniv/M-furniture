package m.furniture.M_f.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import m.furniture.M_f.Entity.Product;
import m.furniture.M_f.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class CartService {

    @Autowired
    private ProductRepository productRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Product> getCartItems(HttpServletRequest request) {
        Map<Long, Integer> cartMap = readCartMapFromCookies(request);
        List<Product> products = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : cartMap.entrySet()) {
            productRepository.findById(entry.getKey()).ifPresent(product -> {
                for (int i = 0; i < entry.getValue(); i++) {
                    products.add(product);
                }
            });
        }

        return products;
    }

    public double calculateTotal(HttpServletRequest request) {
        Map<Long, Integer> cartMap = readCartMapFromCookies(request);
        double total = 0.0;

        for (Map.Entry<Long, Integer> entry : cartMap.entrySet()) {
            Optional<Product> productOpt = productRepository.findById(entry.getKey());
            if (productOpt.isPresent()) {
                total += productOpt.get().getPrice() * entry.getValue();
            }
        }

        return total;
    }

    public void addProductToCart(Long productId, HttpServletRequest request, HttpServletResponse response) {
        Map<Long, Integer> cartMap = readCartMapFromCookies(request);
        cartMap.put(productId, cartMap.getOrDefault(productId, 0) + 1);
        writeCartMapToCookies(cartMap, response);
    }

    public void removeProductFromCart(Long productId, HttpServletRequest request, HttpServletResponse response) {
        Map<Long, Integer> cartMap = readCartMapFromCookies(request);
        cartMap.remove(productId);
        writeCartMapToCookies(cartMap, response);
    }

    public void increaseProductQuantity(Long productId, HttpServletRequest request, HttpServletResponse response) {
        Map<Long, Integer> cartMap = readCartMapFromCookies(request);
        cartMap.put(productId, cartMap.getOrDefault(productId, 0) + 1);
        writeCartMapToCookies(cartMap, response);
    }

    public void decreaseProductQuantity(Long productId, HttpServletRequest request, HttpServletResponse response) {
        Map<Long, Integer> cartMap = readCartMapFromCookies(request);
        int count = cartMap.getOrDefault(productId, 0);
        if (count > 1) {
            cartMap.put(productId, count - 1);
        } else {
            cartMap.remove(productId);
        }
        writeCartMapToCookies(cartMap, response);
    }

    private Map<Long, Integer> readCartMapFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("cart".equals(cookie.getName())) {
                    try {
                        String decoded = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                        return objectMapper.readValue(decoded, new TypeReference<Map<Long, Integer>>() {
                        });
                    } catch (Exception e) {
                        System.err.println("Помилка при читанні кошика: " + e.getMessage());
                    }
                }
            }
        }
        return new HashMap<>();
    }

    private void writeCartMapToCookies(Map<Long, Integer> cartMap, HttpServletResponse response) {
        try {
            String json = objectMapper.writeValueAsString(cartMap);
            String encoded = URLEncoder.encode(json, StandardCharsets.UTF_8);

            Cookie cookie = new Cookie("cart", encoded);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(cookie);
        } catch (Exception e) {
            throw new RuntimeException("Помилка при записі кошика в куки", e);
        }
    }
}