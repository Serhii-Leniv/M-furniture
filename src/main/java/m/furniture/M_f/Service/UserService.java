package m.furniture.M_f.Service;

import m.furniture.M_f.Entity.User;
import m.furniture.M_f.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Реєстрація нового користувача
    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Хешуємо пароль
        userRepository.save(user);
    }

    // Пошук користувача за іменем
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}