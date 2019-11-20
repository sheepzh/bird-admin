package com.bird.model.vo;

import com.bird.model.entity.Attachment;
import com.bird.model.entity.Inform;

import java.util.List;

/**
 * @author zhyyy
 **/
public class InformVo extends Inform {

    /**
     * 添加公告时，带附件ID
     */
    private List<Integer> attachments;

    /**
     * 查找公告时，带附件信息
     */
    private List<Attachment> attachmentsToShow;

    /**
     * 查找公告时，带创建人ID
     */
    private String creatorName;

    public List<Integer> getAttachments() {
        return attachments;
    }

    public InformVo setAttachments(List<Integer> attachments) {
        this.attachments = attachments;
        return this;
    }

    public List<Attachment> getAttachmentsToShow() {
        return attachmentsToShow;
    }

    public InformVo setAttachmentsToShow(List<Attachment> attachmentsToShow) {
        this.attachmentsToShow = attachmentsToShow;
        return this;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public InformVo setCreatorName(String creatorName) {
        this.creatorName = creatorName;
        return this;
    }
}
