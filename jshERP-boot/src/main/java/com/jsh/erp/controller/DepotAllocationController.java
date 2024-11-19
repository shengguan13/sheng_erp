package com.jsh.erp.controller;

import com.jsh.erp.datasource.entities.DepotAllocation;
import com.jsh.erp.datasource.entities.DepotAllocationVo4Depot;
import com.jsh.erp.datasource.entities.DepotItemVo4WithInfoEx;
import com.jsh.erp.datasource.vo.DepotHeadVo4List;
import com.jsh.erp.datasource.vo.DepotItemVoBatchNumberList;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.depotAllocation.DepotAllocationService;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.utils.BaseResponseInfo;
import com.jsh.erp.utils.ExcelUtils;
import com.jsh.erp.utils.ExportExecUtil;
import com.jsh.erp.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/depotAllocation")
@Api(tags = {"货位管理"})
public class DepotAllocationController {
    private Logger logger = LoggerFactory.getLogger(DepotAllocationController.class);

    @Resource
    private LogService logService;
    @Resource
    private DepotAllocationService depotAllocationService;

    @PostMapping(value = "/importExcel")
    @ApiOperation(value = "excel表格导入产品")
    public BaseResponseInfo importExcel(MultipartFile file,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            res = depotAllocationService.importExcel(file, request);
        } catch (BusinessRunTimeException e) {
            BaseResponseInfo info = new BaseResponseInfo();
            info.code = e.getCode();
            info.data = e.getMessage();
            return info;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 获取批次产品列表信息
     * @param request
     * @return
     */
    @GetMapping(value = "/getAllocationList")
    @ApiOperation(value = "获取货位信息")
    public BaseResponseInfo getAllocationList(@RequestParam("name") String name,
                                              @RequestParam("type") String type,
                                              @RequestParam("currentPage") Integer currentPage,
                                              @RequestParam("pageSize") Integer pageSize,
                                              HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<>();
        try {
            List<DepotAllocationVo4Depot> list = depotAllocationService.select(null, type, name, (currentPage-1)*pageSize, pageSize);
            Long count = depotAllocationService.countDepotAllocation(null, type, name);
            if (list.size() <= 30) {
                for (DepotAllocationVo4Depot allocation : list) {
                    allocation.setTotalNumber(depotAllocationService.getAllocationSum(allocation.getId()));
                }
            }
            map.put("rows", list);
            map.put("total", count);
            res.code = 200;
            res.data = map;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    @GetMapping(value = "/getAllocationDetail")
    @ApiOperation(value = "获取货位明细")
    public BaseResponseInfo getAllocationDetail(@RequestParam("allocationId") Long allocationId,
                                                HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<>();
        try {
            List<DepotItemVoBatchNumberList> list = depotAllocationService.getAllocationDetail(allocationId);
            map.put("rows", list);
            map.put("total", list.size());
            res.code = 200;
            res.data = map;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    @GetMapping(value = "/getDepotAllocation")
    @ApiOperation(value = "获取货位")
    public BaseResponseInfo getDepotAllocation(@RequestParam("id") Long id,
                                               HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            DepotAllocation allocation = depotAllocationService.getDepotAllocation(id);
            res.code = 200;
            res.data = allocation;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    @GetMapping(value = "/exportAllocationReportExcel")
    @ApiOperation(value = "生成excel表格")
    public void exportExcel(@RequestParam(value = "depotId", required = false) String depotId,
                            @RequestParam(value = "allocation", required = false) String allocation,
                            @RequestParam(value = "type", required = false) String type,
                            @RequestParam(value = "currentPage", required = false) String currentPage,
                            @RequestParam(value = "pageSize", required = false) String pageSize,
                            HttpServletRequest request, HttpServletResponse response) {
        try {
            int curr = Integer.parseInt(currentPage);
            int size = Integer.parseInt(pageSize);
            logger.info("XXXXX-" + depotId + "-" + allocation + "-" + type + "-" + currentPage + "-" + pageSize);
            List<DepotAllocationVo4Depot> allocationList = depotAllocationService.select(depotId, type, allocation, (curr-1)*size, size);
            logger.info("XXXXX count " + (allocationList == null ? 0 : allocationList.size()));
            String[] names = {"货位", "货位分类", "仓库", "编码", "名称", "型号", "批号", "数量", "单位"};
            String title = "货位明细";
            List<String[]> objects = new ArrayList<>();
            if (null != allocationList) {
                for (DepotAllocationVo4Depot all : allocationList) {
                    List<DepotItemVoBatchNumberList> itemList = depotAllocationService.getAllocationDetail(all.getId());
                    if (null != itemList) {
                        for (DepotItemVoBatchNumberList item : itemList) {
                            if (item.getTotalNum() != null && item.getTotalNum().compareTo(BigDecimal.ZERO) != 0) {
                                String[] objs = new String[10];
                                objs[0] = all.getAllocation();
                                objs[1] = all.getType();
                                objs[2] = all.getDepotName();
                                objs[3] = item.getBarCode();
                                objs[4] = item.getName();
                                objs[5] = item.getModel();
                                objs[6] = item.getBatchNumber();
                                objs[7] = item.getTotalNum().toString();
                                objs[8] = item.getCommodityUnit();
                                objects.add(objs);
                            }
                        }
                    }
                }
            }
            File file = ExcelUtils.exportObjectsWithoutTitle(title, names, title, objects);
            ExportExecUtil.showExec(file, file.getName(), response);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
    }
}
