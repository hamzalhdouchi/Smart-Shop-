package com.smartshop.service.impl;

import com.smartshop.dto.response.UserResponseDTO;
import com.smartshop.entity.User;
import com.smartshop.mapper.UserMapper;
import com.smartshop.repository.UserRepository;
import com.smartshop.service.UserService;
import com.smartshop.exception.BusinessException;
import com.smartshop.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDTO login(String username, String password, HttpSession session) {
        log.info("Starting UserService.login for username={}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Login failed - user not found for username={}", username);
                    return new ResourceNotFoundException("Utilisateur introuvable");
                });

        if (!BCrypt.checkpw(password, user.getPassword())) {
            log.warn("Login failed - invalid credentials for username={}", username);
            throw new BusinessException("Nom d'utilisateur ou mot de passe incorrect");
        }

        session.setAttribute("USER_ID", user.getId());
        session.setAttribute("USER_ROLE", user.getRole().name());
        session.setAttribute("USERNAME", user.getUsername());

        log.info("User logged in successfully, username={}, role={}", user.getUsername(), user.getRole());
        return userMapper.toUserResponseDTO(user);
    }

    @Override
    public void logout(HttpSession session) {
        Object userId = session.getAttribute("USER_ID");
        log.info("Logging out user with id={}", userId);
        session.invalidate();
        log.info("Session invalidated successfully for userId={}", userId);
    }

    @Override
    public UserResponseDTO getUserById(String id) {
        log.info("Starting UserService.getUserById with id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id={}", id);
                    return new ResourceNotFoundException("Utilisateur introuvable");
                });

        log.info("Finished UserService.getUserById with id={}", id);
        return userMapper.toUserResponseDTO(user);
    }

}
