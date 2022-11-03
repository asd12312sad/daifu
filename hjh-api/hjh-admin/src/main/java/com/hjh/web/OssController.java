package com.hjh.web;


import com.hjh.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 回访
 * Oss相关操作接口
 * Created by macro on 2018/4/26.
 */
@RestController
@RequestMapping("/aliyun/oss")
public class OssController {
    @Autowired
    private OssServiceImpl ossService;

    @RequestMapping(value = "/getOssSts", method = RequestMethod.GET)
    public Map<String, String> getOssSts() {
        return ossService.getOssSts();
    }


    /**
     * 上传图片OssServiceImpl
     */
    @PostMapping("/submit/file")
    public AjaxResult<Map<String, String>> submitImg(@RequestParam("file") MultipartFile file) {
        System.out.println("上传图片:" + file.getName());
        String o = ossService.uploadImg(file);
        Map<String, String> idAndValueVo = new HashMap<>();
        idAndValueVo.put("data", o);

        return AjaxResult.success(idAndValueVo);
    }
//    @RequestMapping(value = "callback")
//    @ResponseBody
//    public R<OssCallbackResult> callback(HttpServletRequest request) {
//        OssCallbackResult ossCallbackResult = ossService.callback(request);
//        return R.success(ossCallbackResult);
//    }

}
