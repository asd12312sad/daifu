package com.hjh.web;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.*;
import com.hjh.common.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * oss上传管理Service实现类
 * Created by macro on 2018/5/17.
 */
@Service
public class OssServiceImpl implements OssService {
    @Autowired
    private OSSClient ossClient;

    public Map<String, String> getOssSts() {


        String endpoint = "sts.aliyuncs.com";
        String accessKeyId = "*";
        String accessKeySecret = "*";
        String roleArn = "*";
        String roleSessionName = "*";
        String policy = null;
        try {
            // 添加endpoint（直接使用STS endpoint，前两个参数留空，无需添加region ID）
            DefaultProfile.addEndpoint("", "", "Sts", endpoint);
            // 构造default profile（参数留空，无需添加region ID）
            IClientProfile profile = DefaultProfile.getProfile("", accessKeyId, accessKeySecret);
            // 用profile构造client
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setMethod(MethodType.POST);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            // 若policy为空，则用户将获得该角色下所有权限
            request.setPolicy(policy);
            // 设置凭证有效时间
            request.setDurationSeconds(3600L);
            final AssumeRoleResponse response = client.getAcsResponse(request);

            Map<String, String> aliMap = new HashMap<>();
            aliMap.put("Expiration", response.getCredentials().getExpiration());
            aliMap.put("AccessKeyId", response.getCredentials().getAccessKeyId());
            aliMap.put("AccessKeySecret", response.getCredentials().getAccessKeySecret());
            aliMap.put("SecurityToken", response.getCredentials().getSecurityToken());
            aliMap.put("StatusCode", "200");

            return aliMap;
        } catch (ClientException e) {
            System.out.println(e.getMessage());
            return new HashMap<>();
        }

    }

    // 文件类型
    public static String IMG_TYPE_PNG = "PNG";
    public static String IMG_TYPE_JPG = "JPG";
    public static String IMG_TYPE_JPEG = "JPEG";
    public static String IMG_TYPE_DMG = "BMP";
    public static String IMG_TYPE_GIF = "GIF";
    public static String IMG_TYPE_SVG = "SVG";
    public static String IMG_TYPE_IMAGE = "IMAGE";

    public String uploadImg(MultipartFile file) {

        String filename = file.getOriginalFilename();

        long size = file.getSize();
        System.out.println(size);
        if (size > 10485760) {
            return null;
        }

        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);

        filename = IdUtils.fastSimpleUUID() + filename;


        byte[] uploadBytes = getBytesWithMultipartFile(file);

// 创建OSSClient实例。


        ObjectMetadata objectMetadata = new ObjectMetadata();
// 上传Byte数组。
        ossClient.putObject("*", filename, new ByteArrayInputStream(uploadBytes), objectMetadata);

        return "*" + filename;
    }


    public String deleteObject(String url) {
        String str1 = "*";
        url = url.replaceAll(str1, "");
        // 删除文件。如需删除文件夹，请将ObjectName设置为对应的文件夹名称。如果文件夹非空，则需要将文件夹下的所有object删除后才能删除该文件夹。
        ossClient.deleteObject("*", url);

        return null;
    }

    ;

    private static byte[] getBytesWithMultipartFile(MultipartFile multipartFile) {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
