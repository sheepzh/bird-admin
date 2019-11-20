package com.bird.service.system;

import com.bird.model.entity.SystemPermissionNode;

import java.util.List;
import java.util.Map;

/**
 * 权限细节服务
 *
 * @author zhyyy
 * @see com.bird.service.system.impl.PermissionServiceImpl
 **/
public interface IPermissionService {
    /**
     * 状态：正常
     */
    short NORMAL = 1;

    /**
     * 获取所有的权限点
     *
     * @param module 所在组件名称，模糊查询
     * @param name   名称，模糊查询
     * @param des    描述信息，模糊查询
     * @return 权限信息
     */
    List<SystemPermissionNode> list(String module, String name, String des);

    /**
     * 添加权限
     *
     * @param toAdd 待添加权限
     */
    void addPermission(SystemPermissionNode toAdd);

    /**
     * 更新权限信息
     *
     * @param toUpdate 待更新权限
     */
    void updatePermission(SystemPermissionNode toUpdate);

    /**
     * 开发模式下删除权限点
     *
     * @param toDelete 待删除权限点ID
     */
    void deletePermission(int toDelete);

    /**
     * 更新权限角色
     *
     * @param id      权限点ID
     * @param roleIds 角色ID
     */
    void updatePermissionRoles(int id, List<Integer> roleIds);

    /**
     * 通过组件名字获取所有权限点
     *
     * @param moduleName 组件名称
     * @return [permissionName, roleNameList]
     */
    Map<String, List<String>> listRoleIdsByModule(String moduleName);

    /**
     * 刷新所有缓存
     */
    void flush();
}
