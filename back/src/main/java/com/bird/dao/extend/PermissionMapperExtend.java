package com.bird.dao.extend;

import com.bird.model.entity.SystemPermissionNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zhyyy
 **/
@Mapper
public interface PermissionMapperExtend {
    /**
     * 获取组件内的所有权限点
     *
     * @param module 组件
     * @return 权限
     */
    @Select("select * from T_SYSTEM_PERMISSION_NODE where module = #{module}")
    List<SystemPermissionNode> listByModule(@Param("module") String module);

    /**
     * 获取权限点关联的角色
     *
     * @param nodeId 权限点ID
     * @return 角色名称列表
     */
    @Select("select t1.name from T_SYSTEM_ROLE t1, T_SYSTEM_PERMISSION_ROLE t2 " +
            "where t2.role_id = t1.id and t2.permission_id = #{nodeId}")
    List<String> listRoleNamesByPermission(@Param("nodeId") int nodeId);
}
