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

@Service
public class CartService {

    private final UserRepository userRepo;
    private final ClothesRepository clothesRepo;

    @Autowired
    public CartService(UserRepository userRepo, ClothesRepository clothesRepo) {
        this.userRepo = userRepo;
        this.clothesRepo = clothesRepo;
    }

    @Transactional
    public List<CartItem> getCartItems(Long userId) {
        validateUserId(userId);
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        Cart cart = user.getCart();
        if (cart == null) {
            throw new NoSuchElementException("Cart not found for user: " + userId);
        }
        return cart.getItems();
    }

    @Transactional
    public void addToCart(Long userId, Long clothesId, int quantity) {
        validateUserId(userId);
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
        cart.internalAddItem(clothes, quantity);
        userRepo.save(user);
    }

    @Transactional
    public void addOneItem(Long userId, Long clothesId) {
        addToCart(userId, clothesId, 1);
    }

    @Transactional
    public void decrementItem(Long userId, Long clothesId) {
        validateUserId(userId);
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

    @Transactional
    public void removeAllOfItem(Long userId, Long clothesId) {
        validateUserId(userId);
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        Cart cart = user.getCart();
        if (cart == null) {
            throw new NoSuchElementException("Cart not found for user: " + userId);
        }
        cart.internalRemoveItem(clothesId);
        userRepo.save(user);
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User must be authenticated");
        }
    }
}
