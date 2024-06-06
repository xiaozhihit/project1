<script setup>
import {reactive} from 'vue';
import {ElMessage, ElUpload} from 'element-plus';
import {postFile} from "../../net";
const form =reactive({
    remark:'',
    file:null
})

const beforeUpload = (fileToUpload) => {
    console.log('文件类型' + fileToUpload.type)
    console.log('文件大小：' + fileToUpload.size)
    const isZip = fileToUpload.name.toLowerCase().endsWith('.zip');
    const validSize = fileToUpload.size <= 20 * 1024 * 1024
    console.log('文件类型是：.zip' + isZip)
    if (!isZip) {
        ElMessage.error('只能上传ZIP文件！');
    }
    if (!validSize) {
        ElMessage.error('只能上传20MB以内大小的文件')
    }
    return isZip&&validSize;
};
const handleFileChange = (file) => {
    console.log(file)
    console.log(file.raw)
    if(form.file!==null){
        form.file =file.raw;
        ElMessage.warning('只能上传最后一次提交的文件哦')
    }else{
        form.file = file.raw;
    }
    console.log(form.file)

};

const submit = () => {
    const formData = new FormData();
    formData.append('file', form.file);
    formData.append('remark', form.remark);
    postFile('/api/user/submit-task',formData,(data)=>{
        ElMessage.success(data)
    });
};
</script>

<template>
    <div class="submission-container">
        <h2>提交大作业</h2>
        <div slot="tip" class="el-upload__tip">
            请将未压缩项目文件、演示视频、报告文档放入按照规范命名文件夹中,再将文件夹压缩上传
        </div>
        <div slot="tip" class="el-upload__tip">现只支持上传.zip文件,文件大小限制20MB</div>
        <el-form>
            <el-upload
                :before-upload="beforeUpload"
                :auto-upload="false"
                :on-change="handleFileChange"
            >
                <el-button slot="trigger" size="default" type="primary">点击上传</el-button>
            </el-upload>
            <el-input
                    type="textarea"
                    :rows="4"
                    placeholder="添加备注说明,150字以内（选填）"
                    v-model="form.remark"
            ></el-input>
            <el-button type="primary" @click="submit">提交</el-button>
        </el-form>
    </div>
</template>

<style scoped>
.submission-container {
    text-align: center;
    max-width: 600px;
    margin: 0 auto;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    border-radius: 4px;
}

.el-upload__tip {
    font-size: 14px;
    color: #969696;
    margin-top: 10px;
}
</style>