package com.smartshop.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAdvancedResponseDTO {

    private String id;
    private String nom;
    private BigDecimal prixUnitaire;
    private BigDecimal stockDisponible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean inStock;
    private String stockStatus;
}

