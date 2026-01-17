<template>
  <div class="min-h-screen bg-[#f5f7fa] flex flex-col">
    <!-- Header with back button -->
    <div class="bg-white shadow-sm p-4">
      <div class="flex items-center justify-between max-w-5xl mx-auto">
        <div class="flex items-center">
          <button
            @click="navigateBack"
            class="flex items-center text-gray-500 active:text-gray-700 transition-colors p-2 -ml-2"
          >
            <ArrowLeft class="w-5 h-5" />
          </button>
          <h1 class="text-xl font-bold text-gray-800 ml-2">Odstránené úrody</h1>
        </div>
      </div>
    </div>

    <!-- Main content -->
    <div class="flex-1 p-4">
      <div class="max-w-5xl mx-auto">

        <!-- Loading State -->
        <div v-if="loading" class="flex items-center justify-center py-20">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600"></div>
        </div>

        <!-- Empty State -->
        <div v-else-if="deletedStocks.length === 0" class="bg-white rounded-lg shadow-sm p-8 text-center">
          <div class="text-gray-400 mb-4">
            <Archive class="w-16 h-16 mx-auto" />
          </div>
          <h3 class="text-lg font-semibold text-gray-700 mb-2">Žiadne odstránené úrody</h3>
          <p class="text-gray-500">Všetky úrody sú aktívne.</p>
        </div>

        <!-- Deleted Stock List Table -->
        <div v-else class="bg-white rounded-lg shadow-sm overflow-hidden">
          <table class="w-full">
            <thead class="bg-gray-50">
            <tr>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Názov</th>
              <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Množstvo</th>
              <th class="px-4 py-3 text-center text-sm font-semibold text-gray-700">Akcia</th>
            </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
            <tr
              v-for="stock in deletedStocks"
              :key="stock.id"
              class="hover:bg-gray-50"
            >
              <td class="px-4 py-3">
                <div>
                  <p class="font-medium text-gray-900">{{ stock.crop.name }}</p>
                  <p class="text-sm text-gray-500">{{ formatDate(stock.harvestDate) }}</p>
                </div>
              </td>
              <td class="px-4 py-3">{{ stock.quantity }} {{ stock.unitOfMeasure }}</td>
              <td class="px-4 py-3 text-center">
                <button
                  @click="confirmRestore(stock)"
                  class="text-green-600 hover:text-green-800 px-3 py-1 rounded-lg border border-green-600 hover:bg-green-50 text-sm font-medium transition-colors"
                  title="Obnoviť úrodu"
                >
                  Obnoviť
                </button>
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Restore Confirmation Modal -->
    <div v-if="showRestoreConfirm"
         class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50"
         @click.self="cancelRestore">
      <div class="bg-white rounded-lg p-6 max-w-sm w-full">
        <h2 class="text-xl font-bold mb-4">Obnoviť úrodu?</h2>
        <p class="text-gray-600 mb-2">
          <strong>{{ stockToRestore?.crop.name }}</strong>
        </p>
        <p class="text-sm text-gray-500 mb-4">
          Dátum zberu: {{ formatDate(stockToRestore?.harvestDate) }}
          <br>
          Množstvo: {{ stockToRestore?.quantity }} {{ stockToRestore?.unitOfMeasure }}
        </p>
        <div class="bg-green-50 border border-green-200 rounded-lg p-3 mb-4">
          <p class="text-sm text-green-800">
            <strong>✓ Informácia:</strong><br>
            Úroda bude obnovená a opäť sa zobrazí v aktívnych zásob ách.
          </p>
        </div>
        <div class="flex gap-2">
          <button
            @click="restoreStock"
            class="flex-1 bg-green-500 text-white py-2 px-4 rounded-lg hover:bg-green-600"
          >
            Obnoviť
          </button>
          <button
            @click="cancelRestore"
            class="flex-1 bg-gray-200 text-gray-800 py-2 px-4 rounded-lg hover:bg-gray-300"
          >
            Zrušiť
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ArrowLeft, Archive } from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import { stockApi } from '../services/api'

const router = useRouter()

const deletedStocks = ref([])
const loading = ref(true)
const showRestoreConfirm = ref(false)
const stockToRestore = ref(null)

onMounted(async () => {
  await loadDeletedStocks()
})

const loadDeletedStocks = async () => {
  loading.value = true
  try {
    const response = await stockApi.getDeletedStocks()
    deletedStocks.value = response.data
  } catch (error) {
    console.error('Failed to load deleted stocks:', error)
  } finally {
    loading.value = false
  }
}

const navigateBack = () => {
  router.push('/farm')
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('sk-SK')
}

const confirmRestore = (stock) => {
  stockToRestore.value = stock
  showRestoreConfirm.value = true
}

const cancelRestore = () => {
  stockToRestore.value = null
  showRestoreConfirm.value = false
}

const restoreStock = async () => {
  try {
    await stockApi.restoreStock(stockToRestore.value.id)

    // Remove from local list
    deletedStocks.value = deletedStocks.value.filter(s => s.id !== stockToRestore.value.id)

    // Close modal
    showRestoreConfirm.value = false
    stockToRestore.value = null
  } catch (error) {
    console.error('Failed to restore stock:', error)
    const errorMessage = error.response?.data?.message || 'Nepodarilo sa obnoviť položku'
    alert(errorMessage)
    showRestoreConfirm.value = false
    stockToRestore.value = null
  }
}
</script>