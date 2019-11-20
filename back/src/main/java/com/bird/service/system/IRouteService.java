package com.bird.service.system;

import com.bird.model.entity.SystemRoute;
import com.bird.model.vo.MenuAndRolePermission;

import java.util.List;

/**
 * 前端路由权限
 *
 * @author zhyyy
 * @see com.bird.service.system.impl.RouteServiceImpl
 **/
public interface IRouteService {

    /**
     * 按角色权限获取路由
     *
     * @param roleIds 角色列表
     * @return 路由信息
     */
    List<SystemRoute> listByRoles(List<Integer> roleIds);

    /**
     * 所有路由
     *
     * @return 路由信息
     */
    List<SystemRoute> listAllRoutes();

    /**
     * 所有路由的权限
     *
     * @return 路由权限
     */
    List<MenuAndRolePermission> listAllRoutesWithRoles();

    /**
     * 刷新路由缓存信息
     */
    void flushRoutes();

    /**
     * 更新路由的用户权限
     *
     * @param id      路由的ID
     * @param cascade 是否级联子菜单
     * @param roleIds 角色ID
     */
    void updateRoleIds(int id, Boolean cascade, List<Integer> roleIds);

    /**
     * 添加路由
     * active=dev时可用
     *
     * @param toAdd 路由信息
     */
    void addRoute(SystemRoute toAdd);

    /**
     * 更新路由信息
     * active=dev时可用
     *
     * @param toUpdate 路由信息
     */
    void updateRoute(SystemRoute toUpdate);

    /**
     * 删除路由信息
     * active=dev时可用
     *
     * @param id 路由ID
     */
    void deleteRoute(int id);
}
