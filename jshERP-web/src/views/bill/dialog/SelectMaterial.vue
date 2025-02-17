<template>
  <div ref="container">
    <a-modal
      :width="modalWidth"
      :visible="visible"
      :title="title"
      @ok="handleOk"
      @cancel="close"
      cancelText="关闭"
      style="top:12%;height: 90%;overflow-y: hidden"
      wrapClassName="ant-modal-cust-warp">
      <a-row :gutter="10" style="padding: 10px; margin: -10px">
        <a-col :md="24" :sm="24">
          <!-- 查询区域 -->
          <div class="table-page-search-wrapper">
            <!-- 搜索区域 -->
            <a-form layout="inline" @keyup.enter.native="onSearch">
              <a-row :gutter="24">
                <a-col :md="6" :sm="8">
                  <a-form-item label="产品信息" :labelCol="{span: 5}" :wrapperCol="{span: 18, offset: 1}">
                    <a-input ref="material" placeholder="编码/名称/型号/规格/颜色" v-model="queryParam.q"></a-input>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="8">
                  <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="类别">
                    <a-tree-select style="width:100%" :dropdownStyle="{maxHeight:'200px',overflow:'auto'}" allow-clear
                                   :treeData="categoryTree" v-model="queryParam.categoryId" placeholder="请选择类别">
                    </a-tree-select>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="8">
                  <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物料来源">
                    <a-input ref="material" placeholder="包覆/采购/..." v-model="queryParam.source"></a-input>
                  </a-form-item>
                </a-col>
                <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                  <a-col :md="12" :sm="24">
                    <a-button type="primary" @click="loadData(1)">查询</a-button>
                    <a-button style="margin-left: 8px" @click="searchReset(1)">重置</a-button>
                    <a @click="handleToggleSearch" style="margin-left: 8px">
                      {{ toggleSearchStatus ? '收起' : '展开' }}
                      <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
                    </a>
                  </a-col>
                </span>
                <template v-if="toggleSearchStatus">
                  <a-col :md="6" :sm="24">
                    <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="仓库">
                      <a-select placeholder="选择仓库" v-model="queryParam.depotId" @change="onDepotChange"
                        :dropdownMatchSelectWidth="false" showSearch optionFilterProp="children" allow-clear>
                        <a-select-option v-for="(item,index) in depotList" :key="index" :value="item.id">
                          {{ item.depotName }}
                        </a-select-option>
                      </a-select>
                    </a-form-item>
                  </a-col>
                </template>
              </a-row>
            </a-form>
            <a-table
              ref="table"
              :scroll="scrollTrigger"
              size="middle"
              rowKey="id"
              :columns="columns"
              :dataSource="dataSource"
              :pagination="ipagination"
              :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange,type: 'radio'}"
              :loading="loading"
              :customRow="rowAction"
              @change="handleTableChange">
            </a-table>
          </div>
        </a-col>
      </a-row>
    </a-modal>
  </div>
</template>
<script>
  import pick from 'lodash.pick'
  import { postAction } from '@/api/manage'
  import { filterObj, getMpListShort } from '@/utils/util'
  import { getMaterialBySelect, queryMaterialCategoryTreeList } from '@/api/api'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import Vue from 'vue'

  export default {
    name: "SelectMaterial",
    mixins: [JeecgListMixin],
    data () {
      return {
        title:"操作",
        visible: false,
        model: {},
        isReadOnly: false,
        modalWidth: 1600,
        queryParam: {
          q: '',
          source: '',
          categoryId: '',
          depotId: ''
        },
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        categoryTree:[],
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        confirmLoading: false,
        scrollTrigger: {},
        dataSource: [],
        selectedRowKeys: [],
        selectRows: [],
        selectIds: [],
        title: '选择物料编码',
        ipagination: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        isorter: {
          column: 'createTime',
          order: 'desc'
        },
        form: this.$form.createForm(this),
        loading: false,
        columns: [
          {dataIndex: 'mBarCode', title: '编码', scopedSlots: { customRender: 'customBarCode' }},
          {dataIndex: 'name', title: '名称', scopedSlots: { customRender: 'customName' }},
          {dataIndex: 'supplierModel', title: '客/供型号'},
          {dataIndex: 'categoryName', title: '类别'},
          {dataIndex: 'model', title: '零件号'},
          {dataIndex: 'color', title: '颜色'},
          {dataIndex: 'colorCode', title: '颜色代码'},
          {dataIndex: 'project', title: '项目'},
          {dataIndex: 'unit', title: '单位'},
          {dataIndex: 'stock', title: '库存'},
          {dataIndex: 'expand', title: '扩展信息'}
        ],
      }
    },
    created() {
    },
    methods: {
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
      loadData(arg) {
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        this.loading = true
        let params = this.getQueryParams()//查询条件
        getMaterialBySelect(params).then((res) => {
          if (res) {
            this.dataSource = res.rows
            this.ipagination.total = res.total
          }
        }).finally(() => {
          this.loading = false
        })
      },
      getQueryParams() {
        let param = Object.assign({}, this.queryParam, this.isorter);
        param.mpList = getMpListShort(Vue.ls.get('materialPropertyList'))  //扩展属性
        param.page = this.ipagination.current;
        param.rows = this.ipagination.pageSize;
        return filterObj(param);
      },
      searchReset(num) {
        let that = this;
        if (num !== 0) {
          that.queryParam = {};
          that.loadData(1);
        }
        that.selectedRowKeys = [];
        that.selectMaterialIds = [];
      },
      add () {
        this.edit({});
        this.loadData()
      },
      edit (record) {
        this.loadTreeData()
        this.visible = true;
      },
      close () {
        this.$emit('close');
        this.searchReset(0);
        this.visible = false;
      },
      handleTableChange(pagination, filters, sorter) {
        if (Object.keys(sorter).length > 0) {
          this.isorter.column = sorter.field;
          this.isorter.order = 'ascend' === sorter.order ? 'asc' : 'desc';
        }
        this.ipagination = pagination;
        this.loadData();
      },
      handleOk () {
        const that = this;
        that.confirmLoading = true;
        this.getSelectRows();
        that.$emit('ok', this.selectIds);
        this.searchReset(0);
        that.confirmLoading = false;
        that.close();
      },
      handleCancel () {
        this.close()
      },
      //获取选择信息
      getSelectRows(rowId) {
        let dataSource = this.dataSource;
        let barCodes = "";
        this.selectRows = [];
        for (let i = 0, len = dataSource.length; i < len; i++) {
          if (this.selectedRowKeys.includes(dataSource[i].id)) {
            this.selectRows.push(dataSource[i]);
            barCodes = barCodes + "," + dataSource[i].mBarCode
          }
        }
        this.selectIds = barCodes.substring(1);
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedRowKeys = selectedRowKeys;
        this.selectionRows = selectionRows;
      },
      onSearch() {
        this.loadData(1)
      },
      rowAction(record, index) {
        return {
          on: {
            click: () => {
              let arr = []
              arr.push(record.id)
              this.selectedRowKeys = arr
            }
          }
        }
      }
    }
  }
</script>

<style scoped>

</style>