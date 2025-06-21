package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import domain.OrderEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import repository.OrderRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.Scanner;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

    private final OrderRepository orderRepository;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    public StripeWebhookController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request, @RequestHeader("Stripe-Signature") String sigHeader) throws StripeException, JsonProcessingException {
        String payload = "";
        try (Scanner s = new Scanner(request.getInputStream()).useDelimiter("\\A")) {
            payload = s.hasNext() ? s.next() : "";
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }

        ObjectMapper mapper = new ObjectMapper();

        if ("checkout.session.completed".equals(event.getType())) {
            var objOpt = event.getDataObjectDeserializer().getObject();
            Session session = null;
            if (objOpt.isPresent() && objOpt.get() instanceof Session s) {
                session = s;
            } else {
                JsonNode root = mapper.readTree(event.getData().getObject().toJson());
                String sessionId = root.get("id").asText();
                System.out.println("Fetching Session by id: " + sessionId);
                session = Session.retrieve(sessionId);
            }

            if (session != null) {
                String orderIdRaw = session.getMetadata().get("order_id");
                System.out.println("Stripe webhook received order_id=" + orderIdRaw);
                try {
                    Long orderId = Long.valueOf(orderIdRaw);
                    Optional<OrderEntity> orderOpt = orderRepository.findById(orderId);
                    if (orderOpt.isPresent()) {
                        OrderEntity order = orderOpt.get();
                        order.setStatus(OrderEntity.Status.PAID);
                        orderRepository.save(order);
                        System.out.println("Order " + orderId + " marked as PAID!");
                    } else {
                        System.err.println("No order found for id: " + orderId);
                    }
                } catch (Exception ex) {
                    System.err.println("Error parsing order id from Stripe metadata: " + orderIdRaw + " | " + ex.getMessage());
                }
            } else {
                System.err.println("Could not retrieve session for event: " + event.toJson());
            }
        }
        return ResponseEntity.ok("");
    }
}
