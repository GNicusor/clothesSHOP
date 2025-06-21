package server;

import com.stripe.exception.StripeException;
import domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import repository.CartRepository;
import repository.ClothesRepository;
import repository.OrderRepository;
import repository.UserRepository;
import service.CartService;
import service.OrderService;
import service.StripeCheckoutService;
import shared.OrderCreateDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class ApiEndpoints {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClothesRepository clothesRepository;

    @Autowired
    private StripeCheckoutService stripeCheckoutService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/clothes")
    public List<Clothes> getAllClothes() {
        return clothesRepository.findAll();
    }

    @PostMapping(path = "/addclothes", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Clothes> addClothes(@RequestBody Clothes clothes) {
        Clothes saved = clothesRepository.save(clothes);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    @PostMapping("/users/{userId}/cart/{clothesId}/add")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addOneToCart(
            @PathVariable("userId")   Long userId,
            @PathVariable("clothesId") Long clothesId
    ) {
        try {
            cartService.addOneItem(userId, clothesId);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping("/users/{userId}/cart/{clothesId}/removeOne")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeOneFromCart(
            @PathVariable("userId") Long userId,
            @PathVariable("clothesId") Long clothesId
    ) {
        try {
            cartService.decrementItem(userId, clothesId);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @GetMapping("/{userId}/cart")
    public List<CartItem> getCartOfUser(@PathVariable("userId") Long userId) {
        try {
            return cartService.getCartItems(userId);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @GetMapping("/clothes/{clothesId}")
    public Clothes getClothesById(
            @PathVariable("clothesId") Long clothesId
    ) {
        return clothesRepository.findById(clothesId)
                .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Clothes not found with id: " + clothesId
                                )
                        );
    }

    @DeleteMapping("/clothes/{clothesId}")
    public void deleteClothesById(@PathVariable("clothesId") Long clothesId) {
        if (!clothesRepository.existsById(clothesId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Clothes not found with id: " + clothesId
            );
        }
        clothesRepository.deleteById(clothesId);
    }

    @DeleteMapping("/users/{userId}/cart/{clothesId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAllFromCart(
            @PathVariable Long userId,
            @PathVariable Long clothesId
    ) {
        try {
            cartService.removeAllOfItem(userId, clothesId);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @GetMapping("/allUsers")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    public Map<String, String> createOrder(@RequestBody OrderCreateDTO dto) {
        String orderId = orderService.createOrder(dto);
        return Map.of("orderId", orderId);
    }

    @PostMapping("/orders/checkout")
    public Map<String, Long> checkout(@RequestParam("userId") Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        OrderEntity order = new OrderEntity();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderEntity.Status.PENDING);
        order.setUser(user); // <-- Correct usage

        for (CartItem ci : cart.getItems()) {
            OrderItem oi = new OrderItem(ci.getClothes(), ci.getQuantity(), order);
            order.addOrderItem(oi);
        }

        orderRepository.save(order);

        return Map.of("orderId", order.getOrderId());
    }

    @PostMapping("/checkout-session/{orderId}")
    public Map<String, String> createCheckoutSession(@PathVariable("orderId") Long orderId) throws StripeException {
        String sessionId = stripeCheckoutService.createCheckoutSession(orderId);
        return Map.of("sessionId", sessionId);
    }

    // 2. Get order by ID (for admin/success page/history)
    @GetMapping("/{orderId}")
    public OrderEntity getOrder(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    // 3. Get orders for user (optional, for user order history)
    @GetMapping("/user/{userId}")
    public List<OrderEntity> getOrdersForUser(@PathVariable Long userId) {
        return orderService.getOrdersByUser(userId);
    }

}
