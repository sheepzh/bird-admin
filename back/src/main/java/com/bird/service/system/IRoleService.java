package com.bird.service.system;

import com.bird.model.entity.SystemRole;

import java.util.List;

/**
 * 角色服务
 *
 * @author zhyyy
 * @see com.bird.service.system.impl.RoleServiceImpl
 **/
public interface IRoleService {

    /**
     * 状态：正常
     */
    short NORMAL = 1;
    /**
     * 状态：禁用
     */
    short BANNED = 0;

    /**
     * 获取所有角色信息，除去root
     *
     * @return 所有角色
     */
    List<SystemRole> listAllRoles();

    /**
     * 获取用户的所有角色ID
     *
     * @param userId 用户ID
     * @return 角色ID
     */
    List<Integer> listRoleIdsByStaff(int userId);

    /**
     * 获取用户的所有角色
     *
     * @param userId 用户ID
     * @return 角色ID
     */
    List<SystemRole> listRolesByStaff(int userId);

    /**
     * 新增角色
     *
     * @param toAdd 角色信息
     */
    void addRole(SystemRole toAdd);

    /**
     * 按主键修改角色信息
     *
     * @param toUpdate 角色信息
     */
    void updateRole(SystemRole toUpdate);

    /**
     * 按条件查询
     *
     * @param name     代码，模糊查询
     * @param nameShow 显示名称，模糊查询
     * @param status   状态
     * @return 角色信息
     */
    List<SystemRole> list(String name, String nameShow, Short status);

    /**
     * 禁用角色
     *
     * @param id 角色ID
     */
    void ban(int id);

    /**
     * 解禁角色
     *
     * @param id 角色ID
     */
    void lift(int id);

    /**
     * 获取权限细节的所有角色
     *
     * @param id 权限点ID
     * @return 角色ID
     */
    List<Integer> listRoleIdsByPermission(Integer id);

    /**
     * 刷新权限缓存
     */
    void flushPermissionCache();

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    void deleteRole(int id);

    /**
     * 刷新用户角色缓存
     *
     * @param userId 用户ID，为空时全部刷新
     */
    void flushUserRole(Integer userId);
}
