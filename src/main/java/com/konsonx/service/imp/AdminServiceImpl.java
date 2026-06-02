package com.konsonx.service.imp;

import com.github.pagehelper.PageHelper;
import com.konsonx.dao.AdminMapper;
import com.konsonx.po.Admin;
import com.konsonx.service.AdminService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * =====================================================
 * 管理员服务实现类
 * =====================================================
 * 功能说明：
 * 1. 实现AdminService接口定义的所有方法
 * 2. 调用AdminMapper完成数据库操作
 * 3. 使用PageHelper实现分页查询
 * =====================================================
 */
@Service(value = "AdminService")
public class AdminServiceImpl implements AdminService {

    @Resource(name = "AdminMapper")
    private AdminMapper adminMapper;

    /**
     * =====================================================
     * 新增管理员
     * =====================================================
     * 核心逻辑：
     * 1. 调用Mapper的insertSelective方法（只插入非空字段）
     * 2. 根据影响行数判断操作是否成功
     *
     * @param admin 管理员对象
     * @return true-插入成功（影响行数>0），false-插入失败
     */
    public boolean insert(Admin admin) {
        return adminMapper.insertSelective(admin) > 0 ? true : false;
    }

    /**
     * =====================================================
     * 删除管理员
     * =====================================================
     * 核心逻辑：
     * 1. 根据主键ID删除记录
     * 2. 根据影响行数判断操作是否成功
     *
     * @param id 管理员ID
     * @return true-删除成功（影响行数>0），false-删除失败
     */
    public boolean delete(Integer id) {
        return adminMapper.deleteByPrimaryKey(id) > 0 ? true : false;
    }

    /**
     * =====================================================
     * 更新管理员信息
     * =====================================================
     * 核心逻辑：
     * 1. 调用Mapper的updateByPrimaryKeySelective方法
     * 2. 只更新非空字段，避免覆盖原有数据
     * 3. 根据影响行数判断操作是否成功
     *
     * @param admin 管理员对象（ID必填）
     * @return true-更新成功（影响行数>0），false-更新失败
     */
    public boolean update(Admin admin) {
        return adminMapper.updateByPrimaryKeySelective(admin) > 0 ? true : false;
    }

    /**
     * =====================================================
     * 按ID查询管理员
     * =====================================================
     * 核心逻辑：
     * 1. 调用Mapper的selectByPrimaryKey方法
     * 2. 根据主键ID查询完整信息
     *
     * @param id 管理员ID
     * @return 管理员对象，不存在则返回null
     */
    public Admin selectById(Integer id) {
        return adminMapper.selectByPrimaryKey(id);
    }

    /**
     * =====================================================
     * 按账号精确查询管理员
     * =====================================================
     * 核心逻辑：
     * 1. 调用Mapper的findByAdmin_account方法
     * 2. 根据账号精确匹配
     *
     * @param account 管理员账号
     * @return 管理员对象，不存在则返回null
     */
    public Admin selectByAccount(String account) {
        return adminMapper.findByAdmin_account(account);
    }

    /**
     * =====================================================
     * 管理员登录验证
     * =====================================================
     * 核心逻辑：
     * 1. 参数非空校验（账号或密码为null则直接返回false）
     * 2. 根据账号查询管理员
     * 3. 管理员不存在则返回false
     * 4. 比对数据库中存储的密码与传入密码是否一致
     *
     * @param account  管理员账号
     * @param password 管理员密码
     * @return true-账号密码正确，false-验证失败
     */
    public boolean login(String account, String password) {
        // 参数非空校验
        if (account == null || password == null) return false;
        // 根据账号查询管理员
        Admin admin = adminMapper.findByAdmin_account(account);
        if (admin == null) return false;
        // 密码比对（目前是明文比对，生产环境建议使用加密比对）
        return admin.getAdmin_password().equals(password);
    }

    /**
     * =====================================================
     * 按账号模糊分页查询管理员列表
     * =====================================================
     * 核心逻辑：
     * 1. 使用PageHelper.startPage()开启分页
     * 2. 调用Mapper的findByadmin_accountcontaining方法进行模糊查询
     * 3. PageHelper会自动拦截SQL并添加分页参数（LIMIT）
     *
     * @param account  管理员账号（模糊匹配，like %account%）
     * @param pageNum  页码（从1开始）
     * @param pageSize 每页记录数
     * @return 管理员列表，可能为空列表
     */
    public List<Admin> selectByAccount(String account, Integer pageNum, Integer pageSize) {
        // 开启分页（PageHelper会拦截后续第一次查询）
        PageHelper.startPage(pageNum, pageSize);
        // 执行模糊查询，PageHelper自动添加分页限制
        return adminMapper.findByadmin_accountcontaining(account);
    }

    /**
     * =====================================================
     * 分页查询所有管理员
     * =====================================================
     * 核心逻辑：
     * 1. 使用PageHelper.startPage()开启分页
     * 2. 调用Mapper的findorderByAdmin_iddesc方法按ID倒序查询
     * 3. PageHelper会自动拦截SQL并添加分页参数（LIMIT）
     *
     * @param pageNum  页码（从1开始）
     * @param pageSize 每页记录数，如果为0则不分页
     * @return 管理员列表（按ID倒序），可能为空列表
     */
    public List<Admin> selectByPage(Integer pageNum, Integer pageSize) {
        // 开启分页（PageHelper会拦截后续第一次查询）
        PageHelper.startPage(pageNum, pageSize);
        // 执行按ID倒序查询，PageHelper自动添加分页限制
        return adminMapper.findorderByAdmin_iddesc();
    }
}