package codingtask.orderbook;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    public List<Order> getValidOrders(OrderBook orderBook) {
        return orderBook.getOrders().stream()
                .filter(order -> order.getPrice() == null || order.getPrice().compareTo(orderBook.getExecutionPrice().orElseThrow(() -> new IllegalStateException("Unable to calculate valid orders if the execution price is not set"))) >= 0).collect(Collectors.toList());
    }

    public List<Order> getInvalidOrders(OrderBook orderBook) {
        return orderBook.getOrders().stream().filter(order -> order.getPrice() != null && order.getPrice().compareTo(orderBook.getExecutionPrice().orElseThrow(() -> new IllegalStateException("Unable to calculate invalid orders if the execution price is not set"))) < 0).collect(Collectors.toList());
    }

    public Optional<Order> getLargestOrder(List<Order> orders) {
        return orders.stream().max(Comparator.comparing(Order::getQuantity));
    }

    public Optional<Order> getSmallestOrder(List<Order> orders) {
        return orders.stream().min(Comparator.comparing(Order::getQuantity));
    }

    public Optional<Order> getMostRecentOrder(List<Order> orders) {
        return orders.stream().max(Comparator.comparing(Order::getEntryDate));
    }

    public Optional<Order> getEarliestOrder(List<Order> orders) {
        return orders.stream().min(Comparator.comparing(Order::getEntryDate));
    }

    public int getDemand(List<Order> orders) {
        return orders.stream().mapToInt(Order::getQuantity).sum();
    }

    public int getValidDemand(OrderBook orderBook) {
        return getValidOrders(orderBook).stream().mapToInt(Order::getQuantity).sum();
    }

    public int getInvalidDemand(OrderBook orderBook) {
        return getInvalidOrders(orderBook).stream().mapToInt(Order::getQuantity).sum();
    }

    public Map<BigDecimal, Integer> getLimitBreakDown(List<Order> orders) {
        return orders.stream().filter(order -> order.getPrice() != null)
                .collect(Collectors.groupingBy(Order::getPrice,
                Collectors.summingInt(Order::getQuantity)));

    }

    public int getAccumulatedExecutionQuantity(List<Execution> executions) {
        return executions.stream().mapToInt(Execution::getQuantity).sum();
    }


}
