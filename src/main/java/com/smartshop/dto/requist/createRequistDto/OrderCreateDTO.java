package com.smartshop.dto.requist.createRequistDto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateDTO {

    @NotNull(message = "Client ID is required")
    private String clientId;

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemCreateDTO> items;

    @Pattern(regexp = "PROMO-[A-Z0-9]{5}", message = "Invalid promo code format. Expected: PROMO-XXXXX")
    private String codePromo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemCreateDTO {

        @NotNull(message = "Product ID is required")
        private String produitId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantite;
    }
}

