package codingtask.orderbook;

import static java.lang.String.format;

public class OrderQuanityTooLargeException extends Exception {
    public OrderQuanityTooLargeException(int accumulatedExecutionQuantity, int validDemand) {
        super(format("Existing execution quantity is %d. Valid demand is %d. Maximum order size is %d",
                accumulatedExecutionQuantity, validDemand, validDemand - accumulatedExecutionQuantity));
    }
}
