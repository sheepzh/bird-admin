package com.bird.model.vo;

import com.bird.model.entity.SystemPermissionNode;

import java.util.List;

/**
 * 前端展示权限细节，及其角色
 *
 * @author zhyyy
 **/
public class PermissionNodeWithRole extends SystemPermissionNode {
    private List<Integer> roleIds;

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public PermissionNodeWithRole setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
        return this;
    }
}
