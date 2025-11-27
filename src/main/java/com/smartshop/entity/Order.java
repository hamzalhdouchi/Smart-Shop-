package com.smartshop.entity;

import com.smartshop.audit.Auditable;
import com.smartshop.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "sous_total_ht", nullable = false, precision = 10, scale = 2)
    private BigDecimal sousTotalHT;

    @Column(name = "montant_remise", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantRemise = BigDecimal.ZERO;

    @Column(name = "montant_ht_apres_remise", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantHTApresRemise;

    @Column(name = "taux_tva", nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxTVA = new BigDecimal("20.00");

    @Column(name = "montant_tva", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTVA;

    @Column(name = "total_ttc", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalTTC;

    @Column(name = "montant_restant", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantRestant;

    @Column(name = "code_promo", length = 50)
    private String codePromo;

    @Column(name = "remise_fidelite", precision = 5, scale = 2)
    private BigDecimal remiseFidelite = BigDecimal.ZERO;

    @Column(name = "remise_promo", precision = 5, scale = 2)
    private BigDecimal remisePromo = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus statut = OrderStatus.PENDING;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> paiements = new ArrayList<>();

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "date_annulation")
    private LocalDateTime dateAnnulation;
}
