<template>
  <div style="text-align: center;">
    <div style="width: 420px;margin: auto;margin-top: 200px;">
      <h2>Sign Up</h2>
      <br>
      <Form ref="formInline" :model="formInline" :rules="ruleInline">
        <FormItem prop="name">
          <Input type="text" v-model="formInline.name" placeholder="用户名">
          <Icon type="ios-person-outline" slot="prepend"></Icon>
          </Input>
        </FormItem>
        <FormItem prop="pwd">
          <Input type="password" v-model="formInline.pwd" placeholder="密码">
          <Icon type="ios-lock-outline" slot="prepend"></Icon>
          </Input>
        </FormItem>
        <FormItem prop="pwd2">
          <Input type="password" v-model="formInline.pwd2" placeholder="再次确认密码">
          <Icon type="ios-lock-outline" slot="prepend"></Icon>
          </Input>
        </FormItem>
        <FormItem>
          <Button type="primary" @click="handleSubmit('formInline')">Sign Up</Button>
          已有账号？去<router-link to="/sign/in">登录 </router-link>
        </FormItem>
      </Form>
    </div>
  </div>
</template>

<script>

  import Global from '@/components/Global.vue'

  export default {
    name: "SignUp",
    data() {
      const validatePass = (rule, value, callback) => {
        if (value != this.formInline.pwd) {
          callback(new Error('两次输入的密码不一致!'));
        }else {
          callback();
        }
      };
      const validateName = (rule, value, callback) => {
        this.$http.get(Global.SERVER_ROOT + '/sign/check/' + value).then((resp) => {
          if(resp.body.code == -1) {
            callback(new Error('用户名已存在!'));
          }else {
            callback();
          }
        }, (error) => {

        });
      };

      return {
        formInline: {
          name: '',
          pwd: '',
          pwd2: ''
        },
        ruleInline: {
          name: [
            {required: true, message: '请输入用户名', trigger: 'blur'},
            { validator: validateName, trigger: 'blur' }
          ],
          pwd: [
            {required: true, message: '请输入密码', trigger: 'blur'},
            {type: 'string', min: 6, message: '密码长度不得少于6位数', trigger: 'blur'}
          ],
          pwd2: [
            {required: true, message: '请输入确认密码', trigger: 'blur'},
            { validator: validatePass, trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      handleSubmit(name) {
        this.$refs[name].validate((valid) => {
          if (valid) {
            console.log(this.formInline)
            this.$http.post(Global.SERVER_ROOT + '/sign/up', this.formInline).then((resp) => {
              if(resp.body.code == -1) {
                this.$Message.error(resp.body.msg);
              }else {
                var user = resp.body.data;
                // sessionStorage.setItem(Global.SESSION_KEY, JSON.stringify(user))
                Global.COOKIE.set(Global.SESSION_KEY, JSON.stringify(user))
                this.$Message.success('登录成功，即将跳转到首页……');
                this.$router.push({
                  path: '/'
                });
              }
            }, (error) => {

            });
          } else {
            this.$Message.error('Fail!');
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
