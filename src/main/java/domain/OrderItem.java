package domain;

import jakarta.persistence.*;
import shared.Size;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A line item in an Order. Snapshots product details & price at purchase time.
 */
@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clothes_id", nullable = false, length = 36)
    private Long clothesId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "size", nullable = false, length = 5)
    private Size size;

    @Column(name = "for_children", nullable = false)
    private boolean forChildren;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    /**
     * Many OrderItems belong to one Order.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")   // whatever your FK column is
    private OrderEntity order;

    // ===== Constructors =====

    protected OrderItem() { }

    /**
     * Copy relevant fields from a Clothes instance at purchase time.
     */
    public OrderItem(Clothes clothes, int quantity, OrderEntity orderEntity) {
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be ≥ 1");

        this.clothesId = clothes.getId();
        this.name = clothes.getName();
        this.size = clothes.getSize();
        this.forChildren = clothes.isForChildren();
        this.price = clothes.getPrice();
        this.quantity = quantity;
        this.order = Objects.requireNonNull(orderEntity, "Order cannot be null");
    }

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public Long getClothesId() {
        return clothesId;
    }

    public String getName() {
        return name;
    }

    public Size getSize() {
        return size;
    }

    public boolean isForChildren() {
        return forChildren;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public long getQuantity() {
        return quantity;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setQuantity(int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be ≥ 1");
        this.quantity = quantity;
    }

    /**
     * Compute the total price for this line (price × quantity).
     */
    public BigDecimal getLineTotal() {
        return price.multiply(BigDecimal.valueOf(Long.valueOf(quantity)));
    }

    // ===== equals & hashCode =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderItem that = (OrderItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
