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
      @cancel="handleCancel"
      cancelText="关闭"
      style="top:100px;height: 90%;">
      <template slot="footer">
        <a-button key="back" v-if="isReadOnly" @click="handleCancel">
          关闭
        </a-button>
      </template>
      <template>
        <a-button @click="handleSelectProductSupplier"><a-icon type="setting"/>选择归属总成</a-button>
        <a-button @click="handleSelectMaterial"><a-icon type="setting"/>选择物料编码</a-button>
      </template>
      <a-spin :spinning="confirmLoading">
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="总成">
            <a-input :readOnly="true" placeholder="请选择归属总成" v-decorator.trim="[ 'parent' ]" />
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物料编码">
            <a-input :readOnly="true" placeholder="请输入物料编码" v-decorator.trim="[ 'barCode', validatorRules.barCode ]" />
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="项目">
            <a-input placeholder="请输入项目" v-decorator.trim="[ 'project', validatorRules.project ]" />
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="状态">
            <a-select placeholder="请选择状态" v-decorator="[ 'source', validatorRules.source ]" >
              <a-select-option value="项目">项目</a-select-option>
              <a-select-option value="量产">量产</a-select-option>
              <a-select-option value="新增">新增</a-select-option>
              <a-select-option value="设变">设变</a-select-option>
              <a-select-option value="临时">临时</a-select-option>
              <a-select-option value="沿用">沿用</a-select-option>
              <a-select-option value="屏蔽">屏蔽</a-select-option>
              <a-select-option value="指定">指定</a-select-option>
              <a-select-option value="取消">取消</a-select-option>
              <a-select-option value="下线">下线</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="备注">
            <a-input placeholder="请输入备注" v-decorator.trim="[ 'remark' ]" />
          </a-form-item>
        </a-form>
      </a-spin>
      <select-product-supplier ref="selectProductSupplierForm" @ok="selectProductSupplierFormOk"></select-product-supplier>
      <select-material ref="selectMaterialForm" @ok="selectMaterialFormOk"></select-material>
    </a-modal>
  </div>
</template>
<script>
  import pick from 'lodash.pick'
  import {addMaterialBom,editMaterialBom,checkMaterialAttribute } from '@/api/api'
  import SelectProductSupplier from '@/views/bill/dialog/SelectProductSupplier'
  import SelectMaterial from '@/views/bill/dialog/SelectMaterial'
  import {mixinDevice} from '@/utils/mixin'
  export default {
    name: "MaterialBomModal",
    mixins: [mixinDevice],
    components: {
      SelectProductSupplier,
      SelectMaterial
    },
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
        validatorRules:{
          parent: {rules: [{required: true, message: '请选择归属总成!'}]},
          barCode: {rules: [{required: true, message: '请输入物料编码!'}]},
          project: {rules: [{required: true, message: '请输入项目!'}]},
          source: {rules: [{required: true, message: '请选择状态!'}]}
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
          this.form.setFieldsValue(pick(this.model, 'project', 'parent', 'barCode', 'source', 'remark'))
        });
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      selectProductSupplierFormOk(ids) {
        this.form.setFieldsValue({
          'parent': ids
        })
      },
      handleSelectProductSupplier () {
        console.log("handleSelectProductSupplier")
        this.$refs.selectProductSupplierForm.add();
        this.$refs.selectProductSupplierForm.title = "选择归属总成";
        this.$refs.selectProductSupplierForm.disableSubmit = false;
      },
      selectMaterialFormOk(ids) {
        this.form.setFieldsValue({
          'barCode': ids
        })
      },
      handleSelectMaterial () {
        console.log("handleSelectMaterial")
        this.$refs.selectMaterialForm.add();
        this.$refs.selectMaterialForm.title = "选择物料编码";
        this.$refs.selectMaterialForm.disableSubmit = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let formData = Object.assign(this.model, values);
            let obj;
            if(!this.model.id){
              obj=addMaterialBom(formData);
            }else{
              obj=editMaterialBom(formData);
            }
            obj.then((res)=>{
              if(res.code === 200){
                that.$emit('ok');
              }else{
                that.$message.warning(res.data.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })
          }
        })
      },
      handleCancel () {
        this.close()
      }
    }
  }
</script>
<style scoped>

</style>