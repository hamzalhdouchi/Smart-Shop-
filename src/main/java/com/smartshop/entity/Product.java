package com.smartshop.entity;


import com.smartshop.audit.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "name" ,nullable=false)
    private String nom;

    @Column(name = "prix",nullable = false)
    private BigDecimal prix_unitair;

    @Column(name = "stock")
    private BigDecimal stockDisponible;

}
