package com.bird.model.vo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 前端路由权限展示树
 *
 * @author zhyyy
 **/
public class MenuAndRolePermission implements Serializable {
    private int id;
    private String label;
    private String path;
    private List<Integer> roleIds;
    private List<MenuAndRolePermission> children;
    private String des;

    public int getId() {
        return id;
    }

    public MenuAndRolePermission setId(int id) {
        this.id = id;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public MenuAndRolePermission setLabel(String label) {
        this.label = label;
        return this;
    }

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public MenuAndRolePermission setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
        return this;
    }

    public List<MenuAndRolePermission> getChildren() {
        if (children == null) {
            children = new LinkedList<>();
        }
        return children;
    }

    public MenuAndRolePermission setChildren(List<MenuAndRolePermission> children) {
        this.children = children;
        return this;
    }

    public String getDes() {
        return des;
    }

    public MenuAndRolePermission setDes(String des) {
        this.des = des;
        return this;
    }

    public String getPath() {
        return path;
    }

    public MenuAndRolePermission setPath(String path) {
        this.path = path;
        return this;
    }
}
