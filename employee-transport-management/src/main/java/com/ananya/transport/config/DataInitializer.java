package com.ananya.transport.config;
import com.ananya.transport.entity.*;
import com.ananya.transport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration public class DataInitializer {
    @Bean CommandLineRunner seedAdmin(UserRepository users, PasswordEncoder encoder,
       @Value("${app.admin.email}") String email,@Value("${app.admin.password}") String password) {
        return args->{ if(!users.existsByEmailIgnoreCase(email)) users.save(User.builder().name("System Admin").email(email.toLowerCase()).password(encoder.encode(password)).role(Role.ADMIN).build()); };
    }
}
