<template>

  <div style="padding: 0px 35px;">

    <Tabs value="custom">
      <TabPane label="Custom Schema" name="custom">
        <div style="padding: 40px 0px;">
          <Row>
            <Col span="3">
              <Button type="dashed" @click="openNewSchemaModal" long>Create Schema +</Button>
            </Col>
            <Col span="12" offset="5">
              <Input v-model="searchKey" placeholder="search by schema content ..." style="width: 100%"/>
            </Col>
            <Col span="3" offset="1">
              <Button @click="getSchemaList" type="info" long>Search</Button>
            </Col>
          </Row>
        </div>
        <Table border :columns="columns" :data="schemaData"></Table>
        <br>
        <br>
        <Page style="float: right;margin-right: 45px;" :total="total" :current="page" @on-change="changePage"/>
      </TabPane>
      <TabPane label="Swagger Schema" name="swagger">
        <div style="padding: 40px 0px;">
          <Row>
            <Col span="12" offset="0">
              <Input v-model="swaggerSchemaSearchKey" @on-keyup="enterSwaggerSchemaSearch" placeholder="search by schema content ..." style="width: 100%"/>
            </Col>
            <Col span="3" offset="1">
              <Button @click="getSwagerSchemaList" type="info" long>Search</Button>
            </Col>
          </Row>
        </div>
        <Table border :columns="swaggerColumns" :data="swaggerSchemaShowList"></Table>
        <br>
        <br>
        <Page v-show="!swaggerSchemaSearchKey" style="float: right;margin-right: 45px;" :total="swaggerTotal" :current="swaggerPage"
              @on-change="changeSwaggerPage"/>
      </TabPane>
    </Tabs>

    <Drawer title="Swagger schema Json Data" placement="left" :closable="false" v-model="schemaDisplay.show" width="60">
      <div v-if="schemaDisplay.history.length > 0" style="width: fit-content;" @click="goBackDisplay">
        <Icon type="md-arrow-back" style="font-size: 24px;cursor: pointer;color: darkred;"/>
      </div>
      <pre v-html="schemaDisplay.content" @click="handleApiContentClick($event)"></pre>
    </Drawer>


    <Modal
      v-model="newSchemaModal.show"
      :width="1000"
    >
      <p slot="header" style="color:green;text-align:center">
        <Icon type="ios-albums"/>
        <span>Create A New Schema Object</span>
      </p>
      <div style="text-align:left">
        <br>
        <div>
          <label>Name *</label>
          <Input v-model="newSchemaModal.name" placeholder="" style="width: 70%;margin-left: 38px;"></Input>
        </div>
        <br>
        <div>
          <label>Description</label>
          <Input v-model="newSchemaModal.desc" type="textarea" :rows="4" placeholder=""
                 style="width: 70%;margin-left: 18px;"/>
        </div>
        <br>
        <br>
        <div>
          <Button type="info" @click="createNewSchemaParameter">Create Parameter +</Button>
          <div v-if="newSchemaModal.parameters.length > 0" style="margin-top: 25px;">
            <Row :gutter="16">
              <Col span="1">
                #
              </Col>
              <Col span="5">
                Param Name *
              </Col>
              <Col span="3">
                Param Type *
              </Col>
              <Col span="5">
                Schema Model
              </Col>
              <Col span="5">
                Param Descriptions
              </Col>
              <Col span="4" offset="1">
                Param Operation
              </Col>
            </Row>
          </div>
          <div style="margin-top: 20px;" v-for="(param, index) in newSchemaModal.parameters">
            <Row :gutter="16" style="margin-bottom: 10px;">
              <Col span="1">
                {{index+1}}
              </Col>
              <Col span="5">
                <Input v-model="param.name" placeholder="" style="width: 150px;"></Input>
              </Col>
              <Col span="3">
                <Select v-model="param.type" style="width:100px">
                  <Option v-for="item in paramCategoryList" :value="item.value" :key="item.value">{{ item.label }}
                  </Option>
                </Select>
              </Col>
              <Col span="5">
                <AutoComplete
                  v-if="param.type == 'object' || param.type == 'array'"
                  :filter-method="schemaFilterMethod"
                  :data="schemaList"
                  v-model="param.schema"
                  placeholder="input here for search"
                  style="width:168px">
                </AutoComplete>
                <span v-else> - </span>
              </Col>
              <Col span="5">
                <Input v-model="param.desc" type="textarea" :rows="4" placeholder=""/>
              </Col>
              <Col span="4" offset="1">
                <Button type="error" @click="removeSchemaParam(index)">Delete</Button>
              </Col>
            </Row>

          </div>
        </div>
        <br>
      </div>
      <div slot="footer">
        <Button type="info" size="large" long @click="createSchemaObject">Submit</Button>
      </div>
    </Modal>


  </div>

</template>

<script>

  import Global from '@/components/Global.vue'

  export default {
    name: "SwaggerSchemaManage",
    data() {
      return {
        swaggerData: null,
        selfSchema: {},
        schemaData: [],
        user: null,
        columns: [
          {
            type: 'index',
            width: 60,
            align: 'center'
          },
          {
            title: 'Schema Name',
            key: 'name',
            width: 300,
            render: (h, params) => {
              var color = '#000';
              if (true == params.row.isSwagger) {
                color = '#71C671';
                return h('div', [
                  h('strong', {
                    style: {
                      'color': color,
                    }
                  }, params.row.name),
                  h('Tooltip', {
                    props: {
                      'content': 'Already defended in swagger.json'
                    },
                    style: {
                      'margin-left': '8px',
                      'cursor': 'pointer'
                    },
                  }, [
                    h('Icon', {
                      props: {
                        'type': 'ios-help-circle'
                      },
                      style: {
                        'font-size': '12px'
                      }
                    }, '?'),
                  ]),
                ]);
              } else {
                return h('div', [
                  h('strong', {
                    style: {
                      'color': color,
                    }
                  }, params.row.name),
                ]);
              }
            }
          },
          {
            title: 'Description',
            key: 'desc'
          },
          {
            title: 'Create Time',
            render: (h, params) => {
              var timeStr = new Date(params.row.createTime).Format('yyyy-MM-dd hh:mm:ss')
              return h('div', [
                h('span', timeStr)
              ]);
            }
          },
          {
            title: 'Action',
            key: 'action',
            width: 250,
            align: 'center',
            render: (h, params) => {
              return h('div', [
                h('Button', {
                  props: {
                    type: 'warning',
                    size: 'small'
                  },
                  style: {
                    marginRight: '5px'
                  },
                  on: {
                    click: () => {
                      this.editSchema(params.index)
                    }
                  }
                }, 'Modify'),
                h('Button', {
                  props: {
                    type: 'primary',
                    size: 'small'
                  },
                  style: {
                    marginRight: '5px'
                  },
                  on: {
                    click: () => {
                      this.show(params.index)
                    }
                  }
                }, 'Detail'),
                h('Button', {
                  props: {
                    type: 'error',
                    size: 'small'
                  },
                  style: {
                    marginRight: '5px'
                  },
                  on: {
                    click: () => {
                      this.deleteSchema(params.index)
                    }
                  }
                }, 'Delete'),
              ]);
            }
          }

        ],
        swaggerColumns: [
          {
            type: 'index',
            width: 60,
            align: 'center'
          },
          {
            title: 'Schema Name',
            key: 'name',
            width: 300,
            render: (h, params) => {
              var color = '#000';
              if (true == params.row.isSwagger) {
                color = '#71C671';
                return h('div', [
                  h('strong', {
                    style: {
                      'color': color,
                    }
                  }, params.row.name),
                  h('Tooltip', {
                    props: {
                      'content': 'Already defended in swagger.json'
                    },
                    style: {
                      'margin-left': '8px',
                      'cursor': 'pointer'
                    },
                  }, [
                    h('Icon', {
                      props: {
                        'type': 'ios-help-circle'
                      },
                      style: {
                        'font-size': '12px'
                      }
                    }, '?'),
                  ]),
                ]);
              } else {
                return h('div', [
                  h('strong', {
                    style: {
                      'color': color,
                    }
                  }, params.row.name),
                ]);
              }
            }
          },
          {
            title: 'Description',
            key: 'desc'
          },
          {
            title: 'Create Time',
            render: (h, params) => {
              return h('div', [
                h('span', params.row.createTime)
              ]);
            }
          },
          {
            title: 'Action',
            key: 'action',
            width: 250,
            align: 'center',
            render: (h, params) => {
              return h('div', [
                h('Button', {
                  props: {
                    type: 'primary',
                    size: 'small'
                  },
                  style: {
                    marginRight: '5px'
                  },
                  on: {
                    click: () => {
                      this.showSwaggerSchema(params.row.name)
                    }
                  }
                }, 'Detail'),
              ]);
            }
          }

        ],
        swaggerTotal: 0,
        swaggerPage: 1,
        swaggerSize: 10,
        swaggerSchemaList: [],
        swaggerSchemaShowList: [],
        swaggerSchemaSearchKey: '',
        schemaDisplay: {
          show: false,
          content: '',
          history: [],
        },
        total: 0,
        size: 10,
        page: 1,
        searchKey: '',
        newSchemaModal: {
          show: false,
          name: '',
          desc: '',
          parameters: [],
        },
        paramCategoryList: [
          {
            label: 'string',
            value: 'string'
          },
          {
            label: 'integer',
            value: 'integer'
          },
          {
            label: 'double',
            value: 'double'
          },
          {
            label: 'float',
            value: 'float'
          },
          {
            label: 'date-time',
            value: 'date-time'
          },
          {
            label: 'object',
            value: 'object'
          },
          {
            label: 'array',
            value: 'array'
          }
        ],
        schemaList: [],

      }
    },
    methods: {
      enterSwaggerSchemaSearch(event) {
        if(event.code == 'Enter') {
          this.getSwagerSchemaList()
        }
      },
      getSwagerSchemaList() {
        this.swaggerSchemaShowList = [];
        if (this.swaggerSchemaSearchKey) {
          var reg = new RegExp(this.swaggerSchemaSearchKey, 'i');
          for (var i = 0; i < this.swaggerSchemaList.length; i++) {
            var schema = this.swaggerSchemaList[i];
            if(schema.name.match(reg)){
              this.swaggerSchemaShowList.push({
                name: schema.name,
                desc: '-',
                createTime: '-',
              });
            }
          }
          return;
        }
        var skip = (this.swaggerPage - 1) * this.swaggerSize;
        if (skip > this.swaggerSchemaList.length) {
          return;
        }
        for (var i = skip; i < this.swaggerPage * this.swaggerSize; i++) {
          this.swaggerSchemaShowList.push(this.swaggerSchemaList[i]);
        }
      },
      deleteSchema(index) {
        var data = this.schemaData[index];
        var r = confirm("Sure to delete this schema model ? ")
        if (r == true) {
          this.$http.delete(Global.SERVER_ROOT + '/schemaManage/schema?id=' + data._id).then((resp) => {
            this.getSchemaList();
            this.freashSchemaList();
            this.$Message.success('delete succeed!')
          }, (error) => {
          });
        }
        else {
        }
      },
      editSchema(index) {
        var data = this.schemaData[index];
        this.openNewSchemaModal(data);
      },
      schemaFilterMethod(value, option) {
        if (!value)
          return false;
        else
          return option.toUpperCase().indexOf(value.toUpperCase()) !== -1;
      },
      createNewSchemaParameter() {
        this.newSchemaModal.parameters.push({
          name: '',
          schema: null,
          desc: '',
          type: 'string',
        });
      },
      removeSchemaParam(index) {
        var datas = [];
        for (var i = 0; i < this.newSchemaModal.parameters.length; i++) {
          if (index != i) {
            datas.push(this.newSchemaModal.parameters[i]);
          }
        }
        this.newSchemaModal.parameters = datas;
      },
      openNewSchemaModal(data) {
        if (data._id) {
          this.newSchemaModal = {
            _id: data._id,
            show: true,
            name: data.name,
            desc: data.desc,
            parameters: data.parameters,
          }
        } else {
          this.newSchemaModal = {
            show: true,
            name: '',
            desc: '',
            parameters: [],
          }
        }
      },
      createSchemaObject() {
        // 校验参数合法性
        if (!this.newSchemaModal.name) {
          this.$Message.error('Please Enter Schema Object Name!')
          return;
        }
        if (this.newSchemaModal.parameters && this.newSchemaModal.parameters.length > 0) {
          for (var i = 0; i < this.newSchemaModal.parameters.length; i++) {
            var param = this.newSchemaModal.parameters[i];
            if (!param.name) {
              this.$Message.error('Please Enter Param [' + (i + 1) + ']\'s Name!')
              return;
            }
          }
        }
        // 组装Schema数据结构
        var data = {};
        data[this.newSchemaModal.name] = {
          "type": "object",
          "description": this.newSchemaModal.desc,
        };
        if (this.newSchemaModal.parameters && this.newSchemaModal.parameters.length > 0) {
          var properties = {};
          for (var i = 0; i < this.newSchemaModal.parameters.length; i++) {
            var param = this.newSchemaModal.parameters[i];
            if (param.type == 'object') {
              properties[param.name] = {
                "$ref": "#/definitions/" + param.schema,
                "description": param.desc
              };
            } else if (param.type == 'array') {
              properties[param.name] = {
                "type": "array",
                "items": {
                  "$ref": "#/definitions/" + param.schema
                },
                "description": param.desc
              };
            } else if (param.type == 'string') {
              properties[param.name] = {
                "type": "string",
                "description": param.desc
              };
            } else if (param.type == 'integer') {
              properties[param.name] = {
                "type": "integer",
                "description": param.desc
              };
            } else if (param.type == 'double') {
              properties[param.name] = {
                "type": "number",
                "format": "double",
                "description": param.desc
              };
            } else if (param.type == 'float') {
              properties[param.name] = {
                "type": "number",
                "format": "float",
                "description": param.desc
              };
            } else if (param.type == 'date-time') {
              properties[param.name] = {
                "type": "string",
                "format": "date-time",
                "description": param.desc
              };
            }
          }
          data[this.newSchemaModal.name]['properties'] = properties;
        }
        // 后台存入数据库
        var userId = this.user.id;
        var queryParam = {
          userId: userId,
          content: JSON.stringify(data),
          name: this.newSchemaModal.name,
          desc: this.newSchemaModal.desc,
          parameters: this.newSchemaModal.parameters
        };
        if (this.newSchemaModal._id) {
          queryParam._id = this.newSchemaModal._id;
        }
        var url = this.newSchemaModal._id ? Global.SERVER_ROOT + '/schemaManage/schema' : Global.SERVER_ROOT + '/swaggerTool/newApi/schema';
        this.$http.put(url, queryParam).then((resp) => {
          if (resp.body.code == -1) {
            this.$Message.error(resp.body.msg)
          } else {
            // 刷新Schema列表
            this.freashSchemaList();
            this.getSchemaList();
            this.$Message.success('Create Succeed!')
            // 关闭modal
            this.newSchemaModal.show = !this.newSchemaModal.show;
          }
        }, (error) => {
          this.$Message.error('Net Error!')
        });
      },
      freashSchemaList() {
        this.$http.get(Global.SERVER_ROOT + '/swaggerTool/newApi/schema/list?userId=' + this.user.id).then((resp) => {
          console.log(resp.body);
          if (resp.body && resp.body.length > 0) {
            for (var i = 0; i < resp.body.length; i++) {
              var schema = JSON.parse(resp.body[i].content);
              for (var key in schema) {
                this.schemaList.push(key);
              }
            }
          }
        }, (error) => {

        });
      },
      changePage(page) {
        this.page = page;
        this.getSchemaList();
      },
      changeSwaggerPage(page) {
        this.swaggerPage = page;
        this.getSwagerSchemaList()
      },
      getSchemaList() {
        var url = Global.SERVER_ROOT + '/schemaManage/list?userId=' + this.user.id + '&pageSize=' + this.size + '&page=' + this.page;
        if (this.searchKey) {
          url += '&searchKey=' + this.searchKey;
        }
        this.$http.get(url).then((resp) => {
          this.schemaData = [];
          if (resp.body && resp.body.datas.length > 0) {
            this.total = resp.body.total;
            for (var i = 0; i < resp.body.datas.length; i++) {
              var data = resp.body.datas[i];
              // 筛选出与swagger重复的对象，标记一下
              if (this.swaggerData.definitions[data.name]) {
                data.isSwagger = true;
              } else {
                data.isSwagger = false;
              }
              this.schemaData.push(data);
              // 组装selfSchema对象
              var schema = JSON.parse(resp.body.datas[i].content);
              for (var key in schema) {
                this.selfSchema[key] = schema[key];
              }
            }
          }
        }, (error) => {

        });
      },
      getSwagger() {
        this.$http.get(Global.SERVER_ROOT + '/swaggerTool/newApi/swagger/resource').then((resp) => {
          this.swaggerData = resp.body;
          for (var key in this.swaggerData.definitions) {
            this.schemaList.push(key);
            var swaggerSchema = {
              name: key,
              desc: '-',
              createTime: '-'
            };
            this.swaggerSchemaList.push(swaggerSchema)
          }
          this.swaggerTotal = this.swaggerSchemaList.length;
          this.getSwagerSchemaList();
          this.getSchemaList();
          this.freashSchemaList();
        }, (error) => {

        });
      },
      getUser() {
        this.user = JSON.parse(Global.COOKIE.get(Global.SESSION_KEY));
        console.log(this.user)
        this.getSwagger();
      },
      show(index) {
        var data = this.schemaData[index];
        var content = JSON.stringify(JSON.parse(data.content), null, 4);
        if (data.parameters && data.parameters.length > 0) {
          for (var i = 0; i < data.parameters.length; i++) {
            var param = data.parameters[i];
            if (param.type == 'object' || param.type == 'array') {
              content = content.replace('#/definitions/' + param.schema, '#/definitions/<a class="schema_object" data-id="' + param.schema + '">' + param.schema + '</a>');
            }
          }
        }
        this.schemaDisplay.history = [];
        this.schemaDisplay.content = content;
        this.schemaDisplay.show = true;
      },
      showSwaggerSchema(name) {
        var data = this.findSchema(name);
        var _data = {};
        _data[name] = data.val;
        var content = JSON.stringify(_data, null, 4);
        var parameters = _data[name].properties;
        if (parameters) {
          for (var key in parameters) {
            var param = parameters[key];
            if (param.$ref) {
              content = content.replace(param.$ref, '#/definitions/<a class="schema_object" data-id="' + param.$ref.replace('#/definitions/', '') + '">' + param.$ref.replace('#/definitions/', '') + '</a>');
            } else if (param.type == 'array') {
              if (param.items.$ref) {
                var key2 = param.items.$ref.replace('#/definitions/', '');
                content = content.replace(param.items.$ref, '#/definitions/<a class="schema_object" data-id="' + key2 + '">' + key2 + '</a>');
              }
            }
          }
        }
        this.schemaDisplay.history = [];
        this.schemaDisplay.content = content;
        this.schemaDisplay.show = true;
      },
      handleApiContentClick(event) {
        if (event.target.className == 'schema_object') {
          this.openSchemaModal(event.target.dataset.id);
        }
      },
      openSchemaModal(name) {
        var schema = this.findSchema(name);
        if (schema) {
          var data = {};
          data[schema.key] = schema.val;
          var content = JSON.stringify(data, null, 4);
          var properties = schema.val.properties;
          if (properties) {
            for (var key in properties) {
              var param = properties[key];
              if (param.$ref) {
                var key1 = param.$ref.replace('#/definitions/', '');
                content = content.replace(param.$ref, '#/definitions/<a class="schema_object" data-id="' + key1 + '">' + key1 + '</a>');
              } else if (param.type == 'array') {
                if (param.items.$ref) {
                  var key2 = param.items.$ref.replace('#/definitions/', '');
                  content = content.replace(param.items.$ref, '#/definitions/<a class="schema_object" data-id="' + key2 + '">' + key2 + '</a>');
                }
              }
            }
          }
          this.schemaDisplay.history.push(this.schemaDisplay.content);
          this.schemaDisplay.content = content;
          this.schemaDisplay.show = true;
        }
      },
      goBackDisplay() {
        this.schemaDisplay.content = this.schemaDisplay.history[this.schemaDisplay.history.length - 1];
        var arr = [];
        for (var i = 0; i < this.schemaDisplay.history.length - 1; i++) {
          arr.push(this.schemaDisplay.history[i])
        }
        this.schemaDisplay.history = arr;
        this.schemaDisplay.show = true;
      },
      findSchema(name) {
        var schema = {};
        // 现在线上swagger中找
        if (this.swaggerData.definitions[name]) {
          schema.val = this.swaggerData.definitions[name];
          schema.key = name;
          schema.from = 'swagger';
          return schema;
        }
        // 在自己数据库定义的schema中找
        if (this.selfSchema[name]) {
          schema.val = this.selfSchema[name];
          schema.key = name;
          schema.from = 'database';
          return schema;
        }
        this.$Message.error('can not find this model ! ')
        return null;
      },


    },
    created() {
      this.getUser();
    },
  }
</script>

<style scoped>

</style>
