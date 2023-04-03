import { FormTypes, getListData } from '@/utils/JEditableTableUtil'
import { getMaterialByBarCode, getCurrentSystemConfig} from '@/api/api'
import { getAction } from '@/api/manage'
import { getMpListShort } from "@/utils/util"
import Vue from 'vue'

export const MaterialCompositeMixin = {
  data() {
    return {
      action: '',
      minWidth: 1100,
      spans: {
        labelCol1: {span: 2},
        wrapperCol1: {span: 22},
        //1_5: 分为1.5列（相当于占了2/3）
        labelCol1_5: { span: 3 },
        wrapperCol1_5: { span: 21 },
        labelCol2: {span: 4},
        wrapperCol2: {span: 20},
        labelCol3: {span: 6},
        wrapperCol3: {span: 18},
        labelCol6: {span: 12},
        wrapperCol6: {span: 12}
      },
    };
  },
  created () {
    let realScreenWidth = window.screen.width
    this.width = realScreenWidth<1500?'1200px':'1550px'
    this.minWidth = realScreenWidth<1500?1150:1500
  },
  methods: {
    /** 查询某个tab的数据 */
    requestSubTableData(url, params, tab, success) {
      tab.loading = true
      getAction(url, params).then(res => {
        if(res && res.code === 200){
          tab.dataSource = res.data.rows
          for(let i=0; i<tab.dataSource.length; i++){
            let info = tab.dataSource[i]
            this.changeColumnShow(info)
          }
          typeof success === 'function' ? success(res) : ''
        }
      }).finally(() => {
        tab.loading = false
      })
    },
    //改变字段的状态，1-显示 0-隐藏
    changeFormTypes(columns, key, type) {
      for(let i=0; i<columns.length; i++){
        if(columns[i].key === key) {
          if(type){
            columns[i].type = FormTypes.normal //显示
          } else {
            columns[i].type = FormTypes.hidden //隐藏
          }
        }
      }
    },
    initSystemConfig() {
      getCurrentSystemConfig().then((res) => {
        if(res.code === 200 && res.data){
          let multiBillType = res.data.multiBillType
          let multiLevelApprovalFlag = res.data.multiLevelApprovalFlag
        }
      })
    },
    onAddedMaterialComposite(event) {
      const { row, target } = event
      target.setValues([{rowKey: row.id, values: {operNumber:0}}])
    },
    //单元值改变一个字符就触发一次
    onValueChangeMaterialComposite(event) {
      let that = this
      const { type, row, column, value, target } = event
      let param,operNumber
      switch(column.key) {
        case "barCode":
          param = {
            barCode: value,
            mpList: getMpListShort(Vue.ls.get('materialPropertyList')),  //扩展属性
            prefixNo: this.prefixNo
          }
          getMaterialByBarCode(param).then((res) => {
            if (res && res.code === 200) {
              let mList = res.data
              if (value.indexOf(',') > -1) {
                //多个条码
              } else {
                let mArr = []
                let mInfo = mList[0]
                this.changeColumnShow(mInfo)
                let mInfoEx = this.parseInfoToObj(mInfo)
                let mObj = {
                  rowKey: row.id,
                  values: mInfoEx
                }
                mArr.push(mObj)
                target.setValues(mArr);
                target.recalcAllStatisticsColumns()
                target.autoSelectBySpecialKey('operNumber', row.orderNum)
              }
            }
          });
          break;
        case "operNumber":
          operNumber = value-0
          break;
      }
    },
    //转为商品对象
    parseInfoToObj(mInfo) {
      return {
        barCode: mInfo.mBarCode,
        name: mInfo.name,
        internalId: mInfo.internalId,
        model: mInfo.model,
        color: mInfo.color,
        materialOther: mInfo.materialOther,
        unit: mInfo.commodityUnit,
        sku: mInfo.sku,
        operNumber: 1,
      }
    },
    //使得型号、颜色编码、扩展信息、sku等为隐藏
    changeColumnHide() {
      this.changeFormTypes(this.materialTable.columns, 'model', 0)
      this.changeFormTypes(this.materialTable.columns, 'color', 0)
      this.changeFormTypes(this.materialTable.columns, 'materialOther', 0)
      this.changeFormTypes(this.materialTable.columns, 'sku', 0)
    },
    //使得sku、序列号、批号、到期日等为显示
    changeColumnShow(info) {
      if(info.model) {
        this.changeFormTypes(this.materialTable.columns, 'model', 1)
      }
      if(info.color) {
        this.changeFormTypes(this.materialTable.columns, 'color', 1)
      }
      if(info.materialOther) {
        this.changeFormTypes(this.materialTable.columns, 'materialOther', 1)
      }
      if(info.sku) {
        this.changeFormTypes(this.materialTable.columns, 'sku', 1)
      }
    },
    //删除一行或多行的时候触发
    onDeletedMaterialComposite(ids, target) {
    },
  }
}