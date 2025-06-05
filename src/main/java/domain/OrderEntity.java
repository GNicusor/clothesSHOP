package domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Builder;
import org.hibernate.query.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


//@Builder
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @Column(name = "order_id", length = 36, nullable = false, updatable = false)
    private String orderId;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Nullable
    @Column(name = "stripe_id", unique = true)
    private String stripeId;

    //@Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Nullable
    public String getStripeId() {
        return stripeId;
    }

    public void setStripeId(@Nullable String stripeId) {
        this.stripeId = stripeId;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    public enum Status { PENDING, PAID, FAILED}

    protected OrderEntity() { }


    public String getOrderId() {
        return orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
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

    public String getId() {
        return this.orderId;
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
