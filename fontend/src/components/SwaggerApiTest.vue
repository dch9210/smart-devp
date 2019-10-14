<template>

  <div>

    <Row>
      <Col span="4">
        <div style="border-right: 1px solid #eee;height: 1024px;padding: 10px 15px;overflow: auto;">
          <h2>History</h2>
          <br>
          <div v-if="historyList.length <=0">
            No Test History...
          </div>
          <div v-else v-for="(history, index) in historyList">
            <Alert v-if="history.data.status == 0" type="success" :closable="true"
                   @on-close="deleteHistory(history.id, $event)">
              <span style="font-size: 16px;font-weight: bold;cursor: pointer;color: #2b85e4;" @click="recoverApiTestData(history)">#{{historyList.length - index}}</span>
              {{history.data.host}}
              <br>
              <span>{{history.data.mark}}</span>
              <br>
              <small style="font-size: 6px;color: #ddd777;">{{history.createTime | date}} &nbsp;&nbsp;{{history.data.consumeTime}}</small>
            </Alert>
            <Alert v-else type="error" :closable="true"
                   @on-close="deleteHistory(history.id, $event)">
              <span style="font-size: 16px;font-weight: bold;cursor: pointer;color: #2b85e4;" @click="recoverApiTestData(history)">#{{historyList.length - index}}</span>
              {{history.data.host}}
              <br>
              <small style="font-size: 6px;color: #ddd777;">{{history.createTime | date}} &nbsp;&nbsp;{{history.data.consumeTime}}</small>
            </Alert>
          </div>
        </div>
      </Col>
      <Col span="20">
        <div style="padding: 45px;" v-if="api">
          <Row>
            <Col span="1">
              <span style="font-size: 20px;">{{api.method.toUpperCase()}}</span>
            </Col>
            <Col span="3" offset="0" style="margin-left: 25px;">
              <Input v-model="host" placeholder="http://localhost:8080/v1"></Input>
            </Col>
            <Col span="15" style="margin-left: 25px;">
              <Input v-model="api.path" disabled>
              </Input>
            </Col>
            <Col span="3" offset="1">
              <Button type="primary" long :loading="loading" @click="sendTest">
                <span v-if="!loading">Go Test</span>
                <span v-else>Loading...</span>
              </Button>
            </Col>
          </Row>
          <br>
          <Input v-model="mark" type="textarea" :rows="3" placeholder="Write some descriptions..."/>
          <br><br>
          <Tabs value="name1">
            <TabPane label="Params" name="name1">
              <Row>
                <Col span="2">
                  <label>SELECT</label>
                </Col>
                <Col span="8">
                  <label>KEY</label>
                </Col>
                <Col span="8">
                  <label>VALUE</label>
                </Col>
                <Col span="6">
                  <label>DESCRIPTION</label>
                </Col>
              </Row>
              <div v-for="param in api.parameters">
                <Row v-if="param.type != 'object'"
                     style="padding: 10px 0;border-bottom: 1px solid #eee;border-top: 1px solid #eee;">
                  <Col span="2">
                    <Checkbox v-model="param.selected"></Checkbox>
                  </Col>
                  <Col span="8">
                    <span>{{param.name}}</span>
                  </Col>
                  <Col span="8">
                    <Input v-model="param.value" style="width: 90%"></Input>
                  </Col>
                  <Col span="6">
                    <span>{{param.desc}}</span>
                  </Col>
                </Row>
              </div>
            </TabPane>
            <TabPane label="Headers" name="name2">
              <Row>
                <Col span="2">
                  <label>SELECT</label>
                </Col>
                <Col span="8">
                  <label>KEY</label>
                </Col>
                <Col span="8">
                  <label>VALUE</label>
                </Col>
                <Col span="6">
                  <label>DESCRIPTION</label>
                </Col>
              </Row>
              <div v-for="header in headers"
                   style="padding: 10px 0;border-bottom: 1px solid #eee;border-top: 1px solid #eee;">
                <Row>
                  <Col span="2">
                    <Checkbox v-model="header.selected"></Checkbox>
                  </Col>
                  <Col span="8">
                    <Input v-model="header.name" style="width: 90%"></Input>
                  </Col>
                  <Col span="8">
                    <Input v-model="header.value" style="width: 90%"></Input>
                  </Col>
                  <Col span="6">
                    <Input v-model="header.desc" style="width: 90%"></Input>
                  </Col>
                </Row>
              </div>
            </TabPane>
            <TabPane label="Body" name="name3">
              <Input v-model="reqBody" type="textarea" :rows="20" placeholder=""/>
            </TabPane>
          </Tabs>

          <h2>Response</h2>
          <br>
          <div style="margin-top: 20px;background: #eee;padding: 20px;max-height: 800px;overflow: auto;">
            <h5>Consume Time: {{consumeTime}}</h5>
            <pre v-if="responseData">{{JSON.stringify(JSON.parse(responseData), null, 4)}}
           </pre>
          </div>
        </div>
      </Col>
    </Row>
  </div>

</template>

<script>

  import Global from '@/components/Global.vue'

  export default {
    name: "SwaggerApiTest",
    data() {
      return {
        loading: false,
        apiId: '',
        api: null,
        host: 'http://localhost:8082/v1',
        headers: [
          {
            selected: false,
            name: 'Authorization',
            value: '',
            desc: 'Authorization',
          },
          {
            selected: false,
            name: '',
            value: '',
            desc: '',
          },
          {
            selected: false,
            name: '',
            value: '',
            desc: '',
          },
          {
            selected: false,
            name: '',
            value: '',
            desc: '',
          },
          {
            selected: false,
            name: '',
            value: '',
            desc: '',
          },
        ],
        reqBody: null,
        selfSchema: {},
        swaggerData: null,
        responseData: '',
        consumeTime: '',
        historyList: [],
        user: null,
        mark: ''
      };
    },
    methods: {
      sendTest() {
        this.loading = true;
        this.responseData = '';
        this.consumeTime = '';
        var param = {
          target: this.host + this.api.path,
          method: this.api.method,
          query: '?1=1',
        };
        // 设置query参数
        for (var i = 0; i < this.api.parameters.length; i++) {
          var pme = this.api.parameters[i];
          if (pme.selected == true) {
            param.query += '&' + pme.name + '=' + pme.value;
          }
        }
        // 设置headers请求头
        var hDatas = [];
        for (var i = 0; i < this.headers.length; i++) {
          var header = this.headers[i];
          if (header.selected == true) {
            hDatas.push({
              key: header.name,
              val: header.value
            })
          }
        }
        param.headers = hDatas;
        // 设置body请求体
        if (this.reqBody) {
          param.reqBody = this.reqBody;
        }
        // 记录API
        var apiTestHistoryReq = {
          apiId: this.apiId,
          userId: this.user.id,
          content: {
            host: this.host,
            headers: this.headers,
            api: this.api,
            status: -1,
            reqBody: this.reqBody,
            mark: this.mark
          }
        };
        this.$http.post(Global.SERVER_ROOT + '/swaggerTool/apiManage/testApi', param).then((resp) => {
          if (resp.body.code != -1) {
            apiTestHistoryReq.content.status = 0;
            apiTestHistoryReq.content.responseData = resp.body.msg;
            apiTestHistoryReq.content.consumeTime = resp.body.consumeTime + '(ms)';
          }
          apiTestHistoryReq.content = JSON.stringify(apiTestHistoryReq.content);
          this.saveHistory(apiTestHistoryReq);
          this.responseData = resp.body.msg;
          this.consumeTime = resp.body.consumeTime + '(ms)';
          this.loading = false;
        }, (error) => {
          apiTestHistoryReq.content = JSON.stringify(apiTestHistoryReq.content);
          this.saveHistory(apiTestHistoryReq);
          this.loading = false;
        });
      },
      deleteHistory(id, e) {
        console.log(e)
        this.$http.delete(Global.SERVER_ROOT + '/apiTest/history/' + id).then((resp) => {
          if (resp.body && resp.body.code != -1) {
            this.$Message.success('delete success ! ');
            this.getHistoryList();
          } else {
            this.$Message.error(resp.body.msg);
          }
        }, (error) => {
          this.$Message.error('网络异常.');
        });
      },
      saveHistory(apiTestHistoryReq) {
        this.$http.post(Global.SERVER_ROOT + '/apiTest/history', apiTestHistoryReq).then((resp) => {
          if (resp.body && resp.body.code != -1) {
            this.getHistoryList();
          } else {
            this.$Message.error(resp.body.msg);
          }
        }, (error) => {
          this.$Message.error('网络异常.');
        });
      },
      recoverApiTestData(history) {
        console.log(history)
        this.host = history.data.host;
        this.headers = history.data.headers;
        this.api = history.data.api;
        this.responseData = history.data.responseData;
        this.consumeTime = history.data.consumeTime;
        this.reqBody = history.data.reqBody;
        this.mark = history.data.mark;
        window.document.title = this.mark;
      },
      getHistoryList() {
        this.$http.get(Global.SERVER_ROOT + '/apiTest/historyList?apiId=' + this.apiId + '&userId=' + this.user.id).then((resp) => {
          if (resp.body) {
            this.historyList = [];
            var datas = resp.body;
            for (var i = 0; i < datas.length; i++) {
              var data = datas[i];
              this.historyList.push({
                id: data._id,
                createTime: data.createTime,
                data: JSON.parse(data.content)
              });
            }
          }
        }, (error) => {
        });
      },
      getUser() {
        this.user = JSON.parse(Global.COOKIE.get(Global.SESSION_KEY));
        this.getSwagger();
      },
      getApi() {
        this.$http.get(Global.SERVER_ROOT + '/swaggerTool/apiManage/api/' + this.apiId).then((resp) => {
          this.api = resp.body;
          console.log(resp.body)
          // 拼接reqBody
          if (this.api.parameters && this.api.parameters.length > 0) {
            for (var i = 0; i < this.api.parameters.length; i++) {
              var param = this.api.parameters[i];
              if (param.type == 'object') {
                this.reqBody = {};
                var schema = this.findSchema(param.schema);
                for (var key in schema.val.properties) {
                  this.reqBody[key] = '';
                }
                this.reqBody = JSON.stringify(this.reqBody);
              }
            }
          }
        }, (error) => {

        });
      },
      getSwagger() {
        this.$http.get(Global.SERVER_ROOT + '/swaggerTool/newApi/swagger/resource').then((resp) => {
          this.swaggerData = resp.body;
          this.freashSchemaList();
          this.getHistoryList();
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
          }
          this.getApi();
        }, (error) => {

        });
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
        return null;
      },


    },
    created() {
      // 路由中获取API - ID
      this.apiId = this.$route.params.apiId;
      this.getUser();
    },
  }
</script>

<style scoped>

</style>
