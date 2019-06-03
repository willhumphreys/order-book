package codingtask.orderbook;

import java.math.BigDecimal;

public class Execution {

    private int quantity;
    private BigDecimal price;

    public Execution(int quantity, BigDecimal price) {
        this.quantity = quantity;
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Execution{" +
                "quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
