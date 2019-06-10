package codingtask.orderbook;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.lang.Integer.*;

public class Order {

    private String id;
    private int quantity;
    private LocalDateTime entryDate;
    private BigDecimal price;

    public Order(int quantity, LocalDateTime entryDate, BigDecimal price) {

        this.id = UUID.randomUUID().toString();
        this.quantity = quantity;
        this.entryDate = entryDate;
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getEntryDate() {
        return entryDate;
    }


    public BigDecimal getPrice() {
        return price;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", quantity=" + quantity +
                ", entryDate=" + entryDate +
                ", price=" + price +
                '}';
    }

    public static class Builder {
        private int quantity;
        private LocalDateTime entryDate;
        private BigDecimal price;

        public Builder setQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder setEntryDate(LocalDateTime entryDate) {
            this.entryDate = entryDate;
            return this;
        }

        public Builder setPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Order createOrder() {
            return new Order(quantity, entryDate, price);
        }
    }
}
