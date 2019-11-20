package com.bird.dao.extend;

import com.bird.model.entity.Attachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zhyyy
 **/
@Mapper
public interface AttachmentMapperExtend {
    /**
     * 按编号获取文件信息
     *
     * @param sequence 文件编号
     * @return 文件信息
     */
    @Select("select * from T_ATTACHMENT where sequence = #{sequence}")
    Attachment selectBySequence(@Param("sequence") String sequence);


    /**
     * 按ID获取文件名称
     *
     * @param ids ID
     * @return ID-fileName
     */
    @Select("<script>" +
            "  select id, file_name, file_size from T_ATTACHMENT " +
            "  where id in " +
            "    <foreach collection='ids' item='id' open='(' close=')' separator=','>" +
            "      #{id}" +
            "    </foreach>" +
            "</script>")
    List<Attachment> selectNamesByIds(@Param("ids") List<String> ids);
}
