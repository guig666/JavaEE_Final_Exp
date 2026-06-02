package com.konsonx.controller.manage.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.konsonx.po.Order;
import com.konsonx.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping(value = "/api/manage")
@Controller
public class OrderAPI {
    @Resource(name = "OrderService")
    private OrderService orderService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Integer pageSize = 14;

    @GetMapping("/orders")
    @ResponseBody
    public ObjectNode getOrders(@RequestParam("pageNum") Integer pageNum,
            @RequestParam(value = "order_user_id", required = false) Integer userId) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (pageNum != null && pageNum > 0) {
            List<Order> list = null;
            if (userId != null && userId > 0) {
                list = orderService.selectByOrder_lent_location_idAndOrder_revert_location_idAndOrder_user_id(null,
                        null, userId, pageNum, pageSize);
            } else {
                list = orderService.selectByPage(pageNum, pageSize);
            }
            code = 1;
            msg = "查询成功。";
            data = objectMapper.valueToTree(list);
        } else {
            code = -1;
            msg = "参数有误";
            data = null;
        }

        resultJsonObject.put("code", code);
        resultJsonObject.put("msg", msg);
        resultJsonObject.set("data", objectMapper.valueToTree(data));
        return resultJsonObject;
    }

    @GetMapping("/orders/{order_id}")
    @ResponseBody
    public ObjectNode getOrders(@PathVariable("order_id") Integer orderId) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (orderId != null && orderId > 0) {
            Order order = orderService.selectById(orderId);
            if (order != null) {
                code = 1;
                msg = "查询成功。";
                ArrayNode jsonArray = objectMapper.createArrayNode();
                jsonArray.add(objectMapper.valueToTree(order));
                data = jsonArray;
            } else {
                code = 0;
                msg = "不存在订单。";
                data = null;
            }
        } else {
            code = -1;
            msg = "参数有误";
            data = null;
        }

        resultJsonObject.put("code", code);
        resultJsonObject.put("msg", msg);
        resultJsonObject.set("data", objectMapper.valueToTree(data));
        return resultJsonObject;
    }
}