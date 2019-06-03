package codingtask.orderbook;

import org.springframework.hateoas.Link;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OrderBook {

    private Instrument instrument;

    private List<Order> orders;

    private List<Execution> executions;

    private BigDecimal executionPrice;

    private boolean open;

    private Map<Link, OrderReceipt> orderReceipts;

    public OrderBook(Instrument instrument) {
        this.instrument = instrument;
        this.orders = new ArrayList<>();
        this.executions = new ArrayList<>();

        this.orderReceipts = new HashMap<Link, OrderReceipt>();
    }

    public void open() {

        if (this.open) {
            throw new IllegalStateException("Order book is already open");
        }

        this.executionPrice = null;
        this.open = true;
    }

    public void close() {

        if (!this.open) {
            throw new IllegalStateException("Order book is already closed");
        }

        this.open = false;
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }


    public Map<BigDecimal, Integer> getLimitBreakDown() {

        return this.orders.stream().collect(Collectors.groupingBy(Order::getPrice,
                Collectors.summingInt(Order::getQuantity)));

    }

    public int getAccumulatedExecutionQuantity() {
        return this.executions.stream().mapToInt(Execution::getQuantity).sum();
    }

    public void addExecution(Execution execution) {

        if (this.open) {
            throw new IllegalStateException("Executions may not be added when the order book is open");
        }

        if (executionPrice == null) {
            this.executionPrice = execution.getPrice();
        }

        if (execution.getPrice().compareTo(this.executionPrice) != 0) {
            throw new IllegalStateException("Only one execution price is allowed and that is " + this.executionPrice);
        }


        int validDemand = getValidDemand();

        int propsedNewExecutionQuantity = getAccumulatedExecutionQuantity() + execution.getQuantity();
        if (propsedNewExecutionQuantity <= validDemand) {
            this.executions.add(execution);

            if (propsedNewExecutionQuantity == validDemand) {
                executeBook();
            }

        } else {
            throw new IllegalStateException(String.format("Unable to add Execution it would exceed valid order quantity %d Proposed new quantity %d", validDemand, propsedNewExecutionQuantity));
        }

    }

    private void executeBook() {
        System.out.println("Executing book");

        addInvalidOrders();

        List<Order> validOrders = this.getValidOrders();

        List<OrderReceipt.Builder> orderReceiptBuilders = this.getValidOrders().stream().map(order ->
                new OrderReceipt.Builder()
                        .setOrderQuantity(order.getQuantity())
                        .setOrderPrice(order.getPrice())
                        .setOrderId(order.getId()))
                .collect(Collectors.toList());

        List<Order> ordersToFill = new LinkedList<>(validOrders);

        int accumulatedExecutionQuantity = getAccumulatedExecutionQuantity();


        IntStream.range(0, accumulatedExecutionQuantity).forEach(count -> {
            int currentBuilderIndex = count % ordersToFill.size();
            OrderReceipt.Builder orderReceiptBuilder = orderReceiptBuilders.get(currentBuilderIndex);
            orderReceiptBuilder.incrementExecutionQuantity();
            if (orderReceiptBuilder.isFilled()) {
                OrderReceipt orderReceipt = orderReceiptBuilder.createOrderReceipt();
                this.orderReceipts.put(orderReceipt.getOrderId(), orderReceipt);
                orderReceiptBuilders.remove(currentBuilderIndex);
            }
        });


    }

    private void addInvalidOrders() {

        Map<Link, OrderReceipt> invalidOrderReceipts = this.getInvalidOrders().stream()
                .map(order -> new OrderReceipt.Builder()
                        .setOrderPrice(order.getPrice())
                        .setOrderId(order.getId())
                        .createOrderReceipt())
                .collect(Collectors.toMap(OrderReceipt::getOrderId, c -> c));

        this.orderReceipts.putAll(invalidOrderReceipts);
    }

    public List<Order> getValidOrders() {
        return this.orders.stream().filter(order -> order.getPrice().compareTo(executionPrice) >= 0).collect(Collectors.toList());
    }

    public Optional<BigDecimal> getExecutionPrice() {
        return Optional.ofNullable(executionPrice);
    }

    public List<Order> getInvalidOrders() {
        return this.orders.stream().filter(order -> order.getPrice().compareTo(executionPrice) < 0).collect(Collectors.toList());
    }

    public Optional<Order> getLargestOrder() {
        return orders.stream().max(Comparator.comparing(Order::getQuantity));
    }

    public Optional<Order> getSmallestOrder() {
        return orders.stream().min(Comparator.comparing(Order::getQuantity));
    }

    public Optional<Order> getMostRecentOrder() {
        return orders.stream().max(Comparator.comparing(Order::getEntryDate));
    }

    public Optional<Order> getOldestOrder() {
        return orders.stream().min(Comparator.comparing(Order::getEntryDate));
    }

    public int getDemand() {
        return this.orders.stream().mapToInt(Order::getQuantity).sum();
    }

    public int getValidDemand() {
        return getValidOrders().stream().mapToInt(Order::getQuantity).sum();
    }

    public int getInvalidDemand() {
        return getInvalidOrders().stream().mapToInt(Order::getQuantity).sum();
    }


    public boolean isOpen() {
        return open;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
