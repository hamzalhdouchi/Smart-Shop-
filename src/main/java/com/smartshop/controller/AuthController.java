package com.smartshop.controller;

import com.smartshop.apiResponse.ApiResponseDTO;
import com.smartshop.dto.requist.createRequistDto.LoginRequestDTO;
import com.smartshop.dto.response.UserResponseDTO;
import com.smartshop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final HttpServletRequest request;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO loginRequest,
            HttpSession session) {

        UserResponseDTO user = userService.login(
                loginRequest.getUsername(),
                loginRequest.getPassword(),
                session
        );


        ApiResponseDTO<UserResponseDTO> response = ApiResponseDTO.success(
                user,
                "Login successful"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO<Void>> logout(HttpSession session) {

        userService.logout(session);

        ApiResponseDTO<Void> response = ApiResponseDTO.success("Logout successful");
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/session")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getSessionInfo(HttpSession session) {

        String userId = (String) session.getAttribute("USER_ID");

        if (userId == null) {
            ApiResponseDTO<UserResponseDTO> response = ApiResponseDTO.error("No active session");
            response.setPath(request.getRequestURI());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // ❌ ERREUR: vous appelez getUserByUsername avec un ID
        // UserResponseDTO user = userService.getUserByUsername(userId);

        // ✅ CORRECTION: appelez getUserById
        UserResponseDTO user = userService.getUserById(userId);

        ApiResponseDTO<UserResponseDTO> response = ApiResponseDTO.success(
                user,
                "Session information retrieved"
        );
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }
}
