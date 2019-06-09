package codingtask.orderbook;

import java.math.BigDecimal;

public class OrderReceipt {

    private String instrument;

    private String orderId;
    private boolean valid;

    private int executionQuantity;

    private BigDecimal orderPrice;

    private BigDecimal executionPrice;
    private int orderQuantity;

    public OrderReceipt(String instrument, String orderId, boolean valid, int executionQuantity, BigDecimal orderPrice, BigDecimal executionPrice, int orderQuantity) {
        this.instrument = instrument;
        this.orderId = orderId;
        this.valid = valid;
        this.executionQuantity = executionQuantity;
        this.orderPrice = orderPrice;
        this.executionPrice = executionPrice;
        this.orderQuantity = orderQuantity;
    }

    public String getOrderId() {
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

    public String getInstrument() {
        return instrument;
    }

    @Override
    public String toString() {
        return "OrderReceipt{" +
                "instrument='" + instrument + '\'' +
                ", orderId=" + orderId +
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
        private String orderId;
        private int orderQuantity;
        private String instrument;

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

        public Builder setInstrument(String instrument) {
            this.instrument = instrument;
            return this;
        }

        public OrderReceipt createOrderReceipt() {
            return new OrderReceipt(instrument, orderId, valid, executionQuantity, orderPrice, executionPrice, orderQuantity);
        }

        public String getOrderId() {
            return this.orderId;
        }

        public Builder setOrderId(String orderId) {
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
