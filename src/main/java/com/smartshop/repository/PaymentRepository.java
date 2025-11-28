package com.smartshop.repository;

import com.smartshop.entity.Payment;
import com.smartshop.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    List<Payment> findByOrderId(String orderId);

    int countByOrderId(String orderId);

    List<Payment> findByPaymentStatus(PaymentStatus status);
}

