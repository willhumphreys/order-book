package codingtask.orderbook;

import static java.lang.String.format;

public class OrderQuanityTooLargeException extends Exception {
    public OrderQuanityTooLargeException(int proposedNewExecutionQuantity, int validDemand) {
        super(format("Proposed execution quantity is %d but the valid demand is only %d",
                proposedNewExecutionQuantity, validDemand));
    }
}
