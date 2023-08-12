package com.jsh.erp.service.materialBom;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.MaterialBom;
import com.jsh.erp.datasource.entities.MaterialBomExample;
import com.jsh.erp.datasource.mappers.MaterialBomMapper;
import com.jsh.erp.datasource.mappers.MaterialBomMapperEx;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class MaterialBomService {
    private Logger logger = LoggerFactory.getLogger(MaterialBomService.class);

    @Resource
    private LogService logService;

    @Resource
    private MaterialBomMapper materialBomMapper;

    @Resource
    private MaterialBomMapperEx materialBomMapperEx;

    public MaterialBom getMaterialBom(long id)throws Exception {
        MaterialBom result=null;
        try{
            result=materialBomMapper.selectByPrimaryKey(id);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<MaterialBom> getMaterialBom() throws Exception{
        MaterialBomExample example = new MaterialBomExample();
        example.createCriteria().andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        example.setOrderByClause("id desc");
        List<MaterialBom> list=null;
        try{
            list=materialBomMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<MaterialBom> select(String process, String project, int offset, int rows)
            throws Exception{
        List<MaterialBom> list = new ArrayList<>();
        try{
            list = materialBomMapperEx.selectByVagueMaterialBom(process, project, offset, rows);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public Long countMaterialBom(String process, String project)throws Exception {
        Long result =null;
        try{
            result=materialBomMapperEx.countsByBom(process, project);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertMaterialBom(JSONObject obj, HttpServletRequest request)throws Exception {
        MaterialBom m = JSONObject.parseObject(obj.toJSONString(), MaterialBom.class);
        try{
            materialBomMapper.insertSelective(m);
            logService.insertLog("工艺流程",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD)
                            .append("[" + m.getProject() + "]")
                            .append(m.getProcess()).toString(), request);
            return 1;
        }
        catch (BusinessRunTimeException ex) {
            throw new BusinessRunTimeException(ex.getCode(), ex.getMessage());
        }
        catch(Exception e){
            JshException.writeFail(logger, e);
            return 0;
        }
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateMaterialBom(JSONObject obj, HttpServletRequest request) throws Exception{
        MaterialBom materialBom = JSONObject.parseObject(obj.toJSONString(), MaterialBom.class);
        try{
            materialBomMapper.updateByPrimaryKeySelective(materialBom);
            logService.insertLog("工艺流程",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT)
                            .append("[" + materialBom.getProject() + "]")
                            .append(materialBom.getProcess()).toString(), request);
            return 1;
        }catch(Exception e){
            JshException.writeFail(logger, e);
            return 0;
        }
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteMaterialBom(Long id, HttpServletRequest request)throws Exception {
        return batchDeleteMaterialBomByIds(id.toString());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteMaterialBom(String ids, HttpServletRequest request)throws Exception {
        return batchDeleteMaterialBomByIds(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteMaterialBomByIds(String ids) throws Exception{
        String [] idArray=ids.split(",");
        try{
            return materialBomMapperEx.batchDeleteMaterialBomByIds(idArray);
        }catch(Exception e){
            JshException.writeFail(logger, e);
            return 0;
        }
    }

    public int checkIsProcessExist(Long id, String process, String project)throws Exception {
        MaterialBomExample example = new MaterialBomExample();
        example.createCriteria().andIdNotEqualTo(id)
                .andProcessEqualTo(process)
                .andProjectEqualTo(project)
                .andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<MaterialBom> list =null;
        try{
            list = materialBomMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list==null?0:list.size();
    }

    /**
     * 因为interface里面有，实际上用不到这个method
     * @param id
     * @param name
     * @return
     * @throws Exception
     */
    public int checkIsNameExist(Long id, String name)throws Exception {
        MaterialBomExample example = new MaterialBomExample();
        example.createCriteria().andIdNotEqualTo(id)
                .andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<MaterialBom> list =null;
        try{
            list = materialBomMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list==null?0:list.size();
    }
}
