package com.jsh.erp.service.person;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.AccountHeadMapperEx;
import com.jsh.erp.datasource.mappers.DepotHeadMapperEx;
import com.jsh.erp.datasource.mappers.PersonMapper;
import com.jsh.erp.datasource.mappers.PersonMapperEx;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PersonService {
    private Logger logger = LoggerFactory.getLogger(PersonService.class);

    @Resource
    private PersonMapper personMapper;

    @Resource
    private PersonMapperEx personMapperEx;
    @Resource
    private UserService userService;
    @Resource
    private LogService logService;
    @Resource
    private AccountHeadMapperEx accountHeadMapperEx;
    @Resource
    private DepotHeadMapperEx depotHeadMapperEx;

    public Person getPerson(long id)throws Exception {
        Person result=null;
        try{
            result=personMapper.selectByPrimaryKey(id);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<Person> getPersonListByIds(String ids)throws Exception {
        List<Long> idList = StringUtil.strToLongList(ids);
        List<Person> list = new ArrayList<>();
        try{
            PersonExample example = new PersonExample();
            example.createCriteria().andIdIn(idList);
            list = personMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<Person> getPerson()throws Exception {
        PersonExample example = new PersonExample();
        example.createCriteria().andEnabledEqualTo(true).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<Person> list=null;
        try{
            list=personMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<Person> select(String name, String type, String department, int offset, int rows)throws Exception {
        List<Person> list=null;
        try{
            list=personMapperEx.selectByConditionPerson(name, type, department, offset, rows);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public Long countPerson(String name, String type, String department)throws Exception {
        Long result=null;
        try{
            result=personMapperEx.countsByPerson(name, type, department);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
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
            List<Person> personList = new ArrayList<>();
            for (int i = 1; i < rightRows; i++) {
                String department = ExcelUtils.getContent(src, i, 0);
                String name = ExcelUtils.getContent(src, i, 1);
                String type = ExcelUtils.getContent(src, i, 2);
                //不允许为空
                if(StringUtil.isEmpty(department) || StringUtil.isEmpty(name) || StringUtil.isEmpty(type)) {
                    throw new BusinessRunTimeException(ExceptionConstants.PERSON_INFO_MISSING_CODE,
                            String.format(ExceptionConstants.PERSON_INFO_MISSING_MSG, i+1));
                }
                batchCheckExistPersonByParam(personList, department, name, type);
                Person person = new Person();
                person.setDepartment(department);
                person.setName(name);
                person.setType(type);
                person.setEnabled(true);
                personList.add(person);
            }
            for (Person person : personList) {
                personMapper.insertSelective(person);
            }
            logService.insertLog("人员",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_IMPORT)
                            .append(personList.size())
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
            logger.info(e.getMessage());
            info.code = 500;
            info.data = "导入失败";
        }
        return info;
    }

    /**
     * 批量校验excel中有无重复人员
     */
    public void batchCheckExistPersonByParam(List<Person> personList, String department, String name, String type) {
        for(Person person: personList){
            if(department.equals(person.getDepartment()) && name.equals(person.getName()) && type.equals(person.getType())){
                String info = department + "-" + name + "-" + type;
                throw new BusinessRunTimeException(ExceptionConstants.PERSON_EXCEL_IMPORT_EXIST_CODE,
                        String.format(ExceptionConstants.PERSON_EXCEL_IMPORT_EXIST_MSG, info));
            }
        }
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertPerson(JSONObject obj, HttpServletRequest request)throws Exception {
        Person person = JSONObject.parseObject(obj.toJSONString(), Person.class);
        int result=0;
        try{
            person.setEnabled(true);
            result=personMapper.insertSelective(person);
            logService.insertLog("经手人",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(person.getName()).toString(), request);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updatePerson(JSONObject obj, HttpServletRequest request)throws Exception {
        Person person = JSONObject.parseObject(obj.toJSONString(), Person.class);
        int result=0;
        try{
            result=personMapper.updateByPrimaryKeySelective(person);
            logService.insertLog("经手人",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(person.getName()).toString(), request);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deletePerson(Long id, HttpServletRequest request)throws Exception {
        return batchDeletePersonByIds(id.toString());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeletePerson(String ids, HttpServletRequest request) throws Exception{
        return batchDeletePersonByIds(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeletePersonByIds(String ids)throws Exception {
        int result =0;
        String [] idArray=ids.split(",");
        //校验财务主表	jsh_accounthead
        List<AccountHead> accountHeadList =null;
        try{
            accountHeadList=accountHeadMapperEx.getAccountHeadListByHandsPersonIds(idArray);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        if(accountHeadList!=null&&accountHeadList.size()>0){
            logger.error("异常码[{}],异常提示[{}],参数,HandsPersonIds[{}]",
                    ExceptionConstants.DELETE_FORCE_CONFIRM_CODE,ExceptionConstants.DELETE_FORCE_CONFIRM_MSG,ids);
            throw new BusinessRunTimeException(ExceptionConstants.DELETE_FORCE_CONFIRM_CODE,
                    ExceptionConstants.DELETE_FORCE_CONFIRM_MSG);
        }
        //校验单据主表	jsh_depot_head
        List<DepotHead> depotHeadList =null;
        try{
            depotHeadList=depotHeadMapperEx.getDepotHeadListByCreator(idArray);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        if(depotHeadList!=null&&depotHeadList.size()>0){
            logger.error("异常码[{}],异常提示[{}],参数,HandsPersonIds[{}]",
                    ExceptionConstants.DELETE_FORCE_CONFIRM_CODE,ExceptionConstants.DELETE_FORCE_CONFIRM_MSG,ids);
            throw new BusinessRunTimeException(ExceptionConstants.DELETE_FORCE_CONFIRM_CODE,
                    ExceptionConstants.DELETE_FORCE_CONFIRM_MSG);
        }
        //记录日志
        StringBuffer sb = new StringBuffer();
        sb.append(BusinessConstants.LOG_OPERATION_TYPE_DELETE);
        List<Person> list = getPersonListByIds(ids);
        for(Person person: list){
            sb.append("[").append(person.getName()).append("]");
        }
        logService.insertLog("经手人", sb.toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        //删除经手人
        try{
            result=personMapperEx.batchDeletePersonByIds(idArray);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    public int checkIsNameExist(Long id, String name) throws Exception{
        PersonExample example = new PersonExample();
        example.createCriteria().andIdNotEqualTo(id).andNameEqualTo(name).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<Person> list =null;
        try{
            list=personMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list==null?0:list.size();
    }

    public Map<Long,String> getPersonMap() throws Exception {
        List<Person> personList = getPerson();
        Map<Long,String> personMap = new HashMap<>();
        for(Person person : personList){
            personMap.put(person.getId(), person.getName());
        }
        return personMap;
    }

    public String getPersonByMapAndIds(Map<Long,String> personMap, String personIds)throws Exception {
        List<Long> ids = StringUtil.strToLongList(personIds);
        StringBuffer sb = new StringBuffer();
        for(Long id: ids){
            sb.append(personMap.get(id) + " ");
        }
        return sb.toString();
    }

    public List<Person> getPersonByType(String type)throws Exception {
        PersonExample example = new PersonExample();
        example.createCriteria().andTypeEqualTo(type).andEnabledEqualTo(true)
                .andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        example.setOrderByClause("sort asc, id desc");
        List<Person> list =null;
        try{
            list=personMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<Person> getAllPerson()throws Exception {
        PersonExample example = new PersonExample();
        example.createCriteria().andEnabledEqualTo(true)
                .andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        example.setOrderByClause("sort asc, id desc");
        List<Person> list =null;
        try{
            list=personMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<String> getDepartment()throws Exception {
        List<String> list =null;
        try{
            list=personMapper.getDepartment();
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<Person> getPersonByDepartment(String department)throws Exception {
        PersonExample example = new PersonExample();
        example.createCriteria().andDepartmentEqualTo(department).andEnabledEqualTo(true)
                .andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        example.setOrderByClause("sort asc, id desc");
        List<Person> list =null;
        try{
            list=personMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchSetStatus(Boolean status, String ids)throws Exception {
        logService.insertLog("经手人",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ENABLED).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        List<Long> personIds = StringUtil.strToLongList(ids);
        Person person = new Person();
        person.setEnabled(status);
        PersonExample example = new PersonExample();
        example.createCriteria().andIdIn(personIds);
        int result=0;
        try{
            result = personMapper.updateByExampleSelective(person, example);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }
}
