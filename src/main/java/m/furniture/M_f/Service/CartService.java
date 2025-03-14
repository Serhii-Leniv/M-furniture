package m.furniture.M_f.Service;

import m.furniture.M_f.Entity.Cart;
import m.furniture.M_f.Entity.Product;
import m.furniture.M_f.Entity.User;
import m.furniture.M_f.Repository.CartRepository;
import m.furniture.M_f.Repository.ProductRepository;
import m.furniture.M_f.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public void addProductToCart(Long productId, String username) {
        User user = userRepository.findByUsername(username);
        Cart cart = cartRepository.findByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
        }

        Product product = productRepository.findById(productId).orElseThrow();
        cart.getProducts().add(product);
        cartRepository.save(cart);
    }
}