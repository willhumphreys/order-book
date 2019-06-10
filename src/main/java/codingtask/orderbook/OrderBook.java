package codingtask.orderbook;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderBook {

    private String instrumentId;

    private List<Order> orders;

    private List<Execution> executions;

    private BigDecimal executionPrice;

    private boolean open;

    public OrderBook(String instrumentId) {
        this.open = true;
        this.instrumentId = instrumentId;
        this.orders = new ArrayList<>();
        this.executions = new ArrayList<>();
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<Execution> getExecutions() {
        return executions;
    }

    public Optional<BigDecimal> getExecutionPrice() {
        return Optional.ofNullable(executionPrice);
    }

    public void open() {

        if (this.open) {
            throw new IllegalStateException("Order book is already open");
        }

        this.open = true;
    }

    public void close() {

        if (!this.open) {
            throw new IllegalStateException("Order book is already closed");
        }

        this.open = false;
    }

    public void setExecutionPrice(BigDecimal executionPrice) {
        this.executionPrice = executionPrice;
    }

    public void addExecution(Execution execution) {

        if (this.open) {
            throw new IllegalStateException("Executions may not be added when the order book is open");
        }

        if (executionPrice == null) {
            throw new IllegalStateException("Executions can only be added after the execution price has been set");
        }

        if (execution.getPrice().compareTo(executionPrice) != 0) {
            throw new IllegalStateException("Only one execution price is allowed and that is " + executionPrice);
        }

        this.executions.add(execution);

    }


    @Override
    public String toString() {
        return "OrderBook{" +
                "instrumentId='" + instrumentId + '\'' +
                ", orders=" + orders +
                ", executions=" + executions +
                ", executionPrice=" + executionPrice +
                ", open=" + open +
                '}';
    }

    public Order addOrder(Order order) {
        if (!this.open) {
            throw new IllegalStateException("Order book is closed. Unable to add order " + order);
        }

        this.orders.add(order);

        return order;
    }

    public boolean isOpen() {
        return open;
    }
}
