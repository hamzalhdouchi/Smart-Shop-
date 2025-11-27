package com.smartshop.service;


import com.smartshop.dto.response.UserResponseDTO;
import jakarta.servlet.http.HttpSession;

public interface UserService {

    UserResponseDTO login(String username, String password, HttpSession session);

    void logout(HttpSession session);

    UserResponseDTO getUserById(String id);

}

