package com.jsh.erp.service.productSupplier;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.ProductSupplierMapper;
import com.jsh.erp.datasource.mappers.ProductSupplierMapperEx;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.service.supplier.SupplierService;
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
public class ProductSupplierService {
    private Logger logger = LoggerFactory.getLogger(ProductSupplierService.class);

    @Resource
    private LogService logService;

    @Resource
    private ProductSupplierMapper productSupplierMapper;

    @Resource
    private ProductSupplierMapperEx productSupplierMapperEx;

    @Resource
    private UserService userService;

    @Resource
    private SupplierService supplierService;

    public ProductSupplier getProductSupplier(long id)throws Exception {
        ProductSupplier result=null;
        try{
            result = productSupplierMapper.selectByPrimaryKey(id);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<ProductSupplier> getProductSupplier() throws Exception{
        ProductSupplierExample example = new ProductSupplierExample();
        example.createCriteria().andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        example.setOrderByClause("id desc");
        List<ProductSupplier> list=null;
        try {
            list=productSupplierMapper.selectByExample(example);
        } catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<ProductSupplierVo4Info> select(String supplierId, String keyword, Integer offset, Integer rows) throws Exception{
        List<ProductSupplierVo4Info> list = new ArrayList<>();
        try{
            list = productSupplierMapperEx.selectProductSupplier(supplierId, keyword, offset, rows);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public Long countProductSupplier(String supplierId, String keyword) throws Exception{
        Long result = null;
        try{
            result = productSupplierMapperEx.countProductSupplier(supplierId, keyword);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<ProductSupplierVo4Info> getProductSupplierList(String name, String barCode) throws Exception{
        List<ProductSupplierVo4Info> result = null;
        try{
            result = productSupplierMapperEx.selectProductSupplierByBarCode(name, barCode);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertProductSupplier(JSONObject obj, HttpServletRequest request)throws Exception {
        ProductSupplier productSupplier = JSONObject.parseObject(obj.toJSONString(), ProductSupplier.class);
        try{
            productSupplierMapper.insertSelective(productSupplier);
            logService.insertLog("客商档案",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD)
                            .append(productSupplier.getSupplierId() + "-")
                            .append(productSupplier.getSupplierType() + "-")
                            .append(productSupplier.getBarCode()).toString(), request);
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
    public int updateProductSupplier(JSONObject obj, HttpServletRequest request) throws Exception{
        ProductSupplier productSupplier = JSONObject.parseObject(obj.toJSONString(), ProductSupplier.class);
        try{
            productSupplierMapper.updateByPrimaryKeySelective(productSupplier);
            logService.insertLog("客商档案",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT)
                            .append(productSupplier.getSupplierId() + "-")
                            .append(productSupplier.getSupplierType() + "-")
                            .append(productSupplier.getBarCode()).toString(), request);
            return 1;
        }catch(Exception e){
            JshException.writeFail(logger, e);
            return 0;
        }
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteProductSupplier(Long id, HttpServletRequest request)throws Exception {
        return batchDeleteProductSupplierByIds(id.toString());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteProductSupplier(String ids, HttpServletRequest request)throws Exception {
        return batchDeleteProductSupplierByIds(ids);
    }

    public List<ProductSupplierVo4Info> selectByIds(String ids)throws Exception {
        String [] idArray=ids.split(",");
        List<ProductSupplierVo4Info> result = new ArrayList<>();
        try {
            result = productSupplierMapperEx.selectByIds(idArray);
        } catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteProductSupplierByIds(String ids) throws Exception{
        String [] idArray=ids.split(",");
        try{
            return productSupplierMapperEx.batchDeleteProductSupplierByIds(idArray);
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
            List<ProductSupplier> productSupplierList = new ArrayList<>();
            List<Supplier> allSuppliers = supplierService.getSupplier();
            Map<String, Long> supplierNameToId = new HashMap<>();
            for (Supplier supplier : allSuppliers) {
                supplierNameToId.put(supplier.getSupplier(), supplier.getId());
            }

            for (int i = 1; i < rightRows; i++) {
                String barCode = ExcelUtils.getContent(src, i, 1);
                String supplierName = ExcelUtils.getContent(src, i, 2);
                String manufactory = ExcelUtils.getContent(src, i, 3);
                String model = ExcelUtils.getContent(src, i, 4);
                String pack = ExcelUtils.getContent(src, i, 5);
                String taxRate = ExcelUtils.getContent(src, i, 7); // 保质期
                String type = ExcelUtils.getContent(src, i, 8);
                String purchaseCycle = ExcelUtils.getContent(src, i, 9);
                String unit = ExcelUtils.getContent(src, i, 12); // 起订量
                BigDecimal expir = BigDecimal.ZERO;
                try {
                    expir = BigDecimal.valueOf(Double.valueOf(taxRate));
                } catch (Exception e) {}
                //不允许为空
                if(StringUtil.isEmpty(supplierName) || StringUtil.isEmpty(barCode) || StringUtil.isEmpty(type)) {
                    throw new BusinessRunTimeException(ExceptionConstants.PRODUCT_SUPPLIER_INFO_MISSING_CODE,
                            String.format(ExceptionConstants.PRODUCT_SUPPLIER_INFO_MISSING_MSG, i+1));
                }

                if (!supplierNameToId.containsKey(supplierName)) {
//                    throw new BusinessRunTimeException(ExceptionConstants.SUPPLIER_NAME_NOT_EXIST_CODE,
//                            String.format(ExceptionConstants.SUPPLIER_NAME_NOT_EXIST_MSG, supplierName));
                    continue;
                }
                ProductSupplierExample example = new ProductSupplierExample();
                if (StringUtil.isEmpty(model)) {
                    example.createCriteria().andSupplierIdEqualTo(supplierNameToId.get(supplierName))
                            .andBarCodeEqualTo(barCode).andModelIsNull().andSupplierTypeEqualTo(type);
                } else {
                    example.createCriteria().andSupplierIdEqualTo(supplierNameToId.get(supplierName))
                            .andBarCodeEqualTo(barCode).andModelEqualTo(model).andSupplierTypeEqualTo(type);
                }
                List<ProductSupplier> list = productSupplierMapper.selectByExample(example);

                ProductSupplier productSupplier = new ProductSupplier();
                productSupplier.setSupplierId(supplierNameToId.get(supplierName));
                productSupplier.setManufactory(manufactory);
                productSupplier.setSupplierType(type);
                productSupplier.setBarCode(barCode);
                productSupplier.setUnit(unit);
                productSupplier.setModel(model);
                productSupplier.setPack(pack);
                productSupplier.setPurchaseCycle(purchaseCycle);
                productSupplier.setTaxRate(expir);
                productSupplier.setDeleteFlag("0");
                productSupplierList.add(productSupplier);
                if (list.size() > 0) {
                    productSupplier.setId(list.get(0).getId());
                    productSupplierMapper.updateByPrimaryKeySelective(productSupplier);
                } else {
                    productSupplierMapper.insertSelective(productSupplier);
                }
            }
            logService.insertLog("客商档案",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_IMPORT)
                            .append(productSupplierList.size())
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
     * 批量校验excel中有无重复
     */
    public void batchCheckExistProductSupplierByParam(List<ProductSupplier> productSupplierList, String supplierName, Long supplierId,
                                                      String barCode, String supplierType, String model, String pack) {
        for(ProductSupplier productSupplier: productSupplierList){
            if(supplierId.longValue() == productSupplier.getSupplierId().longValue()
                    && barCode.equals(productSupplier.getBarCode())
                    && supplierType.equals(productSupplier.getSupplierType())
                    && model.equals(productSupplier.getModel())
                    && pack.equals(productSupplier.getPack())){
                String info = supplierName + "-" + barCode + "-" + model + "-" + supplierType;
                throw new BusinessRunTimeException(ExceptionConstants.PRODUCT_SUPPLIER_EXIST_CODE,
                        String.format(ExceptionConstants.PRODUCT_SUPPLIER_EXIST_MSG, info));
            }
        }
    }

    /**
     * 实际上用不到这个method，只是interface里面有
     */
    public int checkIsNameExist(Long id, String name)throws Exception {
        ProductSupplierExample example = new ProductSupplierExample();
        example.createCriteria().andIdNotEqualTo(id)
                .andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<ProductSupplier> list =null;
        try{
            list = productSupplierMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list == null ? 0 : list.size();
    }
}
