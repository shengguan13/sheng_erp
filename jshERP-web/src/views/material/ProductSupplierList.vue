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
                <a-form-item label="客商名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-select placeholder="请选择客商" showSearch optionFilterProp="children" v-model="queryParam.supplierId">
                    <a-select-option v-for="(supplier,index) in supplierList" :value="supplier.id">
                      {{ supplier.supplier }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="关键词" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="请输入编码、名称、型号、规格查询" v-model="queryParam.keyword"></a-input>
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
          <a-button v-if="btnEnableList.indexOf(3)>-1" type="primary" icon="download" @click="handleExportXls('客商档案')">导出</a-button>
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
        <product-supplier-modal ref="modalForm" @ok="modalFormOk"></product-supplier-modal>
        <import-file-modal ref="modalImportForm" @ok="modalFormOk"></import-file-modal>
      </a-card>
    </a-col>
  </a-row>
</template>
<!-- BY cao_yu_li -->
<script>
  import ProductSupplierModal from './modules/ProductSupplierModal'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JDate from '@/components/jeecg/JDate'
  import ImportFileModal from '@/components/tools/ImportFileModal'
  import { postAction, getFileAccessHttpUrl } from '@api/manage'
  export default {
    name: "ProductSupplierList",
    mixins:[JeecgListMixin],
    components: {
      ProductSupplierModal,
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
        queryParam: {supplierId:'',keyword:''},
        supplierList:[],
        // 表头
        columns: [
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            width: 50,
            scopedSlots: { customRender: 'action' },
          },
          {title: '客商名称', dataIndex: 'supplierName', width: 120},
          {title: '客商类别', dataIndex: 'supplierType', width: 40},
          {title: '物料编码', dataIndex: 'barCode', width: 70},
          {title: '物料名称', dataIndex: 'materialName', width: 120},
          {title: '客/供型号', dataIndex: 'model', width: 80},
          {title: '零件号', dataIndex: 'materialModel', width: 80},
          {title: '标包', dataIndex: 'pack', width: 40}
        ],
        url: {
          list: "/productSupplier/list",
          delete: "/productSupplier/delete",
          deleteBatch: "/productSupplier/deleteBatch",
          importExcelUrl: "/productSupplier/importExcel",
          exportXlsUrl: "/productSupplier/exportExcel"
        }
      }
    },
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}${this.url.importExcelUrl}`;
      }
    },
    created () {
      postAction('/supplier/findBySelect_sup').then((res)=>{
        this.supplierList = res;
      })
    },
    methods: {
      handleImportXls() {
        let importExcelUrl = this.url.importExcelUrl
        let templateUrl = '/doc/product_supplier_template.xls'
        let templateName = '客商档案模板[下载]'
        this.$refs.modalImportForm.initModal(importExcelUrl, templateUrl, templateName);
        this.$refs.modalImportForm.title = "客商档案导入";
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