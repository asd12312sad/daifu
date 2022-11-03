package com.ms.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * oss上传文件的回调结果
 * Created by macro on 2018/5/17.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OssCallbackResult {
    private String filename;
    private String size;
    private String mimeType;
    private String width;
    private String height;
}
