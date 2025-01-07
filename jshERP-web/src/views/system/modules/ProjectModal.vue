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
      style="top:20%;height: 45%;">
      <template slot="footer">
        <a-button key="back" v-if="isReadOnly" @click="handleCancel">
          关闭
        </a-button>
      </template>
      <template>
        <a-button @click="handleSelectUser"><a-icon type="setting"/>选择权限用户</a-button>
      </template>
      <a-spin :spinning="confirmLoading">
        <a-form :form="form" id="projectModal">
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="名称">
            <a-input placeholder="请输入项目名称" v-decorator.trim="[ 'name', validatorRules.name]" />
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="状态">
            <a-input placeholder="请输入项目状态" v-decorator.trim="[ 'type' ]" />
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="客户">
            <a-select placeholder="选择客户" v-decorator="[ 'customer' ]" allow-clear
              :dropdownMatchSelectWidth="false" showSearch optionFilterProp="children">
              <a-select-option v-for="(item,index) in cusList" :key="index" :value="item.id">
                {{ item.supplier }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="用户">
            <a-input :readOnly="true" placeholder="请选择对此项目有权限的用户" v-decorator.trim="[ 'users' ]" />
          </a-form-item>
        </a-form>
      </a-spin>
      <select-user ref="selectUserForm" @ok="selectUserFormOk"></select-user>
    </a-modal>
  </div>
</template>
<script>
  import pick from 'lodash.pick'
  import { addProject,editProject,checkProject,findBySelectCus } from '@/api/api'
  import { autoJumpNextInput } from "@/utils/util"
  import SelectUser from '@/views/bill/dialog/SelectUser'
  import { mixinDevice } from '@/utils/mixin'
  export default {
    name: "ProjectModal",
    mixins: [mixinDevice],
    components: {
      SelectUser
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
        cusList: [],
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
          name:{
            rules: [
              { required: true, message: '请输入项目名称!'},
              { pattern: /^[^\\s]+(\\s+[^\\s]+)*$/, message: '前后不能有空格!'},
              { validator: this.validateProjectName}
          ]}
        },
      }
    },
    created () {
      findBySelectCus({}).then((res)=>{
        if(res) {
          this.cusList = res;
        }
      });
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
          this.form.setFieldsValue(pick(this.model, 'name', 'customer', 'users', 'type'))
          autoJumpNextInput('projectModal')
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
              obj=addProject(formData);
            }else{
              obj=editProject(formData);
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
      selectUserFormOk(ids) {
        this.form.setFieldsValue({
          'users': ids
        })
      },
      handleSelectUser () {
        console.log("handleSelectUser")
        this.$refs.selectUserForm.add(this.model.users);
        this.$refs.selectUserForm.title = "选择权限用户";
        this.$refs.selectUserForm.disableSubmit = false;
      },
      handleCancel () {
        this.close()
      },
      validateProjectName(rule, value, callback){
        let params = {
          name: value,
          id: this.model.id?this.model.id:0
        };
        checkProject(params).then((res)=>{
          if(res && res.code===200) {
            if(!res.data.status){
              callback();
            } else {
              callback("名称已经存在");
            }
          } else {
            callback(res.data);
          }
        });
      }
    }
  }
</script>
<style scoped>

</style>