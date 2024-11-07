package com.jsh.erp.service.depotAllocation;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.*;
import com.jsh.erp.datasource.vo.DepotItemVoBatchNumberList;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.depot.DepotService;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.BaseResponseInfo;
import com.jsh.erp.utils.ExcelUtils;
import com.jsh.erp.utils.StringUtil;
import jxl.Sheet;
import jxl.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DepotAllocationService {
    private Logger logger = LoggerFactory.getLogger(DepotAllocationService.class);

    @Resource
    private LogService logService;

    @Resource
    private DepotService depotService;

    @Resource
    private DepotAllocationMapper depotAllocationMapper;

    @Resource
    private DepotAllocationMapperEx depotAllocationMapperEx;

    @Resource
    private UserService userService;

    public DepotAllocation getDepotAllocation(long id)throws Exception {
        DepotAllocation result=null;
        try{
            result = depotAllocationMapper.selectByPrimaryKey(id);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotAllocation> getDepotAllocation() throws Exception{
        DepotAllocationExample example = new DepotAllocationExample();
        example.createCriteria().andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        example.setOrderByClause("id desc");
        List<DepotAllocation> list=null;
        try{
            list=depotAllocationMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<DepotAllocationVo4Depot> select(
            String depotId, String type, String allocation, Integer offset, Integer rows) throws Exception{
        List<DepotAllocationVo4Depot> list = new ArrayList<>();
        try{
            list = depotAllocationMapperEx.selectDepotAllocation(depotId, type, allocation, offset, rows);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public Long countDepotAllocation(String depotId, String type, String allocation) throws Exception{
        Long result = null;
        try{
            result = depotAllocationMapperEx.countDepotAllocation(depotId, type, allocation);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertDepotAllocation(JSONObject obj, HttpServletRequest request)throws Exception {
        DepotAllocation depotAllocation = JSONObject.parseObject(obj.toJSONString(), DepotAllocation.class);
        try{
            depotAllocationMapper.insertSelective(depotAllocation);
            logService.insertLog("仓库货位",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD)
                            .append(depotAllocation.getAllocation() + "-")
                            .append(depotAllocation.getType()).toString(), request);
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
    public List<DepotItemVoBatchNumberList> getAllocationDetail(long allocationId) throws Exception {
        DepotAllocation allocation = depotAllocationMapper.selectByPrimaryKey(allocationId);
        List<DepotItemVoBatchNumberList> detailList = depotAllocationMapperEx.getAllocationDetail(allocationId, allocation.getDepotId());
        return detailList;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public BigDecimal getAllocationSum(long allocationId) throws Exception {
        BigDecimal result = null;
        try{
            result = depotAllocationMapperEx.getAllocationSum(allocationId);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
            return null;
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateDepotAllocation(JSONObject obj, HttpServletRequest request) throws Exception{
        DepotAllocation depotAllocation = JSONObject.parseObject(obj.toJSONString(), DepotAllocation.class);
        try{
            depotAllocationMapper.updateByPrimaryKeySelective(depotAllocation);
            logService.insertLog("仓库货位",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT)
                            .append(depotAllocation.getAllocation() + "-")
                            .append(depotAllocation.getType()).toString(), request);
            return 1;
        }catch(Exception e){
            JshException.writeFail(logger, e);
            return 0;
        }
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteDepotAllocation(Long id, HttpServletRequest request)throws Exception {
        return batchDeleteDepotAllocationByIds(id.toString());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteDepotAllocation(String ids, HttpServletRequest request)throws Exception {
        return batchDeleteDepotAllocationByIds(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteDepotAllocationByIds(String ids) throws Exception{
        String [] idArray=ids.split(",");
        try{
            return depotAllocationMapperEx.batchDeleteDepotAllocationByIds(idArray);
        }catch(Exception e){
            JshException.writeFail(logger, e);
            return 0;
        }
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public BaseResponseInfo importExcel(MultipartFile file, HttpServletRequest request) throws Exception {
        BaseResponseInfo info = new BaseResponseInfo();
        try {
            Long beginTime = System.currentTimeMillis();
            //文件扩展名只能为xls
            String fileName = file.getOriginalFilename();
            if(StringUtil.isNotEmpty(fileName)) {
                String fileExt = fileName.substring(fileName.indexOf(".")+1);
                if(!"xls".equals(fileExt)) {
                    throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_EXTENSION_ERROR_CODE,
                            ExceptionConstants.MATERIAL_EXTENSION_ERROR_MSG);
                }
            }
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet src = workbook.getSheet(0);
            //获取真实的行数，剔除掉空白行
            int rightRows = ExcelUtils.getRightRows(src);
            User user = userService.getCurrentUser();
            //单次导入超出5000条
            if(rightRows > 5001) {
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_CODE,
                        String.format(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_MSG));
            }
            List<DepotAllocation> depotAllocationList = new ArrayList<>();
            List<Depot> allDepots = depotService.getAllList();
            Map<String, Long> depotNameToId = new HashMap<>();
            for (Depot depot : allDepots) {
                depotNameToId.put(depot.getName(), depot.getId());
            }

            for (int i = 1; i < rightRows; i++) {
                String depotName = ExcelUtils.getContent(src, i, 0);
                String allocation = ExcelUtils.getContent(src, i, 1);
                String type = ExcelUtils.getContent(src, i, 2);

                if (!depotNameToId.containsKey(depotName)) {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_NAME_NOT_EXIST_CODE,
                            String.format(ExceptionConstants.DEPOT_NAME_NOT_EXIST_MSG, depotName));
                }

                // 批量校验excel中有无重复allocation
                batchCheckExistDepotAllocationByParam(depotAllocationList, depotNameToId.get(depotName), depotName, allocation, type);
                DepotAllocation depotAllocation = new DepotAllocation();
                depotAllocation.setDepotId(depotNameToId.get(depotName));
                depotAllocation.setAllocation(allocation);
                depotAllocation.setType(type);
                depotAllocation.setDeleteFlag("0");
                depotAllocationList.add(depotAllocation);

                //不允许为空
                if(StringUtil.isEmpty(depotName) || StringUtil.isEmpty(allocation) || StringUtil.isEmpty(type)) {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_ALLOCATION_INFO_MISSING_CODE,
                            String.format(ExceptionConstants.DEPOT_ALLOCATION_INFO_MISSING_MSG, i+1));
                }
            }
            for (DepotAllocation depotAllocation : depotAllocationList) {
                depotAllocationMapper.insertSelective(depotAllocation);
            }
            logService.insertLog("仓库货位",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_IMPORT)
                            .append(depotAllocationList.size())
                            .append(BusinessConstants.LOG_DATA_UNIT).toString(),
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
            Long endTime = System.currentTimeMillis();
            logger.info("导入耗时：{}", endTime-beginTime);
            info.code = 200;
            info.data = "导入成功";
        } catch (BusinessRunTimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            info.code = 500;
            info.data = "导入失败";
        }
        return info;
    }

    /**
     * 批量校验excel中有无重复allocation
     */
    public void batchCheckExistDepotAllocationByParam(List<DepotAllocation> depotAllocationList, Long depotId, String depotName, String allocation, String type) {
        for(DepotAllocation depotAllocation: depotAllocationList){
            if(depotId.equals(depotAllocation.getDepotId()) && allocation.equals(depotAllocation.getAllocation()) && type.equals(depotAllocation.getType())){
                String info = depotName + "-" + allocation + "-" + type;
                throw new BusinessRunTimeException(ExceptionConstants.ALLOCATION_EXCEL_IMPORT_EXIST_CODE,
                        String.format(ExceptionConstants.ALLOCATION_EXCEL_IMPORT_EXIST_MSG, info));
            }
        }
    }

    /**
     * 实际上用不到这个method，只是interface里面有
     */
    public int checkIsNameExist(Long id, String name)throws Exception {
        DepotAllocationExample example = new DepotAllocationExample();
        example.createCriteria().andIdNotEqualTo(id)
                .andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<DepotAllocation> list =null;
        try{
            list = depotAllocationMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list==null?0:list.size();
    }
}
