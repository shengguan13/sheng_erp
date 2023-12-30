package com.jsh.erp.controller;

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
}
