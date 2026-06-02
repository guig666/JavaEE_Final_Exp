package com.konsonx.dao;

import com.konsonx.po.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * =====================================================
 * 用户数据访问层（Mapper）
 * =====================================================
 * 功能说明：
 * 1. 用户表的CRUD操作（由MyBatis Generator自动生成基础方法）
 * 2. 自定义查询方法：按手机号模糊查询、按昵称模糊查询、按手机号精确查询、按ID排序查询
 * =====================================================
 */
@Repository(value = "UserMapper")
public interface UserMapper {

    /**
     * =====================================================
     * 根据主键删除用户
     * =====================================================
     * 核心逻辑：
     * 1. 根据user_id删除对应记录
     * 2. 返回影响行数
     *
     * @param user_id 用户ID
     * @return 影响行数（1-删除成功，0-未找到记录）
     */
    int deleteByPrimaryKey(Integer user_id);

    /**
     * =====================================================
     * 新增用户（插入所有字段）
     * =====================================================
     * 核心逻辑：
     * 1. 插入完整的用户记录
     * 2. 注意：如果字段为null也会插入null值
     *
     * @param record 用户对象
     * @return 影响行数（1-插入成功）
     */
    int insert(User record);

    /**
     * =====================================================
     * 新增用户（选择性插入）
     * =====================================================
     * 核心逻辑：
     * 1. 只插入非空字段
     * 2. 未设置的字段使用数据库默认值或保持null
     * 3. 推荐使用此方法，更安全
     *
     * @param record 用户对象
     * @return 影响行数（1-插入成功）
     */
    int insertSelective(User record);

    /**
     * =====================================================
     * 根据主键查询用户
     * =====================================================
     * 核心逻辑：
     * 1. 根据user_id查询单个用户完整信息
     *
     * @param user_id 用户ID
     * @return 用户对象，不存在则返回null
     */
    User selectByPrimaryKey(Integer user_id);

    /**
     * =====================================================
     * 根据主键选择性更新用户
     * =====================================================
     * 核心逻辑：
     * 1. 根据user_id定位记录
     * 2. 只更新非空字段
     * 3. 推荐使用此方法，避免误覆盖
     *
     * @param record 用户对象（ID必填）
     * @return 影响行数（1-更新成功，0-未找到记录）
     */
    int updateByPrimaryKeySelective(User record);

    /**
     * =====================================================
     * 根据主键全量更新用户
     * =====================================================
     * 核心逻辑：
     * 1. 根据user_id定位记录
     * 2. 更新所有字段（包括null值）
     * 3. 谨慎使用，可能导致字段被覆盖为null
     *
     * @param record 用户对象（ID必填）
     * @return 影响行数（1-更新成功，0-未找到记录）
     */
    int updateByPrimaryKey(User record);

    /**
     * =====================================================
     * 按手机号模糊查询用户列表
     * =====================================================
     * 核心逻辑：
     * 1. 执行 like concat('%', #{containingUser_phone}, '%') 模糊匹配
     * 2. 配合PageHelper实现分页
     * 3. 用于按手机号搜索功能
     * 4. 注意：查询结果不包含密码字段（使用List_Column_List）
     *
     * @param containingUser_phone 用户手机号（模糊匹配）
     * @return 用户列表（不含密码），可能为空列表
     */
    List<User> findbyUser_phonecontaining(@Param("containingUser_phone") String containingUser_phone);

    /**
     * =====================================================
     * 查询所有用户（按ID倒序）
     * =====================================================
     * 核心逻辑：
     * 1. 查询所有用户记录
     * 2. 按user_id降序排列（最新注册的在最前）
     * 3. 配合PageHelper实现分页
     * 4. 注意：查询结果不包含密码字段（使用List_Column_List）
     *
     * @return 用户列表（按ID倒序，不含密码）
     */
    List<User> findorderByuser_iddesc();

    /**
     * =====================================================
     * 查询所有用户（按ID正序）
     * =====================================================
     * 核心逻辑：
     * 1. 查询所有用户记录
     * 2. 按user_id升序排列（最早注册的在最前）
     * 3. 注意：查询结果不包含密码字段（使用List_Column_List）
     *
     * @return 用户列表（按ID正序，不含密码）
     */
    List<User> findorderByuser_id();

    /**
     * =====================================================
     * 按手机号精确查询用户
     * =====================================================
     * 核心逻辑：
     * 1. 执行 where user_phone = #{user_phone} 精确匹配
     * 2. 用于登录验证、账号唯一性检查
     * 3. 注意：查询结果包含完整字段（包括密码，使用Base_Column_List）
     *
     * @param user_phone 用户手机号（精确匹配）
     * @return 用户对象（含密码），不存在则返回null
     */
    User findbyuser_phone(@Param("user_phone") String user_phone);

    /**
     * =====================================================
     * 按昵称模糊查询用户列表
     * =====================================================
     * 核心逻辑：
     * 1. 执行 like concat('%', #{containingUser_alias}, '%') 模糊匹配
     * 2. 配合PageHelper实现分页
     * 3. 用于按昵称搜索功能
     * 4. 注意：查询结果不包含密码字段（使用List_Column_List）
     *
     * @param containingUser_alias 用户昵称（模糊匹配）
     * @return 用户列表（不含密码），可能为空列表
     */
    List<User> findbyUser_aliascontaining(@Param("containingUser_alias") String containingUser_alias);
}