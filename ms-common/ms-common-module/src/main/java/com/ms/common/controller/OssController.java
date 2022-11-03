package com.ms.common.controller;


import com.ms.common.domain.R;
import com.ms.common.utils.OssServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Oss相关操作接口
 * Created by macro on 2018/4/26.
 */
@RestController
@RequestMapping("/aliyun/oss")
public class OssController {
    @Autowired
    private OssServiceImpl ossService;

    @RequestMapping(value = "/getOssSts" , method = RequestMethod.GET)
    public Map<String, String> getOssSts() {
        return ossService.getOssSts();
    }


    /**
     * 上传图片OssServiceImpl
     */
    @PostMapping("/submit/img")
    public R<Map<String, String>> submitImg(@RequestParam("file") MultipartFile file) {
        System.out.println("上传图片:" + file.getName());
        String o = ossService.uploadImg(file);
        if (o == null) {
            return R.failed("请检查图片格式，图片大小不能超过10M！");
        }
        Map<String, String> idAndValueVo = new HashMap<>();
        idAndValueVo.put("data" , o);

        return R.success(idAndValueVo);
    }
//    @RequestMapping(value = "callback")
//    @ResponseBody
//    public R<OssCallbackResult> callback(HttpServletRequest request) {
//        OssCallbackResult ossCallbackResult = ossService.callback(request);
//        return R.success(ossCallbackResult);
//    }

}
