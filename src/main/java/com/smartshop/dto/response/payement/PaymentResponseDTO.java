package com.smartshop.dto.response.payement;

import com.smartshop.enums.PaymentStatus;
import com.smartshop.enums.PaymentType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {

    private String id;
    private String orderId;
    private Integer paymentNumber;
    private String reference;
    private BigDecimal montant;
    private LocalDate datePayment;
    private LocalDate dateEcheance;
    private PaymentType typePayment;
    private PaymentStatus paymentStatus;

}

