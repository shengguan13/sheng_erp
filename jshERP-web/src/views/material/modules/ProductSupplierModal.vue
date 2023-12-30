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
      <a-spin :spinning="confirmLoading">
        <a-form :form="form">
          <a-form-item label="客商名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-select placeholder="请选择客商" v-decorator="[ 'supplierId', validatorRules.supplierId ]"
              :dropdownMatchSelectWidth="false" showSearch optionFilterProp="children">
              <a-select-option v-for="(supplier,index) in supplierList" :value="supplier.id">
                {{ supplier.supplier }}
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item label="客商类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-select placeholder="请选择客商类型" v-decorator="[ 'supplierType', validatorRules.supplierType ]"
              :dropdownMatchSelectWidth="false" showSearch optionFilterProp="children">
              <a-select-option value="客户">客户</a-select-option>
              <a-select-option value="供应商">供应商</a-select-option>
            </a-select>
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="型号">
            <a-input placeholder="型号" v-decorator.trim="[ 'model', validatorRules.model]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物料编码">
            <a-input placeholder="物料编码" v-decorator.trim="[ 'barCode', validatorRules.barCode]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="采购周期">
            <a-input placeholder="采购周期" v-decorator.trim="[ 'purchaseCycle']" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="标包">
            <a-input placeholder="标包" v-decorator.trim="[ 'pack' ]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单位">
            <a-input placeholder="单位" v-decorator.trim="[ 'unit', validatorRules.unit ]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="未税单价">
            <a-input placeholder="未税单价" v-decorator.trim="[ 'priceNoTax' ]" />
          </a-form-item>
        </a-form>
        <a-form :form="form">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="税率">
            <a-input placeholder="税率" v-decorator.trim="[ 'taxRate' ]" />
          </a-form-item>
        </a-form>
      </a-spin>
    </a-modal>
  </div>
</template>
<script>
  import pick from 'lodash.pick'
  import {addProductSupplier,editProductSupplier } from '@/api/api'
  import {mixinDevice} from '@/utils/mixin'
  import { postAction } from '@api/manage'
  export default {
    name: "ProductSupplierModal",
    mixins: [mixinDevice],
    data () {
      return {
        title:"操作",
        visible: false,
        model: {},
        supplierList: [],
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
        form: this.$form.createForm(this),
        validatorRules:{
          supplierId:{
            rules: [
              { required: true, message: '请选择客商名称!' }
            ]
          },
          supplierType:{
            rules: [
              { required: true, message: '请选择客商种类!' }
            ]
          },
          model:{
            rules: [
              { required: true, message: '请输入型号!' }
            ]
          },
          barCode:{
            rules: [
              { required: true, message: '请输入物料编码!' }
            ]
          },
          unit:{
            rules: [
              { required: true, message: '请输入单位!' }
            ]
          }
        }
      }
    },
    created () {
      postAction('/supplier/findBySelect_sup').then((res)=>{
        this.supplierList = res;
      })
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
          this.form.setFieldsValue(pick(this.model, 'supplierId', 'supplierType', 'model',
          'barCode', 'purchaseCycle', 'pack', 'unit', 'priceNoTax', 'taxRate'))
        });
      },
      close () {
        this.$emit('close');
        this.visible = false;
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
              obj=addProductSupplier(formData);
            }else{
              obj=editProductSupplier(formData);
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