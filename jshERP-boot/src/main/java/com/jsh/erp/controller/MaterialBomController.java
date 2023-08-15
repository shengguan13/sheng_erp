package com.jsh.erp.controller;

import com.jsh.erp.datasource.entities.MaterialBom;
import com.jsh.erp.service.materialBom.MaterialBomService;
import com.jsh.erp.utils.BaseResponseInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/materialBom")
@Api(tags = {"BOM管理"})
public class MaterialBomController {
    @Resource
    private MaterialBomService materialBomService;

    @GetMapping(value = "/findComposite")
    @ApiOperation(value = "找到BOM中所有子部件")
    public BaseResponseInfo findComposite(@RequestParam("process") String process,
                                          @RequestParam("project") String project,
                                          HttpServletRequest request) throws Exception{
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<MaterialBom> list = materialBomService.selectByPrefix(process, project);
            res.code = 200;
            res.data = list;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }
}
