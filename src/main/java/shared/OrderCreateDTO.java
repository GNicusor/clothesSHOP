package shared;

import java.util.List;

public class OrderCreateDTO {
    private Long userId;
    private List<CartItemDTO> items;
    // + getters/setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }
}
