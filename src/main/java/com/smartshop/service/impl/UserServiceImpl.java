package com.smartshop.service.impl;


import com.smartshop.dto.response.UserResponseDTO;
import com.smartshop.entity.User;
import com.smartshop.enums.UserRole;
import com.smartshop.mapper.UserMapper;
import com.smartshop.repository.UserRepository;
import com.smartshop.service.UserService;
import com.smartshop.exception.BusinessException;
import com.smartshop.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
    import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDTO login(String username, String password, HttpSession session) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));


        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new BusinessException("Nom d'utilisateur ou mot de passe incorrect");
        }

        session.setAttribute("USER_ID", user.getId());
        session.setAttribute("USER_ROLE", user.getRole().name());
        session.setAttribute("USERNAME", user.getUsername());

        return userMapper.toUserResponseDTO(user);
    }

    @Override
    public void logout(HttpSession session) {
        session.invalidate();
    }

    @Override
    public UserResponseDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return userMapper.toUserResponseDTO(user);
    }

}

