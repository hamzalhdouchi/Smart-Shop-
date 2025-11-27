package com.smartshop.dto.response;

import com.smartshop.entity.User;
import com.smartshop.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {
    private String id;
    private String username;
    private UserRole role;

}

