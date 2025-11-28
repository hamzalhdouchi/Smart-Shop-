package com.smartshop.service;

import com.smartshop.dto.requist.createRequistDto.PaymentCreateDTO;
import com.smartshop.dto.response.payement.PaymentAdvancedResponseDTO;
import com.smartshop.dto.response.payement.PaymentResponseDTO;
import com.smartshop.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {

    PaymentResponseDTO createPayment(PaymentCreateDTO dto);

    PaymentResponseDTO getPaymentById(String paymentId);

    PaymentAdvancedResponseDTO getPaymentByIdAdvanced(String paymentId);

    List<PaymentResponseDTO> getPaymentsByOrder(String orderId);

    Page<PaymentResponseDTO> getAllPayments(Pageable pageable);

    PaymentResponseDTO updatePaymentStatus(String paymentId, PaymentStatus status);

    void deletePayment(String paymentId);
}

