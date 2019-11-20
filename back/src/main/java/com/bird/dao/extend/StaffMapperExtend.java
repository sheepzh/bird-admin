package com.bird.dao.extend;

import com.bird.model.entity.Staff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zhyyy
 **/
@Mapper
public interface StaffMapperExtend {
    /**
     * 按条件查找，排除root用户
     *
     * @param name    用户名，模糊查询
     * @param account 账号，模糊查询
     * @param roles   所拥有的角色
     * @param status  状态
     * @return 用户列表
     */
    @Select({"<script>",
            "  select * from T_STAFF where account != 'root'",
            "    <if test='name!=null'> ",
            "      and name like '%${name}%'",
            "    </if>",
            "    <if test='account!=null'> ",
            "      and account like '%${account}%'",
            "    </if>",
            "    <if test='status!=null'>",
            "      and status = #{status}",
            "    </if>",
            "    <if test='roles!=null and roles.size()&gt;0'>",
            "      and",
            "      <foreach collection='roles' item='role' separator='and ' open='(' close=')'>",
            "        exists( select 1 from T_STAFF_ROLE where staff_id = id and role_id = #{role})",
            "      </foreach>",
            "    </if>",
            "</script>"})
    List<Staff> select(@Param("name") String name,
                       @Param("account") String account,
                       @Param("roles") List<Integer> roles,
                       @Param("status") Integer status);

    /**
     * 按账号查找
     *
     * @param account 账号
     * @return 用户
     */
    @Select("select * from T_STAFF where account = #{account}")
    Staff selectByAccount(@Param("account") String account);

    /**
     * 按ID获取用户昵称
     *
     * @param id ID
     * @return 昵称
     */
    @Select("select name from T_STAFF where id = #{id}")
    String selectNameById(@Param("id") Integer id);
}
