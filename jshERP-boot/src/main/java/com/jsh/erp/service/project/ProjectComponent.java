package com.jsh.erp.service.project;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.service.ICommonQuery;
import com.jsh.erp.utils.Constants;
import com.jsh.erp.utils.QueryUtils;
import com.jsh.erp.utils.StringUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service(value = "project_component")
@ProjectResource
public class ProjectComponent implements ICommonQuery {
    @Resource
    private ProjectService projectService;

    @Override
    public Object selectOne(Long id) throws Exception {
        return projectService.getProject(id);
    }

    @Override
    public List<?> select(Map<String, String> map)throws Exception {
        return getProjectList(map);
    }

    private List<?> getProjectList(Map<String, String> map) throws Exception{
        String search = map.get(Constants.SEARCH);
        String name = StringUtil.getInfo(search, "name");
        String type = StringUtil.getInfo(search, "type");
        String customer = StringUtil.getInfo(search, "customer");
        return projectService.select(name, type, customer, QueryUtils.offset(map), QueryUtils.rows(map));
    }

    @Override
    public Long counts(Map<String, String> map) throws Exception{
        String search = map.get(Constants.SEARCH);
        String name = StringUtil.getInfo(search, "name");
        String type = StringUtil.getInfo(search, "type");
        String customer = StringUtil.getInfo(search, "customer");
        return projectService.countProject(name, type, customer);
    }

    @Override
    public int insert(JSONObject obj, HttpServletRequest request)throws Exception {
        return projectService.insertProject(obj, request);
    }

    @Override
    public int update(JSONObject obj, HttpServletRequest request)throws Exception {
        return projectService.updateProject(obj, request);
    }

    @Override
    public int delete(Long id, HttpServletRequest request)throws Exception {
        return projectService.deleteProject(id, request);
    }

    @Override
    public int deleteBatch(String ids, HttpServletRequest request)throws Exception {
        return projectService.batchDeleteProject(ids, request);
    }

    @Override
    public int checkIsNameExist(Long id, String name)throws Exception {
        return projectService.checkIsNameExist(id, name);
    }
}
