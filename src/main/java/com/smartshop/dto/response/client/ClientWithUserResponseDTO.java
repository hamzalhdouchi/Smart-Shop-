package com.smartshop.dto.response.client;

import com.smartshop.dto.response.UserResponseDTO;
import com.smartshop.enums.ClientTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientWithUserResponseDTO {
    private String id;
    private String nom;
    private String email;
    private ClientTier tier;
    private UserResponseDTO user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
