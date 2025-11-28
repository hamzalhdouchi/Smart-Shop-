package com.smartshop.dto.requist.createRequistDto;

import com.smartshop.enums.PaymentType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCreateDTO {

    @NotNull(message = "Order ID is required")
    private String orderId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal montant;

    @NotNull(message = "Payment type is required")
    private PaymentType typePayment;

    @Size(max = 100, message = "Reference cannot exceed 100 characters")
    private String reference;

    private LocalDate dateEcheance;
}

