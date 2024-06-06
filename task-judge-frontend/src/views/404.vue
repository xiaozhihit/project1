<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const goBack = () => {
    router.push('/index');
};
</script>

<template>
    <div class="not-found">
        <h1>404: 页面未找到</h1>
        <p>抱歉，您访问的页面不存在。</p>
        <button @click="goBack">返回首页</button>
    </div>
</template>

<style scoped>

</style>