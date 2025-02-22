package com.jsh.erp.controller;

import com.jsh.erp.datasource.entities.MaterialBomVo4Info;
import com.jsh.erp.datasource.entities.MaterialVo4Unit;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.service.materialBom.MaterialBomService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/materialBom")
@Api(tags = {"BOM管理"})
public class MaterialBomController {
    private Logger logger = LoggerFactory.getLogger(MaterialBomController.class);

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
                            @RequestParam(value = "project", required = false) String project,
                            HttpServletRequest request, HttpServletResponse response) {
        try {
            List<MaterialBomVo4Info> bomList = materialBomService.select(
                    StringUtil.toNull(categoryId), null, StringUtil.toNull(project), StringUtil.toNull(materialParam), 0, 300);
            logger.info("XXXXX bomList size " + bomList.size());
            String[] names = {"总成编号", "级别", "客/供零件号", "物料编码", "型号", "名称", "规格", "颜色", "颜色代码", "材料", "材料标准", "物料来源", "状态", "用量", "单位", "类别", "标包", "客户/供应商"};
            String title = project + " BOM";
            List<String[]> objects = new ArrayList<>();
            if (null != bomList) {
                for (int i = 0; i < bomList.size(); i++) {
                    MaterialBomVo4Info bom = bomList.get(i);
                    List<MaterialBomVo4Info> tree = materialBomService.getMaterialBomTree(bom.getParent(), bom.getBarCode(), bom.getProject());
                    flattenTree(tree, objects, i, 1);
                }
            }
            File file = ExcelUtils.exportObjectsWithoutTitle(title, names, title, objects);
            ExportExecUtil.showExec(file, file.getName(), response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void flattenTree(List<MaterialBomVo4Info> tree, List<String[]> objects, int parentCount, int level) {
        if (tree == null || tree.size() == 0) {
            return;
        }
        for (MaterialBomVo4Info bom : tree) {
            String[] objs = new String[100];
            objs[0] = String.valueOf(parentCount);
            objs[1] = String.valueOf(level);
            objs[2] = bom.getSupplierModel();
            objs[3] = bom.getBarCode();
            objs[4] = bom.getModel() == null ? "" : bom.getModel();
            objs[5] = bom.getName() == null ? "" : bom.getName();
            objs[6] = bom.getOtherField5() == null ? "" : bom.getOtherField5();
            objs[7] = bom.getColor() == null ? "" : bom.getColor();
            objs[8] = bom.getColorCode() == null ? "" : bom.getColorCode();
            objs[9] = bom.getMaterial() == null ? "" : bom.getMaterial();
            objs[10] = bom.getOtherField6() == null ? "" : bom.getOtherField6();
            objs[11] = bom.getOtherField7() == null ? "" : bom.getOtherField7();
            objs[12]= bom.getSource() == null ? "" : bom.getSource();
            objs[13] = bom.getProcessUsage() == null ? "" : bom.getProcessUsage().toString();
            objs[14] = bom.getmUnit() == null ? "" : bom.getmUnit();
            objs[15] = bom.getCategory() == null ? "" : bom.getCategory();
            objs[16] = "";
            objs[17] = bom.getSupplierName() == null ? "" : bom.getSupplierName();
            objects.add(objs);
            if (bom.getChildren() != null && bom.getChildren().size() > 0) {
                flattenTree(bom.getChildren(), objects, parentCount, level + 1);
            }
        }
    }
}
