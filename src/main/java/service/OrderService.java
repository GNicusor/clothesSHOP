package service;

import domain.Clothes;
import domain.OrderEntity;
import domain.OrderItem;
import org.springframework.stereotype.Service;
import repository.ClothesRepository;
import repository.OrderRepository;
import shared.CartItemDTO;
import shared.OrderCreateDTO;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClothesRepository clothesRepository;

    public OrderService(OrderRepository orderRepository, ClothesRepository clothesRepository) {
        this.orderRepository = orderRepository;
        this.clothesRepository = clothesRepository;
    }

    public String createOrder(OrderCreateDTO dto) {
        OrderEntity order = new OrderEntity();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderEntity.Status.PENDING);

        // Map DTOs to OrderItems:
        for (CartItemDTO cartItem : dto.getItems()) {
            Clothes clothes = clothesRepository.findById(cartItem.getClothesId())
                    .orElseThrow(() -> new RuntimeException("Clothes not found"));
            OrderItem orderItem = new OrderItem(clothes, cartItem.getQuantity(), order);
            order.addOrderItem(orderItem);
        }

        orderRepository.save(order);
        return String.valueOf(order.getOrderId());
    }

    public OrderEntity getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<OrderEntity> getOrdersByUser(Long userId) {
        // implement based on your user/order structure
        return orderRepository.findByUserId(userId);
    }
}

