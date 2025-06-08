package server;

import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.StripeCheckoutService;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class CheckoutController {

    private final StripeCheckoutService stripeService;

    @Autowired
    public CheckoutController(StripeCheckoutService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/checkout")
    public Map<String,String> createCheckout(@RequestBody Map<String,Long> body) throws StripeException {
        Long userId = body.get("userId");
        String url = stripeService.createCheckoutSessionForCart(userId);
        return Map.of("url", url);
    }
}
