package com.bird.service.buz;

import com.bird.model.entity.Staff;
import com.bird.model.vo.StaffWithRoles;

import java.util.List;

/**
 * 用户相关服务
 *
 * @author zhyyy
 * @see com.bird.service.buz.impl.UserServiceImpl
 */
public interface IUserService {
    /**
     * 状态：已失效/离职
     */
    short INVALID = 0;

    /**
     * 状态：正常
     */
    short NORMAL = 1;

    /**
     * 被禁用
     */
    short FORBIDDEN = 2;

    /**
     * 用户登录
     *
     * @param account  账号
     * @param password 密码,MD5之后
     * @return 用户信息
     */
    Staff login(String account, String password);

    /**
     * 修改密码
     *
     * @param account 账号
     * @param oldPsw  旧密码
     * @param newPsw  新密码
     */
    void changePassword(String account, String oldPsw, String newPsw);

    /**
     * 重置密码
     *
     * @param account 账号
     */
    void resetPassword(String account);

    /**
     * 根据账号查询用户
     *
     * @param account 账号
     * @return 用户信息
     */
    Staff foundByAccount(String account);

    /**
     * 条件查询
     *
     * @param name    用户名，模糊查询
     * @param account 账号，模糊查询
     * @param roles   拥有的角色列表
     * @param statue  状态
     * @return 用户列表
     */
    List<Staff> list(String name, String account, List<Integer> roles, Integer statue);

    /**
     * 禁用用户
     *
     * @param account 账号
     */
    void forbidden(String account);

    /**
     * 解禁
     *
     * @param account 账号
     */
    void lift(String account);

    /**
     * 失效
     *
     * @param account 账号信息
     */
    void invalid(String account);

    /**
     * 新增用户，并添加用户角色关系
     *
     * @param user 用户信息
     */
    void createUser(StaffWithRoles user);

    /**
     * 更新用户角色
     *
     * @param account 账号
     * @param roles   角色
     * @return 用户ID
     */
    int updateRoles(String account, List<Integer> roles);

    /**
     * 获取所有（所有状态）用户的简易信息
     *
     * @return 用户
     */
    List<Staff> listAllUsersSimple();

    /**
     * 修改用户信息
     *
     * @param systemStaff 用户信息
     */
    void updateUser(Staff systemStaff);
}
