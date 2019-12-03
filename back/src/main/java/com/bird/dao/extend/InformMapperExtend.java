package com.bird.dao.extend;

import com.bird.model.entity.Inform;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 公告dao扩展
 *
 * @author zhyyy
 **/
@Mapper
public interface InformMapperExtend {
    /**
     * 获取系统公告的简要信息
     *
     * @param status    状态
     * @param title     标题，模糊查询
     * @param creator   创建人ID
     * @param topFirst  是否优先置顶
     * @param startDate 创建日期开始
     * @param endDate   创建日期结束
     * @return 公告列表
     */
    @Select("<script>" +
            "  select id, title, creator, create_date, canceler, cancel_date, outdate_operator, outdate_date, top, status from T_INFORM " +
            "  where 1 = 1 " +
            "  <if test='status!=null'>" +
            "    and status = #{status}" +
            "  </if>" +
            "  <if test='title!=null'>" +
            "    and title like '%#{title}%'" +
            "  </if>" +
            "  <if test='creator!=null'>" +
            "    and creator = #{creator}" +
            "  </if>" +
            "  <if test='startDate!=null'>" +
            "    and create_time&gt;=#{startDate}" +
            "  </if>" +
            "  <if test='endDate!=null'>" +
            "    and create_time&lt;#{endDate}" +
            "  </if>" +
            "  order by" +
            "  <if test='topFirst!=null and topFirst==true'>" +
            "    top desc, " +
            "  </if>" +
            "  create_date desc" +
            "</script>")
    List<Inform> selectSimple(@Param("status") Short status,
                              @Param("title") String title,
                              @Param("creator") Integer creator,
                              @Param("topFirst") Boolean topFirst,
                              @Param("startDate") Date startDate,
                              @Param("endDate") Date endDate);
}
