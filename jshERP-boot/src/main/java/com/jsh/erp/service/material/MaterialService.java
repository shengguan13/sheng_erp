package com.jsh.erp.service.material;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.*;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.depot.DepotService;
import com.jsh.erp.service.depotItem.DepotItemService;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.service.materialBom.MaterialBomService;
import com.jsh.erp.service.materialCategory.MaterialCategoryService;
import com.jsh.erp.service.materialExtend.MaterialExtendService;
import com.jsh.erp.service.redis.RedisService;
import com.jsh.erp.service.unit.UnitService;
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
import java.util.*;

@Service
public class MaterialService {
    private Logger logger = LoggerFactory.getLogger(MaterialService.class);
    private static final int MAX_COL_NUM = 30;

    @Resource
    private MaterialMapper materialMapper;
    @Resource
    private MaterialBomService materialBomService;
    @Resource
    private MaterialExtendMapper materialExtendMapper;
    @Resource
    private MaterialMapperEx materialMapperEx;
    @Resource
    private MaterialBomMapperEx materialBomMapperEx;
    @Resource
    private MaterialCategoryMapperEx materialCategoryMapperEx;
    @Resource
    private MaterialExtendMapperEx materialExtendMapperEx;
    @Resource
    private LogService logService;
    @Resource
    private UserService userService;
    @Resource
    private DepotItemMapperEx depotItemMapperEx;
    @Resource
    private DepotItemService depotItemService;
    @Resource
    private MaterialCategoryService materialCategoryService;
    @Resource
    private UnitService unitService;
    @Resource
    private MaterialInitialStockMapper materialInitialStockMapper;
    @Resource
    private MaterialInitialStockMapperEx materialInitialStockMapperEx;
    @Resource
    private MaterialCurrentStockMapper materialCurrentStockMapper;
    @Resource
    private MaterialCurrentStockMapperEx materialCurrentStockMapperEx;
    @Resource
    private DepotService depotService;
    @Resource
    private MaterialExtendService materialExtendService;
    @Resource
    private RedisService redisService;

    public Material getMaterial(long id)throws Exception {
        Material result=null;
        try{
            result=materialMapper.selectByPrimaryKey(id);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<Material> getMaterialListByIds(String ids)throws Exception {
        List<Long> idList = StringUtil.strToLongList(ids);
        List<Material> list = new ArrayList<>();
        try{
            MaterialExample example = new MaterialExample();
            example.createCriteria().andIdIn(idList);
            list = materialMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<Material> getMaterial() throws Exception{
        MaterialExample example = new MaterialExample();
        example.createCriteria().andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<Material> list=null;
        try{
            list=materialMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<MaterialVo4Unit> select(String materialParam, String color, String materialOther,
                                        String weight, String expiryNum, String enabled, String remark,
                                        String categoryId, String mpList, int offset, int rows)
            throws Exception{
        String[] mpArr = new String[]{};
        if(StringUtil.isNotEmpty(mpList)){
            mpArr= mpList.split(",");
        }
        List<MaterialVo4Unit> resList = new ArrayList<>();
        List<MaterialVo4Unit> list =null;
        try{
            List<Long> idList = new ArrayList<>();
            if(StringUtil.isNotEmpty(categoryId)){
                idList = getListByParentId(Long.parseLong(categoryId));
            }
            long t1 = System.currentTimeMillis();
            list= materialMapperEx.selectByConditionMaterial(materialParam, color,
                    materialOther, weight, expiryNum, enabled, remark, idList, mpList, offset, rows);
            long t2 = System.currentTimeMillis();
            if (null != list && list.size()>0) {
                Map<Long,BigDecimal> currentStockMap = getCurrentStockMapByMaterialList(list);
                for (MaterialVo4Unit m : list) {
                    m.setMaterialOther(getMaterialOtherByParam(mpArr, m));
                    m.setStock(currentStockMap.get(m.getId())!=null? currentStockMap.get(m.getId()): BigDecimal.ZERO);
                    m.setBigUnitStock(getBigUnitStock(m.getStock(), m.getUnitId()));
                    resList.add(m);
                }
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return resList;
    }

    public Long countMaterial(String materialParam, String color, String materialOther,
                              String weight, String expiryNum, String enabled, String remark,
                              String categoryId,String mpList)throws Exception {
        Long result =null;
        try{
            List<Long> idList = new ArrayList<>();
            if(StringUtil.isNotEmpty(categoryId)){
                idList = getListByParentId(Long.parseLong(categoryId));
            }
            result= materialMapperEx.countsByMaterial(materialParam, color, materialOther,
                    weight, expiryNum, enabled, remark, idList, mpList);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertMaterial(JSONObject obj, HttpServletRequest request)throws Exception {
        Material m = JSONObject.parseObject(obj.toJSONString(), Material.class);
        m.setEnabled(true);
        try{
            materialMapperEx.insertSelectiveEx(m);
            Long mId = m.getId();
            materialExtendService.saveDetials(obj, obj.getString("sortList"), mId, "insert");
            if(obj.get("stock") != null) {
                JSONArray stockArr = obj.getJSONArray("stock");
                for (int i = 0; i < stockArr.size(); i++) {
                    JSONObject jsonObj = stockArr.getJSONObject(i);
                    if(jsonObj.get("id")!=null && jsonObj.get("initStock")!=null) {
                        String number = jsonObj.getString("initStock");
                        BigDecimal lowSafeStock = null;
                        BigDecimal highSafeStock = null;
                        if(jsonObj.get("lowSafeStock")!=null) {
                            lowSafeStock = jsonObj.getBigDecimal("lowSafeStock");
                        }
                        if(jsonObj.get("highSafeStock")!=null) {
                            highSafeStock = jsonObj.getBigDecimal("highSafeStock");
                        }
                        Long depotId = jsonObj.getLong("id");
                        if(StringUtil.isNotEmpty(number) && Double.parseDouble(number)>0 || lowSafeStock!=null || highSafeStock!=null) {
                            insertInitialStockByMaterialAndDepot(depotId, mId, parseBigDecimalEx(number), lowSafeStock, highSafeStock);
                            insertCurrentStockByMaterialAndDepot(depotId, mId, parseBigDecimalEx(number));
                        }
                    }
                }
            }
            logService.insertLog("产品",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(m.getName()).toString(), request);
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
    public int updateMaterial(JSONObject obj, HttpServletRequest request) throws Exception{
        Material material = JSONObject.parseObject(obj.toJSONString(), Material.class);
        try{
            materialMapper.updateByPrimaryKeySelective(material);
            if(material.getUnitId() == null) {
                materialMapperEx.setUnitIdToNull(material.getId());
            }
            if(material.getExpiryNum() == null) {
                materialMapperEx.setExpiryNumToNull(material.getId());
            }
            materialExtendService.saveDetials(obj, obj.getString("sortList"),material.getId(), "update");
            if(obj.get("stock")!=null) {
                JSONArray stockArr = obj.getJSONArray("stock");
                for (int i = 0; i < stockArr.size(); i++) {
                    JSONObject jsonObj = stockArr.getJSONObject(i);
                    if (jsonObj.get("id") != null && jsonObj.get("initStock") != null) {
                        String number = jsonObj.getString("initStock");
                        BigDecimal lowSafeStock = null;
                        BigDecimal highSafeStock = null;
                        if(jsonObj.get("lowSafeStock")!=null) {
                            lowSafeStock = jsonObj.getBigDecimal("lowSafeStock");
                        }
                        if(jsonObj.get("highSafeStock")!=null) {
                            highSafeStock = jsonObj.getBigDecimal("highSafeStock");
                        }
                        Long depotId = jsonObj.getLong("id");
                        //初始库存-先清除再插入
                        MaterialInitialStockExample example = new MaterialInitialStockExample();
                        example.createCriteria().andMaterialIdEqualTo(material.getId()).andDepotIdEqualTo(depotId);
                        materialInitialStockMapper.deleteByExample(example);
                        if (StringUtil.isNotEmpty(number) || lowSafeStock!=null || highSafeStock!=null) {
                            insertInitialStockByMaterialAndDepot(depotId, material.getId(), parseBigDecimalEx(number), lowSafeStock, highSafeStock);
                        }
                        //更新当前库存
                        depotItemService.updateCurrentStockFun(material.getId(), depotId);
                    }
                }
            }
            logService.insertLog("产品",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(material.getName()).toString(), request);
            return 1;
        } catch (BusinessRunTimeException ex) {
            throw new BusinessRunTimeException(ex.getCode(), ex.getMessage());
        } catch(Exception e){
            JshException.writeFail(logger, e);
            return 0;
        }
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteMaterial(Long id, HttpServletRequest request)throws Exception {
        return batchDeleteMaterialByIds(id.toString());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteMaterial(String ids, HttpServletRequest request)throws Exception {
        return batchDeleteMaterialByIds(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteMaterialByIds(String ids) throws Exception{
        String [] idArray=ids.split(",");
        //校验单据子表	jsh_depot_item
        List<DepotItem> depotItemList =null;
        try{
            depotItemList=  depotItemMapperEx.getDepotItemListListByMaterialIds(idArray);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        if(depotItemList!=null&&depotItemList.size()>0){
            logger.error("异常码[{}],异常提示[{}],参数,MaterialIds[{}]",
                    ExceptionConstants.DELETE_FORCE_CONFIRM_CODE,ExceptionConstants.DELETE_FORCE_CONFIRM_MSG,ids);
            throw new BusinessRunTimeException(ExceptionConstants.DELETE_FORCE_CONFIRM_CODE,
                    ExceptionConstants.DELETE_FORCE_CONFIRM_MSG);
        }
        //记录日志
        StringBuffer sb = new StringBuffer();
        sb.append(BusinessConstants.LOG_OPERATION_TYPE_DELETE);
        List<Material> list = getMaterialListByIds(ids);
        for(Material material: list){
            sb.append("[").append(material.getName()).append("]");
        }
        logService.insertLog("产品", sb.toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        User userInfo=userService.getCurrentUser();
        //校验通过执行删除操作
        try{
            //逻辑删除产品
            materialMapperEx.batchDeleteMaterialByIds(new Date(),userInfo==null?null:userInfo.getId(),idArray);
            //逻辑删除产品价格扩展
            materialExtendMapperEx.batchDeleteMaterialExtendByMIds(idArray);
            return 1;
        }catch(Exception e){
            JshException.writeFail(logger, e);
            return 0;
        }
    }

    public int checkIsNameExist(Long id, String name)throws Exception {
        MaterialExample example = new MaterialExample();
        example.createCriteria().andIdNotEqualTo(id).andNameEqualTo(name).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<Material> list =null;
        try{
            list=  materialMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list==null?0:list.size();
    }

    public int checkIsExist(Long id, String name, String model, String color,
                            String colorCode, String mat,
                            String otherField1, String otherField2, String otherField3,
                            String otherField4, String otherField5, String otherField6,
                            String otherField7, String otherField8, String otherField9,
                            String otherField10, String unit, Long unitId)throws Exception {
        return materialMapperEx.checkIsExist(id, name, model, color, colorCode, mat, otherField1,
                otherField2, otherField3, otherField4, otherField5, otherField6, otherField7, otherField8,
                otherField9, otherField10, unit, unitId);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchSetStatus(Boolean status, String ids)throws Exception {
        logService.insertLog("产品",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(ids).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        List<Long> materialIds = StringUtil.strToLongList(ids);
        Material material = new Material();
        material.setEnabled(status);
        MaterialExample example = new MaterialExample();
        example.createCriteria().andIdIn(materialIds);
        int result =0;
        try{
            result=  materialMapper.updateByExampleSelective(material, example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public Unit findUnit(Long mId)throws Exception{
        Unit unit = new Unit();
        try{
            List<Unit> list = materialMapperEx.findUnitList(mId);
            if(list!=null && list.size()>0) {
                unit = list.get(0);
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return unit;
    }

    public List<MaterialVo4Unit> findById(Long id)throws Exception{
        List<MaterialVo4Unit> list =null;
        try{
            list=  materialMapperEx.findById(id);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<MaterialVo4Unit> findByIdWithBarCode(Long meId)throws Exception{
        List<MaterialVo4Unit> list =null;
        try{
            list=  materialMapperEx.findByIdWithBarCode(meId);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
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

    public List<MaterialVo4Unit> findBySelectWithBarCode(Long categoryId, String q,
                                                         Integer offset, Integer rows)throws Exception{
        List<MaterialVo4Unit> list =null;
        try{
            List<Long> idList = new ArrayList<>();
            if(categoryId!=null){
                Long parentId = categoryId;
                idList = getListByParentId(parentId);
            } else {
                User user = userService.getCurrentUser();
                List<MaterialCategory> categories = materialCategoryService.getMaterialCategory();
                if ("lihongjun".equals(user.getLoginName())) {
                    for (MaterialCategory category : categories) {
                        if ("半成品".equals(category.getName()) || "项目".equals(category.getName())) {
                            idList.add(category.getId());
                        }
                    }
                } else if ("zhangshanshan".equals(user.getLoginName())) {
                    for (MaterialCategory category : categories) {
                        if ("成品".equals(category.getName())) {
                            idList.add(category.getId());
                        }
                    }
                } else {}
            }
            if(StringUtil.isNotEmpty(q)) {
                q = q.replace("'", "");
                q = q.trim();
            }
            list=  materialMapperEx.findBySelectWithBarCode(idList, q, offset, rows);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int findBySelectWithBarCodeCount(Long categoryId, String q)throws Exception{
        int result=0;
        try{
            List<Long> idList = new ArrayList<>();
            if(categoryId!=null){
                Long parentId = categoryId;
                idList = getListByParentId(parentId);
            } else {
                User user = userService.getCurrentUser();
                List<MaterialCategory> categories = materialCategoryService.getMaterialCategory();
                if ("lihongjun".equals(user.getLoginName())) {
                    for (MaterialCategory category : categories) {
                        if ("半成品".equals(category.getName()) || "项目".equals(category.getName())) {
                            idList.add(category.getId());
                        }
                    }
                } else if ("zhangshanshan".equals(user.getLoginName())) {
                    for (MaterialCategory category : categories) {
                        if ("成品".equals(category.getName())) {
                            idList.add(category.getId());
                        }
                    }
                } else {}
            }
            if(StringUtil.isNotEmpty(q)) {
                q = q.replace("'", "");
            }
            result = materialMapperEx.findBySelectWithBarCodeCount(idList, q);
        }catch(Exception e){
            logger.error("异常码[{}],异常提示[{}],异常[{}]",
                    ExceptionConstants.DATA_READ_FAIL_CODE,ExceptionConstants.DATA_READ_FAIL_MSG,e);
            throw new BusinessRunTimeException(ExceptionConstants.DATA_READ_FAIL_CODE,
                    ExceptionConstants.DATA_READ_FAIL_MSG);
        }
        return result;
    }

    public List<MaterialVo4Unit> exportExcel(String materialParam, String color, String weight, String expiryNum,
                                             String enabled, String remark, String categoryId)throws Exception {
        List<MaterialVo4Unit> resList = new ArrayList<>();
        List<MaterialVo4Unit> list =null;
        try{
            List<Long> idList = new ArrayList<>();
            if(StringUtil.isNotEmpty(categoryId)){
                idList = getListByParentId(Long.parseLong(categoryId));
            }
            list=  materialMapperEx.exportExcel(materialParam, color, weight, expiryNum, enabled, remark, idList);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        if (null != list) {
            for (MaterialVo4Unit m : list) {
                resList.add(m);
            }
        }
        return resList;
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
            List<Depot> depotList= depotService.getDepot();
            int depotCount = depotList.size();
            Map<String, Long> depotMap = parseDepotToMap(depotList);
            User user = userService.getCurrentUser();
            List<MaterialWithInitStock> mList = new ArrayList<>();
            //单次导入超出5000条
            if(rightRows > 5001) {
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_CODE,
                        String.format(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_MSG));
            }
            for (int i = 1; i < rightRows; i++) {
                String barCode = ExcelUtils.getContent(src, i, 0); //物料编码
                String category = ExcelUtils.getContent(src, i, 1); //分类
                String categoryName = "";
                try {
                    if (category.contains("总成")) {
                        categoryName = "总成";
                    } else if (category.contains("半成品")) {
                        categoryName = "半成品";
                    } else if (category.contains("项目")) {
                        categoryName = "项目";
                    } else {
                        String[] categoryList = category.split(",");
                        categoryName = categoryList[0];
                    }
                } catch (Exception e) {}
                String model = ExcelUtils.getContent(src, i, 2); //型号
                String name = ExcelUtils.getContent(src, i, 3); //名称
                String color = ExcelUtils.getContent(src, i, 5); //颜色
                String colorCode = ExcelUtils.getContent(src, i, 6); //颜色代码
                String mat = ExcelUtils.getContent(src, i, 7); //材质
                String other5 = ExcelUtils.getContent(src, i, 8); //规格
                String other6 = ExcelUtils.getContent(src, i, 9); //材料标准
                String weightStr = ExcelUtils.getContent(src, i, 10); //重量
                String unit = ExcelUtils.getContent(src, i, 11); //单位
                BigDecimal weight = BigDecimal.ZERO;
                try {
                    Double weightVal = Double.parseDouble(weightStr);
                    weight = BigDecimal.valueOf(weightVal);
                } catch (Exception e) {
                }
                String expiryNum = ""; //保质期/月
                String other1 = ""; //主壁厚
                String other2 = ""; //模腔数
                String other3 = ""; //模具重量
                String other4 = ""; //浇口重量

                String enabled = "1"; //状态
                String remark = ""; //备注
//                名称为空
//                if(StringUtil.isEmpty(name)) {
//                    throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_NAME_EMPTY_CODE,
//                            String.format(ExceptionConstants.MATERIAL_NAME_EMPTY_MSG, i+1));
//                }
//                单位为空
                if(StringUtil.isEmpty(unit)) {
                    throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_UNIT_EMPTY_CODE,
                            String.format(ExceptionConstants.MATERIAL_UNIT_EMPTY_MSG, i+1));
                }

                MaterialWithInitStock m = new MaterialWithInitStock();
                m.setName(name);
                m.setMat(mat);
                m.setColorCode(colorCode);
                m.setModel(model);
                m.setColor(color);
                m.setWeight(weight);
                m.setOtherField1(other1);
                m.setOtherField2(other2);
                m.setOtherField3(other3);
                m.setOtherField4(other4);
                m.setOtherField5(other5);
                m.setOtherField6(other6);
                m.setRemark(remark);

                Long categoryId = materialCategoryService.getCategoryIdByName(categoryName);
                if(null!=categoryId){
                    m.setCategoryId(categoryId);
                }
                if(StringUtil.isNotEmpty(expiryNum)) {
                    //校验保质期是否是正整数
                    if(!StringUtil.isPositiveLong(expiryNum)) {
                        throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_EXPIRY_NUM_NOT_INTEGER_CODE,
                                String.format(ExceptionConstants.MATERIAL_EXPIRY_NUM_NOT_INTEGER_MSG, i+1));
                    }
                    m.setExpiryNum(Integer.parseInt(expiryNum));
                }
//                校验编码的格式
//                if(!StringUtil.checkBarCode(barCode)) {
//                    throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_BARCODE_ERROR_CODE,
//                            String.format(ExceptionConstants.MATERIAL_BARCODE_ERROR_MSG, barCode));
//                }
//                批量校验excel中有无重复产品，是指名称、型号、规格、颜色、单位
//                batchCheckExistMaterialListByParam(mList, name, other1, other2, mat, colorCode,
//                        model, color, other4, unit, categoryId, other9);

                //批量校验excel中有无重复编码
                batchCheckExistBarCodeByParam(mList, barCode);
                JSONObject materialExObj = new JSONObject();
                materialExObj.put("barCode", barCode);
                materialExObj.put("commodityUnit", unit);
                m.setUnit(unit);
                m.setMaterialExObj(materialExObj);
                m.setEnabled("1".equals(enabled));
                // -------------------------------------------------------------
                m.setStockMap(new HashMap<>());
                //m.setStockMap(getStockMapCache(src, depotCount, depotMap, i));
                mList.add(m);
            }
            List<Long> deleteInitialStockMaterialIdList = new ArrayList<>();
            List<Long> deleteCurrentStockMaterialIdList = new ArrayList<>();
            List<MaterialInitialStock> insertInitialStockMaterialList = new ArrayList<>();
            List<MaterialCurrentStock> insertCurrentStockMaterialList = new ArrayList<>();
            for(MaterialWithInitStock m: mList) {
                Long mId = 0L;
                // 判断该产品是否存在，如果不存在就新增，如果存在就更新
                String barCode = getBarCode(m);
//                TODO: 目前产品一样的情况下也新增，除非物料编码一样才更新
//                List<Material> materials = getMaterialListByParam(m.getName(), m.getColorCode(),
//                        m.getModel(), m.getColor(), m.getUnit(), m.getUnitId(), barCode);
                List<Material> materials = getMaterialListByBarCode(barCode);
                if(materials.size() == 0) {
                    materialMapperEx.insertSelectiveEx(m);
                    mId = m.getId();
                } else {
                    mId = materials.get(0).getId();
                    String materialJson = JSON.toJSONString(m);
                    Material material = JSONObject.parseObject(materialJson, Material.class);
                    material.setId(mId);
                    materialMapper.updateByPrimaryKeySelective(material);
                }
                //给产品新增或更新编码等相关信息
                JSONObject materialExObj = m.getMaterialExObj();
                insertOrUpdateMaterialExtend(materialExObj, "1", mId, user);
                //给产品更新库存
                Map<Long, BigDecimal> stockMap = m.getStockMap();
                for(Depot depot: depotList){
                    Long depotId = depot.getId();
                    BigDecimal stock = stockMap.get(depot.getId());
                    //新增初始库存
                    if(stock!=null && stock.compareTo(BigDecimal.ZERO)!=0) {
                        MaterialInitialStock materialInitialStock = new MaterialInitialStock();
                        materialInitialStock.setMaterialId(mId);
                        materialInitialStock.setDepotId(depotId);
                        materialInitialStock.setNumber(stock);
                        insertInitialStockMaterialList.add(materialInitialStock);
                        deleteInitialStockMaterialIdList.add(mId);
                    }
                    //新增或更新当前库存
                    Long billCount = depotItemService.getCountByMaterialAndDepot(mId, depotId);
                    if(billCount == 0) {
                        if(stock!=null && stock.compareTo(BigDecimal.ZERO)!=0) {
                            MaterialCurrentStock materialCurrentStock = new MaterialCurrentStock();
                            materialCurrentStock.setMaterialId(mId);
                            materialCurrentStock.setDepotId(depotId);
                            materialCurrentStock.setCurrentNumber(stock);
                            insertCurrentStockMaterialList.add(materialCurrentStock);
                            deleteCurrentStockMaterialIdList.add(mId);
                        }
                    } else {
                        BigDecimal initStock = getInitStock(mId, depotId);
                        BigDecimal currentNumber = getCurrentStockByMaterialIdAndDepotId(mId, depotId);
                        //当前库存的更新：减去初始库存，再加上导入的新初始库存
                        if(currentNumber!=null && initStock!=null && stock!=null) {
                            currentNumber = currentNumber.subtract(initStock).add(stock);
                        }
                        MaterialCurrentStock materialCurrentStock = new MaterialCurrentStock();
                        materialCurrentStock.setMaterialId(mId);
                        materialCurrentStock.setDepotId(depotId);
                        materialCurrentStock.setCurrentNumber(currentNumber);
                        insertCurrentStockMaterialList.add(materialCurrentStock);
                        deleteCurrentStockMaterialIdList.add(mId);
                    }
                }
            }

            //批量更新库存,先删除后新增
            if(insertInitialStockMaterialList.size()>0) {
                batchDeleteInitialStockByMaterialList(deleteInitialStockMaterialIdList);
                materialInitialStockMapperEx.batchInsert(insertInitialStockMaterialList);
            }
            if(insertCurrentStockMaterialList.size()>0) {
                batchDeleteCurrentStockByMaterialList(deleteCurrentStockMaterialIdList);
                materialCurrentStockMapperEx.batchInsert(insertCurrentStockMaterialList);
            }
            logService.insertLog("产品",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_IMPORT).append(mList.size()).append(BusinessConstants.LOG_DATA_UNIT).toString(),
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
            Long endTime = System.currentTimeMillis();
            logger.info("导入耗时：{}", endTime-beginTime);
            info.code = 200;
            info.data = "导入成功";
        } catch (BusinessRunTimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("导入失败：{}", e.getMessage());
            logger.error(e.getMessage(), e);
            info.code = 500;
            info.data = "导入失败";
        }
        return info;
    }

    private boolean isValid(String str) {
        if (!str.contains("*")) {
            return false;
        }
        String[] split = str.split("\\*");
        if (split.length != 2) {
            return false;
        }
        try {
            Double.parseDouble(split[1]);
        } catch (Exception e) {
            return false;
        }
        if (split[0].length() <= 2 || split[0].charAt(0) != '[' || split[0].charAt(split[0].length() - 1) != ']') {
            return false;
        }
        try {
            Long.parseLong(split[0].substring(1, split[0].length() - 1));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private Map<String, Long> parseDepotToMap(List<Depot> depotList) {
        Map<String, Long> map = new HashMap<>();
        for(Depot depot: depotList) {
            map.put(depot.getName(), depot.getId());
        }
        return map;
    }

    /**
     * 缓存各个仓库的库存信息
     * @param src
     * @param depotCount
     * @param depotMap
     * @param i
     * @return
     * @throws Exception
     */
    private Map<Long, BigDecimal> getStockMapCache(Sheet src, int depotCount, Map<String, Long> depotMap, int i) throws Exception {
        Map<Long, BigDecimal> stockMap = new HashMap<>();
        for(int j = 1; j<= depotCount; j++) {
            int col = MAX_COL_NUM + j;       // 仓库之前的列数
            if(col < src.getColumns()){
                String depotName = ExcelUtils.getContent(src, 1, col); //获取仓库名称
                if(StringUtil.isNotEmpty(depotName)) {
                    Long depotId = depotMap.get(depotName);
                    if(depotId!=null && depotId!=0L){
                        String stockStr = ExcelUtils.getContent(src, i, col);
                        if(StringUtil.isNotEmpty(stockStr)) {
                            stockMap.put(depotId, parseBigDecimalEx(stockStr));
                        }
                    }
                }
            }
        }
        return stockMap;
    }

    /**
     * 批量校验excel中有无重复产品，是指名称、型号、规格、颜色、单位、标包、材质等
     * @param mList
     */
    public void batchCheckExistMaterialListByParam(List<MaterialWithInitStock> mList, String name, String customer,
                                                   String customerType, String mat, String colorCode, String model,
                                                   String color, String unit, Long categoryId, String other9) {
        for(MaterialWithInitStock material: mList){
            if(name.equals(material.getName()) &&
                    customer.equals(material.getOtherField1()) &&
                    customerType.equals(material.getOtherField2()) &&
                    mat.equals(material.getMat()) &&
                    colorCode.equals(material.getColorCode()) &&
                    model.equals(material.getModel()) &&
                    color.equals(material.getColor()) &&
                    unit.equals(material.getUnit()) &&
                    categoryId.equals(material.getCategoryId()) &&
                    other9.equals(material.getOtherField9())){
                String info = name + "-" + customer + "-" + customerType + "-" + mat + "-" + colorCode + "-" + model + "-" + color + "-" + unit;
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_EXCEL_IMPORT_EXIST_CODE,
                        String.format(ExceptionConstants.MATERIAL_EXCEL_IMPORT_EXIST_MSG, info));
            }
        }
    }

    /**
     * 批量校验excel中有无重复编码
     * @param mList
     */
    public void batchCheckExistBarCodeByParam(List<MaterialWithInitStock> mList, String barCode) {
        for(MaterialWithInitStock material: mList){
            JSONObject materialExObj = material.getMaterialExObj();
            String existBarCode = materialExObj.getString("barCode");
            if(barCode.equals(existBarCode)){
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_EXCEL_IMPORT_BARCODE_EXIST_CODE,
                        String.format(ExceptionConstants.MATERIAL_EXCEL_IMPORT_BARCODE_EXIST_MSG, barCode));
            }
        }
    }

    /**
     * 给产品新增或更新编码与价格相关信息
     * @param materialExObj
     * @param defaultFlag
     * @param mId
     * @param user
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void insertOrUpdateMaterialExtend(JSONObject materialExObj, String defaultFlag, Long mId, User user) {
        String materialExStr = materialExObj.toString();
        MaterialExtend materialExtend = JSONObject.parseObject(materialExStr, MaterialExtend.class);
        materialExtend.setMaterialId(mId);
        materialExtend.setDefaultFlag(defaultFlag);
        materialExtend.setCreateTime(new Date());
        materialExtend.setUpdateTime(System.currentTimeMillis());
        materialExtend.setCreateSerial(user.getLoginName());
        materialExtend.setUpdateSerial(user.getLoginName());
        Long meId = materialExtendService.selectIdByMaterialIdAndDefaultFlag(mId, defaultFlag);
        if(meId==0L){
            materialExtendMapper.insertSelective(materialExtend);
        } else {
            materialExtend.setId(meId);
            materialExtendMapper.updateByPrimaryKeySelective(materialExtend);
        }
    }

    public String getBarCode(MaterialWithInitStock m) {
        JSONObject materialExObj = m.getMaterialExObj();
        MaterialExtend materialExtend = JSONObject.parseObject(m.getMaterialExObj().toString(), MaterialExtend.class);
        return materialExtend.getBarCode();
    }

    /**
     * 根据条件返回产品列表
     * @param name
     * @param colorCode
     * @param model
     * @param color
     * @param unit
     * @param unitId
     * @return
     */
    private List<Material> getMaterialListByParam(String name, String colorCode, String model, String color,
                                                  String unit, Long unitId, String barCode) throws Exception {
        List<Material> list = new ArrayList<>();
        MaterialExample example = new MaterialExample();
        MaterialExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(name);
        if (StringUtil.isNotEmpty(model)) {
            criteria.andModelEqualTo(model);
        }
        if (StringUtil.isNotEmpty(color)) {
            criteria.andColorEqualTo(color);
        }
        if (StringUtil.isNotEmpty(colorCode)) {
            criteria.andColorCodeEqualTo(colorCode);
        }
        if (StringUtil.isNotEmpty(unit)) {
            criteria.andUnitEqualTo(unit);
        }
        if (unitId !=null) {
            criteria.andUnitIdEqualTo(unitId);
        }
        criteria.andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        list = materialMapper.selectByExample(example);
        if(list.size()==0) {
            //如果通过组合条件没有查到该产品，则通过编码再查一次
            MaterialExtend materialExtend = materialExtendService.getInfoByBarCode(barCode);
            if(materialExtend != null && materialExtend.getMaterialId()!=null) {
                Material material = new Material();
                material.setId(materialExtend.getMaterialId());
                list.add(material);
            }
        }
        return list;
    }

    private List<Material> getMaterialListByBarCode(String barCode) throws Exception {
        List<Material> list = new ArrayList<>();
        MaterialExtend materialExtend = materialExtendService.getInfoByBarCode(barCode);
        if(materialExtend != null && materialExtend.getMaterialId()!=null) {
            Material material = new Material();
            material.setId(materialExtend.getMaterialId());
            list.add(material);
        }
        return list;
    }

    /**
     * 写入初始库存
     * @param depotId
     * @param mId
     * @param stock
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void insertInitialStockByMaterialAndDepot(Long depotId, Long mId, BigDecimal stock, BigDecimal lowSafeStock, BigDecimal highSafeStock){
        MaterialInitialStock materialInitialStock = new MaterialInitialStock();
        materialInitialStock.setDepotId(depotId);
        materialInitialStock.setMaterialId(mId);
        stock = stock == null? BigDecimal.ZERO: stock;
        materialInitialStock.setNumber(stock);
        if(lowSafeStock!=null) {
            materialInitialStock.setLowSafeStock(lowSafeStock);
        }
        if(highSafeStock!=null) {
            materialInitialStock.setHighSafeStock(highSafeStock);
        }
        materialInitialStockMapper.insertSelective(materialInitialStock); //存入初始库存
    }

    /**
     * 写入当前库存
     * @param depotId
     * @param mId
     * @param stock
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void insertCurrentStockByMaterialAndDepot(Long depotId, Long mId, BigDecimal stock){
        MaterialCurrentStock materialCurrentStock = new MaterialCurrentStock();
        materialCurrentStock.setDepotId(depotId);
        materialCurrentStock.setMaterialId(mId);
        materialCurrentStock.setCurrentNumber(stock);
        materialCurrentStockMapper.insertSelective(materialCurrentStock); //存入初始库存
    }

    /**
     * 批量删除初始库存
     * @param mIdList
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void batchDeleteInitialStockByMaterialList(List<Long> mIdList){
        MaterialInitialStockExample example = new MaterialInitialStockExample();
        example.createCriteria().andMaterialIdIn(mIdList);
        materialInitialStockMapper.deleteByExample(example);
    }

    /**
     * 批量删除当前库存
     * @param mIdList
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void batchDeleteCurrentStockByMaterialList(List<Long> mIdList){
        MaterialCurrentStockExample example = new MaterialCurrentStockExample();
        example.createCriteria().andMaterialIdIn(mIdList);
        materialCurrentStockMapper.deleteByExample(example);
    }

    public BigDecimal parseBigDecimalEx(String str) throws Exception{
        if(!StringUtil.isEmpty(str)) {
            return  new BigDecimal(str);
        } else {
            return null;
        }
    }

    public BigDecimal parsePrice(String price, String ratio) throws Exception{
        if(StringUtil.isEmpty(price) || StringUtil.isEmpty(ratio)) {
            return BigDecimal.ZERO;
        } else {
            BigDecimal pr=new BigDecimal(price);
            BigDecimal r=new BigDecimal(ratio);
            return pr.multiply(r);
        }
    }

    /**
     * 根据产品获取初始库存-多仓库
     * @param depotList
     * @param materialId
     * @return
     */
    public BigDecimal getInitStockByMidAndDepotList(List<Long> depotList, Long materialId) {
        BigDecimal stock = BigDecimal.ZERO;
        MaterialInitialStockExample example = new MaterialInitialStockExample();
        if(depotList!=null && depotList.size()>0) {
            example.createCriteria().andMaterialIdEqualTo(materialId).andDepotIdIn(depotList)
                    .andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        } else {
            example.createCriteria().andMaterialIdEqualTo(materialId)
                    .andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        }
        List<MaterialInitialStock> list = materialInitialStockMapper.selectByExample(example);
        if(list!=null && list.size()>0) {
            for(MaterialInitialStock ms: list) {
                if(ms!=null) {
                    stock = stock.add(ms.getNumber() == null ? BigDecimal.ZERO : ms.getNumber());
                }
            }
        }
        return stock;
    }

    /**
     * 根据产品和仓库获取初始库存
     * @param materialId
     * @param depotId
     * @return
     */
    public BigDecimal getInitStock(Long materialId, Long depotId) {
        BigDecimal stock = BigDecimal.ZERO;
        MaterialInitialStockExample example = new MaterialInitialStockExample();
        example.createCriteria().andMaterialIdEqualTo(materialId).andDepotIdEqualTo(depotId)
                .andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<MaterialInitialStock> list = materialInitialStockMapper.selectByExample(example);
        if(list!=null && list.size()>0) {
            stock = list.get(0).getNumber() == null ? BigDecimal.ZERO : list.get(0).getNumber();
        }
        return stock;
    }

    /**
     * 根据产品和仓库获取当前库存
     * @param materialId
     * @param depotId
     * @return
     */
    public BigDecimal getCurrentStockByMaterialIdAndDepotId(Long materialId, Long depotId) {
        BigDecimal stock = BigDecimal.ZERO;
        MaterialCurrentStockExample example = new MaterialCurrentStockExample();
        example.createCriteria().andMaterialIdEqualTo(materialId).andDepotIdEqualTo(depotId)
                .andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<MaterialCurrentStock> list = materialCurrentStockMapper.selectByExample(example);
        if(list!=null && list.size()>0) {
            stock = list.get(0).getCurrentNumber();
        } else {
            stock = getInitStock(materialId,depotId);
        }
        return stock;
    }

    /**
     * 根据产品列表获取当前库存Map
     * @param list
     * @return
     */
    public Map<Long,BigDecimal> getCurrentStockMapByMaterialList(List<MaterialVo4Unit> list) {
        Map<Long,BigDecimal> map = new HashMap<>();
        List<Long> materialIdList = new ArrayList<>();
        for(MaterialVo4Unit materialVo4Unit: list) {
            materialIdList.add(materialVo4Unit.getId());
        }
        List<MaterialCurrentStock> mcsList = materialCurrentStockMapperEx.getCurrentStockMapByIdList(materialIdList);
        for(MaterialCurrentStock materialCurrentStock: mcsList) {
            map.put(materialCurrentStock.getMaterialId(), materialCurrentStock.getCurrentNumber());
        }
        return map;
    }

    public Map<Long,BigDecimal> getCurrentStockMapByMaterialId(List<Long> idList) {
        Map<Long,BigDecimal> map = new HashMap<>();
        List<MaterialCurrentStock> mcsList = materialCurrentStockMapperEx.getCurrentStockMapByIdList(idList);
        for(MaterialCurrentStock materialCurrentStock: mcsList) {
            map.put(materialCurrentStock.getMaterialId(), materialCurrentStock.getCurrentNumber());
        }
        return map;
    }

    /**
     * 根据产品和仓库获取安全库存信息
     * @param materialId
     * @param depotId
     * @return
     */
    public MaterialInitialStock getSafeStock(Long materialId, Long depotId) {
        MaterialInitialStock materialInitialStock = new MaterialInitialStock();
        MaterialInitialStockExample example = new MaterialInitialStockExample();
        example.createCriteria().andMaterialIdEqualTo(materialId).andDepotIdEqualTo(depotId)
                .andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<MaterialInitialStock> list = materialInitialStockMapper.selectByExample(example);
        if(list!=null && list.size()>0) {
            materialInitialStock = list.get(0);
        }
        return materialInitialStock;
    }

    public List<MaterialVo4Unit> getMaterialByMeId(Long meId) {
        List<MaterialVo4Unit> result = new ArrayList<MaterialVo4Unit>();
        try{
            if(meId != null) {
                result= materialMapperEx.getMaterialByMeId(meId);
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<MaterialVo4Unit> getMaterialByMeIdList(List<Long> meIdList) {
        List<MaterialVo4Unit> result = new ArrayList<MaterialVo4Unit>();
        try{
            if(meIdList != null) {
                result= materialMapperEx.getMaterialByMeIdList(meIdList);
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<MaterialBomVo4Info> getMaterialByProcessPrefix(List<String> prefixList,
                                                               List<String> projectList,
                                                               List<Double> amountList) {
        List<MaterialBomVo4Info> result = new ArrayList<MaterialBomVo4Info>();
        Map<String, MaterialBomVo4Info> barCodeToAmountMap = new HashMap<>();
        try{
            int length = prefixList.size();
            if (length == 0 || projectList.size() != length || amountList.size() != length) {
                throw new Exception();
            }
            for (int i = 0; i < length; i++) {
                List<MaterialBomVo4Info> list = materialBomMapperEx.selectNextLevelBomByPrefix(prefixList.get(i), projectList.get(i));
                for (MaterialBomVo4Info mb : list) {
                    if (barCodeToAmountMap.containsKey(mb.getBarCode())) {
                        BigDecimal usage = barCodeToAmountMap.get(mb.getBarCode()).getProcessUsage() == null ?
                                BigDecimal.ZERO : barCodeToAmountMap.get(mb.getBarCode()).getProcessUsage();
                        barCodeToAmountMap.get(mb.getBarCode()).setProcessUsage(
                                usage.add(mb.getProcessUsage() == null ?
                                        BigDecimal.ZERO :
                                        mb.getProcessUsage().multiply(BigDecimal.valueOf(amountList.get(i)))));
                    } else {
                        mb.setProcessUsage(mb.getProcessUsage() == null ?
                                BigDecimal.ZERO :
                                mb.getProcessUsage().multiply(BigDecimal.valueOf(amountList.get(i))));
                        barCodeToAmountMap.put(mb.getBarCode(), mb);
                    }
                }
            }
            for (MaterialBomVo4Info mb : barCodeToAmountMap.values()) {
                if (mb.getBarCode() != null && !"".equals(mb.getBarCode())) {
                    result.add(mb);
                }
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<String> getMaterialNameList() {
        return materialMapperEx.getMaterialNameList();
    }

    public List<MaterialVo4Unit> getMaterialByBarCode(String barCode) {
        String [] barCodeArray=barCode.split(",");
        return materialMapperEx.getMaterialByBarCode(barCodeArray);
    }

    public List<MaterialVo4Unit> getMaterialByBarCodeAndWithOutMId(String barCode, Long mId) {
        String [] barCodeArray=barCode.split(",");
        return materialMapperEx.getMaterialByBarCodeAndWithOutMId(barCodeArray, mId);
    }

    public List<MaterialInitialStockWithMaterial> getInitialStockWithMaterial(List<Long> depotList) {
        return materialMapperEx.getInitialStockWithMaterial(depotList);
    }

    public List<MaterialVo4Unit> getListWithStock(List<Long> depotList, List<Long> idList, String materialParam, Integer zeroStock,
                                                  String column, String order, Integer offset, Integer rows) throws Exception {
        Map<Long, BigDecimal> initialStockMap = new HashMap<>();
        List<MaterialInitialStockWithMaterial> initialStockList = getInitialStockWithMaterial(depotList);
        for (MaterialInitialStockWithMaterial mism: initialStockList) {
            initialStockMap.put(mism.getMaterialId(), mism.getNumber());
        }
        List<MaterialVo4Unit> dataList = materialMapperEx.getListWithStock(depotList, idList, materialParam, zeroStock, column, order, offset, rows);
        for(MaterialVo4Unit item: dataList) {
            item.setUnitName(null!=item.getUnitId()?item.getUnitName() + "[多单位]":item.getUnitName());
            item.setInitialStock(null!=initialStockMap.get(item.getId())?initialStockMap.get(item.getId()):BigDecimal.ZERO);
            item.setBigUnitStock(getBigUnitStock(item.getCurrentStock(), item.getUnitId()));
        }
        return dataList;
    }

    public int getListWithStockCount(List<Long> depotList, List<Long> idList, String materialParam, Integer zeroStock) {
        return materialMapperEx.getListWithStockCount(depotList, idList, materialParam, zeroStock);
    }

    public MaterialVo4Unit getTotalStockAndPrice(List<Long> depotList, List<Long> idList, String materialParam) {
        return materialMapperEx.getTotalStockAndPrice(depotList, idList, materialParam);
    }

    /**
     * 将小单位的库存换算为大单位的库存
     * @param stock
     * @param unitId
     * @return
     * @throws Exception
     */
    public String getBigUnitStock(BigDecimal stock, Long unitId) throws Exception {
        String bigUnitStock = "";
        if(null!= unitId) {
            Unit unit = unitService.getUnit(unitId);
            if(unit.getRatio()!=0 && stock!=null) {
                bigUnitStock = stock.divide(BigDecimal.valueOf(unit.getRatio()),2,BigDecimal.ROUND_HALF_UP) + unit.getOtherUnit();
            }
        }
        return bigUnitStock;
    }

    /**
     * 构造扩展信息
     * @param mpArr
     * @param m
     * @return
     */
    public String getMaterialOtherByParam(String[] mpArr, MaterialVo4Unit m) {
        String materialOther = "";
        for (int i = 0; i < mpArr.length; i++) {
            if (mpArr[i].equals("主壁厚")) {
                materialOther = materialOther +
                        ((m.getOtherField1() == null || m.getOtherField1().equals("")) ? "" : "\"" + mpArr[i] + "\":[" + m.getOtherField1() + "]");
            }
            if (mpArr[i].equals("模腔数")) {
                materialOther = materialOther +
                        ((m.getOtherField2() == null || m.getOtherField2().equals("")) ? "" : "\"" + mpArr[i] + "\":[" + m.getOtherField2() + "]");
            }
            if (mpArr[i].equals("模具重量")) {
                materialOther = materialOther +
                        ((m.getOtherField3() == null || m.getOtherField3().equals("")) ? "" : "\"" + mpArr[i] + "\":[" + m.getOtherField3() + "]");
            }
            if (mpArr[i].equals("浇口重量")) {
                materialOther = materialOther +
                        ((m.getOtherField4() == null || m.getOtherField4().equals("")) ? "" : "\"" + mpArr[i] + "\":[" + m.getOtherField4() + "]");
            }
            if (mpArr[i].equals("规格")) {
                materialOther = materialOther +
                        ((m.getOtherField5() == null || m.getOtherField5().equals("")) ? "" : "\"" + mpArr[i] + "\":[" + m.getOtherField5() + "]");
            }
//            if (mpArr[i].equals("保留1")) {
//                materialOther = materialOther +
//                        ((m.getOtherField6() == null || m.getOtherField6().equals("")) ? "" : "\"" + mpArr[i] + "\":[" + m.getOtherField6() + "]");
//            }
//            if (mpArr[i].equals("保留2")) {
//                materialOther = materialOther +
//                        ((m.getOtherField7() == null || m.getOtherField7().equals("")) ? "" : "\"" + mpArr[i] + "\":[" + m.getOtherField7() + "]");
//            }
//            if (mpArr[i].equals("保留3")) {
//                materialOther = materialOther +
//                        ((m.getOtherField8() == null || m.getOtherField8().equals("")) ? "" : "\"" + mpArr[i] + "\":[" + m.getOtherField8() + "]");
//            }
//            if (mpArr[i].equals("保留4")) {
//                materialOther = materialOther +
//                        ((m.getOtherField9() == null || m.getOtherField9().equals("")) ? "" : "\"" + mpArr[i] + "\":[" + m.getOtherField9() + "]");
//            }
//            if (mpArr[i].equals("保留5")) {
//                materialOther = materialOther +
//                        ((m.getOtherField10() == null || m.getOtherField10().equals("")) ? "\"" + mpArr[i] + "\":[" + ":[" + m.getOtherField10() + "]");
//            }
        }
        return materialOther;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchSetMaterialCurrentStock(String ids) throws Exception {
        int res = 0;
        List<Long> idList = StringUtil.strToLongList(ids);
        List<Depot> depotList = depotService.getAllList();
        for(Long mId: idList) {
            for(Depot depot: depotList) {
                depotItemService.updateCurrentStockFun(mId, depot.getId());
                res = 1;
            }
        }
        return res;
    }

    public int batchUpdate(JSONObject jsonObject) {
        String ids = jsonObject.getString("ids");
        String materialStr = jsonObject.getString("material");
        List<Long> idList = StringUtil.strToLongList(ids);
        Material material = JSONObject.parseObject(materialStr, Material.class);
        MaterialExample example = new MaterialExample();
        example.createCriteria().andIdIn(idList).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        return materialMapper.updateByExampleSelective(material, example);
    }
}
