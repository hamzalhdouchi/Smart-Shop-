package com.smartshop.dto.response.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatisticsDTO {
    private Long totalConfirmedOrders;
    private BigDecimal totalConfirmedAmount;
    private LocalDateTime lastUpdated;

    // Bonus : stats supplémentaires utiles
    private Long totalPendingOrders;
    private Long totalCancelledOrders;
    private BigDecimal averageOrderAmount;
}

