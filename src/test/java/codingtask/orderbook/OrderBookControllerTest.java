package codingtask.orderbook;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
}