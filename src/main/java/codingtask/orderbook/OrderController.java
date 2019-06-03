package codingtask.orderbook;

import com.google.common.collect.Lists;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class OrderController {

    @RequestMapping("/orders")
    public HttpEntity<List<Order>> retrieveOrders() {

        Order order = new Order.Builder().setQuantity(5).setEntryDate(LocalDateTime.now()).setInstrumentId(3434).setPrice(BigDecimal.valueOf(1234)).createOrder();
        order.add(linkTo(methodOn(OrderController.class).retrieveOrders()).withSelfRel());

        return new ResponseEntity<>(Lists.newArrayList(order), HttpStatus.OK);
    }

    @PostMapping("/orders")
    HttpEntity<Order> createOrder(@RequestBody Order order) {

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

}