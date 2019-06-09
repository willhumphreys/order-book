package codingtask.orderbook;

import com.google.common.collect.ImmutableMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
public class OrderBookController {

    private OrderBookService orderBookService;


    public OrderBookController(OrderBookService orderBookService) {
        this.orderBookService = orderBookService;
    }

    @PostMapping("orderbooks/{id}/orders")
    public void addOrder(String instrumentId, Order order) {

        this.orderBookService.getOrderBook(instrumentId).addOrder(order);
    }

    @PostMapping("orderbooks/{id}/executions")
    public void addExecution(String instrumentId, Execution execution) throws OrderQuanityTooLargeException {

        OrderBook orderBook = this.orderBookService.getOrderBook(instrumentId);

        this.orderBookService.addExecution(orderBook, execution);
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
}
