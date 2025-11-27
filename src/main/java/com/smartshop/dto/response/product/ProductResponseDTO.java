package com.smartshop.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {

    private String id;
    private String nom;
    private BigDecimal prixUnitaire;
    private BigDecimal stockDisponible;
}

