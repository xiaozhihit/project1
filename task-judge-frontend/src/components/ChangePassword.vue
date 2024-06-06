<script setup>
import {reactive} from "vue";
import {put} from "../net";
import {ElMessage} from "element-plus";
import router from "../router";

const form = reactive({
    oldPassword: '',
    newPassword: '',
    repeatPassword: '',
})
const changePassword = () => {
    if (form.oldPassword === '') {
        ElMessage.warning('请输入原先密码')
        return;
    }
    if (form.newPassword === '' || form.repeatPassword === '') {
        ElMessage.warning('请输入新密码')
        return;
    }
    if (form.repeatPassword !== form.newPassword) {
        ElMessage.warning('新的密码与确认密码不一致')
        return;
    }
    if (form.newPassword === form.oldPassword) {
        ElMessage.warning('新密码不能与原先密码相同')
        return;
    }
    put('/api/auth/change-password', form, () => {
        ElMessage.success('修改密码成功');
        router.push('/index');
    })
}
</script>

<template>
    <div style="text-align: center;font-size: 25px;padding: 5px;margin-top: 180px">修改密码</div>
    <el-form style="text-align: center;margin-top: 20px">
        <div class="el-input-group">
            <div class="el-input-group__prepend">旧的密码</div>
            <el-input v-model="form.oldPassword" type="password" placeholder="请输入"></el-input>
        </div>
        <div class="blank"></div>
        <div class="el-input-group">
            <div class="el-input-group__prepend">新的密码</div>
            <el-input v-model="form.newPassword" type="password" autocomplete="" placeholder="请输入"></el-input>
        </div>
        <div class="blank"></div>
        <div class="el-input-group">
            <div class="el-input-group__prepend">确认密码</div>
            <el-input v-model="form.repeatPassword" type="password" autocomplete="" placeholder="请输入"></el-input>
        </div>
        <el-row style="margin-top: 20px">
            <el-col :span="12" style="text-align: left">
                <el-button @click="router.push('/index')" style="width: 224px"
                           type="primary"
                           plain>返回首页
                </el-button>
            </el-col>
            <el-col :span="12" style="text-align: right">
                <el-button @click="changePassword()" style="width: 224px"
                           type="success"
                           plain>确认修改
                </el-button>
            </el-col>
        </el-row>
    </el-form>
</template>

<style scoped>
.blank {
    min-height: 15px;
}
</style>