package com.konsonx.dao;

import com.konsonx.po.Admin;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * =====================================================
 * 管理员数据访问层（Mapper）
 * =====================================================
 * 功能说明：
 * 1. 管理员表的CRUD操作（由MyBatis Generator自动生成基础方法）
 * 2. 自定义查询方法：按账号模糊查询、按账号精确查询、按ID排序查询
 * =====================================================
 */
@Repository(value = "AdminMapper")
public interface AdminMapper {

    /**
     * =====================================================
     * 根据主键删除管理员
     * =====================================================
     * 核心逻辑：
     * 1. 根据admin_id删除对应记录
     * 2. 返回影响行数
     *
     * @param admin_id 管理员ID
     * @return 影响行数（1-删除成功，0-未找到记录）
     */
    int deleteByPrimaryKey(Integer admin_id);

    /**
     * =====================================================
     * 新增管理员（插入所有字段）
     * =====================================================
     * 核心逻辑：
     * 1. 插入完整的管理员记录
     * 2. 注意：如果字段为null也会插入null值
     *
     * @param record 管理员对象
     * @return 影响行数（1-插入成功）
     */
    int insert(Admin record);

    /**
     * =====================================================
     * 新增管理员（选择性插入）
     * =====================================================
     * 核心逻辑：
     * 1. 只插入非空字段
     * 2. 未设置的字段使用数据库默认值或保持null
     * 3. 推荐使用此方法，更安全
     *
     * @param record 管理员对象
     * @return 影响行数（1-插入成功）
     */
    int insertSelective(Admin record);

    /**
     * =====================================================
     * 根据主键查询管理员
     * =====================================================
     * 核心逻辑：
     * 1. 根据admin_id查询单个管理员完整信息
     *
     * @param admin_id 管理员ID
     * @return 管理员对象，不存在则返回null
     */
    Admin selectByPrimaryKey(Integer admin_id);

    /**
     * =====================================================
     * 根据主键选择性更新管理员
     * =====================================================
     * 核心逻辑：
     * 1. 根据admin_id定位记录
     * 2. 只更新非空字段
     * 3. 推荐使用此方法，避免误覆盖
     *
     * @param record 管理员对象（ID必填）
     * @return 影响行数（1-更新成功，0-未找到记录）
     */
    int updateByPrimaryKeySelective(Admin record);

    /**
     * =====================================================
     * 根据主键全量更新管理员
     * =====================================================
     * 核心逻辑：
     * 1. 根据admin_id定位记录
     * 2. 更新所有字段（包括null值）
     * 3. 谨慎使用，可能导致字段被覆盖为null
     *
     * @param record 管理员对象（ID必填）
     * @return 影响行数（1-更新成功，0-未找到记录）
     */
    int updateByPrimaryKey(Admin record);

    /**
     * =====================================================
     * 按账号模糊查询管理员列表
     * =====================================================
     * 核心逻辑：
     * 1. 执行 like concat('%', #{containingAdmin_account}, '%') 模糊匹配
     * 2. 配合PageHelper实现分页
     * 3. 用于搜索功能
     *
     * @param containingAdmin_account 管理员账号（模糊匹配）
     * @return 管理员列表，可能为空列表
     */
    List<Admin> findByadmin_accountcontaining(@Param("containingAdmin_account") String containingAdmin_account);

    /**
     * =====================================================
     * 按账号精确查询管理员
     * =====================================================
     * 核心逻辑：
     * 1. 执行 where admin_account = #{admin_account} 精确匹配
     * 2. 用于登录验证、账号唯一性检查
     *
     * @param admin_account 管理员账号（精确匹配）
     * @return 管理员对象，不存在则返回null
     */
    Admin findByAdmin_account(@Param("admin_account") String admin_account);

    /**
     * =====================================================
     * 查询所有管理员（按ID倒序）
     * =====================================================
     * 核心逻辑：
     * 1. 查询所有管理员记录
     * 2. 按admin_id降序排列（最新注册的在最前）
     * 3. 配合PageHelper实现分页
     *
     * @return 管理员列表（按ID倒序）
     */
    List<Admin> findorderByAdmin_iddesc();

    /**
     * =====================================================
     * 查询所有管理员（按ID正序）
     * =====================================================
     * 核心逻辑：
     * 1. 查询所有管理员记录
     * 2. 按admin_id升序排列（最早注册的在最前）
     *
     * @return 管理员列表（按ID正序）
     */
    List<Admin> findorderByadmin_id();
}