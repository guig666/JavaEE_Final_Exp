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

/**
 * =====================================================
 * 管理员账号管理控制器
 * =====================================================
 * 功能说明：
 * 1. 管理员登录：验证账号密码，登录成功后保存Session
 * 2. 管理员登出：清除Session中的管理员信息
 * 3. 管理员列表：分页查询所有管理员
 * 4. 管理员搜索：按账号模糊查询
 * 5. 管理员详情：按ID查询单个管理员
 * 6. 管理员删除：按ID删除管理员
 * 7. 管理员注册：添加新管理员（需检查账号唯一性）
 * 8. 密码修改：修改管理员密码（需验证原密码）
 * =====================================================
 */
@RequestMapping(value = "/api/manage")
@Controller
public class AdministratorsAPI {
    @Resource(name = "AdminService")
    private AdminService adminService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Integer pageSize = 14;

    /**
     * =====================================================
     * 管理员登录接口
     * =====================================================
     * 接口路径：GET /api/manage/administrators/account/{account}/actions/login
     * 参数说明：
     *   - account：管理员账号（路径参数）
     *   - password：管理员密码（请求参数）
     *
     * 核心逻辑：
     * 1. 验证参数是否为空
     * 2. 调用adminService.login()验证账号密码
     * 3. 登录成功后将管理员对象存入HttpSession
     * 4. 返回JSON响应（包含状态码、消息、重定向URL）
     *
     * @param account  管理员账号
     * @param password 管理员密码
     * @param session  HttpSession对象，用于存储登录状态
     * @return JSON响应，包含登录结果和重定向地址
     */
    @RequestMapping("/administrators/account/{account}/actions/login")
    @ResponseBody
    public ObjectNode login(@PathVariable("account") String account, @RequestParam("password") String password,
                            HttpSession session) {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        int code = 0;
        ObjectNode data = objectMapper.createObjectNode();
        String msg = null;
        if (account != null && password != null) {
            // 调用Service层验证账号密码
            if (adminService.login(account, password)) {
                code = 1;
                data.put("redirect_url", "/manage/manage.action");
                msg = "登录成功。";
                // 登录成功后，将管理员对象存入Session，用于后续请求的权限验证
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

    /**
     * =====================================================
     * 管理员登出接口（指定账号）
     * =====================================================
     * 接口路径：POST /api/manage/administrators/account/{account}/actions/logout
     *
     * 核心逻辑：
     * 1. 从Session中获取当前登录的管理员对象
     * 2. 验证Session中的账号与请求路径中的账号是否一致
     * 3. 验证通过后，清除Session中的管理员信息
     *
     * @param account 管理员账号（路径参数）
     * @param session HttpSession对象
     * @return JSON响应，包含登出结果
     */
    @RequestMapping("/administrators/account/{account}/actions/logout")
    @ResponseBody
    public ObjectNode logout(@PathVariable("account") String account, HttpSession session) {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        int code = 0;
        ObjectNode data = objectMapper.createObjectNode();
        String msg = null;
        if (account != null) {
            // 从Session中获取当前登录的管理员对象
            Object object = session.getAttribute("admin");
            if (object != null) {
                Admin onlineAdmin = (Admin) object;
                // 验证Session中的账号与请求账号是否一致，防止跨用户登出
                if (onlineAdmin.getAdmin_account().equals(account)) {
                    code = 1;
                    msg = "退出成功。";
                    data.put("redirect_url", "/manage/login.action");
                    // 清除Session中的管理员信息，实现登出
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

    /**
     * =====================================================
     * 管理员登出接口（通用）
     * =====================================================
     * 接口路径：POST /api/manage/administrators/actions/logout
     *
     * 核心逻辑：
     * 直接清除Session中的管理员信息，无需验证账号
     *
     * @param session HttpSession对象
     * @return JSON响应
     */
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
            // 清除Session中的管理员信息
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

    /**
     * =====================================================
     * 管理员密码修改接口
     * =====================================================
     * 接口路径：POST /api/manage/administrators/account/{account}/password
     * 参数说明：
     *   - account：管理员账号（路径参数）
     *   - oldPassword：原密码（请求参数）
     *   - newPassword：新密码（请求参数）
     *
     * 核心逻辑：
     * 1. 验证原密码是否正确（防止未授权修改）
     * 2. 更新管理员密码
     * 3. 返回结果
     *
     * @param account     管理员账号
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return JSON响应
     */
    @PostMapping("/administrators/account/{account}/password")
    @ResponseBody
    public ObjectNode resetPassword(@PathVariable("account") String account,
                                    @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        int code = -1;
        String msg = null;
        ObjectNode data = objectMapper.createObjectNode();
        if (account != null && oldPassword != null && newPassword != null) {
            // 首先验证原密码是否正确
            if (adminService.login(account, oldPassword)) {
                // 原密码正确，获取管理员对象并修改密码
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

    /**
     * =====================================================
     * 管理员列表查询接口（分页）
     * =====================================================
     * 接口路径：GET /api/manage/administrators
     * 参数说明：
     *   - pageNum：页码（必填，从1开始）
     *
     * 核心逻辑：
     * 1. 调用adminService.selectByPage()进行分页查询
     * 2. 返回管理员列表
     *
     * @param pageNum 页码
     * @return JSON响应，包含管理员列表
     */
    @GetMapping("/administrators")
    @ResponseBody
    public ObjectNode getAdministratorList(@RequestParam("pageNum") Integer pageNum) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (pageNum != null) {
            code = 1;
            // 调用Service层进行分页查询
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

    /**
     * =====================================================
     * 管理员搜索接口（按账号模糊查询）
     * =====================================================
     * 接口路径：GET /api/manage/administrators/account/{adminAccount}
     * 参数说明：
     *   - adminAccount：管理员账号（路径参数）
     *   - pageNum：页码（可选，不传则查询单个）
     *
     * 核心逻辑：
     * 1. 如果传了pageNum，进行分页模糊查询
     * 2. 如果没传pageNum，查询单个管理员详情
     *
     * @param adminAccount 管理员账号
     * @param pageNum      页码（可选）
     * @return JSON响应
     */
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
                // 有pageNum参数，进行分页模糊查询
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
                // 没有pageNum参数，查询单个管理员
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

    /**
     * =====================================================
     * 管理员详情查询接口（按ID）
     * =====================================================
     * 接口路径：GET /api/manage/administrators/{adminId}
     * 参数说明：
     *   - adminId：管理员ID（路径参数）
     *
     * 核心逻辑：
     * 1. 根据ID查询管理员详情
     * 2. 出于安全考虑，清除密码字段返回给前端
     *
     * @param adminId 管理员ID
     * @return JSON响应，包含管理员信息
     */
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
                // 安全考虑：清除密码字段再返回给前端
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

    /**
     * =====================================================
     * 管理员删除接口
     * =====================================================
     * 接口路径：DELETE /api/manage/administrators/{adminId}
     * 参数说明：
     *   - adminId：管理员ID（路径参数）
     *
     * 核心逻辑：
     * 1. 先查询管理员是否存在
     * 2. 存在则调用Service层删除
     *
     * @param adminId 管理员ID
     * @return JSON响应
     */
    @DeleteMapping("/administrators/{adminId}")
    @ResponseBody
    public ObjectNode deleteAdministrator(@PathVariable(name = "adminId") Integer adminId) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (adminId != null) {
            // 先查询管理员是否存在
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

    /**
     * =====================================================
     * 管理员注册接口（添加新管理员）
     * =====================================================
     * 接口路径：PUT /api/manage/administrators
     * 参数说明：
     *   - account：管理员账号（请求参数）
     *   - password：管理员密码（请求参数）
     *
     * 核心逻辑：
     * 1. 验证参数是否为空
     * 2. 检查账号是否已存在（保证唯一性）
     * 3. 不存在则创建新管理员
     *
     * @param account  管理员账号
     * @param password 管理员密码
     * @return JSON响应
     */
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
            // 先检查账号是否已存在
            if (adminService.selectByAccount(account) == null) {
                // 账号不存在，可以创建
                admin.setAdmin_account(account);
                admin.setAdmin_password(password);
                if (adminService.insert(admin)) {
                    code = 1;
                    msg = "操作成功！";
                    // 重新查询获取完整信息，清除密码后返回
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