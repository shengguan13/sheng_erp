package com.jsh.erp.service.MaterialUsage;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.MaterialCategoryMapperEx;
import com.jsh.erp.datasource.mappers.MaterialInitialStockMapper;
import com.jsh.erp.datasource.mappers.MaterialUsageMapper;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.service.material.MaterialService;
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
public class MaterialUsageService {
    private Logger logger = LoggerFactory.getLogger(MaterialUsageService.class);
    @Resource
    private LogService logService;
    @Resource
    private MaterialService materialService;
    @Resource
    private MaterialUsageMapper materialUsageMapper;
    @Resource
    private MaterialInitialStockMapper materialInitialStockMapper;
    @Resource
    private MaterialCategoryMapperEx materialCategoryMapperEx;
    @Resource
    private UserService userService;

    public MaterialInitialStockVo4Info getMaterialUsage(long id)throws Exception {
        MaterialInitialStockVo4Info result=null;
        try{
            result=materialUsageMapper.selectByPrimaryKey(id);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<MaterialInitialStockVo4Info> getMaterialUsage() throws Exception{
        MaterialInitialStockExample example = new MaterialInitialStockExample();
        example.createCriteria().andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        example.setOrderByClause("id desc");
        List<MaterialInitialStockVo4Info> list=null;
        try{
            list=materialUsageMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<MaterialBomVo4Info> select(String categoryId, String materialParam, int offset, int rows) throws Exception{
        List<MaterialBomVo4Info> list = new ArrayList<>();
        try{
            List<Long> idList = new ArrayList<>();
            if(StringUtil.isNotEmpty(categoryId)){
                idList = getListByParentId(Long.parseLong(categoryId));
            }
            list = materialUsageMapper.selectMaterialUsage(materialParam, idList, offset, rows);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public Long countMaterialUsage(String categoryId, String materialParam) throws Exception{
        Long result = null;
        try{
            List<Long> idList = new ArrayList<>();
            if(StringUtil.isNotEmpty(categoryId)){
                idList = getListByParentId(Long.parseLong(categoryId));
            }
            result = materialUsageMapper.countMaterialUsage(materialParam, idList);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertMaterialUsage(JSONObject obj, HttpServletRequest request)throws Exception {
        MaterialInitialStockVo4Info m = JSONObject.parseObject(obj.toJSONString(), MaterialInitialStockVo4Info.class);
        try{
            List<MaterialVo4Unit> material = materialService.getMaterialByBarCode(m.getBarCode());
            if (material == null || material.size() == 0) {
                return 0;
            }
            m.setMaterialId(material.get(0).getId());
            m.setDepotId(-1L);
            materialInitialStockMapper.insertSelective((MaterialInitialStock)m);
            logService.insertLog("产品用量",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD)
                            .append(m.getBarCode() + "-")
                            .append(m.getName() + "-")
                            .append(m.getNumber()).toString(), request);
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
    public int updateMaterialUsage(JSONObject obj, HttpServletRequest request) throws Exception{
        MaterialInitialStockVo4Info m = JSONObject.parseObject(obj.toJSONString(), MaterialInitialStockVo4Info.class);
        try{
            List<MaterialVo4Unit> material = materialService.getMaterialByBarCode(m.getBarCode());
            if (material == null || material.size() == 0) {
                return 0;
            }
            m.setMaterialId(material.get(0).getId());
            m.setDepotId(-1L);
            materialInitialStockMapper.updateByPrimaryKeySelective((MaterialInitialStock)m);
            logService.insertLog("产品用量",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT)
                            .append(m.getBarCode() + "-")
                            .append(m.getName() + "-")
                            .append(m.getNumber()).toString(), request);
            return 1;
        }catch(Exception e){
            JshException.writeFail(logger, e);
            return 0;
        }
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteMaterialUsage(Long id, HttpServletRequest request)throws Exception {
        return batchDeleteMaterialUsageByIds(id.toString());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteMaterialUsage(String ids, HttpServletRequest request)throws Exception {
        return batchDeleteMaterialUsageByIds(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteMaterialUsageByIds(String ids) throws Exception{
        String [] idArray=ids.split(",");
        try{
            return materialUsageMapper.batchDeleteMaterialUsageByIds(idArray);
        }catch(Exception e){
            JshException.writeFail(logger, e);
            return 0;
        }
    }

    public List<Long> getListByParentId(Long parentId) {
        List<Long> idList = new ArrayList<Long>();
        List<MaterialCategory> list = materialCategoryMapperEx.getListByParentId(parentId);
        idList.add(parentId);
        if(list!=null && list.size()>0) {
            getIdListByParentId(idList, parentId);
        }
        return idList;
    }

    public List<Long> getIdListByParentId(List<Long> idList, Long parentId){
        List<MaterialCategory> list = materialCategoryMapperEx.getListByParentId(parentId);
        if(list!=null && list.size()>0) {
            for(MaterialCategory mc : list){
                idList.add(mc.getId());
                getIdListByParentId(idList, mc.getId());
            }
        }
        return idList;
    }
}
