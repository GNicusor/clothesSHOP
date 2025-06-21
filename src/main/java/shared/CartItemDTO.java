package shared;

public class CartItemDTO {
    private Long clothesId;
    private int quantity;
    // + getters/setters

    public Long getClothesId() {
        return clothesId;
    }

    public void setClothesId(Long clothesId) {
        this.clothesId = clothesId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
