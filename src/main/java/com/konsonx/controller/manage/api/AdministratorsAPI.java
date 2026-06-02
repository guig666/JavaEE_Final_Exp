package com.konsonx.controller.manage.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.konsonx.po.Admin;
import com.konsonx.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@RequestMapping(value = "/api/manage")
@Controller
public class AdministratorsAPI {
    @Resource(name = "AdminService")
    private AdminService adminService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Integer pageSize = 14;

    @RequestMapping("/administrators/account/{account}/actions/login")
    @ResponseBody
    public ObjectNode login(@PathVariable("account") String account, @RequestParam("password") String password,
            HttpSession session) {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        int code = 0;
        ObjectNode data = objectMapper.createObjectNode();
        String msg = null;
        if (account != null && password != null) {
            if (adminService.login(account, password)) {
                code = 1;
                data.put("redirect_url", "/manage/manage.action");
                msg = "登录成功。";
                session.setAttribute("admin", adminService.selectByAccount(account));
            } else {
                code = 0;
                data.put("redirect_url", "/manage/login.action");
                msg = "密码错误，登录失败。";
            }
        } else {
            code = -1;
            data.put("redirect_url", "/manage/login.action");
            msg = "参数有误，登陆失败";
        }
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        jsonObject.set("data", data);
        return jsonObject;
    }

    @RequestMapping("/administrators/account/{account}/actions/logout")
    @ResponseBody
    public ObjectNode logout(@PathVariable("account") String account, HttpSession session) {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        int code = 0;
        ObjectNode data = objectMapper.createObjectNode();
        String msg = null;
        if (account != null) {
            Object object = session.getAttribute("admin");
            if (object != null) {
                Admin onlineAdmin = (Admin) object;
                if (onlineAdmin.getAdmin_account().equals(account)) {
                    code = 1;
                    msg = "退出成功。";
                    data.put("redirect_url", "/manage/login.action");
                    session.removeAttribute("admin");
                } else {
                    code = 0;
                    msg = "退出失败。";
                    data.put("redirect_url", "/manage/login.action");
                }
            } else {
                code = -1;
                msg = "尚未登录。";
                data.put("redirect_url", "/manage/login.action");
            }
        } else {
            code = -1;
            data.put("redirect_url", "/manage/login.action");
            msg = "参数有误，登陆失败";
        }
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        jsonObject.set("data", data);
        return jsonObject;
    }

    @RequestMapping("/administrators/actions/logout")
    @ResponseBody
    public ObjectNode logout(HttpSession session) {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        int code = 0;
        ObjectNode data = objectMapper.createObjectNode();
        String msg = null;

        Object object = session.getAttribute("admin");
        if (object != null) {
            Admin onlineAdmin = (Admin) object;
            session.removeAttribute("admin");
            code = 1;
            msg = "退出成功。";
            data.put("redirect_url", "/manage/login.action");
        } else {
            code = 0;
            msg = "未登录";
        }

        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        jsonObject.set("data", data);
        return jsonObject;
    }

    @PostMapping("/administrators/account/{account}/password")
    @ResponseBody
    public ObjectNode resetPassword(@PathVariable("account") String account,
            @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        int code = -1;
        String msg = null;
        ObjectNode data = objectMapper.createObjectNode();
        if (account != null && oldPassword != null && newPassword != null) {
            if (adminService.login(account, oldPassword)) {
                Admin admin = adminService.selectByAccount(account);
                admin.setAdmin_password(newPassword);
                if (adminService.update(admin)) {
                    code = 1;
                    msg = "密码修改成功。";
                    data.put("redirect_url", "/manage/login.action");
                } else {
                    code = 0;
                    msg = "未知错误，更改失败。";
                    data.put("redirect_url", "/manage/login.action");
                }
            } else {
                code = 0;
                msg = "密码错误，更改失败。";
                data.put("redirect_url", "/manage/login.action");
            }
        } else {
            code = -1;
            msg = "参数有误，更改失败。";
            data.put("redirect_url", "/manage/login.action");
        }

        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        jsonObject.set("data", data);
        return jsonObject;
    }

    @GetMapping("/administrators")
    @ResponseBody
    public ObjectNode getAdministratorList(@RequestParam("pageNum") Integer pageNum) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (pageNum != null) {
            code = 1;
            List<Admin> list = adminService.selectByPage(pageNum, pageSize);
            data = objectMapper.valueToTree(list);
        } else {
            code = 0;
            msg = "获取失败。";
            data = null;
        }
        resultJsonObject.put("code", code);
        resultJsonObject.put("msg", msg);
        resultJsonObject.set("data", objectMapper.valueToTree(data));
        return resultJsonObject;
    }

    @GetMapping("/administrators/account/{adminAccount}")
    @ResponseBody
    public ObjectNode getAdministratorList(@PathVariable(name = "adminAccount") String adminAccount,
            @RequestParam(name = "pageNum", required = false) Integer pageNum) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (adminAccount != null && !"".equals(adminAccount)) {
            if (pageNum != null) {
                List<Admin> list = adminService.selectByAccount(adminAccount, pageNum, pageSize);
                if (list != null && list.size() > 0) {
                    code = 1;
                    data = objectMapper.valueToTree(list);
                    msg = "获取成功。";
                } else {
                    code = 0;
                    data = null;
                    msg = "没有找到相应账号。";
                }

            } else {
                Admin admin = adminService.selectByAccount(adminAccount);
                if (admin != null) {
                    code = 1;
                    msg = "获取成功。";
                    ArrayNode jsonArray = objectMapper.createArrayNode();
                    jsonArray.add(objectMapper.valueToTree(admin));
                    data = jsonArray;
                } else {
                    code = 0;
                    msg = "获取失败。";
                    data = null;
                }
            }
        } else {
            code = 0;
            msg = "获取失败。";
            data = null;
        }

        resultJsonObject.put("code", code);
        resultJsonObject.put("msg", msg);
        resultJsonObject.set("data", objectMapper.valueToTree(data));
        return resultJsonObject;
    }

    @GetMapping("/administrators/{adminId}")
    @ResponseBody
    public ObjectNode getAdministrator(@PathVariable(name = "adminId") Integer adminId) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (adminId != null) {
            Admin admin = adminService.selectById(adminId);
            if (admin != null) {
                code = 1;
                msg = "获取成功。";
                admin.setAdmin_password("");
                ArrayNode jsonArray = objectMapper.createArrayNode();
                jsonArray.add(objectMapper.valueToTree(admin));
                data = jsonArray;
            } else {
                code = 0;
                msg = "没有找到账号。";
                data = null;
            }
        } else {
            code = 0;
            msg = "获取失败。";
            data = null;
        }
        resultJsonObject.put("code", code);
        resultJsonObject.put("msg", msg);
        resultJsonObject.set("data", objectMapper.valueToTree(data));
        return resultJsonObject;
    }

    @DeleteMapping("/administrators/{adminId}")
    @ResponseBody
    public ObjectNode deleteAdministrator(@PathVariable(name = "adminId") Integer adminId) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (adminId != null) {
            Admin admin = adminService.selectById(adminId);
            if (admin != null) {
                if (adminService.delete(adminId)) {
                    code = 1;
                    msg = "删除成功。";
                    data = null;
                } else {
                    code = 0;
                    msg = "删除失败。";
                }
            } else {
                code = 0;
                msg = "没有找到账号。";
                data = null;
            }
        } else {
            code = 0;
            msg = "参数有误。";
            data = null;
        }
        resultJsonObject.put("code", code);
        resultJsonObject.put("msg", msg);
        resultJsonObject.set("data", objectMapper.valueToTree(data));
        return resultJsonObject;
    }

    @PutMapping("/administrators")
    @ResponseBody
    public ObjectNode putAdministrator(@RequestParam("adminAccount") String account,
            @RequestParam("adminPassword") String password) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;

        if (account != null && !"".equals(account) && password != null && !"".equals(password)) {
            Admin admin = new Admin();
            if (adminService.selectByAccount(account) == null) {
                admin.setAdmin_account(account);
                admin.setAdmin_password(password);
                if (adminService.insert(admin)) {
                    code = 1;
                    msg = "操作成功！";
                    admin = adminService.selectByAccount(account);
                    admin.setAdmin_password("");
                    ArrayNode jsonArray = objectMapper.createArrayNode();
                    jsonArray.add(objectMapper.valueToTree(admin));
                    data = jsonArray;
                } else {
                    code = 0;
                    msg = "未知错误，操作失败。";
                    data = null;
                }
            } else {
                code = 0;
                msg = "账户已存在，操作失败。";
                data = null;
            }

        } else {
            code = -1;
            msg = "参数有误，操作失败。";
            data = null;
        }
        resultJsonObject.put("code", code);
        resultJsonObject.put("msg", msg);
        resultJsonObject.set("data", objectMapper.valueToTree(data));
        return resultJsonObject;
    }
}