<script setup>
import {Edit} from '@element-plus/icons-vue'
import {reactive, ref, watchEffect} from 'vue';
import {get, put} from "../../net";
import {useUserStore} from "../../stores/userStore";
import {ElMessage} from "element-plus";

const userStore = useUserStore()
let flag = ref(false)

let tasks = reactive([])//查询得到大作业列表
const editedTasks = reactive(new Map());
let editDialogVisible = ref(false);//编辑窗口

let selectedTask = reactive({
    groupId:"",
    storageUrl:"",
    demoUrl:"",
    videoUrl:"",
})

//点击保存提交按钮
const saveData = () => {
    // 将 Map 转换为数组
    const tasksToSave = Array.from(editedTasks.values());
    put('/api/tasks', tasksToSave, (data) => {
        ElMessage.success(data)
    })
    editedTasks.clear()
};

const fetchData = async () => {
    await get(`/api/tasks`, (data) => {
        tasks = data
    })
}

const openEditDialog = (row) => {
    selectedTask.groupId = row.groupId;
    selectedTask.storageUrl = row.storageUrl;
    selectedTask.demoUrl = row.demoUrl;
    selectedTask.videoUrl = row.videoUrl;
    editDialogVisible.value = true;
};

const saveSelectedTask = () => {
    const newTasks = reactive([]);
    tasks.forEach(task => newTasks.push(task));

    let taskIndex = tasks.findIndex(task => task.groupId === selectedTask.groupId);
    if (taskIndex > -1) {
        newTasks[taskIndex] = {...newTasks[taskIndex], ...selectedTask};
        tasks = newTasks;
    }
    editDialogVisible.value = false;
    // 将任务对象副本 保存到 Map 中
    editedTasks.set(selectedTask.groupId, {...selectedTask});
    // for (let [key, value] of editedTasks.entries()) {
    //     console.log(`key: ${key}, value: ${JSON.stringify(value)}`);
    // }};

}
watchEffect(async () => {
  if (userStore.refreshOnNavigate) {
    await fetchData()
    flag.value = true
  }
});
</script>
<template>
    <div>
        <!-- 编辑弹窗 -->
        <el-dialog v-model="editDialogVisible" title="设置资源访问路径">
            <el-form :model="selectedTask" label-width="120px">
                <el-form-item label="小组编号">
                    <el-input v-model="selectedTask.groupId" disabled></el-input>
                </el-form-item>
                <el-form-item label="项目压缩包地址">
                    <el-input v-model="selectedTask.storageUrl"></el-input>
                </el-form-item>
                <el-form-item label="项目演示网址">
                    <el-input v-model="selectedTask.demoUrl"></el-input>
                </el-form-item>
                <el-form-item label="演示视频网址">
                    <el-input v-model="selectedTask.videoUrl"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="editDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveSelectedTask">保存</el-button>
            </div>
        </el-dialog>

        <div style="font-size: 25px;font-style: italic">为大作业设置资源访问路径</div>
        <div v-if="flag" style="text-align: center;margin-top: 15px">
            <el-table :data="tasks" border>
                <el-table-column prop="groupId" label="小组编号"></el-table-column>
                <el-table-column prop="storageUrl" label="项目压缩包地址"></el-table-column>
                <el-table-column prop="demoUrl" label="项目演示网址"></el-table-column>
                <el-table-column prop="videoUrl" label="演示视频网址"></el-table-column>
                <el-table-column label="操作">
                    <template #default="{ row }">
                        <el-button :icon="Edit" circle @click="openEditDialog(row)"></el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-button type="primary" @click="saveData" style="margin-top: 10px">保存</el-button>
        </div>
        <div v-else>
            <p>正在加载数据，请稍候...</p>
        </div>
    </div>
</template>
<style scoped>
</style>