package codingtask.orderbook;

import org.springframework.hateoas.Link;

import java.math.BigDecimal;

public class OrderReceipt {

    private Link orderId;
    private boolean valid;

    private int executionQuantity;

    private BigDecimal orderPrice;

    private BigDecimal executionPrice;
    private int orderQuantity;

    public OrderReceipt(Link orderId, boolean valid, int executionQuantity, BigDecimal orderPrice, BigDecimal executionPrice, int orderQuantity) {
        this.orderId = orderId;
        this.valid = valid;
        this.executionQuantity = executionQuantity;
        this.orderPrice = orderPrice;
        this.executionPrice = executionPrice;
        this.orderQuantity = orderQuantity;
    }

    public Link getOrderId() {
        return orderId;
    }

    public boolean isValid() {
        return valid;
    }

    public int getExecutionQuantity() {
        return executionQuantity;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public BigDecimal getExecutionPrice() {
        return executionPrice;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    @Override
    public String toString() {
        return "OrderReceipt{" +
                "orderId='" + orderId + '\'' +
                ", valid=" + valid +
                ", executionQuantity=" + executionQuantity +
                ", orderPrice=" + orderPrice +
                ", executionPrice=" + executionPrice +
                ", orderQuantity=" + orderQuantity +
                '}';
    }

    public static class Builder {
        private boolean valid;
        private int executionQuantity;
        private BigDecimal orderPrice;
        private BigDecimal executionPrice;
        private Link orderId;
        private int orderQuantity;

        public Builder setValid(boolean valid) {
            this.valid = valid;
            return this;
        }

        public Builder setExecutionQuantity(int executionQuantity) {
            this.executionQuantity = executionQuantity;
            return this;
        }

        public Builder setOrderPrice(BigDecimal orderPrice) {
            this.orderPrice = orderPrice;
            return this;
        }

        public Builder setExecutionPrice(BigDecimal executionPrice) {
            this.executionPrice = executionPrice;
            return this;
        }

        public OrderReceipt createOrderReceipt() {
            return new OrderReceipt(orderId, valid, executionQuantity, orderPrice, executionPrice, orderQuantity);
        }

        public Link getOrderId() {
            return this.orderId;
        }

        public Builder setOrderId(Link orderId) {
            this.orderId = orderId;
            return this;
        }

        public void incrementExecutionQuantity() {
            this.executionQuantity++;
        }

        public Builder setOrderQuantity(int quantity) {
            this.orderQuantity = quantity;
            return this;
        }

        public boolean isFilled() {
            return this.orderQuantity == executionQuantity;
        }
    }
}
