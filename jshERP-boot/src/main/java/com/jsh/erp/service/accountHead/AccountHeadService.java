package com.jsh.erp.service.accountHead;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Table;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.AccountHeadMapper;
import com.jsh.erp.datasource.mappers.AccountHeadMapperEx;
import com.jsh.erp.datasource.mappers.AccountItemMapper;
import com.jsh.erp.datasource.mappers.AccountItemMapperEx;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.accountItem.AccountItemService;
import com.jsh.erp.service.depotHead.DepotHeadService;
import com.jsh.erp.service.depotItem.DepotItemService;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.service.material.MaterialService;
import com.jsh.erp.service.orgaUserRel.OrgaUserRelService;
import com.jsh.erp.service.productSupplier.ProductSupplierService;
import com.jsh.erp.service.sequence.SequenceService;
import com.jsh.erp.service.supplier.SupplierService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.StringUtil;
import com.jsh.erp.utils.Tools;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.jsh.erp.utils.Tools.getCenternTime;

@Service
public class AccountHeadService {
    private Logger logger = LoggerFactory.getLogger(AccountHeadService.class);
    @Resource
    private AccountHeadMapper accountHeadMapper;
    @Resource
    private AccountItemMapper accountItemMapper;
    @Resource
    private AccountHeadMapperEx accountHeadMapperEx;
    @Resource
    private OrgaUserRelService orgaUserRelService;
    @Resource
    private AccountItemService accountItemService;
    @Resource
    private DepotHeadService depotHeadService;
    @Resource
    private DepotItemService depotItemService;
    @Resource
    private UserService userService;
    @Resource
    private MaterialService materialService;
    @Resource
    private ProductSupplierService productSupplierService;
    @Resource
    private SupplierService supplierService;
    @Resource
    private SequenceService sequenceService;
    @Resource
    private LogService logService;
    @Resource
    private AccountItemMapperEx accountItemMapperEx;

    public AccountHead getAccountHead(long id) throws Exception {
        AccountHead result=null;
        try{
            result=accountHeadMapper.selectByPrimaryKey(id);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<AccountHead> getAccountHeadListByIds(String ids)throws Exception {
        List<Long> idList = StringUtil.strToLongList(ids);
        List<AccountHead> list = new ArrayList<>();
        try{
            AccountHeadExample example = new AccountHeadExample();
            example.createCriteria().andIdIn(idList);
            list = accountHeadMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<AccountHead> getAccountHeadByIds(List<Long> idList)throws Exception {
        List<AccountHead> list = new ArrayList<>();
        try{
            AccountHeadExample example = new AccountHeadExample();
            example.createCriteria().andIdIn(idList).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
            list = accountHeadMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<AccountHead> getAccountHead() throws Exception{
        AccountHeadExample example = new AccountHeadExample();
        List<AccountHead> list=null;
        try{
            list=accountHeadMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<AccountHeadVo4ListEx> select(String type, String roleType, String billNo, String beginTime, String endTime,
                                             Long organId, Long creator, Long handsPersonId, Long accountId, String status,
                                             String remark, String number, int offset, int rows) throws Exception{
        List<AccountHeadVo4ListEx> resList = new ArrayList<>();
        try{
            String [] creatorArray = getCreatorArray(roleType);
            beginTime = Tools.parseDayToTime(beginTime,BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            List<AccountHeadVo4ListEx> list = accountHeadMapperEx.selectByConditionAccountHead(type, creatorArray, billNo,
                    beginTime, endTime, organId, creator, handsPersonId, accountId, status, remark, number, offset, rows);
            if (null != list) {
                for (AccountHeadVo4ListEx ah : list) {
                    if(ah.getChangeAmount() != null) {
                        if(BusinessConstants.TYPE_MONEY_IN.equals(ah.getType())) {
                            ah.setChangeAmount(ah.getChangeAmount());
                        } else if(BusinessConstants.TYPE_MONEY_OUT.equals(ah.getType())) {
                            ah.setChangeAmount(BigDecimal.ZERO.subtract(ah.getChangeAmount()));
                        } else {
                            ah.setChangeAmount(ah.getChangeAmount().abs());
                        }
                    }
                    if(ah.getTotalPrice() != null) {
                        if(BusinessConstants.TYPE_MONEY_IN.equals(ah.getType())) {
                            ah.setTotalPrice(ah.getTotalPrice());
                        } else if(BusinessConstants.TYPE_MONEY_OUT.equals(ah.getType())) {
                            ah.setTotalPrice(BigDecimal.ZERO.subtract(ah.getTotalPrice()));
                        } else {
                            ah.setTotalPrice(ah.getTotalPrice().abs());
                        }
                    }
                    if(ah.getBillTime() !=null) {
                        ah.setBillTimeStr(getCenternTime(ah.getBillTime()));
                    }
                    resList.add(ah);
                }
            }
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return resList;
    }

    public Long countAccountHead(String type, String roleType, String billNo, String beginTime, String endTime,
                                 Long organId, Long creator, Long handsPersonId, Long accountId, String status,
                                 String remark, String number) throws Exception{
        Long result=null;
        try{
            String [] creatorArray = getCreatorArray(roleType);
            beginTime = Tools.parseDayToTime(beginTime,BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime,BusinessConstants.DAY_LAST_TIME);
            result = accountHeadMapperEx.countsByAccountHead(type, creatorArray, billNo,
                    beginTime, endTime, organId, creator, handsPersonId, accountId, status, remark, number);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 根据角色类型获取操作员数组
     * @param roleType
     * @return
     * @throws Exception
     */
    private String[] getCreatorArray(String roleType) throws Exception {
        String creator = "";
        User user = userService.getCurrentUser();
        if(BusinessConstants.ROLE_TYPE_PRIVATE.equals(roleType)) {
            creator = user.getId().toString();
        } else if(BusinessConstants.ROLE_TYPE_THIS_ORG.equals(roleType)) {
            creator = orgaUserRelService.getUserIdListByUserId(user.getId());
        }
        String [] creatorArray=null;
        if(StringUtil.isNotEmpty(creator)){
            creatorArray = creator.split(",");
        }
        return creatorArray;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertAccountHead(JSONObject obj, HttpServletRequest request) throws Exception{
        AccountHead accountHead = JSONObject.parseObject(obj.toJSONString(), AccountHead.class);
        int result=0;
        try{
            User userInfo=userService.getCurrentUser();
            accountHead.setCreator(userInfo==null?null:userInfo.getId());
            result = accountHeadMapper.insertSelective(accountHead);
            logService.insertLog("财务",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(accountHead.getBillNo()).toString(), request);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateAccountHead(JSONObject obj, HttpServletRequest request)throws Exception {
        AccountHead accountHead = JSONObject.parseObject(obj.toJSONString(), AccountHead.class);
        int result=0;
        try{
            result = accountHeadMapper.updateByPrimaryKeySelective(accountHead);
            logService.insertLog("财务",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(accountHead.getBillNo()).toString(), request);
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteAccountHead(Long id, HttpServletRequest request)throws Exception {
        return batchDeleteAccountHeadByIds(id.toString());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteAccountHead(String ids, HttpServletRequest request)throws Exception {
        return batchDeleteAccountHeadByIds(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteAccountHeadByIds(String ids)throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append(BusinessConstants.LOG_OPERATION_TYPE_DELETE);
        List<AccountHead> list = getAccountHeadListByIds(ids);
        List<Long> needRecalcHeaderIdList = new ArrayList<>();
        Set<Long> needRecalcBillNoSet = new HashSet<>();
        for(AccountHead accountHead: list){
            sb.append("[").append(accountHead.getBillNo()).append("]");
            if("1".equals(accountHead.getStatus())) {
                throw new BusinessRunTimeException(ExceptionConstants.ACCOUNT_HEAD_UN_AUDIT_DELETE_FAILED_CODE,
                        String.format(ExceptionConstants.ACCOUNT_HEAD_UN_AUDIT_DELETE_FAILED_MSG));
            }
            if("收预付款".equals(accountHead.getType())){
                if (accountHead.getOrganId() != null) {
                    //删除时需要从会员扣除预付款
                    supplierService.updateAdvanceIn(accountHead.getOrganId(), BigDecimal.ZERO.subtract(accountHead.getTotalPrice()));
                }
            }
            if (needRecalc(accountHead.getType())) {
                needRecalcHeaderIdList.add(accountHead.getId());
            }
        }
        User userInfo=userService.getCurrentUser();
        String [] idArray=ids.split(",");
        List<AccountItem> needRecalcAccountItems = accountItemService.selectByHeaderIds(needRecalcHeaderIdList);
        for (AccountItem item : needRecalcAccountItems) {
            if (item.getBillId() != null) {
                needRecalcBillNoSet.add(item.getBillId());
            }
        }
        //删除子表
        accountItemMapperEx.batchDeleteAccountItemByHeadIds(new Date(),userInfo==null?null:userInfo.getId(),idArray);
        //删除主表
        accountHeadMapperEx.batchDeleteAccountHeadByIds(new Date(),userInfo==null?null:userInfo.getId(),idArray);
        if (!needRecalcBillNoSet.isEmpty()) {
            depotHeadService.recalcDepotHeadPayments(needRecalcBillNoSet);
        }
        logService.insertLog("财务", sb.toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        return 1;
    }

    private boolean needRecalc(String type) {
        return "采购定金".equals(type) || "采购付款".equals(type) || "采购退款".equals(type)
                || "销售定金".equals(type) || "销售收款".equals(type) || "销售退款".equals(type);
    }

    /**
     * 校验单据编号是否存在
     * @param id
     * @param billNo
     * @return
     * @throws Exception
     */
    public int checkIsBillNoExist(Long id, String billNo)throws Exception {
        AccountHeadExample example = new AccountHeadExample();
        example.createCriteria().andIdNotEqualTo(id).andBillNoEqualTo(billNo).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<AccountHead> list = null;
        try{
            list = accountHeadMapper.selectByExample(example);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        return list==null?0:list.size();
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchSetStatus(String status, String accountHeadIds)throws Exception {
        int result = 0;
        try{
            List<Long> ahIds = new ArrayList<>();
            List<Long> needRecalcAhIds = new ArrayList<>();
            List<Long> ids = StringUtil.strToLongList(accountHeadIds);
            for(Long id: ids) {
                AccountHead accountHead = getAccountHead(id);
                if("0".equals(status)){
                    if("1".equals(accountHead.getStatus())) {
                        ahIds.add(id);
                        if (needRecalc(accountHead.getType())) {
                            needRecalcAhIds.add(id);
                        }
                    }
                } else if("1".equals(status)){
                    if("0".equals(accountHead.getStatus())) {
                        ahIds.add(id);
                        if (needRecalc(accountHead.getType())) {
                            needRecalcAhIds.add(id);
                        }
                    }
                }
            }
            if(ahIds.size()>0) {
                AccountHead accountHead = new AccountHead();
                accountHead.setStatus(status);
                AccountHeadExample example = new AccountHeadExample();
                example.createCriteria().andIdIn(ahIds);
                result = accountHeadMapper.updateByExampleSelective(accountHead, example);
                if (needRecalcAhIds.size()>0) {
                    Set<Long> needRecalcBillNoSet = new HashSet<>();
                    List<AccountItem> needRecalcAccountItems = accountItemService.selectByHeaderIds(needRecalcAhIds);
                    for (AccountItem item : needRecalcAccountItems) {
                        if (item.getBillId() != null) {
                            needRecalcBillNoSet.add(item.getBillId());
                        }
                    }
                    if (needRecalcBillNoSet.size()>0) {
                        depotHeadService.recalcDepotHeadPayments(needRecalcBillNoSet);
                    }
                }
            } else {
                return 1;
            }
        }catch(Exception e){
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void addAccountHeadAndDetail(String beanJson, String rows, HttpServletRequest request) throws Exception {
        AccountHead accountHead = JSONObject.parseObject(beanJson, AccountHead.class);
        //校验单号是否重复
        if(checkIsBillNoExist(0L, accountHead.getBillNo())>0) {
            throw new BusinessRunTimeException(ExceptionConstants.ACCOUNT_HEAD_BILL_NO_EXIST_CODE,
                    String.format(ExceptionConstants.ACCOUNT_HEAD_BILL_NO_EXIST_MSG));
        }
        User userInfo=userService.getCurrentUser();
        accountHead.setCreator(userInfo==null?null:userInfo.getId());
        if(StringUtil.isEmpty(accountHead.getStatus())) {
            accountHead.setStatus(BusinessConstants.BILLS_STATUS_UN_AUDIT);
        }
        accountHeadMapper.insertSelective(accountHead);
        //根据单据编号查询单据id
        AccountHeadExample dhExample = new AccountHeadExample();
        dhExample.createCriteria().andBillNoEqualTo(accountHead.getBillNo()).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<AccountHead> list = accountHeadMapper.selectByExample(dhExample);
        if(list!=null) {
            Long headId = list.get(0).getId();
            String type = list.get(0).getType();
            /**处理单据子表信息*/
            accountItemService.saveDetials(rows, headId, type, request);
        }
        if("收预付款".equals(accountHead.getType())){
            supplierService.updateAdvanceIn(accountHead.getOrganId(), accountHead.getTotalPrice());
        }
        if (needRecalc(accountHead.getType())) {
            depotHeadService.recalcDepotHeadPayments(getImpactedBillIds(rows));
        }
        logService.insertLog("财务单据",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(accountHead.getBillNo()).toString(), request);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void generateStatement(String numbers, String beginTime, String endTime) throws Exception {
        List<DepotHead> depotHeadList = new ArrayList<>();
        String[] numberArr = numbers.split(",");
        for (String number : numberArr) {
            depotHeadList.add(depotHeadService.getDepotHead(number));
        }
        Long supplierId = depotHeadList.get(0).getOrganId();
        Supplier supplier = supplierService.getSupplier(supplierId);
        Map<String, Map<String, BigDecimal>> barCodeDateToAmountMap = new HashMap<>();

        for (DepotHead depotHead : depotHeadList) {
            List<DepotItemVo4WithInfoEx> depotItemList = depotItemService.getDetailList(depotHead.getId());
            for (DepotItemVo4WithInfoEx depotItem : depotItemList) {
                if (!barCodeDateToAmountMap.containsKey(depotItem.getBarCode())) {
                    barCodeDateToAmountMap.put(depotItem.getBarCode(), new HashMap<>());
                }
                barCodeDateToAmountMap.get(depotItem.getBarCode()).merge(
                        new SimpleDateFormat("yyyy/MM/dd").format(depotHead.getOperTime()),
                        depotItem.getOperNumber(),
                        BigDecimal::add);
            }
        }
        generateStatement(supplier, beginTime, endTime, barCodeDateToAmountMap);
    }

    private void generateStatement(Supplier supplier, String beginTime, String endTime,
                                   Map<String, Map<String, BigDecimal>> barCodeDateToAmountMap) throws Exception {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet xssfSheet = xssfWorkbook.createSheet("对账单");
        XSSFRow titleRow, supplierRow, headRow; // 行
        titleRow = xssfSheet.getRow(0);
        if (titleRow == null) {
            titleRow = xssfSheet.createRow(0);
        }
        titleRow.createCell(0).setCellValue("对账单" + "(" + beginTime + "至" + endTime + ")");

        supplierRow = xssfSheet.getRow(1);
        if (supplierRow == null) {
            supplierRow = xssfSheet.createRow(1);
        }
        supplierRow.createCell(0).setCellValue("供应商名称：" + supplier.getSupplier());
        supplierRow.createCell(1).setCellValue("联系方式：" + supplier.getPhoneNum());

        headRow = xssfSheet.getRow(2);
        if (headRow == null) {
            headRow = xssfSheet.createRow(2);
        }
        headRow.createCell(0).setCellValue("物料型号");
        headRow.createCell(1).setCellValue("规格");
        headRow.createCell(2).setCellValue("单位");
        headRow.createCell(3).setCellValue("单价（未税，小数点后4位）");
        Set<String> dateSet = new HashSet<>();
        for (String barCode : barCodeDateToAmountMap.keySet()) {
            for (String date : barCodeDateToAmountMap.get(barCode).keySet()) {
                dateSet.add(date);
            }
        }
        List<String> dateList = dateSet.stream()
                .map(s -> {
                    try {
                        return new SimpleDateFormat("yyyy/MM/dd").parse(s);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sorted()
                .map(d -> new SimpleDateFormat("yyyy/MM/dd").format(d))
                .collect(Collectors.toList());
        int i = 0;
        for (; i < dateList.size(); i++) {
            headRow.createCell(4 + i).setCellValue(dateList.get(i).substring(5));
        }
        headRow.createCell(4 + i).setCellValue("数量合计");
        headRow.createCell(5 + i).setCellValue("金额（未税，小数点后4位）");
        headRow.createCell(6 + i).setCellValue("税率");
        headRow.createCell(7 + i).setCellValue("税额（小数点后4位）");
        headRow.createCell(8 + i).setCellValue("金额（含税，小数点后4位）");

        int rowCnt = 3;
        double amountTotal = 0.0;
        double priceNoTaxTotal = 0.0;
        double taxTotal = 0.0;
        double priceTotal = 0.0;
        for (String barCode : barCodeDateToAmountMap.keySet()) {
            XSSFRow row = xssfSheet.getRow(rowCnt);
            if (row == null) {
                row = xssfSheet.createRow(rowCnt);
            }
            row.createCell(0).setCellValue(rowCnt - 2);
            row.createCell(1).setCellValue(barCode);
            List<MaterialVo4Unit> material = materialService.getMaterialByBarCode(barCode);
            row.createCell(2).setCellValue(material.get(0).getModel());
            row.createCell(3).setCellValue(material.get(0).getUnitName());
            List<ProductSupplierVo4Info> productSupplier = productSupplierService.getProductSupplierList(supplier.getSupplier(), barCode);
            double priceNoTax = productSupplier.get(0).getPriceNoTax() == null ? 0.0 : productSupplier.get(0).getPriceNoTax().doubleValue();
            double taxRate = productSupplier.get(0).getTaxRate() == null ? 0.0 : productSupplier.get(0).getTaxRate().doubleValue();

            row.createCell(4).setCellValue(priceNoTax);
            double materialTotal = 0.0;
            int dateIndex = 0;
            for (; dateIndex < dateList.size(); dateIndex++) {
                XSSFCell cell = row.createCell(4 + dateIndex);
                if (barCodeDateToAmountMap.get(barCode).containsKey(dateList.get(dateIndex))) {
                    double value = barCodeDateToAmountMap.get(barCode).get(dateList.get(dateIndex)).doubleValue();
                    cell.setCellValue(value);
                    materialTotal += value;
                }
            }
            amountTotal += materialTotal;
            priceNoTaxTotal += materialTotal * priceNoTax;
            taxTotal += materialTotal * priceNoTax * taxRate;
            priceTotal += materialTotal * priceNoTax * (1.0 + taxRate);

            row.createCell(4 + dateIndex).setCellValue(materialTotal);
            row.createCell(5 + dateIndex).setCellValue(materialTotal * priceNoTax);
            row.createCell(6 + dateIndex).setCellValue(taxRate);
            row.createCell(7 + dateIndex).setCellValue(materialTotal * priceNoTax * taxRate);
            row.createCell(8 + dateIndex).setCellValue(materialTotal * priceNoTax * (1.0 + taxRate));
            rowCnt++;
        }
        XSSFRow row = xssfSheet.getRow(rowCnt);
        if (row == null) {
            row = xssfSheet.createRow(rowCnt);
        }
        row.createCell(1).setCellValue("合计");
        row.createCell(4 + i).setCellValue(amountTotal);
        row.createCell(5 + i).setCellValue(priceNoTaxTotal);
        row.createCell(7 + i).setCellValue(taxTotal);
        row.createCell(8 + i).setCellValue(priceTotal);

        String accountNumber = "FK" + sequenceService.buildOnlyNumber();
        File path = new File("/opt/jshERP/upload" + File.separator + "statement" + File.separator);
        if (!path.exists()) {
            path.mkdirs();
        }
        File statementFile = new File(path,  accountNumber + ".xlsx");
        FileOutputStream outputStream = new FileOutputStream(statementFile);
        xssfWorkbook.write(outputStream);
        outputStream.close();
        xssfWorkbook.close();

        AccountHead accountHead = new AccountHead();
        accountHead.setCreator(userService.getCurrentUser().getId());
        accountHead.setOrganId(supplier.getId());
        accountHead.setTotalPrice(BigDecimal.valueOf(priceTotal));
        accountHead.setBillNo(accountNumber);
        accountHead.setBillTime(new Date());
        accountHead.setType("供应商对账");
        accountHead.setStatus("0");
        accountHead.setDeleteFlag("0");
        accountHead.setFileName("statement/" + accountNumber + ".xlsx");
        accountHeadMapper.insertSelective(accountHead);
    }

    private Set<Long> getImpactedBillIds(String rows) throws Exception {
        JSONArray rowArr = JSONArray.parseArray(rows);
        Set<Long> result = new HashSet<>();
        if (null != rowArr && rowArr.size()>0) {
            for (int i = 0; i < rowArr.size(); i++) {
                JSONObject tempInsertedJson = JSONObject.parseObject(rowArr.getString(i));
                if (tempInsertedJson.get("billNumber") != null && !tempInsertedJson.get("billNumber").equals("")) {
                    String billNo = tempInsertedJson.getString("billNumber");
                    result.add(depotHeadService.getDepotHead(billNo).getId());
                }
            }
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void updateAccountHeadAndDetail(String beanJson, String rows, HttpServletRequest request) throws Exception {
        AccountHead accountHead = JSONObject.parseObject(beanJson, AccountHead.class);
        //校验单号是否重复
        if(checkIsBillNoExist(accountHead.getId(), accountHead.getBillNo())>0) {
            throw new BusinessRunTimeException(ExceptionConstants.ACCOUNT_HEAD_BILL_NO_EXIST_CODE,
                    String.format(ExceptionConstants.ACCOUNT_HEAD_BILL_NO_EXIST_MSG));
        }
        //获取之前的金额数据
        BigDecimal preTotalPrice = getAccountHead(accountHead.getId()).getTotalPrice().abs();
        accountHeadMapper.updateByPrimaryKeySelective(accountHead);
        //根据单据编号查询单据id
        AccountHeadExample dhExample = new AccountHeadExample();
        dhExample.createCriteria().andBillNoEqualTo(accountHead.getBillNo()).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<AccountHead> list = accountHeadMapper.selectByExample(dhExample);
        if(list!=null) {
            Long headId = list.get(0).getId();
            String type = list.get(0).getType();
            /**处理单据子表信息*/
            accountItemService.saveDetials(rows, headId, type, request);
        }
        if("收预付款".equals(accountHead.getType())){
            supplierService.updateAdvanceIn(accountHead.getOrganId(), accountHead.getTotalPrice().subtract(preTotalPrice));
        }
        if (needRecalc(accountHead.getType())) {
            depotHeadService.recalcDepotHeadPayments(getImpactedBillIds(rows));
        }
        logService.insertLog("财务单据",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(accountHead.getBillNo()).toString(), request);
    }

    public List<AccountHeadVo4ListEx> getDetailByNumber(String billNo)throws Exception {
        List<AccountHeadVo4ListEx> resList = new ArrayList<AccountHeadVo4ListEx>();
        List<AccountHeadVo4ListEx> list = null;
        try{
            list = accountHeadMapperEx.getDetailByNumber(billNo);
        }catch(Exception e){
            JshException.readFail(logger, e);
        }
        if (null != list) {
            for (AccountHeadVo4ListEx ah : list) {
                if(ah.getChangeAmount() != null) {
                    if(BusinessConstants.TYPE_MONEY_IN.equals(ah.getType())) {
                        ah.setChangeAmount(ah.getChangeAmount());
                    } else if(BusinessConstants.TYPE_MONEY_OUT.equals(ah.getType())) {
                        ah.setChangeAmount(BigDecimal.ZERO.subtract(ah.getChangeAmount()));
                    } else {
                        ah.setChangeAmount(ah.getChangeAmount().abs());
                    }
                }
                if(ah.getTotalPrice() != null) {
                    if(BusinessConstants.TYPE_MONEY_IN.equals(ah.getType())) {
                        ah.setTotalPrice(ah.getTotalPrice());
                    } else if(BusinessConstants.TYPE_MONEY_OUT.equals(ah.getType())) {
                        ah.setTotalPrice(BigDecimal.ZERO.subtract(ah.getTotalPrice()));
                    } else {
                        ah.setTotalPrice(ah.getTotalPrice().abs());
                    }
                }
                if(ah.getBillTime() !=null) {
                    ah.setBillTimeStr(getCenternTime(ah.getBillTime()));
                }
                resList.add(ah);
            }
        }
        return resList;
    }

    public List<AccountItem> getFinancialBillNoByBillIdList(List<Long> idList) {
        return accountHeadMapperEx.getFinancialBillNoByBillIdList(idList);
    }

    public List<AccountHead> getFinancialBillNoByBillId(Long billId) {
        return accountHeadMapperEx.getFinancialBillNoByBillId(billId);
    }
}
