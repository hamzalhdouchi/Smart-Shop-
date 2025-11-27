package com.smartshop.entity;

import com.smartshop.audit.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "commande_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Product produit;

    @Column(nullable = false)
    private Integer quantite;

    @Column(name = "prix_unitaire_ht", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaireHT;

    @Column(name = "total_ligne_ht", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalLigneHT;
}

