package com.smartshop;

import com.smartshop.dto.requist.createRequistDto.UserClientRegistrationDTO;
import com.smartshop.entity.User;
import com.smartshop.enums.UserRole;
import com.smartshop.mapper.ClientMapper;
import com.smartshop.repository.UserRepository;
import com.smartshop.service.ClientService;
import com.smartshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
//@RequiredArgsConstructor
public class SmartShopApplication {

//    private final ClientService userRepository;
//    private final ClientMapper userService;

    public static void main(String[] args) {
        SpringApplication.run(SmartShopApplication.class, args);

        System.out.println("||========================================||");
        System.out.println("||      Server is working successfully    ||");
        System.out.println("||========================================||");
    }

//    @Bean
//    CommandLineRunner initDatabase() {
//        return args -> {
//            UserClientRegistrationDTO userClientRegistrationDTO = new UserClientRegistrationDTO();
//            userClientRegistrationDTO.setUsername("admin");
//            userClientRegistrationDTO.setPassword("admin");
//            userClientRegistrationDTO.setRole(UserRole.ADMIN);
//            userClientRegistrationDTO.setNom("admindd");
//            userClientRegistrationDTO.setEmail("admindd@gmail.com");
//            userRepository.create(userClientRegistrationDTO);
//        };
//    }
}
