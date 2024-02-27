package com.udanicode.orderservice.controller;

import com.udanicode.orderservice.dto.OrderRequest;
import com.udanicode.orderservice.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest){
        System.out.println("in OrderController");
        orderService.createOrder(orderRequest);
        return "Order Created";
    }
}
