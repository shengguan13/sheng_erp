package com.jsh.erp.controller;

import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.service.depotAllocation.DepotAllocationService;
import com.jsh.erp.utils.BaseResponseInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/depotAllocation")
@Api(tags = {"货位管理"})
public class DepotAllocationController {
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
            e.printStackTrace();
        }
        return res;
    }
}
