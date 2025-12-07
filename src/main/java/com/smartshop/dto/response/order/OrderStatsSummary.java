package com.smartshop.dto.response.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatsSummary {
    private Long totalConfirmedOrders;
    private BigDecimal totalConfirmedAmount;
    private Long totalPendingOrders;
    private Long totalCancelledOrders;
    private BigDecimal averageOrderAmount;
}

