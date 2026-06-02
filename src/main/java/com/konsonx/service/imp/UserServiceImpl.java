package com.konsonx.service.imp;

import com.github.pagehelper.PageHelper;
import com.konsonx.dao.UserMapper;
import com.konsonx.po.User;
import com.konsonx.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * =====================================================
 * 用户服务实现类
 * =====================================================
 * 功能说明：
 * 1. 实现UserService接口定义的所有方法
 * 2. 调用UserMapper完成数据库操作
 * 3. 使用PageHelper实现分页查询
 * 4. 提供用户余额充值/扣费业务逻辑
 * =====================================================
 */
@Service(value = "UserService")
public class UserServiceImpl implements UserService {

    @Resource(name = "UserMapper")
    private UserMapper userMapper;

    /**
     * =====================================================
     * 新增用户
     * =====================================================
     * 核心逻辑：
     * 1. 参数非空校验（用户对象为null则返回false）
     * 2. 调用Mapper的insertSelective方法（只插入非空字段）
     * 3. 根据影响行数判断操作是否成功
     *
     * @param user 用户对象
     * @return true-插入成功（影响行数>0），false-插入失败
     */
    public boolean insert(User user) {
        if (user == null) return false;
        return userMapper.insertSelective(user) > 0 ? true : false;
    }

    /**
     * =====================================================
     * 删除用户
     * =====================================================
     * 核心逻辑：
     * 1. 根据主键ID删除记录
     * 2. 根据影响行数判断操作是否成功
     *
     * @param userId 用户ID
     * @return true-删除成功（影响行数>0），false-删除失败
     */
    public boolean delete(Integer userId) {
        return userMapper.deleteByPrimaryKey(userId) > 0 ? true : false;
    }

    /**
     * =====================================================
     * 更新用户信息
     * =====================================================
     * 核心逻辑：
     * 1. 参数非空校验（用户对象为null则返回false）
     * 2. 调用Mapper的updateByPrimaryKeySelective方法
     * 3. 只更新非空字段，避免覆盖原有数据
     * 4. 根据影响行数判断操作是否成功
     *
     * @param user 用户对象（ID必填）
     * @return true-更新成功（影响行数>0），false-更新失败
     */
    public boolean update(User user) {
        if (user == null) return false;
        return userMapper.updateByPrimaryKeySelective(user) > 0 ? true : false;
    }

    /**
     * =====================================================
     * 按ID查询用户
     * =====================================================
     * 核心逻辑：
     * 1. 调用Mapper的selectByPrimaryKey方法
     * 2. 根据主键ID查询完整信息
     *
     * @param userId 用户ID
     * @return 用户对象，不存在则返回null
     */
    public User selectById(Integer userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    /**
     * =====================================================
     * 按手机号精确查询用户
     * =====================================================
     * 核心逻辑：
     * 1. 调用Mapper的findbyuser_phone方法
     * 2. 根据手机号精确匹配
     *
     * @param userPhone 用户手机号
     * @return 用户对象，不存在则返回null
     */
    public User selectByPhone(String userPhone) {
        return userMapper.findbyuser_phone(userPhone);
    }

    /**
     * =====================================================
     * 用户登录验证
     * =====================================================
     * 核心逻辑：
     * 1. 参数非空校验（手机号或密码为null则直接返回false）
     * 2. 根据手机号查询用户
     * 3. 用户不存在则返回false
     * 4. 比对数据库中存储的密码与传入密码是否一致
     *
     * @param phoneNumber 用户手机号
     * @param password    用户密码
     * @return true-账号密码正确，false-验证失败
     */
    public boolean login(String phoneNumber, String password) {
        // 参数非空校验
        if (password == null || phoneNumber == null) return false;
        // 根据手机号查询用户
        User user = userMapper.findbyuser_phone(phoneNumber);
        if (user == null) return false;
        // 密码比对（目前是明文比对，生产环境建议使用加密比对）
        return user.getUser_password().equals(password);
    }

    /**
     * =====================================================
     * 按手机号模糊分页查询用户列表
     * =====================================================
     * 核心逻辑：
     * 1. 使用PageHelper.startPage()开启分页
     * 2. 调用Mapper的findbyUser_phonecontaining方法进行模糊查询
     * 3. PageHelper会自动拦截SQL并添加分页参数（LIMIT）
     *
     * @param phoneNumber 用户手机号（模糊匹配，like %phoneNumber%）
     * @param pageNum     页码（从1开始）
     * @param pageSize    每页记录数
     * @return 用户列表（不含密码字段），可能为空列表
     */
    public List<User> selectByPhone(String phoneNumber, Integer pageNum, Integer pageSize) {
        // 开启分页（PageHelper会拦截后续第一次查询）
        PageHelper.startPage(pageNum, pageSize);
        // 执行模糊查询，PageHelper自动添加分页限制
        return userMapper.findbyUser_phonecontaining(phoneNumber);
    }

    /**
     * =====================================================
     * 按昵称模糊分页查询用户列表
     * =====================================================
     * 核心逻辑：
     * 1. 使用PageHelper.startPage()开启分页
     * 2. 调用Mapper的findbyUser_aliascontaining方法进行模糊查询
     * 3. PageHelper会自动拦截SQL并添加分页参数（LIMIT）
     *
     * @param alias    用户昵称（模糊匹配，like %alias%）
     * @param pageNum  页码（从1开始）
     * @param pageSize 每页记录数
     * @return 用户列表（不含密码字段），可能为空列表
     */
    public List<User> selectByAlias(String alias, Integer pageNum, Integer pageSize) {
        // 开启分页（PageHelper会拦截后续第一次查询）
        PageHelper.startPage(pageNum, pageSize);
        // 执行模糊查询，PageHelper自动添加分页限制
        return userMapper.findbyUser_aliascontaining(alias);
    }

    /**
     * =====================================================
     * 分页查询所有用户
     * =====================================================
     * 核心逻辑：
     * 1. 使用PageHelper.startPage()开启分页
     * 2. 调用Mapper的findorderByuser_iddesc方法按ID倒序查询
     * 3. PageHelper会自动拦截SQL并添加分页参数（LIMIT）
     *
     * @param pageNum  页码（从1开始）
     * @param pageSize 每页记录数，如果为0则不分页
     * @return 用户列表（按ID倒序，不含密码字段），可能为空列表
     */
    public List<User> selectByPage(Integer pageNum, Integer pageSize) {
        // 开启分页（PageHelper会拦截后续第一次查询）
        PageHelper.startPage(pageNum, pageSize);
        // 执行按ID倒序查询，PageHelper自动添加分页限制
        return userMapper.findorderByuser_iddesc();
    }

    /**
     * =====================================================
     * 用户余额充值
     * =====================================================
     * 核心逻辑：
     * 1. 校验充值金额不能为负数（业务规则：充值金额必须>=0）
     * 2. 根据用户ID查询当前用户信息
     * 3. 用户不存在则返回false
     * 4. 计算新余额 = 原余额 + 充值金额
     * 5. 清空密码和手机号字段（避免更新时误覆盖敏感字段）
     * 6. 执行更新操作，根据影响行数判断是否成功
     *
     * @param userId 用户ID
     * @param money  充值金额（正数）
     * @return true-充值成功，false-充值失败
     */
    public boolean recharge(Integer userId, Float money) {
        // 充值金额不能为负数
        if (money < 0) return false;
        // 查询用户当前信息
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) return false;
        // 计算新余额
        user.setUser_balance(user.getUser_balance() + money);
        // 清空密码和手机号，避免更新时被覆盖为空（只更新余额字段）
        user.setUser_password(null);
        user.setUser_phone(null);
        // 执行更新，返回是否成功
        return userMapper.updateByPrimaryKeySelective(user) > 0 ? true : false;
    }

    /**
     * =====================================================
     * 用户余额扣费
     * =====================================================
     * 核心逻辑：
     * 1. 校验扣费金额不能为负数（业务规则：扣费金额必须>=0）
     * 2. 根据用户ID查询当前用户信息
     * 3. 用户不存在则返回false
     * 4. 校验余额是否充足（扣费后余额不能为负数）
     * 5. 计算新余额 = 原余额 - 扣费金额
     * 6. 清空密码和手机号字段（避免更新时误覆盖敏感字段）
     * 7. 执行更新操作，根据影响行数判断是否成功
     *
     * @param userId 用户ID
     * @param cost   扣费金额（正数）
     * @return true-扣费成功，false-扣费失败（余额不足或参数错误）
     */
    public boolean deduct(Integer userId, Float cost) {
        // 扣费金额不能为负数
        if (cost < 0) return false;
        // 查询用户当前信息
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) return false;
        // 余额不足校验（扣费后不能为负数）
        if (user.getUser_balance() - cost < 0) return false;
        // 计算新余额
        user.setUser_balance(user.getUser_balance() - cost);
        // 清空密码和手机号，避免更新时被覆盖为空（只更新余额字段）
        user.setUser_password(null);
        user.setUser_phone(null);
        // 执行更新，返回是否成功
        return userMapper.updateByPrimaryKeySelective(user) > 0 ? true : false;
    }
}