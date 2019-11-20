package com.bird.service.system.impl;

import com.bird.common.BirdException;
import com.bird.common.BirdOutException;
import com.bird.common.cache.CacheKeySeed;
import com.bird.common.cache.MemcacheHelper;
import com.bird.dao.SystemRouteMapper;
import com.bird.dao.SystemRouteRoleMapper;
import com.bird.dao.extend.RoleMapperExtend;
import com.bird.model.entity.*;
import com.bird.model.vo.MenuAndRolePermission;
import com.bird.service.system.IRouteService;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 路由服务
 *
 * @author zhyyy
 **/
@Service
public class RouteServiceImpl implements IRouteService {
    private final CacheKeySeed MEM_FLAG_ROUTE_ID_ROLE = new CacheKeySeed("ROUTE_ID_OF_ROLE");
    private final CacheKeySeed MEM_FLAG_ROUTE_SIMPLE_LIST = new CacheKeySeed("ROUTE_LIST");
    private final CacheKeySeed MEM_FLAG_ROUTE_ROLE_TREE = new CacheKeySeed("ROUTE_ROLE_TREE");

    private static int ROOT_ROLE_ID = 0;

    private final Object ROLE_READ_MUTEX = new Object();

    @Autowired
    private MemcacheHelper memcacheHelper;
    @Autowired
    private SystemRouteRoleMapper routeRoleMapper;
    @Autowired
    private SystemRouteMapper routeMapper;
    @Autowired
    private RoleMapperExtend roleMapperExtend;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public List<SystemRoute> listByRoles(List<Integer> roleIds) {
        // 所有路由
        List<SystemRoute> allRoutes = listAllRoutes();
        // 当前角色所拥有的路由
        Set<Integer> allRouteIds = roleIds.stream().map(i ->
                memcacheHelper.getOrDefault(MEM_FLAG_ROUTE_ID_ROLE.key(i), () -> {
                    SystemRouteRoleExample example = new SystemRouteRoleExample();
                    example.createCriteria().andRoleIdEqualTo(i);
                    return routeRoleMapper.selectByExample(example).stream()
                            .map(SystemRouteRole::getRouteId).collect(Collectors.toList());
                })).flatMap(Collection::stream).collect(Collectors.toSet());
        return allRoutes.stream().filter(r -> allRouteIds.contains(r.getId()) && r.getAsync()).collect(Collectors.toList());
    }

    @Override
    public List<SystemRoute> listAllRoutes() {
        return memcacheHelper.getOrDefault(MEM_FLAG_ROUTE_SIMPLE_LIST.key(), () -> routeMapper.selectByExample(new SystemRouteExample()));
    }

    @Override
    public List<MenuAndRolePermission> listAllRoutesWithRoles() {
        return memcacheHelper.getOrDefault(MEM_FLAG_ROUTE_ROLE_TREE.key(), () -> {
            SystemRouteExample example = new SystemRouteExample();
            // 只获取动态配置的路由信息,除去/root路径的所有功能
            example.createCriteria().andAsyncEqualTo(true).andPathNotLike("/root%");
            // 生成路由树
            return getChildren(routeMapper.selectByExample(example), null);
        });
    }

    @Override
    public void flushRoutes() {
        MEM_FLAG_ROUTE_ID_ROLE.updateVersion();
        MEM_FLAG_ROUTE_ROLE_TREE.updateVersion();
        MEM_FLAG_ROUTE_SIMPLE_LIST.updateVersion();
    }

    @Override
    public void updateRoleIds(int id, Boolean cascade, List<Integer> roleIds) {
        SystemRoute route = routeMapper.selectByPrimaryKey(id);
        // 路由信息校验
        if (route == null) {
            throw new BirdException("路由不存在");
        }
        if (!route.getAsync()) {
            throw new BirdException("不是动态路由：" + route.getPath());
        }
        // 开启批量提交SQL
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        SystemRouteRoleMapper routeRoleMapperBatch = sqlSession.getMapper(SystemRouteRoleMapper.class);
        List<SystemRoute> routeList = listAllRoutes();
        // 修改角色权限，如果级联则递归
        updateRoleIds(routeRoleMapperBatch, routeList, id, roleIds, cascade);
        // 如果非级联，删除子孙路由中过期的角色
        if (!cascade) {
            deleteChildrenRoles(routeRoleMapperBatch, routeList, id, roleIds);
        }
        // 提交
        sqlSession.flushStatements();
        // 清空缓存
        flushRoutes();
    }

    @Override
    public void addRoute(SystemRoute toAdd) {
        toAdd.setAsync(true);
        routeMapper.insertSelective(toAdd);
        flushRoutes();
    }

    @Override
    public void updateRoute(SystemRoute toUpdate) {
        toUpdate.setAsync(null);
        routeMapper.updateByPrimaryKeySelective(toUpdate);
        flushRoutes();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED)
    public void deleteRoute(int id) {
        // 删除关联角色
        SystemRouteRoleExample example = new SystemRouteRoleExample();
        example.createCriteria().andRouteIdEqualTo(id);
        routeRoleMapper.deleteByExample(example);
        // 删除子路由
        SystemRouteExample routeExample = new SystemRouteExample();
        routeExample.createCriteria().andParentEqualTo(id);
        List<SystemRoute> children = routeMapper.selectByExample(routeExample);
        children.stream().map(SystemRoute::getId).forEach(this::deleteRoute);
        // 删除当前路由
        routeMapper.deleteByPrimaryKey(id);
    }

    private void updateRoleIds(SystemRouteRoleMapper routeRoleMapperBatch, List<SystemRoute> routeList,
                               int routeId, List<Integer> roleIds, boolean cascade) {
        // 1.删除原有路由
        SystemRouteRoleExample routeRoleExample = new SystemRouteRoleExample();
        routeRoleExample.createCriteria().andRouteIdEqualTo(routeId).andRoleIdNotEqualTo(getRootId());
        routeRoleMapperBatch.deleteByExample(routeRoleExample);
        // 2.添加新路由信息
        roleIds.forEach(r -> {
            SystemRouteRole routeRole = new SystemRouteRole();
            routeRole.setRoleId(r);
            routeRole.setRouteId(routeId);
            routeRoleMapperBatch.insertSelective(routeRole);
        });
        // 级联修改
        if (cascade) {
            routeList.stream()
                    .filter(r -> Objects.equals(r.getParent(), routeId))
                    .forEach(r -> updateRoleIds(routeRoleMapperBatch, routeList, r.getId(), roleIds, true));
        }
    }

    private void deleteChildrenRoles(SystemRouteRoleMapper routeRoleMapperBatch, List<SystemRoute> routeList,
                                     int routeId, List<Integer> roleIds) {
        int rootId = getRootId();
        routeList.stream()
                // 子路由
                .filter(r -> Objects.equals(r.getParent(), routeId))
                .forEach(cr -> {
                    SystemRouteRoleExample example = new SystemRouteRoleExample();
                    example.createCriteria().andRouteIdEqualTo(cr.getId()).andRoleIdNotIn(roleIds).andRoleIdNotEqualTo(rootId);
                    routeRoleMapperBatch.deleteByExample(example);
                    // 递归删除
                    deleteChildrenRoles(routeRoleMapperBatch, routeList, cr.getId(), roleIds);
                });
    }

    private List<MenuAndRolePermission> getChildren(List<SystemRoute> routeList, Integer parent) {
        return routeList.stream()
                .filter(r -> Objects.equals(r.getParent(), parent))
                .map(r -> {
                    MenuAndRolePermission menu = new MenuAndRolePermission();
                    menu.setDes(r.getDes());
                    menu.setId(r.getId());
                    menu.setLabel(r.getDes());
                    menu.setPath(r.getPath());
                    // 获取有权限的所有角色
                    SystemRouteRoleExample routeRoleExample = new SystemRouteRoleExample();
                    routeRoleExample.createCriteria().andRouteIdEqualTo(r.getId()).andRoleIdNotEqualTo(getRootId());
                    menu.setRoleIds(routeRoleMapper.selectByExample(routeRoleExample)
                            .stream()
                            .map(SystemRouteRole::getRoleId).collect(Collectors.toList()));
                    // 获取children
                    menu.setChildren(getChildren(routeList, menu.getId()));
                    return menu;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取root角色ID
     *
     * @return root ID
     */
    private int getRootId() {
        synchronized (ROLE_READ_MUTEX) {
            if (ROOT_ROLE_ID == 0) {
                SystemRole root = roleMapperExtend.selectByName("root");
                if (root == null) {
                    throw new BirdOutException("ROOT 角色未配置");
                }
                ROOT_ROLE_ID = root.getId();
            }
        }
        return ROOT_ROLE_ID;
    }
}
