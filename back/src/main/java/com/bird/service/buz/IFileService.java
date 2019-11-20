package com.bird.service.buz;

import com.bird.model.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;

/**
 * 系统附件管理服务，默认将附件存储本地（服务器或nas）
 * 可使用云对象存储进行扩展
 *
 * @author zhyyy
 * @see com.bird.service.buz.impl.LocalFileServiceImpl
 **/
public interface IFileService {
    /**
     * 状态：正常
     */
    Short FILE_EXISTING_ON_DISK = 1;

    /**
     * 状态：已删除
     */
    Short FILE_DELETED_ON_DISK = 2;

    /**
     * 上传文件
     *
     * @param file 文件
     * @param user 上传者ID
     * @return 文件ID
     */
    Integer uploadFile(MultipartFile file, Integer user);

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @param user   删除者ID
     */
    void deleteFile(Integer fileId, Integer user);

    /**
     * 按主键获取
     *
     * @param fileId 文件id
     * @return 文件信息
     */
    Attachment getById(Integer fileId);
}
