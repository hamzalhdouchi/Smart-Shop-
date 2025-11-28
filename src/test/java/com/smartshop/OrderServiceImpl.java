package com.smartshop;

import com.smartshop.dto.requist.createRequistDto.OrderCreateDTO;
import com.smartshop.dto.requist.updateRequistDto.OrderUpdateDTO;
import com.smartshop.dto.response.order.OrderAdvancedResponseDTO;
import com.smartshop.dto.response.order.OrderResponseDTO;
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
import com.smartshop.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PromoCodeRepository promoCodeRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private Pageable pageable;

    private Client client;
    private Product product;
    private PromoCode promoCode;
    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId("client-1");
        client.setTier(ClientTier.SILVER);
        client.setTotalOrders(0);
        client.setTotalSpent(BigDecimal.ZERO);

        product = new Product();
        product.setId("prod-1");
        product.setNom("Produit 1");
        product.setPrix_unitair(new BigDecimal("100.00"));
        product.setStockDisponible(10);

        promoCode = new PromoCode();
        promoCode.setId("promo-1");
        promoCode.setCode("PROMO10");
        promoCode.setDisponible(true);
        promoCode.setRemisePourcentage(new BigDecimal("10.00"));

        order = new Order();
        order.setId("order-1");
        order.setClient(client);
        order.setStatut(OrderStatus.PENDING);
        order.setTauxTVA(new BigDecimal("20.00"));
        order.setItems(new ArrayList<>());
        order.setPaiements(new ArrayList<>());

        OrderItemId itemId = new OrderItemId("order-1", "prod-1");
        orderItem = new OrderItem();
        orderItem.setId(itemId);
        orderItem.setOrder(order);
        orderItem.setProduit(product);
        orderItem.setQuantite(2);
        orderItem.setPrixUnitaireHT(product.getPrix_unitair());
        orderItem.setTotalLigneHT(product.getPrix_unitair().multiply(new BigDecimal("2")));

        order.setItems(Collections.singletonList(orderItem));
        order.setSousTotalHT(orderItem.getTotalLigneHT());
        order.setRemiseFidelite(BigDecimal.ZERO);
        order.setRemisePromo(BigDecimal.ZERO);
        order.setMontantRemise(BigDecimal.ZERO);
        order.setMontantHTApresRemise(order.getSousTotalHT());
        order.setMontantTVA(new BigDecimal("40.00"));
        order.setTotalTTC(order.getSousTotalHT().add(order.getMontantTVA()));
        order.setMontantRestant(order.getTotalTTC());
    }

    // ---------- createOrder ----------

    @Test
    void createOrder_success_withoutPromo() {
        OrderCreateDTO.OrderItemCreateDTO itemDTO = new OrderCreateDTO.OrderItemCreateDTO();
        itemDTO.setProduitId("prod-1");
        itemDTO.setQuantite(2);

        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setClientId("client-1");
        dto.setItems(Collections.singletonList(itemDTO));
        dto.setCodePromo(null);

        when(clientRepository.findById("client-1")).thenReturn(Optional.of(client));
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId("order-1");
            return o;
        });
        when(orderMapper.toSimpleDTO(any(Order.class))).thenReturn(new OrderResponseDTO());

        OrderResponseDTO response = orderService.createOrder(dto);

        assertNotNull(response);
        verify(clientRepository).findById("client-1");
        verify(productRepository).findById("prod-1");
        verify(orderRepository).save(any(Order.class));
        verify(productRepository, atLeastOnce()).save(any(Product.class)); // décrément stock
    }

    @Test
    void createOrder_withPromo_success() {
        OrderCreateDTO.OrderItemCreateDTO itemDTO = new OrderCreateDTO.OrderItemCreateDTO();
        itemDTO.setProduitId("prod-1");
        itemDTO.setQuantite(3); // 300

        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setClientId("client-1");
        dto.setItems(Collections.singletonList(itemDTO));
        dto.setCodePromo("PROMO10");

        when(clientRepository.findById("client-1")).thenReturn(Optional.of(client));
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(product));
        when(promoCodeRepository.findByCode("PROMO10")).thenReturn(Optional.of(promoCode));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toSimpleDTO(any(Order.class))).thenReturn(new OrderResponseDTO());
        when(promoCodeRepository.save(any(PromoCode.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDTO response = orderService.createOrder(dto);

        assertNotNull(response);
        verify(promoCodeRepository).findByCode("PROMO10");
        verify(promoCodeRepository).save(any(PromoCode.class)); // marqué indisponible
    }

    @Test
    void createOrder_clientNotFound() {
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setClientId("unknown");

        when(clientRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(dto));
    }

    @Test
    void createOrder_productNotFound() {
        OrderCreateDTO.OrderItemCreateDTO itemDTO = new OrderCreateDTO.OrderItemCreateDTO();
        itemDTO.setProduitId("missing");
        itemDTO.setQuantite(1);

        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setClientId("client-1");
        dto.setItems(Collections.singletonList(itemDTO));

        when(clientRepository.findById("client-1")).thenReturn(Optional.of(client));
        when(productRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(dto));
    }

    @Test
    void createOrder_insufficientStock() {
        product.setStockDisponible(1);

        OrderCreateDTO.OrderItemCreateDTO itemDTO = new OrderCreateDTO.OrderItemCreateDTO();
        itemDTO.setProduitId("prod-1");
        itemDTO.setQuantite(5);

        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setClientId("client-1");
        dto.setItems(Collections.singletonList(itemDTO));

        when(clientRepository.findById("client-1")).thenReturn(Optional.of(client));
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(product));

        assertThrows(BusinessException.class, () -> orderService.createOrder(dto));
    }


    @Test
    void decrementStock_success() {
        product.setStockDisponible(10);
        orderItem.setQuantite(3);
        order.setItems(Collections.singletonList(orderItem));

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.decrementStock(order);

        assertEquals(7, product.getStockDisponible());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void decrementStock_insufficientAfterChange() {
        product.setStockDisponible(1);
        orderItem.setQuantite(3);
        order.setItems(Collections.singletonList(orderItem));

        assertThrows(BusinessException.class, () -> orderService.decrementStock(order));
    }

    @Test
    void calculateLoyaltyDiscount_silver_aboveMinimum() {
        client.setTier(ClientTier.SILVER);
        BigDecimal subTotal = new BigDecimal("600.00"); // >= 500

        BigDecimal discount = orderService.calculateLoyaltyDiscount(client, subTotal);

        assertEquals(new BigDecimal("30.00"), discount);
    }

    @Test
    void calculateLoyaltyDiscount_basic_noDiscount() {
        client.setTier(ClientTier.BASIC);
        BigDecimal subTotal = new BigDecimal("1000.00");

        BigDecimal discount = orderService.calculateLoyaltyDiscount(client, subTotal);

        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    void getOrderById_success() {
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));
        when(orderMapper.toSimpleDTO(order)).thenReturn(new OrderResponseDTO());

        OrderResponseDTO dto = orderService.getOrderById("order-1");

        assertNotNull(dto);
        verify(orderRepository).findById("order-1");
    }

    @Test
    void getOrderById_notFound() {
        when(orderRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById("unknown"));
    }

    @Test
    void getOrderByIdAdvanced_success() {
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));
        when(orderMapper.toAdvancedDTO(order)).thenReturn(new OrderAdvancedResponseDTO());

        OrderAdvancedResponseDTO dto = orderService.getOrderByIdAdvanced("order-1");

        assertNotNull(dto);
    }

    @Test
    void getOrdersByClient_success() {
        when(orderRepository.findAllByClientId("client-1")).thenReturn(Collections.singletonList(order));
        when(orderMapper.toSimpleDTOList(anyList())).thenReturn(Collections.singletonList(new OrderResponseDTO()));

        List<OrderResponseDTO> list = orderService.getOrdersByClient("client-1");

        assertEquals(1, list.size());
        verify(orderRepository).findAllByClientId("client-1");
    }

    @Test
    void getAllOrders_success() {
        Page<Order> page = new PageImpl<>(Collections.singletonList(order));
        when(orderRepository.findAll(pageable)).thenReturn(page);
        when(orderMapper.toSimpleDTO(order)).thenReturn(new OrderResponseDTO());

        Page<OrderResponseDTO> result = orderService.getAllOrders(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void confirmOrder_success() {
        order.setMontantRestant(BigDecimal.ZERO);
        order.setStatut(OrderStatus.PENDING);

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toSimpleDTO(any(Order.class))).thenReturn(new OrderResponseDTO());
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDTO dto = orderService.confirmOrder("order-1");

        assertNotNull(dto);
        assertEquals(OrderStatus.CONFIRMED, order.getStatut());
        assertNotNull(order.getDateValidation());
        verify(clientRepository).save(client); // stats mises à jour
    }

    @Test
    void confirmOrder_notFullyPaid() {
        order.setMontantRestant(new BigDecimal("10.00"));

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> orderService.confirmOrder("order-1"));
    }

    @Test
    void confirmOrder_notPendingStatus() {
        order.setMontantRestant(BigDecimal.ZERO);
        order.setStatut(OrderStatus.CONFIRMED);

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> orderService.confirmOrder("order-1"));
    }


    @Test
    void cancelOrder_success() {
        order.setStatut(OrderStatus.PENDING);
        order.setItems(Collections.singletonList(orderItem));
        product.setStockDisponible(5);
        orderItem.setQuantite(2);

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toSimpleDTO(any(Order.class))).thenReturn(new OrderResponseDTO());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDTO dto = orderService.cancelOrder("order-1", "reason");

        assertNotNull(dto);
        assertEquals(OrderStatus.CANCELED, order.getStatut());
        assertTrue(product.getStockDisponible() >= 7); // restauré
    }

    @Test
    void cancelOrder_wrongStatus() {
        order.setStatut(OrderStatus.CONFIRMED);

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> orderService.cancelOrder("order-1", "reason"));
    }

    @Test
    void updateOrderStatus_success() {
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toSimpleDTO(any(Order.class))).thenReturn(new OrderResponseDTO());

        OrderResponseDTO dto = orderService.updateOrderStatus("order-1", OrderStatus.PENDING);

        assertNotNull(dto);
        assertEquals(OrderStatus.PENDING, order.getStatut());
    }

    @Test
    void updateOrder_pendingOnly() {
        order.setStatut(OrderStatus.PENDING);
        OrderUpdateDTO dto = new OrderUpdateDTO(); // à adapter selon ton DTO

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toSimpleDTO(any(Order.class))).thenReturn(new OrderResponseDTO());

        OrderResponseDTO response = orderService.updateOrder("order-1", dto);

        assertNotNull(response);
    }

    @Test
    void updateOrder_nonPending_throws() {
        order.setStatut(OrderStatus.CONFIRMED);
        OrderUpdateDTO dto = new OrderUpdateDTO();

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> orderService.updateOrder("order-1", dto));
    }
}
