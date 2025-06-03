//package dto;
//
//import domain.CartItem;
//
//import java.math.BigDecimal;
//
//public class CartLineDTO {
//
//    private long clothesID;
//    private String name;           // nice for the UI
//    private int qty;
//    private BigDecimal unitPrice;  // optional
//    private BigDecimal lineTotal;  // qty × unitPrice
//
//    public CartLineDTO() { }
//
//    public CartLineDTO(long clothesID, String name,
//                       int qty, BigDecimal unitPrice, BigDecimal lineTotal) {
//        this.clothesID   = clothesID;
//        this.name       = name;
//        this.qty        = qty;
//        this.unitPrice  = unitPrice;
//        this.lineTotal  = lineTotal;
//    }
//
//    public static CartLineDTO of(CartItem item) {
//        var coffee = item.getClothes();
//        BigDecimal unit = BigDecimal.valueOf(coffee.getPrice());                 // assume getter
//        BigDecimal total = unit.multiply(BigDecimal.valueOf(item.getQuantity()));
//        return new CartLineDTO(
//                coffee.getId(),
//                coffee.getName(),
//                item.getQuantity(),
//                unit,
//                total
//        );
//    }
//
//    public long getCoffeeId() {
//        return clothesID;
//    }
//
//    public void setCoffeeId(long coffeeId) {
//        this.clothesID = coffeeId;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public int getQty() {
//        return qty;
//    }
//
//    public void setQty(int qty) {
//        this.qty = qty;
//    }
//
//    public BigDecimal getUnitPrice() {
//        return unitPrice;
//    }
//
//    public void setUnitPrice(BigDecimal unitPrice) {
//        this.unitPrice = unitPrice;
//    }
//
//    public BigDecimal getLineTotal() {
//        return lineTotal;
//    }
//
//    public void setLineTotal(BigDecimal lineTotal) {
//        this.lineTotal = lineTotal;
//    }
//}
