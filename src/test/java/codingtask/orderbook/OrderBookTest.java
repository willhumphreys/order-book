package codingtask.orderbook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderBookTest {

    private static final int UUID_LENGTH = 36;
    private OrderBook csgnOrderBook;

    @BeforeEach
    void setUp() {
        OrderBookService orderBookService = new OrderBookService(new OrderService(), new ReceiptService());

        csgnOrderBook = orderBookService.getOrderBook("CSGN");
    }

    @Test
    void returnsTheOrderBookForAnInstrument() {

        assertThat(csgnOrderBook, is(notNullValue()));
        assertThat(csgnOrderBook.getExecutionPrice().isPresent(), is(false));
        assertThat(csgnOrderBook.isOpen(), is(true));
        assertThat(csgnOrderBook.getExecutionPrice().isPresent(), is(false));
        assertThat(csgnOrderBook.getInstrumentId(), is(equalTo("CSGN")));
        assertThat(csgnOrderBook.getOrders().isEmpty(), is(true));
        assertThat(csgnOrderBook.getExecutions().isEmpty(), is(true));
    }

    @Test
    void throwExceptionIfYouTryToAddOrdersToAClosedOrderBook() {
        csgnOrderBook.close();

        Order order = new Order.Builder()
                .setEntryDate(LocalDateTime.now())
                .setPrice(TEN)
                .setQuantity(100)
                .createOrder();

        assertThrows(IllegalStateException.class, () -> csgnOrderBook.addOrder(order));
    }

    @Test
    void orderBookCanBeOpenedAndOrdersAddedToIt() {
        LocalDateTime orderDate = LocalDateTime.now();
        Order order = new Order.Builder()
                .setEntryDate(orderDate)
                .setPrice(TEN)
                .setQuantity(100)
                .createOrder();

        csgnOrderBook.addOrder(order);

        List<Order> orders = csgnOrderBook.getOrders();

        assertThat(orders.size(), is(equalTo(1)));

        assertThat(orders.get(0).getQuantity(), is(equalTo(100)));
        assertThat(orders.get(0).getPrice(), is(equalTo(TEN)));
        assertThat(orders.get(0).getEntryDate(), is(equalTo(orderDate)));
        assertThat(orders.get(0).getId().length(), is(UUID_LENGTH));
    }

    @Test
    void throwsExceptionIfYouTryToAddExecutionsToAnOpenOrderBook() {
        assertThrows(IllegalStateException.class, () -> csgnOrderBook.addExecution(new Execution(10, TEN)));
    }

    @Test
    void throwsExceptionWhenAddingExecutionsToAnOrderBookWhereTheExecutionPriceIsNotSet() {
        assertThrows(IllegalStateException.class, () -> csgnOrderBook.addExecution(new Execution(10, TEN)));
    }

    @Test
    void executionsCanBeAddedToAClosedOrderBook() {
        csgnOrderBook.close();

        csgnOrderBook.setExecutionPrice(TEN);
        csgnOrderBook.addExecution(new Execution(10, TEN));
        csgnOrderBook.addExecution(new Execution(10, TEN));
    }

    @Test
    void throwsExceptionWhenAddingExecutionWithAPriceDifferentToTheExecutionPrice() {
        csgnOrderBook.close();

        csgnOrderBook.setExecutionPrice(TEN);
        csgnOrderBook.addExecution(new Execution(10, TEN));

        assertThrows(IllegalStateException.class, () -> csgnOrderBook.addExecution(new Execution(10, ONE)));
    }

    @Test
    void executesOrderBookWhenTheExecutionQuantityEqualsTheValidOrderQuantity() {
        LocalDateTime orderDate = LocalDateTime.now();
        Order order = new Order.Builder()
                .setEntryDate(orderDate)
                .setPrice(TEN)
                .setQuantity(100)
                .createOrder();

        csgnOrderBook.addOrder(order);
    }

    @Test
    void throwExceptionIfYouTryToOpenAnOpenOrderBook() {
        assertThrows(IllegalStateException.class, () -> csgnOrderBook.open());
    }

    @Test
    void throwExceptionIfYouTryToCloseAClosedOrderBook() {
        csgnOrderBook.close();
        assertThrows(IllegalStateException.class, () -> csgnOrderBook.close());
    }

    @Test
    void closesAnOpenOrderBook() {
        csgnOrderBook.close();

        assertThat(csgnOrderBook.isOpen(), is(false));
    }
}