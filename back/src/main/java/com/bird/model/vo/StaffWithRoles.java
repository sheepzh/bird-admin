package com.bird.model.vo;

import com.bird.model.entity.Staff;

import java.util.List;

/**
 * 新建用户时使用
 *
 * @author zhyyy
 **/
public class StaffWithRoles extends Staff {

    private List<Integer> roles;
    private String newPassword;

    public StaffWithRoles() {
    }

    public List<Integer> getRoles() {
        return roles;
    }

    public StaffWithRoles setRoles(List<Integer> roles) {
        this.roles = roles;
        return this;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public StaffWithRoles setNewPassword(String newPassword) {
        this.newPassword = newPassword;
        return this;
    }
}
