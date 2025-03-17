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

import java.net.URLEncoder;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private ProductRepository productRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Product> getCartItems(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("cart")) {
                    String encodedCartJson = cookie.getValue();
                    String cartJson = decodeCookieValue(encodedCartJson); // Декодуємо значення куки
                    try {
                        return parseCartJson(cartJson);
                    } catch (Exception e) {
                        System.err.println("Помилка при парсингу JSON кошика: " + e.getMessage());
                        return new ArrayList<>();
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    public void addProductToCart(Long productId, HttpServletRequest request, HttpServletResponse response) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Товар не знайдено: productId = " + productId));

            List<Product> cartItems = getCartItems(request);
            cartItems.add(product);

            String cartJson = convertCartToJson(cartItems);
            System.out.println("JSON кошика: " + cartJson); // Логування JSON

            // Кодуємо JSON перед збереженням у куці
            String encodedCartJson = encodeCookieValue(cartJson);

            Cookie cartCookie = new Cookie("cart", encodedCartJson);
            cartCookie.setMaxAge(7 * 24 * 60 * 60); // Термін дії куки - 7 днів
            cartCookie.setPath("/"); // Встановлюємо шлях для куки
            response.addCookie(cartCookie);
        } catch (Exception e) {
            System.err.println("Помилка при додаванні товару до кошика: " + e.getMessage());
            throw new RuntimeException("Помилка при додаванні товару до кошика", e);
        }
    }

    public void removeProductFromCart(Long productId, HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Product> cartItems = getCartItems(request);

            // Знаходимо перший товар з вказаним ID і видаляємо його
            Product productToRemove = cartItems.stream()
                    .filter(product -> product.getId().equals(productId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Товар не знайдено: productId = " + productId));

            cartItems.remove(productToRemove); // Видаляємо лише один товар

            String cartJson = convertCartToJson(cartItems);
            String encodedCartJson = encodeCookieValue(cartJson);

            Cookie cartCookie = new Cookie("cart", encodedCartJson);
            cartCookie.setMaxAge(7 * 24 * 60 * 60); // Термін дії куки - 7 днів
            cartCookie.setPath("/"); // Встановлюємо шлях для куки
            response.addCookie(cartCookie);
        } catch (Exception e) {
            System.err.println("Помилка при видаленні товару з кошика: " + e.getMessage());
            throw new RuntimeException("Помилка при видаленні товару з кошика", e);
        }
    }


    private List<Product> parseCartJson(String cartJson) {
        try {
            return objectMapper.readValue(cartJson, new TypeReference<List<Product>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Помилка при парсингу JSON кошика", e);
        }
    }

    private String convertCartToJson(List<Product> cartItems) {
        try {
            return objectMapper.writeValueAsString(cartItems);
        } catch (Exception e) {
            throw new RuntimeException("Помилка при конвертації кошика в JSON", e);
        }
    }

    // Кодуємо значення куки
    private String encodeCookieValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            throw new RuntimeException("Помилка при кодуванні значення куки", e);
        }
    }

    // Декодуємо значення куки
    private String decodeCookieValue(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            throw new RuntimeException("Помилка при декодуванні значення куки", e);
        }
    }
}