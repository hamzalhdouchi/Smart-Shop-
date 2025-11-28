package com.smartshop.dto.requist.updateRequistDto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderUpdateDTO {

    @Pattern(regexp = "PROMO-[A-Z0-9]{5}", message = "Invalid promo code format. Expected: PROMO-XXXXX")
    private String codePromo;

    private List<OrderItemUpdateDTO> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemUpdateDTO {

        @NotNull(message = "Product ID is required")
        private String produitId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantite;
    }
}

