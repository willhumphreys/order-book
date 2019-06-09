package codingtask.orderbook;

import com.google.common.collect.ImmutableMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
public class OrderBookController {

    private OrderBookService orderBookService;
    private OrderService orderService;

    public OrderBookController(OrderBookService orderBookService, OrderService orderService) {
        this.orderBookService = orderBookService;
        this.orderService = orderService;
    }

    @PostMapping("orderbooks/{id}/orders")
    public ResponseEntity<Object> addOrder(@PathVariable String id, @RequestBody Order order, UriComponentsBuilder uriComponentsBuilder) {

        this.orderBookService.getOrderBook(id).addOrder(order);

        UriComponents uriComponents =
                uriComponentsBuilder.path("/orderbooks/{id}/orders/{orderId}").buildAndExpand(id, order.getId());

        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @PostMapping("orderbooks/{id}/executions")
    public ResponseEntity<Object> addExecution(@PathVariable String id, @RequestBody Execution execution) {

        OrderBook orderBook = this.orderBookService.getOrderBook(id);

        try {
            this.orderBookService.addExecution(orderBook, execution);
        } catch (OrderQuanityTooLargeException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.status(CREATED).build();
    }

    @PutMapping("/orderbooks/{id}/open")
    public ResponseEntity<Map<String, String>> openOrderBook(@PathVariable("id") String instrumentId) {
        try {
            this.orderBookService.getOrderBook(instrumentId).open();
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.ok(ImmutableMap.of("message", format("Order book for %s has been opened", instrumentId)));
    }

    @PutMapping("/orderbooks/{id}/close")
    public ResponseEntity<Map<String, String>> closeOrderBook(@PathVariable("id") String instrumentId) {
        try {
            this.orderBookService.getOrderBook(instrumentId).close();
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());

        }
        return ResponseEntity.ok(ImmutableMap.of("message", format("Order book for %s has been closed", instrumentId)));
    }

    @GetMapping("/orderbooks/{id}/orders/largest")
    public ResponseEntity<Order> getLargestOrder(@PathVariable("id") String instrumentId) {
        List<Order> orders = this.orderBookService.getOrderBook(instrumentId).getOrders();
        Optional<Order> largestOrder = this.orderService.getLargestOrder(orders);

        return largestOrder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/orderbooks/{id}/orders/smallest")
    public ResponseEntity<Order> getSmallestOrder(@PathVariable("id") String instrumentId) {
        List<Order> orders = this.orderBookService.getOrderBook(instrumentId).getOrders();
        Optional<Order> smallestOrder = this.orderService.getSmallestOrder(orders);

        return smallestOrder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/orderbooks/{id}/orders/earliest")
    public ResponseEntity<Order> getEarliestOrder(@PathVariable("id") String instrumentId) {
        List<Order> orders = this.orderBookService.getOrderBook(instrumentId).getOrders();
        Optional<Order> earliestOrder = this.orderService.getEarliestOrder(orders);

        return earliestOrder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/orderbooks/{id}/orders/last")
    public ResponseEntity<Order> getLastOrder(@PathVariable("id") String instrumentId) {
        List<Order> orders = this.orderBookService.getOrderBook(instrumentId).getOrders();
        Optional<Order> earliestOrder = this.orderService.getMostRecentOrder(orders);

        return earliestOrder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
