package codingtask.orderbook;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
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
}