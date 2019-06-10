package codingtask.orderbook;

import com.google.common.collect.ImmutableMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

@RestController()
@RequestMapping("api/orderbooks")
public class OrderBookController {

    private OrderBookService orderBookService;
    private OrderService orderService;
    private ReceiptService receiptService;

    public OrderBookController(OrderBookService orderBookService, OrderService orderService, ReceiptService receiptService) {
        this.orderBookService = orderBookService;
        this.orderService = orderService;
        this.receiptService = receiptService;
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<Order>> getOrders(@PathVariable("id") String id) {
        return ResponseEntity.ok(this.orderBookService.getOrderBook(id).getOrders());
    }

    @PostMapping("/{id}/orders")
    public ResponseEntity<Map<String, String>> addOrder(@PathVariable String id, @RequestBody Order order, UriComponentsBuilder uriComponentsBuilder) {

        this.orderBookService.getOrderBook(id).addOrder(order);

        UriComponents uriComponents =
                uriComponentsBuilder.path("/api/orderbooks/{id}/orders/{orderId}").buildAndExpand(id, order.getId());

        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @PostMapping("/{id}/executions")
    public ResponseEntity<Map<String, String>> addExecution(@PathVariable String id, @RequestBody Execution execution) {

        OrderBook orderBook = this.orderBookService.getOrderBook(id);

        try {
            this.orderBookService.addExecution(orderBook, execution);
        } catch (ExecutionQuanityTooLargeException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.status(CREATED).build();
    }

    @PutMapping("/{id}/open")
    public ResponseEntity<Map<String, String>> openOrderBook(@PathVariable("id") String instrumentId) {
        try {
            this.orderBookService.getOrderBook(instrumentId).open();
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.ok(ImmutableMap.of("message", format("Order book for %s has been opened", instrumentId)));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<Map<String, String>> closeOrderBook(@PathVariable("id") String instrumentId) {
        try {
            this.orderBookService.getOrderBook(instrumentId).close();
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());

        }
        return ResponseEntity.ok(ImmutableMap.of("message", format("Order book for %s has been closed", instrumentId)));
    }

    @GetMapping("/{id}/orders/largest")
    public ResponseEntity<Order> getLargestOrder(@PathVariable("id") String instrumentId) {
        List<Order> orders = this.orderBookService.getOrderBook(instrumentId).getOrders();
        Optional<Order> largestOrder = this.orderService.getLargestOrder(orders);

        return largestOrder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/orders/smallest")
    public ResponseEntity<Order> getSmallestOrder(@PathVariable("id") String instrumentId) {
        List<Order> orders = this.orderBookService.getOrderBook(instrumentId).getOrders();
        Optional<Order> smallestOrder = this.orderService.getSmallestOrder(orders);

        return smallestOrder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/orders/earliest")
    public ResponseEntity<Order> getEarliestOrder(@PathVariable("id") String instrumentId) {
        List<Order> orders = this.orderBookService.getOrderBook(instrumentId).getOrders();
        Optional<Order> earliestOrder = this.orderService.getEarliestOrder(orders);

        return earliestOrder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/orders/last")
    public ResponseEntity<Order> getLastOrder(@PathVariable("id") String instrumentId) {
        List<Order> orders = this.orderBookService.getOrderBook(instrumentId).getOrders();
        Optional<Order> earliestOrder = this.orderService.getMostRecentOrder(orders);

        return earliestOrder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/orders/limitbreakdown")
    public ResponseEntity<Map<BigDecimal, Integer>> getLimitBreakDown(@PathVariable("id") String instrumentId) {
        List<Order> orders = this.orderBookService.getOrderBook(instrumentId).getOrders();
        Map<BigDecimal, Integer> limitBreakDown = this.orderService.getLimitBreakDown(orders);

        return ResponseEntity.ok(limitBreakDown);
    }

    @GetMapping("/{id}/executions/quantity")
    public ResponseEntity<Map<String, Integer>> getExecutionQuantity(@PathVariable("id") String instrumentId) {
        OrderBook orderBook = this.orderBookService.getOrderBook(instrumentId);

        if (orderBook.isOpen()) {
            throw new ResponseStatusException(BAD_REQUEST, "Execution quantity is not available if the order book is open");
        }

        List<Execution> executions = orderBook.getExecutions();
        int executionQuantity = this.orderService.getAccumulatedExecutionQuantity(executions);
        return ResponseEntity.ok(ImmutableMap.of("executionQuantity", executionQuantity));

    }

    @GetMapping("/{id}/executions/price")
    public ResponseEntity<ImmutableMap<String, BigDecimal>> getExecutionPrice(@PathVariable("id") String instrumentId) {
        OrderBook orderBook = this.orderBookService.getOrderBook(instrumentId);

        if (orderBook.isOpen()) {
            throw new ResponseStatusException(BAD_REQUEST, "Execution price is not available if the order book is open");
        }

        return orderBook.getExecutionPrice().map(body -> ResponseEntity.ok(ImmutableMap.of("executionPrice", body))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/orders/{orderId}")
    public ResponseEntity<OrderReceipt> getOrderDetails(@PathVariable("id") String instrumentId, @PathVariable("orderId") String orderId) {
        return receiptService.get(orderId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
