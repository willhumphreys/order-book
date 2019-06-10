package codingtask.orderbook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderBookServiceTest {

    private OrderBookService orderBookService;

    @BeforeEach
    void setUp() {
        orderBookService = new OrderBookService(new OrderService(), new ReceiptService());
    }

    @Test
    void executeTheBook() throws ExecutionQuanityTooLargeException {
        OrderBook orderBook = orderBookService.getOrderBook("CSGN");

        LocalDateTime orderDate = LocalDateTime.now();
        Order validOrder = new Order.Builder()
                .setEntryDate(orderDate)
                .setPrice(TEN)
                .setQuantity(100)
                .createOrder();

        orderBook.addOrder(validOrder);

        Order invalidOrder = new Order.Builder()
                .setEntryDate(orderDate)
                .setPrice(ONE)
                .setQuantity(50)
                .createOrder();

        orderBook.addOrder(invalidOrder);

        orderBook.close();

        orderBookService.addExecution(orderBook, new Execution(100, TEN));

    }

    @Test
    void ThrowExceptionIfTheExecutionQuantityIfTooLargeForTheOrderBook() {
        OrderBook orderBook = orderBookService.getOrderBook("CSGN");

        LocalDateTime orderDate = LocalDateTime.now();
        Order validOrder = new Order.Builder()
                .setEntryDate(orderDate)
                .setPrice(TEN)
                .setQuantity(100)
                .createOrder();

        orderBook.addOrder(validOrder);

        orderBook.close();

        assertThrows(ExecutionQuanityTooLargeException.class, () -> orderBookService.addExecution(orderBook, new Execution(200, TEN)));
    }
}