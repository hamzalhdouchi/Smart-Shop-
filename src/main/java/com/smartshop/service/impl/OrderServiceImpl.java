package com.smartshop.service.impl;

import com.smartshop.dto.requist.createRequistDto.OrderCreateDTO;
import com.smartshop.dto.requist.updateRequistDto.OrderUpdateDTO;
import com.smartshop.dto.response.client.ClientStatisticsDTO;
import com.smartshop.dto.response.order.OrderAdvancedResponseDTO;
import com.smartshop.dto.response.order.OrderResponseDTO;
import com.smartshop.dto.response.order.OrderStatisticsDTO;
import com.smartshop.entity.*;
import com.smartshop.enums.ClientTier;
import com.smartshop.enums.OrderStatus;
import com.smartshop.exception.BusinessException;
import com.smartshop.exception.ResourceNotFoundException;
import com.smartshop.mapper.OrderMapper;
import com.smartshop.repository.ClientRepository;
import com.smartshop.repository.OrderRepository;
import com.smartshop.repository.ProductRepository;
import com.smartshop.repository.PromoCodeRepository;
import com.smartshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final PromoCodeRepository promoCodeRepository;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderCreateDTO dto) {

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("No client found with ID: " + dto.getClientId()));

        BigDecimal promoPercentage = BigDecimal.ZERO;
        PromoCode promoCode = null;

        if (dto.getCodePromo() != null && !dto.getCodePromo().isEmpty()) {
            promoCode = promoCodeRepository.findByCode(dto.getCodePromo())
                    .orElseThrow(() -> new ResourceNotFoundException("No promo code found with code: " + dto.getCodePromo()));

            if (!promoCode.getDisponible()) {
                throw new BusinessException("Promo code is not available! Try another one!");
            }

            promoPercentage = promoCode.getRemisePourcentage();
        }

        Order order = Order.builder()
                .client(client)
                .statut(OrderStatus.PENDING)
                .tauxTVA(new BigDecimal("20.00"))
                .montantRemise(BigDecimal.ZERO)
                .remiseFidelite(BigDecimal.ZERO)
                .remisePromo(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .paiements(new ArrayList<>())
                .build();

        BigDecimal subTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderCreateDTO.OrderItemCreateDTO itemDTO : dto.getItems()) {
            Product product = productRepository.findById(itemDTO.getProduitId())
                    .orElseThrow(() -> new ResourceNotFoundException("No product found with ID: " + itemDTO.getProduitId()));

            if (itemDTO.getQuantite() > product.getStockDisponible()) {
                throw new BusinessException("Insufficient stock for product: " + product.getNom() +
                        ". Available: " + product.getStockDisponible() + ", Requested: " + itemDTO.getQuantite());
            }

            BigDecimal lineTotal = product.getPrix_unitair()
                    .multiply(new BigDecimal(itemDTO.getQuantite()))
                    .setScale(2, RoundingMode.HALF_UP);

            subTotal = subTotal.add(lineTotal);

            OrderItemId orderItemId = new OrderItemId(null, itemDTO.getProduitId());

            OrderItem orderItem = OrderItem.builder()
                    .id(orderItemId)
                    .order(order)
                    .produit(product)
                    .quantite(itemDTO.getQuantite())
                    .prixUnitaireHT(product.getPrix_unitair())
                    .totalLigneHT(lineTotal)
                    .build();

            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.setSousTotalHT(subTotal);

        BigDecimal loyaltyDiscount = calculateLoyaltyDiscount(client, subTotal);
        order.setRemiseFidelite(loyaltyDiscount);

        BigDecimal promoDiscount = BigDecimal.ZERO;
        if (promoPercentage.compareTo(BigDecimal.ZERO) > 0) {
            promoDiscount = subTotal.multiply(promoPercentage.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP))
                    .setScale(2, RoundingMode.HALF_UP);
            order.setRemisePromo(promoDiscount);
            order.setCodePromo(dto.getCodePromo());
        }

        BigDecimal totalDiscount = loyaltyDiscount.add(promoDiscount);
        order.setMontantRemise(totalDiscount);

        BigDecimal montantHTApresRemise = subTotal.subtract(totalDiscount).setScale(2, RoundingMode.HALF_UP);
        order.setMontantHTApresRemise(montantHTApresRemise);

        BigDecimal montantTVA = montantHTApresRemise
                .multiply(order.getTauxTVA().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
        order.setMontantTVA(montantTVA);

        BigDecimal totalTTC = montantHTApresRemise.add(montantTVA).setScale(2, RoundingMode.HALF_UP);
        order.setTotalTTC(totalTTC);

        order.setMontantRestant(totalTTC);



        Order savedOrder = orderRepository.save(order);

        decrementStock(savedOrder);

        if (promoCode != null) {
            promoCode.setDisponible(false);
            promoCodeRepository.save(promoCode);
        }

        return orderMapper.toSimpleDTO(savedOrder);
    }

    @Override
    @Transactional
    public void decrementStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduit();

            if (product.getStockDisponible() < item.getQuantite()) {
                throw new BusinessException("Stock changed! Insufficient stock for: " + product.getNom());
            }

            Integer newStock = product.getStockDisponible() - item.getQuantite();
            product.setStockDisponible(newStock);
            productRepository.save(product);
        }
    }

    @Override
    public BigDecimal calculateLoyaltyDiscount(Client client, BigDecimal subTotal) {
        ClientTier tier = client.getTier();
        BigDecimal discountPercentage;
        BigDecimal minimumAmount;

        switch (tier) {
            case SILVER:
                discountPercentage = new BigDecimal("5.00");
                minimumAmount = new BigDecimal("500.00");
                break;
            case GOLD:
                discountPercentage = new BigDecimal("10.00");
                minimumAmount = new BigDecimal("800.00");
                break;
            case PLATINUM:
                discountPercentage = new BigDecimal("15.00");
                minimumAmount = new BigDecimal("1200.00");
                break;
            case BASIC:
            default:
                return BigDecimal.ZERO;
        }

        if (subTotal.compareTo(minimumAmount) >= 0) {
            return subTotal.multiply(discountPercentage.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    @Override
    public OrderResponseDTO getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No order found with ID: " + orderId));
        return orderMapper.toSimpleDTO(order);
    }

    @Override
    public OrderAdvancedResponseDTO getOrderByIdAdvanced(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No order found with ID: " + orderId));
        return orderMapper.toAdvancedDTO(order);
    }

    @Override
    public List<OrderResponseDTO> getOrdersByClient(String clientId) {
        List<Order> orders = orderRepository.findAllByClientId(clientId);
        return orderMapper.toSimpleDTOList(orders);
    }

    @Override
    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(orderMapper::toSimpleDTO);
    }

    @Override
    @Transactional
    public OrderResponseDTO confirmOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No order found with ID: " + orderId));

        if (order.getMontantRestant().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessException("Cannot confirm order, it is not fully paid. Remaining: " + order.getMontantRestant());
        }

        if (!order.getStatut().equals(OrderStatus.PENDING)) {
            throw new BusinessException("Order cannot be confirmed. Current status: " + order.getStatut());
        }

        order.setStatut(OrderStatus.CONFIRMED);
        order.setDateValidation(LocalDateTime.now());

        Order confirmedOrder = orderRepository.save(order);

        updateClientStatistics(confirmedOrder);

        return orderMapper.toSimpleDTO(confirmedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDTO cancelOrder(String orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No order found with ID: " + orderId));

        if (!order.getStatut().equals(OrderStatus.PENDING)) {
            throw new BusinessException("Only PENDING orders can be cancelled. Current status: " + order.getStatut());
        }

        order.setStatut(OrderStatus.CANCELED);
        order.setDateAnnulation(LocalDateTime.now());

        restoreStock(order);

        Order cancelledOrder = orderRepository.save(order);
        return orderMapper.toSimpleDTO(cancelledOrder);
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(String orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No order found with ID: " + orderId));

        order.setStatut(status);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toSimpleDTO(updatedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrder(String orderId, OrderUpdateDTO dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No order found with ID: " + orderId));

        if (!order.getStatut().equals(OrderStatus.PENDING)) {
            throw new BusinessException("Only PENDING orders can be updated");
        }


        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toSimpleDTO(updatedOrder);
    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduit();
            Integer newStock = product.getStockDisponible() + item.getQuantite();
            product.setStockDisponible(newStock);
            productRepository.save(product);
        }
    }

    private void updateClientStatistics(Order order) {
        Client client = order.getClient();

        client.setTotalOrders(client.getTotalOrders() + 1);
        client.setTotalSpent(client.getTotalSpent().add(order.getTotalTTC()));
        updateClientTier(client);
        clientRepository.save(client);
    }

    private void updateClientTier(Client client) {
        Integer totalOrders = client.getTotalOrders();
        BigDecimal totalSpent = client.getTotalSpent();

        if (totalOrders >= 20 || totalSpent.compareTo(new BigDecimal("15000")) >= 0) {
            client.setTier(ClientTier.PLATINUM);
        } else if (totalOrders >= 10 || totalSpent.compareTo(new BigDecimal("5000")) >= 0) {
            client.setTier(ClientTier.GOLD);
        } else if (totalOrders >= 3 || totalSpent.compareTo(new BigDecimal("1000")) >= 0) {
            client.setTier(ClientTier.SILVER);
        } else {
            client.setTier(ClientTier.BASIC);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrderStatisticsDTO getOrderStatistics() {
        return OrderStatisticsDTO.builder()
                .totalConfirmedOrders(orderRepository.countConfirmedOrders())
                .totalConfirmedAmount(orderRepository.sumConfirmedOrdersAmount())
                .totalPendingOrders(orderRepository.countPendingOrders())
                .totalCancelledOrders(orderRepository.countCancelledOrders())
                .averageOrderAmount(orderRepository.averageConfirmedOrderAmount())
                .lastUpdated(LocalDateTime.now())
                .build();
    }

}
