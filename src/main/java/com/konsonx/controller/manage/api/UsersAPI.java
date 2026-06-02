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

/**
 * =====================================================
 * 用户账号管理控制器
 * =====================================================
 * 功能说明：
 * 1. 用户列表查询：分页查询所有用户
 * 2. 用户搜索：按手机号或昵称搜索
 * 3. 用户详情：按ID查询单个用户
 * 4. 用户充值：增加用户余额
 * 5. 手机号修改：修改用户手机号
 * 6. 用户删除：按ID删除用户
 *
 * 数据库表：user
 * 表结构：
 *   - user_id: 用户ID（主键，自增）
 *   - user_phone: 用户手机号（唯一）
 *   - user_alias: 用户昵称
 *   - user_password: 用户密码
 *   - user_balance: 用户余额（FLOAT类型）
 * =====================================================
 */
@RequestMapping(value = "/api/manage/")
@Controller
public class UsersAPI {
    @Resource(name = "UserService")
    private UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Integer pageSize = 14;

    /**
     * =====================================================
     * 用户列表查询接口（支持搜索）
     * =====================================================
     * 接口路径：GET /api/manage/users
     * 参数说明：
     *   - pageNum：页码（必填）
     *   - userPhone：用户手机号（可选，用于搜索）
     *   - userAlias：用户昵称（可选，用于搜索）
     *
     * 核心逻辑：
     * 1. 如果传了userPhone，调用按手机号搜索的方法
     * 2. 如果传了userAlias，调用按昵称搜索的方法
     * 3. 如果都没传，分页查询所有用户
     *
     * @param pageNum   页码
     * @param userPhone 用户手机号（可选）
     * @param userAlias 用户昵称（可选）
     * @return JSON响应
     */
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

            // 如果传了手机号，按手机号搜索
            if (userPhone != null && !"".equals(userPhone)) {
                return getUserListByPhone(userPhone, pageNum);
            } else if (userAlias != null && !"".equals(userAlias)) {
                // 如果传了昵称，按昵称搜索
                return getUserListByAlias(userAlias, pageNum);
            }
            code = 1;
            // 默认分页查询所有用户
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

    /**
     * =====================================================
     * 用户搜索接口（按手机号查询）
     * =====================================================
     * 接口路径：GET /api/manage/users/phone/{userPhone}
     * 参数说明：
     *   - userPhone：用户手机号（路径参数）
     *   - pageNum：页码（可选，不传则查单个）
     *
     * 核心逻辑：
     * 1. 如果传了pageNum，进行分页模糊查询
     * 2. 如果没传pageNum，查询单个用户的完整信息
     *
     * @param userPhone 用户手机号
     * @param pageNum  页码（可选）
     * @return JSON响应
     */
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
                // 有pageNum，进行分页模糊查询
                code = 1;
                List<User> userList = userService.selectByPhone(userPhone, pageNum, pageSize);
                data = objectMapper.valueToTree(userList);
            } else {
                // 没有pageNum，查询单个用户完整信息
                code = 1;
                User user = userService.selectByPhone(userPhone);
                // 安全考虑：清除密码字段
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

    /**
     * =====================================================
     * 用户搜索接口（按昵称查询）
     * =====================================================
     * 接口路径：GET /api/manage/users/alias/{userAlias}
     * 参数说明：
     *   - userAlias：用户昵称（路径参数）
     *   - pageNum：页码（可选，不传默认为1）
     *
     * 核心逻辑：
     * 进行分页模糊查询，搜索昵称包含指定字符串的用户
     *
     * @param userAlias 用户昵称
     * @param pageNum  页码（可选，默认1）
     * @return JSON响应
     */
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
                // 有pageNum，按指定页码查询
                code = 1;
                List<User> userList = userService.selectByAlias(userAlias, pageNum, pageSize);
                data = objectMapper.valueToTree(userList);
            } else {
                // 没有pageNum，默认第1页
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

    /**
     * =====================================================
     * 用户详情查询接口（按ID）
     * =====================================================
     * 接口路径：GET /api/manage/users/{userId}
     * 参数说明：
     *   - userId：用户ID（路径参数）
     *
     * 核心逻辑：
     * 根据用户ID查询用户详细信息
     *
     * @param userId 用户ID
     * @return JSON响应
     */
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

    /**
     * =====================================================
     * 用户充值接口
     * =====================================================
     * 接口路径：POST /api/manage/users/{userId}/balance
     * 参数说明：
     *   - userId：用户ID（路径参数）
     *   - money：充值金额（请求参数，正数）
     *
     * 核心逻辑：
     * 1. 验证参数（用户ID和金额不能为空，金额必须为正数）
     * 2. 查询用户是否存在
     * 3. 增加用户余额：user_balance = user_balance + money
     * 4. 更新用户信息
     *
     * @param userId 用户ID
     * @param money  充值金额
     * @return JSON响应
     */
    @PostMapping("/users/{userId}/balance")
    @ResponseBody
    public ObjectNode recharge(@PathVariable("userId") Integer userId, @RequestParam("money") Float money) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (userId != null && money != null) {
            // 先查询用户是否存在
            User user = userService.selectById(userId);
            if (user != null) {
                // 增加用户余额
                user.setUser_balance(user.getUser_balance() + money);
                // 更新用户信息
                if (userService.update(user)) {
                    code = 1;
                    msg = "操作成功。";
                    // 重新查询获取最新信息
                    user = userService.selectById(userId);
                    // 安全考虑：清除密码字段
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

    /**
     * =====================================================
     * 用户手机号修改接口
     * =====================================================
     * 接口路径：POST /api/manage/users/{userId}/phone
     * 参数说明：
     *   - userId：用户ID（路径参数）
     *   - phone：新手机号（请求参数）
     *
     * 核心逻辑：
     * 1. 验证参数
     * 2. 查询用户是否存在
     * 3. 更新用户手机号
     *
     * @param userId 用户ID
     * @param phone  新手机号
     * @return JSON响应
     */
    @PostMapping("/users/{userId}/phone")
    @ResponseBody
    public ObjectNode resetPhone(@PathVariable("userId") Integer userId, @RequestParam("phone") String phone) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (userId != null && phone != null && phone != "") {
            // 查询用户是否存在
            User user = userService.selectById(userId);
            if (user != null) {
                // 更新手机号
                user.setUser_phone(phone);
                if (userService.update(user)) {
                    code = 1;
                    msg = "操作成功。";
                    // 重新查询获取最新信息
                    user = userService.selectById(userId);
                    // 安全考虑：清除密码字段
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

    /**
     * =====================================================
     * 用户删除接口
     * =====================================================
     * 接口路径：DELETE /api/manage/users/{userId}
     * 参数说明：
     *   - userId：用户ID（路径参数）
     *
     * 核心逻辑：
     * 1. 验证参数
     * 2. 查询用户是否存在
     * 3. 删除用户
     *
     * @param userId 用户ID
     * @return JSON响应
     */
    @DeleteMapping("/users/{userId}")
    @ResponseBody
    public ObjectNode delete(@PathVariable("userId") Integer userId) {
        ObjectNode resultJsonObject = objectMapper.createObjectNode();
        int code = 0;
        String msg = null;
        Object data = null;
        if (userId != null) {
            // 查询用户是否存在
            User user = userService.selectById(userId);
            if (user != null) {
                // 删除用户
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