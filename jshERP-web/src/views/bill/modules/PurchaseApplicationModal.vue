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
      <a-button type="primary" @click="handleOk">保存</a-button>
    </template>
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row class="form-row" :gutter="24">
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="申请人">
              <a-select placeholder="选择申请人" v-decorator="[ 'salesMan' ]"
                :dropdownMatchSelectWidth="false" showSearch optionFilterProp="children">
                <a-select-option v-for="(item,index) in personList.options" :key="index" :value="item.value">
                  {{ item.text }}
                </a-select-option>
              </a-select>
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
            <a-row :gutter="24" style="float:left;padding-bottom: 5px;" data-step="4" data-title="扫码录入" data-intro="此功能支持扫码枪扫描产品编码进行录入">
              <a-col v-if="scanStatus" :md="6" :sm="24">
                <a-button @click="scanEnter">扫码录入</a-button>
              </a-col>
              <a-col v-if="!scanStatus" :md="16" :sm="24" style="padding: 0 8px 0 12px">
                <a-input placeholder="请扫码产品编码并回车" v-model="scanBarCode" @pressEnter="scanPressEnter" ref="scanBarCode"/>
              </a-col>
              <a-col v-if="!scanStatus" :md="6" :sm="24" style="padding: 0px 12px 0 0">
                <a-button @click="stopScan">收起扫码</a-button>
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
              <a-textarea :rows="1" placeholder="请输入备注" v-decorator="[ 'remark' ]" style="margin-top:8px;"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="24">
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="附件" data-step="10" data-title="附件" data-intro="可以上传与单据相关的图片、文档，支持多个文件">
              <j-upload v-model="fileList" bizPath="bill"></j-upload>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
    <many-account-modal ref="manyAccountModalForm" @ok="manyAccountModalFormOk"></many-account-modal>
    <vendor-modal ref="vendorModalForm" @ok="vendorModalFormOk"></vendor-modal>
    <account-modal ref="accountModalForm" @ok="accountModalFormOk"></account-modal>
    <history-bill-list ref="historyBillListModalForm"></history-bill-list>
  </j-modal>
</template>
<script>
  import pick from 'lodash.pick'
  import ManyAccountModal from '../dialog/ManyAccountModal'
  import VendorModal from '../../system/modules/VendorModal'
  import AccountModal from '../../system/modules/AccountModal'
  import HistoryBillList from '../dialog/HistoryBillList'
  import { FormTypes } from '@/utils/JEditableTableUtil'
  import { JEditableTableMixin } from '@/mixins/JEditableTableMixin'
  import { BillModalMixin } from '../mixins/BillModalMixin'
  import { getCurrentSystemConfig } from '@/api/api'
  import { getMpListShort, changeListFmtMinus,handleIntroJs } from "@/utils/util"
  import JUpload from '@/components/jeecg/JUpload'
  import JDate from '@/components/jeecg/JDate'
  import Vue from 'vue'
  export default {
    name: "PurchaseApplicationModal",
    mixins: [JEditableTableMixin,BillModalMixin],
    components: {
      ManyAccountModal,
      VendorModal,
      AccountModal,
      HistoryBillList,
      JUpload,
      JDate,
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
        supList: [],
        depotList: [],
        operTimeStr: '',
        prefixNo: 'CGSQ',
        fileList:[],
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
        tableKeys:['materialDataTable', ],
        activeKey: 'materialDataTable',
        materialTable: {
          loading: false,
          dataSource: [],
          columns: [
            { title: '编码', key: 'barCode', width: '10%', type: FormTypes.popupJsh, kind: 'material', multi: true,
              validateRules: [{ required: true, message: '${title}不能为空' }]
            },
            { title: '名称', key: 'name', width: '8%', type: FormTypes.normal },
            { title: '型号', key: 'internalId', width: '7%', type: FormTypes.normal },
            { title: '规格', key: 'model', width: '7%', type: FormTypes.normal },
            { title: '类别', key: 'categoryName', width: '5%', type: FormTypes.normal },
            { title: '颜色', key: 'color', width: '5%', type: FormTypes.normal },
            { title: '扩展信息', key: 'materialOther', width: '5%', type: FormTypes.normal },
            { title: '库存', key: 'stock', width: '5%', type: FormTypes.normal },
            { title: '单位', key: 'unit', width: '4%', type: FormTypes.normal },
            { title: '数量', key: 'operNumber', width: '5%', type: FormTypes.inputNumber, statistics: true,
              validateRules: [{ required: true, message: '${title}不能为空' }]
            },
            { title: '备注', key: 'remark', width: '6%', type: FormTypes.input},
          ]
        },
        confirmLoading: false,
        validatorRules:{
          operTime:{
            rules: [
              { required: true, message: '请输入单据日期!' }
            ]
          },
          organId:{
            rules: [
              { required: true, message: '请选择供应商!' }
            ]
          },
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
        this.materialTable.columns[0].type = FormTypes.popupJsh
        this.changeColumnHide()
        if (this.action === 'add') {
          this.addInit(this.prefixNo)
          this.fileList = []
          this.salesMan = ''
          this.$nextTick(() => {
            handleIntroJs(this.prefixNo, 1)
          })
        } else {
          this.model.operTime = this.model.operTimeStr
          this.salesMan = this.model.salesMan
          if(this.model.accountId == null && this.model.accountIdList) {
            this.model.accountId = 0
            this.manyAccountBtnStatus = true
            this.accountIdList = this.model.accountIdList
            this.accountMoneyList = this.model.accountMoneyList
          } else {
            this.manyAccountBtnStatus = false
          }
          this.fileList = this.model.fileName
          this.$nextTick(() => {
            this.form.setFieldsValue(pick(this.model,'organId', 'operTime', 'number', 'remark',
            'discountLastMoney','accountId','salesMan'))
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
        this.initSupplier()
        this.initAccount()
      },
      /** 整理成formData */
      classifyIntoFormData(allValues) {
        let totalPrice = 0
        let billMain = Object.assign(this.model, allValues.formValue)
        let detailArr = allValues.tablesValue[0].values
        billMain.type = '其它'
        billMain.subType = '采购申请'
        billMain.defaultNumber = billMain.number
        for(let item of detailArr){
          item.depotId = '' //订单不需要仓库
          totalPrice += item.allPrice-0
        }
        billMain.totalPrice = 0-totalPrice
        if(billMain.accountId === 0) {
          billMain.accountId = ''
        }
        this.accountMoneyList = changeListFmtMinus(this.accountMoneyList)
        billMain.accountIdList = this.accountIdList.length>0 ? JSON.stringify(this.accountIdList) : ""
        billMain.accountMoneyList = this.accountMoneyList.length>0 ? JSON.stringify(this.accountMoneyList) : ""
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
        let organId = this.form.getFieldValue('organId')
        this.$refs.historyBillListModalForm.show('其它', '采购申请', '供应商', organId);
        this.$refs.historyBillListModalForm.disableSubmit = false;
      },
    }
  }
</script>
<style scoped>

</style>