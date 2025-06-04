package service;

import domain.Cart;
import domain.CartItem;
import domain.Clothes;
import domain.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.ClothesRepository;
import repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CartService {

    private final UserRepository userRepo;
    private final ClothesRepository clothesRepo;

    @Autowired
    public CartService(UserRepository userRepo, ClothesRepository clothesRepo) {
        this.userRepo = userRepo;
        this.clothesRepo = clothesRepo;
    }

    /**
     * Return all CartItem objects in a user’s cart.
     */
    @Transactional
    public List<CartItem> getCartItems(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        Cart cart = user.getCart();
        if (cart == null) {
            throw new NoSuchElementException("Cart not found for user: " + userId);
        }
        return cart.getItems();
    }

    /**
     * Add `quantity` of the given clothes to the user’s cart.
     * If the cart does not exist, create it.
     */
    @Transactional
    public void addToCart(Long userId, Long clothesId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be ≥ 1");
        }
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart(user);
            user.setCart(cart);
        }

        Clothes clothes = clothesRepo.findById(clothesId)
                .orElseThrow(() -> new NoSuchElementException("Clothes not found: " + clothesId));

        // “internalAddItem” is the helper on Cart that either increments an existing CartItem
        // or creates a new one if none exists
        cart.internalAddItem(clothes, quantity);

        // Because User ↔ Cart is Cascade.ALL, saving user is enough to persist cart‐item changes
        userRepo.save(user);
    }

    /**
     * Convenience method: add exactly one of the given Clothes to the user’s cart.
     */
    @Transactional
    public void addOneItem(Long userId, Long clothesId) {
        addToCart(userId, clothesId, 1);
    }

    /**
     * Remove exactly one quantity of the given Clothes from the user’s cart.
     * If the quantity on that CartItem reaches 0, the CartItem is removed entirely.
     */
    @Transactional
    public void decrementItem(Long userId, Long clothesId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        Cart cart = user.getCart();
        if (cart == null) {
            throw new NoSuchElementException("Cart not found for user: " + userId);
        }

        CartItem existing = cart.getItems().stream()
                .filter(ci -> ci.getClothes().getId().equals(clothesId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Item not found in cart: " + clothesId));

        int newQty = existing.getQuantity() - 1;
        cart.updateItemQuantity(clothesId, newQty);
    }

    /**
     * Remove *all* of the given Clothes from the user’s cart.
     */
    @Transactional
    public void removeAllOfItem(Long userId, Long clothesId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        Cart cart = user.getCart();
        if (cart == null) {
            throw new NoSuchElementException("Cart not found for user: " + userId);
        }
        cart.getItems().removeIf(ci -> ci.getClothes().getId().equals(clothesId));

        userRepo.save(user);
    }
}
