package codingtask.orderbook;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ReceiptService {

    private Map<String, OrderReceipt> orderReceipts;


    public ReceiptService() {
        this.orderReceipts = new HashMap<>();

    }

    public void add(OrderReceipt orderReceipt) {
        this.orderReceipts.put(orderReceipt.getOrderId(), orderReceipt);
    }

    public Optional<OrderReceipt> get(String orderId) {
        return Optional.ofNullable(orderReceipts.get(orderId));
    }
}
