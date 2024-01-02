<!-- from 7 5 2 7 1 8 9 2 0 -->
<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :style="cardStyle" :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
          <a-form layout="inline" @keyup.enter.native="searchQuery">
            <a-row :gutter="24">
              <a-col :md="6" :sm="24">
                <a-form-item label="产品信息" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="编码/名称/型号/规格" v-model="queryParam.materialParam"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="生产单日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
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
              <a-col :md="6" :sm="24">
                <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                  <a-button type="primary" @click="searchQuery">查询</a-button>
                  <a-button style="margin-left: 8px" @click="exportExcel" icon="download">导出</a-button>
                </span>
              </a-col>
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
            <span slot="action" slot-scope="text, record">
              <a @click="showProductionInList(record)">{{record.materialId?'详情':''}}</a>
            </span>
            <span slot="action2" slot-scope="text, record">
              <a @click="showMaterialUsageList(record)">{{record.materialId?'详情':''}}</a>
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
        <production-in-list ref="productionInList" @ok="modalFormOk"></production-in-list>
        <material-usage-list ref="materialUsageList" @ok="modalFormOk"></material-usage-list>
      </a-card>
    </a-col>
  </a-row>
</template>
<script>
  import ProductionInList from './modules/ProductionInList'
  import MaterialUsageList from './modules/MaterialUsageList'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { getNowFormatYear, getMpListShort, openDownloadDialog, sheet2blob} from "@/utils/util"
  import JEllipsis from '@/components/jeecg/JEllipsis'
  import moment from 'moment'
  import Vue from 'vue'
  export default {
    name: "ProductionReport",
    mixins:[JeecgListMixin],
    components: {
      ProductionInList,
      MaterialUsageList,
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
        queryParam: {
          materialParam:'',
          beginTime: getNowFormatYear() + '-01-01',
          endTime: moment().format('YYYY-MM-DD'),
          mpList: getMpListShort(Vue.ls.get('materialPropertyList')),
          roleType: Vue.ls.get('roleType'),
        },
        ipagination:{
          pageSize: 11,
          pageSizeOptions: ['11', '21', '31', '101', '201']
        },
        dateFormat: 'YYYY-MM-DD',
        currentDay: moment().format('YYYY-MM-DD'),
        defaultTimeStr: '',
        tabKey: "1",
        // 表头
        columns: [
          {
            title: '#', dataIndex: 'rowIndex', width:60, align:"center", fixed: 'left',
            customRender:function (t,r,index) {
              return (t !== '合计') ? (parseInt(index) + 1) : t
            }
          },
          {title: '入库详情', dataIndex: 'action', align:"center", width: 100, fixed: 'left',
            scopedSlots: { customRender: 'action' }
          },
          {title: '用料详情', dataIndex: 'action2', align:"center", width: 100, fixed: 'left',
            scopedSlots: { customRender: 'action2' }
          },
          {title: '编码', dataIndex: 'barCode', width: 100, fixed: 'left'},
          {title: '名称', dataIndex: 'materialName', width: 150, fixed: 'left'},
          {title: '型号', dataIndex: 'materialColorCode'},
          {title: '规格', dataIndex: 'materialModel'},
          {title: '扩展信息', dataIndex: 'materialOther', ellipsis:true},
          {title: '单位', dataIndex: 'materialUnit', ellipsis:true},
          {title: '生产入库数量', dataIndex: 'productionIn', sorter: (a, b) => a.productionIn - b.productionIn}
        ],
        url: {
          list: "/depotItem/productionIn"
        }
      }
    },
    created () {
      this.defaultTimeStr = [moment(getNowFormatYear() + '-01-01', this.dateFormat), moment(this.currentDay, this.dateFormat)]
    },
    mounted () {
      this.scroll.x = 1400
    },
    methods: {
      moment,
      getQueryParams() {
        let param = Object.assign({}, this.queryParam, this.isorter);
        param.monthTime = this.queryParam.monthTime;
        param.field = this.getQueryField();
        param.currentPage = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize-1;
        return param;
      },
      onDateChange: function (value, dateString) {
        this.queryParam.beginTime=dateString[0];
        this.queryParam.endTime=dateString[1];
      },
      searchQuery() {
        if(this.queryParam.beginTime == '' || this.queryParam.endTime == ''){
          this.$message.warning('请选择生产单日期！')
        } else {
          this.loadData(1);
        }
      },
      showProductionInList(record) {
        let depotIds = ''
        if(this.depotSelected && this.depotSelected.length>0) {
          depotIds = this.depotSelected.join()
        }
        this.$refs.productionInList.show(record, depotIds);
        this.$refs.productionInList.title = "查看入库详情";
        this.$refs.productionInList.disableSubmit = false;
      },
      showMaterialUsageList(record) {
        let depotIds = ''
        if(this.depotSelected && this.depotSelected.length>0) {
          depotIds = this.depotSelected.join()
        }
        this.$refs.materialUsageList.show(record, depotIds);
        this.$refs.materialUsageList.title = "查看用料详情";
        this.$refs.materialUsageList.disableSubmit = false;
      },
      exportExcel() {
        let aoa = [['编码', '名称', '型号', '规格', '扩展信息', '单位', '生产入库数量']]
        for (let i = 0; i < this.dataSource.length; i++) {
          let ds = this.dataSource[i]
          let item = [ds.barCode, ds.materialName, ds.materialColorCode, ds.materialModel, ds.materialOther, ds.materialUnit, ds.productionIn]
          aoa.push(item)
        }
        openDownloadDialog(sheet2blob(aoa), '生产统计')
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>