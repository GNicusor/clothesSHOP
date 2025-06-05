package domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.*;


@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonBackReference
    private User owner;

    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<CartItem> items = new ArrayList<>();

    protected Cart() { }

    public Cart(User owner) {
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Simple helper that adjusts quantity or appends a new CartItem record.
     * (We leave validation—e.g. “does Clothes exist?”—to the service layer.)
     */
    public void internalAddItem(Clothes clothes, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be ≥ 1");
        }
        for (CartItem ci : items) {
            if (ci.getClothes().getId().equals(clothes.getId())) {
                ci.setQuantity(ci.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItem(clothes, quantity, this));
    }

    /**
     * Simple helper that removes an item from the cart.
     */
    public void internalRemoveItem(Long clothesId) {
        items.removeIf(ci -> ci.getClothes().getId().equals(clothesId));
    }

    /**
     * Update quantity; if ≤0, remove entirely.
     */
    public void updateItemQuantity(Long clothesId, int newQuantity) {
        Iterator<CartItem> it = items.iterator();
        while (it.hasNext()) {
            CartItem ci = it.next();
            if (ci.getClothes().getId().equals(clothesId)) {
                // Found the matching CartItem
                if (newQuantity < 1) {
                    it.remove();
                } else {
                    ci.setQuantity(newQuantity);
                }
                return;
            }
        }
        throw new NoSuchElementException("Item not found in cart: " + clothesId);
    }

    public BigDecimal getSubtotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : items) {
            total = total.add(ci.getLineTotal());
        }
        return total;
    }

    public void internalClear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
