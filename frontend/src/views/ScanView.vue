<template>
  <div class="min-h-screen bg-[#f5f7fa] flex flex-col">
    <div class="p-4 bg-white shadow-sm flex items-center">
      <button @click="goBack" class="mr-4">
        <ArrowLeft class="w-6 h-6" />
      </button>
      <h1 class="text-xl">Naskenujte QR kód</h1>
    </div>

    <div class="flex-grow flex flex-col items-center justify-center p-4">
      <div class="w-full max-w-lg aspect-square bg-gray-100 rounded-lg overflow-hidden mb-4">
        <QrcodeStream @detect="onDetect" @error="onError" />
      </div>

      <!-- Manual Selection Fallback -->
      <button
        @click="showManualSelect = true"
        class="bg-white text-gray-700 px-6 py-3 rounded-lg shadow-sm hover:bg-gray-50 active:bg-gray-100 transition-colors font-medium"
      >
        Manuálny výber úrody
      </button>
    </div>

    <!-- Error Modal - Only shown when stock not found -->
    <div v-if="showModal"
         class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
      <div class="bg-white rounded-lg p-6 max-w-sm w-full">
        <div class="text-center">
          <div class="mb-4 text-red-500">
            <XCircle class="w-12 h-12 mx-auto" />
          </div>
          <h2 class="text-xl font-bold mb-2">Položka nenájdená</h2>
          <p class="text-gray-600 mb-4">Skúste naskenovať QR kód znova alebo použite manuálny výber</p>
          <button @click="closeModal"
                  class="w-full bg-gray-200 text-gray-800 py-2 rounded-lg hover:bg-gray-300">
            Zavrieť
          </button>
        </div>
      </div>
    </div>

    <!-- Manual Selection Modal -->
    <div v-if="showManualSelect"
         class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50"
         @click.self="showManualSelect = false">
      <div class="bg-white rounded-lg p-6 max-w-md w-full">
        <h2 class="text-xl font-bold mb-4">Vyberte úrodu</h2>

        <div v-if="loadingStocks" class="text-center py-8">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-green-600 mx-auto"></div>
        </div>

        <div v-else>
          <select
            v-model="selectedStockId"
            class="w-full p-3 border rounded-lg mb-4"
            @change="onManualSelect"
          >
            <option value="">-- Vyberte úrodu --</option>
            <option v-for="stock in availableStocks" :key="stock.id" :value="stock.id">
              {{ stock.crop.name }} - {{ stock.quantity }} {{ stock.unitOfMeasure }}
            </option>
          </select>

          <button
            @click="showManualSelect = false"
            class="w-full bg-gray-200 text-gray-800 py-2 rounded-lg hover:bg-gray-300"
          >
            Zrušiť
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, XCircle } from 'lucide-vue-next'
import { QrcodeStream } from 'vue-qrcode-reader'
import { stockApi } from '../services/api'

const router = useRouter()
const route = useRoute()
const showModal = ref(false)
const foundStock = ref(null)
const isProcessing = ref(false)
const showManualSelect = ref(false)
const availableStocks = ref([])
const loadingStocks = ref(false)
const selectedStockId = ref('')

// Get mode from route query (in or out)
const mode = computed(() => route.query.mode || 'in')

onMounted(async () => {
  loadingStocks.value = true
  try {
    const response = await stockApi.getAllStocks()
    availableStocks.value = response.data
  } catch (error) {
    console.error('Failed to load stocks:', error)
  } finally {
    loadingStocks.value = false
  }
})

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
      // Directly proceed with the mode from route
      proceedToAdjust(mode.value)
    } else {
      foundStock.value = null
      showModal.value = true
    }
  } catch (error) {
    console.error('API error:', error)
    foundStock.value = null
    showModal.value = true
  } finally {
    isProcessing.value = false
  }
}

const proceedToAdjust = (type) => {
  if (foundStock.value) {
    router.push({
      name: 'adjust-quantity',
      params: { stockId: foundStock.value.batchCode },
      query: { type }  // 'in' alebo 'out'
    })
  }
}

const onManualSelect = () => {
  if (selectedStockId.value) {
    const stock = availableStocks.value.find(s => s.id === parseInt(selectedStockId.value))
    if (stock) {
      foundStock.value = stock
      showManualSelect.value = false
      selectedStockId.value = ''
      // Directly proceed with the mode from route
      proceedToAdjust(mode.value)
    }
  }
}

const closeModal = () => {
  showModal.value = false
  foundStock.value = null
}

const goBack = () => router.back()
</script>