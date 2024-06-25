import { FormTypes, getListData } from '@/utils/JEditableTableUtil'
import {findBySelectSup,findBySelectCus,findBySelectRetail,getMaterialByBarCode,findStockByDepotAndBarCode,getAccount,
  getPersonByNumType, getAllPerson, getDepartment, getBatchNumberList, getCurrentSystemConfig} from '@/api/api'
import { getAction,putAction } from '@/api/manage'
import { getMpListShort, getNowFormatDateTime, getCheckFlag } from "@/utils/util"
import { USER_INFO } from "@/store/mutation-types"
import Vue from 'vue'

export const BillModalMixin = {
  data() {
    return {
      action: '',
      manyAccountBtnStatus: false,
      supList: [],
      cusList: [],
      retailList: [],
      personList: {
        options: [],
        value: ''
      },
      departmentList: {
        options: [],
        value: ''
      },
      depotList: [],
      accountList: [],
      accountIdList: [],
      accountMoneyList: [],
      billUnitPirce: '',
      scanBarCode: '',
      scanStatus: true,
      billStatus: '0',
      minWidth: 1100,
      isCanCheck: true,
      isTenant: false,
      /* 原始审核是否开启 */
      checkFlag: true,
      validatorRules:{
        price:{
          rules: [
            { pattern: /^(([0-9][0-9]*)|([0]\.\d{0,4}|[0-9][0-9]*\.\d{0,4}))$/, message: '金额格式不正确!' }
          ]
        }
      },
      spans: {
        labelCol1: {span: 2},
        wrapperCol1: {span: 22},
        //1_5: 分为1.5列（相当于占了2/3）
        labelCol1_5: { span: 3 },
        wrapperCol1_5: { span: 21 },
        labelCol2: {span: 4},
        wrapperCol2: {span: 20},
        labelCol3: {span: 6},
        wrapperCol3: {span: 18},
        labelCol6: {span: 12},
        wrapperCol6: {span: 12}
      },
    };
  },
  created () {
    let userInfo = Vue.ls.get(USER_INFO)
    this.isTenant = userInfo.id === userInfo.tenantId? true:false
    let realScreenWidth = window.screen.width
    this.width = realScreenWidth<1500?'1200px':'1550px'
    this.minWidth = realScreenWidth<1500?1150:1500
  },
  computed: {
    readOnly: function() {
      return this.action !== "add" && this.action !== "edit";
    }
  },
  methods: {
    addInit(amountNum) {
      getAction('/sequence/buildNumber').then((res) => {
        if (res && res.code === 200) {
          this.form.setFieldsValue({'number':amountNum + res.data.defaultNumber})
        }
      })
      this.$nextTick(() => {
        this.form.setFieldsValue({'operTime':getNowFormatDateTime(),
          'discountLastMoney': 0, 'otherMoney': 0, 'changeAmount': 0, 'debt': 0})
      })
      this.$nextTick(() => {
      })
      this.accountIdList = []
      this.accountMoneyList = []
      this.manyAccountBtnStatus = false
    },
    copyAddInit(amountNum) {
      getAction('/sequence/buildNumber').then((res) => {
        if (res && res.code === 200) {
          this.form.setFieldsValue({'number':amountNum + res.data.defaultNumber})
        }
      })
      this.$nextTick(() => {
        this.form.setFieldsValue({'operTime':getNowFormatDateTime()})
      })
    },
    /** 查询某个tab的数据 */
    requestSubTableData(url, params, tab, success) {
      tab.loading = true
      getAction(url, params).then(res => {
        if(res && res.code === 200){
          tab.dataSource = res.data.rows
          for(let i=0; i<tab.dataSource.length; i++){
            let info = tab.dataSource[i]
            this.changeColumnShow(info)
          }
          typeof success === 'function' ? success(res) : ''
        }
      }).finally(() => {
        tab.loading = false
      })
    },
    //改变字段的状态，1-显示 0-隐藏
    changeFormTypes(columns, key, type) {
      for(let i=0; i<columns.length; i++){
        if(columns[i].key === key) {
          if(type){
            if(key === 'batchNumber') {
              if(this.prefixNo === 'LSCK' || this.prefixNo === 'CGTH'  || this.prefixNo === 'XSCK'
                  || this.prefixNo === 'DBCK' || this.prefixNo === 'QTCK' || this.prefixNo === 'FXCK'
                  || this.prefixNo === 'LLCK' || this.prefixNo === 'GLCK') {
                columns[i].type = FormTypes.popupJsh //显示
              } else {
                columns[i].type = FormTypes.input //输入
              }
            } else if(key === 'sku') {
              if(this.prefixNo === 'CGDD' || this.prefixNo === 'CGSQ') {
                columns[i].type = FormTypes.popupJsh //显示
              } else {
                columns[i].type = FormTypes.hidden //隐藏
              }
            } else if(key === 'expirationDate') {
              if(this.prefixNo === 'CGDD' || this.prefixNo === 'CGSQ') {
                columns[i].type = FormTypes.date //显示
              } else {
                columns[i].type = FormTypes.hidden //隐藏
              }
            } else if(key === 'snList') {
              if(this.prefixNo === 'LSTH' || this.prefixNo === 'CGRK' || this.prefixNo === 'SCRK'
                  || this.prefixNo === 'XSTH' || this.prefixNo === 'QTRK' || this.prefixNo === 'DBCK'
                  || this.prefixNo === 'FXRK' || this.prefixNo === 'CYRK' || this.prefixNo === 'TLRK') {
                columns[i].type = FormTypes.select //显示
              } else {
                columns[i].type = FormTypes.hidden //隐藏
              }
            } else {
              columns[i].type = FormTypes.normal //显示
            }
          } else {
            columns[i].type = FormTypes.hidden //隐藏
          }
        }
      }
    },
    initSystemConfig() {
      getCurrentSystemConfig().then((res) => {
        if(res.code === 200 && res.data){
          let multiBillType = res.data.multiBillType
          let multiLevelApprovalFlag = res.data.multiLevelApprovalFlag
          this.checkFlag = getCheckFlag(multiBillType, multiLevelApprovalFlag, this.prefixNo)
        }
      })
    },
    initSupplier() {
      let that = this;
      findBySelectSup({}).then((res)=>{
        if(res) {
          that.supList = res;
        }
      });
    },
    initCustomer() {
      let that = this;
      findBySelectCus({}).then((res)=>{
        if(res) {
          that.cusList = res;
        }
      });
    },
    initRetail() {
      let that = this;
      findBySelectRetail({}).then((res)=>{
        if(res) {
          that.retailList = res;
        }
      });
    },
    initSalesman() {
      let that = this;
      getPersonByNumType({type:1}).then((res)=>{
        if(res) {
          that.personList.options = res;
        }
      });
    },
    initDepartment() {
      let that = this;
      getDepartment().then((res)=>{
        if(res) {
          that.departmentList.options = res;
        }
      });
    },
    initPerson() {
      let that = this;
      getAllPerson().then((res)=>{
        if(res) {
          that.personList.options = res;
        }
      });
    },
    initDepotMan() {
      let that = this;
      getPersonByNumType({type:2}).then((res)=>{
        if(res) {
          that.personList.options = res;
        }
      });
    },
    initDepot() {
      let that = this;
      getAction('/depot/findDepotByCurrentUser').then((res) => {
        if(res.code === 200){
          let arr = res.data
          if(that.prefixNo == "FXCK") {
            for (let i = 0; i < arr.length; i++) {
              if(arr[i].depotName == "隔离库"){
                that.defaultDepotId = arr[i].id
              }
            }
          } else {
            for (let i = 0; i < arr.length; i++) {
              if(arr[i].isDefault){
                that.defaultDepotId = arr[i].id
              }
            }
          }
          for(let item of that.materialTable.columns){
            if(item.key == 'depotId' || item.key == 'anotherDepotId') {
              item.options = []
              for(let i=0; i<arr.length; i++) {
                let depotInfo = {};
                depotInfo.value = arr[i].id + '' //注意-此处value必须为字符串格式
                depotInfo.text = arr[i].depotName
                depotInfo.title = arr[i].depotName
                item.options.push(depotInfo)
              }
            }
          }
        }
      })
    },
    initAllocation() {
      let that = this;
      getAction('/depot/findAllocation').then((res) => {
        if(res.code === 200){
          let arr = res.data
          for(let item of that.materialTable.columns){
            if(item.key == 'snList') {
              item.options = []
              let info1 = {};
              info1.value = 564
              info1.text = "期初-期初"
              info1.title = "期初-期初"
              let info2 = {};
              info2.value = 565
              info2.text = "隔离-隔离"
              info2.title = "隔离-隔离"
              item.options.push(info1)
              item.options.push(info2)
              for(let i=0; i<arr.length; i++) {
                if( arr[i].allocation!='期初' && arr[i].allocation!='隔离' ) {
                  let allocationInfo = {};
                  let text = arr[i].allocation + '-' + arr[i].type;
                  allocationInfo.value = arr[i].id
                  allocationInfo.text = text
                  allocationInfo.title = text
                  item.options.push(allocationInfo)
                }
              }
            }
          }
        }
      })
    },
    initAccount(){
      let that = this;
      getAccount({}).then((res)=>{
        if(res && res.code === 200) {
          let list = res.data.accountList
          list.splice(0,0,{id: 0, name: '多账户'})
          that.accountList = list
        }
      })
    },
    handleManyAccount(){
      this.selectAccount(0)
    },
    selectAccount(value){
      if(value === 0) { //多账户
        this.$refs.manyAccountModalForm.edit(this.accountIdList, this.accountMoneyList)
        this.$refs.manyAccountModalForm.title = "多账户结算"
        this.manyAccountBtnStatus = true
      } else {
        this.accountIdList = []
        this.accountMoneyList = []
        this.manyAccountBtnStatus = false
      }
    },
    manyAccountModalFormOk(idList, moneyList, allPrice) {
      this.accountIdList = idList
      this.accountMoneyList = moneyList
      this.$nextTick(() => {
      });
    },
    addSupplier() {
      this.$refs.vendorModalForm.add();
      this.$refs.vendorModalForm.title = "新增供应商";
      this.$refs.vendorModalForm.disableSubmit = false;
    },
    addCustomer() {
      this.$refs.customerModalForm.add();
      this.$refs.customerModalForm.title = "新增客户（提醒：如果找不到新添加的客户，请到用户管理检查是否分配了该客户权限）";
      this.$refs.customerModalForm.disableSubmit = false;
    },
    addMember() {
      this.$refs.memberModalForm.add();
      this.$refs.memberModalForm.title = "新增会员";
      this.$refs.memberModalForm.disableSubmit = false;
    },
    handleBatchSetDepot() {
      this.$refs.batchSetDepotModalForm.add();
      this.$refs.batchSetDepotModalForm.title = "批量设置仓库";
      this.$refs.batchSetDepotModalForm.disableSubmit = false;
    },
    handleBatchSetAllocation() {
      this.$refs.batchSetAllocationModalForm.add();
      this.$refs.batchSetAllocationModalForm.title = "批量设置货位";
      this.$refs.batchSetAllocationModalForm.disableSubmit = false;
    },
    addDepot() {
      this.$refs.depotModalForm.add();
      this.$refs.depotModalForm.title = "新增仓库";
      this.$refs.depotModalForm.disableSubmit = false;
    },
    addAccount() {
      this.$refs.accountModalForm.add();
      this.$refs.accountModalForm.title = "新增结算账户";
      this.$refs.accountModalForm.disableSubmit = false;
    },
    vendorModalFormOk() {
      this.initSupplier()
    },
    customerModalFormOk() {
      this.initCustomer()
    },
    memberModalFormOk() {
      this.initRetail()
    },
    batchSetDepotModalFormOk(depotId) {
      this.getAllTable().then(tables => {
        return getListData(this.form, tables)
      }).then(allValues => {
        //获取单据明细列表信息
        let detailArr = allValues.tablesValue[0].values
        let barCodes = ''
        for(let detail of detailArr){
          barCodes += detail.barCode + ','
        }
        if(barCodes) {
          barCodes = barCodes.substring(0, barCodes.length-1)
        }
        let param = {
          barCode: barCodes,
          depotId: depotId,
          mpList: getMpListShort(Vue.ls.get('materialPropertyList')),  //扩展属性
          prefixNo: this.prefixNo
        }
        getMaterialByBarCode(param).then((res) => {
          if (res && res.code === 200) {
            let mList = res.data
            //构造新的列表数组，用于存放单据明细信息
            let newDetailArr = []
            if(mList && mList.length) {
              for (let i = 0; i < detailArr.length; i++) {
                let item = detailArr[i]
                item.depotId = depotId
                for (let j = 0; j < mList.length; j++) {
                  if(mList[j].mBarCode === item.barCode) {
                    item.stock = mList[j].stock
                  }
                }
                newDetailArr.push(item)
              }
            } else {
              for (let i = 0; i < detailArr.length; i++) {
                let item = detailArr[i]
                item.depotId = depotId
                newDetailArr.push(item)
              }
            }
            this.materialTable.dataSource = newDetailArr
          }
        })
      })
    },
    batchSetAllocationModalFormOk(snList) {
      this.getAllTable().then(tables => {
        return getListData(this.form, tables)
      }).then(allValues => {
        //获取单据明细列表信息
        let detailArr = allValues.tablesValue[0].values
        let newDetailArr = []
        for(let detail of detailArr){
          let item = detail
          item.snList = snList
          newDetailArr.push(item)
        }
        this.materialTable.dataSource = newDetailArr
      })
    },
    depotModalFormOk() {
      this.initDepot()
    },
    accountModalFormOk() {
      this.initAccount()
    },
    onAdded(event) {
      const { row, target } = event
      target.setValues([{rowKey: row.id, values: {operNumber:0}}])
      getAction('/depot/findDepotByCurrentUser').then((res) => {
        if (res.code === 200) {
          let arr = res.data
          if(arr.length===1) {
            target.setValues([{rowKey: row.id, values: {depotId: arr[0].id+''}}])
          } else {
            if(this.prefixNo == "FXCK") {
              for (let i = 0; i < arr.length; i++) {
                if(arr[i].depotName == "隔离库"){
                  target.setValues([{rowKey: row.id, values: {depotId: arr[i].id+''}}])
                }
              }
            } else {
              for (let i = 0; i < arr.length; i++) {
                if(arr[i].isDefault){
                  target.setValues([{rowKey: row.id, values: {depotId: arr[i].id+''}}])
                }
              }
            }
          }
        }
      })
    },
    //单元值改变一个字符就触发一次
    onValueChange(event) {
      let that = this
      const { type, row, column, value, target } = event
      let param,snList,batchNumber,operNumber,unitPrice,allPrice,taxRate,taxMoney,taxLastMoney
      switch(column.key) {
        case "depotId":
          if(row.barCode){
            that.getStockByDepotBarCode(row, target)
          }
          break;
        case "barCode":
          param = {
            barCode: value,
            mpList: getMpListShort(Vue.ls.get('materialPropertyList')),  //扩展属性
            prefixNo: this.prefixNo
          }
          getMaterialByBarCode(param).then((res) => {
            if (res && res.code === 200) {
              let mList = res.data
              if (value.indexOf(',') > -1) {
                //多个条码
                this.$refs.materialDataTable.getValues((error, values) => {
                  values.pop()  //移除最后一行数据
                  let mArr = values
                  for (let i = 0; i < mList.length; i++) {
                    let mInfo = mList[i]
                    this.changeColumnShow(mInfo)
                    let mObj = this.parseInfoToObj(mInfo)
                    mObj.depotId = mInfo.depotId
                    mObj.stock = mInfo.stock
                    mArr.push(mObj)
                  }
                  let allPriceTotal = 0
                  for (let j = 0; j < mArr.length; j++) {
                    allPriceTotal += mArr[j].allPrice-0
                    //组合和拆分单据给商品类型进行重新赋值
                    if(j===0) {
                      mArr[0].mType = '组合件'
                    } else {
                      mArr[j].mType = '普通子件'
                    }
                  }
                  this.materialTable.dataSource = mArr
                  if (!Number.isNaN(allPriceTotal)) {
                    target.statisticsColumns.allPrice = allPriceTotal
                  }
                  that.autoChangePrice(target)
                })
              } else {
                //单个条码
                let depotIdSelected = this.prefixNo !== 'CGDD' && this.prefixNo !== 'XSDD'? row.depotId: ''
                findStockByDepotAndBarCode({ depotId: depotIdSelected, barCode: row.barCode }).then((res) => {
                  if (res && res.code === 200) {
                    let mArr = []
                    let mInfo = mList[0]
                    this.changeColumnShow(mInfo)
                    let mInfoEx = this.parseInfoToObj(mInfo)
                    mInfoEx.stock = res.data.stock
                    let mObj = {
                      rowKey: row.id,
                      values: mInfoEx
                    }
                    mArr.push(mObj)
                    target.setValues(mArr);
                    target.recalcAllStatisticsColumns()
                    that.autoChangePrice(target)
                    target.autoSelectBySpecialKey('operNumber', row.orderNum)
                  }
                })
              }
            }
          });
          break;
        case "operNumber":
          operNumber = value-0
          taxRate = row.taxRate-0 //税率
          unitPrice = row.unitPrice-0 //单价
          allPrice = (unitPrice*operNumber).toFixed(2)-0
          taxMoney =((taxRate*0.01)*allPrice).toFixed(2)-0
          taxLastMoney = (allPrice + taxMoney).toFixed(2)-0
          target.setValues([{rowKey: row.id, values: {allPrice: allPrice, taxMoney: taxMoney, taxLastMoney: taxLastMoney}}])
          target.recalcAllStatisticsColumns()
          that.autoChangePrice(target)
          break;
        case "unitPrice":
          operNumber = row.operNumber-0 //数量
          unitPrice = value-0 //单价
          taxRate = row.taxRate-0 //税率
          allPrice = (unitPrice*operNumber).toFixed(2)-0
          taxMoney =((taxRate*0.01)*allPrice).toFixed(2)-0
          taxLastMoney = (allPrice + taxMoney).toFixed(2)-0
          target.setValues([{rowKey: row.id, values: {allPrice: allPrice, taxMoney: taxMoney, taxLastMoney: taxLastMoney}}])
          target.recalcAllStatisticsColumns()
          that.autoChangePrice(target)
          break;
        case "allPrice":
          operNumber = row.operNumber-0 //数量
          taxRate = row.taxRate-0 //税率
          allPrice = value-0
          unitPrice = (allPrice/operNumber).toFixed(2)-0 //单价
          taxMoney =((taxRate*0.01)*allPrice).toFixed(2)-0
          taxLastMoney = (allPrice + taxMoney).toFixed(2)-0
          target.setValues([{rowKey: row.id, values: {unitPrice: unitPrice, taxMoney: taxMoney, taxLastMoney: taxLastMoney}}])
          target.recalcAllStatisticsColumns()
          that.autoChangePrice(target)
          break;
        case "taxRate":
          operNumber = row.operNumber-0 //数量
          allPrice = row.allPrice-0
          unitPrice = row.unitPrice-0
          taxRate = value-0 //税率
          taxMoney =((taxRate*0.01)*allPrice).toFixed(2)-0
          taxLastMoney = (allPrice + taxMoney).toFixed(2)-0
          target.setValues([{rowKey: row.id, values: {taxMoney: taxMoney, taxLastMoney: taxLastMoney}}])
          target.recalcAllStatisticsColumns()
          that.autoChangePrice(target)
          break;
        case "taxLastMoney":
          operNumber = row.operNumber-0 //数量
          taxLastMoney = value-0
          taxRate = row.taxRate-0 //税率
          unitPrice = (taxLastMoney/operNumber/(1+taxRate*0.01)).toFixed(2)-0
          allPrice = (unitPrice*operNumber).toFixed(2)-0
          taxMoney =(taxLastMoney-allPrice).toFixed(2)-0
          target.setValues([{rowKey: row.id, values: {unitPrice: unitPrice, allPrice: allPrice, taxMoney: taxMoney}}])
          target.recalcAllStatisticsColumns()
          that.autoChangePrice(target)
          break;
      }
    },
    //转为商品对象
    parseInfoToObj(mInfo) {
      return {
        barCode: mInfo.mBarCode,
        name: mInfo.name,
        colorCode: mInfo.colorCode,
        model: mInfo.model,
        color: mInfo.color,
        materialOther: mInfo.materialOther,
        categoryName: mInfo.categoryName,
        unit: mInfo.commodityUnit,
        sku: mInfo.sku,
        operNumber: 1,
        taxRate: 0,
        taxMoney: 0,
        taxLastMoney: 0
      }
    },
    //使得型号、颜色、扩展信息、sku等为隐藏
    changeColumnHide() {
      this.changeFormTypes(this.materialTable.columns, 'model', 0)
      this.changeFormTypes(this.materialTable.columns, 'color', 0)
      this.changeFormTypes(this.materialTable.columns, 'categoryName', 0)
      this.changeFormTypes(this.materialTable.columns, 'materialOther', 0)
    },
    //使得sku、序列号、批号、到期日等为显示
    changeColumnShow(info) {
      if(info.model) {
        this.changeFormTypes(this.materialTable.columns, 'model', 1)
      }
      if(info.color) {
        this.changeFormTypes(this.materialTable.columns, 'color', 1)
      }
      if(info.materialOther) {
        this.changeFormTypes(this.materialTable.columns, 'materialOther', 1)
      }
      if(info.categoryName) {
        this.changeFormTypes(this.materialTable.columns, 'categoryName', 1)
      }
      this.changeFormTypes(this.materialTable.columns, 'sku', 1)
      this.changeFormTypes(this.materialTable.columns, 'snList', 1)
      this.changeFormTypes(this.materialTable.columns, 'batchNumber', 1)
      this.changeFormTypes(this.materialTable.columns, 'expirationDate', 1)
    },
    //删除一行或多行的时候触发
    onDeleted(ids, target) {
      target.recalcAllStatisticsColumns()
      this.autoChangePrice(target)
    },
    //根据仓库和条码查询库存
    getStockByDepotBarCode(row, target){
      findStockByDepotAndBarCode({ depotId: row.depotId, barCode: row.barCode }).then((res) => {
        if (res && res.code === 200) {
          target.setValues([{rowKey: row.id, values: {stock: res.data.stock}}])
          target.recalcAllStatisticsColumns()
        }
      })
    },
    //改变优惠、本次付款、欠款的值
    autoChangePrice(target) {
      let allTaxLastMoney = target.statisticsColumns.taxLastMoney-0
      let otherMoney = this.form.getFieldValue('otherMoney')?this.form.getFieldValue('otherMoney')-0:0
      let deposit = this.form.getFieldValue('deposit')
      let discountLastMoney = allTaxLastMoney
      let changeAmountNew = (discountLastMoney + otherMoney).toFixed(2)-0
      if(deposit) {
        changeAmountNew = (changeAmountNew - deposit).toFixed(2)-0
      }
      this.$nextTick(() => {
        changeAmountNew = this.prefixNo === 'CGDD' || this.prefixNo === 'XSDD'?0:changeAmountNew
        this.form.setFieldsValue({'discountLastMoney':discountLastMoney,'changeAmount':changeAmountNew,'debt':0})
      });
    },
    //其它费用
    onChangeOtherMoney(e) {
      const value = e.target.value-0
      let discountLastMoney = this.form.getFieldValue('discountLastMoney')-0
      let deposit = this.form.getFieldValue('deposit')
      let changeAmountNew = (discountLastMoney + value).toFixed(2)-0
      if(deposit) {
        changeAmountNew = (changeAmountNew - deposit).toFixed(2)-0
      }
      this.$nextTick(() => {
        this.form.setFieldsValue({'changeAmount':changeAmountNew, 'debt':0})
      });
    },
    //改变扣除订金
    onChangeDeposit(e){
      const value = e.target.value-0
      let discountLastMoney = this.form.getFieldValue('discountLastMoney')-0
      let otherMoney = this.form.getFieldValue('otherMoney')?this.form.getFieldValue('otherMoney')-0:0
      let changeAmountNew = (discountLastMoney + otherMoney).toFixed(2)-0
      if(value) {
        changeAmountNew = (changeAmountNew - value).toFixed(2)-0
      }
      this.$nextTick(() => {
        this.form.setFieldsValue({'changeAmount':changeAmountNew, 'debt':0})
      });
    },
    //改变本次付款
    onChangeChangeAmount(e) {
      const value = e.target.value-0
      let discountLastMoney = this.form.getFieldValue('discountLastMoney')-0
      let otherMoney = this.form.getFieldValue('otherMoney')?this.form.getFieldValue('otherMoney')-0:0
      let deposit = this.form.getFieldValue('deposit')
      let debtNew = (discountLastMoney + otherMoney - value).toFixed(2)-0
      if(deposit) {
        debtNew = (debtNew - deposit).toFixed(2)-0
      }
      this.$nextTick(() => {
        this.form.setFieldsValue({'debt':debtNew})
      });
    },
    scanEnter() {
      this.scanStatus = false
      this.$nextTick(() => {
        this.$refs.scanBarCode.focus()
      })
    },
    //扫码之后回车
    scanPressEnter() {
      if(this.scanBarCode) {
        this.getAllTable().then(tables => {
          return getListData(this.form, tables)
        }).then(allValues => {
          let map = this.parseBarCode(this.scanBarCode)
          let param = {
            barCode: this.scanBarCode,
            mpList: getMpListShort(Vue.ls.get('materialPropertyList')),  //扩展属性
            prefixNo: this.prefixNo
          }
          if (map.size > 0) {
            param.barCode = this.getBarCodeStrOnly(this.scanBarCode)
          }
          getMaterialByBarCode(param).then((res) => {
            if (res && res.code === 200) {
              let hasFinished = false
              let allLastMoney = 0
              //获取单据明细列表信息
              let detailArr = allValues.tablesValue[0].values
              //构造新的列表数组，用于存放单据明细信息
              let newDetailArr = []
              for(let detail of detailArr){
                if(detail.barCode) {
                  //如果条码重复，就在给原来的数量加1
                  if(detail.barCode === this.scanBarCode) {
                    detail.operNumber = (detail.operNumber-0)+1
                    hasFinished = true
                  }
                  newDetailArr.push(detail)
                }
              }
              if(!hasFinished) {
                //将扫码的条码对应的商品加入列表
                let mList = res.data
                if(mList && mList.length>0) {
                  for (let i = 0; i < mList.length; i++) {
                    let item = {}
                    let mInfo = mList[i]
                    item.barCode = mInfo.mBarCode
                    this.changeColumnShow(mInfo)
                    item.depotId = mInfo.depotId
                    item.name = mInfo.name
                    item.colorCode = mInfo.colorCode
                    item.model = mInfo.model
                    item.color = mInfo.color
                    item.categoryName = mInfo.categoryName
                    item.materialOther = mInfo.materialOther
                    item.stock = mInfo.stock
                    item.unit = mInfo.commodityUnit
                    item.sku = mInfo.sku
                    item.operNumber = 1
                    if (map.size > 0) {
                      item.operNumber = map.get(mInfo.mBarCode)
                    }
                    newDetailArr.push(item)
                  }
                } else {
                  this.$message.warning('抱歉，此条码不存在商品信息！');
                }
              }
              //组合和拆分单据给商品类型进行重新赋值
              for(let i=0; i< newDetailArr.length; i++) {
                if(i===0) {
                  newDetailArr[0].mType = '组合件'
                } else {
                  newDetailArr[i].mType = '普通子件'
                }
              }
              this.materialTable.dataSource = newDetailArr
              //更新优惠后金额、本次付款等信息
              for(let newDetail of newDetailArr){
                allLastMoney = allLastMoney + (newDetail.allPrice-0)
              }
              let discountLastMoney = allLastMoney-0
              if(this.prefixNo === 'LSCK' || this.prefixNo === 'LSTH') {
                this.$nextTick(() => {
                  //this.form.setFieldsValue({'changeAmount':0,'getAmount':0,'backAmount':0})
                });
              } else {
                this.$nextTick(() => {
                  changeAmountNew = this.prefixNo === 'CGDD' || this.prefixNo === 'XSDD'?0:changeAmountNew
                  this.form.setFieldsValue({'discountLastMoney':discountLastMoney,'changeAmount':0,'debt':0})
                });
              }
              //置空扫码的内容
              this.scanBarCode = ''
              this.$refs.scanBarCode.focus()
            }
          })
        })
      }
    },
    isNumber: function (str) {
      return /^\d+$/.test(str);
    },
    getBarCodeStrOnly: function (str) {
      var arr = str.split(',')
      var res = ''
      for (let i = 0; i < arr.length; i++) {
        if(i % 2 == 0) {
          res = res + arr[i]
          if(i < arr.length - 2) {
            res = res + ','
          }
        }
      }
      return res
    },
    parseBarCode: function (str) {
      var arr = str.split(',')
      let map = new Map()
      if (arr.length == 1) {
        return map
      }
      for (let i = 0; i < arr.length; i++) {
        if(i % 2 > 0) {
          if (this.isNumber(arr[i])) {
            map.set(arr[i-1], arr[i]-0)
          } else {
            return new Map()
          }
        }
      }
      return map
    },
    stopScan() {
      this.scanStatus = true
      this.scanBarCode = ''
    },
    //保存并审核
    handleOkAndCheck() {
      this.billStatus = '1'
      this.handleOk()
    },
  }
}