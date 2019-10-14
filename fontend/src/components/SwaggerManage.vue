<template>

  <div style="padding: 0px 35px;">
    <Tabs value="custom">
      <TabPane label="Custom API" name="custom">
        <div style="padding: 40px 0px;">
          <Row>
            <Col span="12" offset="0">
              <Input v-model="searchKey" placeholder="search api ..." style="width: 100%"/>
            </Col>
            <Col span="3" offset="1">
              <Button @click="getApiList" type="info" long>Search</Button>
            </Col>
          </Row>
        </div>
        <Table border :columns="columns" :data="apiList"></Table>
        <br>
        <br>
        <br>
        <Page style="float: right;margin-right: 45px;" :total="total" :current="page" @on-change="changePage"/>
      </TabPane>
      <TabPane label="Swagger API" name="swagger">
        <swagger-api-list @show="show" v-if="swaggerData" :source="swaggerData"></swagger-api-list>
      </TabPane>
    </Tabs>

    <Drawer title="API Swagger Json Data" placement="left" :closable="false" v-model="apiDisplay.show" width="60">
      <div v-if="apiDisplay.history.length > 0" style="width: fit-content;" @click="goBackDisplay">
        <Icon type="md-arrow-back" style="font-size: 24px;cursor: pointer;color: darkred;"/>
      </div>
      <pre v-html="apiDisplay.content" @click="handleApiContentClick($event)"></pre>
    </Drawer>

    <Modal v-model="sharModal.show" draggable scrollable title="Share Your API">
      <div>
        <p>{{sharModal.url}}</p>
        <br>
        <a v-clipboard:copy="sharModal.url" v-clipboard:success="copyShareUrl">Copy To ClipBoard</a>
      </div>
    </Modal>

  </div>

</template>

<script>

  import Global from '@/components/Global.vue'
  import SwaggerApiList from "./SwaggerApiList";

  export default {
    name: "SwaggerManage",
    components: {SwaggerApiList},
    data() {
      return {
        user: null,
        searchKey: '',
        page: 1,
        size: 10,
        total: 0,
        apiList: [],
        columns: [
          {
            type: 'index',
            width: 60,
            align: 'center'
          },
          {
            title: 'API Method',
            key: 'method',
            width: 150,
            render: (h, params) => {
              var color = 'rgb(15,106,180)';
              if ('post' == params.row.method) {
                color = 'rgb(16,165,74)'
              }
              if ('put' == params.row.method) {
                color = 'rgb(255,173,51)'
              }
              if ('delete' == params.row.method) {
                color = 'rgb(237,64,20)'
              }
              return h('div', [
                h('strong', {
                  style: {
                    'background-color': color,
                    'color': '#fff',
                    'padding': '10px',
                  }
                }, params.row.method.toUpperCase())
              ]);
            }
          },
          {
            title: 'Operation ID',
            key: 'name',
            render: (h, params) => {
              return h('div', [
                h('a', {
                  props: {
                    type: 'person'
                  },
                  style: {
                    'font-size': '16px'
                  },
                  on: {
                    click: () => {
                      this.goApiEditPage(params.index)
                    }
                  }
                }, params.row.name)
              ]);
            }
          },
          {
            title: 'API Route',
            key: 'path'
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
            },
            width: 150
          },
          {
            title: 'Action',
            key: 'action',
            width: 380,
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
                      this.show(params.index)
                    }
                  }
                }, 'API'),
                h('Button', {
                  props: {
                    type: 'info',
                    size: 'small'
                  },
                  style: {
                    marginRight: '5px'
                  },
                  on: {
                    click: () => {
                      this.showDoc(params.index)
                    }
                  }
                }, 'DOC'),
                h('Button', {
                  props: {
                    type: 'info',
                    size: 'small',
                    ghost: ''
                  },
                  style: {
                    marginRight: '5px'
                  },
                  on: {
                    click: () => {
                      this.openShareModal(params.index)
                    }
                  }
                }, 'Share'),
                h('Button', {
                  props: {
                    type: 'success',
                    size: 'small',
                    ghost: ''
                  },
                  style: {
                    marginRight: '5px'
                  },
                  on: {
                    click: () => {
                      this.copyAPI(params.index)
                    }
                  }
                }, 'Copy'),
                h('Button', {
                  props: {
                    type: 'default',
                    size: 'small'
                  },
                  style: {
                    marginRight: '5px'
                  },
                  on: {
                    click: () => {
                      this.goApiTestPage(params.index)
                    }
                  }
                }, 'Test'),
                h('Button', {
                  props: {
                    type: 'error',
                    size: 'small'
                  },
                  on: {
                    click: () => {
                      this.remove(params.index)
                    }
                  }
                }, 'Delete')
              ]);
            }
          }
        ],
        swaggerData: null,
        selfSchema: {},
        apiDisplay: {
          show: false,
          content: '',
          history: [],
        },
        sharModal: {
          show: false,
          url: ''
        }
      }
    },
    methods: {
      copyShareUrl() {
        this.$Message.success('Copy Success!');
      },
      openShareModal(index) {
        var data = this.apiList[index];
        this.sharModal.show = true;
        this.sharModal.url = "http://" + location.host + "/#/swagger/share/profile/" + data._id;
      },
      copyAPI(index) {
        var data = this.apiList[index];
        sessionStorage.setItem('api-copy', JSON.stringify(data))
        this.$router.push({
          path: '/swagger/editor'
        })
      },
      goApiEditPage(index) {
        var data = this.apiList[index];
        this.$router.push({
          path: '/swagger/editor/' + data._id
        })
      },
      getSwagger() {
        this.$http.get(Global.SERVER_ROOT + '/swaggerTool/newApi/swagger/resource').then((resp) => {
          this.swaggerData = resp.body;
          this.freashSchemaList();
        }, (error) => {

        });
      },
      freashSchemaList() {
        this.$http.get(Global.SERVER_ROOT + '/swaggerTool/newApi/schema/list?userId=' + this.user.id).then((resp) => {
          if (resp.body && resp.body.length > 0) {
            for (var i = 0; i < resp.body.length; i++) {
              var schema = JSON.parse(resp.body[i].content);
              for (var key in schema) {
                this.selfSchema[key] = schema[key];
              }
            }
            console.log(this.selfSchema);
          }
        }, (error) => {

        });
      },
      getUser() {
        this.user = JSON.parse(Global.COOKIE.get(Global.SESSION_KEY));
        this.getApiList();
        this.getSwagger();
      },
      // 加载api列表
      getApiList() {
        var url = Global.SERVER_ROOT + '/swaggerTool/apiManage/api/list?userId=' + this.user.id + '&pageNum=' + this.page + '&pageSize=' + this.size;
        if (this.searchKey) {
          url += '&searchKey=' + this.searchKey;
        }
        this.$http.get(url).then((resp) => {
          this.total = resp.body.totalCount;
          this.apiList = resp.body.datas;
          console.log(this.apiList);
        }, (error) => {
        });
      },
      changePage(page) {
        this.page = page;
        this.getApiList();
      },
      show(index, source) {
        var data = null;
        var content = null;
        if (source) {
          // 这里的source是从swagger本身的API列表里传进来的
          data = source;
        } else {
          data = this.apiList[index];
        }
        content = JSON.stringify(JSON.parse(data.content), null, 4);
        // API的json内容中找到#/definitions/ 将后面的schema增加<a>标签
        if (data.parameters && data.parameters.length > 0) {
          for (var i = 0; i < data.parameters.length; i++) {
            var param = data.parameters[i];
            if (param.type == 'object' || param.type == 'array') {
              content = content.replace('#/definitions/' + param.schema, '#/definitions/<a class="schema_object" data-id="' + param.schema + '">' + param.schema + '</a>');
            }
          }
        }
        if (data.response && data.response != 'Nothing') {
          content = content.replace('#/definitions/' + data.response, '#/definitions/<a class="schema_object" data-id="' + data.response + '">' + data.response + '</a>');
        }
        this.apiDisplay.history = [];
        this.apiDisplay.content = content;
        this.apiDisplay.show = true;
      },
      showDoc(index) {
        var data = this.apiList[index];
        console.log(data)
        var content = '## 1. ' + data.desc + '<br><br>';
        content += data.method.toUpperCase() + '  `' + data.path + '`<br><br>';
        content += '请求<br>';
        content += '```<br>';
        if (data.parameters && data.parameters.length > 0) {
          for (var i = 0; i < data.parameters.length; i++) {
            var param = data.parameters[i];
            if (param.type == 'object') {
              content += this.genApiSchemaDoc(param.schema);
            } else if (param.type == 'array') {

            } else {
              var pdescstr = '';
              if (param.desc)
                pdescstr = param.desc;
              content += '  private ' + param.type + ' ' + param.name + '&nbsp;&nbsp;&nbsp;&nbsp;//  ' + pdescstr + '<br>';
            }
          }
        }
        content += '```<br><br>';
        content += '响应<br>';
        if (data.response == 'Nothing') {
          content += '<br><br>无';
        } else {
          content += '```<br>';
          content += this.genApiSchemaDoc(data.response);
        }
        content += '<br>```<br>';
        this.apiDisplay.history = [];
        this.apiDisplay.content = content;
        this.apiDisplay.show = true;
      },
      genApiSchemaDoc(schema) {
        var content = schema + ' {';
        var obj = this.findSchema(schema);
        if (obj) {
          var val = obj.val;
          var subObjs = [];
          if (val.properties) {
            for (var key in val.properties) {
              var paramItem = val.properties[key];
              var descStr = '';
              if (paramItem.description) {
                descStr = paramItem.description;
              }
              if (paramItem.type) {  // 这里可能是string等普通字段，也可能是array
                if (paramItem.type == 'array') {
                  var arrObj = paramItem.items;
                  if (arrObj.$ref) {  // 数组元素类型为对象
                    subObjs.push(arrObj);
                    content += '<br>' + 'private List&#60;' + arrObj.$ref.replace('#/definitions/', '') + '&#62; ' + key + ';&nbsp;&nbsp;// ' + descStr;
                  } else {  // 数据元素类型为基本数据类型
                    content += '<br>' + 'private List&#60;' + this.getSchemaTypeStr(arrObj.type) + '&#62; ' + key + ';&nbsp;&nbsp;// ' + descStr;
                  }
                } else {
                  // 这里肯定是普通类型（string、integer等）
                  content += '<br>' + 'private ' + this.getSchemaTypeStr(paramItem.type) + ' ' + key + ';&nbsp;&nbsp;// ' + descStr;
                }
              } else {
                // 这里只可能是object
                subObjs.push(paramItem);
                content += '<br>' + 'private ' + paramItem.$ref.replace('#/definitions/', '') + ' ' + key + ';&nbsp;&nbsp;// ' + descStr;
              }
            }
            content += '<br>}<br>';
            // 再把参数种依赖的对象doc追加再下面
            for (var i = 0; i < subObjs.length; i++) {
              var paramItemSchemaName = subObjs[i].$ref.replace('#/definitions/', '');
              if (schema == paramItemSchemaName)
                continue; // 这里是避免递归调用时遇到重复对象造成死循环
              content += '<br>' + this.genApiSchemaDoc(paramItemSchemaName);
            }
          }
        }
        return content;
      },
      getSchemaTypeStr(type) {
        var typeStr = '';
        if (type == 'string') {
          // TODO: 之后这里还要区分下Double等字段
          typeStr = 'String';
        } else if (type == 'integer') {
          typeStr = 'Integer';
        } else if (type == 'double') {
          typeStr = 'Double';
        }
        return typeStr;
      },
      genApiDocContent(name) {
        var content = '';
        var respSchema = this.findSchema(name);
        if (respSchema) {
          content += name + ' {<br>';
        } else {
          return content;
        }
        var properties = respSchema.val.properties;
        var depObjs = [];
        if (properties) {
          for (var key in properties) {
            var param = properties[key];
            var descStr = (param.description == undefined || param.description == null) ? '' : param.description;
            if (param["$ref"]) {
              content += '  private ' + param["$ref"].replace('#/definitions/', '') + ' ' + key + '&nbsp;&nbsp;&nbsp;&nbsp;// ' + descStr + '<br>';
            } else if (param["type"] == 'array') {
              content += '  private List&#60;' + param["items"]['$ref'].replace('#/definitions/', '') + '&#62; ' + key + '&nbsp;&nbsp;&nbsp;&nbsp;// ' + descStr + '<br>';
              depObjs.push(param["items"]['$ref'].replace('#/definitions/', ''));
            } else {
              content += '  private ' + param["type"] + ' ' + key + '&nbsp;&nbsp;&nbsp;&nbsp;// ' + descStr + '<br>';
            }
          }
        }
        content += ' }<br>';
        if (depObjs.length > 0) {
          for (var i = 0; i < depObjs.length; i++) {
            if (name == depObjs[i])
              continue;
            else
              content += this.genApiDocContent(depObjs[i], content);
          }
        }
        return content;
      },
      handleApiContentClick(event) {
        if (event.target.className == 'schema_object') {
          this.openSchemaModal(event.target.dataset.id);
        }
      },
      openSchemaModal(name) {
        var schema = this.findSchema(name);
        if (schema) {
          console.log(schema)
          var data = {};
          data[schema.key] = schema.val;
          var content = JSON.stringify(data, null, 4);
          var properties = schema.val.properties;
          if (properties) {
            for (var key in properties) {
              var param = properties[key];
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
          this.apiDisplay.history.push(this.apiDisplay.content);
          this.apiDisplay.content = content;
          this.apiDisplay.show = true;
        }
      },
      goBackDisplay() {
        this.apiDisplay.content = this.apiDisplay.history[this.apiDisplay.history.length - 1];
        var arr = [];
        for (var i = 0; i < this.apiDisplay.history.length - 1; i++) {
          arr.push(this.apiDisplay.history[i])
        }
        this.apiDisplay.history = arr;
        this.apiDisplay.show = true;
      },
      remove(index) {
        var data = this.apiList[index];
        var r = confirm("Sure to delete API [" + data.name + " ] ? ");
        if (r == true) {
          this.$http.delete(Global.SERVER_ROOT + '/swaggerTool/apiManage/api/' + this.apiList[index]._id).then((resp) => {
            this.getApiList();
          }, (error) => {
          });
        }
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
        this.$Message.error('can not find this model!')
        return null;
      },
      goApiTestPage(index, source) {
        var data = source ? source : this.apiList[index];
        this.$router.push(
          {
            path: '/swagger/test/' + data._id
          }
        )
      },


    },
    created() {
      this.getUser();
    },

  }
</script>

<style scoped>

</style>
