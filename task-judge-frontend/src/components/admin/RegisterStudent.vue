<script setup>
import {reactive} from "vue";
import {ElUpload, ElButton, ElForm, ElFormItem, ElInput, ElMessage} from "element-plus";
import {post} from "../../net";

const newStudent = reactive({
    name: '',
    username: '',
    password: '',
    groupId: '',//小组编号可以不输入
}); // 新增单个学生的表单数据

function handleUploadSuccess(response, file, fileList) {
    //todo 处理上传成功的回调
    console.log("上传成功", response, file, fileList);
}

function beforeUpload(file) {
    //todo 处理上传前的回调，可以在此处对上传的文件进行验证
    console.log("准备上传", file);
}

// 处理新增单个学生的提交事件
function submitNewStudent() {
    if (newStudent.name === '') {
        ElMessage.warning('学生姓名不能为空')
        return
    }
    if (newStudent.username === '') {
        ElMessage.warning('学生学号/用户名不能为空')
        return
    }
    if (newStudent.password === '') {
        ElMessage.warning('学生密码不能为空')
        return
    }
    post('/api/user', {
        username: newStudent.username,
        name: newStudent.name,
        password: newStudent.password,
        groupId: newStudent.groupId,
    }, (data) => {
        ElMessage.success(data)
    })
}

const uploadExcel = () => {
    ElMessage.success("上传文件功能仍在开发中")
}
</script>

<template>
    <div>
        <el-row>
            <el-col :span="3">
                通过excel文件新增学生
            </el-col>
            <el-col :span="14">
                <el-upload
                        class="upload-demo"
                        :auto-upload="false"
                        :on-success="handleUploadSuccess"
                        :show-file-list="false"
                        :before-upload="beforeUpload"
                >
                    <el-button size="small" type="primary" @click="uploadExcel">上传 Excel 文件</el-button>
                </el-upload>
            </el-col>
        </el-row>
        <el-divider></el-divider>
        <div style="margin-left: 205px">新增指定学生</div>
        <el-form :model="newStudent" style="max-width: 460px;margin-top: 10px" label-width="80px"
                 :label-position="'right'">
            <el-form-item label="姓名" prop="name">
                <el-input v-model.lazy="newStudent.name"></el-input>
            </el-form-item>
            <el-form-item label="学号" prop="username">
                <el-input v-model.lazy="newStudent.username"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input v-bind="newStudent.username" v-model.lazy="newStudent.password"></el-input>
            </el-form-item>
            <el-form-item label="组号" prop="groupId">
                <el-input v-model.lazy="newStudent.groupId" placeholder="选填"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button style="margin-left: 140px" type="primary" plain @click="submitNewStudent">提交</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<style scoped>
</style>