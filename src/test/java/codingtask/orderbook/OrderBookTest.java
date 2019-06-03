package codingtask.orderbook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.math.BigDecimal.ONE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderBookTest {

    private OrderBook orderBook;
    private Order largeFirstOrder;
    private Order smallSecondOrder;
    private LocalDateTime firstDate;
    private LocalDateTime lastDate;

    @BeforeEach
    void setUp() {
        orderBook = new OrderBook(new Instrument(1, "GOOG"));

        firstDate = LocalDateTime.of(2018, 1, 1, 1, 1);
        largeFirstOrder = new Order.Builder()
                .setEntryDate(firstDate)
                .setInstrumentId(1234)
                .setPrice(ONE)
                .setQuantity(100)
                .createOrder();

        lastDate = LocalDateTime.of(2018, 2, 2, 2, 2);
        smallSecondOrder = new Order.Builder()
                .setEntryDate(lastDate)
                .setInstrumentId(1234)
                .setPrice(ONE)
                .setQuantity(10)
                .createOrder();
    }

    @Test
    void shouldReturnEmptyWhenThereAreNoOrdersAndTheLargestOrderIsRequested() {
        assertThat(orderBook.getLargestOrder(), is(equalTo(Optional.empty())));
    }

    @Test
    void shouldReturnEmptyWhenThereAreNoOrdersAndTheSmallestOrderIsRequested() {
        assertThat(orderBook.getSmallestOrder(), is(equalTo(Optional.empty())));
    }

    @Test
    void shouldRetrieveTheLargestOrder() {

        orderBook.addOrder(largeFirstOrder);
        orderBook.addOrder(smallSecondOrder);

        assertThat(orderBook.getLargestOrder().orElseThrow(IllegalStateException::new).getQuantity(), is(100));
    }

    @Test
    void shouldRetrieveTheSmallestOrder() {

        orderBook.addOrder(largeFirstOrder);
        orderBook.addOrder(smallSecondOrder);

        assertThat(orderBook.getSmallestOrder().orElseThrow(IllegalStateException::new).getQuantity(), is(10));

    }

    @Test
    void shouldOpenAClosedOrderBookWhenOpenIsCalled() {
        this.orderBook.open();

        assertThat(this.orderBook.isOpen(), is(true));
    }

    @Test
    void shouldThrowIllegalStateExceptionIfAnAlreadyOpenOrderBookIsOpened() {
        this.orderBook.open();

        assertThrows(IllegalStateException.class, () -> this.orderBook.open());
    }

    @Test
    void shouldCloseAnOpenOrderBookWhenCloseIsCalled() {
        this.orderBook.open();
        this.orderBook.close();
        assertThat(this.orderBook.isOpen(), is(false));
    }

    @Test
    void shouldThrowIllegalStateExceptionIfAnAlreadyClosedOrderBookIsClosed() {
        assertThrows(IllegalStateException.class, () -> this.orderBook.close());
    }

    @Test
    void shouldRetrieveTheMostRecentOrder() {
        orderBook.addOrder(largeFirstOrder);
        orderBook.addOrder(smallSecondOrder);

        assertThat(this.orderBook.getMostRecentOrder().orElseThrow(IllegalStateException::new).getEntryDate(), is(lastDate));
    }

    @Test
    void shouldRetrieveTheOldestOrder() {
        orderBook.addOrder(largeFirstOrder);
        orderBook.addOrder(smallSecondOrder);

        assertThat(this.orderBook.getOldestOrder().orElseThrow(IllegalStateException::new).getEntryDate(), is(firstDate));
    }
}