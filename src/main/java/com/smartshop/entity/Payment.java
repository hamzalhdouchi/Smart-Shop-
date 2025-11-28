package com.smartshop.entity;

import com.smartshop.audit.Auditable;
import com.smartshop.enums.PaymentStatus;
import com.smartshop.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Integer paymentNumber;
    private String reference;
    private BigDecimal montant;
    private LocalDate datePayment = LocalDate.now();
    private LocalDate dateEcheance;;

    @Enumerated(EnumType.STRING)
    private PaymentType typePayment;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Order order;
}
