<template>
  <div ref="container">
    <a-modal
      :title="title"
      :width="1300"
      :visible="visible"
      :confirmLoading="confirmLoading"
      :getContainer="() => $refs.container"
      :maskStyle="{'top':'93px','left':'154px'}"
      :wrapClassName="wrapClassNameInfo()"
      :mask="isDesktop()"
      :maskClosable="false"
      @ok="handleOk"
      :ok-button-props="{ style: { display: 'none' } }"
      @cancel="handleCancel"
      cancelText="关闭"
      style="top:20px;height: 95%;">
      <template slot="footer">
        <a-button key="back" v-if="isReadOnly" @click="handleCancel">
          关闭
        </a-button>
      </template>
      <a-spin :spinning="confirmLoading">
        <a-form :form="form">
          <j-editable-table
            ref="materialCompositeTable"
            :loading="compositeTable.loading"
            :columns="compositeTable.columns"
            :dataSource="compositeTable.dataSource"
            :minWidth="1100"
            :maxHeight="300"
            :rowNumber="false"
            :rowSelection="false"
            :actionButton="false"
            @added="onAddedMaterialComposite"
            @deleted="onDeletedMaterialComposite"
            @valueChange="onValueChangeMaterialComposite">
          </j-editable-table>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>
<script>
  import pick from 'lodash.pick'
  import {addMaterialBom,editMaterialBom} from '@/api/api'
  import { getAction, httpAction } from '@/api/manage'
  import JEditableTable from '@/components/jeecg/JEditableTable'
  import { FormTypes, getRefPromise, VALIDATE_NO_PASSED, validateFormAndTables } from '@/utils/JEditableTableUtil'
  import {mixinDevice} from '@/utils/mixin'
  export default {
    name: "MaterialBomViewModal",
    components: {
      JEditableTable
    },
    mixins: [mixinDevice],
    data () {
      return {
        title:"操作",
        visible: false,
        model: {},
        isReadOnly: false,
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },
        compositeTable: {
          loading: false,
          dataSource: [],
          columns: [
            { title: '工艺流程', key: 'process', width: '8%', type: FormTypes.normal },
            { title: '编码', key: 'barCode', width: '6%', type: FormTypes.normal },
            { title: '名称', key: 'name', width: '8%', type: FormTypes.normal },
            { title: '零件号', key: 'partNo', width: '8%', type: FormTypes.normal },
            { title: '零件号', key: 'model', width: '9%', type: FormTypes.normal },
            { title: '颜色', key: 'color', width: '5%', type: FormTypes.normal },
            { title: '颜色代码', key: 'colorCode', width: '5%', type: FormTypes.normal },
            { title: '类别', key: 'category', width: '7%', type: FormTypes.normal },
            { title: '用量', key: 'processUsage', width: '8%', type: FormTypes.normal },
            { title: '单位', key: 'unit', width: '4%', type: FormTypes.normal },
            { title: '负责部门', key: 'department', width: '6%', type: FormTypes.normal }
          ]
        },
        url: {
          findComposite: '/materialBom/findComposite',
        },
        confirmLoading: false,
        form: this.$form.createForm(this)
      }
    },
    created () {
    },
    methods: {
      add () {
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.requestCompositeTableData(this.url.findComposite, record.process, record.project, this.compositeTable);
        this.visible = true;
        this.$nextTick(() => {
        });
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      requestCompositeTableData(url, process, project, tab) {
        tab.loading = true
        let params = {
          process: process,
          project: project,
        }
        getAction(url, params).then(res => {
          tab.dataSource = res.data || []
        }).finally(() => {
          tab.loading = false
        })
      },
      handleOk () {
      },
      handleCancel () {
        this.close()
      }
    }
  }
</script>
<style scoped>

</style>