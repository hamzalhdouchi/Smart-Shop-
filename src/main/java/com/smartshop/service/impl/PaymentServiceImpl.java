package com.smartshop.service.impl;

import com.smartshop.dto.requist.createRequistDto.PaymentCreateDTO;
import com.smartshop.dto.response.payement.PaymentAdvancedResponseDTO;
import com.smartshop.dto.response.payement.PaymentResponseDTO;
import com.smartshop.entity.Order;
import com.smartshop.entity.Payment;
import com.smartshop.enums.PaymentStatus;
import com.smartshop.exception.BusinessException;
import com.smartshop.exception.ResourceNotFoundException;
import com.smartshop.mapper.PaymentMapper;
import com.smartshop.repository.OrderRepository;
import com.smartshop.repository.PaymentRepository;
import com.smartshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public PaymentResponseDTO createPayment(PaymentCreateDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("No order found with ID: " + dto.getOrderId()));

        if (dto.getMontant().compareTo(order.getMontantRestant()) > 0) {
            throw new BusinessException("Payment amount (" + dto.getMontant() +
                    ") exceeds remaining amount (" + order.getMontantRestant() + ")");
        }

        int paymentCount = paymentRepository.countByOrderId(dto.getOrderId());

        Payment payment = Payment.builder()
                .order(order)
                .paymentNumber(paymentCount + 1)
                .montant(dto.getMontant())
                .typePayment(dto.getTypePayment())
                .reference(dto.getReference())
                .dateEcheance(dto.getDateEcheance())
                .paymentStatus(PaymentStatus.ENCAISSE)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        updateOrderRemainingAmount(order, dto.getMontant());

        return paymentMapper.toSimpleDTO(savedPayment);
    }


    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment found with ID: " + paymentId));
        return paymentMapper.toSimpleDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentAdvancedResponseDTO getPaymentByIdAdvanced(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment found with ID: " + paymentId));
        return paymentMapper.toAdvancedDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByOrder(String orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        return paymentMapper.toSimpleDTOList(payments);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(paymentMapper::toSimpleDTO);
    }

    @Override
    @Transactional
    public PaymentResponseDTO updatePaymentStatus(String paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment found with ID: " + paymentId));

        payment.setPaymentStatus(status);
        Payment updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toSimpleDTO(updatedPayment);
    }

    @Override
    @Transactional
    public void deletePayment(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment found with ID: " + paymentId));

        // Remettre le montant dans le montant restant de la commande
        Order order = payment.getOrder();
        order.setMontantRestant(order.getMontantRestant().add(payment.getMontant()));
        orderRepository.save(order);

        paymentRepository.delete(payment);
    }

    private void updateOrderRemainingAmount(Order order, BigDecimal paymentAmount) {
        BigDecimal newRemainingAmount = order.getMontantRestant().subtract(paymentAmount);
        order.setMontantRestant(newRemainingAmount);
        orderRepository.save(order);
    }
}

