package com.smartshop.dto.requist.createRequistDto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCodeCreateDTO {


    @NotNull
    @DecimalMin(value = "0.01")
    @DecimalMax(value = "100.00")
    private BigDecimal remisePourcentage;

    private Boolean disponible = true;
}

