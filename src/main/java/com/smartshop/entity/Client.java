package com.smartshop.entity;

import com.smartshop.audit.Auditable;
import com.smartshop.enums.ClientTier;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ClientTier tier = ClientTier.BASIC;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalOrders = 0;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

}
