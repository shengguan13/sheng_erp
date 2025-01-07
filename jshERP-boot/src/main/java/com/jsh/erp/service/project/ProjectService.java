package com.jsh.erp.service.project;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.AccountHeadMapperEx;
import com.jsh.erp.datasource.mappers.DepotHeadMapperEx;
import com.jsh.erp.datasource.mappers.ProjectMapper;
import com.jsh.erp.datasource.mappers.ProjectMapperEx;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.service.supplier.SupplierService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {
    private Logger logger = LoggerFactory.getLogger(ProjectService.class);

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private ProjectMapperEx projectMapperEx;
    @Resource
    private UserService userService;
    @Resource
    private LogService logService;
    @Resource
    private SupplierService supplierService;
    @Resource
    private AccountHeadMapperEx accountHeadMapperEx;
    @Resource
    private DepotHeadMapperEx depotHeadMapperEx;

    public Project getProject(long id)throws Exception {
        Project result=null;
        try{
            result=projectMapper.selectByPrimaryKey(id);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<Project> getProjectListByIds(String ids)throws Exception {
        List<Long> idList = StringUtil.strToLongList(ids);
        List<Project> list = new ArrayList<>();
        try{
            ProjectExample example = new ProjectExample();
            example.createCriteria().andIdIn(idList);
            list = projectMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<Project> select(String name, String type, String customer, int offset, int rows)throws Exception {
        List<Project> list=null;
        try{
            list=projectMapperEx.selectByConditionProject(name, type, customer, offset, rows);
            for (Project project : list) {
                try {
                    if (project.getUsers() != null && !"".equals(project.getUsers())) {
                        String[] userArr = project.getUsers().split(",");
                        StringBuilder sb = new StringBuilder();
                        for (String user : userArr) {
                            sb.append(userService.getUser(Long.valueOf(user)).getUsername() + ",");
                        }
                        String usersStr = sb.toString();
                        project.setUsersStr(usersStr.substring(0, usersStr.length() - 1));
                    }
                    if (project.getCustomer() != null) {
                        Supplier supplier = supplierService.getSupplier(project.getCustomer());
                        project.setCustomerStr(supplier.getSupplier());
                    }
                }catch (Exception e) {
                }
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public Long countProject(String name, String type, String customer)throws Exception {
        Long result=null;
        try{
            result=projectMapperEx.countsByProject(name, type, customer);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertProject(JSONObject obj, HttpServletRequest request)throws Exception {
        Project project = JSONObject.parseObject(obj.toJSONString(), Project.class);
        int result=0;
        try{
            result=projectMapper.insertSelective(project);
            logService.insertLog("项目",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(project.getName()).toString(), request);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateProject(JSONObject obj, HttpServletRequest request)throws Exception {
        Project project = JSONObject.parseObject(obj.toJSONString(), Project.class);
        int result=0;
        try{
            result=projectMapper.updateByPrimaryKeySelective(project);
            logService.insertLog("项目",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(project.getName()).toString(), request);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteProject(Long id, HttpServletRequest request)throws Exception {
        return batchDeleteProjectByIds(id.toString());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteProject(String ids, HttpServletRequest request) throws Exception{
        return batchDeleteProjectByIds(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteProjectByIds(String ids)throws Exception {
        int result =0;
        String [] idArray=ids.split(",");
        //删除项目
        try{
            result=projectMapperEx.batchDeleteProjectByIds(idArray);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    public int checkIsNameExist(Long id, String name) throws Exception{
        ProjectExample example = new ProjectExample();
        example.createCriteria().andIdNotEqualTo(id).andNameEqualTo(name).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<Project> list =null;
        try{
            list=projectMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list==null?0:list.size();
    }

    public List<Project> getAllProject()throws Exception {
        ProjectExample example = new ProjectExample();
        example.createCriteria().andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        example.setOrderByClause("id desc");
        List<Project> list =null;
        try{
            list=projectMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }
}
