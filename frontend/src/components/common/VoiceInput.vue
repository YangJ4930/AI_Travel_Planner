<template>
  <div class="voice-input-container">
    <!-- 云端模式：录音按钮 -->
    <template v-if="useCloud">
      <el-button
        :type="getCloudButtonType"
        :loading="isProcessing"
        @click="handleCloudVoiceClick"
        circle
        size="large"
        :title="cloudButtonTitle"
        class="z-20"
      >
        <el-icon class="text-xl">
          <Microphone v-if="!isRecorded && !isRecognizing && !isTranslating" />
          <Check v-else-if="isRecorded && !isProcessing" />
          <Loading v-else />
        </el-icon>
      </el-button>
      
      <!-- 云端录音状态提示 -->
      <div v-if="isRecording" class="voice-status">
        <div class="voice-wave">
          <div class="wave-dot"></div>
          <div class="wave-dot"></div>
          <div class="wave-dot"></div>
        </div>
        <span class="voice-text">正在录音...</span>
      </div>
      
      <!-- 识别状态提示 -->
      <div v-else-if="isRecognizing" class="voice-status voice-status-recognizing">
        <el-icon class="animate-spin"><Loading /></el-icon>
        <span class="voice-text">正在识别语音...</span>
      </div>
      
      <!-- 翻译状态提示 -->
      <div v-else-if="isTranslating" class="voice-status voice-status-translating">
        <el-icon class="animate-spin"><Loading /></el-icon>
        <span class="voice-text">正在翻译内容...</span>
      </div>
      
      <!-- 录音完成提示 -->
      <div v-else-if="isRecorded && !isProcessing" class="voice-status">
        <el-icon class="text-green-500"><Check /></el-icon>
        <span class="voice-text">录音完成，点击识别</span>
      </div>
    </template>
    
    <!-- 本地模式：原有逻辑 -->
    <template v-else>
      <el-button
        :type="isListening ? 'danger' : 'primary'"
        :loading="isProcessing"
        @click="toggleVoiceInput"
        circle
        size="large"
        :title="buttonTitle"
        class="z-20"
      >
        <el-icon class="text-xl">
          <Microphone />
        </el-icon>
      </el-button>
      
      <!-- 语音状态提示 -->
      <div v-if="isListening" class="voice-status">
        <div class="voice-wave">
          <div class="wave-dot"></div>
          <div class="wave-dot"></div>
          <div class="wave-dot"></div>
        </div>
        <span class="voice-text">正在听取...</span>
      </div>
    </template>
    
    <!-- 错误提示 - 隐藏以防止遮挡按钮 -->
    <!-- <div v-if="error" class="voice-error">
      <el-alert
        :title="error"
        type="error"
        :closable="false"
        size="small"
      />
    </div> -->
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Microphone, Check, Loading } from '@element-plus/icons-vue'
import annyang from 'annyang'

interface Props {
  placeholder?: string
  language?: string
  continuous?: boolean
  interimResults?: boolean
  useCloud?: boolean
}

interface Emits {
  (e: 'result', text: string): void
  (e: 'start'): void
  (e: 'end'): void
  (e: 'error', error: string): void
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: '点击开始语音输入',
  language: 'zh-CN',
  continuous: false,
  interimResults: true,
  useCloud: false
})

const emit = defineEmits<Emits>()

// 状态管理
const isListening = ref(false)
const isProcessing = ref(false)
const isSupported = ref(false)
const error = ref<string | null>(null)
// 新增：标记是否用户手动停止（仅用于本地 annyang 模式）
const userStopped = ref(false)

// 云端模式新增状态
const isRecording = ref(false)
const isRecorded = ref(false)
const isRecognizing = ref(false) // 新增：语音识别状态
const isTranslating = ref(false) // 新增：翻译状态
const recordedAudioData = ref<string | null>(null)

// 计算属性
const buttonTitle = computed(() => {
  if (!isSupported.value) return '您的浏览器不支持语音识别'
  if (isListening.value) return '点击停止语音输入'
  return props.placeholder
})

// 云端模式按钮相关计算属性
const cloudButtonTitle = computed(() => {
  if (isRecording.value) return '正在录音中...'
  if (isRecognizing.value) return '正在识别中...'
  if (isTranslating.value) return '正在翻译中...'
  if (isRecorded.value && !isProcessing.value) return '点击开始识别'
  return '点击开始录音'
})

const getCloudButtonType = computed(() => {
  if (isRecording.value) return 'warning'
  if (isRecognizing.value) return 'info'
  if (isTranslating.value) return 'success'
  if (isRecorded.value && !isProcessing.value) return 'success'
  return 'primary'
})

// 初始化语音识别
const getAnnyang = () => {
  const imported: any = (annyang as any)
  const fromImport = imported?.default ?? imported
  const fromWindow = (window as any).annyang
  return fromImport || fromWindow || undefined
}

const detectSupport = () => {
  const a = getAnnyang()
  // 兼容缺少isSupported的场景
  const supported = a?.isSupported ? a.isSupported() : !!(window as any).webkitSpeechRecognition || !!(window as any).SpeechRecognition
  return { a, supported }
}

const initVoiceRecognition = async () => {
  await ensureAnnyangLoaded()
  const { a, supported } = detectSupport()
  if (!a) {
    error.value = '语音识别库未加载'
    isSupported.value = supported
    return false
  }

  if (!supported) {
    error.value = '您的浏览器不支持语音识别功能'
    isSupported.value = false
    return false
  }

  isSupported.value = true

  // 设置语言
  a.setLanguage(props.language)

  // 设置命令 - 捕获所有语音输入
  const commands = {
    '*text': (text: string) => {
      console.log('语音识别结果:', text)
      emit('result', text)
      if (!props.continuous) {
        stopListening()
      }
    }
  }

  a.addCommands(commands)

  // 设置回调
  a.addCallback('start', () => {
    console.log('语音识别开始')
    isListening.value = true
    isProcessing.value = false
    error.value = null
    userStopped.value = false
    emit('start')
  })
  
  a.addCallback('end', () => {
    console.log('语音识别结束')
    isListening.value = false
    isProcessing.value = false
    // 结束后复位手动停止标记
    userStopped.value = false
    emit('end')
  })

  a.addCallback('error', (err: any) => {
    console.error('语音识别错误:', err)
    const errorMessage = getErrorMessage(err)
    const isAbort = (typeof err === 'string' && err === 'aborted') || (err && err.error === 'aborted') || errorMessage.includes('被中断')
    // 如果是用户点击“结束”导致的 aborted，则视为正常结束，不弹错
    if (isAbort && userStopped.value) {
      isListening.value = false
      isProcessing.value = false
      error.value = null
      // 清除手动停止标记
      userStopped.value = false
      return
    }
    error.value = errorMessage
    isListening.value = false
    isProcessing.value = false
    emit('error', errorMessage)
  })

  a.addCallback('result', (phrases: string[]) => {
    console.log('语音识别候选结果:', phrases)
    // 选择第一条作为最佳结果并发出一次
    if (phrases && phrases.length > 0) {
      const bestResult = phrases[0]
      emit('result', bestResult)
      if (!props.continuous) {
        stopListening()
      }
    }
  })

  return true
}

// 获取错误信息
const getErrorMessage = (error: any): string => {
  const errorMessages: Record<string, string> = {
    'network': '网络连接错误',
    'not-allowed': '麦克风权限被拒绝',
    'no-speech': '未检测到语音输入',
    'aborted': '语音识别被中断',
    'audio-capture': '音频捕获失败',
    'service-not-allowed': '语音识别服务不可用'
  }

  if (typeof error === 'string') {
    return errorMessages[error] || error
  }

  if (error && error.error) {
    return errorMessages[error.error] || error.error
  }

  return '语音识别出现未知错误'
}

// 动态加载脚本并按需引入 annyang / SpeechKITT
const loadScript = (src: string) => new Promise<void>((resolve, reject) => {
  const s = document.createElement('script')
  s.src = src
  s.async = true
  s.onload = () => resolve()
  s.onerror = (e) => reject(e)
  document.head.appendChild(s)
})

const ensureAnnyangLoaded = async () => {
  if (getAnnyang()) return
  try {
    await loadScript('//cdnjs.cloudflare.com/ajax/libs/annyang/2.6.1/annyang.min.js')
  } catch (e) {
    console.error('加载 annyang 失败:', e)
  }
  const a = getAnnyang()
  if (!a) return
  try {
    const kitt = (window as any).SpeechKITT
    if (kitt && typeof kitt.annyang === 'function') {
      kitt.annyang()
      kitt.setStylesheet('//cdnjs.cloudflare.com/ajax/libs/SpeechKITT/0.3.0/themes/flat.css')
      if (typeof kitt.vroom === 'function') kitt.vroom()
    }
  } catch {}
}

// 主动触发麦克风权限弹窗
const ensureMicPermission = async () => {
  try {
    if (navigator?.mediaDevices?.getUserMedia) {
      await navigator.mediaDevices.getUserMedia({ audio: true })
    }
  } catch (e) {
    const msg = '麦克风权限被拒绝'
    error.value = msg
    emit('error', msg)
    throw e
  }
}

// 开始监听
const startListening = async () => {
  await ensureAnnyangLoaded()
  const { a, supported } = detectSupport()
  // 先尝试触发权限弹窗
  await ensureMicPermission().catch(() => {})
  if (!supported || !a) {
    const msg = !a ? '语音识别库未加载' : '您的浏览器不支持语音识别功能'
    if (error.value !== msg) {
      error.value = msg
      ElMessage.error(msg)
    } else {
      error.value = msg
    }
    return
  }
  error.value = null
  isProcessing.value = true
  userStopped.value = false
  try {
    await a.start({
      autoRestart: props.continuous,
      continuous: props.continuous
    })
  } catch (err) {
    console.error('启动语音识别失败:', err)
    error.value = '启动语音识别失败'
    isProcessing.value = false
  }
}

// 停止监听
const stopListening = () => {
  try {
    const { a } = detectSupport()
    // 标记用户手动停止，避免 aborted 误报
    userStopped.value = true
    a?.abort()
    isListening.value = false
    isProcessing.value = false
  } catch (err) {
    console.error('停止语音识别失败:', err)
  }
}

// 生命周期
onMounted(() => {
  initVoiceRecognition()
})

onUnmounted(() => {
  if (isListening.value) {
    stopListening()
  }
})

// 云端录音与转写辅助函数
const recordOnceToBase64 = async (durationMs = 3000): Promise<string> => {
  await ensureMicPermission().catch(() => {})
  const AudioCtx = (window as any).AudioContext || (window as any).webkitAudioContext
  const audioCtx = new AudioCtx()
  const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
  const source = audioCtx.createMediaStreamSource(stream)
  const processor = audioCtx.createScriptProcessor(4096, 1, 1)
  const chunks: Float32Array[] = []
  processor.onaudioprocess = (e: any) => {
    const input = e.inputBuffer.getChannelData(0)
    chunks.push(new Float32Array(input))
  }
  source.connect(processor); processor.connect(audioCtx.destination)
  isListening.value = true
  await new Promise(res => setTimeout(res, durationMs))
  processor.disconnect(); source.disconnect(); stream.getTracks().forEach(t => t.stop())
  isListening.value = false
  const float32 = concatFloat32(chunks)
  const pcm16k = resampleTo16k(float32, audioCtx.sampleRate)
  const base64 = toBase64(f32ToPcm16(pcm16k))
  return base64
}

const concatFloat32 = (arrs: Float32Array[]): Float32Array => {
  let len = 0; arrs.forEach(a => len += a.length)
  const out = new Float32Array(len)
  let off = 0; arrs.forEach(a => { out.set(a, off); off += a.length })
  return out
}

const resampleTo16k = (float32: Float32Array, srcRate: number): Float32Array => {
  const tgtRate = 16000
  const ratio = srcRate / tgtRate
  const newLen = Math.round(float32.length / ratio)
  const out = new Float32Array(newLen)
  for (let i = 0; i < newLen; i++) {
    const s = i * ratio
    const si = Math.floor(s)
    const frac = s - si
    const v0 = float32[si] || 0
    const v1 = float32[si + 1] || v0
    out[i] = v0 + (v1 - v0) * frac
  }
  return out
}

const f32ToPcm16 = (f32: Float32Array): Uint8Array => {
  const buffer = new ArrayBuffer(f32.length * 2)
  const view = new DataView(buffer)
  let offset = 0
  for (let i = 0; i < f32.length; i++) {
    let s = Math.max(-1, Math.min(1, f32[i]))
    view.setInt16(offset, s < 0 ? s * 0x8000 : s * 0x7FFF, true)
    offset += 2
  }
  return new Uint8Array(buffer)
}

const toBase64 = (u8: Uint8Array): string => {
  const CHUNK = 0x8000
  let str = ''
  for (let i = 0; i < u8.length; i += CHUNK) {
    str += String.fromCharCode.apply(null, Array.from(u8.subarray(i, i + CHUNK)))
  }
  return btoa(str)
}

// 云端模式录音功能
let mediaRecorder: MediaRecorder | null = null
let audioChunks: Blob[] = []
let silenceTimer: NodeJS.Timeout | null = null
let audioContext: AudioContext | null = null
let analyser: AnalyserNode | null = null
let microphone: MediaStreamAudioSourceNode | null = null

// 静音检测配置
const SILENCE_THRESHOLD = 0.01 // 静音阈值
const SILENCE_DURATION = 2000 // 静音持续时间（毫秒）

// 开始录音
const startRecording = async () => {
  try {
    await ensureMicPermission()
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    
    // 设置音频分析器用于静音检测
    audioContext = new (window.AudioContext || (window as any).webkitAudioContext)()
    analyser = audioContext.createAnalyser()
    microphone = audioContext.createMediaStreamSource(stream)
    
    analyser.fftSize = 512
    analyser.smoothingTimeConstant = 0.3
    microphone.connect(analyser)
    
    mediaRecorder = new MediaRecorder(stream)
    audioChunks = []
    
    mediaRecorder.ondataavailable = (event) => {
      if (event.data.size > 0) {
        audioChunks.push(event.data)
      }
    }
    
    mediaRecorder.onstop = async () => {
      const audioBlob = new Blob(audioChunks, { type: 'audio/wav' })
      recordedAudioData.value = await blobToBase64(audioBlob)
      
      // 停止所有音频轨道
      stream.getTracks().forEach(track => track.stop())
      
      // 清理音频上下文
      if (audioContext) {
        audioContext.close()
        audioContext = null
      }
      
      // 自动开始识别
      await recognizeRecording()
    }
    
    mediaRecorder.start()
    isRecording.value = true
    error.value = null
    emit('start')
    
    // 开始静音检测
    startSilenceDetection()
    
  } catch (err: any) {
    console.error('开始录音失败:', err)
    const msg = err?.message || '开始录音失败'
    error.value = msg
    emit('error', msg)
    ElMessage.error(msg)
  }
}

// 静音检测
const startSilenceDetection = () => {
  if (!analyser) return
  
  const bufferLength = analyser.frequencyBinCount
  const dataArray = new Uint8Array(bufferLength)
  
  const checkAudioLevel = () => {
    if (!analyser || !isRecording.value) return
    
    analyser.getByteFrequencyData(dataArray)
    
    // 计算音频能量
    let sum = 0
    for (let i = 0; i < bufferLength; i++) {
      sum += dataArray[i]
    }
    const average = sum / bufferLength / 255 // 归一化到0-1
    
    if (average < SILENCE_THRESHOLD) {
      // 检测到静音
      if (!silenceTimer) {
        silenceTimer = setTimeout(() => {
          if (isRecording.value) {
            console.log('检测到静音，自动停止录音')
            stopRecording()
          }
        }, SILENCE_DURATION)
      }
    } else {
      // 有声音，清除静音计时器
      if (silenceTimer) {
        clearTimeout(silenceTimer)
        silenceTimer = null
      }
    }
    
    // 继续检测
    if (isRecording.value) {
      requestAnimationFrame(checkAudioLevel)
    }
  }
  
  checkAudioLevel()
}

// 停止录音
const stopRecording = () => {
  if (mediaRecorder && mediaRecorder.state === 'recording') {
    mediaRecorder.stop()
    isRecording.value = false
    
    // 清除静音检测计时器
    if (silenceTimer) {
      clearTimeout(silenceTimer)
      silenceTimer = null
    }
    
    emit('end')
  }
}

// 将Blob转换为Base64
const blobToBase64 = (blob: Blob): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => {
      const result = reader.result as string
      // 移除data:audio/wav;base64,前缀
      const base64 = result.split(',')[1]
      resolve(base64)
    }
    reader.onerror = reject
    reader.readAsDataURL(blob)
  })
}

// 云端识别录音
const recognizeRecording = async () => {
  if (!recordedAudioData.value) {
    ElMessage.warning('没有录音数据')
    return
  }
  
  try {
    // 第一阶段：语音识别
    isRecognizing.value = true
    isRecorded.value = false
    
    const resp = await fetch('http://localhost:8080/api/speech/iat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ audioBase64: recordedAudioData.value })
    })
    
    const data = await resp.json()
    const recognizedText = data?.data || ''
    
    if (!recognizedText) {
      ElMessage.warning('未识别到有效语音')
      resetCloudState()
      return
    }
    
    // 识别成功，进入翻译阶段
    isRecognizing.value = false
    isTranslating.value = true
    
    // 模拟翻译处理时间（实际项目中这里可能是调用翻译API）
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 翻译完成，返回结果
    emit('result', recognizedText)
    ElMessage.success('语音识别和翻译完成')
    
    // 重置状态，准备下次录音
    resetCloudState()
    
  } catch (err: any) {
    console.error('语音识别失败:', err)
    const msg = err?.message || '语音识别失败'
    error.value = msg
    emit('error', msg)
    ElMessage.error(msg)
    resetCloudState()
  }
}

// 重置云端状态
const resetCloudState = () => {
  isRecording.value = false
  isRecorded.value = false
  isRecognizing.value = false
  isTranslating.value = false
  recordedAudioData.value = null
  isProcessing.value = false
  
  // 清理定时器
  if (silenceTimer) {
    clearTimeout(silenceTimer)
    silenceTimer = null
  }
}

// 云端模式按钮点击处理
const handleCloudVoiceClick = async () => {
  if (isRecording.value) {
    // 正在录音 -> 手动停止录音
    stopRecording()
  } else if (isRecognizing.value || isTranslating.value) {
    // 正在识别或翻译中，不允许操作
    return
  } else {
    // 初始状态 -> 开始录音，显示友好提示
    showVoiceInstructions()
    await startRecording()
  }
}

// 显示语音识别使用说明
const showVoiceInstructions = () => {
  ElMessageBox.alert(
    '请保持安静的环境，清晰地说出您的旅行需求。说完后请停顿2秒，系统会自动完成语音识别和翻译。请不要中断对话过程。',
    '语音识别提示',
    {
      confirmButtonText: '我知道了',
      type: 'info',
      showClose: false,
      closeOnClickModal: false,
      closeOnPressEscape: false
    }
  )
}

// 云端识别一次（保留原有方法作为备用）
const cloudRecognizeOnce = async () => {
  try {
    isProcessing.value = true
    const audioBase64 = await recordOnceToBase64(3000)
    const resp = await fetch('http://localhost:8080/api/speech/iat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ audioBase64 })
    })
    const data = await resp.json()
    const text = data?.data || ''
    if (text) {
      emit('result', text)
      ElMessage.success('识别成功')
    } else {
      ElMessage.warning('未识别到有效语音')
    }
  } catch (err: any) {
    console.error('云端识别失败:', err)
    const msg = err?.message || '云端识别失败'
    error.value = msg
    emit('error', msg)
    ElMessage.error(msg)
  } finally {
    isProcessing.value = false
    isListening.value = false
  }
}

// 切换按钮逻辑：本地 or 云端
const toggleVoiceInput = async () => {
  if (isListening.value) {
    stopListening()
    return
  }
  if (props.useCloud) {
    // 云端模式现在使用新的交互方式
    await handleCloudVoiceClick()
  } else {
    await startListening()
  }
}

// 暴露方法给父组件
defineExpose({
  startListening,
  stopListening,
  startRecording,
  stopRecording,
  resetCloudState,
  isListening: computed(() => isListening.value),
  isSupported: computed(() => isSupported.value),
  isRecording: computed(() => isRecording.value),
  isRecorded: computed(() => isRecorded.value)
})
</script>

<style scoped>
.voice-input-container {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.voice-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  background: #f0f9ff;
  border: 1px solid #0ea5e9;
  border-radius: 4px;
  font-size: 12px;
  color: #0369a1;
}

/* 识别状态样式 */
.voice-status-recognizing {
  background: #fef3c7;
  border-color: #f59e0b;
  color: #92400e;
}

/* 翻译状态样式 */
.voice-status-translating {
  background: #dcfce7;
  border-color: #22c55e;
  color: #166534;
}

.voice-wave {
  display: flex;
  gap: 2px;
}

.wave-dot {
  width: 4px;
  height: 4px;
  background: #0ea5e9;
  border-radius: 50%;
  animation: wave 1.4s infinite ease-in-out;
}

.wave-dot:nth-child(2) {
  animation-delay: -1.2s;
}

.wave-dot:nth-child(3) {
  animation-delay: -1.0s;
}

@keyframes wave {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1.2);
    opacity: 1;
  }
}

.voice-error {
  position: absolute;
  top: 100%;
  left: 0;
  margin-top: 4px;
  z-index: 1000;
  min-width: 200px;
}

.voice-text {
  white-space: nowrap;
}

/* 旋转动画 */
.animate-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>