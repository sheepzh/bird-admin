package com.bird.service.system.impl;

import com.bird.common.BirdException;
import com.bird.common.BirdOutException;
import com.bird.common.cache.CacheKeySeed;
import com.bird.common.cache.SimpleKvCache;
import com.bird.dao.SystemPermissionNodeMapper;
import com.bird.dao.SystemPermissionRoleMapper;
import com.bird.dao.SystemRoleMapper;
import com.bird.dao.extend.PermissionMapperExtend;
import com.bird.model.entity.*;
import com.bird.service.system.IPermissionService;
import com.bird.service.system.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.bird.utils.StringUtil.isBlank;

/**
 * @author zhyyy
 **/
@Service
public class PermissionServiceImpl implements IPermissionService {
    private final CacheKeySeed MEM_FLAG_PERMISSION_ROLES_OF_MODULE = new CacheKeySeed("PERMISSION_ROLES_OF_MODULE");

    @Autowired
    private SystemPermissionNodeMapper permissionNodeMapper;
    @Autowired
    private SystemPermissionRoleMapper permissionRoleMapper;
    @Autowired
    private SimpleKvCache kvCache;
    @Autowired
    private SystemRoleMapper roleMapper;
    @Autowired
    private PermissionMapperExtend permissionMapperExtend;

    @Override
    public List<SystemPermissionNode> list(String module, String name, String des) {
        SystemPermissionNodeExample example = new SystemPermissionNodeExample();
        example.setOrderByClause("module,id");
        SystemPermissionNodeExample.Criteria criteria = example.createCriteria();
        if (!isBlank(module)) {
            criteria.andModuleLike("%" + module.trim() + "%");
        }
        if (!isBlank(name)) {
            criteria.andNameLike("%" + name.trim() + "%");
        }
        if (!isBlank(des)) {
            criteria.andDesLike("%" + des.trim() + "%");
        }
        return permissionNodeMapper.selectByExample(example);
    }

    @Override
    public void addPermission(SystemPermissionNode toAdd) {
        assertDuplicate(toAdd);
        toAdd.setStatus(NORMAL);
        permissionNodeMapper.insertSelective(toAdd);
    }

    @Override
    public void updatePermission(SystemPermissionNode toUpdate) {
        assertDuplicate(toUpdate);
        toUpdate.setStatus(null);
        permissionNodeMapper.updateByPrimaryKeySelective(toUpdate);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deletePermission(int toDelete) {
        // 删除权限节点的绑定角色。先删除外键表
        SystemPermissionRoleExample example = new SystemPermissionRoleExample();
        example.createCriteria().andPermissionIdEqualTo(toDelete);
        permissionRoleMapper.deleteByExample(example);
        // 删除权限节点
        permissionNodeMapper.deleteByPrimaryKey(toDelete);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updatePermissionRoles(int id, List<Integer> roleIds) {
        // 删除原有的
        SystemPermissionRoleExample example = new SystemPermissionRoleExample();
        example.createCriteria().andPermissionIdEqualTo(id);
        permissionRoleMapper.deleteByExample(example);
        // 添加现有的
        roleIds.forEach(r -> {
            SystemRole role = roleMapper.selectByPrimaryKey(r);
            if (role == null) {
                throw new BirdOutException("角色不存在：ID=" + r);
            }
            if (role.getStatus() != IRoleService.NORMAL) {
                throw new BirdOutException("状态非法：ID=" + r);
            }
            SystemPermissionRole permissionRole = new SystemPermissionRole();
            permissionRole.setPermissionId(id);
            permissionRole.setRoleId(r);
            permissionRoleMapper.insertSelective(permissionRole);
        });
    }

    @Override
    public Map<String, List<String>> listRoleIdsByModule(String moduleName) {
        return kvCache.getOrDefault(MEM_FLAG_PERMISSION_ROLES_OF_MODULE.key(moduleName), () -> {
            List<SystemPermissionNode> permissionNodes = permissionMapperExtend.listByModule(moduleName);
            return permissionNodes.stream()
                    .collect(Collectors.toMap(
                            SystemPermissionNode::getName,
                            n -> permissionMapperExtend.listRoleNamesByPermission(n.getId())
                    ));
        });
    }

    @Override
    public void flush() {
        MEM_FLAG_PERMISSION_ROLES_OF_MODULE.updateVersion();
    }

    private void assertDuplicate(SystemPermissionNode toAssert) {
        SystemPermissionNodeExample example = new SystemPermissionNodeExample();
        example.createCriteria().andModuleEqualTo(toAssert.getModule()).andNameEqualTo(toAssert.getName());
        List<SystemPermissionNode> result = permissionNodeMapper.selectByExample(example);
        if (!result.isEmpty()) {
            if (result.size() != 1 || !Objects.equals(result.get(0).getId(), toAssert.getId())) {
                throw new BirdOutException("组件名称和权限代码重复");
            }
        }
    }
}
