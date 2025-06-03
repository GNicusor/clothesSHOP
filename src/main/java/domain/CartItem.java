package domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "cart_item")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many cart items can reference the same Clothes row.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "clothes_id", nullable = false)
    private Clothes clothes;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // ===== Constructors =====

    protected CartItem() { }

    public CartItem(Clothes clothes, int quantity, Cart cart) {
        this.clothes = Objects.requireNonNull(clothes, "Clothes cannot be null");
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be ≥ 1");
        this.quantity = quantity;
        this.cart = Objects.requireNonNull(cart, "Cart cannot be null");
    }

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public Clothes getClothes() {
        return clothes;
    }

    public void setClothes(Clothes clothes) {
        this.clothes = clothes;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Change the quantity. If less than 1, you can decide to remove in Cart logic.
     */
    public void setQuantity(int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be ≥ 1");
        this.quantity = quantity;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    // ===== Business Logic =====

    /**
     * Compute line total: price × quantity.
     */
    public BigDecimal getLineTotal() {
        return clothes.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Two CartItem instances are equal if they have the same ID.
     * (Or you might choose to compare cart+clothes pair, but ID is simpler.)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CartItem that = (CartItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
