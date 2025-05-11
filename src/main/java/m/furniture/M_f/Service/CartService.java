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

    public Map<Long, Integer> getCartMap(HttpServletRequest request) {
        return readCartMapFromCookies(request);
    }

    public void clearCart(HttpServletResponse response) {
        writeCartMapToCookies(new HashMap<>(), response);
    }


    public double calculateTotal(HttpServletRequest request) {
        return readCartMapFromCookies(request).entrySet().stream()
                .mapToDouble(entry -> productRepository.findById(entry.getKey())
                        .map(p -> p.getPrice() * entry.getValue())
                        .orElse(0.0))
                .sum();
    }

    public void addProductToCart(Long productId, HttpServletRequest request, HttpServletResponse response) {
        updateCart(productId, request, response, count -> count + 1);
    }

    public void removeProductFromCart(Long productId, HttpServletRequest request, HttpServletResponse response) {
        updateCart(productId, request, response, count -> 0);
    }

    public void increaseProductQuantity(Long productId, HttpServletRequest request, HttpServletResponse response) {
        updateCart(productId, request, response, count -> count + 1);
    }

    public void decreaseProductQuantity(Long productId, HttpServletRequest request, HttpServletResponse response) {
        updateCart(productId, request, response, count -> count > 1 ? count - 1 : 0);
    }

    private void updateCart(Long productId,
                            HttpServletRequest request,
                            HttpServletResponse response,
                            java.util.function.Function<Integer, Integer> updateFunction) {
        Map<Long, Integer> cartMap = readCartMapFromCookies(request);
        int currentCount = cartMap.getOrDefault(productId, 0);
        int newCount = updateFunction.apply(currentCount);

        if (newCount > 0) {
            cartMap.put(productId, newCount);
        } else {
            cartMap.remove(productId);
        }
        writeCartMapToCookies(cartMap, response);
    }

    private Map<Long, Integer> readCartMapFromCookies(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("cart".equals(cookie.getName())) {
                        String decoded = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                        return objectMapper.readValue(decoded, new TypeReference<Map<Long, Integer>>() {
                        });
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Помилка читання кошика: " + e.getMessage());
        }
        return new HashMap<>();
    }

    private void writeCartMapToCookies(Map<Long, Integer> cartMap, HttpServletResponse response) {
        try {
            String json = objectMapper.writeValueAsString(cartMap);
            String encoded = URLEncoder.encode(json, StandardCharsets.UTF_8);

            Cookie cookie = new Cookie("cart", encoded);
            cookie.setPath("/");
            cookie.setMaxAge(365 * 24 * 60 * 60); // 1 рік
            cookie.setSecure(false);
            cookie.setHttpOnly(false);
            response.addCookie(cookie);
        } catch (Exception e) {
            throw new RuntimeException("Помилка запису кошика", e);
        }
    }
}