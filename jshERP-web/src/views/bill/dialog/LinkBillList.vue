<template>
  <div ref="container">
    <a-modal
      :title="title"
      :width="1600"
      :visible="visible"
      :getContainer="() => $refs.container"
      :maskStyle="{'top':'93px','left':'154px'}"
      :wrapClassName="wrapClassNameInfo()"
      :mask="isDesktop()"
      :maskClosable="false"
      @ok="handleOk"
      @cancel="handleCancel"
      cancelText="关闭"
      style="top:20px;height: 95%;">
      <!-- 查询区域 -->
      <div class="table-page-search-wrapper" v-if="selectType === 'list'">
        <!-- 搜索区域 -->
        <a-form layout="inline" @keyup.enter.native="searchQuery">
          <a-row :gutter="24">
            <a-col :md="6" :sm="24">
              <a-form-item label="单据编号" :labelCol="{span: 5}" :wrapperCol="{span: 18, offset: 1}">
                <a-input placeholder="请输入单据编号查询" v-model="queryParam.number"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="产品信息" :labelCol="{span: 5}" :wrapperCol="{span: 18, offset: 1}">
                <a-input placeholder="编码/名称/型号/规格" v-model="queryParam.materialParam"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="单据日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-range-picker
                  style="width: 100%"
                  v-model="queryParam.createTimeRange"
                  format="YYYY-MM-DD"
                  :placeholder="['开始时间', '结束时间']"
                  @change="onDateChange"
                  @ok="onDateOk"
                />
              </a-form-item>
            </a-col>
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-col :md="6" :sm="24">
                <a-button type="primary" @click="searchQuery">查询</a-button>
                <a-button style="margin-left: 8px" @click="searchReset">重置</a-button>
                <a @click="handleToggleSearch" style="margin-left: 8px">
                  {{ toggleSearchStatus ? '收起' : '展开' }}
                  <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
                </a>
              </a-col>
            </span>
            <template v-if="toggleSearchStatus">
              <a-col :md="6" :sm="24">
                <a-form-item label="单据备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="请输入单据备注" v-model="queryParam.remark"></a-input>
                </a-form-item>
              </a-col>
            </template>
            <template v-if="toggleSearchStatus">
              <a-col :md="6" :sm="24">
                <a-form-item label="单据状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-select placeholder="选择单据状态" v-model="queryParam.status">
                    <a-select-option value="0">未审核</a-select-option>
                    <a-select-option value="1">已审核</a-select-option>
                    <a-select-option v-if="queryParam.subType === '采购订单'" value="3">部分到货</a-select-option>
                    <a-select-option v-if="queryParam.subType === '采购申请'" value="3">部分下单</a-select-option>
                    <a-select-option v-if="queryParam.subType === '销售订单'" value="3">部分出库</a-select-option>
                    <a-select-option v-if="queryParam.subType === '生产计划'" value="3">部分下单</a-select-option>
                    <a-select-option v-if="queryParam.subType === '生产单'" value="3">部分入库</a-select-option>
                    <a-select-option v-if="queryParam.subType === '领料'" value="3">有退料</a-select-option>
                    <a-select-option v-if="queryParam.subType === '采购订单'" value="2">已到货</a-select-option>
                    <a-select-option v-if="queryParam.subType === '采购申请'" value="2">已下单</a-select-option>
                    <a-select-option v-if="queryParam.subType === '销售订单'" value="2">已出库</a-select-option>
                    <a-select-option v-if="queryParam.subType === '生产计划'" value="2">已下单</a-select-option>
                    <a-select-option v-if="queryParam.subType === '生产单'" value="2">已入库</a-select-option>
                    <a-select-option v-if="queryParam.subType === '领料'" value="2">有退料</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </template>
          </a-row>
        </a-form>
      </div>
      <!-- table区域-begin -->
      <a-table v-if="selectType === 'list'"
        bordered
        ref="table"
        size="middle"
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange, type: getType}"
        :customRow="rowAction"
        @change="handleTableChange">
        <span slot="numberCustomRender" slot-scope="text, record">
          <a v-if="!queryParam.purchaseStatus" @click="myHandleDetail(record)">{{record.number}}</a>
          <span v-if="queryParam.purchaseStatus">{{record.number}}</span>
        </span>
        <template slot="customRenderStatus" slot-scope="text, record">
          <template v-if="!queryParam.purchaseStatus">
          <!-- 领料出库只需要两个状态：有退料、无退料 -->
            <a-tag v-if="record.status === '0'" color="red">未审核</a-tag>
            <a-tag v-if="record.status === '1'" color="green">已审核</a-tag>
            <a-tag v-if="record.status === '2' && queryParam.subType === '采购订单'" color="cyan">已入库</a-tag>
            <a-tag v-if="record.status === '2' && queryParam.subType === '采购申请'" color="cyan">已下单</a-tag>
            <a-tag v-if="record.status === '2' && queryParam.subType === '销售订单'" color="cyan">已出库</a-tag>
            <a-tag v-if="record.status === '2' && queryParam.subType === '生产计划'" color="cyan">已下单</a-tag>
            <a-tag v-if="record.status === '2' && queryParam.subType === '生产单'" color="cyan">已入库</a-tag>
            <a-tag v-if="record.status === '2' && queryParam.subType === '领料'" color="cyan">有退料</a-tag>
            <a-tag v-if="record.status === '3' && queryParam.subType === '采购申请'" color="blue">部分下单</a-tag>
            <a-tag v-if="record.status === '3' && queryParam.subType === '采购订单'" color="blue">部分入库</a-tag>
            <a-tag v-if="record.status === '3' && queryParam.subType === '销售订单'" color="blue">部分出库</a-tag>
            <a-tag v-if="record.status === '3' && queryParam.subType === '生产计划'" color="blue">部分下单</a-tag>
            <a-tag v-if="record.status === '3' && queryParam.subType === '生产单'" color="blue">部分入库</a-tag>
            <a-tag v-if="record.status === '3' && queryParam.subType === '领料'" color="cyan">有退料</a-tag>
          </template>
          <!-- 销售转采购 -->
          <template v-if="queryParam.purchaseStatus">
            <a-tag v-if="record.purchaseStatus === '0'" color="red">未采购</a-tag>
            <a-tag v-if="record.purchaseStatus === '2' && queryParam.subType === '销售订单'" color="cyan">完成采购</a-tag>
            <a-tag v-if="record.purchaseStatus === '3' && queryParam.subType === '销售订单'" color="blue">部分采购</a-tag>
          </template>
        </template>
      </a-table>
      <a-table v-if="selectType === 'detail'"
        bordered
        ref="table"
        size="middle"
        rowKey="id"
        :pagination="false"
        :columns="columnsDetail"
        :dataSource="dataSourceDetail"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedDetailRowKeys, onChange: onSelectDetailChange, type: 'checkbox'}"
        @change="handleTableChange">
      </a-table>
      <!-- table区域-end -->
      <!-- 表单区域 -->
      <bill-detail ref="billDetail"></bill-detail>
    </a-modal>
  </div>
</template>

<script>
  import BillDetail from './BillDetail'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import {mixinDevice} from '@/utils/mixin'
  import { findBillDetailByNumber } from '@/api/api'
  import { getAction } from '@/api/manage'
  import Vue from 'vue'
  export default {
    name: 'LinkBillList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      BillDetail
    },
    data () {
      return {
        title: "操作",
        visible: false,
        disableMixinCreated: true,
        selectedRowKeys: [],
        selectedDetailRowKeys: [],
        selectBillRows: [],
        selectBillDetailRows: [],
        showType: 'basic',
        selectType: 'list',
        linkNumber: '',
        organId: '',
        payType: '',
        discountMoney: '',
        deposit: '',
        remark: '',
        queryParam: {
          number: "",
          materialParam: "",
          type: "",
          subType: "",
          status: "",
          remark: ""
        },
        labelCol: {
          xs: { span: 24 },
          sm: { span: 8 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        // 表头
        columns: [
          { title: '', dataIndex: 'organName',width:120, ellipsis:true},
          { title: '单据编号', dataIndex: 'number',width:120,
            scopedSlots: { customRender: 'numberCustomRender' },
          },
          { title: '合同编号', dataIndex: 'payType',width:120, ellipsis:true},
          { title: '产品信息', dataIndex: 'materialsList',width:280, ellipsis:true,
            customRender:function (text,record,index) {
              if(text) {
                return text.replace(",","，");
              }
            }
          },
          { title: '单据日期', dataIndex: 'operTimeStr',width:120},
          { title: '申请人', dataIndex: 'salesManStr',width:60},
          { title: '制单人', dataIndex: 'userName',width:60},
          { title: '数量', dataIndex: 'materialCount',width:60},
          { title: '状态', dataIndex: 'status', width: 70, align: "center",
            scopedSlots: { customRender: 'customRenderStatus' }
          }
        ],
        columnsDetail: [
          { title: '编码', dataIndex: 'barCode',width:50},
          { title: '名称', dataIndex: 'name',width:100, ellipsis:true},
          { title: '客/供型号', dataIndex: 'supplierModel',width:100, ellipsis:true},
          { title: '零件号', dataIndex: 'model',width:100, ellipsis:true},
          { title: '扩展信息', dataIndex: 'materialOther',width:50, ellipsis:true},
          { title: '数量', dataIndex: 'operNumber',width:50},
          { title: '已完成/已退货', dataIndex: 'finishNumber',width:60},
          { title: '单位', dataIndex: 'unit',width:40},
          { title: '到货日期', dataIndex: 'expirationDate',width:50},
          { title: '收货地', dataIndex: 'anotherDepotName',width:60},
          { title: '备注', dataIndex: 'remark',width:100, ellipsis:true},
        ],
        dataSource:[],
        dataSourceDetail: [],
        url: {
          list: "/depotHead/list"
        }
      }
    },
    computed: {
      getType: function () {
        return 'radio';
      }
    },
    created() {
    },
    methods: {
      show(type, subType, organType, status) {
        this.selectType = 'list'
        this.showType = 'basic'
        this.queryParam.type = type
        this.queryParam.subType = subType
        this.queryParam.roleType = Vue.ls.get('roleType')
        this.queryParam.status = status
        if(subType === '采购申请') {
          this.columns[0].width = 0
        } else {
          this.columns[0].title = organType
        }
        if(subType !== '采购订单') {
          this.columns[2].width = 0
        }
        this.model = Object.assign({}, {});
        this.visible = true;
        this.loadData(1)
      },
      purchaseShow(type, subType, organType, status, purchaseStatus) {
        this.selectType = 'list'
        this.showType = 'purchase'
        this.queryParam.type = type
        this.queryParam.subType = subType
        this.queryParam.roleType = '全部数据'
        this.queryParam.status = status
        this.queryParam.purchaseStatus = purchaseStatus
        if(subType === '采购申请') {
          this.columns[0].width = 0
        } else {
          this.columns[0].title = organType
        }
        if(subType !== '采购订单') {
          this.columns[2].width = 0
        }
        this.model = Object.assign({}, {});
        this.visible = true;
        this.loadData(1)
      },
      myHandleDetail(record) {
        findBillDetailByNumber({ number: record.number }).then((res) => {
          if (res && res.code === 200) {
            let type = res.data.depotHeadType
            type = type.replace('其它','')
            this.$refs.billDetail.show(res.data, type)
            this.$refs.billDetail.title=type+"-详情"
          }
        })
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleCancel () {
        this.close()
      },
      onSelectChange(selectedRowKeys) {
        this.selectedRowKeys = selectedRowKeys;
      },
      onSelectDetailChange(selectedRowKeys) {
        this.selectedDetailRowKeys = selectedRowKeys;
      },
      handleOk () {
        if(this.selectType === 'list') {
          this.getSelectBillRows();
          this.selectType = 'detail'
          this.title = "选择单据明细"
          if(this.selectBillRows && this.selectBillRows.length>0) {
            let record = this.selectBillRows[0]
            this.linkNumber = record.number
            this.payType = record.payType
            this.organId = record.organId
            this.discountMoney = record.discountMoney
            this.deposit = record.changeAmount - record.finishDeposit
            this.remark = record.remark
            this.loadDetailData(1)
          }
        } else {
          if(this.selectedDetailRowKeys.length) {
            this.getSelectBillDetailRows()
            this.$emit('ok', this.selectBillDetailRows, this.linkNumber, this.payType, this.organId, this.discountMoney, this.deposit, this.remark)
            this.close()
          } else {
            this.$message.warning('抱歉，请选择单据明细！')
          }
        }
      },
      //查询明细列表
      loadDetailData(arg) {
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        if(this.selectBillRows && this.selectBillRows.length>0) {
          let record = this.selectBillRows[0]
          let param = {
            headerId: record.id,
            mpList : '',
            linkType: this.showType
          }
          this.loading = true;
          getAction('/depotItem/getDetailList', param).then((res) => {
            if (res.code===200) {
              let list = res.data.rows;
              let listEx = []
              for(let j=0; j<list.length; j++){
                let info = list[j];
                //if(info.preNumber !== info.finishNumber) {
                  //去掉已经全部转换的明细
                  //listEx.push(info)
                //} else {
                  //if(this.queryParam.subType === '采购' || this.queryParam.subType === '销售' || this.queryParam.subType === '零售') {
                    //针对退货单，不过滤明细
                    //listEx.push(info)
                  //}
                //}
                listEx.push(info)
              }
              this.dataSourceDetail = listEx
              for (let i = 0, len = this.dataSourceDetail.length; i < len; i++) {
                this.selectedDetailRowKeys.push(this.dataSourceDetail[i].id);
                this.selectBillDetailRows.push(this.dataSourceDetail[i]);
              }
              this.ipagination.total = res.data.total;
            }
            if(res.code===510){
              this.$message.warning(res.data)
            }
            this.loading = false;
          })
        }
      },
      onDateChange: function (value, dateString) {
        this.queryParam.beginTime=dateString[0];
        this.queryParam.endTime=dateString[1];
      },
      onDateOk(value) {
        console.log(value);
      },
      searchReset() {
        this.queryParam = {
          type: this.queryParam.type,
          subType: this.queryParam.subType,
          status: "1,3"
        }
        this.loadData(1);
      },
      getSelectBillRows() {
        let dataSource = this.dataSource;
        this.selectBillRows = [];
        for (let i = 0, len = dataSource.length; i < len; i++) {
          if (this.selectedRowKeys.includes(dataSource[i].id)) {
            this.selectBillRows.push(dataSource[i]);
          }
        }
      },
      getSelectBillDetailRows() {
        let dataSource = this.dataSourceDetail;
        this.selectBillDetailRows = [];
        for (let i = 0, len = dataSource.length; i < len; i++) {
          if (this.selectedDetailRowKeys.includes(dataSource[i].id)) {
            this.selectBillDetailRows.push(dataSource[i]);
          }
        }
      },
      rowAction(record, index) {
        return {
          on: {
            click: () => {
              let arr = []
              arr.push(record.id)
              this.selectedRowKeys = arr
            },
            dblclick: () => {
              let arr = []
              arr.push(record.id)
              this.selectedRowKeys = arr
              this.handleOk()
            }
          }
        }
      }
    }
  }
</script>

<style scoped>

</style>