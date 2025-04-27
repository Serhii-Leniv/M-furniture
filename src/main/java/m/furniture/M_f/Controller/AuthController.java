package m.furniture.M_f.Controller;

import m.furniture.M_f.Entity.User;
import m.furniture.M_f.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    public String registerUser(@ModelAttribute("user") User user, BindingResult result) {
        if (userService.userExists(user.getUsername())) {
            result.rejectValue("username", "error.user", "Цей логін вже зайнятий");
            return "register";
        }
        if (userService.emailExists(user.getEmail())) {
            result.rejectValue("email", "error.user", "Ця електронна пошта вже використовується");
            return "register";
        }

        userService.registerUser(user);
        return "redirect:/login?registerSuccess";
    }
}