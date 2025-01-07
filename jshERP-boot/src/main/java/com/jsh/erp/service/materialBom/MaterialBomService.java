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
import com.jsh.erp.service.material.MaterialService;
import com.jsh.erp.service.productSupplier.ProductSupplierService;
import com.jsh.erp.service.project.ProjectService;
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
public class MaterialBomService {
    private Logger logger = LoggerFactory.getLogger(MaterialBomService.class);

    @Resource
    private LogService logService;
    @Resource
    private ProjectService projectService;

    @Resource
    private MaterialBomMapper materialBomMapper;

    @Resource
    private MaterialBomMapperEx materialBomMapperEx;

    @Resource
    private MaterialCategoryMapperEx materialCategoryMapperEx;

    @Resource
    private MaterialService materialService;

    @Resource
    private ProductSupplierService productSupplierService;

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
            String categoryId, String parent, String project, String materialParam, int offset, int rows) throws Exception{
        List<MaterialBomVo4Info> list = new ArrayList<>();
        if (isBomUser()) {
            if (StringUtil.isEmpty(project) || !getUserProjects().contains(project.trim().toLowerCase())) {
                return list;
            }
        }
        try{
            List<Long> idList = new ArrayList<>();
            if(StringUtil.isNotEmpty(categoryId)){
                idList = getListByParentId(Long.parseLong(categoryId));
            }
            list = materialBomMapperEx.selectMaterialBomNullUpper(parent, project, materialParam, idList, offset, rows);
            for (MaterialBomVo4Info bom : list) {
                bom.setUnit(bom.getmUnit());
                if (bom.getParent() != null && !"".equals(bom.getParent())) {
                    try {
                        List<ProductSupplierVo4Info> psList = productSupplierService.selectByIds(bom.getParent());
                        StringBuilder sb = new StringBuilder();
                        for (ProductSupplierVo4Info ps : psList) {
                            sb.append(ps.getModel() + ";");
                        }
                        String parentStr = sb.toString();
                        bom.setParentStr(parentStr.substring(0, parentStr.length() - 1));
                    } catch(Exception e) {}
                }
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    boolean isBomUser() throws Exception {
        User user = userService.getCurrentUser();
        List<UserEx> bomUser = userService.selectByNameAndOrgan(user.getUsername(), "BOM", 0, 1);
        if (bomUser.size() > 0) {
            return true;
        }
        return false;
    }

    private Set<String> getUserProjects() throws Exception {
        User user = userService.getCurrentUser();
        List<Project> projects = projectService.getAllProject();
        Set<String> pass = new HashSet<>();
        for (Project p : projects) {
            if (p.getUsers() != null && !"".equals(p.getUsers())) {
                String[] userStr = p.getUsers().split(",");
                for (String str : userStr) {
                    if (Long.parseLong(str) == user.getId().longValue()) {
                        pass.add(p.getName().toLowerCase().trim());
                    }
                }
            }
        }
        return pass;
    }

    public List<MaterialBomVo4Info> selectMaterialBomWithUpper(
            String parent, String upper, String project, String materialParam) throws Exception{
        List<MaterialBomVo4Info> list = new ArrayList<>();
        try{
            list = materialBomMapperEx.selectMaterialBomWithUpper(parent, upper, project, materialParam);
            for (MaterialBomVo4Info bom : list) {
                bom.setUnit(bom.getmUnit());
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<MaterialBomVo4Info> getMaterialBomTree(String parent, String barCode, String project) throws Exception{
        List<MaterialBomVo4Info> list = new ArrayList<>();
        try{
            list = materialBomMapperEx.getMaterialBomTree(parent, barCode, project);
        } catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public Long countMaterialBom(String categoryId, String parent, String project, String materialParam) throws Exception{
        Long result = null;
        if (isBomUser()) {
            if (StringUtil.isEmpty(project) || !getUserProjects().contains(project.trim().toLowerCase())) {
                return (long) 0;
            }
        }
        try{
            List<Long> idList = new ArrayList<>();
            if(StringUtil.isNotEmpty(categoryId)){
                idList = getListByParentId(Long.parseLong(categoryId));
            }
            result = materialBomMapperEx.countMaterialBomNullUpper(parent, project, materialParam, idList);
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
                            .append("[" + m.getParent() + "]")
                            .append("[" + m.getProject() + "]")
                            .append("[" + m.getUpper() + "]")
                            .append("[" + m.getBarCode() + "]").toString(), request);
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
    public int addBomChild(String parent, String upper, String project, HttpServletRequest request)throws Exception {
        MaterialBom m = new MaterialBom();
        m.setParent(parent);
        m.setUpper(upper);
        m.setProject(project);
        m.setDeleteFlag("0");
        try{
            List<MaterialVo4Unit> materials = materialService.select(
                    "BOM新增物料", null, null, null, null, null, null, null, null, 0, 10);
            m.setBarCode(materials.get(0).getmBarCode());
            Set<String> toCheck = new HashSet<>();
            toCheck.add(upper);
            int count = 0;
            while (true) {
                count += 1;
                if (count == 100) {
                    throw new BusinessRunTimeException(500, "BOM存在太多相同物料，请联系管理员！");
                }
                Set<String> nextToCheck = new HashSet<>();
                boolean found = false;
                for (String code : toCheck) {
                    List<MaterialBomVo4Info> list = selectMaterialBomWithUpper(parent, null, project, code);
                    if (list != null) {
                        for (MaterialBomVo4Info bom : list) {
                            if (bom.getBarCode().equals(materials.get(0).getmBarCode())) {
                                found = true;
                                break;
                            } else if (bom.getUpper() != null && !"".equals(bom.getUpper())) {
                                nextToCheck.add(bom.getUpper());
                            }
                        }
                    }
                }
                if (found) {
                    throw new BusinessRunTimeException(500, "新增BOM的上级存在与新增BOM相同的物料，请先修改上级！");
                } else if (nextToCheck.isEmpty()) {
                    break;
                } else {
                    toCheck = new HashSet<>(nextToCheck);
                }
            }
            materialBomMapper.insertSelective(m);
            logService.insertLog("产品BOM",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD)
                            .append("[" + m.getParent() + "]")
                            .append("[" + m.getProject() + "]")
                            .append("[" + m.getUpper() + "]")
                            .append("[" + m.getBarCode() + "]").toString(), request);
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
        MaterialBom old = materialBomMapper.selectByPrimaryKey(materialBom.getId());
        List<MaterialBomVo4Info> oldChildren = getMaterialBomTree(old.getParent(), old.getBarCode(), old.getProject());
        try{
            if(old.getUpper() != null && !"".equals(old.getUpper())) {
                Set<String> toCheck = new HashSet<>();
                toCheck.add(old.getUpper());
                int count = 0;
                while (true) {
                    count += 1;
                    if (count == 100) {
                        throw new BusinessRunTimeException(500, "BOM存在太多相同物料，请联系管理员！");
                    }
                    Set<String> nextToCheck = new HashSet<>();
                    boolean found = false;
                    for (String code : toCheck) {
                        List<MaterialBomVo4Info> list = selectMaterialBomWithUpper(old.getParent(), null, old.getProject(), code);
                        if (list != null) {
                            for (MaterialBomVo4Info bom : list) {
                                if (bom.getBarCode().equals(materialBom.getBarCode())) {
                                    found = true;
                                    break;
                                } else if (bom.getUpper() != null && !"".equals(bom.getUpper())) {
                                    nextToCheck.add(bom.getUpper());
                                }
                            }
                        }
                    }
                    if (found) {
                        throw new BusinessRunTimeException(500, "新增BOM的上级存在与新增BOM相同的物料，请先修改上级！");
                    } else if (nextToCheck.isEmpty()) {
                        break;
                    } else {
                        toCheck = new HashSet<>(nextToCheck);
                    }
                }
            }
            materialBomMapper.updateByPrimaryKeySelective(materialBom);
            String oldParent = old.getParent() == null ? "" : old.getParent();
            String newParent = materialBom.getParent() == null ? "" : materialBom.getParent();
            if (StringUtil.isEmpty(materialBom.getUpper()) && !oldParent.equals(newParent)) {
                List<MaterialBomVo4Info> childrenList = new ArrayList<>();
                flattenTree(oldChildren, childrenList);
                logger.info("XXXXX 即将更新" + childrenList.size() + "个子节点的parent");
                for (MaterialBomVo4Info child : childrenList) {
                    child.setParent(materialBom.getParent());
                    materialBomMapper.updateByPrimaryKeySelective(child);
                }
            }
            String oldProject = old.getProject() == null ? "" : old.getProject();
            String newProject = materialBom.getProject() == null ? "" : materialBom.getProject();
            if (StringUtil.isEmpty(materialBom.getUpper()) && !oldProject.equals(newProject)) {
                List<MaterialBomVo4Info> childrenList = new ArrayList<>();
                flattenTree(oldChildren, childrenList);
                logger.info("XXXXX 即将更新" + childrenList.size() + "个子节点的project");
                for (MaterialBomVo4Info child : childrenList) {
                    child.setProject(materialBom.getProject());
                    materialBomMapper.updateByPrimaryKeySelective(child);
                }
            }
            logService.insertLog("产品BOM",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT)
                            .append("[" + materialBom.getParent() + "]")
                            .append("[" + materialBom.getProject() + "]")
                            .append("[" + materialBom.getUpper() + "]")
                            .append("[" + materialBom.getBarCode() + "]").toString(), request);
            return 1;
        } catch(Exception e){
            JshException.writeFail(logger, e);
            return 0;
        }
    }

    public void flattenTree(List<MaterialBomVo4Info> tree, List<MaterialBomVo4Info> res) {
        for (MaterialBomVo4Info m : tree) {
            res.add(m);
            if (m.getChildren() != null && m.getChildren().size() > 0) {
                flattenTree(m.getChildren(), res);
            }
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
            for (String id : idArray) {
                MaterialBom bom = materialBomMapper.selectByPrimaryKey(Long.parseLong(id));
                List<MaterialBomVo4Info> tree;
                if (bom.getUpper() == null || bom.getUpper().equals("")) {
                    logger.info("XXXXX 尝试删除总成");
                    tree = materialBomMapperEx.getMaterialBomTree(bom.getParent(), bom.getBarCode(), bom.getProject());
                } else {
                    logger.info("XXXXX 尝试删除子节点");
                    logger.info(new StringBuilder().append("XXXXX 尝试删除")
                            .append("[" + bom.getParent() + "]")
                            .append("[" + bom.getProject() + "]")
                            .append("[" + bom.getUpper() + "]")
                            .append("[" + bom.getBarCode() + "]").toString());
                    tree = materialBomMapperEx.getMaterialBomTreeWithUpper(bom.getParent(), bom.getUpper(), bom.getBarCode(), bom.getProject());
                }
                logger.info("XXXXX 找到匹配节点" + tree.size() + "个");
                if (tree == null) {
                    throw new BusinessRunTimeException(500, "找不到要删除的BOM条目！");
                }
                boolean found = false;
                MaterialBomVo4Info toDelete = new MaterialBomVo4Info();
                for (MaterialBomVo4Info info : tree) {
                    if (info.getId().longValue() == Long.parseLong(id)) {
                        found = true;
                        toDelete = info;
                        break;
                    }
                }
                if (!found) {
                    throw new BusinessRunTimeException(500, "找不到要删除的BOM条目！");
                }
                logger.info("XXXXX 子节点" + (toDelete.getChildren() == null ? 0 : toDelete.getChildren().size()) + "个");
                if (toDelete.getChildren() != null && toDelete.getChildren().size() > 0) {
                    throw new BusinessRunTimeException(500, "要删除的BOM条目含有下级，请先删除所有下级条目！");
                }
            }
            return materialBomMapperEx.batchDeleteMaterialBomByIds(idArray);
        } catch (BusinessRunTimeException ex) {
            throw new BusinessRunTimeException(ex.getCode(), ex.getMessage());
        } catch (Exception e){
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
            List<MaterialBom> bomList = new ArrayList<>();
            for (int i = 1; i < rightRows; i++) {
                String parentCode = ExcelUtils.getContent(src, i, 0); //总成
                String upperCode = ExcelUtils.getContent(src, i, 1); //总成
                if (StringUtil.isEmpty(parentCode)) {
                    continue;
                }
                String barCode = ExcelUtils.getContent(src, i, 1); //编码
                String partNo = ExcelUtils.getContent(src, i, 2); //零件号
                String project = ExcelUtils.getContent(src, i, 3); //项目
                String department = ExcelUtils.getContent(src, i, 4); //部门
                String source = ExcelUtils.getContent(src, i, 5); //物料来源
                String processUsage = ExcelUtils.getContent(src, i, 6); //用量
                String unit = ExcelUtils.getContent(src, i, 7); //单位

                // 批量校验excel中有无重复BOM
                MaterialBom bom = new MaterialBom();
                bom.setParent("");
                bom.setUpper("");
                bom.setPartNo(partNo);
                bom.setBarCode(barCode);
                bom.setProject(project);
                bom.setDepartment(department);
                bom.setSource(source);
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
            logger.error(e.getMessage(), e);
            info.code = 500;
            info.data = "导入失败";
        }
        return info;
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
