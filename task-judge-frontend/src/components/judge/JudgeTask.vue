<script setup>
import {Edit} from '@element-plus/icons-vue'
import {reactive, ref, watchEffect} from 'vue';
import {get, put} from "../../net";
import {useUserStore} from "../../stores/userStore";
import {ElMessage, ElMessageBox} from "element-plus";

const userStore = useUserStore()
const roles = userStore.roles;
const isUser = roles.includes("ROLE_user");

let flag = ref(false)

let tasks = reactive([])//查询得到大作业列表
const editedTasks = reactive(new Map());
let editDialogVisible = ref(false);//编辑窗口

let selectedTask = reactive({
    groupId: "",
    storageUrl: "",
    demoUrl: "",
    videoUrl: "",
    workloadScore: "",
    completionScore: "",
    designScore: "",
    divisionScore: "",
    remark: "",
    isSubmitted: "",
})

//点击保存提交按钮
const saveData = async () => {
    const confirmResult = await ElMessageBox.confirm(
        `确定要保存刚才的评分吗？仍可修改`,
        "提示"
    );
    if (confirmResult) {
        // 将 Map 转换为数组
        const tasksToSave = Array.from(editedTasks.values());
        await put('/api/tasks-scores', tasksToSave, (data) => {
            ElMessage.success(data)
        })
        editedTasks.clear()
    }
};

const confirmJudge = async () => {
    const confirmResult = await ElMessageBox.confirm(
        `评分一旦确认提交评分就不可再修改！确定要提交刚才的评分吗？`,
        "提示!!!"
    );
    if (confirmResult === "confirm") {
        editedTasks.forEach(t=>t.isSubmitted=true)
        const tasksToSave = Array.from(editedTasks.values());
        //将本次打分的评分记录 修改为已经提交状态 不可再修改除非管理员撤销打分状态
        // tasksToSave.forEach(task=> task.isSubmitted=true)
        await put('/api/tasks-scores',tasksToSave,(data)=>{
            ElMessage.success(data)
            // 更新响应式数组中的元素
            tasksToSave.forEach(selectedTask => {
                let taskIndex = tasks.findIndex(task => task.groupId === selectedTask.groupId);
                if (taskIndex > -1) {
                    tasks[taskIndex] = {...tasks[taskIndex], ...selectedTask};
                }
            });
        })
        editedTasks.clear()
    }
}

const fetchData = async () => {
    await get(`/api/tasks-scores`, (data) => {
        tasks = data
    })
}

const openEditDialog = (row) => {
    selectedTask.groupId = row.groupId;
    selectedTask.storageUrl = row.storageUrl;
    selectedTask.demoUrl = row.demoUrl;
    selectedTask.videoUrl = row.videoUrl;
    selectedTask.workloadScore = row.workloadScore;
    selectedTask.completionScore = row.completionScore;
    selectedTask.designScore = row.designScore;
    selectedTask.divisionScore = row.divisionScore;
    selectedTask.remark = row.remark;
    selectedTask.isSubmitted = row.isSubmitted;
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
    for (let [key, value] of editedTasks.entries()) {
        console.log(`key: ${key}, value: ${JSON.stringify(value)}`);
        console.log(`----------------`);
    }
};

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
                <el-form-item label="工作量占比40%">
                    <el-input v-model="selectedTask.workloadScore"></el-input>
                </el-form-item>
                <el-form-item label="完成度占比40%">
                    <el-input v-model="selectedTask.completionScore"></el-input>
                </el-form-item>
                <el-form-item label="设计占比10%">
                    <el-input v-model="selectedTask.designScore"></el-input>
                </el-form-item>
                <el-form-item label="分工占比10%">
                    <el-input v-model="selectedTask.divisionScore"></el-input>
                </el-form-item>
                <el-form-item label="备注">
                    <el-input v-model="selectedTask.remark" disabled></el-input>
                </el-form-item>
                <el-form-item label="提交状态">
                    <el-input v-model="selectedTask.isSubmitted" disabled>
                        <template v-slot="{row}">
                            <span v-if="row.isSubmitted">已提交评分</span>
                            <span v-else>可修改</span>
                        </template>
                    </el-input>
                </el-form-item>
            </el-form>
            <div slot="footer">
                <el-button @click="editDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveSelectedTask">保存</el-button>
            </div>
        </el-dialog>

        <div style="font-size: 25px;font-style: italic">评委请为大作业评分 每项满分100
            <span v-if="isUser" style="font-size: 10px;margin-left: 5px">(你是学生吧 无法为自己所在小组打分哦)</span>
        </div>
        <div v-if="flag" style="text-align: center;margin-top: 15px">
            <el-table :data="tasks" border>
                <el-table-column prop="groupId" label="小组编号" width="40px"></el-table-column>
                <el-table-column label="项目压缩包地址">
                    <template #default="{ row }">
                        <a v-if="row.storageUrl" v-bind:href="row.storageUrl">{{ row.storageUrl }}</a>
                        <span v-else>未提供</span>
                    </template>
                </el-table-column>
                <el-table-column label="项目演示网址">
                    <template #default="{ row }">
                        <a v-if="row.demoUrl" v-bind:href="row.demoUrl">{{ row.demoUrl }}</a>
                        <span v-else>未提供</span>
                    </template>
                </el-table-column>
                <el-table-column label="演示视频网址">
                    <template #default="{ row }">
                        <a v-if="row.videoUrl" v-bind:href="row.videoUrl">{{ row.videoUrl }}</a>
                        <span v-else>未提供</span>
                    </template>
                </el-table-column>
                <el-table-column prop="workloadScore" label="工作量得分" width="70px"></el-table-column>
                <el-table-column prop="completionScore" label="完成度得分" width="70px"></el-table-column>
                <el-table-column prop="designScore" label="设计得分" width="70px"></el-table-column>
                <el-table-column prop="divisionScore" label="分工得分" width="70px"></el-table-column>
                <el-table-column prop="remark" label="备注"></el-table-column>
                <el-table-column prop="isSubmitted" label="评分状态" width="70px">
                    <template v-slot="{row}">
                        <span v-if="row.isSubmitted">已提交</span>
                        <span v-else>可修改</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="45px">
                    <template #default="{ row }">
                        <el-button :icon="Edit" circle @click="openEditDialog(row)"
                                   :disabled="row.isSubmitted"></el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-row>
                <el-col :span="12">
                    <el-button type="success" @click="saveData" style="margin-top: 15px;width: 300px">保存修改
                    </el-button>
                </el-col>
                <el-col :span="12">
                    <el-button type="danger" @click="confirmJudge" style="margin-top: 15px;width: 300px">
                        提交修改
                    </el-button>
                </el-col>
            </el-row>
        </div>
        <div v-else>
            <p>正在加载数据，请稍候...</p>
        </div>
    </div>
</template>
<style scoped>
</style>
