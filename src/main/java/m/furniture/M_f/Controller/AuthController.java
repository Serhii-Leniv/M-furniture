package m.furniture.M_f.Controller;

import m.furniture.M_f.Entity.User;
import m.furniture.M_f.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Повертає шаблон login.html
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("user") User user,
            BindingResult result,
            Model model // Додайте параметр Model
    ) {
        if (userService.userExists(user.getUsername())) {
            result.rejectValue("username", "error.user", "Цей логін вже зайнятий");
            model.addAttribute("user", user); // Додайте цей рядок
            return "register";
        }
        if (userService.emailExists(user.getEmail())) {
            result.rejectValue("email", "error.user", "Ця пошта вже використовується");
            model.addAttribute("user", user); // Додайте цей рядок
            return "register";
        }
        if (userService.phoneExists(user.getPhone())) {
            result.rejectValue("phone", "error.user", "Цей телефон вже зареєстровано");
            model.addAttribute("user", user); // Додайте цей рядок
            return "register";
        }

        userService.registerUser(user);
        return "redirect:/login?registerSuccess";
    }
}