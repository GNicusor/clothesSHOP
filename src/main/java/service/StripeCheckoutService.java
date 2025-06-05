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

import java.math.BigDecimal;
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
////    @Transactional
////    public String createCheckoutSession(Long orderId) throws StripeException {
////        OrderEntity orderEntity = orderRepository.findById(orderId)
////                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
////        if (!"PENDING".equals(orderEntity.getStatus())) {
////            throw new IllegalStateException("Order is not in a PENDING state.");
////        }
////        List<SessionCreateParams.LineItem> lineItems = orderEntity.getOrderItems().stream()
////                .map(this::toLineItem)
////                .collect(Collectors.toList());
////        SessionCreateParams params = SessionCreateParams.builder()
////                .setMode(SessionCreateParams.Mode.PAYMENT)
////                .addAllLineItem(lineItems)
////                .setSuccessUrl(frontendUrl + "/success?session_id={CHECKOUT_SESSION_ID}")
////                .setCancelUrl(frontendUrl + "/cart")
////                .putMetadata("order_id", orderEntity.getId().toString())
////                .build();
////
////        Session session = Session.create(params);
////
////        // 5) Save the Stripe session ID back to the order so the webhook can match it
////        orderEntity.setStripeId(session.getId());
////        orderRepository.save(orderEntity);
////
////        return session.getId();
////    }
//
//    /**
//     * Convert one OrderItem (with BigDecimal line total in RON) into a Stripe LineItem (amount in bani).
//     */
//    private SessionCreateParams.LineItem toLineItem(OrderItem item) {
//        BigDecimal lineTotalRoni = item.getLineTotal(); // e.g. 123.45
//        BigDecimal lineTotalBani = lineTotalRoni
//                .multiply(BigDecimal.valueOf(100))       // now 12345.00 (bani)
//                .setScale(0, BigDecimal.ROUND_HALF_UP);  // no fractional bani
//
//        long unitAmountInBani = lineTotalBani.longValue();
//        return SessionCreateParams.LineItem.builder()
//                .setQuantity(1L)
//                .setPriceData(
//                        SessionCreateParams.LineItem.PriceData.builder()
//                                .setCurrency("ron")       // your currency
//                                .setUnitAmount(unitAmountInBani)
//                                .setProductData(
//                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                                                .setName(item.getName())
//                                                .build()
//                                )
//                                .build()
//                )
//                .build();
//    }
//}