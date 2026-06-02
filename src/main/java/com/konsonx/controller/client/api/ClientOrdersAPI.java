package com.konsonx.controller.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.konsonx.po.Location;
import com.konsonx.po.Order;
import com.konsonx.po.User;
import com.konsonx.service.LocationService;
import com.konsonx.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@RequestMapping(value = "/api/client")
@Controller
public class ClientOrdersAPI {
    @Resource(name = "OrderService")
    private OrderService orderService;
    @Resource(name = "LocationService")
    private LocationService locationService ;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Integer pageSize = 14;


    @GetMapping("/orders")
    @ResponseBody
    public ObjectNode getOrders(HttpSession session){
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;

        Integer userId = null;
        Object data = null;
        Object object = session.getAttribute("user");
        User user = null;
        if (object != null)user = (User)object;
        if (user != null) userId = user.getUser_id();

        if (userId != null && userId >0){
            List<Order> orders = orderService.selectByOrder_lent_location_idAndOrder_revert_location_idAndOrder_user_id(null,null,userId,1,0);
            if (orders != null){
                code = 1;
                msg = "订单获取成功。";
                data = objectMapper.valueToTree(orders);
            }else {
                code = 0;
                msg = "订单获取失败。";
                data = null;
            }
        }else{
            code = -1;
            msg = "参数有误，订单获取失败。";
            data = null;
        }

        resultJsonObject.put("code",code);
        resultJsonObject.put("msg",msg);
        if (data != null) {
            resultJsonObject.set("data", objectMapper.valueToTree(data));
        } else {
            resultJsonObject.putNull("data");
        }
        return resultJsonObject;
    }

    @GetMapping("/orders/{orderId}")
    @ResponseBody
    public ObjectNode getOrder(@PathVariable("orderId")Integer orderId, HttpSession session){
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;

        Integer userId = null;
        Object data = null;
        Object object = session.getAttribute("user");
        User user = null;
        if (object != null)user = (User)object;
        if (user != null) userId = user.getUser_id();

        if (userId != null && userId >0 && orderId != null && orderId > 0){
            Order order = orderService.selectById(orderId);
            if (order != null && userId.equals(order.getOrder_user_id())){
                code = 1;
                msg = "订单获取成功。";
                ArrayNode jsonArray = objectMapper.createArrayNode();
                jsonArray.add(objectMapper.valueToTree(order));
                data = jsonArray;
            }else {
                code = 0;
                msg = "订单获取失败。";
                data = null;
            }
        }else{
            code = -1;
            msg = "参数有误，订单获取失败。";
            data = null;
        }

        resultJsonObject.put("code",code);
        resultJsonObject.put("msg",msg);
        if (data != null) {
            resultJsonObject.set("data", objectMapper.valueToTree(data));
        } else {
            resultJsonObject.putNull("data");
        }
        return resultJsonObject;
    }
    @PutMapping("/orders")
    @ResponseBody
    public ObjectNode insertOrder(@RequestParam("navLocationId")Integer navLocationId, HttpSession session){
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;

        Integer userId = null;
        Object data = null;
        Object object = session.getAttribute("user");
        User user = null;
        Location location = null;
        Integer locationId = null;
        if (object != null)user = (User)object;
        if (user != null) userId = user.getUser_id();
        if (navLocationId != null && navLocationId > 0){
            location = locationService.selectById(navLocationId);
            if (location != null){
                locationId = location.getLocation_id();
            }
        }
        if (userId != null && userId >0 && locationId != null && locationId > 0){
            try {
               if (orderService.borrow(userId,locationId)){
                   code = 1;
                   msg = "成功租借充电宝。";
                   data = null;
               }else{
                   code = 0;
                   msg = "租借充电宝失败。";
                   data = null;
               }
            } catch (Exception e) {
                e.printStackTrace();
                code = 0;
                msg = e.getMessage();
                data = null;
            }
        }else{
            code = -1;
            msg = "参数有误，订单生成失败。";
            data = null;
        }

        resultJsonObject.put("code",code);
        resultJsonObject.put("msg",msg);
        if (data != null) {
            resultJsonObject.set("data", objectMapper.valueToTree(data));
        } else {
            resultJsonObject.putNull("data");
        }
        return resultJsonObject;
    }

    @PostMapping("/orders/{orderId}")
    @ResponseBody
    public ObjectNode finishOrder(@PathVariable("orderId")Integer orderId, HttpSession session,@RequestParam("navLocationId")Integer navLocationId){
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;

        Integer userId = null;
        Object data = null;
        Object object = session.getAttribute("user");
        User user = null;
        if (object != null)user = (User)object;
        if (user != null) userId = user.getUser_id();

        if (userId != null && userId >0 && orderId != null && orderId > 0 && navLocationId!= null && navLocationId>0){
            Location location = locationService.selectById(navLocationId);
            if (location != null){
                try {
                    if (orderService.revert(orderId,location.getLocation_id())) {
                        code = 1;
                        msg = "订单完成。";
                        data = null;
                    }else{
                        code = -1;
                        msg = "参数有误，归还失败。";
                        data = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    code = -1;
                    msg = "参数有误，归还失败。 error:" + e.getMessage();
                    data = null;
                }
            }else{
                code = -1;
                msg = "参数有误，归还失败。";
                data = null;
            }

        }else{
            code = -1;
            msg = "参数有误，归还失败。";
            data = null;
        }

        resultJsonObject.put("code",code);
        resultJsonObject.put("msg",msg);
        if (data != null) {
            resultJsonObject.set("data", objectMapper.valueToTree(data));
        } else {
            resultJsonObject.putNull("data");
        }
        return resultJsonObject;
    }
}