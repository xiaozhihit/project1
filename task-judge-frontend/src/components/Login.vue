<script setup>
import {Lock, User} from "@element-plus/icons-vue";
import {onMounted, reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {get, postForm} from "../net";
import router from "../router";
import {useUserStore} from "../stores/userStore";

const form = reactive({
    username: '',
    password: '',
    remember: false
})
let username=ref("")
let roles =ref([])

onMounted(async () => {
    //如果用户已经登录过直接来到首页
    await get('/api/auth/check-login', (data) => {
        if (data.isLoggedIn) {
            ElMessage.success('已登录')
            router.push('/index')
        }else{
            ElMessage.warning('未登录')
        }
    })

})
const login = async () => {
    if (!form.username || !form.password) {
        ElMessage.warning("请输入用户名和密码!")
    } else {
        postForm('/api/auth/login', {
            username: form.username,
            password: form.password,
            remember: form.remember,
        }, (data) => {
            ElMessage.success('登录成功');
            username=data.username;
            roles=data.roles;
            // 将用户名和角色保存到 userStore
            const userStore = useUserStore();
            userStore.setUsername(username);
            userStore.setRoles(roles);
            router.push('/index');
        });

    }
}
</script>

<template>
    <div style="text-align: center; margin: 0 20px">
        <div style="margin-top: 150px">
            <div style="font-size: 30px;font-weight: bold">登录</div>
            <div style="font-size: 14px;color: grey;margin-top: 5px">请输入用户名和密码进行登录</div>
        </div>
        <div style="margin-top: 40px">
            <el-input v-model="form.username" type="text" placeholder="用户名">
                <template #prefix>
                    <el-icon>
                        <User/>
                    </el-icon>
                </template>
            </el-input>
            <el-input v-model="form.password" type="password" style="margin-top: 10px" placeholder="密码">
                <template #prefix>
                    <el-icon>
                        <Lock/>
                    </el-icon>
                </template>
            </el-input>
        </div>
        <div style="margin-top: 8px;">
            <el-row>
                <el-col :span="12" style="text-align: left">
                    <el-checkbox v-model="form.remember" label="记住我"/>
                </el-col>
                <el-col :span="12" style="text-align: right">
                    <el-link @click="router.push('/forget')">忘记密码?</el-link>
                </el-col>
            </el-row>
        </div>
        <div style="margin-top: 40px">
            <el-button @click="login()" style="width: 250px" type="success" plain>登录</el-button>
        </div>
    </div>

</template>

<style scoped>

</style>