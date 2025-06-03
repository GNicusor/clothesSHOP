package dto;

import domain.Cart;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

//public class CartDTO {
//
//    private Long id;
//    private List<CartLineDTO> items;
//
//    public CartDTO() { }
//
//    public CartDTO(Long id, List<CartLineDTO> items) {
//        this.id = id;
//        this.items = items;
//    }
//
//    @NotNull
//    public static CartDTO of(Cart cart) {
//        List<CartLineDTO> lines = cart.getItems().stream()
//                .map(CartLineDTO::of)
//                .toList();
//        return new CartDTO(cart.getId(), lines);
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public List<CartLineDTO> getItems() {
//        return items;
//    }
//
//    public void setItems(List<CartLineDTO> items) {
//        this.items = items;
//    }
//}
