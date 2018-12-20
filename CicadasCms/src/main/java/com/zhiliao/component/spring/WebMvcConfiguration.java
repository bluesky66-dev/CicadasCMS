package com.zhiliao.component.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.File;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Value("${enable.virtual.path}")
    String enableVirtualPath;

    @Value("${windows.file.upload.path}")
    String windowsUploadPath;

    @Value("${linux.file.upload.path}")
    String linuxUploadPath;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath =
                enableVirtualPath.equals("true")
                        ?
                        (System.getProperty("os.name").toLowerCase().startsWith("win")
                                ?
                                "file:"+windowsUploadPath.replace("/", File.separator)+File.separator+"upload"+File.separator
                                :
                                "file:"+linuxUploadPath.replace("/", File.separator)+File.separator+"upload"+File.separator)
                        :
                        "classpath:static";
        registry.addResourceHandler("/upload/**").addResourceLocations(uploadPath);
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
    }

}
