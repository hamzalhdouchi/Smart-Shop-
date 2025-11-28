package com.smartshop.dto.response.order;

import com.smartshop.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAdvancedResponseDTO {

    private String id;
    private ClientSummaryDTO client;
    private List<OrderItemResponseDTO> items;
    private BigDecimal sousTotalHT;
    private BigDecimal montantRemise;
    private BigDecimal montantHTApresRemise;
    private BigDecimal tauxTVA;
    private BigDecimal montantTVA;
    private BigDecimal totalTTC;
    private BigDecimal montantRestant;
    private String codePromo;
    private BigDecimal remiseFidelite;
    private BigDecimal remisePromo;
    private OrderStatus statut;
    private List<PaymentSummaryDTO> paiements;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClientSummaryDTO {
        private String id;
        private String nom;
        private String email;
        private String tier;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponseDTO {
        private String produitId;
        private String produitNom;
        private Integer quantite;
        private BigDecimal prixUnitaireHT;
        private BigDecimal totalLigneHT;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentSummaryDTO {
        private String id;
        private Integer numeroPaiement;
        private BigDecimal montant;
        private String typePaiement;
        private String statut;
        private LocalDateTime datePaiement;
    }
}

