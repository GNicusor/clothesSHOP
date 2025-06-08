package service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import domain.CartItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StripeCheckoutService {

    private final CartService cartService;

    @Value("${app.frontendUrl}")
    private String frontendUrl;

    public StripeCheckoutService(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Build a Stripe Checkout session URL from the current user's cart.
     */
    public String createCheckoutSessionForCart(Long userId) throws StripeException {
        List<CartItem> items = cartService.getCartItems(userId);

        List<SessionCreateParams.LineItem> lineItems = items.stream()
                .map(this::toLineItem)
                .collect(Collectors.toList());

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addAllLineItem(lineItems)
                .setSuccessUrl(frontendUrl + "/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/cart")
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }

    /**
     * Convert CartItem into a Stripe LineItem (amount in cents).
     */
    private SessionCreateParams.LineItem toLineItem(CartItem ci) {
        BigDecimal price = ci.getClothes().getPrice();
        int qty = ci.getQuantity();

        BigDecimal amountDecimal = price
                .multiply(BigDecimal.valueOf(100))
                .setScale(0);
        long unitAmount = amountDecimal.longValue();

        return SessionCreateParams.LineItem.builder()
                .setQuantity((long) qty)
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setUnitAmount(unitAmount)
                                .setCurrency("RON")
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName(ci.getClothes().getName())
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}
