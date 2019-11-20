package com.bird.service.system.impl;

import com.bird.common.BirdOutException;
import com.bird.common.cache.CacheKeySeed;
import com.bird.common.cache.SimpleKvCache;
import com.bird.dao.StaffRoleMapper;
import com.bird.dao.SystemPermissionRoleMapper;
import com.bird.dao.SystemRoleMapper;
import com.bird.dao.SystemRouteRoleMapper;
import com.bird.model.entity.*;
import com.bird.service.system.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.trimToNull;

/**
 * @author zhyyy
 **/
@Service
public class RoleServiceImpl implements IRoleService {
    /**
     * 用户的角色ID，附加信息为用户ID
     */
    private final CacheKeySeed MEM_FLAG_STAFF_ROLE_ID = new CacheKeySeed("ROLE_ID_OF_STAFF");
    /**
     * 所有角色信息缓存，无附加信息
     */
    private final CacheKeySeed MEM_FLAG_ALL_ROLE = new CacheKeySeed("ALL_ROLE_LIST");
    /**
     * 权限点相关的角色ID缓存，附加信息为权限点ID
     */
    private final CacheKeySeed MEM_FLAG_PERMISSION_ROLE_PREFIX = new CacheKeySeed("ROLE_OF_PERMISSION_NODE");

    @Autowired
    private SystemRoleMapper roleMapper;

    @Autowired
    private StaffRoleMapper staffRoleMapper;

    @Autowired
    private SystemPermissionRoleMapper permissionRoleMapper;

    @Autowired
    private SystemRouteRoleMapper routeRoleMapper;

    @Autowired
    private SimpleKvCache kvCache;

    @Override
    public List<SystemRole> listAllRoles() {
        return kvCache.getOrDefault(MEM_FLAG_ALL_ROLE.key(),
                () -> roleMapper.selectByExample(new SystemRoleExample())
        );
    }

    @Override
    public List<Integer> listRoleIdsByStaff(int userId) {
        return kvCache.getOrDefault(MEM_FLAG_STAFF_ROLE_ID.key(userId),
                () -> {
                    StaffRoleExample example = new StaffRoleExample();
                    example.createCriteria().andStaffIdEqualTo(userId);
                    return staffRoleMapper.selectByExample(example)
                            .stream()
                            .map(StaffRole::getRoleId).collect(Collectors.toList());
                });
    }

    @Override
    public List<SystemRole> listRolesByStaff(int userId) {
        List<Integer> roleIds = listRoleIdsByStaff(userId);
        List<SystemRole> allRoles = listAllRoles();
        return allRoles.stream().filter(r -> roleIds.contains(r.getId())).collect(Collectors.toList());
    }

    @Override
    public void addRole(SystemRole toAdd) {
        SystemRoleExample example = new SystemRoleExample();
        example.createCriteria().andNameEqualTo(toAdd.getName());
        if (!roleMapper.selectByExample(example).isEmpty()) {
            throw new BirdOutException("角色代码重复!");
        }
        toAdd.setStatus(NORMAL);
        roleMapper.insertSelective(toAdd);
        MEM_FLAG_ALL_ROLE.updateVersion();
    }

    @Override
    public void updateRole(SystemRole toUpdate) {
        toUpdate.setStatus(null);
        roleMapper.updateByPrimaryKeySelective(toUpdate);
    }

    @Override
    public List<SystemRole> list(String name, String nameShow, Short status) {
        SystemRoleExample example = new SystemRoleExample();
        SystemRoleExample.Criteria criteria = example.createCriteria();
        if ((name = trimToNull(name)) != null) {
            criteria.andNameLike("%" + name + "%");
        }
        if ((nameShow = trimToNull(nameShow)) != null) {
            criteria.andNameShowLike("%" + nameShow + "%");
        }
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        return roleMapper.selectByExample(example);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void ban(int id) {
        SystemRole role = roleMapper.selectByPrimaryKey(id);
        if (role == null) {
            throw new BirdOutException("未查询到该角色：" + id);
        }
        if (role.getStatus() != NORMAL) {
            throw new BirdOutException("角色状态异常:" + role.getStatus());
        }
        // 删除角色拥有的权限
        SystemPermissionRoleExample permissionRoleExample = new SystemPermissionRoleExample();
        permissionRoleExample.createCriteria().andRoleIdEqualTo(id);
        permissionRoleMapper.deleteByExample(permissionRoleExample);
        // 删除角色拥有的路由
        SystemRouteRoleExample routeRoleExample = new SystemRouteRoleExample();
        routeRoleExample.createCriteria().andRoleIdEqualTo(id);
        routeRoleMapper.deleteByExample(routeRoleExample);
        // 删除角色与用户的关系
        StaffRoleExample staffRoleExample = new StaffRoleExample();
        staffRoleExample.createCriteria().andRoleIdEqualTo(id);
        staffRoleMapper.deleteByExample(staffRoleExample);
        // 修改状态
        role.setStatus(BANNED);
        roleMapper.updateByPrimaryKeySelective(role);
    }

    @Override
    public void lift(int id) {
        SystemRole role = roleMapper.selectByPrimaryKey(id);
        if (role == null) {
            throw new BirdOutException("未查询到该角色：" + id);
        }
        if (role.getStatus() != BANNED) {
            throw new BirdOutException("角色状态异常:" + role.getStatus());
        }
        // 修改状态
        role.setStatus(NORMAL);
        roleMapper.updateByPrimaryKeySelective(role);
    }

    @Override
    public List<Integer> listRoleIdsByPermission(Integer id) {
        return kvCache.getOrDefault(MEM_FLAG_PERMISSION_ROLE_PREFIX.key(id), () -> {
            SystemPermissionRoleExample example = new SystemPermissionRoleExample();
            example.createCriteria().andPermissionIdEqualTo(id);
            return permissionRoleMapper.selectByExample(example).stream()
                    .map(SystemPermissionRole::getRoleId)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public void flushPermissionCache() {
        MEM_FLAG_PERMISSION_ROLE_PREFIX.updateVersion();
    }

    @Override
    public void deleteRole(int id) {
        SystemPermissionRoleExample permissionRoleExample = new SystemPermissionRoleExample();
        permissionRoleExample.createCriteria().andRoleIdEqualTo(id);
        if (!permissionRoleMapper.selectByExample(permissionRoleExample).isEmpty()) {
            throw new BirdOutException("该角色配置了权限，不可删除");
        }
        // 删除角色拥有的路由
        SystemRouteRoleExample routeRoleExample = new SystemRouteRoleExample();
        routeRoleExample.createCriteria().andRoleIdEqualTo(id);
        if (!routeRoleMapper.selectByExample(routeRoleExample).isEmpty()) {
            throw new BirdOutException("该角色配置了路由，不可删除");
        }
        // 删除角色与用户的关系
        StaffRoleExample staffRoleExample = new StaffRoleExample();
        staffRoleExample.createCriteria().andRoleIdEqualTo(id);
        if (!staffRoleMapper.selectByExample(staffRoleExample).isEmpty()) {
            throw new BirdOutException("该角色下有用户，不可删除");
        }
        roleMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void flushUserRole(Integer userId) {
        if (userId == null) {
            // 全部刷新
            MEM_FLAG_STAFF_ROLE_ID.updateVersion();
        } else {
            // 删除指定用户的角色缓存
            kvCache.delete(MEM_FLAG_STAFF_ROLE_ID.key(userId));
        }
    }
}
