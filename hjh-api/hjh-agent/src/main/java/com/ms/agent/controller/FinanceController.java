package com.ms.agent.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.ms.agent.domain.Agent;
import com.ms.agent.pojo.vo.FinanceListVo;
import com.ms.agent.pojo.vo.SysSettingVO;
import com.ms.agent.service.AgentService;
import com.ms.agent.service.IMerchantPayOrderService;
import com.ms.agent.util.GoogleAuthenticator;
import com.ms.common.domain.R;
import com.ms.common.utils.CommonRequestHolder;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping(value = "/finance")
public class FinanceController {

    @Autowired
    private AgentService agentService;

    @Autowired
    private IMerchantPayOrderService merchantPayOrderService;

    @RequestMapping(value = "/list")
    public R list(FinanceListVo financeListVo) {
        return R.success(merchantPayOrderService.financeList(financeListVo));
    }



    @RequestMapping(value = "/info")
    public R<SysSettingVO> info() {
        SysSettingVO sysSettingVO = merchantPayOrderService.financeInfo();
        if (sysSettingVO.getCollectAmount()==null){
            sysSettingVO.setCollectAmount(new BigDecimal(0));
        }
        if (sysSettingVO.getPayAmount()==null){
            sysSettingVO.setPayAmount(new BigDecimal(0));
        }
        if (sysSettingVO.getProfitAmount()==null){
            sysSettingVO.setProfitAmount(BigDecimal.ZERO);
        }
        if (sysSettingVO.getOutstandingAmount()==null){
            sysSettingVO.setOutstandingAmount(BigDecimal.ZERO);
        }

        if (sysSettingVO.getSettledAmount()==null){
            sysSettingVO.setSettledAmount(BigDecimal.ZERO);
        }

        return R.success(sysSettingVO);
    }

    @RequestMapping(value = "/resetAddress")
    public R<SysSettingVO> resetAddress(@RequestParam String address,@RequestParam String googleSignCode) {

        Agent agent = agentService.getById(CommonRequestHolder.getCurrentUserId());
        Boolean authcode = GoogleAuthenticator.authcode(googleSignCode, agent.getGoogleSignCode());
        if (!authcode) {
            return R.failed(401, "验证码错误");
        }
        agent.setUsdtAddress(address);
        agentService.updateById(agent);
        return R.success();
    }


    @GetMapping("/export")
    public void export(FinanceListVo merchantPayOrder, HttpServletResponse response) {
        merchantPayOrder.setAgentId(CommonRequestHolder.getCurrentUserId().intValue());

        List<FinanceListVo> page = merchantPayOrderService.financeExpoit(merchantPayOrder);

        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("财务数据", "财务数据"),
                FinanceListVo.class, page);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("代付订单" + ".xlsx", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
