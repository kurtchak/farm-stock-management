<!-- views/ScanView.vue -->
<template>
  <div class="min-h-screen bg-[#f5f7fa] flex flex-col">
    <div class="p-4 bg-white shadow-sm flex items-center">
      <button @click="goBack" class="mr-4">
        <ArrowLeft class="w-6 h-6" />
      </button>
      <h1 class="text-xl">Naskenujte QR kód</h1>
    </div>

    <div class="flex-grow flex flex-col items-center justify-center p-4">
      <div class="w-full max-w-lg aspect-square bg-gray-100 rounded-lg overflow-hidden">
        <QrcodeStream @decode="onDecode" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { QrcodeStream } from 'vue-qrcode-reader'
import { ArrowLeft } from 'lucide-vue-next'
import { useRouter } from 'vue-router'

const router = useRouter()

const onDecode = (result) => {
  router.push({
    name: 'adjust-quantity',
    params: { stockId: result }
  })
}

const goBack = () => router.back()
</script>