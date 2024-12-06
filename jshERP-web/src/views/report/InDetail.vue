<!-- from 7 5 2 7 1 8 9 2 0 -->
<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :style="cardStyle" :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
          <a-form layout="inline" @keyup.enter.native="searchQuery">
            <a-row :gutter="24">
              <a-col :md="3" :sm="24">
                <a-form-item label="类别" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-tree-select style="width:100%" :dropdownStyle="{maxHeight:'200px',overflow:'auto'}" allow-clear
                       :treeData="categoryTree" v-model="queryParam.categoryId" placeholder="请选择类别">
                  </a-tree-select>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="产品信息" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="编码/名称/型号/规格" v-model="queryParam.materialParam"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="单据日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-range-picker
                    style="width: 100%"
                    v-model="queryParam.createTimeRange"
                    :default-value="defaultTimeStr"
                    format="YYYY-MM-DD"
                    :placeholder="['开始时间', '结束时间']"
                    @change="onDateChange"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24" >
                <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                  <a-button type="primary" @click="searchQuery">查询</a-button>
                  <a-button style="margin-left: 8px" @click="exportExcel" icon="download">导出</a-button>
                  <a @click="handleToggleSearch" style="margin-left: 8px">
                    {{ toggleSearchStatus ? '收起' : '展开' }}
                    <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
                  </a>
                </span>
              </a-col>
              <template v-if="toggleSearchStatus">
                <a-col :md="6" :sm="24">
                  <a-form-item label="往来单位" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select placeholder="选择往来单位" v-model="queryParam.organId"
                              :dropdownMatchSelectWidth="false" showSearch allow-clear optionFilterProp="children">
                      <a-select-option v-for="(item,index) in organList" :key="index" :value="item.id">
                        {{ item.supplier }}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="仓库" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select
                      optionFilterProp="children"
                      :dropdownMatchSelectWidth="false"
                      showSearch allow-clear style="width: 100%"
                      placeholder="请选择仓库"
                      v-model="queryParam.depotId">
                      <a-select-option v-for="(depot,index) in depotList" :value="depot.id">
                        {{ depot.depotName }}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="单据编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="请输入单据编号" v-model="queryParam.number"></a-input>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="批次" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="请输入批次" v-model="queryParam.batchNumber"></a-input>
                  </a-form-item>
                </a-col>
              </template>
            </a-row>
          </a-form>
        </div>
        <!-- table区域-begin -->
        <section ref="print" id="reportPrint">
          <a-table
            bordered
            ref="table"
            size="middle"
            rowKey="id"
            :columns="columns"
            :dataSource="dataSource"
            :pagination="false"
            :scroll="scroll"
            :loading="loading"
            @change="handleTableChange">
            <span slot="numberCustomRender" slot-scope="text, record">
              <a @click="myHandleDetail(record)">{{record.number}}</a>
            </span>
          </a-table>
          <a-row :gutter="24" style="margin-top: 8px;text-align:right;">
            <a-col :md="24" :sm="24">
              <a-pagination @change="paginationChange" @showSizeChange="paginationShowSizeChange"
                size="small"
                show-size-changer
                :showQuickJumper="true"
                :current="ipagination.current"
                :page-size="ipagination.pageSize"
                :page-size-options="ipagination.pageSizeOptions"
                :total="ipagination.total"
                :show-total="(total, range) => `共 ${total-Math.ceil(total/ipagination.pageSize)} 条`">
                <template slot="buildOptionText" slot-scope="props">
                  <span>{{ props.value-1 }}条/页</span>
                </template>
              </a-pagination>
            </a-col>
          </a-row>
        </section>
        <!-- table区域-end -->
        <!-- 表单区域 -->
        <bill-detail ref="modalDetail"></bill-detail>
      </a-card>
    </a-col>
  </a-row>
</template>
<script>
  import BillDetail from '../bill/dialog/BillDetail'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { getNowFormatYear, openDownloadDialog, sheet2blob} from "@/utils/util"
  import {getAction} from '@/api/manage'
  import {findBySelectOrgan, findBillDetailByNumber, queryMaterialCategoryTreeList} from '@/api/api'
  import JEllipsis from '@/components/jeecg/JEllipsis'
  import moment from 'moment'
  import Vue from 'vue'
  export default {
    name: "InDetail",
    mixins:[JeecgListMixin],
    components: {
      BillDetail,
      JEllipsis
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
          organId: '',
          number: '',
          materialParam:'',
          categoryId:'',
          depotId: '',
          beginTime: getNowFormatYear() + '-01-01',
          endTime: moment().format('YYYY-MM-DD'),
          roleType: Vue.ls.get('roleType'),
          type: "入库",
          batchNumber: '',
          remark: '',
        },
        ipagination:{
          pageSize: 11,
          pageSizeOptions: ['11', '51', '101', '201']
        },
        dateFormat: 'YYYY-MM-DD',
        currentDay: moment().format('YYYY-MM-DD'),
        defaultTimeStr: '',
        organList: [],
        depotList: [],
        categoryTree:[],
        tabKey: "1",
        // 表头
        columns: [
          {
            title: '#', dataIndex: 'rowIndex', width:60, align:"center", fixed: 'left',
            customRender:function (t,r,index) {
              return (t !== '合计') ? (parseInt(index) + 1) : t
            }
          },
          {
            title: '单据编号', dataIndex: 'number', width: 150, fixed: 'left',
            scopedSlots: { customRender: 'numberCustomRender' },
          },
          {title: '编码', dataIndex: 'barCode', width: 80, fixed: 'left'},
          {title: '名称', dataIndex: 'mname', width: 120, fixed: 'left'},
          {title: '零件号', dataIndex: 'model'},
          {title: '客/供型号', dataIndex: 'supplierModel'},
          {title: '类别', dataIndex: 'categoryName'},
          {title: '项目', dataIndex: 'project'},
          {title: '单位', dataIndex: 'mUnit'},
          {title: '数量', dataIndex: 'operNumber', sorter: (a, b) => a.operNumber - b.operNumber},
          {title: '往来单位', dataIndex: 'sname'},
          {title: '仓库', dataIndex: 'dname'},
          {title: '批号', dataIndex: 'batchNumber'},
          {title: '货位', dataIndex: 'snListStr'},
          {title: '入库日期', dataIndex: 'operTime'},
          {title: '备注', dataIndex: 'newRemark'}
        ],
        url: {
          list: "/depotHead/findInOutDetail",
        }
      }
    },
    created () {
      this.getDepotData()
      this.loadTreeData()
      this.initSupplier()
      this.defaultTimeStr = [moment(getNowFormatYear() + '-01-01', this.dateFormat), moment(this.currentDay, this.dateFormat)]
    },
    mounted () {
      this.scroll.x = 1400
    },
    methods: {
      moment,
      getQueryParams() {
        let param = Object.assign({}, this.queryParam, this.isorter);
        param.field = this.getQueryField();
        param.currentPage = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize-1;
        return param;
      },
      onDateChange: function (value, dateString) {
        this.queryParam.beginTime=dateString[0];
        this.queryParam.endTime=dateString[1];
      },
      initSupplier() {
        let that = this;
        findBySelectOrgan({}).then((res)=>{
          if(res) {
            that.organList = res;
          }
        });
      },
      getDepotData() {
        getAction('/depot/findDepotByCurrentUser').then((res)=>{
          if(res.code === 200){
            this.depotList = res.data;
          }else{
            this.$message.info(res.data);
          }
        })
      },
      loadTreeData(){
        let that = this;
        let params = {};
        params.id='';
        queryMaterialCategoryTreeList(params).then((res)=>{
          if(res){
            that.categoryTree = [];
            for (let i = 0; i < res.length; i++) {
              let temp = res[i];
              that.categoryTree.push(temp);
            }
          }
        })
      },
      myHandleDetail(record) {
        findBillDetailByNumber({ number: record.number }).then((res) => {
          if (res && res.code === 200) {
            this.$refs.modalDetail.isCanBackCheck = false
            this.handleDetail(res.data, record.newType)
          }
        })
      },
      searchQuery() {
        if(this.queryParam.beginTime == '' || this.queryParam.endTime == ''){
          this.$message.warning('请选择单据日期！')
        } else {
          this.loadData(1);
        }
      },
      exportExcel() {
        let aoa = [['单据编号', '编码', '名称', '型号', '客/供型号', '类别', '项目', '单位', '数量', '往来单位', '仓库', '批号', '货位', '入库日期', '备注']]
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.number, ds.barCode, ds.mname, ds.model, ds.supplierModel, ds.categoryName, ds.project, ds.mUnit,
            ds.operNumber, ds.sname, ds.dname, ds.batchNumber, ds.snListStr, ds.operTime, ds.newRemark]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), '入库明细')
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>