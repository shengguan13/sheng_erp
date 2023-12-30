<!-- by 7527189 2 0 -->
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
                <a-form-item label="类别" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-tree-select style="width:100%" :dropdownStyle="{maxHeight:'200px',overflow:'auto'}" allow-clear
                   :treeData="categoryTree" v-model="queryParam.categoryId" placeholder="请选择类别">
                  </a-tree-select>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="关键词" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="请输入编码、名称、型号、规格查询" v-model="queryParam.materialParam"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="项目" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input style="width: 100%" placeholder="请输入项目信息查询" v-model="queryParam.project"></a-input>
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
          <a-button v-if="btnEnableList.indexOf(1)>-1" @click="handleImportXls()" type="primary" icon="import">导入</a-button>
          <a-button type="primary" icon="download" @click="handleExportXls('产品信息')">导出</a-button>
          <a-dropdown>
            <a-menu slot="overlay">
              <a-menu-item key="1" v-if="btnEnableList.indexOf(1)>-1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
            </a-menu>
            <a-button>
              批量操作 <a-icon type="down" />
            </a-button>
          </a-dropdown>
          <a-popover trigger="click" placement="right">
            <template slot="content">
              <a-checkbox-group @change="onColChange" v-model="settingColumns" :defaultValue="settingColumns">
                <a-row style="width: 500px">
                  <template v-for="(item,index) in defColumns">
                    <template>
                      <a-col :span="8">
                        <a-checkbox :value="item.dataIndex">
                          <j-ellipsis :value="item.title" :length="10"></j-ellipsis>
                        </a-checkbox>
                      </a-col>
                    </template>
                  </template>
                </a-row>
              </a-checkbox-group>
            </template>
            <a-button icon="setting">列设置</a-button>
          </a-popover>
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
            :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange, columnWidth:'2%'}"
            @change="handleTableChange">
            <span slot="action" slot-scope="text, record">
              <a @click="handleDetail(record)">查看</a>
              <a-divider v-if="btnEnableList.indexOf(1)>-1" type="vertical" />
              <a v-if="btnEnableList.indexOf(1)>-1" @click="handleEdit(record)">编辑</a>
              <a-divider v-if="btnEnableList.indexOf(1)>-1" type="vertical" />
              <a-popconfirm v-if="btnEnableList.indexOf(1)>-1" title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                <a>删除</a>
              </a-popconfirm>
            </span>
          </a-table>
        </div>
        <!-- table区域-end -->
        <!-- 表单区域 -->
        <material-bom-view-modal ref="bomViewModalForm"></material-bom-view-modal>
        <material-bom-modal ref="modalForm"></material-bom-modal>
        <import-file-modal ref="modalImportForm" @ok="modalFormOk"></import-file-modal>
        <batch-set-info-modal ref="batchSetInfoModalForm" @ok="modalFormOk"></batch-set-info-modal>
      </a-card>
    </a-col>
  </a-row>
</template>
<script>
  import MaterialBomViewModal from './modules/MaterialBomViewModal'
  import MaterialBomModal from './modules/MaterialBomModal'
  import ImportFileModal from '@/components/tools/ImportFileModal'
  import BatchSetInfoModal from './modules/BatchSetInfoModal'
  import { queryMaterialCategoryTreeList } from '@/api/api'
  import { postAction, getFileAccessHttpUrl } from '@/api/manage'
  import { getMpListShort } from '@/utils/util'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JEllipsis from '@/components/jeecg/JEllipsis'
  import JDate from '@/components/jeecg/JDate'
  import Vue from 'vue'

  export default {
    name: "MaterialBomList",
    mixins:[JeecgListMixin],
    components: {
      MaterialBomViewModal,
      MaterialBomModal,
      ImportFileModal,
      BatchSetInfoModal,
      JEllipsis,
      JDate
    },
    data () {
      return {
        categoryTree:[],
        mPropertyListShort: '',
        model: {},
        labelCol: {
          span: 5
        },
        wrapperCol: {
          span: 18,
          offset: 1
        },
        // 查询条件
        queryParam: {
          categoryId:'',
          materialParam:'',
          project:'',
          mpList: getMpListShort(Vue.ls.get('materialPropertyList'))  //扩展属性
        },
        ipagination:{
          pageSizeOptions: ['10', '20', '30', '100', '200']
        },
        // 实际表头
        columns:[],
        // 初始化设置的表头
        settingColumns:['action','project','barCode','process','name','partNo','colorCode','model','color',
          'category','processUsage','unit','department','source','remark'],
        // 默认的列
        defColumns: [
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            width: 120,
            scopedSlots: { customRender: 'action' },
          },
          {title: '项目', dataIndex: 'project', width: 70},
          {title: '物料编码', dataIndex: 'barCode', width: 90},
          {title: '工艺流程', dataIndex: 'process', width: 80},
          {title: '名称', dataIndex: 'name', width: 120},
          {title: '零件号', dataIndex: 'partNo', width: 100},
          {title: '型号', dataIndex: 'colorCode', width: 120},
          {title: '规格', dataIndex: 'model', width: 120},
          {title: '颜色', dataIndex: 'color', width: 40},
          {title: '类别', dataIndex: 'category', width: 60},
          {title: '用量', dataIndex: 'processUsage', width: 60},
          {title: '单位', dataIndex: 'unit', width: 60},
          {title: '负责部门', dataIndex: 'department', width: 60},
          {title: '物料来源', dataIndex: 'source', width: 60},
          {title: '备注', dataIndex: 'remark', width: 60}
        ],
        url: {
          list: "/materialBom/list",
          delete: "/materialBom/delete",
          deleteBatch: "/materialBom/deleteBatch",
          importExcelUrl: "/materialBom/importExcel",
          exportXlsUrl: "/materialBom/exportExcel"
        }
      }
    },
    created() {
      this.model = Object.assign({}, {});
      this.initColumnsSetting()
      this.loadTreeData();
    },
    computed: {
      importExcelUrl: function () {
        return `${window._CONFIG['domianURL']}${this.url.importExcelUrl}`;
      }
    },
    methods: {
      //加载初始化列
      initColumnsSetting(){
        let columnsStr = Vue.ls.get('materialColumns')
        if(columnsStr && columnsStr.indexOf(',')>-1) {
          this.settingColumns = columnsStr.split(',')
        }
        this.columns = this.defColumns.filter(item => {
          if (this.settingColumns.includes(item.dataIndex)) {
            return true
          }
          return false
        })
      },
      //列设置更改事件
      onColChange (checkedValues) {
        this.columns = this.defColumns.filter(item => {
          if (checkedValues.includes(item.dataIndex)) {
            return true
          }
          return false
        })
        let columnsStr = checkedValues.join()
        Vue.ls.set('materialColumns', columnsStr)
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
      handleDetail: function (record) {
        this.$refs.bomViewModalForm.edit(record);
        this.$refs.bomViewModalForm.title = "详情";
        this.$refs.bomViewModalForm.disableSubmit = false;
        if(this.btnEnableList.indexOf(1)===-1) {
          this.$refs.bomViewModalForm.isReadOnly = true
        }
      },
      handleEdit: function (record) {
        this.$refs.modalForm.edit(record);
        this.$refs.modalForm.title = "编辑";
        this.$refs.modalForm.disableSubmit = false;
        if(this.btnEnableList.indexOf(1)===-1) {
          this.$refs.modalForm.isReadOnly = true
        }
      },
      handleImportXls() {
        let importExcelUrl = this.url.importExcelUrl
        let templateUrl = '/doc/bom_template.xls'
        let templateName = '项目BOM模板[下载]'
        this.$refs.modalImportForm.initModal(importExcelUrl, templateUrl, templateName);
        this.$refs.modalImportForm.title = "项目BOM导入";
      },
      searchReset() {
        this.queryParam = {
          mpList: getMpListShort(Vue.ls.get('materialPropertyList'))  //扩展属性
        }
        this.loadData(1);
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>