package codingtask.orderbook;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.fail;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class OrderBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void return200WithOrderBookIsOpened() throws Exception {

        this.mockMvc.perform(put("/orderbooks/goog/close")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order book for goog has been closed"));

        this.mockMvc.perform(put("/orderbooks/goog/open")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.message").value("Order book for goog has been opened"));
    }

    @Test
    public void returnStatusCode400IfTheOpenActionIsPerformedOnAnOpenOrderBook() throws Exception {
        this.mockMvc.perform(put("/orderbooks/goog/open"));

        this.mockMvc.perform(put("/orderbooks/goog/open")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Order book is already open"));
    }

    @Test
    public void returns200WhenTheOrderBookIsClosed() throws Exception {

        this.mockMvc.perform(put("/orderbooks/goog/open")
                .accept(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(put("/orderbooks/goog/close")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order book for goog has been closed"));

    }

    @Test
    public void returnStatusCode400IfTheCloseActionIsPerformedOnAClosedOrderBook() throws Exception {

        this.mockMvc.perform(put("/orderbooks/goog/close")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        this.mockMvc.perform(put("/orderbooks/goog/close")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Order book is already closed"));
    }

    @Test
    public void return201WhenAnOrderIsCreated() throws Exception {
        this.mockMvc.perform(put("/orderbooks/goog/open")
                .accept(MediaType.APPLICATION_JSON));

        String order = "{\"quantity\" : \"5\", \"entryDate\" : \"2019-06-09T09:04:29Z\",\"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(redirectedUrlPattern("http://localhost/orderbooks/goog/orders/*"))
                .andExpect(status().isCreated()).andReturn().getResponse();
    }

    @Test
    public void return201WhenAnExecutionIsCreated() throws Exception {

        this.mockMvc.perform(put("/orderbooks/goog/open")
                .accept(MediaType.APPLICATION_JSON));

        String order = "{\"quantity\" : \"5\", \"entryDate\" : \"2019-06-09T09:04:29Z\",\"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));


        this.mockMvc.perform(put("/orderbooks/goog/close")
                .accept(MediaType.APPLICATION_JSON));

        String execution = "{\"quantity\" : \"5\", \"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/executions")
                .content(execution)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void return400WhenSubmittedExecutionIsLargerThenOrderQuantity() throws Exception {

        this.mockMvc.perform(put("/orderbooks/goog/open")
                .accept(MediaType.APPLICATION_JSON));

        String order = "{\"quantity\" : \"5\", \"entryDate\" : \"2019-06-09T09:04:29Z\",\"price\" : \"150\" }";

        this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));


        this.mockMvc.perform(put("/orderbooks/goog/close")
                .accept(MediaType.APPLICATION_JSON));

        String execution = "{\"quantity\" : \"3\", \"price\" : \"140\" }";

        this.mockMvc.perform(post("/orderbooks/goog/executions")
                .content(execution)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        String execution2 = "{\"quantity\" : \"3\", \"price\" : \"140\" }";

        this.mockMvc.perform(post("/orderbooks/goog/executions")
                .content(execution2)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Proposed execution quantity is 6 but the valid demand is only 5"));
    }

    @Test
    public void return404IfThereIsNoLargestOrder() throws Exception {
        this.mockMvc.perform(get("/orderbooks/goog/orders/largest"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void returnLargestOrderWhenThereAreMultipleOrders() throws Exception {
        this.mockMvc.perform(put("/orderbooks/goog/open")
                .accept(MediaType.APPLICATION_JSON));

        String order = "{\"quantity\" : \"5\", \"entryDate\" : \"2019-06-09T09:04:29Z\",\"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        String order2 = "{\"quantity\" : \"10\", \"entryDate\" : \"2019-06-09T09:04:29Z\",\"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order2)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(get("/orderbooks/goog/orders/largest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    public void return404IfThereIsNoSmallestOrder() throws Exception {
        this.mockMvc.perform(get("/orderbooks/goog/orders/smallest"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void returnSmallestOrderWhenThereAreMultipleOrders() throws Exception {
        this.mockMvc.perform(put("/orderbooks/goog/open")
                .accept(MediaType.APPLICATION_JSON));

        String order = "{\"quantity\" : \"5\", \"entryDate\" : \"2019-06-09T09:04:29Z\",\"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        String order2 = "{\"quantity\" : \"10\", \"entryDate\" : \"2019-06-09T09:04:29Z\",\"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order2)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(get("/orderbooks/goog/orders/smallest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5));
    }

    @Test
    public void return404IfThereIsNoEarliestOrder() throws Exception {
        this.mockMvc.perform(get("/orderbooks/goog/orders/earliest"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void returnEarliestOrderWhenThereAreMultipleOrders() throws Exception {
        this.mockMvc.perform(put("/orderbooks/goog/open")
                .accept(MediaType.APPLICATION_JSON));

        String order = "{\"quantity\" : \"5\", \"entryDate\" : \"2019-06-09T09:05:29Z\",\"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        String order2 = "{\"quantity\" : \"10\", \"entryDate\" : \"2019-06-09T09:04:29Z\",\"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order2)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(get("/orderbooks/goog/orders/earliest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entryDate").value("2019-06-09T09:04:29"));
    }

    @Test
    public void return404IfThereIsNoLastOrder() throws Exception {
        this.mockMvc.perform(get("/orderbooks/goog/orders/last"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void returnLastOrderWhenThereAreMultipleOrders() throws Exception {
        this.mockMvc.perform(put("/orderbooks/goog/open")
                .accept(MediaType.APPLICATION_JSON));

        String order = "{\"quantity\" : \"5\", \"entryDate\" : \"2019-06-09T09:05:29Z\",\"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        String order2 = "{\"quantity\" : \"10\", \"entryDate\" : \"2019-06-09T09:04:29Z\",\"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order2)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(get("/orderbooks/goog/orders/last"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entryDate").value("2019-06-09T09:05:29"));
    }

    @Test
    public void returnLimitBreakDown() throws Exception {
        this.mockMvc.perform(put("/orderbooks/goog/open")
                .accept(MediaType.APPLICATION_JSON));

        String order = "{\"quantity\" : \"5\", \"entryDate\" : \"2019-06-09T09:05:29Z\",\"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        String order2 = "{\"quantity\" : \"10\", \"entryDate\" : \"2019-06-09T09:04:29Z\",\"price\" : \"122\" }";

        this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order2)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(get("/orderbooks/goog/orders/limitbreakdown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.122").value("10"))
                .andExpect(jsonPath("$.120").value("5"));
    }

    @Test
    public void return400IfTheOrderBookIsOpenAndARequestIsMadeForTheExecutionQuantity() throws Exception {
        this.mockMvc.perform(put("/orderbooks/goog/open")
                .accept(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(get("/orderbooks/goog/executions/quantity"))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Execution quantity is not available if the order book is open"));

    }

    @Test
    public void returnExecutionQuantityIfTheOrderBookIsClosed() throws Exception {

        this.mockMvc.perform(put("/orderbooks/goog/open")
                .accept(MediaType.APPLICATION_JSON));


        String order = "{\"quantity\" : \"5\", \"entryDate\" : \"2019-06-09T09:05:29Z\",\"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(put("/orderbooks/goog/close")
                .accept(MediaType.APPLICATION_JSON));

        String execution = "{\"quantity\" : \"3\", \"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/executions")
                .content(execution)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        this.mockMvc.perform(get("/orderbooks/goog/executions/quantity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.executionQuantity").value("3"));
    }

    @Test
    public void returnExecutionPriceIfTheOrderBookIsClosed() throws Exception {
        String order = "{\"quantity\" : \"5\", \"entryDate\" : \"2019-06-09T09:05:29Z\",\"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(put("/orderbooks/goog/close")
                .accept(MediaType.APPLICATION_JSON));

        String execution = "{\"quantity\" : \"3\", \"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/executions")
                .content(execution)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        this.mockMvc.perform(get("/orderbooks/goog/executions/price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.executionPrice").value("120"));
    }

    @Test
    public void return404IfExecutionPriceHasNotBeenSet() throws Exception {

        this.mockMvc.perform(put("/orderbooks/goog/open")
                .accept(MediaType.APPLICATION_JSON));

        String order = "{\"quantity\" : \"5\", \"entryDate\" : \"2019-06-09T09:05:29Z\",\"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(put("/orderbooks/goog/close")
                .accept(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(get("/orderbooks/goog/executions/price"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void return400IfTheExecutionPriceIsRequestedWhenTheOrderBookIsOpen() throws Exception {

        this.mockMvc.perform(put("/orderbooks/goog/open")
                .accept(MediaType.APPLICATION_JSON));

        String order = "{\"quantity\" : \"5\", \"entryDate\" : \"2019-06-09T09:05:29Z\",\"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(get("/orderbooks/goog/executions/price"))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Execution price is not available if the order book is open"));

    }

    @Test
    public void return404WhenRequestingAnOrderReceiptIfTheOrderHasNotBeenExecuted() throws Exception {

        this.mockMvc.perform(put("/orderbooks/goog/open")
                .accept(MediaType.APPLICATION_JSON));

        String order = "{\"quantity\" : \"5\", \"entryDate\" : \"2019-06-09T09:04:29Z\",\"price\" : \"120\" }";

        MockHttpServletResponse response = this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        String orderLocation = response.getHeader("Location");

        if (orderLocation == null) {
            fail("Unable to read the location header");
        }

        this.mockMvc.perform(put("/orderbooks/goog/close")
                .accept(MediaType.APPLICATION_JSON));

        String execution = "{\"quantity\" : \"4\", \"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/executions")
                .content(execution)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        this.mockMvc.perform(get(orderLocation)).andExpect(status().isNotFound());
    }

    @Test
    public void returnAnOrderReceiptWhenOrderHasBeenExecuted() throws Exception {

        this.mockMvc.perform(put("/orderbooks/goog/open")
                .accept(MediaType.APPLICATION_JSON));

        String order = "{\"quantity\" : \"5\", \"entryDate\" : \"2019-06-09T09:04:29Z\",\"price\" : \"120\" }";

        MockHttpServletResponse response = this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        String orderLocation = response.getHeader("Location");

        if (orderLocation == null) {
            fail("Unable to read the location header");
        }

        this.mockMvc.perform(put("/orderbooks/goog/close")
                .accept(MediaType.APPLICATION_JSON));

        String execution = "{\"quantity\" : \"5\", \"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/executions")
                .content(execution)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        String orderId = orderLocation.substring(orderLocation.lastIndexOf("/") + 1);

        this.mockMvc.perform(get(orderLocation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.instrument").value("goog"))
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.executionQuantity").value(5))
                .andExpect(jsonPath("$.orderPrice").value(120))
                .andExpect(jsonPath("$.executionPrice").value(120))
                .andExpect(jsonPath("$.orderQuantity").value(5));
    }

    @Test
    public void returnTwoOrderReceiptsWhenTwoOrdersAreExecuted() throws Exception {

        this.mockMvc.perform(put("/orderbooks/goog/open")
                .accept(MediaType.APPLICATION_JSON));

        String order = "{\"quantity\" : \"3\", \"entryDate\" : \"2019-06-09T09:04:29Z\",\"price\" : \"120\" }";

        MockHttpServletResponse response = this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        String orderLocation = response.getHeader("Location");

        if (orderLocation == null) {
            fail("Unable to read the location header");
        }

        String order2 = "{\"quantity\" : \"2\", \"entryDate\" : \"2019-06-09T09:04:29Z\",\"price\" : \"120\" }";

        MockHttpServletResponse response2 = this.mockMvc.perform(post("/orderbooks/goog/orders")
                .content(order2)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        String orderLocation2 = response2.getHeader("Location");

        if (orderLocation2 == null) {
            fail("Unable to read the location header");
        }

        this.mockMvc.perform(put("/orderbooks/goog/close")
                .accept(MediaType.APPLICATION_JSON));

        String execution = "{\"quantity\" : \"5\", \"price\" : \"120\" }";

        this.mockMvc.perform(post("/orderbooks/goog/executions")
                .content(execution)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        String orderId = orderLocation.substring(orderLocation.lastIndexOf("/") + 1);

        this.mockMvc.perform(get(orderLocation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.instrument").value("goog"))
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.executionQuantity").value(3))
                .andExpect(jsonPath("$.orderPrice").value(120))
                .andExpect(jsonPath("$.executionPrice").value(120))
                .andExpect(jsonPath("$.orderQuantity").value(3));

        String orderId2 = orderLocation2.substring(orderLocation2.lastIndexOf("/") + 1);

        this.mockMvc.perform(get(orderLocation2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.instrument").value("goog"))
                .andExpect(jsonPath("$.orderId").value(orderId2))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.executionQuantity").value(2))
                .andExpect(jsonPath("$.orderPrice").value(120))
                .andExpect(jsonPath("$.executionPrice").value(120))
                .andExpect(jsonPath("$.orderQuantity").value(2));
    }
}