package com.ms.agent.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ms.agent.domain.MerchantPayOrder;
import com.ms.agent.service.IMerchantPayOrderService;
import com.ms.common.controller.BaseController;
import com.ms.common.domain.R;
import com.ms.common.utils.CommonRequestHolder;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;


/**
 * 商户代付记录Controller
 *
 * @author xiaobing
 * @date 2022-06-10
 */
@RestController
@RequestMapping("/payOrder")
public class MerchantPayOrderController extends BaseController {

    @Autowired
    private IMerchantPayOrderService merchantPayOrderService;



    /**
     * 查询商户代付记录列表
     */
    @GetMapping("/list")
    public R<IPage<MerchantPayOrder>> list(MerchantPayOrder merchantPayOrder, @RequestParam Integer pageSize, @RequestParam Integer pageNumber) {

        IPage<MerchantPayOrder> page = new Page<>(pageNumber, pageSize);
        merchantPayOrder.setAgentId(CommonRequestHolder.getCurrentUserId().intValue());
        page = merchantPayOrderService.selectMerchantPayOrderList(page, merchantPayOrder);

        return R.success(page);
    }

    @GetMapping("/export")
    public void export(MerchantPayOrder merchantPayOrder, HttpServletResponse response) {
        merchantPayOrder.setAgentId(CommonRequestHolder.getCurrentUserId().intValue());

        List<MerchantPayOrder> page = merchantPayOrderService.selectMerchantPayOrderExport(merchantPayOrder);

        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("代付订单", "代付订单"),
                MerchantPayOrder.class, page);
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

    /**
     * 获取商户代付记录详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        return R.success(merchantPayOrderService.getById(id));
    }








}
