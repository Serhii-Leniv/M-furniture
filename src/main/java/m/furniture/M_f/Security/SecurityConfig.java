package m.furniture.M_f.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/main", "/register", "/login").permitAll() // Дозволити доступ для всіх
                        .requestMatchers("/order/**").authenticated() // Обмежити доступ для зареєстрованих
                        .anyRequest().permitAll() // Дозволити доступ до решти (якщо потрібно)
                )
                .formLogin(form -> form
                        .loginPage("/login") // Сторінка входу
                        .defaultSuccessUrl("/main") // Перенаправлення після успішного входу
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL для виходу
                        .logoutSuccessUrl("/main") // Перенаправлення після виходу
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable()); // Вимикаємо CSRF (якщо потрібно)

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Для хешування паролів
    }
}