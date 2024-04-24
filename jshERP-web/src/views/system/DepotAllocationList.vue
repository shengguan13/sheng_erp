<!-- f r o m 7 5  2 7 1  8 9 2 0 -->
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
        <!-- 操作按钮区域 -->
        <div class="table-operator"  style="margin-top: 5px">
          <a-button v-if="btnEnableList.indexOf(1)>-1" @click="handleAdd" type="primary" icon="plus">新增</a-button>
          <a-button v-if="btnEnableList.indexOf(3)>-1" @click="handleImportXls()" type="primary" icon="import">导入</a-button>
          <a-button v-if="btnEnableList.indexOf(3)>-1" type="primary" icon="download" @click="handleExportXls('仓库货位')">导出</a-button>
          <a-dropdown>
            <a-menu slot="overlay">
              <a-menu-item key="1" v-if="btnEnableList.indexOf(1)>-1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
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
            :pagination="ipagination"
            :scroll="scroll"
            :loading="loading"
            :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
            @change="handleTableChange">
            <span slot="action" slot-scope="text, record">
              <a @click="handleEdit(record)">编辑</a>
              <a-divider v-if="btnEnableList.indexOf(1)>-1" type="vertical" />
              <a-popconfirm v-if="btnEnableList.indexOf(1)>-1" title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                <a>删除</a>
              </a-popconfirm>
            </span>
          </a-table>
        </div>
        <!-- table区域-end -->
        <!-- 表单区域 -->
        <depot-allocation-modal ref="modalForm" @ok="modalFormOk"></depot-allocation-modal>
        <import-file-modal ref="modalImportForm" @ok="modalFormOk"></import-file-modal>
      </a-card>
    </a-col>
  </a-row>
</template>
<!-- BY cao_yu_li -->
<script>
  import DepotAllocationModal from './modules/DepotAllocationModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JDate from '@/components/jeecg/JDate'
  import ImportFileModal from '@/components/tools/ImportFileModal'
  import { getAction, getFileAccessHttpUrl } from '@api/manage'
  export default {
    name: "DepotAllocationList",
    mixins:[JeecgListMixin],
    components: {
      DepotAllocationModal,
      ImportFileModal,
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
        queryParam: {depotId:'',allocation:'',type:''},
        depotList:[],
        // 表头
        columns: [
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            width: 150,
            scopedSlots: { customRender: 'action' },
          },
          {title: '货位', dataIndex: 'allocation', width: 100},
          {title: '货位分类', dataIndex: 'type', width: 100},
          {title: '仓库', dataIndex: 'depotName', width: 100}
        ],
        url: {
          list: "/depotAllocation/list",
          delete: "/depotAllocation/delete",
          deleteBatch: "/depotAllocation/deleteBatch",
          importExcelUrl: "/depotAllocation/importExcel",
          exportXlsUrl: "/depotAllocation/exportExcel"
        }
      }
    },
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}${this.url.importExcelUrl}`;
      }
    },
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
      handleImportXls() {
        let importExcelUrl = this.url.importExcelUrl
        let templateUrl = '/doc/allocation_template.xls'
        let templateName = '仓库货位模板[下载]'
        this.$refs.modalImportForm.initModal(importExcelUrl, templateUrl, templateName);
        this.$refs.modalImportForm.title = "仓库货位导入";
      },
      handleEdit: function (record) {
        this.$refs.modalForm.edit(record);
        this.$refs.modalForm.title = "编辑";
        this.$refs.modalForm.disableSubmit = false;
        if(this.btnEnableList.indexOf(1)===-1) {
          this.$refs.modalForm.isReadOnly = true
        }
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>