package com.zhiliao.common.upload.bean;

import io.swagger.annotations.ApiModelProperty;

public class UploadBean {

    @ApiModelProperty("附件地址")
     private String fileUrl;


    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

}
