package com.jsh.erp.service.depotHead;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableList;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.DepotHeadMapper;
import com.jsh.erp.datasource.mappers.DepotHeadMapperEx;
import com.jsh.erp.datasource.mappers.DepotItemMapper;
import com.jsh.erp.datasource.mappers.DepotItemMapperEx;
import com.jsh.erp.datasource.vo.*;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.account.AccountService;
import com.jsh.erp.service.accountHead.AccountHeadService;
import com.jsh.erp.service.accountItem.AccountItemService;
import com.jsh.erp.service.depot.DepotService;
import com.jsh.erp.service.depotAllocation.DepotAllocationService;
import com.jsh.erp.service.depotItem.DepotItemService;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.service.material.MaterialService;
import com.jsh.erp.service.orgaUserRel.OrgaUserRelService;
import com.jsh.erp.service.person.PersonService;
import com.jsh.erp.service.productSupplier.ProductSupplierService;
import com.jsh.erp.service.role.RoleService;
import com.jsh.erp.service.sequence.SequenceService;
import com.jsh.erp.service.supplier.SupplierService;
import com.jsh.erp.service.systemConfig.SystemConfigService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.service.userBusiness.UserBusinessService;
import com.jsh.erp.utils.BaseResponseInfo;
import com.jsh.erp.utils.ExcelUtils;
import com.jsh.erp.utils.StringUtil;
import com.jsh.erp.utils.Tools;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jsh.erp.constants.BusinessConstants.*;
import static com.jsh.erp.utils.Tools.getCenternTime;
import static com.jsh.erp.utils.Tools.getNow3;

@Service
public class DepotHeadService {
    private Logger logger = LoggerFactory.getLogger(DepotHeadService.class);

    @Resource
    private DepotHeadMapper depotHeadMapper;
    @Resource
    private DepotItemMapper depotItemMapper;
    @Resource
    private DepotHeadMapperEx depotHeadMapperEx;
    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private DepotService depotService;
    @Resource
    private SequenceService sequenceService;
    @Resource
    private DepotAllocationService depotAllocationService;
    @Resource
    private MaterialService materialService;
    @Resource
    private DepotItemService depotItemService;
    @Resource
    private SupplierService supplierService;
    @Resource
    private UserBusinessService userBusinessService;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private OrgaUserRelService orgaUserRelService;
    @Resource
    private PersonService personService;
    @Resource
    private AccountService accountService;
    @Resource
    private AccountHeadService accountHeadService;
    @Resource
    private AccountItemService accountItemService;
    @Resource
    private DepotItemMapperEx depotItemMapperEx;
    @Resource
    private ProductSupplierService productSupplierService;
    @Resource
    private LogService logService;

    public DepotHead getDepotHead(long id) throws Exception {
        DepotHead result = null;
        try {
            result = depotHeadMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHead> getDepotHead() throws Exception {
        DepotHeadExample example = new DepotHeadExample();
        example.createCriteria().andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<DepotHead> list = null;
        try {
            list = depotHeadMapper.selectByExample(example);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<DepotHeadVo4List> select(String type, String subType, String roleType, String hasDebt, String status,
                                         String purchaseStatus, String number, String linkNumber, String beginTime,
                                         String endTime, String salesMan, String materialParam, Long organId,
                                         Long creator, Long depotId, Long accountId, String remark, int offset, int rows) throws Exception {
        List<DepotHeadVo4List> resList = new ArrayList<>();
        try {
            String[] depotArray = getDepotArray(subType);
            String[] creatorArray = getCreatorArray(roleType);
            String[] statusArray = StringUtil.isNotEmpty(status) ? status.split(",") : null;
            String[] purchaseStatusArray = StringUtil.isNotEmpty(purchaseStatus) ? purchaseStatus.split(",") : null;
            String[] organArray = getOrganArray(subType, purchaseStatus);
            Map<Long, String> personMap = personService.getPersonMap();
            Map<Long, String> accountMap = accountService.getAccountMap();
            beginTime = Tools.parseDayToTime(beginTime, BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime, BusinessConstants.DAY_LAST_TIME);
            List<DepotHeadVo4List> list = depotHeadMapperEx.selectByConditionDepotHead(type, subType, creatorArray,
                    hasDebt, statusArray, purchaseStatusArray, number, linkNumber, beginTime, endTime, salesMan,
                    materialParam, organId, organArray, creator, depotId, depotArray, accountId, remark, offset, rows);
            if (null != list) {
                List<Long> idList = new ArrayList<>();
                List<String> numberList = new ArrayList<>();
                for (DepotHeadVo4List dh : list) {
                    idList.add(dh.getId());
                    numberList.add(dh.getNumber());
                }
                Map<Long, String> materialsListMap = findMaterialsListMapByHeaderIdList(idList);
                Map<Long, BigDecimal> materialCountListMap = getMaterialCountListMapByHeaderIdList(idList);
                for (DepotHeadVo4List dh : list) {
                    if (accountMap != null && StringUtil.isNotEmpty(dh.getAccountIdList()) && StringUtil.isNotEmpty(dh.getAccountMoneyList())) {
                        String accountStr = accountService.getAccountStrByIdAndMoney(accountMap, dh.getAccountIdList(), dh.getAccountMoneyList());
                        dh.setAccountName(accountStr);
                    }
                    if (dh.getAccountIdList() != null) {
                        String accountidlistStr = dh.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", "");
                        dh.setAccountIdList(accountidlistStr);
                    }
                    if (dh.getAccountMoneyList() != null) {
                        String accountmoneylistStr = dh.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
                        dh.setAccountMoneyList(accountmoneylistStr);
                    }
                    if (dh.getChangeAmount() != null) {
                        dh.setChangeAmount(dh.getChangeAmount().abs());
                    } else {
                        dh.setChangeAmount(BigDecimal.ZERO);
                    }
                    if (dh.getTotalPrice() != null) {
                        dh.setTotalPrice(dh.getTotalPrice().abs());
                    }
                    dh.setHasBackFlag(false);
                    dh.setHasFinancialFlag(false);
                    List<DepotHead> allLinkedToThis = getBillListByLinkNumberList(ImmutableList.of(dh.getNumber()));
                    if (allLinkedToThis != null && allLinkedToThis.size() > 0) {
                        dh.setHasBackFlag(true);
                        List<String> allDhNumberLinkedToThis = allLinkedToThis.stream().map(e -> e.getNumber()).collect(Collectors.toList());
                        List<DepotHead> allLinkedToThisSecondLevel = getBillListByLinkNumberList(allDhNumberLinkedToThis);
                        if (allLinkedToThisSecondLevel.size() > 0) {
                            dh.setHasFinancialFlag(true);
                        }
                    }
                    if (dh.getDeposit() == null) {
                        dh.setDeposit(BigDecimal.ZERO);
                    }
                    if (StringUtil.isNotEmpty(dh.getSalesMan())) {
                        dh.setSalesManStr(personService.getPersonByMapAndIds(personMap, dh.getSalesMan()));
                    }
                    if (dh.getSubType().equals(SUB_TYPE_PURCHASE_ORDER) && dh.getLinkNumber() != null && !"".equals(dh.getLinkNumber())) {
                        DepotHead purchaseApplication = getDepotHead(dh.getLinkNumber());
                        if (purchaseApplication != null && purchaseApplication.getSalesMan() != null)
                        dh.setSalesManStr(personService.getPersonByMapAndIds(personMap, purchaseApplication.getSalesMan()));
                    }
                    if (dh.getOperTime() != null) {
                        dh.setOperTimeStr(getCenternTime(dh.getOperTime()));
                    }
                    if (dh.getPlanStartTime() != null) {
                        dh.setPlanStartTimeStr(new SimpleDateFormat("yyyy-MM-dd").format(dh.getPlanStartTime()));
                    }
                    if (dh.getPlanFinishTime() != null) {
                        dh.setPlanFinishTimeStr(new SimpleDateFormat("yyyy-MM-dd").format(dh.getPlanFinishTime()));
                    }
                    //产品信息简述
                    if (materialsListMap != null) {
                        dh.setMaterialsList(materialsListMap.get(dh.getId()));
                    }
                    //产品总数量
                    if (materialCountListMap != null) {
                        dh.setMaterialCount(materialCountListMap.get(dh.getId()));
                    }
                    //以销定购的情况（不能显示销售单据的金额和客户名称）
                    if (StringUtil.isNotEmpty(purchaseStatus)) {
                        dh.setOrganName("****");
                        dh.setTotalPrice(null);
                        dh.setDiscountLastMoney(null);
                    }
                    resList.add(dh);
                }
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return resList;
    }

    public Long countDepotHead(String type, String subType, String roleType, String hasDebt, String status, String purchaseStatus, String number, String linkNumber,
                               String beginTime, String endTime, String salesMan, String materialParam, Long organId, Long creator, Long depotId, Long accountId, String remark) throws Exception {
        Long result = null;
        try {
            String[] depotArray = getDepotArray(subType);
            String[] creatorArray = getCreatorArray(roleType);
            String[] statusArray = StringUtil.isNotEmpty(status) ? status.split(",") : null;
            String[] purchaseStatusArray = StringUtil.isNotEmpty(purchaseStatus) ? purchaseStatus.split(",") : null;
            String[] organArray = getOrganArray(subType, purchaseStatus);
            beginTime = Tools.parseDayToTime(beginTime, BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime, BusinessConstants.DAY_LAST_TIME);
            result = depotHeadMapperEx.countsByDepotHead(type, subType, creatorArray, hasDebt, statusArray, purchaseStatusArray,
                    number, linkNumber, beginTime, endTime, salesMan, materialParam, organId, organArray, creator, depotId, depotArray, accountId, remark);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 根据单据类型获取仓库数组：只有[采购订单、采购申请、销售订单、生产单]不涉及仓库
     *
     * @param subType
     * @return
     * @throws Exception
     */
    public String[] getDepotArray(String subType) throws Exception {
        String[] depotArray = null;
        if (!BusinessConstants.SUB_TYPE_PURCHASE_ORDER.equals(subType)
                && !SUB_TYPE_PURCHASE_APPLICATION.equals(subType)
                && !BusinessConstants.SUB_TYPE_SALES_ORDER.equals(subType)
                && !SUB_TYPE_PRODUCTION_ORDER.equals(subType)) {
            String depotIds = depotService.findDepotStrByCurrentUser();
            depotArray = StringUtil.isNotEmpty(depotIds) ? depotIds.split(",") : null;
        }
        return depotArray;
    }

    /**
     * 根据角色类型获取操作员数组
     *
     * @param roleType
     * @return
     * @throws Exception
     */
    public String[] getCreatorArray(String roleType) throws Exception {
        String creator = getCreatorByRoleType(roleType);
        String[] creatorArray = null;
        if (StringUtil.isNotEmpty(creator)) {
            creatorArray = creator.split(",");
        }
        return creatorArray;
    }

    /**
     * 获取机构数组
     * 系统里有个设置叫做客户权限 - 在某些情况下，销售人员只有特定的客户权限，在我们的erp中不适用
     *
     * @return
     */
    public String[] getOrganArray(String subType, String purchaseStatus) throws Exception {
        String[] organArray = null;
//        String type = "UserCustomer";
//        Long userId = userService.getCurrentUser().getId();
//        //获取权限信息
//        String ubValue = userBusinessService.getUBValueByTypeAndKeyId(type, userId.toString());
//        List<Supplier> supplierList = supplierService.findBySelectCus();
//        if (BusinessConstants.SUB_TYPE_SALES_ORDER.equals(subType)
//                || BusinessConstants.SUB_TYPE_SALES.equals(subType)
//                || BusinessConstants.SUB_TYPE_SALES_RETURN.equals(subType)) {
//            //采购订单里面选择销售订单的时候不要过滤
//            if (StringUtil.isEmpty(purchaseStatus)) {
//                if (null != supplierList && supplierList.size() > 0) {
//                    boolean customerFlag = systemConfigService.getCustomerFlag();
//                    List<String> organList = new ArrayList<>();
//                    for (Supplier supplier : supplierList) {
//                        boolean flag = ubValue.contains("[" + supplier.getId().toString() + "]");
//                        if (!customerFlag || flag) {
//                            organList.add(supplier.getId().toString());
//                        }
//                    }
//                    if (organList.size() > 0) {
//                        organArray = StringUtil.listToStringArray(organList);
//                    }
//                }
//            }
//        }
        return organArray;
    }

    /**
     * 根据角色类型获取操作员
     *
     * @param roleType
     * @return
     * @throws Exception
     */
    public String getCreatorByRoleType(String roleType) throws Exception {
        String creator = "";
        User user = userService.getCurrentUser();
        if (BusinessConstants.ROLE_TYPE_PRIVATE.equals(roleType)) {
            creator = user.getId().toString();
        } else if (BusinessConstants.ROLE_TYPE_THIS_ORG.equals(roleType)) {
            creator = orgaUserRelService.getUserIdListByUserId(user.getId());
        }
        return creator;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertDepotHead(JSONObject obj, HttpServletRequest request) throws Exception {
        DepotHead depotHead = JSONObject.parseObject(obj.toJSONString(), DepotHead.class);
        depotHead.setCreateTime(new Timestamp(System.currentTimeMillis()));
        depotHead.setStatus(BusinessConstants.BILLS_STATUS_UN_AUDIT);
        int result = 0;
        try {
            result = depotHeadMapper.insert(depotHead);
            logService.insertLog("单据", BusinessConstants.LOG_OPERATION_TYPE_ADD, request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateDepotHead(JSONObject obj, HttpServletRequest request) throws Exception {
        DepotHead depotHead = JSONObject.parseObject(obj.toJSONString(), DepotHead.class);
        DepotHead dh = null;
        try {
            dh = depotHeadMapper.selectByPrimaryKey(depotHead.getId());
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        depotHead.setStatus(dh.getStatus());
        depotHead.setCreateTime(dh.getCreateTime());
        int result = 0;
        try {
            result = depotHeadMapper.updateByPrimaryKey(depotHead);
            logService.insertLog("单据",
                    new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(depotHead.getId()).toString(), request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void recalcDepotHeadPayments(Set<Long> depotHeadIds) throws Exception {
        // 可以有多个accountItem对应同一个depotHead
        List<AccountItem> accountItems = new ArrayList<>();
        // 也可以有多个accountHead对应同一个depotHead
        List<AccountHead> accountHeads = new ArrayList<>();
        try {
            accountItems = accountItemService.selectByBillIds(new ArrayList<>(depotHeadIds));
            Set<Long> headIds = accountItems.stream().map(e -> e.getHeaderId()).collect(Collectors.toSet());
            accountHeads = accountHeadService.getAccountHeadByIds(new ArrayList<>(headIds));
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }

        Map<Long, List<AccountItem>> depotHeadIdToAccountItems = new HashMap<>();
        Map<Long, AccountHead> accountItemIdToAccountHeads = new HashMap<>();
        for (AccountItem accountItem : accountItems) {
            if (depotHeadIds.contains(accountItem.getBillId())) {
                depotHeadIdToAccountItems.computeIfAbsent(accountItem.getBillId(), k -> new ArrayList<>()).add(accountItem);
            }
            for (AccountHead accountHead : accountHeads) {
                if (accountHead.getId().longValue() == accountItem.getHeaderId().longValue()) {
                    accountItemIdToAccountHeads.put(accountItem.getId(), accountHead);
                    break;
                }
            }
        }
        for (Long id : depotHeadIds) {
            DepotHead dh = null;
            try {
                dh = depotHeadMapper.selectByPrimaryKey(id);
            } catch (Exception e) {
                JshException.readFail(logger, e);
            }
            BigDecimal changeAmount = BigDecimal.ZERO;
            BigDecimal backAmount = BigDecimal.ZERO;
            BigDecimal discount = BigDecimal.ZERO;
            BigDecimal discountMoney = BigDecimal.ZERO;
            BigDecimal otherMoney = BigDecimal.ZERO;
            BigDecimal deposit = BigDecimal.ZERO;
            for (AccountItem accountItem : depotHeadIdToAccountItems.getOrDefault(id, new ArrayList<>())) {
                BigDecimal need = accountItem.getNeedDebt() == null ? BigDecimal.ZERO : accountItem.getNeedDebt();
                BigDecimal finish = accountItem.getFinishDebt() == null ? BigDecimal.ZERO : accountItem.getFinishDebt();
                if (accountItemIdToAccountHeads.containsKey(accountItem.getId())) {
                    AccountHead accountHead = accountItemIdToAccountHeads.get(accountItem.getId());
                    switch (accountHead.getType()) {
                        case "采购定金":
                        case "销售定金":
                            changeAmount = changeAmount.add(need);
                            if (Integer.valueOf(accountHead.getStatus()) > 0) {
                                backAmount = backAmount.add(finish);
                            }
                            break;
                        case "采购付款":
                        case "销售收款":
                            discount = discount.add(need);
                            if (Integer.valueOf(accountHead.getStatus()) > 0) {
                                discountMoney = discountMoney.add(finish);
                            }
                            break;
                        case "采购退款":
                        case "销售退款":
                            otherMoney = otherMoney.add(need);
                            if (Integer.valueOf(accountHead.getStatus()) > 0) {
                                deposit = deposit.add(finish);
                            }
                            break;
                    }
                }
            }
            dh.setChangeAmount(changeAmount);           //已申请定金
            dh.setBackAmount(backAmount);             //已支付定金
            dh.setDiscount(discount);               //已申请付款
            dh.setDiscountMoney(discountMoney);          //已付款
            dh.setOtherMoney(otherMoney);             //已提交退款
            dh.setDeposit(deposit);                //已退款
            try {
                depotHeadMapper.updateByPrimaryKey(dh);
            } catch (Exception e) {
                JshException.writeFail(logger, e);
            }
        }
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteDepotHead(Long id, HttpServletRequest request) throws Exception {
        return batchDeleteBillByIds(id.toString());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteDepotHead(String ids, HttpServletRequest request) throws Exception {
        return batchDeleteBillByIds(ids);
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteBillByIds(String ids) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append(BusinessConstants.LOG_OPERATION_TYPE_DELETE);
        List<DepotHead> dhList = getDepotHeadListByIds(ids);
        for (DepotHead depotHead : dhList) {
            sb.append("[").append(depotHead.getNumber()).append("]");
            //只有未审核的单据才能被删除
            if ("0".equals(depotHead.getStatus())) {
                List<DepotItem> list = depotItemService.getListByHeaderId(depotHead.getId());
                //删除单据子表数据
                depotItemMapperEx.batchDeleteDepotItemByDepotHeadIds(new Long[]{depotHead.getId()});
                //删除单据主表信息
                batchDeleteDepotHeadByIds(depotHead.getId().toString());
                //更新关联单据的状态
                if (StringUtil.isNotEmpty(depotHead.getLinkNumber())) {
                    if ((BusinessConstants.DEPOTHEAD_TYPE_IN.equals(depotHead.getType()) &&
                            BusinessConstants.SUB_TYPE_PURCHASE.equals(depotHead.getSubType()))
                            || (BusinessConstants.DEPOTHEAD_TYPE_OUT.equals(depotHead.getType()) &&
                            BusinessConstants.SUB_TYPE_SALES.equals(depotHead.getSubType()))
                            || (BusinessConstants.DEPOTHEAD_TYPE_OTHER.equals(depotHead.getType()) &&
                            BusinessConstants.SUB_TYPE_REPLAY.equals(depotHead.getSubType()))
                            || (BusinessConstants.DEPOTHEAD_TYPE_IN.equals(depotHead.getType()) &&          // 生产入库（关联生产单）
                            BusinessConstants.SUB_TYPE_PRODUCTION.equals(depotHead.getSubType()))
                            || (BusinessConstants.DEPOTHEAD_TYPE_OTHER.equals(depotHead.getType()) &&       // 采购订单（关联采购申请）
                            SUB_TYPE_PURCHASE_ORDER.equals(depotHead.getSubType()))
                            || (BusinessConstants.DEPOTHEAD_TYPE_IN.equals(depotHead.getType()) &&       // 退料（关联领料）
                            SUB_TYPE_MATERIAL_RETURN.equals(depotHead.getSubType()))) {

                        String billStatus = depotItemService.getBillStatusByParam(depotHead);
                        depotItemService.changeBillStatus(depotHead, billStatus);
                    }
                }
                //更新当前库存
                for (DepotItem depotItem : list) {
                    depotItemService.updateCurrentStock(depotItem);
                }
            } else {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_DELETE_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_DELETE_FAILED_MSG));
            }
        }
        logService.insertLog("单据", sb.toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        return 1;
    }

    /**
     * 删除单据主表信息
     *
     * @param ids
     * @return
     * @throws Exception
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteDepotHeadByIds(String ids) throws Exception {
        User userInfo = userService.getCurrentUser();
        String[] idArray = ids.split(",");
        int result = 0;
        try {
            result = depotHeadMapperEx.batchDeleteDepotHeadByIds(new Date(), userInfo == null ? null : userInfo.getId(), idArray);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    public List<DepotHead> getDepotHeadListByIds(String ids) throws Exception {
        List<Long> idList = StringUtil.strToLongList(ids);
        List<DepotHead> list = new ArrayList<>();
        try {
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andIdIn(idList);
            list = depotHeadMapper.selectByExample(example);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    /**
     * 校验单据编号是否存在
     *
     * @param id
     * @param number
     * @return
     * @throws Exception
     */
    public int checkIsBillNumberExist(Long id, String number) throws Exception {
        DepotHeadExample example = new DepotHeadExample();
        example.createCriteria().andIdNotEqualTo(id).andNumberEqualTo(number).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<DepotHead> list = null;
        try {
            list = depotHeadMapper.selectByExample(example);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list == null ? 0 : list.size();
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchSetStatus(String status, String depotHeadIDs) throws Exception {
        int result = 0;
        List<Long> dhIds = new ArrayList<>();
        List<Long> ids = StringUtil.strToLongList(depotHeadIDs);
        for (Long id : ids) {
            DepotHead depotHead = getDepotHead(id);
            if ("0".equals(status)) {
                if ("11".equals(depotHead.getStatus()) || "12".equals(depotHead.getStatus()) || "1".equals(depotHead.getStatus())) {
                    dhIds.add(id);
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_AUDIT_TO_UN_AUDIT_FAILED_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_AUDIT_TO_UN_AUDIT_FAILED_MSG));
                }
            } else if ("1".equals(status)) {
                if (SUB_TYPE_PURCHASE_APPLICATION.equals(depotHead.getSubType())) {
                    if (!"1".equals(depotHead.getStatus())) {
                        dhIds.add(id);
                    } else {
                        throw new BusinessRunTimeException(ExceptionConstants.APPLICATION_THIRD_LEVEL_AUDIT_FAILED_CODE,
                                String.format(ExceptionConstants.APPLICATION_THIRD_LEVEL_AUDIT_FAILED_MSG));
                    }
                } else {
                    if ("0".equals(depotHead.getStatus())) {
                        dhIds.add(id);
                    } else {
                        throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_TO_AUDIT_FAILED_CODE,
                                String.format(ExceptionConstants.DEPOT_HEAD_UN_AUDIT_TO_AUDIT_FAILED_MSG));
                    }
                }
            } else if ("11".equals(status)) {
                if ("0".equals(depotHead.getStatus())) {
                    dhIds.add(id);
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.APPLICATION_FIRST_LEVEL_AUDIT_FAILED_CODE,
                            String.format(ExceptionConstants.APPLICATION_FIRST_LEVEL_AUDIT_FAILED_MSG));
                }
            } else if ("12".equals(status)) {
                if ("11".equals(depotHead.getStatus())) {
                    dhIds.add(id);
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.APPLICATION_SECOND_LEVEL_AUDIT_FAILED_CODE,
                            String.format(ExceptionConstants.APPLICATION_SECOND_LEVEL_AUDIT_FAILED_MSG));
                }
            }
        }
        if (dhIds.size() > 0) {
            DepotHead depotHead = new DepotHead();
            depotHead.setStatus(status);
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andIdIn(dhIds);
            result = depotHeadMapper.updateByExampleSelective(depotHead, example);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchSetCheckStatus(String status, String depotHeadIDs) throws Exception {
        int result = 0;
        List<Long> ids = StringUtil.strToLongList(depotHeadIDs);
        if (ids.size() > 0) {
            DepotHead depotHead = new DepotHead();
            depotHead.setBillType(status);
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andIdIn(ids);
            result = depotHeadMapper.updateByExampleSelective(depotHead, example);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchSetPurchaseStatus(String status, String depotHeadIDs) throws Exception {
        int result = 0;
        List<Long> dhIds = new ArrayList<>();
        List<Long> ids = StringUtil.strToLongList(depotHeadIDs);
        for (Long id : ids) {
            DepotHead depotHead = getDepotHead(id);
            if ("0".equals(status)) {
                if (PURCHASE_STATUS_RECEIPT.equals(depotHead.getPurchaseStatus())) {
                    dhIds.add(id);
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.PURCHASE_RECEIPT_CHECK_FAILED_CODE,
                            String.format(ExceptionConstants.PURCHASE_RECEIPT_CHECK_FAILED_MSG));
                }
            } else if ("1".equals(status)) {
                if (PURCHASE_STATUS_NO_RECEIPT.equals(depotHead.getPurchaseStatus())) {
                    dhIds.add(id);
                } else {
                    throw new BusinessRunTimeException(ExceptionConstants.PURCHASE_RECEIPT_UN_CHECK_FAILED_CODE,
                            String.format(ExceptionConstants.PURCHASE_RECEIPT_UN_CHECK_FAILED_MSG));
                }
            }
        }
        if (dhIds.size() > 0) {
            DepotHead depotHead = new DepotHead();
            depotHead.setPurchaseStatus(status);
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andIdIn(dhIds);
            result = depotHeadMapper.updateByExampleSelective(depotHead, example);
        }
        return result;
    }

    public Map<Long, String> findMaterialsListMapByHeaderIdList(List<Long> idList) throws Exception {
        List<MaterialsListVo> list = depotHeadMapperEx.findMaterialsListMapByHeaderIdList(idList);
        Map<Long, String> materialsListMap = new HashMap<>();
        for (MaterialsListVo materialsListVo : list) {
            materialsListMap.put(materialsListVo.getHeaderId(), materialsListVo.getMaterialsList());
        }
        return materialsListMap;
    }

    public Map<Long, BigDecimal> getMaterialCountListMapByHeaderIdList(List<Long> idList) throws Exception {
        List<MaterialCountVo> list = depotHeadMapperEx.getMaterialCountListByHeaderIdList(idList);
        Map<Long, BigDecimal> materialCountListMap = new HashMap<>();
        for (MaterialCountVo materialCountVo : list) {
            materialCountListMap.put(materialCountVo.getHeaderId(), materialCountVo.getMaterialCount());
        }
        return materialCountListMap;
    }

    public List<DepotHeadVo4InDetail> findInOutDetail(String beginTime, String endTime, String type, String[] creatorArray,
                                                      String[] organArray, String materialParam, List<Long> depotList, String categoryId, Integer oId, String number,
                                                      String remark, String batchNumber, Integer offset, Integer rows) throws Exception {
        List<DepotHeadVo4InDetail> list = null;
        List<DepotAllocation> allocationList = depotAllocationService.getDepotAllocation();
        Map<String, String> allocationIdToName = new HashMap<>();
        allocationList.stream().forEach(e -> allocationIdToName.put(e.getId().toString(), e.getType() + "-" + e.getAllocation()));
        try {
            list = depotHeadMapperEx.findInOutDetail(beginTime, endTime, type, creatorArray, organArray,
                    materialParam, depotList, categoryId, oId, number, remark, batchNumber, offset, rows);
            for (DepotHeadVo4InDetail detail : list) {
                if (detail.getSnList() != null && allocationIdToName.containsKey(detail.getSnList())) {
                    detail.setSnListStr(allocationIdToName.get(detail.getSnList()));
                }
                if (detail.getNewSnList() != null && allocationIdToName.containsKey(detail.getNewSnList())) {
                    detail.setNewSnListStr(allocationIdToName.get(detail.getNewSnList()));
                }
            }
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<DepotHeadVo4List> productionOrders(Long MId, String beginTime, String endTime, String[] creatorArray) throws Exception {
        List<DepotHeadVo4List> list = null;
        try {
            list = depotHeadMapperEx.getDepotHeadListWithProductionOrder(MId, beginTime, endTime, creatorArray);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int findInOutDetailCount(String beginTime, String endTime, String type, String[] creatorArray,
                                    String[] organArray, String materialParam, List<Long> depotList, String categoryId, Integer oId,
                                    String number, String remark, String batchNumber) throws Exception {
        int result = 0;
        try {
            result = depotHeadMapperEx.findInOutDetailCount(beginTime, endTime, type, creatorArray,
                    organArray, materialParam, depotList, categoryId, oId, number, remark, batchNumber);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHeadVo4InOutMCount> findInOutMaterialCount(String beginTime, String endTime, String type, String materialParam,
                                                                List<Long> depotList, String categoryId, Integer oId, String roleType, Integer offset, Integer rows) throws Exception {
        List<DepotHeadVo4InOutMCount> list = null;
        try {
            String[] creatorArray = getCreatorArray(roleType);
            // 查询进出库明细的时候，不需要指定客户/供应商，直接查询所有的出入库（包括生产入库、领料、退料、采购销售的退货等）
            String[] organArray = null;
            list = depotHeadMapperEx.findInOutMaterialCount(beginTime, endTime, type, materialParam, depotList, categoryId, oId,
                    creatorArray, organArray, offset, rows);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int findInOutMaterialCountTotal(String beginTime, String endTime, String type, String materialParam,
                                           List<Long> depotList, String categoryId, Integer oId, String roleType) throws Exception {
        int result = 0;
        try {
            String[] creatorArray = getCreatorArray(roleType);
            String subType = "出库".equals(type) ? "销售" : "";
            String[] organArray = getOrganArray(subType, "");
            result = depotHeadMapperEx.findInOutMaterialCountTotal(beginTime, endTime, type, materialParam, depotList, categoryId, oId,
                    creatorArray, organArray);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHeadVo4InDetail> findAllocationDetail(String beginTime, String endTime, String subType, String number,
                                                           String[] creatorArray, String materialParam, List<Long> depotList, List<Long> depotFList,
                                                           String remark, String batchNumber, Integer offset, Integer rows) throws Exception {
        List<DepotHeadVo4InDetail> list = null;
        List<DepotAllocation> allocationList = depotAllocationService.getDepotAllocation();
        Map<String, String> allocationIdToName = new HashMap<>();
        allocationList.stream().forEach(e -> allocationIdToName.put(e.getId().toString(), e.getType() + "-" + e.getAllocation()));
        try {
            list = depotHeadMapperEx.findAllocationDetail(beginTime, endTime, subType, number, creatorArray,
                    materialParam, depotList, depotFList, remark, batchNumber, offset, rows);
            for (DepotHeadVo4InDetail detail : list) {
                if (detail.getSnList() != null && allocationIdToName.containsKey(detail.getSnList())) {
                    detail.setSnListStr(allocationIdToName.get(detail.getSnList()));
                }
                if (detail.getNewSnList() != null && allocationIdToName.containsKey(detail.getNewSnList())) {
                    detail.setNewSnListStr(allocationIdToName.get(detail.getNewSnList()));
                }
            }
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int findAllocationDetailCount(String beginTime, String endTime, String subType, String number,
                                         String[] creatorArray, String materialParam, List<Long> depotList, List<Long> depotFList,
                                         String remark, String batchNumber) throws Exception {
        int result = 0;
        try {
            result = depotHeadMapperEx.findAllocationDetailCount(beginTime, endTime, subType, number, creatorArray,
                    materialParam, depotList, depotFList, remark, batchNumber);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHeadVo4StatementAccount> getStatementAccount(String beginTime, String endTime, Integer organId, String[] organArray,
                                                                  String supplierType, String type, String subType, String typeBack,
                                                                  String subTypeBack, String billType, Integer offset, Integer rows) {
        List<DepotHeadVo4StatementAccount> list = null;
        try {
            list = depotHeadMapperEx.getStatementAccount(beginTime, endTime, organId, organArray, supplierType, type, subType, typeBack, subTypeBack, billType, offset, rows);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public int getStatementAccountCount(String beginTime, String endTime, Integer organId,
                                        String[] organArray, String supplierType, String type,
                                        String subType, String typeBack, String subTypeBack, String billType) {
        int result = 0;
        try {
            result = depotHeadMapperEx.getStatementAccountCount(beginTime, endTime, organId, organArray, supplierType, type, subType, typeBack, subTypeBack, billType);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<DepotHeadVo4List> getDetailByNumber(String number) throws Exception {
        List<DepotHeadVo4List> resList = new ArrayList<DepotHeadVo4List>();
        try {
            Map<Long, String> personMap = personService.getPersonMap();
            Map<Long, String> accountMap = accountService.getAccountMap();
            List<DepotHeadVo4List> list = depotHeadMapperEx.getDetailByNumber(number);
            if (null != list) {
                List<Long> idList = new ArrayList<>();
                List<String> numberList = new ArrayList<>();
                for (DepotHeadVo4List dh : list) {
                    idList.add(dh.getId());
                    numberList.add(dh.getNumber());
                }
                Map<Long, String> materialsListMap = findMaterialsListMapByHeaderIdList(idList);
                for (DepotHeadVo4List dh : list) {
                    if (accountMap != null && StringUtil.isNotEmpty(dh.getAccountIdList()) && StringUtil.isNotEmpty(dh.getAccountMoneyList())) {
                        String accountStr = accountService.getAccountStrByIdAndMoney(accountMap, dh.getAccountIdList(), dh.getAccountMoneyList());
                        dh.setAccountName(accountStr);
                    }
                    if (dh.getAccountIdList() != null) {
                        String accountidlistStr = dh.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", "");
                        dh.setAccountIdList(accountidlistStr);
                    }
                    if (dh.getAccountMoneyList() != null) {
                        String accountmoneylistStr = dh.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
                        dh.setAccountMoneyList(accountmoneylistStr);
                    }
                    if (dh.getChangeAmount() != null) {
                        dh.setChangeAmount(dh.getChangeAmount().abs());
                    }
                    if (dh.getTotalPrice() != null) {
                        dh.setTotalPrice(dh.getTotalPrice().abs());
                    }
                    dh.setHasBackFlag(false);
                    dh.setHasFinancialFlag(false);
                    List<DepotHead> allLinkedToThis = getBillListByLinkNumberList(ImmutableList.of(dh.getNumber()));
                    if (allLinkedToThis != null && allLinkedToThis.size() > 0) {
                        dh.setHasBackFlag(true);
                        List<String> allDhNumberLinkedToThis = allLinkedToThis.stream().map(e -> e.getNumber()).collect(Collectors.toList());
                        List<DepotHead> allLinkedToThisSecondLevel = getBillListByLinkNumberList(allDhNumberLinkedToThis);
                        if (allLinkedToThisSecondLevel.size() > 0) {
                            dh.setHasFinancialFlag(true);
                        }
                    }
                    if (StringUtil.isNotEmpty(dh.getSalesMan())) {
                        dh.setSalesManStr(personService.getPersonByMapAndIds(personMap, dh.getSalesMan()));
                    }
                    if (dh.getOperTime() != null) {
                        dh.setOperTimeStr(getCenternTime(dh.getOperTime()));
                    }
                    if (dh.getPlanStartTime() != null) {
                        dh.setPlanStartTimeStr(new SimpleDateFormat("yyyy-MM-dd").format(dh.getPlanStartTime()));
                    }
                    if (dh.getPlanFinishTime() != null) {
                        dh.setPlanFinishTimeStr(new SimpleDateFormat("yyyy-MM-dd").format(dh.getPlanFinishTime()));
                    }
                    //产品信息简述
                    if (materialsListMap != null) {
                        dh.setMaterialsList(materialsListMap.get(dh.getId()));
                    }
                    dh.setCreatorName(userService.getUser(dh.getCreator()).getUsername());
                    resList.add(dh);
                }
            }
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return resList;
    }

    /**
     * 根据原单号查询关联的单据列表(批量)
     *
     * @param linkNumberList
     * @return
     * @throws Exception
     */
    public List<DepotHead> getBillListByLinkNumberList(List<String> linkNumberList) throws Exception {
        if (linkNumberList != null && linkNumberList.size() > 0) {
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andLinkNumberIn(linkNumberList).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
            return depotHeadMapper.selectByExample(example);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 根据原单号查询关联的单据列表
     *
     * @param linkNumber
     * @return
     * @throws Exception
     */
    public List<DepotHead> getBillListByLinkNumber(String linkNumber) throws Exception {
        DepotHeadExample example = new DepotHeadExample();
        example.createCriteria().andLinkNumberEqualTo(linkNumber).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        return depotHeadMapper.selectByExample(example);
    }

    /**
     * 根据原单号查询关联的单据列表(排除当前的单据编号)
     *
     * @param linkNumber
     * @return
     * @throws Exception
     */
    public List<DepotHead> getBillListByLinkNumberExceptNumber(String linkNumber, String number) throws Exception {
        DepotHeadExample example = new DepotHeadExample();
        example.createCriteria().andLinkNumberEqualTo(linkNumber).andNumberNotEqualTo(number).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        return depotHeadMapper.selectByExample(example);
    }

    /**
     * depotHeadController.addDepotHeadAndDetail -> depotHeadService.addDepotHeadAndDetail/updateDepotHeadAndDetail -> depotItemService.saveDetials
     * 新增单据主表及单据子表信息
     *
     * @param beanJson
     * @param rows     在这里已经有 preNumber 和 finishNumber 等数值了
     *                 每个modal里面
     *                 let url = this.readOnly ? this.url.detailList : this.url.detailList;
     *                 this.requestSubTableData(url, params, this.materialTable);
     *                 invoke了depotIteamController.getDetailList()，然后提交表单的时候，rows里都已经有具体信息了
     * @param request
     * @throws Exception
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void addDepotHeadAndDetail(String beanJson, String rows,
                                      HttpServletRequest request) throws Exception {
        /**处理单据主表数据*/
        DepotHead depotHead = JSONObject.parseObject(beanJson, DepotHead.class);
        //校验单号是否重复
        if (checkIsBillNumberExist(0L, depotHead.getNumber()) > 0) {
            throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_BILL_NUMBER_EXIST_CODE,
                    String.format(ExceptionConstants.DEPOT_HEAD_BILL_NUMBER_EXIST_MSG));
        }
        String subType = depotHead.getSubType();
        if (SUB_TYPE_PRODUCTION_ORDER.equals(subType)) {
            // 生产单开工时间要>=今天
            if (depotHead.getPlanStartTime().toInstant().plusSeconds(24 * 3600).truncatedTo(ChronoUnit.DAYS)
                    .compareTo(Instant.now().truncatedTo(ChronoUnit.DAYS)) < 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_PRODUCTION_START_TIME_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_PRODUCTION_START_TIME_FAILED_MSG));
            }
        }
        if (SUB_TYPE_PURCHASE_ORDER.equals(subType)) {
            JSONArray rowArr = JSONArray.parseArray(rows);
            List<String> ids = new ArrayList<>();
            if (null != rowArr && rowArr.size()>0) {
                for (int i = 0; i < rowArr.size(); i++) {
                    JSONObject rowObj = JSONObject.parseObject(rowArr.getString(i));
                    String productSupplierId = rowObj.getString("sku");
                    if (productSupplierId != null && !"".equals(productSupplierId)) {
                        ids.add(productSupplierId);
                    }
                }
            }
            List<ProductSupplierVo4Info> productSupplierList = productSupplierService.selectByIds(String.join(",", ids));
            Set<String> supplierSet = productSupplierList.stream().map(e -> e.getSupplierName()).collect(Collectors.toSet());
            List<String> supplierList = supplierSet.stream().collect(Collectors.toList());
            if (supplierSet.size() > 1) {
                throw new BusinessRunTimeException(ExceptionConstants.SUPPLIER_MORE_THAN_ONE_FAILED_CODE,
                        String.format(ExceptionConstants.SUPPLIER_MORE_THAN_ONE_FAILED_MSG, supplierList.get(0), supplierList.get(1)));
            }
            if (supplierSet.size() == 0) {
                throw new BusinessRunTimeException(ExceptionConstants.SUPPLIER_ZERO_FAILED_CODE,
                        ExceptionConstants.SUPPLIER_ZERO_FAILED_MSG);
            }
            depotHead.setOrganId(productSupplierList.get(0).getSupplierId());
        }
        //判断用户是否已经登录过，登录过不再处理
        User userInfo = userService.getCurrentUser();
        depotHead.setCreator(userInfo == null ? null : userInfo.getId());
        depotHead.setCreateTime(new Timestamp(System.currentTimeMillis()));
        if (StringUtil.isEmpty(depotHead.getStatus())) {
            depotHead.setStatus(BusinessConstants.BILLS_STATUS_UN_AUDIT);
        }
        depotHead.setPurchaseStatus(BusinessConstants.BILLS_STATUS_UN_AUDIT);
        if (StringUtil.isNotEmpty(depotHead.getAccountIdList())) {
            depotHead.setAccountIdList(depotHead.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", ""));
        }
        if (StringUtil.isNotEmpty(depotHead.getAccountMoneyList())) {
            //校验多账户的结算金额
            String accountMoneyList = depotHead.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
            BigDecimal sum = StringUtil.getArrSum(accountMoneyList.split(","));
            BigDecimal manyAccountSum = sum.abs();
            if (manyAccountSum.compareTo(depotHead.getTotalPrice().abs()) != 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_MSG));
            }
            depotHead.setAccountMoneyList(accountMoneyList);
        }
        try {
            depotHeadMapper.insertSelective(depotHead);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        //根据单据编号查询单据id
        DepotHeadExample dhExample = new DepotHeadExample();
        dhExample.createCriteria().andNumberEqualTo(depotHead.getNumber()).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        List<DepotHead> list = depotHeadMapper.selectByExample(dhExample);
        if (list != null) {
            Long headId = list.get(0).getId();
            /**入库和出库处理单据子表信息*/
            depotItemService.saveDetails(rows, headId, "add", request);
        }
        logService.insertLog("单据",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(depotHead.getNumber()).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        //MailUtil.sendMail("jilinhongze@hornze.com", "ERP测试", "测试");
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void updateReceipt(String beanJson, HttpServletRequest request) throws Exception {
        DepotHead depotHead = JSONObject.parseObject(beanJson, DepotHead.class);
        try {
            depotHeadMapper.updateByPrimaryKeySelective(depotHead);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        logService.insertLog("单据",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(depotHead.getNumber()).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public BaseResponseInfo importOtherInExcel(MultipartFile file, HttpServletRequest request) throws Exception {
        BaseResponseInfo info = new BaseResponseInfo();
        try {
            Long beginTime = System.currentTimeMillis();
            //文件扩展名只能为xls
            String fileName = file.getOriginalFilename();
            if (StringUtil.isNotEmpty(fileName)) {
                String fileExt = fileName.substring(fileName.indexOf(".") + 1);
                if (!"xls".equals(fileExt)) {
                    throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_EXTENSION_ERROR_CODE,
                            ExceptionConstants.MATERIAL_EXTENSION_ERROR_MSG);
                }
            }
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet src = workbook.getSheet(0);
            //获取真实的行数，剔除掉空白行
            int rightRows = ExcelUtils.getRightRows(src);
            //单次导入超出5000条
            if (rightRows > 5001) {
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_CODE,
                        String.format(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_MSG));
            }
            int counter = 0;
            List<DepotAllocation> allocationList = depotAllocationService.getDepotAllocation();
            Map<String, String> allocationNameToId = new HashMap<>();
            for (DepotAllocation depotAllocation : allocationList) {
                allocationNameToId.put(depotAllocation.getAllocation(), depotAllocation.getId().toString());
            }
            Map<String, Map<String, DepotItem>> orderNumberToDepotItems = new HashMap<>();
            Map<String, DepotHead> orderNumberToDepotHead = new HashMap<>();
            DepotHead depotHead = new DepotHead();

            String headNumber = "QTRK" + sequenceService.buildOnlyNumber();
            depotHead.setType("入库");
            depotHead.setSubType("其它");
            depotHead.setNumber(headNumber);
            depotHead.setDefaultNumber(headNumber);
            depotHead.setCreator(63L); // 63-管盛 189-半成品 188-成品
            depotHead.setStatus("0");
            depotHead.setPurchaseStatus("0");
            depotHead.setDeleteFlag("0");
            orderNumberToDepotHead.put(headNumber, depotHead);
            for (int i = 1; i < rightRows; i++) {
                Date dateValue = new Date();
                try {
                    //dateValue = new SimpleDateFormat("yyyy/M/d").parse(ExcelUtils.getContent(src, i, 2));
                    dateValue = new SimpleDateFormat("yyyy/M/d").parse("2024/10/1");
                } catch (Exception e) {}
                depotHead.setCreateTime(dateValue);
                depotHead.setOperTime(dateValue);
                depotHead.setRemark("导入");
                String barCode = ExcelUtils.getContent(src, i, 1); //物料编码
                List<MaterialVo4Unit> mList = materialService.getMaterialByBarCode(barCode);
                if (mList.isEmpty()) {
                    logger.info("XXXXX 未找到编码： " + barCode);
                    continue;
                }
                String stock = ExcelUtils.getContent(src, i, 2); //数量
                String allocation = ExcelUtils.getContent(src, i, 0); //货位
                String batchNumber = ExcelUtils.getContent(src, i, 3); //批次
                BigDecimal number = BigDecimal.valueOf(Double.parseDouble(stock));
                DepotItem depotItem = new DepotItem();
                depotItem.setSnList(allocationNameToId.getOrDefault(allocation, "564"));
                depotItem.setBatchNumber(batchNumber);
                depotItem.setDepotId(27L);  // 26-成品 27-半成品 28-原材料
                depotItem.setOperNumber(number);
                depotItem.setMaterialUnit(mList.get(0).getUnit());
                if (orderNumberToDepotItems.containsKey(headNumber)) {
                    orderNumberToDepotItems.get(headNumber).put(barCode, depotItem);
                } else {
                    orderNumberToDepotItems.put(headNumber, new HashMap<>());
                    orderNumberToDepotItems.get(headNumber).put(barCode, depotItem);
                }
                counter += 1;
                //if (counter % 1 == 0) {
                    importDepotHeadAndDetail(orderNumberToDepotItems, orderNumberToDepotHead);
                    orderNumberToDepotItems = new HashMap<>();
                    orderNumberToDepotHead = new HashMap<>();
                    headNumber = "QTRK" + sequenceService.buildOnlyNumber();
                    depotHead.setNumber(headNumber);
                    depotHead.setDefaultNumber(headNumber);
                    orderNumberToDepotHead.put(headNumber, depotHead);
                //}
            }
//            if (counter % 20 > 0) {
//                importDepotHeadAndDetail(orderNumberToDepotItems, orderNumberToDepotHead);
//            }
            Long endTime = System.currentTimeMillis();
            logger.info("导入耗时：{}", endTime - beginTime);
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

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public BaseResponseInfo importOtherOutExcel(MultipartFile file, HttpServletRequest request) throws Exception {
        BaseResponseInfo info = new BaseResponseInfo();
        try {
            Long beginTime = System.currentTimeMillis();
            //文件扩展名只能为xls
            String fileName = file.getOriginalFilename();
            if (StringUtil.isNotEmpty(fileName)) {
                String fileExt = fileName.substring(fileName.indexOf(".") + 1);
                if (!"xls".equals(fileExt)) {
                    throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_EXTENSION_ERROR_CODE,
                            ExceptionConstants.MATERIAL_EXTENSION_ERROR_MSG);
                }
            }
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet src = workbook.getSheet(0);
            //获取真实的行数，剔除掉空白行
            int rightRows = ExcelUtils.getRightRows(src);
            //单次导入超出5000条
            if (rightRows > 5001) {
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_CODE,
                        String.format(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_MSG));
            }
            List<DepotAllocation> allocationList = depotAllocationService.getDepotAllocation();
            Map<String, String> allocationNameToId = new HashMap<>();
            for (DepotAllocation depotAllocation : allocationList) {
                allocationNameToId.put(depotAllocation.getAllocation(), depotAllocation.getId().toString());
            }
            for (int i = 1; i < rightRows; i++) {
                Map<String, Map<String, DepotItem>> orderNumberToDepotItems = new HashMap<>();
                Map<String, DepotHead> orderNumberToDepotHead = new HashMap<>();
                Date dateValue = new Date();
                try {
                    dateValue = new SimpleDateFormat("yyyy/M/d").parse(ExcelUtils.getContent(src, i, 8));
                } catch (Exception e) {}
                DepotHead depotHead = new DepotHead();
                String headNumber = "QTCK" + sequenceService.buildOnlyNumber();
                depotHead.setType("出库");
                depotHead.setSubType("其它");
                depotHead.setNumber(headNumber);
                depotHead.setDefaultNumber(headNumber);
                depotHead.setCreateTime(dateValue);
                depotHead.setOperTime(dateValue);
                depotHead.setCreator(63L);
                depotHead.setStatus("0");
                depotHead.setPurchaseStatus("0");
                depotHead.setDeleteFlag("0");
                depotHead.setRemark(ExcelUtils.getContent(src, i, 6) + ExcelUtils.getContent(src, i, 7));
                orderNumberToDepotHead.put(headNumber, depotHead);
                String barCode = ExcelUtils.getContent(src, i, 5); //物料编码
                String batchNumber = ExcelUtils.getContent(src, i, 3); //批次
                String allocation = ExcelUtils.getContent(src, i, 4); //货位
                List<MaterialVo4Unit> mList = materialService.getMaterialByBarCode(barCode);
                if (mList.isEmpty()) {
                    continue;
                }
                String operNumber = ExcelUtils.getContent(src, i, 9); //数量
                BigDecimal operNumberValue;
                try {
                    operNumberValue = BigDecimal.valueOf(Double.parseDouble(operNumber));
                } catch (Exception e) {
                    continue;
                }
                if (operNumberValue.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                DepotItem depotItem = new DepotItem();
                if (allocationNameToId.containsKey(allocation)) {
                    depotItem.setSnList(allocationNameToId.get(allocation));
                }
                depotItem.setBatchNumber(batchNumber);
                depotItem.setDepotId(28L);
                depotItem.setOperNumber(operNumberValue);
                depotItem.setMaterialUnit(mList.get(0).getUnit());
                if (orderNumberToDepotItems.containsKey(headNumber)) {
                    orderNumberToDepotItems.get(headNumber).put(barCode, depotItem);
                } else {
                    orderNumberToDepotItems.put(headNumber, new HashMap<>());
                    orderNumberToDepotItems.get(headNumber).put(barCode, depotItem);
                }
                importDepotHeadAndDetail(orderNumberToDepotItems, orderNumberToDepotHead);
            }
            Long endTime = System.currentTimeMillis();
            logger.info("导入耗时：{}", endTime - beginTime);
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

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public BaseResponseInfo importPurchaseApplicationExcel(MultipartFile file, HttpServletRequest request) throws Exception {
        BaseResponseInfo info = new BaseResponseInfo();
        try {
            Long beginTime = System.currentTimeMillis();
            //文件扩展名只能为xls
            String fileName = file.getOriginalFilename();
            if (StringUtil.isNotEmpty(fileName)) {
                String fileExt = fileName.substring(fileName.indexOf(".") + 1);
                if (!"xls".equals(fileExt)) {
                    throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_EXTENSION_ERROR_CODE,
                            ExceptionConstants.MATERIAL_EXTENSION_ERROR_MSG);
                }
            }
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet src = workbook.getSheet(0);
            //获取真实的行数，剔除掉空白行
            int rightRows = ExcelUtils.getRightRows(src);
            //单次导入超出5000条
            if (rightRows > 5001) {
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_CODE,
                        String.format(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_MSG));
            }
            List<Person> personList = personService.getAllPerson();
            Map<String, Long> nameToId = new HashMap<>();
            personList.stream().forEach(e -> {
                if (!nameToId.containsKey(e.getName())) {
                    nameToId.put(e.getName(), e.getId());
                }
            });
            List<Depot> depotList = depotService.getAllList();
            Map<String, Long> depotToId = new HashMap<>();
            depotList.stream().forEach(e -> {
                if (!depotToId.containsKey(e.getName())) {
                    depotToId.put(e.getName(), e.getId());
                }
            });
            Map<String, Map<String, DepotItem>> orderNumberToDepotItems = new HashMap<>();
            Map<String, DepotHead> orderNumberToDepotHead = new HashMap<>();
            for (int i = 1; i < rightRows; i++) {
                String date = ExcelUtils.getContent(src, i, 2); //日期
                Date dateValue;
                try {
                    dateValue = new SimpleDateFormat("yyyy/M/d").parse(date);
                } catch (Exception e) {
                    continue;
                }
                String uniqueId = ExcelUtils.getContent(src, i, 0); //记录ID
                String applicationNumber = ExcelUtils.getContent(src, i, 1); //采购单号
                String barCode = ExcelUtils.getContent(src, i, 5); //物料编码
                String destination = ExcelUtils.getContent(src, i, 7); //收货地
                String applicant = ExcelUtils.getContent(src, i, 6); //申请人
                List<MaterialVo4Unit> mList = materialService.getMaterialByBarCode(barCode);
                if (mList.isEmpty()) {
                    continue;
                }
                String operNumber = ExcelUtils.getContent(src, i, 4); //数量
                BigDecimal operNumberValue;
                try {
                    operNumberValue = BigDecimal.valueOf(Double.parseDouble(operNumber));
                } catch (Exception e) {
                    continue;
                }
                if (operNumberValue.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                String headNumber = "CGSQ" + sequenceService.buildOnlyNumber();
                DepotHead depotHead = new DepotHead();
                depotHead.setType("其它");
                depotHead.setSubType("采购申请");
                depotHead.setNumber(headNumber);
                depotHead.setDefaultNumber(headNumber);
                depotHead.setDiscountLastMoney(BigDecimal.ONE);
                depotHead.setTotalPrice(BigDecimal.ZERO.subtract(BigDecimal.ONE));
                if (!orderNumberToDepotHead.containsKey(applicationNumber)) {
                    depotHead.setRemark(applicationNumber + ";到货时间:" + ExcelUtils.getContent(src, i, 3) + ";" + uniqueId);
                } else {
                    orderNumberToDepotHead.get(applicationNumber).setRemark(
                            orderNumberToDepotHead.get(applicationNumber).getRemark() + ";" + uniqueId);
                }

                depotHead.setSalesMan(String.valueOf(nameToId.getOrDefault(applicant, 295L)));
                depotHead.setCreateTime(dateValue);
                depotHead.setOperTime(dateValue);
                depotHead.setCreator(63L);
                depotHead.setStatus("1");
                depotHead.setPurchaseStatus("0");
                depotHead.setDeleteFlag("0");

                DepotItem depotItem = new DepotItem();
                depotItem.setOperNumber(operNumberValue);
                depotItem.setMaterialUnit(mList.get(0).getUnit());
                depotItem.setAnotherDepotId(depotToId.getOrDefault(destination,34L));

                if (!orderNumberToDepotItems.containsKey(applicationNumber)) {
                    orderNumberToDepotItems.put(applicationNumber, new HashMap<>());
                }
                orderNumberToDepotItems.get(applicationNumber).put(barCode, depotItem);

                if (!orderNumberToDepotHead.containsKey(applicationNumber)) {
                    orderNumberToDepotHead.put(applicationNumber, depotHead);
                }
            }
            importDepotHeadAndDetail(orderNumberToDepotItems, orderNumberToDepotHead);
            Long endTime = System.currentTimeMillis();
            logger.info("导入耗时：{}", endTime - beginTime);
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

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public BaseResponseInfo importSaleOrderExcel(MultipartFile file, HttpServletRequest request) throws Exception {
        BaseResponseInfo info = new BaseResponseInfo();
        List<String> errors = new ArrayList<>();
        try {
            Long beginTime = System.currentTimeMillis();
            //文件扩展名只能为xls
            String fileName = file.getOriginalFilename();
            if (StringUtil.isNotEmpty(fileName)) {
                String fileExt = fileName.substring(fileName.indexOf(".") + 1);
                if (!"xls".equals(fileExt)) {
                    throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_EXTENSION_ERROR_CODE,
                            ExceptionConstants.MATERIAL_EXTENSION_ERROR_MSG);
                }
            }
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet src = workbook.getSheet(0);
            //获取真实的行数，剔除掉空白行
            int rightRows = ExcelUtils.getRightRows(src);
            //单次导入超出5000条
            if (rightRows > 5001) {
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_CODE,
                        String.format(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_MSG));
            }
            Map<String, Map<String, DepotItem>> customerToDepotItems = new HashMap<>();
            Map<String, DepotHead> customerToDepotHead = new HashMap<>();
            int imported = 0, total = 0;
            for (int i = 1; i < rightRows; i++) {
                total += 1;
                String date = ExcelUtils.getContent(src, i, 0); //日期
                Date dateValue;
                try {
                    dateValue = new SimpleDateFormat("yyyy/MM").parse(date);
                } catch (Exception e) {
                    errors.add("第" + i + "行日期格式应为yyyy/MM");
                    continue;
                }
                String customer = ExcelUtils.getContent(src, i, 1).trim(); //客户
                String model = ExcelUtils.getContent(src, i, 2).trim(); //型号
                String operNumber = ExcelUtils.getContent(src, i, 3); //数量

                List<Supplier> organs = supplierService.selectByExact(customer, "客户", null, null, null, null);
                if (organs.size() != 1) {
                    errors.add("第" + i + "行名为【" + customer +  "】的客户未找到或者不止一个");
                    continue;
                }
                List<ProductSupplierVo4Info> psList = productSupplierService.select(organs.get(0).getId().toString(), model, null, null);
                if (psList.size() != 1) {
                    errors.add("第" + i + "行【" + customer +  "】客户的零件号【" + model + "】在客商档案里未找到或不止一个");
                    continue;
                }
                String barCode = psList.get(0).getBarCode();
                BigDecimal operNumberValue = BigDecimal.ZERO;
                try {
                    operNumberValue = BigDecimal.valueOf(Double.parseDouble(operNumber));
                } catch (Exception e) {
                    errors.add("第" + i + "行数量格式错误： " + operNumber);
                    continue;
                }
                List<MaterialVo4Unit> mList = materialService.getMaterialByBarCode(barCode);

                String headNumber = "XSDD" + sequenceService.buildOnlyNumber();
                DepotHead depotHead = new DepotHead();
                depotHead.setType("其它");
                depotHead.setSubType("销售订单");
                depotHead.setNumber(headNumber);
                depotHead.setDefaultNumber(headNumber);
                depotHead.setDiscountLastMoney(BigDecimal.ONE);
                depotHead.setTotalPrice(BigDecimal.ZERO.subtract(BigDecimal.ONE));
                depotHead.setOrganId(organs.get(0).getId());
                depotHead.setCreateTime(new Timestamp(System.currentTimeMillis()));
                depotHead.setOperTime(new Timestamp(System.currentTimeMillis()));
                depotHead.setPlanStartTime(dateValue);
                depotHead.setCreator(63L);
                depotHead.setPayType("现付");
                depotHead.setStatus("0");
                depotHead.setPurchaseStatus("0");
                depotHead.setDeleteFlag("0");

                DepotItem depotItem = new DepotItem();
                depotItem.setOperNumber(operNumberValue);
                depotItem.setMaterialUnit(mList.get(0).getUnit());

                if (!customerToDepotItems.containsKey(organs.get(0).getSupplier())) {
                    customerToDepotItems.put(organs.get(0).getSupplier(), new HashMap<>());
                }
                customerToDepotItems.get(organs.get(0).getSupplier()).put(barCode, depotItem);
                if (!customerToDepotHead.containsKey(organs.get(0).getSupplier())) {
                    customerToDepotHead.put(organs.get(0).getSupplier(), depotHead);
                }
                imported += 1;
            }
            for (String error : errors) {
                logger.info("XXXXX " + error);
            }
            for (DepotHead head : customerToDepotHead.values()) {
                if (errors.size() > 0) {
                    String path = "/opt/jshERP/upload" + File.separator + "statement" + File.separator;
                    String name = UUID.randomUUID() + ".xlsx";
                    generateImportResultStatement(errors, path, name);
                    head.setFileName("statement" + File.separator + name);
                }
            }
            importDepotHeadAndDetail(customerToDepotItems, customerToDepotHead);
            Long endTime = System.currentTimeMillis();
            logger.info("导入耗时：{}", endTime - beginTime);
            info.code = 200;
            info.data = "一共" + total + "条；成功" + imported + "条";
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

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public BaseResponseInfo importPurchaseOrderExcel(MultipartFile file, HttpServletRequest request) throws Exception {
        BaseResponseInfo info = new BaseResponseInfo();
        try {
            Long beginTime = System.currentTimeMillis();
            //文件扩展名只能为xls
            String fileName = file.getOriginalFilename();
            if (StringUtil.isNotEmpty(fileName)) {
                String fileExt = fileName.substring(fileName.indexOf(".") + 1);
                if (!"xls".equals(fileExt)) {
                    throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_EXTENSION_ERROR_CODE,
                            ExceptionConstants.MATERIAL_EXTENSION_ERROR_MSG);
                }
            }
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet src = workbook.getSheet(0);
            //获取真实的行数，剔除掉空白行
            int rightRows = ExcelUtils.getRightRows(src);
            //单次导入超出5000条
            if (rightRows > 5001) {
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_CODE,
                        String.format(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_MSG));
            }

            List<Person> personList = personService.getAllPerson();
            Map<String, Long> nameToId = new HashMap<>();
            personList.stream().forEach(e -> {
                if (!nameToId.containsKey(e.getName())) {
                    nameToId.put(e.getName(), e.getId());
                }
            });

            Map<String, Map<String, DepotItem>> orderNumberToDepotItems = new HashMap<>();
            Map<String, DepotHead> orderNumberToDepotHead = new HashMap<>();
            List<Supplier> supplierList = supplierService.getSupplier();
            for (int i = 1; i < rightRows; i++) {
                String date = ExcelUtils.getContent(src, i, 4); //日期
                Date dateValue;
                try {
                    dateValue = new SimpleDateFormat("yyyy/M/d").parse(date);
                } catch (Exception e) {
                    continue;
                }
                String identifier = ExcelUtils.getContent(src, i, 0); //采购记录id
                String orderNumber = ExcelUtils.getContent(src, i, 2); //采购单号
                String applicationNumber = ExcelUtils.getContent(src, i, 3); //申请单号
                String barCode = ExcelUtils.getContent(src, i, 8); //物料编码
                String applicant = "管盛"; //采购人
                List<MaterialVo4Unit> mList = materialService.getMaterialByBarCode(barCode);
                if (mList.isEmpty()) {
                    continue;
                }
                String supplierName = ExcelUtils.getContent(src, i, 6); //供应商
                String key = date + orderNumber + supplierName;
                String operNumber = ExcelUtils.getContent(src, i, 5); //数量
                BigDecimal operNumberValue;
                try {
                    operNumberValue = BigDecimal.valueOf(Double.parseDouble(operNumber));
                } catch (Exception e) {
                    continue;
                }
                if (operNumberValue.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                String headNumber = "CGDD" + sequenceService.buildOnlyNumber();
                DepotHead depotHead = new DepotHead();
                depotHead.setType("其它");
                depotHead.setSubType("采购订单");
                depotHead.setNumber(headNumber);
                depotHead.setDefaultNumber(headNumber);
                depotHead.setDiscountLastMoney(BigDecimal.ONE);
                depotHead.setTotalPrice(BigDecimal.ZERO.subtract(BigDecimal.ONE));
                if (!orderNumberToDepotHead.containsKey(key)) {
                    depotHead.setRemark(key + ";" + identifier);
                } else {
                    orderNumberToDepotHead.get(key).setRemark(
                            orderNumberToDepotHead.get(key).getRemark() + ";" + identifier);
                }

                for (Supplier supplier : supplierList) {
                    if ("供应商".equals(supplier.getType()) && supplierName.equals(supplier.getSupplier())) {
                        depotHead.setOrganId(supplier.getId());
                        break;
                    }
                }
                depotHead.setSalesMan(String.valueOf(nameToId.getOrDefault(applicant, 295L)));
                depotHead.setCreateTime(dateValue);
                depotHead.setOperTime(dateValue);
                depotHead.setCreator(63L);
                depotHead.setPayType("现付");
                depotHead.setStatus("1");
                depotHead.setPurchaseStatus("0");
                depotHead.setDeleteFlag("0");

                DepotItem depotItem = new DepotItem();
                depotItem.setOperNumber(operNumberValue);
                depotItem.setMaterialUnit(mList.get(0).getUnit());

                List<DepotHeadVo4List> list = depotHeadMapperEx.selectByConditionDepotHead("其它", "采购申请", null,
                        null, null, null, null, null, null, null,
                        null, null, null, null, null, null, null,
                        null, applicationNumber, null, null);
                if (list != null && !list.isEmpty()) {
                    depotHead.setLinkNumber(list.get(0).getNumber());
                }

                if (!orderNumberToDepotItems.containsKey(key)) {
                    orderNumberToDepotItems.put(key, new HashMap<>());
                }
                orderNumberToDepotItems.get(key).put(barCode, depotItem);

                if (!orderNumberToDepotHead.containsKey(key)) {
                    orderNumberToDepotHead.put(key, depotHead);
                }
            }
            importDepotHeadAndDetail(orderNumberToDepotItems, orderNumberToDepotHead);
            Long endTime = System.currentTimeMillis();
            logger.info("导入耗时：{}", endTime - beginTime);
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

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public BaseResponseInfo importPurchaseInExcel(MultipartFile file, HttpServletRequest request) throws Exception {
        BaseResponseInfo info = new BaseResponseInfo();
        try {
            Long beginTime = System.currentTimeMillis();
            //文件扩展名只能为xls
            String fileName = file.getOriginalFilename();
            if (StringUtil.isNotEmpty(fileName)) {
                String fileExt = fileName.substring(fileName.indexOf(".") + 1);
                if (!"xls".equals(fileExt)) {
                    throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_EXTENSION_ERROR_CODE,
                            ExceptionConstants.MATERIAL_EXTENSION_ERROR_MSG);
                }
            }
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet src = workbook.getSheet(0);
            //获取真实的行数，剔除掉空白行
            int rightRows = ExcelUtils.getRightRows(src);
            //单次导入超出5000条
            if (rightRows > 5001) {
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_CODE,
                        String.format(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_MSG));
            }
            List<DepotAllocation> allocationList = depotAllocationService.getDepotAllocation();
            Map<String, String> allocationNameToId = new HashMap<>();
            for (DepotAllocation depotAllocation : allocationList) {
                allocationNameToId.put(depotAllocation.getAllocation(), depotAllocation.getId().toString());
            }

            Map<String, Map<String, DepotItem>> orderNumberToDepotItems = new HashMap<>();
            Map<String, DepotHead> orderNumberToDepotHead = new HashMap<>();

            List<DepotHead> allDepotHeads = getDepotHead();
            for (int i = 1; i < rightRows; i++) {
                String purchaseOrderId = ExcelUtils.getContent(src, i, 1); //采购订单
                String linkNumber = "";
                Long organId = 0L;
                List<DepotItem> poItems = new ArrayList<>();
                for (DepotHead head : allDepotHeads) {
                    if (head.getRemark() != null && head.getRemark().contains(purchaseOrderId)) {
                        linkNumber = head.getNumber();
                        organId = head.getOrganId();
                        poItems = depotItemService.getListByHeaderId(head.getId());
                    }
                }
                if ("".equals(linkNumber)) {
                    continue;
                }

                String date = ExcelUtils.getContent(src, i, 2); //日期
                Date dateValue;
                try {
                    dateValue = new SimpleDateFormat("yyyy/M/d").parse(date);
                } catch (Exception e) {
                    continue;
                }

                String barCode = ExcelUtils.getContent(src, i, 3); //物料编码
                List<MaterialVo4Unit> mList = materialService.getMaterialByBarCode(barCode);
                if (mList.isEmpty()) {
                    continue;
                }

                String batchNumber = ExcelUtils.getContent(src, i, 6); //批号
                String allocation = ExcelUtils.getContent(src, i, 9); //货位
                String operNumber = ExcelUtils.getContent(src, i, 5); //数量
                BigDecimal operNumberValue;
                try {
                    operNumberValue = BigDecimal.valueOf(Double.parseDouble(operNumber));
                } catch (Exception e) {
                    continue;
                }
                if (operNumberValue.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                String headNumber = "CGRK" + sequenceService.buildOnlyNumber();
                DepotHead depotHead = new DepotHead();
                depotHead.setType("入库");
                depotHead.setSubType("采购");
                depotHead.setNumber(headNumber);
                depotHead.setOrganId(organId);
                depotHead.setDefaultNumber(headNumber);
                depotHead.setCreateTime(dateValue);
                depotHead.setOperTime(dateValue);
                depotHead.setCreator(63L);
                depotHead.setPayType("现付");
                depotHead.setStatus("1");
                depotHead.setPurchaseStatus("0");
                depotHead.setDeleteFlag("0");
                depotHead.setLinkNumber(linkNumber);

                DepotItem depotItem = new DepotItem();
                depotItem.setSnList(allocationNameToId.getOrDefault(allocation, "564"));
                depotItem.setDepotId(28L);
                depotItem.setBatchNumber(batchNumber);
                depotItem.setOperNumber(operNumberValue);
                depotItem.setMaterialUnit(mList.get(0).getUnit());
                for (DepotItem poItem : poItems) {
                    if (mList.get(0).getId().longValue() == poItem.getMaterialId().longValue()) {
                        depotItem.setLinkId(poItem.getId());
                        break;
                    }
                }

                if (!orderNumberToDepotItems.containsKey(headNumber)) {
                    orderNumberToDepotItems.put(headNumber, new HashMap<>());
                }
                orderNumberToDepotItems.get(headNumber).put(barCode, depotItem);

                if (!orderNumberToDepotHead.containsKey(headNumber)) {
                    orderNumberToDepotHead.put(headNumber, depotHead);
                }
            }
            importDepotHeadAndDetail(orderNumberToDepotItems, orderNumberToDepotHead);
            Long endTime = System.currentTimeMillis();
            logger.info("导入耗时：{}", endTime - beginTime);
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

    private void generateImportResultStatement(List<String> errors, String path, String fileName) throws Exception {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet sheet = xssfWorkbook.createSheet("导入结果");
        XSSFRow titleRow, row; // 行
        titleRow = sheet.getRow(0);
        if (titleRow == null) {
            titleRow = sheet.createRow(0);
        }
        titleRow.createCell(0).setCellValue("导入结果");

        for (int i = 0; i < errors.size(); i++) {
            row = sheet.getRow(i+1);
            if (row == null) {
                row = sheet.createRow(i+1);
            }
            row.createCell(0).setCellValue(errors.get(i));
        }
        File filePath = new File(path);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        File statementFile = new File(filePath,   fileName);
        FileOutputStream outputStream = new FileOutputStream(statementFile);
        xssfWorkbook.write(outputStream);
        outputStream.close();
        xssfWorkbook.close();
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public BaseResponseInfo importRepairInExcel(MultipartFile file, HttpServletRequest request) throws Exception {
        BaseResponseInfo info = new BaseResponseInfo();
        try {
            Long beginTime = System.currentTimeMillis();
            //文件扩展名只能为xls
            String fileName = file.getOriginalFilename();
            if (StringUtil.isNotEmpty(fileName)) {
                String fileExt = fileName.substring(fileName.indexOf(".") + 1);
                if (!"xls".equals(fileExt)) {
                    throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_EXTENSION_ERROR_CODE,
                            ExceptionConstants.MATERIAL_EXTENSION_ERROR_MSG);
                }
            }
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet src = workbook.getSheet(0);
            //获取真实的行数，剔除掉空白行
            int rightRows = ExcelUtils.getRightRows(src);
            //单次导入超出5000条
            if (rightRows > 5001) {
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_CODE,
                        String.format(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_MSG));
            }
            List<DepotAllocation> allocationList = depotAllocationService.getDepotAllocation();
            Map<String, String> allocationNameToId = new HashMap<>();
            for (DepotAllocation depotAllocation : allocationList) {
                allocationNameToId.put(depotAllocation.getType() + depotAllocation.getAllocation(), depotAllocation.getId().toString());
            }

            Map<String, Map<String, DepotItem>> orderNumberToDepotItems = new HashMap<>();
            Map<String, DepotHead> orderNumberToDepotHead = new HashMap<>();
            for (int i = 1; i < rightRows; i++) {
                String date = ExcelUtils.getContent(src, i, 1); //日期
                Date dateValue;
                try {
                    dateValue = new SimpleDateFormat("M/d/yy").parse(date);
                } catch (Exception e) {
                    continue;
                }

                String barCode = ExcelUtils.getContent(src, i, 5); //物料编码
                List<MaterialVo4Unit> mList = materialService.getMaterialByBarCode(barCode);
                if (mList.isEmpty()) {
                    continue;
                }
                String batchNumber = ExcelUtils.getContent(src, i, 0); //批号
                String operNumber = ExcelUtils.getContent(src, i, 15); //数量
                BigDecimal operNumberValue;
                try {
                    operNumberValue = BigDecimal.valueOf(Double.parseDouble(operNumber));
                } catch (Exception e) {
                    continue;
                }
                if (operNumberValue.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                String headNumber = "FXRK" + sequenceService.buildOnlyNumber();
                DepotHead depotHead = new DepotHead();
                depotHead.setType("入库");
                depotHead.setSubType("返修");
                depotHead.setNumber(headNumber);
                depotHead.setDefaultNumber(headNumber);
                depotHead.setCreateTime(dateValue);
                depotHead.setOperTime(dateValue);

                depotHead.setCreator(63L);
                depotHead.setPayType("现付");
                depotHead.setStatus("1");
                depotHead.setPurchaseStatus("0");
                depotHead.setDeleteFlag("0");

                DepotItem depotItem = new DepotItem();
                depotItem.setSnList("564");
                depotItem.setBatchNumber(batchNumber);
                depotItem.setOperNumber(operNumberValue);
                depotItem.setMaterialUnit(mList.get(0).getUnit());
                depotItem.setDepotId(null);
                depotItem.setAnotherDepotId(null);

                if (orderNumberToDepotItems.containsKey(headNumber)) {
                    orderNumberToDepotItems.get(headNumber).put(barCode, depotItem);
                } else {
                    orderNumberToDepotItems.put(headNumber, new HashMap<>());
                    orderNumberToDepotItems.get(headNumber).put(barCode, depotItem);
                }
                orderNumberToDepotHead.put(headNumber, depotHead);
            }
            importDepotHeadAndDetail(orderNumberToDepotItems, orderNumberToDepotHead);
            Long endTime = System.currentTimeMillis();
            logger.info("导入耗时：{}", endTime - beginTime);
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

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public BaseResponseInfo importRepairOutExcel(MultipartFile file, HttpServletRequest request) throws Exception {
        BaseResponseInfo info = new BaseResponseInfo();
        try {
            Long beginTime = System.currentTimeMillis();
            //文件扩展名只能为xls
            String fileName = file.getOriginalFilename();
            if (StringUtil.isNotEmpty(fileName)) {
                String fileExt = fileName.substring(fileName.indexOf(".") + 1);
                if (!"xls".equals(fileExt)) {
                    throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_EXTENSION_ERROR_CODE,
                            ExceptionConstants.MATERIAL_EXTENSION_ERROR_MSG);
                }
            }
            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
            Sheet src = workbook.getSheet(0);
            //获取真实的行数，剔除掉空白行
            int rightRows = ExcelUtils.getRightRows(src);
            //单次导入超出5000条
            if (rightRows > 5001) {
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_CODE,
                        String.format(ExceptionConstants.MATERIAL_IMPORT_OVER_LIMIT_MSG));
            }
            List<DepotAllocation> allocationList = depotAllocationService.getDepotAllocation();
            Map<String, String> allocationNameToId = new HashMap<>();
            for (DepotAllocation depotAllocation : allocationList) {
                allocationNameToId.put(depotAllocation.getType() + depotAllocation.getAllocation(), depotAllocation.getId().toString());
            }

            Map<String, Map<String, DepotItem>> orderNumberToDepotItems = new HashMap<>();
            Map<String, DepotHead> orderNumberToDepotHead = new HashMap<>();
            for (int i = 1; i < rightRows; i++) {
                String date = ExcelUtils.getContent(src, i, 1); //日期
                Date dateValue;
                try {
                    dateValue = new SimpleDateFormat("M/d/yy").parse(date);
                } catch (Exception e) {
                    continue;
                }

                String barCode = ExcelUtils.getContent(src, i, 5); //物料编码
                List<MaterialVo4Unit> mList = materialService.getMaterialByBarCode(barCode);
                if (mList.isEmpty()) {
                    continue;
                }

                String batchNumber = ExcelUtils.getContent(src, i, 0); //批号
                String operNumber = ExcelUtils.getContent(src, i, 15); //数量
                BigDecimal operNumberValue;
                try {
                    operNumberValue = BigDecimal.valueOf(Double.parseDouble(operNumber));
                } catch (Exception e) {
                    continue;
                }
                if (operNumberValue.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                String headNumber = "FXCK" + sequenceService.buildOnlyNumber();
                DepotHead depotHead = new DepotHead();
                depotHead.setType("出库");
                depotHead.setSubType("返修出库");
                depotHead.setNumber(headNumber);
                depotHead.setDefaultNumber(headNumber);
                depotHead.setCreateTime(dateValue);
                depotHead.setOperTime(dateValue);
                depotHead.setCreator(63L);
                depotHead.setPayType("现付");
                depotHead.setStatus("1");
                depotHead.setPurchaseStatus("0");
                depotHead.setDeleteFlag("0");

                DepotItem depotItem = new DepotItem();
                depotItem.setSnList("565");
                depotItem.setBatchNumber(batchNumber);
                depotItem.setOperNumber(operNumberValue);
                depotItem.setMaterialUnit(mList.get(0).getUnit());
                depotItem.setDepotId(null);
                depotItem.setAnotherDepotId(null);

                if (orderNumberToDepotItems.containsKey(headNumber)) {
                    orderNumberToDepotItems.get(headNumber).put(barCode, depotItem);
                } else {
                    orderNumberToDepotItems.put(headNumber, new HashMap<>());
                    orderNumberToDepotItems.get(headNumber).put(barCode, depotItem);
                }
                orderNumberToDepotHead.put(headNumber, depotHead);
            }
            importDepotHeadAndDetail(orderNumberToDepotItems, orderNumberToDepotHead);
            Long endTime = System.currentTimeMillis();
            logger.info("导入耗时：{}", endTime - beginTime);
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

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void importDepotHeadAndDetail(Map<String, Map<String, DepotItem>> orderNumberToDepotItems,
                                         Map<String, DepotHead> orderNumberToDepotHead) throws Exception {
        for (String orderNumber : orderNumberToDepotHead.keySet()) {
            if (!orderNumberToDepotItems.containsKey(orderNumber)) {
                continue;
            }
            DepotHead depotHead = orderNumberToDepotHead.get(orderNumber);
            Map<String, DepotItem> barCodeToDepotItem = orderNumberToDepotItems.get(orderNumber);

            //校验单号是否重复
            if (checkIsBillNumberExist(0L, depotHead.getNumber()) > 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_BILL_NUMBER_EXIST_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_BILL_NUMBER_EXIST_MSG));
            }
            //判断用户是否已经登录过，登录过不再处理
            User userInfo = userService.getCurrentUser();
            depotHead.setCreator(userInfo == null ? null : userInfo.getId());
            depotHead.setCreateTime(new Timestamp(System.currentTimeMillis()));
            if (StringUtil.isEmpty(depotHead.getStatus())) {
                depotHead.setStatus(BusinessConstants.BILLS_STATUS_UN_AUDIT);
            }
            depotHead.setPurchaseStatus(BusinessConstants.BILLS_STATUS_UN_AUDIT);
            depotHead.setPayType(depotHead.getPayType() == null ? "现付" : depotHead.getPayType());
            if (StringUtil.isNotEmpty(depotHead.getAccountIdList())) {
                depotHead.setAccountIdList(depotHead.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", ""));
            }
            if (StringUtil.isNotEmpty(depotHead.getAccountMoneyList())) {
                //校验多账户的结算金额
                String accountMoneyList = depotHead.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
                BigDecimal sum = StringUtil.getArrSum(accountMoneyList.split(","));
                BigDecimal manyAccountSum = sum.abs();
                if (manyAccountSum.compareTo(depotHead.getTotalPrice().abs()) != 0) {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_CODE,
                            String.format(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_MSG));
                }
                depotHead.setAccountMoneyList(accountMoneyList);
            }
            try {
                depotHeadMapper.insertSelective(depotHead);
            } catch (Exception e) {
                JshException.writeFail(logger, e);
            }
            //根据单据编号查询单据id
            DepotHeadExample dhExample = new DepotHeadExample();
            dhExample.createCriteria().andNumberEqualTo(depotHead.getNumber()).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
            List<DepotHead> list = depotHeadMapper.selectByExample(dhExample);
            if (list != null) {
                Long headId = list.get(0).getId();
                /**入库和出库处理单据子表信息*/
                String rows = "[";
                for (String barCode : barCodeToDepotItem.keySet()) {
                    if (!rows.equals("[")) {
                        rows = rows + ",";
                    }
                    DepotItem depotItem = barCodeToDepotItem.get(barCode);
                    rows = rows + "{";
                    if (depotItem.getMaterialUnit() != null && !"".equals(depotItem.getMaterialUnit())) {
                        rows = rows + "\"unit\":\"" + depotItem.getMaterialUnit() + "\"";
                    } else {
                        rows = rows + "\"unit\":\"" + "件" + "\"";
                    }
                    if (depotItem.getBatchNumber() != null && !"".equals(depotItem.getBatchNumber())) {
                        rows = rows + ",\"batchNumber\":\"" + depotItem.getBatchNumber() + "\"";
                    }
                    if (depotItem.getSnList() != null && !"".equals(depotItem.getSnList())) {
                        rows = rows + ",\"snList\":\"" + depotItem.getSnList() + "\"";
                    }
                    rows = rows + ",\"operNumber\":\"" + depotItem.getOperNumber() + "\"";
                    if (depotItem.getDepotId() != null) {
                        rows = rows + ",\"depotId\":\"" + depotItem.getDepotId() + "\"";
                    }
                    if (depotItem.getAnotherDepotId() != null) {
                        rows = rows + ",\"anotherDepotId\":\"" + depotItem.getAnotherDepotId() + "\"";
                    }
                    if (depotItem.getLinkId() != null) {
                        rows = rows + ",\"linkId\":\"" + depotItem.getLinkId() + "\"";
                    }
                    rows = rows + ",\"barCode\":\"" + barCode + "\"}";
                }
                rows = rows + "]";
                depotItemService.saveDetails(rows, headId, "add", null);
            }
        }
    }

    /**
     * depotHeadController.addDepotHeadAndDetail -> depotHeadService.addDepotHeadAndDetail/updateDepotHeadAndDetail -> depotItemService.saveDetials
     * 更新单据主表及单据子表信息
     *
     * @param beanJson
     * @param rows     在这里已经有 preNumber 和 finishNumber 等数值了
     *                 每个modal里面
     *                 let url = this.readOnly ? this.url.detailList : this.url.detailList;
     *                 this.requestSubTableData(url, params, this.materialTable);
     *                 invoke了depotIteamController.getDetailList()，然后提交表单的时候，rows里都已经有具体信息了
     * @param request
     * @throws Exception
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void updateDepotHeadAndDetail(String beanJson, String rows, HttpServletRequest request) throws Exception {
        /**更新单据主表信息*/
        DepotHead depotHead = JSONObject.parseObject(beanJson, DepotHead.class);
        //校验单号是否重复
        if (checkIsBillNumberExist(depotHead.getId(), depotHead.getNumber()) > 0) {
            throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_BILL_NUMBER_EXIST_CODE,
                    String.format(ExceptionConstants.DEPOT_HEAD_BILL_NUMBER_EXIST_MSG));
        }
        String subType = depotHead.getSubType();
        if ("生产单".equals(subType)) {
            // 生产单开工时间要>=今天
            if (depotHead.getPlanStartTime().toInstant().plusSeconds(24 * 3600).truncatedTo(ChronoUnit.DAYS)
                    .compareTo(Instant.now().truncatedTo(ChronoUnit.DAYS)) < 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_PRODUCTION_START_TIME_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_PRODUCTION_START_TIME_FAILED_MSG));
            }
        }
        if (StringUtil.isNotEmpty(depotHead.getAccountIdList())) {
            depotHead.setAccountIdList(depotHead.getAccountIdList().replace("[", "").replace("]", "").replaceAll("\"", ""));
        }
        if (StringUtil.isNotEmpty(depotHead.getAccountMoneyList())) {
            //校验多账户的结算金额
            String accountMoneyList = depotHead.getAccountMoneyList().replace("[", "").replace("]", "").replaceAll("\"", "");
            BigDecimal sum = StringUtil.getArrSum(accountMoneyList.split(","));
            BigDecimal manyAccountSum = sum.abs();
            if (manyAccountSum.compareTo(depotHead.getChangeAmount().abs()) != 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_CODE,
                        String.format(ExceptionConstants.DEPOT_HEAD_MANY_ACCOUNT_FAILED_MSG));
            }
            depotHead.setAccountMoneyList(accountMoneyList);
        }
        try {
            depotHeadMapper.updateByPrimaryKeySelective(depotHead);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        /**入库和出库处理单据子表信息*/
        depotItemService.saveDetails(rows, depotHead.getId(), "update", request);
        if ("采购申请".equals(subType) || "采购订单".equals(subType)) {
            String billStatus = depotItemService.getBillStatusByParamSelf(depotHead);
            depotItemService.changeBillStatusSelf(depotHead, billStatus);
        }
        logService.insertLog("单据",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(depotHead.getNumber()).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    /**
     * 退货单对应的原单实际欠款（这里面要除去收付款的金额）
     *
     * @param linkNumber 原单单号
     * @param number     当前单号
     * @return
     */
    public BigDecimal getOriginalRealDebt(String linkNumber, String number) throws Exception {
        DepotHead depotHead = getDepotHead(linkNumber);
        BigDecimal discountLastMoney = depotHead.getDiscountLastMoney() != null ? depotHead.getDiscountLastMoney() : BigDecimal.ZERO;
        BigDecimal otherMoney = depotHead.getOtherMoney() != null ? depotHead.getOtherMoney() : BigDecimal.ZERO;
        BigDecimal deposit = depotHead.getDeposit() != null ? depotHead.getDeposit() : BigDecimal.ZERO;
        BigDecimal changeAmount = depotHead.getChangeAmount() != null ? depotHead.getChangeAmount().abs() : BigDecimal.ZERO;
        //原单欠款
        BigDecimal debt = discountLastMoney.add(otherMoney).subtract((deposit.add(changeAmount)));
        //完成欠款
        BigDecimal finishDebt = accountItemService.getEachAmountByBillId(depotHead.getId());
        finishDebt = finishDebt != null ? finishDebt : BigDecimal.ZERO;
        //原单对应的退货单欠款(总数)
        List<DepotHead> billList = getBillListByLinkNumberExceptNumber(linkNumber, number);
        BigDecimal allBillDebt = BigDecimal.ZERO;
        for (DepotHead dh : billList) {
            BigDecimal billDiscountLastMoney = dh.getDiscountLastMoney() != null ? dh.getDiscountLastMoney() : BigDecimal.ZERO;
            BigDecimal billOtherMoney = dh.getOtherMoney() != null ? dh.getOtherMoney() : BigDecimal.ZERO;
            BigDecimal billDeposit = dh.getDeposit() != null ? dh.getDeposit() : BigDecimal.ZERO;
            BigDecimal billChangeAmount = dh.getChangeAmount() != null ? dh.getChangeAmount().abs() : BigDecimal.ZERO;
            BigDecimal billDebt = billDiscountLastMoney.add(billOtherMoney).subtract((billDeposit.add(billChangeAmount)));
            allBillDebt = allBillDebt.add(billDebt);
        }
        //原单实际欠款
        return debt.subtract(finishDebt).subtract(allBillDebt);
    }

    public Map<String, Object> getBuyAndSaleStatistics(String today, String monthFirstDay, String yesterdayBegin, String yesterdayEnd,
                                                       String yearBegin, String yearEnd, String roleType, HttpServletRequest request) throws Exception {
        String[] creatorArray = getCreatorArray(roleType);
        Map<String, Object> map = new HashMap<>();
        //今日
        BigDecimal todayBuy = getBuyAndSaleBasicStatistics("入库", "采购",
                1, today, getNow3(), creatorArray); //今日采购入库
        BigDecimal todayBuyBack = getBuyAndSaleBasicStatistics("出库", "采购退货",
                1, today, getNow3(), creatorArray); //今日采购退货
        BigDecimal todaySale = getBuyAndSaleBasicStatistics("出库", "销售",
                1, today, getNow3(), creatorArray); //今日销售出库
        BigDecimal todaySaleBack = getBuyAndSaleBasicStatistics("入库", "销售退货",
                1, today, getNow3(), creatorArray); //今日销售退货
        BigDecimal todayRetailSale = getBuyAndSaleRetailStatistics("出库", "零售",
                today, getNow3(), creatorArray); //今日零售出库
        BigDecimal todayRetailSaleBack = getBuyAndSaleRetailStatistics("入库", "零售退货",
                today, getNow3(), creatorArray); //今日零售退货
        //本月
        BigDecimal monthBuy = getBuyAndSaleBasicStatistics("入库", "采购",
                1, monthFirstDay, getNow3(), creatorArray); //本月采购入库
        BigDecimal monthBuyBack = getBuyAndSaleBasicStatistics("出库", "采购退货",
                1, monthFirstDay, getNow3(), creatorArray); //本月采购退货
        BigDecimal monthSale = getBuyAndSaleBasicStatistics("出库", "销售",
                1, monthFirstDay, getNow3(), creatorArray); //本月销售出库
        BigDecimal monthSaleBack = getBuyAndSaleBasicStatistics("入库", "销售退货",
                1, monthFirstDay, getNow3(), creatorArray); //本月销售退货
        BigDecimal monthRetailSale = getBuyAndSaleRetailStatistics("出库", "零售",
                monthFirstDay, getNow3(), creatorArray); //本月零售出库
        BigDecimal monthRetailSaleBack = getBuyAndSaleRetailStatistics("入库", "零售退货",
                monthFirstDay, getNow3(), creatorArray); //本月零售退货
        //昨日
        BigDecimal yesterdayBuy = getBuyAndSaleBasicStatistics("入库", "采购",
                1, yesterdayBegin, yesterdayEnd, creatorArray); //昨日采购入库
        BigDecimal yesterdayBuyBack = getBuyAndSaleBasicStatistics("出库", "采购退货",
                1, yesterdayBegin, yesterdayEnd, creatorArray); //昨日采购退货
        BigDecimal yesterdaySale = getBuyAndSaleBasicStatistics("出库", "销售",
                1, yesterdayBegin, yesterdayEnd, creatorArray); //昨日销售出库
        BigDecimal yesterdaySaleBack = getBuyAndSaleBasicStatistics("入库", "销售退货",
                1, yesterdayBegin, yesterdayEnd, creatorArray); //昨日销售退货
        BigDecimal yesterdayRetailSale = getBuyAndSaleRetailStatistics("出库", "零售",
                yesterdayBegin, yesterdayEnd, creatorArray); //昨日零售出库
        BigDecimal yesterdayRetailSaleBack = getBuyAndSaleRetailStatistics("入库", "零售退货",
                yesterdayBegin, yesterdayEnd, creatorArray); //昨日零售退货
        //今年
        BigDecimal yearBuy = getBuyAndSaleBasicStatistics("入库", "采购",
                1, yearBegin, yearEnd, creatorArray); //今年采购入库
        BigDecimal yearBuyBack = getBuyAndSaleBasicStatistics("出库", "采购退货",
                1, yearBegin, yearEnd, creatorArray); //今年采购退货
        BigDecimal yearSale = getBuyAndSaleBasicStatistics("出库", "销售",
                1, yearBegin, yearEnd, creatorArray); //今年销售出库
        BigDecimal yearSaleBack = getBuyAndSaleBasicStatistics("入库", "销售退货",
                1, yearBegin, yearEnd, creatorArray); //今年销售退货
        BigDecimal yearRetailSale = getBuyAndSaleRetailStatistics("出库", "零售",
                yearBegin, yearEnd, creatorArray); //今年零售出库
        BigDecimal yearRetailSaleBack = getBuyAndSaleRetailStatistics("入库", "零售退货",
                yearBegin, yearEnd, creatorArray); //今年零售退货
        map.put("todayBuy", roleService.parsePriceByLimit(todayBuy.subtract(todayBuyBack), "buy", "***", request));
        map.put("todaySale", roleService.parsePriceByLimit(todaySale.subtract(todaySaleBack), "sale", "***", request));
        map.put("todayRetailSale", roleService.parsePriceByLimit(todayRetailSale.subtract(todayRetailSaleBack), "retail", "***", request));
        map.put("monthBuy", roleService.parsePriceByLimit(monthBuy.subtract(monthBuyBack), "buy", "***", request));
        map.put("monthSale", roleService.parsePriceByLimit(monthSale.subtract(monthSaleBack), "sale", "***", request));
        map.put("monthRetailSale", roleService.parsePriceByLimit(monthRetailSale.subtract(monthRetailSaleBack), "retail", "***", request));
        map.put("yesterdayBuy", roleService.parsePriceByLimit(yesterdayBuy.subtract(yesterdayBuyBack), "buy", "***", request));
        map.put("yesterdaySale", roleService.parsePriceByLimit(yesterdaySale.subtract(yesterdaySaleBack), "sale", "***", request));
        map.put("yesterdayRetailSale", roleService.parsePriceByLimit(yesterdayRetailSale.subtract(yesterdayRetailSaleBack), "retail", "***", request));
        map.put("yearBuy", roleService.parsePriceByLimit(yearBuy.subtract(yearBuyBack), "buy", "***", request));
        map.put("yearSale", roleService.parsePriceByLimit(yearSale.subtract(yearSaleBack), "sale", "***", request));
        map.put("yearRetailSale", roleService.parsePriceByLimit(yearRetailSale.subtract(yearRetailSaleBack), "retail", "***", request));
        return map;
    }

    public BigDecimal getBuyAndSaleBasicStatistics(String type, String subType, Integer hasSupplier,
                                                   String beginTime, String endTime, String[] creatorArray) {
        return depotHeadMapperEx.getBuyAndSaleBasicStatistics(type, subType, hasSupplier, beginTime, endTime, creatorArray);
    }

    public BigDecimal getBuyAndSaleRetailStatistics(String type, String subType,
                                                    String beginTime, String endTime, String[] creatorArray) {
        return depotHeadMapperEx.getBuyAndSaleRetailStatistics(type, subType, beginTime, endTime, creatorArray).abs();
    }

    public DepotHead getDepotHead(String number) throws Exception {
        DepotHead depotHead = new DepotHead();
        try {
            DepotHeadExample example = new DepotHeadExample();
            example.createCriteria().andNumberEqualTo(number).andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
            List<DepotHead> list = depotHeadMapper.selectByExample(example);
            if (null != list && list.size() > 0) {
                depotHead = list.get(0);
            }
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return depotHead;
    }

    public List<DepotHeadVo4List> debtList(Long organId, String materialParam, String number, String beginTime, String endTime,
                                           String type, String subType, String roleType, String status, Integer offset, Integer rows) {
        List<DepotHeadVo4List> resList = new ArrayList<>();
        try {
            String depotIds = depotService.findDepotStrByCurrentUser();
            String[] depotArray = depotIds.split(",");
            String[] creatorArray = getCreatorArray(roleType);
            beginTime = Tools.parseDayToTime(beginTime, BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime, BusinessConstants.DAY_LAST_TIME);
            List<DepotHeadVo4List> list = depotHeadMapperEx.debtList(organId, creatorArray, status, number,
                    beginTime, endTime, type, subType, materialParam, depotArray, offset, rows);
            if (null != list) {
                List<Long> idList = new ArrayList<>();
                for (DepotHeadVo4List dh : list) {
                    idList.add(dh.getId());
                }
                //通过批量查询去构造map
                Map<Long, String> materialsListMap = findMaterialsListMapByHeaderIdList(idList);
                for (DepotHeadVo4List dh : list) {
                    BigDecimal needDebt = (dh.getChangeAmount() == null ? BigDecimal.ZERO : dh.getChangeAmount())
                            .add(dh.getDiscount() == null ? BigDecimal.ZERO : dh.getDiscount())
                            .subtract(dh.getOtherMoney() == null ? BigDecimal.ZERO : dh.getOtherMoney());
                    BigDecimal finishDebt = (dh.getBackAmount() == null ? BigDecimal.ZERO : dh.getBackAmount())
                            .add(dh.getDiscountMoney() == null ? BigDecimal.ZERO : dh.getDiscountMoney())
                            .subtract(dh.getDeposit() == null ? BigDecimal.ZERO : dh.getDeposit());
                    dh.setNeedDebt(needDebt);
                    dh.setFinishDebt(finishDebt);
                    dh.setDebt(needDebt.subtract(finishDebt));
                    if (dh.getOperTime() != null) {
                        dh.setOperTimeStr(getCenternTime(dh.getOperTime()));
                    }
                    //产品信息简述
                    if (materialsListMap != null) {
                        dh.setMaterialsList(materialsListMap.get(dh.getId()));
                    }
                    resList.add(dh);
                }
            }
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return resList;
    }

    public int debtListCount(Long organId, String materialParam, String number, String beginTime, String endTime,
                             String roleType, String status) {
        int total = 0;
        try {
            String depotIds = depotService.findDepotStrByCurrentUser();
            String[] depotArray = depotIds.split(",");
            String[] creatorArray = getCreatorArray(roleType);
            beginTime = Tools.parseDayToTime(beginTime, BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime, BusinessConstants.DAY_LAST_TIME);
            total = depotHeadMapperEx.debtListCount(organId, creatorArray, status, number,
                    beginTime, endTime, materialParam, depotArray);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return total;

    }

    public List<DepotHeadVo4List> purchaseInList(Long organId, String materialParam, String number, String beginTime, String endTime,
                                                 String roleType, String status, Integer offset, Integer rows) {
        List<DepotHeadVo4List> resList = new ArrayList<>();
        try {
            String depotIds = depotService.findDepotStrByCurrentUser();
            String[] depotArray = depotIds.split(",");
            String[] creatorArray = getCreatorArray(roleType);
            beginTime = Tools.parseDayToTime(beginTime, BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime, BusinessConstants.DAY_LAST_TIME);
            List<DepotHeadVo4List> list = depotHeadMapperEx.purchaseInList(organId, creatorArray, status, number,
                    beginTime, endTime, materialParam, depotArray, offset, rows);
            if (null != list) {
                List<Long> idList = new ArrayList<>();
                for (DepotHeadVo4List dh : list) {
                    idList.add(dh.getId());
                }
                //通过批量查询去构造map
                Map<Long, String> materialsListMap = findMaterialsListMapByHeaderIdList(idList);
                for (DepotHeadVo4List dh : list) {
                    if (dh.getTotalPrice() != null) {
                        dh.setTotalPrice(dh.getTotalPrice().abs());
                    }
                    if (dh.getOperTime() != null) {
                        dh.setOperTimeStr(getCenternTime(dh.getOperTime()));
                    }
                    if (dh.getPlanStartTime() != null) {
                        dh.setPlanStartTimeStr(new SimpleDateFormat("yyyy-MM-dd").format(dh.getPlanStartTime()));
                    }
                    if (dh.getPlanFinishTime() != null) {
                        dh.setPlanFinishTimeStr(new SimpleDateFormat("yyyy-MM-dd").format(dh.getPlanFinishTime()));
                    }
                    //产品信息简述
                    if (materialsListMap != null) {
                        dh.setMaterialsList(materialsListMap.get(dh.getId()));
                    }
                    resList.add(dh);
                }
            }
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return resList;
    }

    public int purchaseInListCount(Long organId, String materialParam, String number, String beginTime, String endTime,
                                   String roleType, String status) {
        int total = 0;
        try {
            String depotIds = depotService.findDepotStrByCurrentUser();
            String[] depotArray = depotIds.split(",");
            String[] creatorArray = getCreatorArray(roleType);
            beginTime = Tools.parseDayToTime(beginTime, BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime, BusinessConstants.DAY_LAST_TIME);
            total = depotHeadMapperEx.purchaseInListCount(organId, creatorArray, status, number,
                    beginTime, endTime, materialParam, depotArray);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return total;
    }

    public List<DepotHeadVo4List> saleOutList(Long organId, String materialParam, String number, String beginTime, String endTime,
                                              String roleType, String status, Integer offset, Integer rows) {
        List<DepotHeadVo4List> resList = new ArrayList<>();
        try {
            String depotIds = depotService.findDepotStrByCurrentUser();
            String[] depotArray = depotIds.split(",");
            String[] creatorArray = getCreatorArray(roleType);
            beginTime = Tools.parseDayToTime(beginTime, BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime, BusinessConstants.DAY_LAST_TIME);
            List<DepotHeadVo4List> list = depotHeadMapperEx.saleOutList(organId, creatorArray, status, number,
                    beginTime, endTime, materialParam, depotArray, offset, rows);
            if (null != list) {
                List<Long> idList = new ArrayList<>();
                for (DepotHeadVo4List dh : list) {
                    idList.add(dh.getId());
                }
                //通过批量查询去构造map
                Map<Long, String> materialsListMap = findMaterialsListMapByHeaderIdList(idList);
                for (DepotHeadVo4List dh : list) {
                    if (dh.getTotalPrice() != null) {
                        dh.setTotalPrice(dh.getTotalPrice().abs());
                    }
                    if (dh.getOperTime() != null) {
                        dh.setOperTimeStr(getCenternTime(dh.getOperTime()));
                    }
                    if (dh.getPlanStartTime() != null) {
                        dh.setPlanStartTimeStr(new SimpleDateFormat("yyyy-MM-dd").format(dh.getPlanStartTime()));
                    }
                    if (dh.getPlanFinishTime() != null) {
                        dh.setPlanFinishTimeStr(new SimpleDateFormat("yyyy-MM-dd").format(dh.getPlanFinishTime()));
                    }
                    //产品信息简述
                    if (materialsListMap != null) {
                        dh.setMaterialsList(materialsListMap.get(dh.getId()));
                    }
                    resList.add(dh);
                }
            }
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return resList;
    }

    public int saleOutListCount(Long organId, String materialParam, String number, String beginTime, String endTime,
                                String roleType, String status) {
        int total = 0;
        try {
            String depotIds = depotService.findDepotStrByCurrentUser();
            String[] depotArray = depotIds.split(",");
            String[] creatorArray = getCreatorArray(roleType);
            beginTime = Tools.parseDayToTime(beginTime, BusinessConstants.DAY_FIRST_TIME);
            endTime = Tools.parseDayToTime(endTime, BusinessConstants.DAY_LAST_TIME);
            total = depotHeadMapperEx.saleOutListCount(organId, creatorArray, status, number,
                    beginTime, endTime, materialParam, depotArray);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return total;
    }
}
