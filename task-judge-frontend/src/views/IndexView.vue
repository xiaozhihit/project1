<script setup>
import {computed, ref} from "vue";
import {useUserStore} from "../stores/userStore";
import {useRouter} from "vue-router";
import {get} from "../net";
import {ElMessage} from "element-plus";

const userStore = useUserStore();
const router = useRouter();

const dropdownVisible = ref(false);

const changePassword = () => {
    router.push('/change-password')
};

const logout = () => {
    // 处理退出登录逻辑
    get("/api/auth/logout", (message) => {
        ElMessage.success(message)
        router.push('/')
    })

};

const activeRoute = computed(() => router.currentRoute.value.path);

const menuItems = computed(() => {
    const roles = userStore.roles;
    const isAdmin = roles.includes("ROLE_admin");
    const isUser = roles.includes("ROLE_user");
    const isJudge = roles.includes("ROLE_judge");

    const items = [];

    if (isAdmin) {
        items.push(
            {
                title: "学生管理",
                index: "1",
                icon: "el-icon-s-custom",
                submenu: [
                    {title: "为学生重置密码", index: "/admin/reset-student-password"},
                    {title: "指定分组", index: "/admin/assign-group"},
                    {title: "设置作业访问路径", index: "/admin/set-task-url"},
                    {title: "注册新学生", index: "/admin/register-student"}
                ]
            },
            {
                title: "评委管理",
                index: "2",
                icon: "el-icon-s-check",
                submenu: [
                    {title: "重置评委密码", index: "/admin/reset-judge-password"},
                    {title: "撤销打分状态", index: "/admin/revoke-scoring"},
                    {title: "指定评委", index: "/admin/assign-judge"}
                ]
            }
        );
    }
    if (isUser) {
        items.push(
            {title: "查看大作业成绩", index: "/user/view-task-score", icon: "el-icon-s-data"},
            {title: "提交大作业", index: "/user/submit-task", icon: "el-icon-upload"}
        );
    }
    if (isJudge) {
        items.push({title: "为大作业评分", index: "/judge/judge-task", icon: "el-icon-view"});
    }

    return items;
});

</script>

<template>
    <el-container>
        <el-header>
            <h1 class="title">大作业评分系统</h1>
            <div class="header-right">
                <div class="user-dropdown-container">
                    <div class="user-dropdown">
                        欢迎：{{ userStore.username }}<i class="el-icon-arrow-down el-icon--right"></i>
                    </div>
                    <div class="dropdown-menu">
                        <div @click="changePassword" class="dropdown-item">修改密码</div>
                        <div @click="logout" class="dropdown-item">退出登录</div>
                    </div>
                </div>
            </div>
        </el-header>
        <el-container>
            <el-aside width="200px">
                <el-menu :default-active="activeRoute" router mode="vertical">
                    <template v-for="(item, index) in menuItems">
                        <el-sub-menu v-if="item.submenu" :key="index" :index="item.index">
                            <template #title>
                                <i :class="item.icon"></i>
                                <span>{{ item.title }}</span>
                            </template>
                            <el-menu-item
                                v-for="(subItem, subIndex) in item.submenu"
                                :key="subIndex"
                                :index="subItem.index"
                            >
                                <i :class="subItem.icon"></i>
                                <span>{{ subItem.title }}</span>
                            </el-menu-item>
                        </el-sub-menu>
                        <el-menu-item v-else :key="'other-'+index" :index="item.index">
                            <i :class="item.icon"></i>
                            <span>{{ item.title }}</span>
                        </el-menu-item>
                    </template>
                </el-menu>
            </el-aside>
            <el-main>
                <router-view/>
            </el-main>
        </el-container>
    </el-container>
    <teleport to="body">
        <transition name="el-fade-in-linear">
            <div v-show="dropdownVisible" class="dropdown-menu">
                <div @click="changePassword" class="dropdown-item">修改密码</div>
                <div @click="logout" class="dropdown-item">退出登录</div>
            </div>
        </transition>
    </teleport>
</template>


<style scoped>

.title {
    text-align: center;
    font-size: 30px;
    font-style: italic;
    font-weight: bolder;
    text-transform: uppercase;
    color: darkblue;
    margin: 0;
    line-height: 60px;
}

.header-right {
    position: absolute;
    top: 15px;
    right: 20px;
}

.user-dropdown {
    position: relative;
    display: inline-block;
    cursor: pointer;
    padding: 0 10px;
}

.user-dropdown-container {
    position: relative;
    display: inline-block;
    cursor: pointer;
}

.user-dropdown-container:hover .dropdown-menu {
    display: block;
}

.dropdown-menu {
    display: none;
    position: absolute;
    right: 20px;
    top: 100%;
    background-color: white;
    border: 1px solid #ebeef5;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    padding: 5px 0;
    z-index: 2000;
    max-height: calc(100vh - 50px);
    overflow: auto;
}

.dropdown-item {
    padding: 8px 3px;
    cursor: pointer;
}

.dropdown-item:hover {
    background-color: #ecf5ff;
}

.user-dropdown-container {
    position: relative;
    display: inline-block;
    cursor: pointer;
}

.user-dropdown-container:hover .dropdown-menu {
    display: block;
}

</style>