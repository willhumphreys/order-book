package codingtask.orderbook;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order extends ResourceSupport {

    private int quantity;
    private LocalDateTime entryDate;
    private BigDecimal price;

    public Order(@JsonProperty("quantity") int quantity, @JsonProperty("entryDate") LocalDateTime entryDate, @JsonProperty("price") BigDecimal price) {
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

    @Override
    public String toString() {
        return "Order{" +
                "quantity=" + quantity +
                ", entryDate=" + entryDate +
                ", price=" + price +
                ", id='" + getId() + '\'' +
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
