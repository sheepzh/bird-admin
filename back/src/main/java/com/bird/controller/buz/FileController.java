package com.bird.controller.buz;

import com.bird.common.BirdOutException;
import com.bird.controller.BaseController;
import com.bird.controller.UserLog;
import com.bird.model.entity.Attachment;
import com.bird.model.entity.Staff;
import com.bird.service.buz.IFileService;
import com.bird.service.system.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * 附件Controller
 *
 * @author zhyyy
 **/
@RestController
public class FileController extends BaseController {
    private final ITokenService tokenService;
    private final IFileService fileService;

    @Autowired
    public FileController(ITokenService tokenService, IFileService fileService) {
        this.tokenService = tokenService;
        this.fileService = fileService;
    }

    /**
     * @param file  文件
     * @param request request
     * @return response
     */
    @RequestMapping(value = "/file/upload", method = POST)
    public Object upload(@RequestParam("file") MultipartFile file,
                         HttpServletRequest request) {
        return responseWrap(() -> {
            Staff user = tokenService.map2User(request);
            return fileService.uploadFile(file, user.getId());
        });
    }

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @param request request
     * @return response
     */
    @UserLog("删除文件")
    @RequestMapping(value = "/file/{id}", method = DELETE)
    public Object delete(@PathVariable("id") Integer fileId,
                         HttpServletRequest request) {
        return responseWrap(r -> {
            Staff user = tokenService.map2User(request);
            fileService.deleteFile(fileId, user.getId());
        });
    }

    /**
     * 下载文件
     *
     * @param response 下载人token
     * @param fileId   文件ID
     */
    @RequestMapping(value = "/file/download/{id}", method = GET)
    public void downloadFile(HttpServletResponse response, @PathVariable("id") Integer fileId) {
        Attachment attachment = fileService.getById(fileId);
        if (attachment == null) {
            response.setStatus(404);
            return;
        }
        String filePath = attachment.getFilePath();
        String fileName = attachment.getSequence();
        File file = new File(filePath);
        if (file.exists()) {
            // 强制设置下载而不是打开
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
            byte[] buffer = new byte[1024];
            try (
                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis)
            ) {
                OutputStream outputStream = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    outputStream.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                response.setStatus(404);
                throw new BirdOutException("下载失败");
            }
        } else {
            response.setStatus(404);
        }
    }
}
