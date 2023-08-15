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
  import {addMaterialBom,editMaterialBom,checkMaterialAttribute } from '@/api/api'
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
            { title: '条码', key: 'barCode', width: '10%', type: FormTypes.popupJsh, kind: 'material', multi: true,
              validateRules: [{ required: true, message: '${title}不能为空' }]
            },
            { title: '名称', key: 'name', width: '8%', type: FormTypes.normal },
            { title: '内部零件号', key: 'internalId', width: '9%', type: FormTypes.normal },
            { title: '客户零件号', key: 'model', width: '9%', type: FormTypes.normal },
            { title: '颜色编码', key: 'color', width: '7%', type: FormTypes.normal },
            { title: 'meId', key: 'meId', width: '5%', type: FormTypes.hidden },
            { title: '数量', key: 'operNumber', width: '8%', type: FormTypes.inputNumber, statistics: true,
              validateRules: [{ required: true, message: '${title}不能为空' }]
            },
            { title: '单位', key: 'unit', width: '4%', type: FormTypes.normal }
          ]
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
        this.visible = true;
        this.$nextTick(() => {
        });
      },
      close () {
        this.$emit('close');
        this.visible = false;
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