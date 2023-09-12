package com.jsh.erp.service.materialBom;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.MaterialBomMapper;
import com.jsh.erp.datasource.mappers.MaterialBomMapperEx;
import com.jsh.erp.datasource.mappers.MaterialCategoryMapperEx;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
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

    @Resource
    private MaterialCategoryMapperEx materialCategoryMapperEx;

    @Resource
    private UserService userService;

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

    public List<MaterialBomVo4Info> select(
            String categoryId, String process, String project, String materialParam, int offset, int rows) throws Exception{
        List<MaterialBomVo4Info> list = new ArrayList<>();
        try{
            List<Long> idList = new ArrayList<>();
            if(StringUtil.isNotEmpty(categoryId)){
                idList = getListByParentId(Long.parseLong(categoryId));
            }
            list = materialBomMapperEx.selectMaterialBom(process, project, materialParam, idList, offset, rows);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<MaterialBomVo4Info> selectByPrefix(String process, String project) throws Exception{
        List<MaterialBomVo4Info> list = new ArrayList<>();
        try{
            list = materialBomMapperEx.selectMaterialBomByPrefix(process, project);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public Long countMaterialBom(String categoryId, String process, String project, String materialParam) throws Exception{
        Long result = null;
        try{
            List<Long> idList = new ArrayList<>();
            if(StringUtil.isNotEmpty(categoryId)){
                idList = getListByParentId(Long.parseLong(categoryId));
            }
            result = materialBomMapperEx.countsByBom(process, project, materialParam, idList);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
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

    public List<MaterialBomVo4Info> getMaterialBomByBarCode(String barCode) {
        String [] barCodeArray = barCode.split(",");
        return materialBomMapperEx.getMaterialBomByBarCode(barCodeArray);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertMaterialBom(JSONObject obj, HttpServletRequest request)throws Exception {
        MaterialBom m = JSONObject.parseObject(obj.toJSONString(), MaterialBom.class);
        try{
            materialBomMapper.insertSelective(m);
            logService.insertLog("产品BOM",
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
            logService.insertLog("产品BOM",
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
            //单次导入超出3000条
            if(rightRows > 3001) {
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_CODE,
                        String.format(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_MSG));
            }
            List<MaterialBom> bomList = new ArrayList<>();
            for (int i = 1; i < rightRows; i++) {
                String process = ExcelUtils.getContent(src, i, 0); //工艺流程
                String partNo = ExcelUtils.getContent(src, i, 1); //规格
                String barCode = ExcelUtils.getContent(src, i, 2); //编码

                String project = ExcelUtils.getContent(src, i, 7); //项目
                String department = ExcelUtils.getContent(src, i, 8); //部门
                String processUsage = ExcelUtils.getContent(src, i, 10); //用量
                String unit = ExcelUtils.getContent(src, i, 11); //单位
                String weight = ExcelUtils.getContent(src, i, 15); //重量（kg）
                String remark = ExcelUtils.getContent(src, i, 17); //备注
                String amountPerCar = ExcelUtils.getContent(src, i, 22); //每车用量

                // 批量校验excel中有无重复BOM
                batchCheckExistMaterialBomByParam(bomList, process, project, barCode);
                MaterialBom bom = new MaterialBom();
                bom.setProcess(process);
                bom.setPartNo(partNo);
                bom.setBarCode(barCode);
                bom.setProject(project);
                bom.setDepartment(department);
                //单位为空
                if(StringUtil.isEmpty(unit)) {
                    throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_UNIT_EMPTY_CODE,
                            String.format(ExceptionConstants.MATERIAL_UNIT_EMPTY_MSG, i+1));
                }
                bom.setUnit(unit);
                if(StringUtil.isNotEmpty(processUsage)) {
                    //校验用量是否为数字
                    if(!StringUtil.isBigDecimal(processUsage)) {
                        throw new BusinessRunTimeException(ExceptionConstants.BOM_USAGE_ERROR_CODE,
                                String.format(ExceptionConstants.BOM_USAGE_ERROR_MSG, i+1));
                    }
                    bom.setProcessUsage(BigDecimal.valueOf(Double.valueOf(processUsage)));
                }
                bom.setRemark(remark);
                if(StringUtil.isNotEmpty(amountPerCar)) {
                    //校验每车用量是否为数字
                    if(!StringUtil.isPositiveBigDecimal(amountPerCar)) {
                        throw new BusinessRunTimeException(ExceptionConstants.BOM_USAGE_ERROR_CODE,
                                String.format(ExceptionConstants.BOM_USAGE_ERROR_MSG, i+1));
                    }
                    bom.setAmountPerCar(BigDecimal.valueOf(Double.valueOf(amountPerCar)));
                }
                //校验产品编码
                if(!StringUtil.checkBarCodeEmptyAllowed(barCode)) {
                    throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_BARCODE_ERROR_CODE,
                            String.format(ExceptionConstants.MATERIAL_BARCODE_ERROR_MSG, barCode));
                }
                bomList.add(bom);
            }
            for (MaterialBom bom : bomList) {
                materialBomMapper.insertSelective(bom);
            }
            logService.insertLog("产品BOM",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_IMPORT).append(bomList.size()).append(BusinessConstants.LOG_DATA_UNIT).toString(),
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
            Long endTime = System.currentTimeMillis();
            logger.info("导入耗时：{}", endTime-beginTime);
            info.code = 200;
            info.data = "导入成功";
        } catch (BusinessRunTimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            info.code = 500;
            info.data = "导入失败";
        }
        return info;
    }

    /**
     * 批量校验excel中有无重复BOM
     */
    public void batchCheckExistMaterialBomByParam(List<MaterialBom> bomList, String process, String project, String barCode) {
        for(MaterialBom bom: bomList){
            if(process.equals(bom.getProcess()) && project.equals(bom.getProject()) && barCode.equals(bom.getBarCode())){
                String info = process + "-" + project + "-" + barCode;
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_EXCEL_IMPORT_EXIST_CODE,
                        String.format(ExceptionConstants.MATERIAL_EXCEL_IMPORT_EXIST_MSG, info));
            }
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
     * 实际上用不到这个method，只是interface里面有
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
