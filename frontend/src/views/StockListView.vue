<template>
  <div class="min-h-screen bg-[#f5f7fa] p-4">
    <div class="max-w-5xl mx-auto">
      <h1 class="text-2xl mb-6">Skladové zásoby</h1>

      <!-- Stock List Table -->
      <div class="bg-white rounded-lg shadow-sm overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50">
          <tr>
            <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Názov</th>
            <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Množstvo</th>
            <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Dátum zberu</th>
            <th class="px-4 py-3 text-right text-sm font-semibold text-gray-700">Akcie</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
          <tr v-for="stock in stocks" :key="stock.id" class="hover:bg-gray-50">
            <td class="px-4 py-3">{{ stock.crop.name }}</td>
            <td class="px-4 py-3">{{ stock.quantity }} {{ stock.unitOfMeasure }}</td>
            <td class="px-4 py-3">{{ formatDate(stock.harvestDate) }}</td>
            <td class="px-4 py-3 text-right">
              <button
                  @click="showQRCode(stock)"
                  class="text-blue-600 hover:text-blue-800"
              >
                <QrCode class="w-5 h-5" />
              </button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- Regular QR Modal -->
      <div v-if="showModal && !isFullscreen"
           class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
        <div class="bg-white rounded-lg p-6 max-w-sm w-full">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-bold">QR Kód</h2>
            <button @click="closeModal" class="text-gray-500 hover:text-gray-700">
              <X class="w-5 h-5" />
            </button>
          </div>
          <div class="flex justify-center mb-4">
            <div class="cursor-pointer" @click="isFullscreen = true">
              <VueQrcode
                  :value="selectedStock?.batchCode || ''"
                  :options="{ width: 200 }"
                  class="border p-2"
              />
            </div>
          </div>
          <p class="text-center text-gray-600 mb-2">{{ selectedStock?.crop.name }}</p>
          <p class="text-center text-sm text-gray-500">{{ selectedStock?.batchCode }}</p>
        </div>
      </div>

      <!-- Fullscreen QR -->
      <div v-if="isFullscreen"
           class="fixed inset-0 bg-black flex flex-col items-center justify-center z-50">
        <div class="absolute top-4 right-4">
          <button @click="isFullscreen = false" class="text-white hover:text-gray-300">
            <X class="w-8 h-8" />
          </button>
        </div>
        <div class="p-4 bg-white rounded-lg">
          <VueQrcode
              :value="selectedStock?.batchCode || ''"
              :options="{ width: qrSize }"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { QrCode, X } from 'lucide-vue-next'
import VueQrcode from 'qrcode.vue'

const stocks = ref([])
const showModal = ref(false)
const selectedStock = ref(null)
const isFullscreen = ref(false)

onMounted(async () => {
  try {
    const response = await fetch('/api/stock')
    stocks.value = await response.json()
  } catch (error) {
    console.error('Failed to load stocks:', error)
  }
})

const windowSize = ref({
  width: window.innerWidth,
  height: window.innerHeight
})

// Calculate QR size based on window dimensions
const qrSize = computed(() => {
  const margin = 48 // 24px margin on each side
  return Math.min(windowSize.value.width, windowSize.value.height) - margin
})

// Update window size on resize
const handleResize = () => {
  windowSize.value = {
    width: window.innerWidth,
    height: window.innerHeight
  }
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('sk-SK')
}

const showQRCode = (stock) => {
  selectedStock.value = stock
  showModal.value = true
  isFullscreen.value = false
}

const closeModal = () => {
  showModal.value = false
  selectedStock.value = null
  isFullscreen.value = false
}
</script>