package codingtask.orderbook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderBookServiceTest {

    private OrderBookService orderBookService;
    private ReceiptService receiptService;

    @BeforeEach
    void setUp() {
        receiptService = new ReceiptService();
        orderBookService = new OrderBookService(new OrderService(), receiptService);
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

        OrderReceipt orderReceipt = this.receiptService.get(validOrder.getId()).orElseThrow(() -> new IllegalStateException("Unable to find order receipt"));
        assertThat(orderReceipt.getExecutionQuantity(), is(equalTo(100)));
        assertThat(orderReceipt.isValid(), is(true));
        assertThat(orderReceipt.getExecutionPrice(), is(TEN));
        assertThat(orderReceipt.getOrderPrice(), is(TEN));
    }

    @Test
    void executeTheBookForAMarketOrder() throws ExecutionQuanityTooLargeException {
        OrderBook orderBook = orderBookService.getOrderBook("CSGN");

        LocalDateTime orderDate = LocalDateTime.now();
        Order validOrder = new Order.Builder()
                .setEntryDate(orderDate)
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

        OrderReceipt orderReceipt = this.receiptService.get(validOrder.getId()).orElseThrow(() -> new IllegalStateException("Unable to find order receipt"));
        assertThat(orderReceipt.getExecutionQuantity(), is(equalTo(100)));
        assertThat(orderReceipt.isValid(), is(true));
        assertThat(orderReceipt.getExecutionPrice(), is(TEN));
        assertThat(orderReceipt.getOrderPrice(), is(nullValue()));
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