package com.smartshop.dto.response.promoCode;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCodeResponseDTO {

    private String id;
    private String code;
    private BigDecimal remisePourcentage;
    private Boolean disponible;

}

