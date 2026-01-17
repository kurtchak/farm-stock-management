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
          <h1 class="text-xl font-bold text-gray-800 ml-2">Skladové zásoby</h1>
        </div>
        <button
          v-if="stocks.length > 0"
          @click="printAllQR"
          class="bg-green-500 text-white py-2 px-4 rounded-lg hover:bg-green-600 active:bg-green-700 flex items-center gap-2"
        >
          <Printer class="w-4 h-4" />
          Tlačiť všetky QR
        </button>
      </div>
    </div>

    <!-- Main content -->
    <div class="flex-1 p-4">
      <div class="max-w-5xl mx-auto">

      <!-- Stock List Table -->
      <div class="bg-white rounded-lg shadow-sm overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50">
          <tr>
            <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Názov</th>
            <th class="px-4 py-3 text-left text-sm font-semibold text-gray-700">Množstvo</th>
            <th class="px-4 py-3 text-center text-sm font-semibold text-gray-700">QR kód</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
          <tr
            v-for="stock in stocks"
            :key="stock.id"
            class="hover:bg-gray-50 cursor-pointer"
            @touchstart="handleTouchStart(stock, $event)"
            @touchend="handleTouchEnd(stock, $event)"
            @touchmove="handleTouchMove"
            @mousedown="handleMouseDown(stock, $event)"
            @mouseup="handleMouseUp(stock, $event)"
            @mouseleave="handleMouseUp(stock, $event)"
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
                  @click.stop="showQRCode(stock)"
                  class="text-blue-600 hover:text-blue-800 p-1"
                  title="Zobraziť QR kód"
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
            <div class="cursor-pointer qr-canvas" @click="isFullscreen = true">
              <VueQrcode
                  :value="selectedStock?.batchCode || ''"
                  :options="{ width: 200 }"
                  class="border p-2"
              />
            </div>
          </div>
          <p class="text-center text-gray-600 mb-2">{{ selectedStock?.crop.name }}</p>
          <p class="text-center text-sm text-gray-500 mb-4">{{ selectedStock?.batchCode }}</p>

          <!-- Action Buttons -->
          <div class="flex gap-2">
            <button
              @click="downloadQR"
              class="flex-1 bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600 flex items-center justify-center gap-2"
            >
              <Download class="w-4 h-4" />
              Stiahnuť
            </button>
            <button
              @click="printQR"
              class="flex-1 bg-green-500 text-white py-2 px-4 rounded-lg hover:bg-green-600 flex items-center justify-center gap-2"
            >
              <Printer class="w-4 h-4" />
              Tlačiť
            </button>
          </div>
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

      <!-- Stock Detail Modal -->
      <div v-if="showDetailModal"
           class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50"
           @click.self="closeDetailModal">
        <div class="bg-white rounded-lg p-6 max-w-md w-full max-h-[90vh] overflow-y-auto">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-xl font-bold text-gray-800">Detail úrody</h2>
            <button @click="closeDetailModal" class="text-gray-500 hover:text-gray-700">
              <X class="w-5 h-5" />
            </button>
          </div>

          <div class="space-y-4">
            <!-- Crop Name -->
            <div>
              <p class="text-xs text-gray-500 uppercase tracking-wide mb-1">Názov úrody</p>
              <p class="text-lg font-bold text-gray-900">{{ detailStock?.crop.name }}</p>
            </div>

            <!-- Batch Code -->
            <div>
              <p class="text-xs text-gray-500 uppercase tracking-wide mb-1">Kód šarže</p>
              <p class="text-base text-gray-800 font-mono">{{ detailStock?.batchCode }}</p>
            </div>

            <!-- Quantity -->
            <div class="grid grid-cols-2 gap-4">
              <div>
                <p class="text-xs text-gray-500 uppercase tracking-wide mb-1">Množstvo</p>
                <p class="text-base font-semibold text-gray-800">{{ detailStock?.quantity }} {{ detailStock?.unitOfMeasure }}</p>
              </div>
              <div>
                <p class="text-xs text-gray-500 uppercase tracking-wide mb-1">Kvalita</p>
                <p class="text-base text-gray-800">{{ detailStock?.qualityGrade || '—' }}</p>
              </div>
            </div>

            <!-- Harvest Date -->
            <div>
              <p class="text-xs text-gray-500 uppercase tracking-wide mb-1">Dátum zberu</p>
              <p class="text-base text-gray-800">{{ formatDate(detailStock?.harvestDate) }}</p>
            </div>

            <!-- Storage Location -->
            <div>
              <p class="text-xs text-gray-500 uppercase tracking-wide mb-1">Miesto skladovania</p>
              <p class="text-base text-gray-800">{{ detailStock?.storageLocation || '—' }}</p>
            </div>

            <!-- Variety -->
            <div v-if="detailStock?.crop.variety">
              <p class="text-xs text-gray-500 uppercase tracking-wide mb-1">Odroda</p>
              <p class="text-base text-gray-800">{{ detailStock?.crop.variety }}</p>
            </div>

            <!-- Harvest Season -->
            <div v-if="detailStock?.crop.harvestSeason">
              <p class="text-xs text-gray-500 uppercase tracking-wide mb-1">Sezóna zberu</p>
              <p class="text-base text-gray-800">{{ detailStock?.crop.harvestSeason }}</p>
            </div>

            <!-- Crop Description -->
            <div v-if="detailStock?.crop.description">
              <p class="text-xs text-gray-500 uppercase tracking-wide mb-1">Popis plodiny</p>
              <p class="text-base text-gray-800">{{ detailStock?.crop.description }}</p>
            </div>

            <!-- Storage Conditions -->
            <div v-if="detailStock?.crop.storageConditions">
              <p class="text-xs text-gray-500 uppercase tracking-wide mb-1">Podmienky skladovania</p>
              <p class="text-base text-gray-800">{{ detailStock?.crop.storageConditions }}</p>
            </div>

            <!-- Notes -->
            <div v-if="detailStock?.notes">
              <p class="text-xs text-gray-500 uppercase tracking-wide mb-1">Poznámky</p>
              <p class="text-base text-gray-800">{{ detailStock?.notes }}</p>
            </div>
          </div>

          <div class="mt-6 space-y-2">
            <button
              @click="confirmDeleteFromDetail"
              class="w-full bg-red-500 text-white py-2 px-4 rounded-lg hover:bg-red-600 flex items-center justify-center gap-2"
            >
              <Trash2 class="w-4 h-4" />
              Skryť úrodu
            </button>
            <button
              @click="closeDetailModal"
              class="w-full bg-gray-200 text-gray-800 py-2 px-4 rounded-lg hover:bg-gray-300"
            >
              Zavrieť
            </button>
          </div>
        </div>
      </div>

      <!-- Delete Confirmation Modal -->
      <div v-if="showDeleteConfirm"
           class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-[60]"
           @click.self="cancelDelete">
        <div class="bg-white rounded-lg p-6 max-w-sm w-full">
          <h2 class="text-xl font-bold mb-4">Skryť úrodu?</h2>
          <p class="text-gray-600 mb-2">
            <strong>{{ stockToDelete?.crop.name }}</strong>
          </p>
          <p class="text-sm text-gray-500 mb-4">
            Dátum zberu: {{ formatDate(stockToDelete?.harvestDate) }}
            <br>
            Množstvo: {{ stockToDelete?.quantity }} {{ stockToDelete?.unitOfMeasure }}
          </p>
          <div class="bg-blue-50 border border-blue-200 rounded-lg p-3 mb-4">
            <p class="text-sm text-blue-800">
              <strong>ℹ️ Informácia:</strong><br>
              Úroda bude skrytá zo zoznamu, ale zostane uložená v systéme. Všetky záznamy o naskladneniach a výberoch budú zachované.
            </p>
          </div>
          <div class="flex gap-2">
            <button
              @click="deleteStock"
              class="flex-1 bg-orange-500 text-white py-2 px-4 rounded-lg hover:bg-orange-600"
            >
              Skryť úrodu
            </button>
            <button
              @click="cancelDelete"
              class="flex-1 bg-gray-200 text-gray-800 py-2 px-4 rounded-lg hover:bg-gray-300"
            >
              Zrušiť
            </button>
          </div>
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { QrCode, X, Download, Printer, ArrowLeft, Trash2 } from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import VueQrcode from 'qrcode.vue'
import { useStockStore } from '../stores/stock'
import { stockApi } from '../services/api'

const router = useRouter()
const stockStore = useStockStore()

const stocks = ref([])
const showModal = ref(false)
const selectedStock = ref(null)
const isFullscreen = ref(false)
const showDeleteConfirm = ref(false)
const stockToDelete = ref(null)
const showDetailModal = ref(false)
const detailStock = ref(null)
const deleteThreshold = computed(() => stockStore.deleteThreshold)

// Long press state
let longPressTimer = null
let longPressStock = null
let wasLongPress = false

onMounted(async () => {
  try {
    await stockStore.fetchStockConfig()
    const response = await stockApi.getAllStocks()
    stocks.value = response.data
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

const navigateBack = () => {
  router.push('/farm')
}

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

const downloadQR = () => {
  const canvas = document.querySelector('.qr-canvas canvas')
  if (!canvas) return

  const url = canvas.toDataURL('image/png')
  const link = document.createElement('a')
  link.download = `QR-${selectedStock.value.batchCode}.png`
  link.href = url
  link.click()
}

const printQR = () => {
  const canvas = document.querySelector('.qr-canvas canvas')
  if (!canvas) return

  const printWindow = window.open('', '_blank')
  const imageUrl = canvas.toDataURL('image/png')

  printWindow.document.write(`
    <!DOCTYPE html>
    <html>
    <head>
      <title>QR Kód - ${selectedStock.value.crop.name}</title>
      <style>
        body {
          margin: 0;
          padding: 20px;
          text-align: center;
          font-family: Arial, sans-serif;
        }
        img {
          max-width: 100%;
          height: auto;
        }
        .info {
          margin-top: 20px;
          font-size: 14px;
        }
        @media print {
          body {
            padding: 0;
          }
        }
      </style>
    </head>
    <body>
      <img src="${imageUrl}" alt="QR Code" />
      <div class="info">
        <h2>${selectedStock.value.crop.name}</h2>
        <p>Kód: ${selectedStock.value.batchCode}</p>
        <p>Množstvo: ${selectedStock.value.quantity} ${selectedStock.value.unitOfMeasure}</p>
      </div>
      <script>
        window.onload = function() {
          window.print();
          window.onafterprint = function() {
            window.close();
          }
        }
      <\/script>
    </body>
    </html>
  `)
  printWindow.document.close()
}

const printAllQR = async () => {
  // Generate QR codes as data URLs first
  const qrDataPromises = stocks.value.map(async (stock) => {
    // Create temporary canvas
    const canvas = document.createElement('canvas')
    const QRCode = (await import('qrcode')).default

    await QRCode.toCanvas(canvas, stock.batchCode, {
      width: 150,
      margin: 1
    })

    return {
      id: stock.id,
      dataUrl: canvas.toDataURL('image/png'),
      name: stock.crop.name,
      batchCode: stock.batchCode,
      quantity: stock.quantity,
      unit: stock.unitOfMeasure
    }
  })

  const qrData = await Promise.all(qrDataPromises)

  // Create print document with all QR codes as images
  const qrCodesHtml = qrData.map(item => `
    <div class="qr-item">
      <img src="${item.dataUrl}" alt="QR Code" class="qr-image" />
      <div class="qr-info">
        <p class="qr-name">${item.name}</p>
        <p class="qr-code">${item.batchCode}</p>
        <p class="qr-quantity">${item.quantity} ${item.unit}</p>
      </div>
    </div>
  `).join('')

  const printWindow = window.open('', '_blank')
  printWindow.document.write(`
    <!DOCTYPE html>
    <html>
    <head>
      <title>QR Kódy - Všetky položky</title>
      <style>
        @page {
          size: A4;
          margin: 10mm;
        }

        body {
          margin: 0;
          padding: 0;
          font-family: Arial, sans-serif;
        }

        .container {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 8mm;
          padding: 5mm;
        }

        .qr-item {
          border: 1px solid #e0e0e0;
          padding: 8px;
          text-align: center;
          break-inside: avoid;
          page-break-inside: avoid;
          background: white;
          border-radius: 4px;
        }

        .qr-image {
          width: 100%;
          max-width: 150px;
          height: auto;
          margin: 0 auto;
          display: block;
        }

        .qr-info {
          margin-top: 8px;
        }

        .qr-name {
          font-weight: bold;
          font-size: 12px;
          margin: 4px 0;
          color: #333;
        }

        .qr-code {
          font-size: 10px;
          color: #666;
          margin: 2px 0;
          font-family: monospace;
        }

        .qr-quantity {
          font-size: 11px;
          color: #444;
          margin: 2px 0;
        }

        @media print {
          body {
            -webkit-print-color-adjust: exact;
            print-color-adjust: exact;
          }

          .container {
            padding: 0;
          }
        }
      </style>
    </head>
    <body>
      <div class="container">
        ${qrCodesHtml}
      </div>
      <script>
        window.onload = function() {
          // Wait a bit for images to load, then print
          setTimeout(function() {
            window.print();
            window.onafterprint = function() {
              window.close();
            }
          }, 300);
        }
      <\/script>
    </body>
    </html>
  `)
  printWindow.document.close()
}

// Detail modal handlers
const showDetailView = (stock) => {
  detailStock.value = stock
  showDetailModal.value = true
}

const closeDetailModal = () => {
  detailStock.value = null
  showDetailModal.value = false
}

// Long press handlers
const handleTouchStart = (stock, event) => {
  // Ignore clicks on buttons
  if (event.target.closest('button')) return

  wasLongPress = false
  longPressStock = stock

  longPressTimer = setTimeout(() => {
    if (longPressStock) {
      wasLongPress = true
      confirmDelete(stock)
    }
  }, 800) // 800ms for long press
}

const handleTouchEnd = (stock, event) => {
  // Ignore clicks on buttons
  if (event.target.closest('button')) return

  if (longPressTimer) {
    clearTimeout(longPressTimer)
    longPressTimer = null
  }

  // If it wasn't a long press, show detail modal
  if (!wasLongPress && longPressStock) {
    showDetailView(longPressStock)
  }

  longPressStock = null
  wasLongPress = false
}

const handleTouchMove = () => {
  // Cancel long press if user moves finger
  if (longPressTimer) {
    clearTimeout(longPressTimer)
    longPressTimer = null
  }
  longPressStock = null
  wasLongPress = false
}

const handleMouseDown = (stock, event) => {
  // Ignore clicks on buttons
  if (event.target.closest('button')) return

  wasLongPress = false
  longPressStock = stock

  longPressTimer = setTimeout(() => {
    if (longPressStock) {
      wasLongPress = true
      confirmDelete(stock)
    }
  }, 800) // 800ms for long press
}

const handleMouseUp = (stock, event) => {
  // Ignore clicks on buttons
  if (event.target.closest('button')) return

  if (longPressTimer) {
    clearTimeout(longPressTimer)
    longPressTimer = null
  }

  // If it wasn't a long press, show detail modal
  if (!wasLongPress && longPressStock) {
    showDetailView(longPressStock)
  }

  longPressStock = null
  wasLongPress = false
}

const confirmDelete = (stock) => {
  stockToDelete.value = stock
  showDeleteConfirm.value = true
}

const confirmDeleteFromDetail = () => {
  stockToDelete.value = detailStock.value
  showDeleteConfirm.value = true
}

const cancelDelete = () => {
  stockToDelete.value = null
  showDeleteConfirm.value = false
}

const deleteStock = async () => {
  try {
    await stockApi.deleteStock(stockToDelete.value.id)

    // Remove from local list
    stocks.value = stocks.value.filter(s => s.id !== stockToDelete.value.id)

    // Close both modals
    showDeleteConfirm.value = false
    showDetailModal.value = false
    stockToDelete.value = null
    detailStock.value = null
  } catch (error) {
    console.error('Failed to delete stock:', error)
    const errorMessage = error.response?.data?.message || 'Nepodarilo sa zmazať položku'
    alert(errorMessage)
    showDeleteConfirm.value = false
    stockToDelete.value = null
  }
}
</script>