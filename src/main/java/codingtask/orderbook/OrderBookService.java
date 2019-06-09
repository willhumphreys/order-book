package codingtask.orderbook;

import com.google.common.collect.Iterators;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Service
public class OrderBookService {

    private Map<String, OrderBook> orderBooks;

    private OrderService orderService;
    private ReceiptService receiptService;

    public OrderBookService(OrderService orderService, ReceiptService receiptService) {
        this.orderService = orderService;
        this.receiptService = receiptService;
        this.orderBooks = new HashMap<>();
    }

    public OrderBook getOrderBook(String instrumentId) {
        return this.orderBooks.computeIfAbsent(instrumentId, OrderBook::new);
    }

    public void addExecution(OrderBook orderBook, Execution execution) throws OrderQuanityTooLargeException {

        int validDemand = orderService.getValidDemand(orderBook);

        int accumulatedExecutionQuantity = this.orderService.getAccumulatedExecutionQuantity(orderBook.getExecutions());
        int proposedNewExecutionQuantity = accumulatedExecutionQuantity + execution.getQuantity();

        if (proposedNewExecutionQuantity <= validDemand) {

            orderBook.addExecution(execution);

            if (accumulatedExecutionQuantity == validDemand) {
                executeBook(orderBook);
            }

        }

        throw new OrderQuanityTooLargeException(accumulatedExecutionQuantity, validDemand);

    }

    void persistReceiptsForInvalidOrders(String instrumentId, List<Order> invalidOrders) {

        invalidOrders.stream()
                .map(order -> new OrderReceipt.Builder()
                        .setInstrument(instrumentId)
                        .setOrderPrice(order.getPrice())
                        .setOrderId(order.getId())
                        .createOrderReceipt()).forEach(orderReceipt -> this.receiptService.add(orderReceipt));
    }

    public void executeBook(OrderBook orderBook) {

        this.persistReceiptsForInvalidOrders(orderBook.getInstrumentId(), this.orderService.getInvalidOrders(orderBook));

        List<Order> validOrders = this.orderService.getValidOrders(orderBook);

        Map<Link, OrderReceipt.Builder> orderReceiptMap = validOrders
                .stream()
                .map(order ->
                        new OrderReceipt.Builder()
                                .setInstrument(orderBook.getInstrumentId())
                                .setOrderQuantity(order.getQuantity())
                                .setOrderPrice(order.getPrice())
                                .setOrderId(order.getId()))
                .collect(Collectors.toMap(OrderReceipt.Builder::getOrderId, identity()));


        Iterator<Order> cycle = Iterators.cycle(validOrders);
        int count = 0;
        while (cycle.hasNext() || count < this.orderService.getAccumulatedExecutionQuantity(orderBook.getExecutions())) {
            count++;
            Order next = cycle.next();
            OrderReceipt.Builder orderReceiptBuilder = orderReceiptMap.get(next.getId());
            orderReceiptBuilder.incrementExecutionQuantity();

            if (orderReceiptBuilder.isFilled()) {
                cycle.remove();
                OrderReceipt orderReceipt = orderReceiptBuilder.createOrderReceipt();
                this.receiptService.add(orderReceipt);
            }
        }

        this.orderBooks.remove(orderBook.getInstrumentId());
    }

}
