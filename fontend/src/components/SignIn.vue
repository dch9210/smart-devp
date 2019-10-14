<template>
  <div style="text-align: center;">
    <div style="width: 420px;margin: auto;margin-top: 200px;" @keyup.enter="handleSubmit('formInline')">
      <h2>登录</h2>
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
        <FormItem>
          <Button type="success" @click="handleSubmit('formInline')">登录</Button>
          还没有账号？去<router-link to="/sign/up">注册 </router-link>
        </FormItem>
      </Form>
    </div>
  </div>
</template>

<script>

  import Global from '@/components/Global.vue'

  export default {
    name: "SignIn",
    data() {
      return {
        formInline: {
          name: '',
          pwd: '',
        },
        ruleInline: {
          name: [
            {required: true, message: '请输入用户名', trigger: 'blur'}
          ],
          pwd: [
            {required: true, message: '请输入密码', trigger: 'blur'},
            {type: 'string', min: 6, message: '密码长度不得少于6位数', trigger: 'blur'}
          ],
        }
      }
    },
    methods: {
      handleSubmit(name) {
        this.$refs[name].validate((valid) => {
          if (valid) {
            console.log(this.formInline);
            this.$http.post(Global.SERVER_ROOT + '/sign/in?name=' + this.formInline.name + '&pwd=' + this.formInline.pwd).then((resp) => {
              if(resp.body.code == -1) {
                this.$Message.error(resp.body.msg);
              }else {
                var user = resp.body.data;
                // sessionStorage.setItem(Global.SESSION_KEY, JSON.stringify(user))
                Global.COOKIE.set(Global.SESSION_KEY, JSON.stringify(user))
                this.$Message.success('登录成功，即将跳转……');
                var redirect = '/';
                if(this.$route.query && this.$route.query.redirect) {
                  redirect = this.$route.query.redirect;
                }
                this.$router.push({
                  path: redirect
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
