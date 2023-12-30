package com.jsh.erp.controller;

import com.jsh.erp.datasource.entities.ProductSupplierVo4Info;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.service.productSupplier.ProductSupplierService;
import com.jsh.erp.utils.BaseResponseInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/productSupplier")
@Api(tags = {"客商档案"})
public class ProductSupplierController {
    @Resource
    private ProductSupplierService productSupplierService;

    @GetMapping(value = "/exportExcel")
    @ApiOperation(value = "生成excel表格")
    public void exportExcel(@RequestParam(value = "supplierId", required = false) String supplierId,
                            @RequestParam(value = "keyword", required = false) String keyword,
                            HttpServletRequest request, HttpServletResponse response) {

    }

    @PostMapping(value = "/importExcel")
    @ApiOperation(value = "excel表格导入")
    public BaseResponseInfo importExcel(MultipartFile file,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            res = productSupplierService.importExcel(file, request);
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

    @GetMapping(value = "/getProductSupplierList")
    @ApiOperation(value = "获取供应商档案信息")
    public BaseResponseInfo getProductSupplierList(@RequestParam("name") String name,
                                                   @RequestParam("barCode") String barCode,
                                                   HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<>();
        try {
            List<ProductSupplierVo4Info> list = productSupplierService.getProductSupplierList(name, barCode);
            map.put("rows", list);
            map.put("total", list.size());
            res.code = 200;
            res.data = map;
        } catch (Exception e) {
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }
}
