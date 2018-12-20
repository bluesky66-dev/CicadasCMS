package com.zhiliao.common.upload;


import com.zhiliao.common.exception.CmsException;
import com.zhiliao.common.upload.bean.UploadBean;
import com.zhiliao.common.utils.CmsUtil;
import com.zhiliao.common.utils.JsonUtil;
import com.zhiliao.module.web.system.service.AttachmentService;
import com.zhiliao.mybatis.model.TSysAttachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.net.URLEncoder;


@Controller
public class UploadController {

    @Autowired
    private UploadComponent uploadComponent;

    @Autowired
    private AttachmentService attachmentService;

    @RequestMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile multipartFile,
                         HttpServletRequest request){
        UploadBean result = uploadComponent.uploadFile(multipartFile,request);

        return JsonUtil.toUploadSUCCESS("上传成功！",result.getFileUrl());
    }


    @RequestMapping("/wangEditorUpload")
    @ResponseBody
    public String WangEditorUpload(@RequestParam("file") MultipartFile multipartFile,
                         HttpServletRequest request) {
        UploadBean result = uploadComponent.uploadFile(multipartFile,request);
        return result.getFileUrl();

    }

    @RequestMapping("/CKEditorUpload")
    @ResponseBody
    public String CKEditorUpload(@RequestParam("upload") MultipartFile multipartFile,
                                 HttpServletRequest request) {
        StringBuffer sb=new StringBuffer();
        UploadBean result = uploadComponent.uploadFile(multipartFile,request);
        sb.append("<script type=\"text/javascript\">");
        sb.append("window.parent.CKEDITOR.tools.callFunction("+ request.getParameter("CKEditorFuncNum") + ",'" +result.getFileUrl()+"','')");
        sb.append("</script>");
        return sb.toString();

    }

    @RequestMapping(value = "/res/{key}.{resType}")
    public void showAttr(@PathVariable(value = "key",required = false) String key,
                         @PathVariable(value = "resType",required = false) String resType,
                         HttpServletRequest request,HttpServletResponse response) {
        if(CmsUtil.isNullOrEmpty(key))return;
        TSysAttachment attachment = attachmentService.findByKey(key);
        if(CmsUtil.isNullOrEmpty(attachment)||!attachment.getFileName().contains(resType))
            throw new CmsException("文件不存在！");
        try {
            response.reset();
            /* 判断浏览器类型，设置文件下载名 */
            String userAgent = request.getHeader("user-agent").toLowerCase();
            if (userAgent.contains("msie") || userAgent.contains("like gecko")||userAgent.contains("trident")||userAgent.contains("edge")) {
                attachment.setOriginalFilename(URLEncoder.encode(attachment.getOriginalFilename(), "UTF-8"));
            } else {
                attachment.setOriginalFilename(new String(attachment.getOriginalFilename().getBytes("utf-8"), "ISO8859-1"));
            }
            response.setHeader("Content-disposition", "attachment;filename="+attachment.getOriginalFilename());
            response.setContentType(attachment.getFileExtname());
            FileCopyUtils.copy(new FileInputStream(uploadComponent.getUploadPath()+attachment.getFilePath()), response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
