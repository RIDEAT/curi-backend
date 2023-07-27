package com.backend.curi.order;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;


@RestController

@RequestMapping("/orders")

public class OrderController {
    Logger log = LoggerFactory.getLogger("com.terry.logging.controller.OrderController");

    @RequestMapping(value="/{id}",method= RequestMethod.GET)

    public Order getOrder(@PathVariable int id,

                          @RequestHeader(value="userid") String userid) {

        MDC.put("userId", userid);

        MDC.put("ordierId",Integer.toString(id));

        Order order = queryOrder(id,userid);



        log.info("Get Order");

        MDC.clear();

        return order;



    }



    Order queryOrder(int id,String userid) {

        String name = "laptop";

        Order order = new Order(id,name);

        order.setUser(userid);

        order.setPricePerItem(100);

        order.setQuantity(1);

        order.setTotalPrice(100);


        log.info("product name:"+name);

        return order;

    }

}
