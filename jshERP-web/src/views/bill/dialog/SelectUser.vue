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
                <a-col :md="6" :sm="24">
                  <a-form-item label="用户名" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="用户名" v-model="queryParam.name"></a-input>
                  </a-form-item>
                </a-col>
                <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                  <a-col :md="12" :sm="24">
                    <a-button type="primary" @click="loadData(1)">查询</a-button>
                    <a-button style="margin-left: 8px" @click="searchReset(1)">重置</a-button>
                  </a-col>
                </span>
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
              :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange,type: 'checkbox'}"
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
  import { listUser } from '@/api/api'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  export default {
    name: "SelectUser",
    mixins: [JeecgListMixin],
    data () {
      return {
        title:"操作",
        visible: false,
        model: {},
        isReadOnly: false,
        modalWidth: 1600,
        queryParam: {
          name: '',
        },
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
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
        title: '选择客商档案',
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
          {title: '编号', dataIndex: 'id', width: 80},
          {title: '用户名', dataIndex: 'username', width: 100}
        ],
      }
    },
    created() {
      this.loadData()
    },
    methods: {
      loadData(arg) {
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        this.loading = true
        let params = this.getQueryParams()//查询条件
        listUser(params).then((res) => {
          if (res && res.code === 200) {
            this.dataSource = res.data.rows
            this.ipagination.total = res.data.total
          }
        }).finally(() => {
          this.loading = false
        })
      },
      getQueryParams() {
        let param = Object.assign({}, this.queryParam, this.isorter);
        param.currentPage = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize;
        return param;
      },
      searchReset(num) {
        let that = this;
        this.queryParam.supplierId=''
        this.queryParam.keyword=''
        that.loadData(1);
        that.selectedRowKeys = [];
        that.selectIds = [];
      },
      add (users) {
        this.edit({});
        let userArr = users.split(",");
        this.selectedRowKeys = userArr.map(Number);
        console.log("SelectUser " + this.selectedRowKeys);
        this.selectIds = users;
        this.loadData()
      },
      edit (record) {
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
        let ids = "";
        this.selectRows = [];
        console.log("selectedRowKeys: " + JSON.stringify(this.selectedRowKeys))
        for (let i = 0, len = this.selectedRowKeys.length; i < len; i++) {
          ids = ids + "," + this.selectedRowKeys[i]
        }
        this.selectIds = ids.substring(1);
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
              console.log("rowAction: " + record.id)
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