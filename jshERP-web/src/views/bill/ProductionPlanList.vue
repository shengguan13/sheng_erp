<!-- create jishenghua-->
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
                  <a-input placeholder="请输入单据编号" v-model="queryParam.number"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="产品信息" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="请输入编码、名称、型号、规格、颜色、扩展信息" v-model="queryParam.materialParam"></a-input>
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
                  <a-form-item label="客户" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select placeholder="选择客户" showSearch optionFilterProp="children" v-model="queryParam.organId">
                      <a-select-option v-for="(item,index) in cusList" :key="index" :value="item.id">
                        {{ item.supplier }}
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
                  <a-form-item label="单据状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select placeholder="选择单据状态" v-model="queryParam.status">
                      <a-select-option value="0">未审核</a-select-option>
                      <a-select-option value="1">已审核</a-select-option>
                      <a-select-option value="3">部分送货</a-select-option>
                      <a-select-option value="2">完成送货</a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="单据备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="请输入单据备注" v-model="queryParam.remark"></a-input>
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
              <a @click="myHandleDetail(record, '生产计划', prefixNo, true)">查看</a>
              <a-divider v-if="btnEnableList.indexOf(1)>-1" type="vertical" />
              <a v-if="btnEnableList.indexOf(1)>-1" @click="myHandleEdit(record)">编辑</a>
              <a-divider v-if="btnEnableList.indexOf(1)>-1" type="vertical" />
              <a v-if="btnEnableList.indexOf(1)>-1" @click="myHandleCopyAdd(record)">复制</a>
              <a-divider v-if="btnEnableList.indexOf(1)>-1" type="vertical" />
              <a-popconfirm v-if="btnEnableList.indexOf(1)>-1" title="确定删除吗?" @confirm="() => myHandleDelete(record)">
                <a>删除</a>
              </a-popconfirm>
            </span>
            <template slot="customRenderStatus" slot-scope="status, record">
              <a-tag v-if="status == '0'" color="red">未审核</a-tag>
              <a-tag v-if="status == '1'" color="green">已审核</a-tag>
              <a-tag v-if="status == '2'" color="cyan">已生产</a-tag>
              <a-tag v-if="status == '3'" color="blue">部分生产</a-tag>
              <a-tag v-if="status == '9'" color="orange">审核中</a-tag>
            </template>
          </a-table>
        </div>
        <!-- table区域-end -->
        <!-- 表单区域 -->
        <production-plan-modal ref="modalForm" @ok="modalFormOk" @close="modalFormClose"></production-plan-modal>
        <bill-detail ref="modalDetail" @ok="modalFormOk" @close="modalFormClose"></bill-detail>
      </a-card>
    </a-col>
  </a-row>
</template>
<script>
  import ProductionPlanModal from './modules/ProductionPlanModal'
  import BillDetail from './dialog/BillDetail'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { BillListMixin } from './mixins/BillListMixin'
  import { getCurrentSystemConfig } from '@/api/api'
  import JDate from '@/components/jeecg/JDate'
  import Vue from 'vue'
  export default {
    name: "ProductionPlanList",
    mixins:[JeecgListMixin,BillListMixin],
    components: {
      ProductionPlanModal,
      BillDetail,
      JDate
    },
    data () {
      return {
        // 查询条件
        queryParam: {
          number: "",
          materialParam: "",
          type: "其它",
          subType: "生产计划",
          roleType: Vue.ls.get('roleType'),
          organId: "",
          depotId: "",
          creator: "",
          status: "",
          remark: ""
        },
        prefixNo: 'SCJH',
        labelCol: {
          span: 5
        },
        wrapperCol: {
          span: 18,
          offset: 1
        },
        // 表头
        columns: [
          {
            title: '操作',
            dataIndex: 'action',
            align:"center", width: 120,
            scopedSlots: { customRender: 'action' },
          },
          { title: '客户', dataIndex: 'organName',width:100, ellipsis:true},
          { title: '单据编号', dataIndex: 'number',width:120},
          { title: '产品信息', dataIndex: 'materialsList',width:150, ellipsis:true,
            customRender:function (text,record,index) {
              if(text) {
                return text.replace(",","，");
              }
            }
          },
          { title: '单据日期', dataIndex: 'operTimeStr',width:100},
          { title: '计划完成日期', dataIndex: 'planFinishTimeStr',width:80},
          { title: '制单人', dataIndex: 'userName',width:60, ellipsis:true},
          { title: '数量', dataIndex: 'materialCount',width:50},
          { title: '状态', dataIndex: 'status', width: 70, align: "center",
            scopedSlots: { customRender: 'customRenderStatus' }
          }
        ],
        url: {
          list: "/depotHead/list",
          delete: "/depotHead/delete",
          deleteBatch: "/depotHead/deleteBatch",
          batchSetStatusUrl: "/depotHead/batchSetStatus",
        }
      }
    },
    created() {
      this.initSystemConfig()
      this.initCustomer()
      this.initUser()
      this.getSystemConfig()
    },
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}${this.url.importExcelUrl}`;
      }
    },
    methods: {
      getSystemConfig() {
        let statusIndex = 0
        for(let i=0; i<this.columns.length; i++){
          if(this.columns[i].dataIndex === 'purchaseStatus') {
            statusIndex = i
          }
        }
        getCurrentSystemConfig().then((res) => {
          if(res.code === 200 && res.data){
            let purchaseBySaleFlag = res.data.purchaseBySaleFlag
            if(purchaseBySaleFlag === "0") {
              if(statusIndex>0) {
                //移除采购进度列
                this.columns.splice(statusIndex, 1)
              }
            } else {
              if(statusIndex===0) {
                let purchaseStatusObj = { title: '采购进度', dataIndex: 'purchaseStatus', width: 70, align: "center",
                  scopedSlots: { customRender: 'customRenderPurchaseStatus' }
                }
                //添加采购进度列
                this.columns.splice(statusIndex-1, 0, purchaseStatusObj)
              }
            }
          } else {
            if(statusIndex>0) {
              //移除采购进度列
              this.columns.splice(statusIndex, 1)
            }
          }
        })
      },
      searchQuery() {
        this.loadData(1)
        this.getSystemConfig()
      },
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>