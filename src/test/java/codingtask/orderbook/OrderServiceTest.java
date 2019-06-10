package codingtask.orderbook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

class OrderServiceTest {

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService();
    }

    @Test
    void calculateTheAccumulatedExecutionQuantity() {
        int accumulatedExecutionQuantity =
                orderService.getAccumulatedExecutionQuantity(
                        newArrayList(new Execution(10, ONE), new Execution(20, TEN)));

        assertThat(accumulatedExecutionQuantity, is(equalTo(30)));
    }

    @Test
    void calculateTheLargestOrder() {
        Optional<Order> largestOrder = orderService.getLargestOrder(
                newArrayList(
                        new Order.Builder()
                                .setQuantity(10)
                                .createOrder(),
                        new Order.Builder()
                                .setQuantity(30)
                                .createOrder()
                ));

        Order order = largestOrder.orElseThrow(() -> new IllegalStateException("OrderService was not able to calculate the largest order"));
        assertThat(order.getQuantity(), is(equalTo(30)));
    }

    @Test
    void calculateTheSmallestOrder() {
        Optional<Order> smallestOrder = orderService.getSmallestOrder(
                newArrayList(
                        new Order.Builder()
                                .setQuantity(10)
                                .createOrder(),
                        new Order.Builder()
                                .setQuantity(30)
                                .createOrder()
                ));

        Order order = smallestOrder.orElseThrow(() -> new IllegalStateException("OrderService was not able to calculate the smallest order"));
        assertThat(order.getQuantity(), is(equalTo(10)));
    }

    @Test
    void calculateTheEarliestOrder() {
        Optional<Order> earliestOrder = orderService.getEarliestOrder(newArrayList(
                new Order.Builder()
                        .setEntryDate(LocalDateTime.MIN)
                        .createOrder(),
                new Order.Builder()
                        .setEntryDate(LocalDateTime.MAX)
                        .createOrder()
        ));

        Order order = earliestOrder.orElseThrow(() -> new IllegalStateException("OrderService was not able to calculate the earliest order"));
        assertThat(order.getEntryDate(), is(equalTo(LocalDateTime.MIN)));
    }

    @Test
    void calculateTheMostRecentOrder() {
        Optional<Order> mostRecentOrder = orderService.getMostRecentOrder(newArrayList(
                new Order.Builder()
                        .setEntryDate(LocalDateTime.MIN)
                        .createOrder(),
                new Order.Builder()
                        .setEntryDate(LocalDateTime.MAX)
                        .createOrder()
        ));

        Order order = mostRecentOrder.orElseThrow(() -> new IllegalStateException("OrderService was not able to calculate the most recent order"));
        assertThat(order.getEntryDate(), is(equalTo(LocalDateTime.MAX)));
    }

    @Test
    void calculateDemand() {
        int demand = orderService.getDemand(newArrayList(
                new Order.Builder()
                        .setQuantity(10)
                        .createOrder(),
                new Order.Builder()
                        .setQuantity(30)
                        .createOrder()
        ));

        assertThat(demand, is(equalTo(40)));
    }

    @Test
    void calculateValidDemand() {
        OrderBook orderBook = new OrderBook("CSGN");

        orderBook.open();

        orderBook.addOrder(new Order.Builder()
                .setQuantity(10)
                .setPrice(TEN)
                .createOrder());

        orderBook.addOrder(new Order.Builder()
                .setQuantity(20)
                .setPrice(ONE)
                .createOrder());

        orderBook.setExecutionPrice(TEN);

        int demand = orderService.getValidDemand(orderBook);

        assertThat(demand, is(equalTo(10)));
    }

    @Test
    void calculateInvalidDemand() {
        OrderBook orderBook = new OrderBook("CSGN");

        orderBook.open();

        orderBook.addOrder(new Order.Builder()
                .setQuantity(10)
                .setPrice(TEN)
                .createOrder());

        orderBook.addOrder(new Order.Builder()
                .setQuantity(20)
                .setPrice(ONE)
                .createOrder());

        orderBook.setExecutionPrice(TEN);

        int demand = orderService.getInvalidDemand(orderBook);

        assertThat(demand, is(equalTo(20)));
    }

    @Test
    void calculateValidOrders() {
        OrderBook orderBook = new OrderBook("CSGN");

        orderBook.open();

        orderBook.addOrder(new Order.Builder()
                .setQuantity(10)
                .setPrice(TEN)
                .createOrder());

        orderBook.addOrder(new Order.Builder()
                .setQuantity(20)
                .setPrice(ONE)
                .createOrder());

        orderBook.setExecutionPrice(TEN);

        List<Order> validOrders = orderService.getValidOrders(orderBook);

        assertThat(validOrders.size(), is(equalTo(1)));
        assertThat(validOrders.get(0).getQuantity(), is(equalTo(10)));
    }

    @Test
    void calculateInvalidOrders() {
        OrderBook orderBook = new OrderBook("CSGN");

        orderBook.open();

        orderBook.addOrder(new Order.Builder()
                .setQuantity(10)
                .setPrice(TEN)
                .createOrder());

        orderBook.addOrder(new Order.Builder()
                .setQuantity(20)
                .setPrice(ONE)
                .createOrder());

        orderBook.setExecutionPrice(TEN);

        List<Order> validOrders = orderService.getInvalidOrders(orderBook);

        assertThat(validOrders.size(), is(equalTo(1)));
        assertThat(validOrders.get(0).getQuantity(), is(equalTo(20)));
    }

    @Test
    void calculateTheLimitBreakDown() {
        Map<BigDecimal, Integer> limitBreakDown = orderService.getLimitBreakDown(newArrayList(
                new Order.Builder()
                        .setQuantity(10)
                        .setPrice(ONE)
                        .createOrder(),
                new Order.Builder()
                        .setQuantity(30)
                        .setPrice(TEN)
                        .createOrder(),
                new Order.Builder()
                        .setQuantity(40)
                        .setPrice(TEN)
                        .createOrder()
        ));

        assertThat(limitBreakDown.get(ONE), is(equalTo(10)));
        assertThat(limitBreakDown.get(TEN), is(equalTo(70)));


    }
}