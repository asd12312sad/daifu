package com.ms.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ms.common.controller.BaseController;
import com.ms.common.domain.R;
import com.ms.common.domain.TbVersion;
import com.ms.common.utils.ServletUtils;
import com.ms.student.domain.Popup;
import com.ms.student.domain.SystemInfo;
import com.ms.student.mapper.PopupMapper;
import com.ms.student.mapper.SystemInfoMapper;
import com.ms.student.mapper.TbVersionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 肖兵
 * @version v1.0.0
 * @date 2021/1/6
 * 历史版本 Modification History:
 * Date                 Author          Version          Description
 * ---------------------------------------------------------------------------------*
 * 2021/1/6              肖兵             v1.0.0           Created
 */
@RestController
@RequestMapping("/version")
public class VersionController extends BaseController {

    @Autowired
    private TbVersionMapper versionMapper;

    @Autowired
    private PopupMapper popupMapper;

    @Autowired
    private SystemInfoMapper systemInfoMapper;


    /**
     * 获取当前版本号
     */
    @GetMapping("version")
    public R<List<TbVersion>> info( @RequestParam(required = false) Integer deviceSide) {
        QueryWrapper<TbVersion> tbVersionQueryWrapper = new QueryWrapper<>();
        if (!(deviceSide == null)) {
            tbVersionQueryWrapper = tbVersionQueryWrapper.eq("device_side", deviceSide);
        }
        List<TbVersion> version = versionMapper.selectList(tbVersionQueryWrapper);
        return R.success(version);
    }

    /**
     * 获取当前版本号
     */
    @GetMapping("systemInfo")
    public R<SystemInfo> systemInfo() {
        List<SystemInfo> systemInfoList = systemInfoMapper.selectList(new QueryWrapper<>());
        if (systemInfoList.size() > 0) {
            return R.success(systemInfoList.get(0));
        }
        return R.failed("没有系统信息");
    }


    /**
     * 获取弹窗
     */
    @GetMapping("popup")
    public R<Popup> popup() {
        HttpServletRequest request = ServletUtils.getRequest();

        String lang = request.getHeader("lang");

        if (lang == null) {
            return R.success();
        }

        Popup popup = popupMapper.selectOne(new QueryWrapper<Popup>().eq("lang", lang).eq("popup_flag", 1).last(" limit 1"));
        return popup == null ? R.success() : R.success(popup);
    }
}
