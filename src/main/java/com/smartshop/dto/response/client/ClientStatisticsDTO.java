package com.smartshop.dto.response.client;

import com.smartshop.enums.ClientTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientStatisticsDTO {
    private String clientId;
    private String nom;
    private String email;
    private ClientTier tier;
    private Integer totalOrders;
    private BigDecimal totalSpent;

}
