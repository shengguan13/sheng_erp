package com.jsh.erp.service.MaterialUsage;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.MaterialCategoryMapperEx;
import com.jsh.erp.datasource.mappers.MaterialInitialStockMapper;
import com.jsh.erp.datasource.mappers.MaterialMapper;
import com.jsh.erp.datasource.mappers.MaterialUsageMapper;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.service.material.MaterialService;
import com.jsh.erp.service.person.PersonService;
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
    @Resource
    private PersonService personService;
    @Resource
    private MaterialMapper materialMapper;

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
        List<MaterialInitialStockVo4Info> list=null;
        try{
            list=materialUsageMapper.selectAllUsage();
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<MaterialInitialStockVo4Info> select(String categoryId, String materialParam, int offset, int rows) throws Exception{
        List<MaterialInitialStockVo4Info> list = new ArrayList<>();
        try{
            List<Long> idList = new ArrayList<>();
            if(StringUtil.isNotEmpty(categoryId)){
                idList = getListByParentId(Long.parseLong(categoryId));
            }
            list = materialUsageMapper.selectMaterialUsage(materialParam, idList, offset, rows);
            for (MaterialInitialStockVo4Info m : list) {
                if (m.getSalesMan() != null && !"".equals(m.getSalesMan())) {
                    try {
                        Person person = personService.getPerson(Long.valueOf(m.getSalesMan()));
                        m.setSalesManStr(person.getName());
                    } catch (Exception e) {}
                }
            }
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
            Map<String, Long> categoryToDepotMapping = new HashMap<>();
            categoryToDepotMapping.put("委外件",26L);
            categoryToDepotMapping.put("包装袋",23L);
            categoryToDepotMapping.put("化学品",23L);
            categoryToDepotMapping.put("原材料",28L);
            categoryToDepotMapping.put("外协件",26L);
            categoryToDepotMapping.put("工装",23L);
            categoryToDepotMapping.put("检具",23L);
            categoryToDepotMapping.put("模具",23L);
            categoryToDepotMapping.put("总成",26L);
            categoryToDepotMapping.put("半成品",27L);
            categoryToDepotMapping.put("包装箱",23L);
            categoryToDepotMapping.put("辅料",23L);
            categoryToDepotMapping.put("进出件",23L);
            categoryToDepotMapping.put("物流",23L);
            categoryToDepotMapping.put("五金",23L);
            categoryToDepotMapping.put("办公",23L);
            categoryToDepotMapping.put("劳保",23L);
            categoryToDepotMapping.put("生产",23L);
            categoryToDepotMapping.put("工具",23L);
            categoryToDepotMapping.put("固定资产",23L);
            categoryToDepotMapping.put("设备",23L);
            categoryToDepotMapping.put("电器",23L);
            categoryToDepotMapping.put("服务",23L);
            categoryToDepotMapping.put("项目",23L);
            int count = 0;
            for (int i = 1; i < rightRows; i++) {
                String barCode = ExcelUtils.getContent(src, i, 0).trim(); //编码
                String lowStock = ExcelUtils.getContent(src, i, 1).trim(); //低储
                String usage = ExcelUtils.getContent(src, i, 2).trim(); //用量
                List<MaterialVo4Unit> material = materialService.getMaterialByBarCode(barCode);
                if (material == null || material.size() == 0) {
                    continue;
                }

                MaterialInitialStock materialUsage = new MaterialInitialStock();
                materialUsage.setMaterialId(material.get(0).getId());
                materialUsage.setDepotId(-1L);

                MaterialInitialStock materialLow = new MaterialInitialStock();
                materialLow.setMaterialId(material.get(0).getId());
                if (!categoryToDepotMapping.containsKey(material.get(0).getCategoryName())) {
                    continue;
                }
                materialLow.setDepotId(categoryToDepotMapping.get(material.get(0).getCategoryName()));
                // LowStock 必须设置，就算是空的也要设置成0，但是用量可以不设置，默认会是0
                Double low = 0.0, use = 0.0;
                try {
                    if (!"".equals(lowStock)) {
                        low = Double.parseDouble(lowStock);
                    }
                } catch (Exception e) {
                    throw new BusinessRunTimeException(ExceptionConstants.USAGE_ERROR_CODE,
                            String.format(ExceptionConstants.USAGE_ERROR_MSG, barCode));
                }
                materialLow.setLowSafeStock(BigDecimal.valueOf(low));
                materialInitialStockMapper.insertSelective(materialLow);

                if (!"".equals(usage)) {
                    try {
                        use = Double.parseDouble(usage);
                    } catch (Exception e) {
                        throw new BusinessRunTimeException(ExceptionConstants.USAGE_ERROR_CODE,
                                String.format(ExceptionConstants.USAGE_ERROR_MSG, barCode));
                    }
                    materialUsage.setNumber(BigDecimal.valueOf(use));
                    materialInitialStockMapper.insertSelective(materialUsage);
                }
                count += 1;
            }
            logService.insertLog("产品用量",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_IMPORT).append(count).append(BusinessConstants.LOG_DATA_UNIT).toString(),
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
