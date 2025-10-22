<template>
  <div class="settings-view container mx-auto px-4 py-8">
    <h1 class="text-2xl font-bold text-gray-800 mb-6">设置</h1>

    <el-card class="mb-6">
      <template #header>
        <div class="flex items-center">
          <span class="text-lg font-semibold">后端 API 配置</span>
          <el-tag type="info" class="ml-3">仅保存在本地浏览器</el-tag>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="140px">
        <el-form-item label="API 基础地址" prop="apiBase">
          <el-input v-model="form.apiBase" placeholder="例如：http://localhost:8080/api" />
        </el-form-item>

        <el-form-item label="API Key" prop="apiKey">
          <el-input v-model="form.apiKey" type="password" show-password placeholder="请输入您的 API Key" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="saving" @click="saveSettings">保存</el-button>
          <el-button class="ml-2" @click="resetSettings">恢复默认</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-alert type="warning" show-icon :closable="false" title="安全提示" class="mb-4"
      description="API Key 不会被上传到服务器或代码仓库，仅保存在浏览器 localStorage 中。请在生产环境使用更安全的密钥管理方案（如后端环境变量或云密钥服务）。" />

    <div class="mt-6">
      <el-button type="success" :loading="testing" @click="testConnection">测试连接</el-button>
      <span v-if="testResult" class="ml-3 text-gray-600">{{ testResult }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { http } from '@/utils/request'
import type { FormInstance, FormRules } from 'element-plus'

const formRef = ref<FormInstance>()
const saving = ref(false)
const testing = ref(false)
const testResult = ref('')

const form = ref({
  apiBase: '',
  apiKey: ''
})

const rules: FormRules = {
  apiBase: [
    { required: true, message: '请输入 API 基础地址', trigger: 'blur' }
  ],
  apiKey: [
    { required: true, message: '请输入 API Key', trigger: 'blur' }
  ]
}

onMounted(() => {
  form.value.apiBase = localStorage.getItem('apiBase') || (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api')
  form.value.apiKey = localStorage.getItem('apiKey') || ''
})

const saveSettings = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    saving.value = true
    localStorage.setItem('apiBase', form.value.apiBase)
    localStorage.setItem('apiKey', form.value.apiKey)
    ElMessage.success('设置已保存')
  } catch (e) {
    // 验证失败
  } finally {
    saving.value = false
  }
}

const resetSettings = () => {
  localStorage.removeItem('apiBase')
  localStorage.removeItem('apiKey')
  form.value.apiBase = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'
  form.value.apiKey = ''
  ElMessage.success('已恢复默认设置')
}

const testConnection = async () => {
  testing.value = true
  testResult.value = ''
  try {
    // 简单健康检查：尝试请求一个公共接口；如无可用端点，尝试 GET '/user/verify' 并忽略错误
    const resp = await http.get('/health')
    if ((resp as any)?.code === 0 || (resp as any)?.success) {
      testResult.value = '连接正常'
      ElMessage.success('连接正常')
    } else {
      testResult.value = '连接失败（返回非成功状态）'
      ElMessage.warning('连接失败：返回非成功状态')
    }
  } catch (e: any) {
    testResult.value = `连接失败：${e?.message || '未知错误'}`
  } finally {
    testing.value = false
  }
}
</script>

<style scoped>
.settings-view {
  max-width: 800px;
}
</style>