package com.smartshop;

import com.smartshop.entity.User;
import com.smartshop.enums.UserRole;
import com.smartshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SmartShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartShopApplication.class, args);

        System.out.println("||========================================||");
        System.out.println("||      Server is work Seccessfuly        ||");
        System.out.println("||========================================||");
    }


}
