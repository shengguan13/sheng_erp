<template>
  <div ref="container">
    <a-modal
      :title="title"
      :width="800"
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
      style="top:100px;height: 50%;">
      <template slot="footer">
        <a-button key="back" v-if="isReadOnly" @click="handleCancel">
          关闭
        </a-button>
      </template>
      <a-spin :spinning="confirmLoading">
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="项目">
            <a-input placeholder="请输入项目" v-decorator.trim="[ 'project' ]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工艺流程">
            <a-input placeholder="请输入工艺流程" v-decorator.trim="[ 'process' ]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="产品编号">
            <a-input placeholder="请输入产品编号" v-decorator.trim="[ 'barCode' ]" />
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>
<script>
  import pick from 'lodash.pick'
  import {addMaterialBom,editMaterialBom,checkMaterialAttribute } from '@/api/api'
  import {mixinDevice} from '@/utils/mixin'
  export default {
    name: "MaterialBomViewModal",
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
          this.form.setFieldsValue(pick(this.model, 'project', 'process', 'barCode'))
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