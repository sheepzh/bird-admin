package com.bird.dao.extend;

import com.bird.model.entity.SystemRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


/**
 * 角色DAO扩展
 * @author zhyyy
 **/
@Mapper
public interface RoleMapperExtend {
    /**
     * 通过名称获取角色
     *
     * @param name 角色名称
     * @return 角色信息
     */
    @Select("select * from T_SYSTEM_ROLE where name = #{name}")
    SystemRole selectByName(@Param("name") String name);
}
