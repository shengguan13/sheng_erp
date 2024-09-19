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
                <a-form-item label="仓库名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-select placeholder="请选择仓库" showSearch optionFilterProp="children" v-model="queryParam.depotId">
                    <a-select-option v-for="(depot,index) in depotList" :value="depot.id">
                      {{ depot.depotName }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="货位" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="请输入货位查询" v-model="queryParam.allocation"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="货位分类" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-select placeholder="选择货位分类" showSearch optionFilterProp="children" v-model="queryParam.type">
                    <a-select-option value="原材料">原材料</a-select-option>
                    <a-select-option value="辅料">辅料</a-select-option>
                    <a-select-option value="隔离">隔离</a-select-option>
                    <a-select-option value="供应商">供应商</a-select-option>
                    <a-select-option value="门卫室">门卫室</a-select-option>
                    <a-select-option value="外检区">外检区</a-select-option>
                    <a-select-option value="项目">项目</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                <a-col :md="6" :sm="24">
                  <a-button type="primary" @click="searchQuery">查询</a-button>
                  <a-button style="margin-left: 8px" @click="searchReset">重置</a-button>
                </a-col>
              </span>
            </a-row>
          </a-form>
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
            :pagination="ipagination"
            :scroll="scroll"
            :loading="loading"
            @change="handleTableChange">
            <span slot="action" slot-scope="text, record">
              <a @click="myHandleDetail(record)">查看货位明细</a>
            </span>
          </a-table>
        </div>
        <!-- table区域-end -->
        <allocation-stock ref="allocationStock"></allocation-stock>
      </a-card>
    </a-col>
  </a-row>
</template>
<script>
  import AllocationStock from './dialog/AllocationStock'
  import { getAllocationDetail } from '@/api/api'
  import { getAction } from '@api/manage'
  import Vue from 'vue'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  export default {
    name: "AllocationReport",
    mixins:[JeecgListMixin],
    components: {
      AllocationStock
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
        queryParam: {depotId:'',allocation:'',type:''},
        depotList:[],
        // 表头
        columns: [
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            width: 100,
            scopedSlots: { customRender: 'action' },
          },
          {title: '货位', dataIndex: 'allocation', width: 100},
          {title: '货位分类', dataIndex: 'type', width: 100},
          {title: '仓库', dataIndex: 'depotName', width: 100}
        ],
        url: {
          list: "/depotAllocation/list"
        }
      }
    },
    computed: {},
    created () {
      getAction('/depot/findDepotByCurrentUser').then((res)=>{
        if(res.code === 200){
          this.depotList = res.data;
        }else{
          this.$message.info(res.data);
        }
      })
    },
    methods: {
      myHandleDetail(record) {
        getAllocationDetail({ allocationId: record.id }).then((res) => {
          if (res && res.code === 200) {
            let dataToShow = []
            for(let i of res.data.rows){
              if(i.totalNum != 0){
                dataToShow.push(i)
              }
            }
            this.$refs.allocationStock.show(dataToShow);
            this.$refs.allocationStock.title="货位明细";
          }
        })
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>