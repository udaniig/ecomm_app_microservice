package com.udanicode.orderservice.service;

import com.udanicode.orderservice.dto.InventoryResponse;
import com.udanicode.orderservice.dto.OrderLIneItemsDto;
import com.udanicode.orderservice.dto.OrderRequest;
import com.udanicode.orderservice.event.OrderPlacedEvent;
import com.udanicode.orderservice.model.Order;
import com.udanicode.orderservice.model.OrderLineItem;
import com.udanicode.orderservice.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;
    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;
    public void createOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> orderLineItems =   orderRequest.getOrderLIneItemsDtoList()
                .stream()
                .map(orderLineItemDto->mapDtoToOrderLineItem(orderLineItemDto))
                .toList();
        order.setOrderLineItemsList(orderLineItems);

        //get the skucode list
        List<String> skuCodes = order.getOrderLineItemsList()
                .stream()
                .map(OrderLineItem::getSkuCode)
                .toList();
        //check if the stock is available
        //call inventory service
        //http://inventory-service/api/inventory?skuCode=iPhone13&skuCode=iPhone14
        InventoryResponse[] inventoryResponses = webClient.get()
                .uri("http://inventory-service/api/inventory" ,uriBuilder ->
                        uriBuilder.queryParam("skuCodes",skuCodes).build())
                .retrieve()
                                .bodyToMono(InventoryResponse[].class)
                                        .block();

        Boolean result = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::getIsInStock);
        if (result){
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
            orderRepository.save(order);
        }else{
            throw new IllegalArgumentException("Product is not in stock");
        }

    }

    private OrderLineItem mapDtoToOrderLineItem(OrderLIneItemsDto orderLIneItemsDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(orderLIneItemsDto.getPrice());
        orderLineItem.setQuantity(orderLIneItemsDto.getQuantity());
        orderLineItem.setSkuCode(orderLineItem.getSkuCode());
        return orderLineItem;
    }
}
