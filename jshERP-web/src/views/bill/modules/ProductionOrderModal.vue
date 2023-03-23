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
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="客户" data-step="1" data-title="客户"
              data-intro="客户必须选择，如果发现需要选择的客户尚未录入，可以在下拉框中点击新增客户进行录入。
                          特别注意，客户如果录入之后在下拉框中不显示，请检查是否给当前用户分配对应的客户权限">
              <a-select placeholder="选择客户" v-decorator="[ 'organId', validatorRules.organId ]"
                :dropdownMatchSelectWidth="false" showSearch optionFilterProp="children">
                <div slot="dropdownRender" slot-scope="menu">
                  <v-nodes :vnodes="menu" />
                  <a-divider style="margin: 4px 0;" />
                  <div v-if="isTenant" style="padding: 4px 8px; cursor: pointer;"
                       @mousedown="e => e.preventDefault()" @click="addCustomer"><a-icon type="plus" /> 新增客户</div>
                </div>
                <a-select-option v-for="(item,index) in cusList" :key="index" :value="item.id">
                  {{ item.supplier }}
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
          <a-col :lg="6" :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联生产计划" data-step="3" data-title="关联生产计划"
              data-intro="生产单可以关联生产计划，选择之后会自动加载生产计划的内容，然后继续录入开工时间、数量等信息完成单据的提交，
              提交之后原来的生产计划会对应的改变计划状态。">
              <a-input-search placeholder="请选择生产计划" v-decorator="[ 'linkNumber' ]" @search="onSearchLinkNumber" :readOnly="true"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row class="form-row" :gutter="24">
          <a-col :lg="10" :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开工时间">
              <j-date v-decorator="['planStartTime', validatorRules.planStartTime]" :show-time="true"/>
            </a-form-item>
          </a-col>
          <a-col :lg="10" :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="完工时间">
              <j-date v-decorator="['planFinishTime', validatorRules.planFinishTime]" :show-time="true"/>
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
          :rowSelection="rowCanEdit"
          :actionButton="rowCanEdit"
          :dragSort="rowCanEdit"
          @valueChange="onValueChange"
          @added="onAdded"
          @deleted="onDeleted">
          <template #buttonAfter>
            <a-row v-if="rowCanEdit" :gutter="24" style="float:left;padding-bottom: 5px;" data-step="4" data-title="扫码录入" data-intro="此功能支持扫码枪扫描商品条码进行录入">
              <a-col v-if="scanStatus" :md="6" :sm="24">
                <a-button @click="scanEnter" style="margin-right: 8px">扫码录入</a-button>
              </a-col>
              <a-col v-if="!scanStatus" :md="16" :sm="24" style="padding: 0 6px 0 12px">
                <a-input placeholder="请扫码商品条码并回车" v-model="scanBarCode" @pressEnter="scanPressEnter" ref="scanBarCode"/>
              </a-col>
              <a-col v-if="!scanStatus" :md="6" :sm="24" style="padding: 0px 12px 0 0">
                <a-button @click="stopScan" style="margin-right: 8px">收起扫码</a-button>
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
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="附件" data-step="11" data-title="附件"
                         data-intro="可以上传与单据相关的图片、文档，支持多个文件">
              <j-upload v-model="fileList" bizPath="bill"></j-upload>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
    <many-account-modal ref="manyAccountModalForm" @ok="manyAccountModalFormOk"></many-account-modal>
    <link-bill-list ref="linkBillList" @ok="linkBillListOk"></link-bill-list>
    <customer-modal ref="customerModalForm" @ok="customerModalFormOk"></customer-modal>
    <account-modal ref="accountModalForm" @ok="accountModalFormOk"></account-modal>
    <history-bill-list ref="historyBillListModalForm"></history-bill-list>
  </j-modal>
</template>

<script>
  import pick from 'lodash.pick'
  import ManyAccountModal from '../dialog/ManyAccountModal'
  import LinkBillList from '../dialog/LinkBillList'
  import CustomerModal from '../../system/modules/CustomerModal'
  import AccountModal from '../../system/modules/AccountModal'
  import HistoryBillList from '../dialog/HistoryBillList'
  import { FormTypes } from '@/utils/JEditableTableUtil'
  import { JEditableTableMixin } from '@/mixins/JEditableTableMixin'
  import { BillModalMixin } from '../mixins/BillModalMixin'
  import { getMpListShort, changeListFmtMinus,handleIntroJs } from "@/utils/util"
  import { getAction } from '@/api/manage'
  import JUpload from '@/components/jeecg/JUpload'
  import JDate from '@/components/jeecg/JDate'
  import Vue from 'vue'
  export default {
    name: "ProductionOrderModal",
    mixins: [JEditableTableMixin, BillModalMixin],
    components: {
      ManyAccountModal,
      LinkBillList,
      CustomerModal,
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
        operTimeStr: '',
        planStartTimeStr: '',
        planFinishTimeStr: '',
        prefixNo: 'SCD',
        depositStatus: false,
        fileList:[],
        rowCanEdit: true,
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
          columns: [
            { title: '条码', key: 'barCode', width: '10%', type: FormTypes.popupJsh, kind: 'material', multi: true,
              validateRules: [{ required: true, message: '${title}不能为空' }]
            },
            { title: '名称', key: 'name', width: '8%', type: FormTypes.normal },
            { title: '内部零件号', key: 'internalId', width: '7%', type: FormTypes.normal },
            { title: '客户零件号', key: 'model', width: '7%', type: FormTypes.normal },
            { title: '颜色编码', key: 'color', width: '5%', type: FormTypes.normal },
            { title: '扩展信息', key: 'materialOther', width: '5%', type: FormTypes.normal },
            { title: '库存', key: 'stock', width: '5%', type: FormTypes.normal },
            { title: '单位', key: 'unit', width: '4%', type: FormTypes.normal },
            { title: '原计划', key: 'preNumber', width: '4%', type: FormTypes.normal },
            { title: '已生产', key: 'finishNumber', width: '4%', type: FormTypes.normal },
            { title: '数量', key: 'operNumber', width: '4%', type: FormTypes.inputNumber, statistics: true,
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
          organId:{
            rules: [
              { required: true, message: '请选择客户！' }
            ]
          },
          planStartTime:{
            rules: [
              { required: true, message: '请输入开工时间!' }
            ]
          },
          planFinishTime:{
            rules: [
              { required: true, message: '请输入完工时间!' }
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
        this.rowCanEdit = true
        this.materialTable.columns[1].type = FormTypes.popupJsh
        this.changeColumnHide()
        this.changeFormTypes(this.materialTable.columns, 'preNumber', 0)
        this.changeFormTypes(this.materialTable.columns, 'finishNumber', 0)
        if (this.action === 'add') {
          this.depositStatus = false
          this.addInit(this.prefixNo)
          this.fileList = []
          this.$nextTick(() => {
            handleIntroJs(this.prefixNo, 1)
          })
        } else {
          if(this.model.linkNumber) {
            this.rowCanEdit = false
            this.materialTable.columns[1].type = FormTypes.normal
          }
          this.model.operTime = this.model.operTimeStr
          this.fileList = this.model.fileName
          this.$nextTick(() => {
            this.form.setFieldsValue(pick(this.model,'organId', 'operTime',
            'planStartTime', 'planFinishTime', 'number', 'linkNumber', 'remark'))
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
        this.initCustomer()
        this.initAccount()
      },
      //提交单据时整理成formData
      classifyIntoFormData(allValues) {
        let billMain = Object.assign(this.model, allValues.formValue)
        let detailArr = allValues.tablesValue[0].values
        billMain.type = '其它'
        billMain.subType = '生产单'
        billMain.defaultNumber = billMain.number
        billMain.totalPrice = 0
        billMain.changeAmount = 0
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
        let organId = this.form.getFieldValue('organId')
        this.$refs.historyBillListModalForm.show('其它', '生产单', '客户', organId);
        this.$refs.historyBillListModalForm.disableSubmit = false;
      },
      onSearchLinkNumber() {
        this.$refs.linkBillList.show('其它', '生产计划', '客户', "1,3")
        this.$refs.linkBillList.title = "选择生产计划"
      },
      linkBillListOk(selectBillDetailRows, linkNumber, organId, discountMoney, deposit, remark) {
        this.rowCanEdit = false
        this.materialTable.columns[1].type = FormTypes.normal
        this.changeFormTypes(this.materialTable.columns, 'preNumber', 1)
        this.changeFormTypes(this.materialTable.columns, 'finishNumber', 1)
        if(selectBillDetailRows && selectBillDetailRows.length>0) {
          let listEx = []
          for(let j=0; j<selectBillDetailRows.length; j++) {
            let info = selectBillDetailRows[j];
            if(info.finishNumber>0) {
              info.operNumber = info.preNumber - info.finishNumber
            }
            info.linkId = info.id
            listEx.push(info)
            this.changeColumnShow(info)
          }
          this.materialTable.dataSource = listEx
          this.$nextTick(() => {
            this.form.setFieldsValue({
              'organId': organId,
              'linkNumber': linkNumber,
              'remark': remark
            })
          })
        }
      },
    }
  }
</script>
<style scoped>

</style>