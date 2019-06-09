package codingtask.orderbook;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<String> openOrderBook(@PathVariable("id") String instrumentId) {
        try {
            this.orderBookService.getOrderBook(instrumentId).open();
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }

        return ResponseEntity.ok("Order book for " + instrumentId + " has been opened");
    }

    @PutMapping("/orderbooks/{id}/close")
    public ResponseEntity<String> closeOrderBook(@PathVariable("id") String instrumentId) {
        try {
            this.orderBookService.getOrderBook(instrumentId).close();
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());

        }
        return ResponseEntity.ok("Order book for " + instrumentId + " has been closed");
    }
}
