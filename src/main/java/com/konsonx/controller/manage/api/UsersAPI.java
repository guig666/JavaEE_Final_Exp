package com.konsonx.controller.manage.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.konsonx.po.User;
import com.konsonx.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping(value = "/api/manage/")
@Controller
public class UsersAPI {
    @Resource(name = "UserService")
    private UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Integer pageSize = 14;

    @GetMapping("/users")
    @ResponseBody
    public ObjectNode getUserList(@RequestParam("pageNum") Integer pageNum,
            @RequestParam(name = "userPhone", required = false) String userPhone,
            @RequestParam(name = "userAlias", required = false) String userAlias) {

        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (pageNum != null) {

            if (userPhone != null && !"".equals(userPhone)) {
                return getUserListByPhone(userPhone, pageNum);
            } else if (userAlias != null && !"".equals(userAlias)) {
                return getUserListByAlias(userAlias, pageNum);
            }
            code = 1;
            List<User> userList = userService.selectByPage(pageNum, pageSize);
            data = objectMapper.valueToTree(userList);
        } else {
            code = 0;
            msg = "获取失败。";
            data = null;
        }
        resultJsonObject.put("code", code);
        resultJsonObject.put("msg", msg);
        if (data != null) {
            resultJsonObject.set("data", objectMapper.valueToTree(data));
        } else {
            resultJsonObject.putNull("data");
        }
        return resultJsonObject;
    }

    @GetMapping("/users/phone/{userPhone}")
    @ResponseBody
    public ObjectNode getUserListByPhone(@PathVariable(name = "userPhone") String userPhone,
            @RequestParam(name = "pageNum", required = false) Integer pageNum) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (userPhone != null && userPhone != "") {
            if (pageNum != null) {
                code = 1;
                List<User> userList = userService.selectByPhone(userPhone, pageNum, pageSize);
                data = objectMapper.valueToTree(userList);
            } else {
                code = 1;
                User user = userService.selectByPhone(userPhone);
                user.setUser_password("");
                ArrayNode jsonArray = objectMapper.createArrayNode();
                jsonArray.add(objectMapper.valueToTree(user));
                data = jsonArray;
            }
        } else {
            code = 0;
            msg = "获取失败。";
            data = null;
        }
        resultJsonObject.put("code", code);
        resultJsonObject.put("msg", msg);
        if (data != null) {
            resultJsonObject.set("data", objectMapper.valueToTree(data));
        } else {
            resultJsonObject.putNull("data");
        }
        return resultJsonObject;
    }

    @GetMapping("/users/alias/{userAlias}")
    @ResponseBody
    public ObjectNode getUserListByAlias(@PathVariable(name = "userAlias") String userAlias,
            @RequestParam(name = "pageNum", required = false) Integer pageNum) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (userAlias != null && userAlias != "") {
            if (pageNum != null) {
                code = 1;
                List<User> userList = userService.selectByAlias(userAlias, pageNum, pageSize);
                data = objectMapper.valueToTree(userList);
            } else {
                code = 1;
                List<User> userList = userService.selectByAlias(userAlias, 1, pageSize);
                data = objectMapper.valueToTree(userList);
            }
        } else {
            code = 0;
            msg = "获取失败。";
            data = null;
        }
        resultJsonObject.put("code", code);
        resultJsonObject.put("msg", msg);
        if (data != null) {
            resultJsonObject.set("data", objectMapper.valueToTree(data));
        } else {
            resultJsonObject.putNull("data");
        }
        return resultJsonObject;
    }

    @GetMapping("/users/{userId}")
    @ResponseBody
    public ObjectNode getUser(@PathVariable("userId") Integer userId) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (userId != null) {
            User user = userService.selectById(userId);
            if (user != null) {
                code = 1;
                msg = "获取成功";
                ArrayNode jsonArray = objectMapper.createArrayNode();
                jsonArray.add(objectMapper.valueToTree(user));
                data = jsonArray;
            } else {
                code = 0;
                msg = "不存在此用户。";
                data = null;
            }
        } else {
            code = 0;
            msg = "获取失败。";
            data = null;
        }
        resultJsonObject.put("code", code);
        resultJsonObject.put("msg", msg);
        if (data != null) {
            resultJsonObject.set("data", objectMapper.valueToTree(data));
        } else {
            resultJsonObject.putNull("data");
        }
        return resultJsonObject;
    }

    @PostMapping("/users/{userId}/balance")
    @ResponseBody
    public ObjectNode recharge(@PathVariable("userId") Integer userId, @RequestParam("money") Float money) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (userId != null && money != null) {
            User user = userService.selectById(userId);
            if (user != null) {
                user.setUser_balance(user.getUser_balance() + money);
                if (userService.update(user)) {
                    code = 1;
                    msg = "操作成功。";
                    user = userService.selectById(userId);
                    user.setUser_password("");
                    try {
                        data = objectMapper.writeValueAsString(user);
                    } catch (Exception e) {
                        data = user.toString();
                    }
                } else {
                    code = 0;
                    msg = "操作失败。";
                    data = null;
                }
            } else {
                code = 0;
                msg = "不存在此用户。";
                data = null;
            }
        } else {
            code = 0;
            msg = "参数有误，充值失败。";
            data = null;
        }
        resultJsonObject.put("code", code);
        resultJsonObject.put("msg", msg);
        if (data != null) {
            resultJsonObject.put("data", data.toString());
        } else {
            resultJsonObject.put("data", "null");
        }
        return resultJsonObject;
    }

    @PostMapping("/users/{userId}/phone")
    @ResponseBody
    public ObjectNode resetPhone(@PathVariable("userId") Integer userId, @RequestParam("phone") String phone) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (userId != null && phone != null && phone != "") {
            User user = userService.selectById(userId);
            if (user != null) {
                user.setUser_phone(phone);
                if (userService.update(user)) {
                    code = 1;
                    msg = "操作成功。";
                    user = userService.selectById(userId);
                    user.setUser_password("");
                    try {
                        data = objectMapper.writeValueAsString(user);
                    } catch (Exception e) {
                        data = user.toString();
                    }
                } else {
                    code = 0;
                    msg = "操作失败。";
                    data = null;
                }
            } else {
                code = 0;
                msg = "不存在此用户。";
                data = null;
            }
        } else {
            code = 0;
            msg = "参数有误，操作失败。";
            data = null;
        }
        resultJsonObject.put("code", code);
        resultJsonObject.put("msg", msg);
        if (data != null) {
            resultJsonObject.put("data", data.toString());
        } else {
            resultJsonObject.put("data", "null");
        }
        return resultJsonObject;
    }

    @DeleteMapping("/users/{userId}")
    @ResponseBody
    public ObjectNode delete(@PathVariable("userId") Integer userId) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (userId != null) {
            User user = userService.selectById(userId);
            if (user != null) {
                if (userService.delete(userId)) {
                    code = 1;
                    msg = "操作成功。";
                    data = null;
                } else {
                    code = 0;
                    msg = "操作失败。";
                    data = null;
                }
            } else {
                code = 0;
                msg = "不存在此用户。";
                data = null;
            }
        } else {
            code = 0;
            msg = "参数有误，充值失败。";
            data = null;
        }
        resultJsonObject.put("code", code);
        resultJsonObject.put("msg", msg);
        if (data != null) {
            resultJsonObject.put("data", data.toString());
        } else {
            resultJsonObject.put("data", "null");
        }
        return resultJsonObject;
    }
}