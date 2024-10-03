package com.jsh.erp.controller;

import com.jsh.erp.datasource.entities.DepotAllocation;
import com.jsh.erp.datasource.entities.DepotAllocationVo4Depot;
import com.jsh.erp.datasource.vo.DepotItemVoBatchNumberList;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.service.depotAllocation.DepotAllocationService;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.utils.BaseResponseInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
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

    @GetMapping(value = "/exportExcel")
    @ApiOperation(value = "生成excel表格")
    public void exportExcel(@RequestParam(value = "depotName", required = false) String depotName,
                            @RequestParam(value = "allocation", required = false) String allocation,
                            @RequestParam(value = "type", required = false) String type,
                            HttpServletRequest request, HttpServletResponse response) {

    }

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
}
