package com.smartshop.repository;

import com.smartshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findAllByClientId(String clientId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.statut = 'CONFIRMED'")
    Long countConfirmedOrders();

    @Query("SELECT COALESCE(SUM(o.totalTTC), 0) FROM Order o WHERE o.statut = 'CONFIRMED'")
    BigDecimal sumConfirmedOrdersAmount();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.statut = 'PENDING'")
    Long countPendingOrders();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.statut = 'CANCELED'")
    Long countCancelledOrders();

    @Query("SELECT COALESCE(AVG(o.totalTTC), 0) FROM Order o WHERE o.statut = 'CONFIRMED'")
    BigDecimal averageConfirmedOrderAmount();
}
