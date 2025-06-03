package domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @Column(name = "order_id", length = 36, nullable = false, updatable = false)
    private String orderId;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    protected OrderEntity() { }

    public OrderEntity(String orderId) {
        this.orderId = Objects.requireNonNull(orderId, "Order ID cannot be null");
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    public String getOrderId() {
        return orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    // ===== Business Logic =====

    public void addOrderItem(OrderItem item) {
        Objects.requireNonNull(item, "OrderItem cannot be null");
        orderItems.add(item);
    }

    public BigDecimal getOrderTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem oi : orderItems) {
            total = total.add(oi.getLineTotal());
        }
        return total;
    }

    public enum OrderStatus {
        PENDING,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        FAILED,
        CANCELLED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderEntity orderEntity = (OrderEntity) o;
        return orderId.equals(orderEntity.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    // ===== toString =====

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", orderDate=" + orderDate +
                ", status=" + status +
                ", total=" + getOrderTotal() +
                '}';
    }
}
