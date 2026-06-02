package com.konsonx.controller.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.konsonx.po.User;
import com.konsonx.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RequestMapping(value = "/api/client")
@Controller
public class ClientUsersAPI {
    @Resource(name = "UserService")
    private UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Integer pageSize = 14;

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
                user.setUser_password("");
                try {
                    jsonArray.add(objectMapper.valueToTree(user));
                } catch (Exception e) {
                    jsonArray.add(user.toString());
                }
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

    @RequestMapping("/users/phone/{userPhone}/actions/login")
    @ResponseBody
    public ObjectNode login(@PathVariable("userPhone") String userPhone, @RequestParam("userPassword") String userPassword, HttpSession session) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (userPhone != null && !"".equals(userPhone) && userPassword != null && !"".equals(userPassword)) {
            User user = userService.selectByPhone(userPhone);
            if (user != null) {
                if (user.getUser_password().equals(userPassword)) {
                    session.setAttribute("user", user);
                    System.out.println(session.getAttribute("user"));
                    code = 1;
                    msg = "登录获取成功";
                    ArrayNode jsonArray = objectMapper.createArrayNode();
                    user.setUser_password("");
                    try {
                        jsonArray.add(objectMapper.valueToTree(user));
                    } catch (Exception e) {
                        jsonArray.add(user.toString());
                    }
                    data = jsonArray;
                } else {
                    code = 0;
                    msg = "登录获取失败";
                    data = null;
                }

            } else {
                code = 0;
                msg = "不存在此用户。";
                data = null;
            }
        } else {
            code = -1;
            msg = "参数有误。";
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

    @RequestMapping("/users/{userId}/actions/logout")
    @ResponseBody
    public ObjectNode logout(@PathVariable("userId") Integer userId, HttpSession session) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        User sessionUser = null;
        Object object = session.getAttribute("user");
        System.out.println(session.getAttribute("user"));
        if (object != null) {
            sessionUser = (User) object;
            if (userId != null && userId > 0) {
                User user = userService.selectById(userId);
                if (user != null) {
                    if (user.getUser_id().equals(sessionUser.getUser_id())) {
                        session.removeAttribute("user");
                        code = 1;
                        msg = "退出成功";
                        data = null;
                    } else {
                        code = 0;
                        msg = "退出失败";
                        data = null;
                    }
                } else {
                    code = 0;
                    msg = "不存在此用户。";
                    data = null;
                }
            } else {
                code = -1;
                msg = "参数有误。";
                data = null;
            }
        } else {
            code = 0;
            msg = "尚未登录。";
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
        if (userId != null && money != null && money > 0) {
            User user = userService.selectById(userId);
            if (user != null) {
                user.setUser_balance(user.getUser_balance() + money);
                if (userService.update(user)) {
                    code = 1;
                    msg = "操作成功。";
                    user = userService.selectById(userId);
                    user.setUser_password("");
                    ArrayNode jsonArray = objectMapper.createArrayNode();
                    try {
                        jsonArray.add(objectMapper.valueToTree(user));
                    } catch (Exception e) {
                        jsonArray.add(user.toString());
                    }
                    data = jsonArray;
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
            resultJsonObject.set("data", objectMapper.valueToTree(data));
        } else {
            resultJsonObject.putNull("data");
        }
        return resultJsonObject;
    }

    @PostMapping("/users/{userId}/phone")
    @ResponseBody
    public ObjectNode resetPhone(@PathVariable("userId") Integer userId, @RequestParam("phone") String phone, HttpSession session) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (userId != null && phone != null && phone != "") {
            User user = userService.selectById(userId);
            Object object = session.getAttribute("user");
            if (object == null || !((User) object).getUser_id().equals(userId)) {
                code = -1;
                msg = "请先登录.";
                data = null;
            } else {
                if (user != null) {
                    if (userService.selectByPhone(phone) == null) {
                        user.setUser_phone(phone);
                        if (userService.update(user)) {
                            code = 1;
                            msg = "操作成功。";
                            user = userService.selectById(userId);
                            ArrayNode jsonArray = objectMapper.createArrayNode();
                            user.setUser_password("");
                            try {
                                jsonArray.add(objectMapper.valueToTree(user));
                            } catch (Exception e) {
                                jsonArray.add(user.toString());
                            }
                            data = jsonArray;
                        } else {
                            code = 0;
                            msg = "操作失败。";
                            data = null;
                        }
                    } else {
                        code = 0;
                        msg = "手机号码已注册。";
                        data = null;
                    }
                } else {
                    code = 0;
                    msg = "不存在此用户。";
                    data = null;
                }
            }
        } else {
            code = 0;
            msg = "参数有误，操作失败。";
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

    @PostMapping("/users/{userId}/password")
    @ResponseBody
    public ObjectNode resetPassowrd(@PathVariable("userId") Integer userId, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (userId != null && oldPassword != null && !"".equals(oldPassword) && newPassword != null && !"".equals(newPassword)) {
            User user = userService.selectById(userId);
            if (user != null && user.getUser_password().equals(oldPassword)) {
                user.setUser_password(newPassword);
                if (userService.update(user)) {
                    code = 1;
                    msg = "操作成功。";
                    user = userService.selectById(userId);
                    ArrayNode jsonArray = objectMapper.createArrayNode();
                    user.setUser_password("");
                    try {
                        jsonArray.add(objectMapper.valueToTree(user));
                    } catch (Exception e) {
                        jsonArray.add(user.toString());
                    }
                    data = jsonArray;
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
            resultJsonObject.set("data", objectMapper.valueToTree(data));
        } else {
            resultJsonObject.putNull("data");
        }
        return resultJsonObject;
    }

    @PostMapping("/users/{userId}/alias")
    @ResponseBody
    public ObjectNode resetAlias(@PathVariable("userId") Integer userId, @RequestParam("userAlias") String userAlias, HttpSession session) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (userId != null && userAlias != null && !"".equals(userAlias)) {
            User user = userService.selectById(userId);
            Object object = session.getAttribute("user");
            if (object == null || !((User) object).getUser_id().equals(userId)) {
                code = -1;
                msg = "请先登录.";
                data = null;
            } else {
                if (user != null) {
                    user.setUser_alias(userAlias);
                    if (userService.update(user)) {
                        code = 1;
                        msg = "操作成功。";
                        user = userService.selectById(userId);
                        ArrayNode jsonArray = objectMapper.createArrayNode();
                        user.setUser_password("");
                        try {
                            jsonArray.add(objectMapper.valueToTree(user));
                        } catch (Exception e) {
                            jsonArray.add(user.toString());
                        }
                        data = jsonArray;
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
            }
        } else {
            code = 0;
            msg = "参数有误，操作失败。";
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

    @PutMapping("/users")
    @ResponseBody
    public ObjectNode insertUser(@RequestParam("userPhone") String userPhone, @RequestParam("userPassword") String userPassword, @RequestParam("userAlias") String userAlias) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;

        if (userPhone != null && !"".equals(userPhone) && userPassword != null && !"".equals(userPassword) && userAlias != null && !"".equals(userAlias)) {
            User user = new User();
            user.setUser_password(userPassword);
            user.setUser_alias(userAlias);
            user.setUser_phone(userPhone);
            user.setUser_balance(0.f);
            if (userService.selectByPhone(userPhone) != null) {
                code = 0;
                msg = "号码已注册。";
                data = null;
            } else {
                if (userService.insert(user)) {
                    code = 1;
                    msg = "操作成功,请登录。";
                    data = null;
                } else {
                    code = 0;
                    msg = "操作失败。";
                    data = null;
                }
            }
        } else {
            code = 0;
            msg = "参数有误，操作失败。";
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
}