package com.jsh.erp.controller;

import com.jsh.erp.datasource.entities.MaterialBomVo4Info;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.service.materialBom.MaterialBomService;
import com.jsh.erp.utils.BaseResponseInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(value = "/materialBom")
@Api(tags = {"BOM管理"})
public class MaterialBomController {
    @Resource
    private MaterialBomService materialBomService;

    @GetMapping(value = "/getMaterialBomTree")
    @ApiOperation(value = "找到BOM中所有子部件")
    public BaseResponseInfo getMaterialBomTree(@RequestParam(value = "parent", required = false) String parent,
                                               @RequestParam(value = "upper", required = false) String upper,
                                               @RequestParam(value = "barCode") String barCode,
                                               @RequestParam(value = "project") String project,
                                               HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<MaterialBomVo4Info> list = materialBomService.getMaterialBomTree(parent, barCode, project);
            res.code = 200;
            res.data = list;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    @GetMapping(value = "/addBomChild")
    @ApiOperation(value = "新增BOM条目")
    public BaseResponseInfo addBomChild(@RequestParam(value = "parent") String parent,
                                        @RequestParam(value = "upper") String upper,
                                        @RequestParam(value = "project") String project,
                                        HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            materialBomService.addBomChild(parent, upper, project, request);
            res.code = 200;
            res.data = "导入成功";
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = e.getMessage();
        }
        return res;
    }

    @GetMapping(value = "/selectMaterialBomWithUpper")
    @ApiOperation(value = "查找")
    public BaseResponseInfo selectMaterialBomWithUpper(@RequestParam(value = "parent", required = false) String parent,
                                                       @RequestParam(value = "upper", required = false) String upper,
                                                       @RequestParam(value = "project", required = false) String project,
                                                       @RequestParam(value = "materialParam", required = false) String materialParam,
                                                       HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<MaterialBomVo4Info> list = materialBomService.selectMaterialBomWithUpper(parent, upper, project, materialParam);
            res.code = 200;
            res.data = list;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    @GetMapping(value = "/exportExcel")
    @ApiOperation(value = "生成excel表格")
    public void exportExcel(@RequestParam(value = "categoryId", required = false) String categoryId,
                            @RequestParam(value = "materialParam", required = false) String materialParam,
                            @RequestParam(value = "color", required = false) String color,
                            @RequestParam(value = "project", required = false) String project,
                            @RequestParam(value = "weight", required = false) String weight,
                            @RequestParam(value = "expiryNum", required = false) String expiryNum,
                            @RequestParam(value = "enabled", required = false) String enabled,
                            @RequestParam(value = "remark", required = false) String remark,
                            @RequestParam(value = "mpList", required = false) String mpList,
                            HttpServletRequest request, HttpServletResponse response) {

    }

    @PostMapping(value = "/importExcel")
    @ApiOperation(value = "excel表格导入产品")
    public BaseResponseInfo importExcel(MultipartFile file,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            res = materialBomService.importExcel(file, request);
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
