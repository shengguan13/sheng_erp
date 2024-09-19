<template>
  <j-modal
    :title="title"
    :width="width"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :keyboard="false"
    :forceRender="true"
    v-bind:prefixNo="prefixNo"
    switchHelp
    switchFullscreen
    @cancel="handleCancel"
    :id="prefixNo"
    style="top:20px;height: 95%;">
    <template slot="footer">
      <a-button @click="handleCancel">取消</a-button>
      <a-button v-if="checkFlag && isCanCheck" @click="handleOkAndCheck">保存并审核</a-button>
      <a-button type="primary" @click="handleOk">保存</a-button>
    </template>
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row class="form-row" :gutter="24">
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联生产单" data-step="3" data-title="关联生产单"
              data-intro="领料出库必须关联生产单">
              <a-input-search placeholder="请选择关联订单" v-decorator="[ 'linkNumber' ]" @search="onSearchLinkNumber" :readOnly="true"/>
            </a-form-item>
          </a-col>
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据日期">
              <j-date v-decorator="['operTime', validatorRules.operTime]" :show-time="true"/>
            </a-form-item>
          </a-col>
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单据编号" data-step="2" data-title="单据编号"
                         data-intro="单据编号自动生成、自动累加、开头是单据类型的首字母缩写，累加的规则是每次打开页面会自动占用一个新的编号">
              <a-input placeholder="请输入单据编号" v-decorator.trim="[ 'number' ]" :readOnly="true"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="24">
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="领料人">
              <a-select placeholder="选择领料人" v-decorator="[ 'salesMan', validatorRules.salesMan ]"
                :dropdownMatchSelectWidth="false" showSearch optionFilterProp="children">
                <a-select-option v-for="(item,index) in personList.options" :key="index" :value="item.value">
                  {{ item.text }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <j-editable-table id="billModal"
          :ref="refKeys[0]"
          :loading="materialTable.loading"
          :columns="materialTable.columns"
          :dataSource="materialTable.dataSource"
          :minWidth="minWidth"
          :maxHeight="300"
          :rowNumber="false"
          :rowSelection="true"
          :actionButton="true"
          :dragSort="true"
          @valueChange="onValueChange"
          @added="onAdded"
          @deleted="onDeleted">
          <template #buttonAfter>
            <a-row :gutter="24" style="float:left;padding-bottom: 5px;" data-step="6" data-title="扫码录入" data-intro="此功能支持扫码枪扫描产品编码进行录入">
              <a-col v-if="scanStatus" :md="6" :sm="24">
                <a-button @click="scanEnter" style="margin-right: 8px">扫码录入</a-button>
              </a-col>
              <a-col v-if="!scanStatus" :md="16" :sm="24" style="padding: 0 8px 0 12px">
                <a-input placeholder="请扫码产品编码并回车" v-model="scanBarCode" @pressEnter="scanPressEnter" ref="scanBarCode"/>
              </a-col>
              <a-col v-if="!scanStatus" :md="6" :sm="24" style="padding: 0px 24px 0 0">
                <a-button @click="stopScan" style="margin-right: 8px">收起扫码</a-button>
              </a-col>
            </a-row>
            <a-row :gutter="24" style="float:left;padding-bottom: 5px;">
              <a-col :md="24" :sm="24">
                <a-dropdown>
                  <a-menu slot="overlay">
                    <a-menu-item key="1" @click="handleBatchSetDepot"><a-icon type="setting"/>批量设置</a-menu-item>
                    <a-menu-item v-if="isTenant" key="2" @click="addDepot"><a-icon type="plus"/>新增仓库</a-menu-item>
                  </a-menu>
                  <a-button>仓库操作 <a-icon type="down" /></a-button>
                </a-dropdown>
              </a-col>
            </a-row>
            <a-row :gutter="24" style="float:left;padding-bottom: 5px;">
              <a-col :md="24" :sm="24">
                <a-button style="margin-left: 8px" @click="handleHistoryBillList"><a-icon type="history" />历史单据</a-button>
              </a-col>
            </a-row>
          </template>
        </j-editable-table>
        <a-row class="form-row" :gutter="24">
          <a-col :lg="24" :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="">
              <a-input placeholder="生产单详细" v-decorator.trim="[ 'orderStatus' ]" :readOnly="true"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="24">
          <a-col :lg="24" :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="{xs: { span: 24 },sm: { span: 24 }}" label="">
              <a-textarea :rows="1" placeholder="请输入备注" v-decorator="[ 'remark' ]" style="margin-top:8px;"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="24">
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="附件" data-step="7" data-title="附件"
                         data-intro="可以上传与单据相关的图片、文档，支持多个文件">
              <j-upload v-model="fileList" bizPath="bill"></j-upload>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
    <many-account-modal ref="manyAccountModalForm" @ok="manyAccountModalFormOk"></many-account-modal>
    <production-order-link-list ref="productionOrderLinkList" @ok="linkBillListOk"></production-order-link-list>
    <depot-modal ref="depotModalForm" @ok="depotModalFormOk"></depot-modal>
    <account-modal ref="accountModalForm" @ok="accountModalFormOk"></account-modal>
    <batch-set-depot ref="batchSetDepotModalForm" @ok="batchSetDepotModalFormOk"></batch-set-depot>
    <sales-man-based-history-bill-list ref="salesManBasedHistoryBillListModalForm"></sales-man-based-history-bill-list>
  </j-modal>
</template>
<script>
  import pick from 'lodash.pick'
  import ManyAccountModal from '../dialog/ManyAccountModal'
  import ProductionOrderLinkList from '../dialog/ProductionOrderLinkList'
  import DepotModal from '../../system/modules/DepotModal'
  import AccountModal from '../../system/modules/AccountModal'
  import BatchSetDepot from '../dialog/BatchSetDepot'
  import SalesManBasedHistoryBillList from '../dialog/SalesManBasedHistoryBillList'
  import { FormTypes } from '@/utils/JEditableTableUtil'
  import { JEditableTableMixin } from '@/mixins/JEditableTableMixin'
  import { BillModalMixin } from '../mixins/BillModalMixin'
  import { getMpListShort,handleIntroJs } from "@/utils/util"
  import { getAction } from '@/api/manage'
  import { getMaterialPickByBarCodeAndAmount, getMaterialByCompositePrefix } from '@/api/api'
  import JSelectMultiple from '@/components/jeecg/JSelectMultiple'
  import JUpload from '@/components/jeecg/JUpload'
  import JDate from '@/components/jeecg/JDate'
  import Vue from 'vue'
  export default {
    name: "MaterialPickModal",
    mixins: [JEditableTableMixin, BillModalMixin],
    components: {
      ManyAccountModal,
      ProductionOrderLinkList,
      DepotModal,
      AccountModal,
      BatchSetDepot,
      SalesManBasedHistoryBillList,
      JUpload,
      JDate,
      JSelectMultiple,
      VNodes: {
        functional: true,
        render: (h, ctx) => ctx.props.vnodes,
      }
    },
    data () {
      return {
        title:"操作",
        width: '1600px',
        moreStatus: false,
        // 新增时子表默认添加几行空数据
        addDefaultRowNum: 1,
        visible: false,
        operTimeStr: '',
        orderStatusStr: '',
        prefixNo: 'LLCK',
        depositStatus: false,
        fileList:[],
        defaultDepotId: '',
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 8 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        refKeys: ['materialDataTable', ],
        activeKey: 'materialDataTable',
        materialTable: {
          loading: false,
          dataSource: [],
          // TODO: 批号不要手动输入，但是下完领料出库之后自动选择批号并且显示出来
          columns: [
            { title: '仓库名称', key: 'depotId', width: '6%', type: FormTypes.select, placeholder: '请选择${title}', options: [],
              allowSearch:true, validateRules: [{ required: true, message: '${title}不能为空' }]
            },
            { title: '编码', key: 'barCode', width: '6%', type: FormTypes.popupJsh, kind: 'material', multi: true,
              validateRules: [{ required: true, message: '${title}不能为空' }]
            },
            { title: '名称', key: 'name', width: '8%', type: FormTypes.normal },
            { title: '型号', key: 'model', width: '10%', type: FormTypes.normal },
            { title: '客/供零件号', key: 'supplierModel', width: '12%', type: FormTypes.normal },
            { title: '类别', key: 'categoryName', width: '5%', type: FormTypes.normal },
            { title: '颜色', key: 'color', width: '5%', type: FormTypes.normal },
            { title: '颜色代码', key: 'colorCode', width: '5%', type: FormTypes.normal },
            { title: '扩展信息', key: 'materialOther', width: '5%', type: FormTypes.normal },
            { title: '库存', key: 'stock', width: '4%', type: FormTypes.normal },
            { title: '单位', key: 'unit', width: '4%', type: FormTypes.normal },
            { title: '批号', key: 'batchNumber', width: '10%', type: FormTypes.popupJsh, kind: 'batch', multi: false },
            { title: '货位', key: 'snList', width: '8%', type: FormTypes.hidden},
            { title: '货位', key: 'snListStr', width: '8%', type: FormTypes.normal},
            { title: '数量', key: 'operNumber', width: '5%', type: FormTypes.inputNumber, statistics: true,
              validateRules: [{ required: true, message: '${title}不能为空' }]
            },
            { title: '备注', key: 'remark', width: '6%', type: FormTypes.input },
            { title: '关联id', key: 'linkId', width: '5%', type: FormTypes.hidden },
          ]
        },
        confirmLoading: false,
        validatorRules:{
          operTime:{
            rules: [
              { required: true, message: '请输入单据日期！' }
            ]
          },
          linkNumber:{
            rules: [
              { required: true, message: '请选择关联生产单！' }
            ]
          }
        },
        url: {
          add: '/depotHead/addDepotHeadAndDetail',
          edit: '/depotHead/updateDepotHeadAndDetail',
          detailList: '/depotItem/getDetailList'
        }
      }
    },
    created () {
    },
    methods: {
      //调用完edit()方法之后会自动调用此方法
      editAfter() {
        this.billStatus = '0'
        this.materialTable.columns[1].type = FormTypes.popupJsh
        this.changeColumnHide()
        this.changeFormTypes(this.materialTable.columns, 'preNumber', 0)
        this.changeFormTypes(this.materialTable.columns, 'finishNumber', 0)
        // TODO: 目前 DepotHeadService 要求销售单必须有accountId，等有了新的单据类型就可以通过check了
        if (this.action === 'add') {
          this.depositStatus = false
          this.addInit(this.prefixNo)
          this.salesMan = ''
          this.fileList = []
          this.$nextTick(() => {
            handleIntroJs(this.prefixNo, 1)
          })
        } else {
          this.model.operTime = this.model.operTimeStr
          this.salesMan = this.model.salesMan
          this.fileList = this.model.fileName
          this.$nextTick(() => {
            this.form.setFieldsValue(pick(this.model, 'operTime', 'number', 'linkNumber', 'remark','salesMan'))
          });
          // 加载子表数据
          let params = {
            headerId: this.model.id,
            mpList: getMpListShort(Vue.ls.get('materialPropertyList')),  //扩展属性
            linkType: 'basic'
          }
          let url = this.readOnly ? this.url.detailList : this.url.detailList;
          this.requestSubTableData(url, params, this.materialTable);
        }
        //复制新增单据-初始化单号和日期
        if(this.action === 'copyAdd') {
          this.model.id = ''
          this.model.tenantId = ''
          this.copyAddInit(this.prefixNo)
        }
        this.initSystemConfig()
        this.initDepot()
        this.initAccount()
        this.initPerson()
      },
      //提交单据时整理成formData
      classifyIntoFormData(allValues) {
        let billMain = Object.assign(this.model, allValues.formValue)
        let detailArr = allValues.tablesValue[0].values
        billMain.type = '出库'
        billMain.subType = '领料'
        billMain.defaultNumber = billMain.number
        billMain.totalPrice = 0
        if(billMain.accountId === 0) {
          billMain.accountId = ''
        }
        billMain.accountIdList = ""
        billMain.accountMoneyList = ""
        if(this.fileList && this.fileList.length > 0) {
          billMain.fileName = this.fileList
        } else {
          billMain.fileName = ''
        }
        if(this.model.id){
          billMain.id = this.model.id
        }
        billMain.status = this.billStatus
        return {
          info: JSON.stringify(billMain),
          rows: JSON.stringify(detailArr),
        }
      },
      handleHistoryBillList() {
        let salesMan = this.form.getFieldValue('salesMan')
        this.$refs.salesManBasedHistoryBillListModalForm.show('出库', '领料', '领料人员', salesMan);
        this.$refs.salesManBasedHistoryBillListModalForm.disableSubmit = false;
      },
      onSearchLinkNumber() {
        this.$refs.productionOrderLinkList.show('其它', '生产单', '客户', "1,2,3")
        this.$refs.productionOrderLinkList.title = "选择生产单（已审核的单据才能关联）"
      },
      linkBillListOk(selectBillDetailRows, linkNumber, organId, discountMoney, deposit, remark) {
        this.orderStatusStr = ''
        this.changeFormTypes(this.materialTable.columns, 'preNumber', 1)
        this.changeFormTypes(this.materialTable.columns, 'finishNumber', 1)

        let productionMap = new Map()
        let materialQueryArr = []
        if(selectBillDetailRows && selectBillDetailRows.length>0) {
          for(let j=0; j<selectBillDetailRows.length; j++) {
            if (j>0) {
              this.orderStatusStr = this.orderStatusStr + "，"
            }
            let info = selectBillDetailRows[j];
            let toDoNumber = info.preNumber
            this.orderStatusStr = this.orderStatusStr + "[" + info.name + "]" + info.preNumber + info.unit
            if(info.finishNumber > 0) {
              toDoNumber = info.preNumber - info.finishNumber
            }
            this.orderStatusStr = this.orderStatusStr + "（还需生产" + toDoNumber + info.unit + "）"
            productionMap.set(info.barCode, toDoNumber)
          }
          // 读取所有生产单的零件barCode
          let barCodeArr = []
          let amountArr = []
          for (let [key, value] of productionMap) {
            barCodeArr.push(key)
            amountArr.push(value)
          }
          let param = {
            barCode: barCodeArr.toString(),
            amount: amountArr.toString(),
          }
          // 读取所有生产单零件的composite
          let res = getMaterialPickByBarCodeAndAmount(param).then((res) => {
            if (res && res.code === 200) {
              let listEx = []
              let list = res.data
              for (let i = 0; i < list.length; i++) {
                let info = list[i]
                info.depotId = this.defaultDepotId
                listEx.push(info)
                this.changeColumnShow(info)
              }
              this.materialTable.dataSource = listEx
              this.$nextTick(() => {
                this.form.setFieldsValue({
                  'linkNumber': linkNumber,
                  'remark': remark,
                  'orderStatus': this.orderStatusStr,
                })
              })
            } else {
              this.form.setFieldsValue({
                'linkNumber': linkNumber,
                'remark': remark,
                'orderStatus': this.orderStatusStr,
              })
            }
          })

        }
      },
    }
  }
</script>
<style scoped>

</style>