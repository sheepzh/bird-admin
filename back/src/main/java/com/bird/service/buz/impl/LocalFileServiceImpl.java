package com.bird.service.buz.impl;

import com.bird.common.BirdOutException;
import com.bird.dao.AttachmentMapper;
import com.bird.dao.extend.AttachmentMapperExtend;
import com.bird.helper.SequenceHelper;
import com.bird.model.entity.Attachment;
import com.bird.service.buz.IFileService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 将附件存在本地
 *
 * @author zhyyy
 **/
@Service
public class LocalFileServiceImpl implements IFileService {
    private final Logger LOG = Logger.getLogger(LocalFileServiceImpl.class);
    @Autowired
    private SequenceHelper sequenceHelper;
    @Autowired
    private AttachmentMapper attachmentMapper;
    @Autowired
    private AttachmentMapperExtend fileMapperExtend;

    @Value("${upload-file.path}")
    private String fileRoot;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Integer uploadFile(MultipartFile file, Integer user) {
        if (file.isEmpty()) {
            throw new BirdOutException("文件为空，请检查文件以及网络");
        }
        String filename = file.getOriginalFilename();
        String sequence = sequenceHelper.generate(Attachment.class, "sequence");
        String realFileName = sequence + "_" + filename;
        File target = new File(fileRoot + File.separator + realFileName);
        try {
            file.transferTo(target);
            Attachment attachment = new Attachment();
            attachment.setFileName(filename);
            attachment.setFilePath(target.getPath());
            attachment.setRealFileName(realFileName);
            attachment.setSequence(sequence);
            attachment.setStatus(FILE_EXISTING_ON_DISK);
            attachment.setUploadDate(new Date());
            attachment.setUploader(user);
            attachment.setFileSize(file.getSize());
            attachmentMapper.insertSelective(attachment);
            return fileMapperExtend.selectBySequence(sequence).getId();
        } catch (IOException e) {
            LOG.error(e);
            LOG.warn("文件处理失败：" + filename);
            throw new BirdOutException("文件处理失败");
        }
    }

    @Override
    public void deleteFile(Integer fileId, Integer user) {
        Attachment systemFile = attachmentMapper.selectByPrimaryKey(fileId);
        if (systemFile == null) {
            throw new BirdOutException("文件不存在");
        }
        if (FILE_DELETED_ON_DISK.equals(systemFile.getStatus())) {
            throw new BirdOutException("文件不已被删除");
        }
        systemFile.setDeleteDate(new Date());
        systemFile.setDeleter(user);
        systemFile.setStatus(FILE_DELETED_ON_DISK);
        attachmentMapper.updateByPrimaryKeySelective(systemFile);
        File file = new File(systemFile.getRealFileName());
        if (file.exists()) {
            if (!file.delete()) {
                throw new BirdOutException("删除失败");
            }
        }
        LOG.info("删除文件成功：" + systemFile.getFilePath());
    }

    @Override
    public Attachment getById(Integer fileId) {
        return attachmentMapper.selectByPrimaryKey(fileId);
    }
}
