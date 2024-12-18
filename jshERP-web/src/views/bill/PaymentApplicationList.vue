<!-- by 75 27 18 920 -->
<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :style="cardStyle" :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
          <!-- 搜索区域 -->
          <a-form layout="inline" @keyup.enter.native="searchQuery">
            <a-row :gutter="24">
              <a-col :md="6" :sm="24">
                <a-form-item label="单据编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="请输入单据编号" v-model="queryParam.billNo"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="单据日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-range-picker
                    style="width:100%"
                    v-model="queryParam.createTimeRange"
                    format="YYYY-MM-DD"
                    :placeholder="['开始时间', '结束时间']"
                    @change="onDateChange"
                    @ok="onDateOk"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="供应商" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-select placeholder="选择供应商" showSearch optionFilterProp="children" v-model="queryParam.organId">
                    <a-select-option v-for="(item,index) in supList" :key="index" :value="item.id">
                      {{ item.supplier }}
                    </a-select-option>
                  </a-select>
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
                  <a-form-item label="财务人员" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select placeholder="选择财务人员" showSearch optionFilterProp="children" v-model="queryParam.handsPersonId">
                      <a-select-option v-for="(item,index) in personList" :key="index" :value="item.id">
                        {{ item.name }}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="制单人" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select placeholder="选择制单人" showSearch optionFilterProp="children" v-model="queryParam.creator">
                      <a-select-option v-for="(item,index) in userList" :key="index" :value="item.id">
                        {{ item.userName }}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="付款账户" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select placeholder="选择付款账户" showSearch optionFilterProp="children" v-model="queryParam.accountId">
                      <a-select-option v-for="(item,index) in accountList" :key="index" :value="item.id">
                        {{ item.name }}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="单据状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select placeholder="选择单据状态" v-model="queryParam.status">
                      <a-select-option value="0">未审核</a-select-option>
                      <a-select-option value="1">已审核</a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="单据备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="请输入单据备注" v-model="queryParam.remark"></a-input>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="采购单号" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="请输入采购单号" v-model="queryParam.number"></a-input>
                  </a-form-item>
                </a-col>
              </template>
            </a-row>
          </a-form>
        </div>
        <!-- 操作按钮区域 -->
        <div class="table-operator"  style="margin-top: 5px">
          <a-button v-if="btnEnableList.indexOf(1)>-1" @click="myHandleAdd" type="primary" icon="plus">新增</a-button>
          <a-dropdown>
            <a-menu slot="overlay">
              <a-menu-item key="1" v-if="btnEnableList.indexOf(1)>-1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
              <a-menu-item key="2" v-if="checkFlag && btnEnableList.indexOf(2)>-1" @click="batchSetStatus(1)"><a-icon type="check"/>审核</a-menu-item>
              <a-menu-item key="3" v-if="checkFlag && btnEnableList.indexOf(7)>-1" @click="batchSetStatus(0)"><a-icon type="stop"/>反审核</a-menu-item>
            </a-menu>
            <a-button>
              批量操作 <a-icon type="down" />
            </a-button>
          </a-dropdown>
          <a-tooltip placement="left" title="申请与采购相关的款项。" slot="action">
            <a-icon v-if="btnEnableList.indexOf(1)>-1" type="question-circle" style="font-size:20px;float:right;" />
          </a-tooltip>
        </div>
        <!-- table区域-begin -->
        <div>
          <a-table
            ref="table"
            size="middle"
            bordered
            rowKey="id"
            :columns="columns"
            :dataSource="dataSource"
            :components="handleDrag(columns)"
            :pagination="ipagination"
            :scroll="scroll"
            :loading="loading"
            :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
            @change="handleTableChange">
            <span slot="action" slot-scope="text, record">
              <a @click="myHandleDetail(record, record.type, prefixNo)">查看</a>
              <a-divider v-if="btnEnableList.indexOf(1)>-1" type="vertical" />
              <a v-if="btnEnableList.indexOf(1)>-1" @click="myHandleEdit(record)">编辑</a>
              <a-divider v-if="btnEnableList.indexOf(1)>-1" type="vertical" />
              <a-popconfirm v-if="btnEnableList.indexOf(1)>-1" title="确定删除吗?" @confirm="() => myHandleDelete(record)">
                <a>删除</a>
              </a-popconfirm>
            </span>
            <span slot="numberCustomRender" slot-scope="text, record">
              <a @click="myHandleBillDetail(record)">详情</a>
            </span>
            <template slot="customRenderStatus" slot-scope="status">
              <a-tag v-if="status == '0'" color="red">未审核</a-tag>
              <a-tag v-if="status == '1'" color="green">已审核</a-tag>
              <a-tag v-if="status == '9'" color="orange">审核中</a-tag>
            </template>
          </a-table>
        </div>
        <!-- table区域-end -->
        <!-- 表单区域 -->
        <payment-application-modal ref="modalForm" @ok="modalFormOk" @close="modalFormClose"></payment-application-modal>
        <financial-detail ref="modalDetail" @ok="modalFormOk" @close="modalFormClose"></financial-detail>
        <bill-detail ref="billDetail"></bill-detail>
      </a-card>
    </a-col>
  </a-row>
</template>
<script>
  import PaymentApplicationModal from './modules/PaymentApplicationModal'
  import BillDetail from '../bill/dialog/BillDetail'
  import FinancialDetail from '../financial/dialog/FinancialDetail'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { FinancialListMixin } from '../financial/mixins/FinancialListMixin'
  import { findBillDetailByNumber, findFinancialDetailByNumber, getAccountItemList } from '@/api/api'
  import JDate from '@/components/jeecg/JDate'
  import Vue from 'vue'
  export default {
    name: "PaymentApplicationList",
    mixins:[JeecgListMixin, FinancialListMixin],
    components: {
      PaymentApplicationModal,
      FinancialDetail,
      BillDetail,
      JDate
    },
    data () {
      return {
        labelCol: {
          span: 5
        },
        wrapperCol: {
          span: 18,
          offset: 1
        },
        // 查询条件
        queryParam: {
          billNo: "",
          searchMaterial: "",
          type: "供应商对账",
          organId: "",
          creator: "",
          handsPersonId: "",
          accountId: "",
          status: "",
          remark: "",
          number: "",
          roleType: Vue.ls.get('roleType')
        },
        prefixNo: 'FK',
        // 表头
        columns: [
          {
            title: '操作',
            dataIndex: 'action',
            width:120,
            align:"center",
            scopedSlots: { customRender: 'action' },
          },
          { title: '种类', dataIndex: 'type',width:100, ellipsis:true },
          { title: '供应商', dataIndex: 'organName',width:140, ellipsis:true},
          { title: '财务人员', dataIndex: 'handsPersonName',width:80},
          { title: '单据编号', dataIndex: 'billNo',width:130},
          { title: '单据日期 ', dataIndex: 'billTimeStr',width:160},
          { title: '采购订单', dataIndex: 'cgdd',width:70,
            scopedSlots: { customRender: 'numberCustomRender' },
          },
          { title: '制单人', dataIndex: 'userName',width:80, ellipsis:true},
          { title: '申请金额', dataIndex: 'totalPrice',width:80},
          { title: '实际金额', dataIndex: 'changeAmount',width:80},
          { title: '备注', dataIndex: 'remark',width:200},
          { title: '状态', dataIndex: 'status', width: 80, align: "center",
            scopedSlots: { customRender: 'customRenderStatus' }
          }
        ],
        url: {
          list: "/accountHead/list",
          delete: "/accountHead/delete",
          deleteBatch: "/accountHead/deleteBatch",
          batchSetStatusUrl: "/accountHead/batchSetStatus"
        }
      }
    },
    computed: {
    },
    created () {
      this.initSystemConfig()
      this.initSupplier()
      this.initUser()
      this.initPerson()
      this.initAccount()
    },
    methods: {
      myHandleBillDetail(record) {
        findFinancialDetailByNumber({ billNo: record.billNo }).then((res) => {
          if (res && res.code === 200) {
            let header = res.data
            getAccountItemList({ headerId: header.id }).then((res2) => {
              if (res2 && res2.code === 200) {
                let item = res2.data.rows[0]
                let that = this
                findBillDetailByNumber({ number: item.billNumber }).then((res3) => {
                  if (res3 && res3.code === 200) {
                    let type = res3.data.type === "其它"? "":res3.data.type
                    this.$refs.billDetail.isCanBackCheck = false
                    that.$refs.billDetail.show(res3.data, res3.data.subType + type);
                    that.$refs.billDetail.title="详情";
                  }
                })
              }
            })
          }
        })
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>