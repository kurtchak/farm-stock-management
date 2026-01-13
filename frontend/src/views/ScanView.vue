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
        <QrcodeStream @detect="onDetect" @error="onError" />
      </div>
    </div>

    <!-- Feedback Modal -->
    <div v-if="showModal"
         class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
      <div class="bg-white rounded-lg p-6 max-w-sm w-full">
        <!-- Success Case -->
        <div v-if="foundStock" class="text-center">
          <div class="mb-4 text-green-500">
            <CheckCircle class="w-12 h-12 mx-auto" />
          </div>
          <h2 class="text-xl font-bold mb-2">Položka nájdená</h2>
          <p class="text-gray-600 mb-4">{{ foundStock.crop.name }}</p>
          <div class="flex gap-2">
            <button @click="proceedToAdjust"
                    class="flex-1 bg-green-500 text-white py-2 rounded-lg hover:bg-green-600">
              Pokračovať
            </button>
            <button @click="closeModal"
                    class="flex-1 bg-gray-200 text-gray-800 py-2 rounded-lg hover:bg-gray-300">
              Zrušiť
            </button>
          </div>
        </div>

        <!-- Error Case -->
        <div v-else class="text-center">
          <div class="mb-4 text-red-500">
            <XCircle class="w-12 h-12 mx-auto" />
          </div>
          <h2 class="text-xl font-bold mb-2">Položka nenájdená</h2>
          <p class="text-gray-600 mb-4">Skúste naskenovať QR kód znova</p>
          <button @click="closeModal"
                  class="w-full bg-gray-200 text-gray-800 py-2 rounded-lg hover:bg-gray-300">
            Zavrieť
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, CheckCircle, XCircle } from 'lucide-vue-next'
import { QrcodeStream } from 'vue-qrcode-reader'

const router = useRouter()
const showModal = ref(false)
const foundStock = ref(null)
const isProcessing = ref(false)

const onError = (error) => {
  console.error('Camera error:', error)
}

const onDetect = async (detectedCodes) => {
  console.log('Detected:', detectedCodes)

  if (!detectedCodes || detectedCodes.length === 0) return
  if (isProcessing.value) return
  isProcessing.value = true

  const qrValue = detectedCodes[0].rawValue
  console.log('QR value:', qrValue)

  try {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/api/stock/batch/${qrValue}`)

    if (response.ok) {
      foundStock.value = await response.json()
    } else {
      foundStock.value = null
    }
    showModal.value = true
  } catch (error) {
    console.error('API error:', error)
    foundStock.value = null
    showModal.value = true
  } finally {
    isProcessing.value = false
  }
}

const proceedToAdjust = () => {
  if (foundStock.value) {
    router.push({
      name: 'adjust-quantity',
      params: { stockId: foundStock.value.batchCode }
    })
  }
}

const closeModal = () => {
  showModal.value = false
  foundStock.value = null
}

const goBack = () => router.back()
</script>