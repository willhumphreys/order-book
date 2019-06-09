package codingtask.orderbook;

class OrderBookSomethingServiceTest {

//    private OrderBookSomethingService orderBookSomethingService;
//    private Order largeFirstOrder;
//    private Order smallSecondOrder;
//    private LocalDateTime firstDate;
//    private LocalDateTime lastDate;
//
//    @BeforeEach
//    void setUp() {
//        orderBookSomethingService = new OrderBookSomethingService(new Instrument(1, "GOOG"));
//
//        firstDate = LocalDateTime.of(2018, 1, 1, 1, 1);
//        largeFirstOrder = new Order.Builder()
//                .setEntryDate(firstDate)
//                .setInstrumentId(1234)
//                .setPrice(ONE)
//                .setQuantity(100)
//                .createOrder();
//
//        lastDate = LocalDateTime.of(2018, 2, 2, 2, 2);
//        smallSecondOrder = new Order.Builder()
//                .setEntryDate(lastDate)
//                .setInstrumentId(1234)
//                .setPrice(ONE)
//                .setQuantity(10)
//                .createOrder();
//    }
//
//    @Test
//    void shouldReturnEmptyWhenThereAreNoOrdersAndTheLargestOrderIsRequested() {
//        assertThat(orderBookSomethingService.getLargestOrder(), is(equalTo(Optional.empty())));
//    }
//
//    @Test
//    void shouldReturnEmptyWhenThereAreNoOrdersAndTheSmallestOrderIsRequested() {
//        assertThat(orderBookSomethingService.getSmallestOrder(), is(equalTo(Optional.empty())));
//    }
//
//    @Test
//    void shouldRetrieveTheLargestOrder() {
//
//        orderBookSomethingService.addOrder(largeFirstOrder);
//        orderBookSomethingService.addOrder(smallSecondOrder);
//
//        assertThat(orderBookSomethingService.getLargestOrder().orElseThrow(IllegalStateException::new).getQuantity(), is(100));
//    }
//
//    @Test
//    void shouldRetrieveTheSmallestOrder() {
//
//        orderBookSomethingService.addOrder(largeFirstOrder);
//        orderBookSomethingService.addOrder(smallSecondOrder);
//
//        assertThat(orderBookSomethingService.getSmallestOrder().orElseThrow(IllegalStateException::new).getQuantity(), is(10));
//
//    }
//
//    @Test
//    void shouldOpenAClosedOrderBookWhenOpenIsCalled() {
//        this.orderBookSomethingService.open();
//
//        assertThat(this.orderBookSomethingService.isOpen(), is(true));
//    }
//
//    @Test
//    void shouldThrowIllegalStateExceptionIfAnAlreadyOpenOrderBookIsOpened() {
//        this.orderBookSomethingService.open();
//
//        assertThrows(IllegalStateException.class, () -> this.orderBookSomethingService.open());
//    }
//
//    @Test
//    void shouldCloseAnOpenOrderBookWhenCloseIsCalled() {
//        this.orderBookSomethingService.open();
//        this.orderBookSomethingService.close();
//        assertThat(this.orderBookSomethingService.isOpen(), is(false));
//    }
//
//    @Test
//    void shouldThrowIllegalStateExceptionIfAnAlreadyClosedOrderBookIsClosed() {
//        assertThrows(IllegalStateException.class, () -> this.orderBookSomethingService.close());
//    }
//
//    @Test
//    void shouldRetrieveTheMostRecentOrder() {
//        orderBookSomethingService.addOrder(largeFirstOrder);
//        orderBookSomethingService.addOrder(smallSecondOrder);
//
//        assertThat(this.orderBookSomethingService.getMostRecentOrder().orElseThrow(IllegalStateException::new).getEntryDate(), is(lastDate));
//    }
//
//    @Test
//    void shouldRetrieveTheOldestOrder() {
//        orderBookSomethingService.addOrder(largeFirstOrder);
//        orderBookSomethingService.addOrder(smallSecondOrder);
//
//        assertThat(this.orderBookSomethingService.getOldestOrder().orElseThrow(IllegalStateException::new).getEntryDate(), is(firstDate));
//    }
//
//    @Test
//    void shouldDoSomething() {
//        this.orderBookSomethingService.addExecution(Execution.atMarket(20));
//        this.orderBookSomethingService.addExecution(Execution.atLimit(20, BigDecimal.TEN));
//    }
}