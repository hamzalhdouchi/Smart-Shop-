package com.smartshop.dto.response.order;

import com.smartshop.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

    private String id;
    private String clientId;
    private String clientNom;
    private Integer itemsCount;
    private BigDecimal sousTotalHT;
    private BigDecimal montantRemise;
    private BigDecimal montantHTApresRemise;
    private BigDecimal tauxTVA;
    private BigDecimal montantTVA;
    private BigDecimal totalTTC;
    private BigDecimal montantRestant;
    private String codePromo;
    private BigDecimal remiseFidelite;
    private BigDecimal remisePromo;
    private OrderStatus statut;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
