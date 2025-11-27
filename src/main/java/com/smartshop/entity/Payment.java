package com.smartshop.entity;

import com.smartshop.audit.Auditable;
import com.smartshop.enums.PaiementStatus;
import com.smartshop.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "paiements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    private Order order;

    @Column(name = "numero_paiement", nullable = false)
    private Integer numeroPaiement;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_paiement", nullable = false, length = 20)
    private PaymentType typePaiement;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_status", nullable = false, length = 20)
    private PaiementStatus paiementStatus;


    @Column(name = "reference_paiement", length = 100)
    private String referencePaiement;

    @Column(length = 100)
    private String banque;

    @Column(name = "date_paiement", nullable = false)
    private LocalDate datePaiement;
    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Column(name = "date_encaissement")
    private LocalDate dateEncaissement;

}

