package codingtask.orderbook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static java.math.BigDecimal.TEN;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReceiptServiceTest {

    private ReceiptService receiptService;

    @BeforeEach
    void setUp() {
        receiptService = new ReceiptService();
    }

    @Test
    void persistsAndRetrievesOrderReceipts() {
        OrderReceipt orderReceipt = new OrderReceipt.Builder()
                .setExecutionPrice(TEN)
                .setValid(true)
                .setInstrument("CSGN")
                .setOrderId("1234")
                .setOrderPrice(TEN)
                .setOrderQuantity(23)
                .setExecutionQuantity(343)
                .createOrderReceipt();

        receiptService.add(orderReceipt);

        Optional<OrderReceipt> retrievedOrderReceipt = receiptService.get("1234");

        OrderReceipt retrievedReceipt = retrievedOrderReceipt.orElseThrow(() -> new IllegalStateException("Unable to rerieve recipet"));
        assertThat(retrievedReceipt.getOrderId(), is(equalTo("1234")));
    }
}