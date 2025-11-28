package com.smartshop.dto.response.payement;

import com.smartshop.enums.OrderStatus;
import com.smartshop.enums.PaymentStatus;
import com.smartshop.enums.PaymentType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentAdvancedResponseDTO {

    private String id;
    private Integer paymentNumber;
    private String reference;
    private BigDecimal montant;
    private LocalDate datePayment;
    private LocalDate dateEcheance;
    private PaymentType typePayment;
    private PaymentStatus paymentStatus;
    private OrderSummaryDTO order;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderSummaryDTO {
        private String id;
        private String clientId;
        private String clientNom;
        private BigDecimal totalTTC;
        private BigDecimal montantRestant;
        private OrderStatus statut;
        private LocalDate dateCreation;
    }
}

