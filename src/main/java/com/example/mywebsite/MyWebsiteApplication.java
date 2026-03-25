package com.example.mywebsite;

import com.example.mywebsite.entity.User;
import com.example.mywebsite.repository.UserRepository;
import com.example.mywebsite.service.CartService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MyWebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyWebsiteApplication.class, args);
    }


    @Bean
    CommandLineRunner commandLineRunner(UserRepository users, PasswordEncoder encoder, CartService cartService) {
        return args -> {
            if (!users.existsByUsername("admin")) {
                User admin = new User("admin", encoder.encode("admin"), "ROLE_USER,ROLE_ADMIN");
                users.save(admin);
                cartService.assignCart(admin.getId());
            }
        };
    }
}
