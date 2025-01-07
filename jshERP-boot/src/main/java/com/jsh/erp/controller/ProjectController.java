package com.jsh.erp.controller;

import com.jsh.erp.datasource.entities.Project;
import com.jsh.erp.service.project.ProjectService;
import com.jsh.erp.utils.BaseResponseInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/project")
@Api(tags = {"项目管理"})
public class ProjectController {
    private Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Resource
    private ProjectService projectService;

    /**
     * 全部数据列表
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/getAllProject")
    @ApiOperation(value = "全部数据列表")
    public BaseResponseInfo getAllProject(HttpServletRequest request)throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            List<Project> projectList = projectService.getAllProject();
            map.put("projectList", projectList);
            res.code = 200;
            res.data = projectList;
        } catch(Exception e){
            e.printStackTrace();
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }
}
