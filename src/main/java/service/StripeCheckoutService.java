package service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import domain.OrderItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.OrderRepository;
import domain.OrderEntity;

import java.util.List;
import java.util.stream.Collectors;

//@Service
//public class StripeCheckoutService {
//
//    private final OrderRepository orderRepository;
//
//    @Value("${app.frontendUrl}")
//    private String frontendUrl;
//
//    public StripeCheckoutService(OrderRepository orderRepository) {
//        this.orderRepository = orderRepository;
//    }
//
//    /**
//     * Creates a Stripe Checkout Session for the given order ID.
//     *
//     * @param orderId the ID of the existing Order (which should already have items, customer info, etc.)
//     * @return the Session’s ID (so the front-end can redirect to Stripe)
//     */
//    @Transactional
//    public String createCheckoutSession(Long orderId) throws StripeException {
//        // 1. Load the order (must exist and be in a state where payment is expected)
//        OrderEntity orderEntity = orderRepository.findById(orderId)
//                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
//
//        if (!"PENDING".equals(orderEntity)) {
//            throw new IllegalStateException("Order is not in a PENDING state.");
//        }
//
//        // 2. Build line items for Stripe
//        List<SessionCreateParams.LineItem> lineItems = orderEntity.getItems().stream()
//                .map(this::toLineItem)
//                .collect(Collectors.toList());
//
//        // 3. Create the Checkout Session
//        SessionCreateParams params = SessionCreateParams.builder()
//                .setMode(SessionCreateParams.Mode.PAYMENT)
//                .addAllLineItem(lineItems)
//                .setSuccessUrl(frontendUrl + "/success?session_id={CHECKOUT_SESSION_ID}")
//                .setCancelUrl(frontendUrl + "/cart")
//                // Pass metadata so we can look up which Order this was
//                .putMetadata("order_id", orderEntity.getId().toString())
//                .build();
//
//        Session session = Session.create(params);
//
//        // 4. Store the session ID in our Order so the webhook can find it
//        orderEntity.setStripeSessionId(session.getId());
//        // (Optionally: order.setStatus("AWAITING_PAYMENT") or something)
//        orderRepository.save(orderEntity);
//
//        return session.getId();
//    }
//
//    /** Convert one OrderItem to a Stripe LineItem */
//    private SessionCreateParams.LineItem toLineItem(OrderItem item) {
//        return SessionCreateParams.LineItem.builder()
//                .setQuantity(item.getQuantity())
//                .setPriceData(
//                        SessionCreateParams.LineItem.PriceData.builder()
//                                .setCurrency("usd") // or your currency
//                                .setUnitAmount(item.getProduct().getUnitPriceCents())
//                                .setProductData(
//                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                                                .setName(item.getProduct().getName())
//                                                .build()
//                                )
//                                .build()
//                )
//                .build();
//    }
//}