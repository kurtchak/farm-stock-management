<!-- views/CreateItemView.vue -->
<template>
  <div class="min-h-screen bg-[#f5f7fa] flex flex-col">
    <div class="p-4 bg-white shadow-sm flex items-center">
      <button @click="goBack" class="mr-4">
        <ArrowLeft class="w-6 h-6" />
      </button>
      <h1 class="text-xl">Nová úroda</h1>
    </div>

    <div class="flex-grow p-4">
      <form @submit.prevent="submitForm" class="max-w-lg mx-auto bg-white rounded-lg shadow-sm p-6">
        <!-- Crop Selection/Creation -->
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">
            Plodina
          </label>
          <div class="flex gap-2">
            <select
                v-if="!isAddingNewCrop"
                v-model="selectedCropId"
                class="flex-1 p-2 border rounded-md"
                required
            >
              <option value="">Vyberte plodinu</option>
              <option v-for="crop in crops"
                      :key="crop.id"
                      :value="crop.id">
                {{ crop.name }}{{ crop.variety ? ' - ' + crop.variety : '' }}
              </option>
              <option value="new">+ Pridať novú plodinu</option>
            </select>

            <!-- New Crop Form -->
            <div v-else class="w-full space-y-3">
              <div class="flex gap-2">
                <input
                    v-model="newCrop.name"
                    placeholder="Názov plodiny"
                    class="flex-1 p-2 border rounded-md"
                    required
                />
                <input
                    v-model="newCrop.variety"
                    placeholder="Odroda"
                    class="flex-1 p-2 border rounded-md"
                    required
                />
              </div>
              <div class="flex gap-2">
                <button
                    type="button"
                    @click="saveNewCrop"
                    class="flex-1 bg-green-500 text-white p-2 rounded-md hover:bg-green-600"
                >
                  Uložiť
                </button>
                <button
                    type="button"
                    @click="cancelNewCrop"
                    class="flex-1 bg-gray-200 text-gray-800 p-2 rounded-md hover:bg-gray-300"
                >
                  Zrušiť
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Rest of the form remains the same but dependent on crop selection -->
        <div class="mb-4" v-if="selectedCropId || isAddingNewCrop">
          <!-- Description -->
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Popis
            </label>
            <textarea
                v-model="form.notes"
                rows="3"
                class="w-full p-2 border rounded-md"
            ></textarea>
          </div>

          <!-- Harvest Date -->
          <div class="mb-4 grid grid-cols-3 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Deň zberu
              </label>
              <input
                  v-model="form.harvestDay"
                  type="number"
                  min="1"
                  max="31"
                  required
                  class="w-full p-2 border rounded-md"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Mesiac
              </label>
              <select
                  v-model="form.harvestMonth"
                  required
                  class="w-full p-2 border rounded-md"
              >
                <option value="1">Január</option>
                <option value="2">Február</option>
                <option value="3">Marec</option>
                <option value="4">Apríl</option>
                <option value="5">Máj</option>
                <option value="6">Jún</option>
                <option value="7">Júl</option>
                <option value="8">August</option>
                <option value="9">September</option>
                <option value="10">Október</option>
                <option value="11">November</option>
                <option value="12">December</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Rok
              </label>
              <input
                  v-model="form.harvestYear"
                  type="number"
                  :min="new Date().getFullYear() - 1"
                  :max="new Date().getFullYear()"
                  required
                  class="w-full p-2 border rounded-md"
              />
            </div>
          </div>

          <!-- Amount and Unit -->
          <div class="mb-6 grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Množstvo
              </label>
              <input
                  v-model="form.quantity"
                  type="number"
                  step="0.01"
                  min="0"
                  required
                  class="w-full p-2 border rounded-md"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Jednotka
              </label>
              <select
                  v-model="form.unitOfMeasure"
                  required
                  class="w-full p-2 border rounded-md"
              >
                <option value="kg">Kilogramy</option>
                <option value="l">Litre</option>
                <option value="ks">Kusy</option>
              </select>
            </div>
          </div>
        </div>
        <!-- Submit Button -->
        <button
            v-if="(selectedCropId && selectedCropId !== 'new') || (isAddingNewCrop && newCrop.name && newCrop.variety)"
            type="submit"
            class="w-full bg-[#94c1e8] text-white py-3 rounded-lg hover:bg-[#7eb3e2]"
        >
          Vytvoriť úrodu
        </button>
      </form>
    </div>
    <!-- QR Code Modal -->
    <div v-if="showQRModal"
         class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-lg p-6 max-w-sm w-full">
        <h2 class="text-xl font-bold mb-4">Úroda vytvorená</h2>
        <div class="flex justify-center mb-4 qr-canvas">
          <VueQrcode :value="createdItem?.batchCode || ''" :options="{ width: 200 }" />
        </div>
        <p class="text-center mb-4 text-gray-600">
          Kód: {{ createdItem?.batchCode }}
        </p>
        <div class="flex flex-col gap-2">
          <div class="flex gap-2">
            <button @click="downloadQR"
                    class="flex-1 bg-blue-500 text-white py-2 rounded-lg hover:bg-blue-600 flex items-center justify-center gap-2">
              <Download class="w-4 h-4" />
              Stiahnuť
            </button>
            <button @click="printQR"
                    class="flex-1 bg-green-500 text-white py-2 rounded-lg hover:bg-green-600 flex items-center justify-center gap-2">
              <Printer class="w-4 h-4" />
              Tlačiť
            </button>
          </div>
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
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Download, Printer } from 'lucide-vue-next'
import VueQrcode from 'qrcode.vue'
import { cropApi, stockApi } from '../services/api'

const router = useRouter()
const crops = ref([])
const selectedCropId = ref('')
const isAddingNewCrop = ref(false)
const createdItem = ref(null)
const showQRModal = ref(false)

// Initialize form with all required fields
const form = ref({
  quantity: '',
  unitOfMeasure: 'kg',
  storageLocation: 'Hlavný sklad',
  qualityGrade: 'A',
  harvestDay: new Date().getDate(),
  harvestMonth: new Date().getMonth() + 1,
  harvestYear: new Date().getFullYear(),
  notes: ''
})

const newCrop = ref({
  name: '',
  variety: ''
})

// Watch for crop selection
watch(selectedCropId, (newValue) => {
  if (newValue === 'new') {
    isAddingNewCrop.value = true
    selectedCropId.value = ''
  } else {
    form.value.cropId = newValue
  }
})

onMounted(async () => {
  try {
    const response = await cropApi.getAllCrops()
    crops.value = response.data
  } catch (error) {
    console.error('Failed to fetch crops:', error)
  }
})

const submitForm = async () => {
  try {
    // Get crop info based on selection
    let cropName, cropVariety, cropDescription, cropStorageConditions, cropHarvestSeason

    if (selectedCropId.value) {
      // Using existing crop
      const selectedCrop = crops.value.find(c => c.id === selectedCropId.value)
      if (!selectedCrop) {
        alert('Prosím vyberte plodinu')
        return
      }
      cropName = selectedCrop.name
      cropVariety = selectedCrop.variety
      cropDescription = selectedCrop.description || ''
      cropStorageConditions = selectedCrop.storageConditions || ''
      cropHarvestSeason = selectedCrop.harvestSeason || ''
    } else if (isAddingNewCrop.value && newCrop.value.name && newCrop.value.variety) {
      // Creating new crop inline
      cropName = newCrop.value.name
      cropVariety = newCrop.value.variety
      cropDescription = ''
      cropStorageConditions = ''
      cropHarvestSeason = ''
    } else {
      alert('Prosím vyberte alebo vytvorte plodinu')
      return
    }

    const harvestDate = new Date(
        form.value.harvestYear,
        form.value.harvestMonth - 1,
        form.value.harvestDay
    )

    const stockData = {
      cropName: cropName,
      variety: cropVariety,
      description: cropDescription,
      storageConditions: cropStorageConditions,
      harvestSeason: cropHarvestSeason,
      quantity: parseFloat(form.value.quantity),
      unitOfMeasure: form.value.unitOfMeasure,
      storageLocation: form.value.storageLocation || 'Hlavný sklad',
      harvestDate: harvestDate.toISOString(),
      qualityGrade: form.value.qualityGrade || 'A',
      notes: form.value.notes || '',
      batchCode: generateBatchCode(cropName)
    }

    const response = await stockApi.createStock(stockData)
    const stock = response.data
    createdItem.value = stock
    showQRModal.value = true
  } catch (error) {
    console.error('Failed to create stock:', error)
    alert('Nepodarilo sa vytvoriť úrodu: ' + error.message)
  }
}

const generateBatchCode = (cropName) => {
  const date = new Date()
  const prefix = cropName.substring(0, 3).toUpperCase()
  const datePart = `${date.getFullYear()}${String(date.getMonth() + 1).padStart(2, '0')}`
  const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0')
  return `${prefix}-${datePart}-${random}`
}

const downloadQR = () => {
  const canvas = document.querySelector('.qr-canvas canvas')
  if (!canvas) return

  const url = canvas.toDataURL('image/png')
  const link = document.createElement('a')
  link.download = `QR-${createdItem.value.batchCode}.png`
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
      <title>QR Kód - ${createdItem.value.crop?.name || 'Úroda'}</title>
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
        <h2>${createdItem.value.crop?.name || 'Úroda'}</h2>
        <p>Kód: ${createdItem.value.batchCode}</p>
        <p>Množstvo: ${createdItem.value.quantity} ${createdItem.value.unitOfMeasure}</p>
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

const closeModal = () => {
  showQRModal.value = false
  router.push('/stocks')
}

const goBack = () => {
  router.back()
}

const saveNewCrop = async () => {
  try {
    const response = await cropApi.createCrop(newCrop.value)
    const createdCrop = response.data
    crops.value.push(createdCrop)
    selectedCropId.value = createdCrop.id
    isAddingNewCrop.value = false
    newCrop.value = { name: '', variety: '' }
  } catch (error) {
    console.error('Failed to save new crop:', error)
  }
}

const cancelNewCrop = () => {
  isAddingNewCrop.value = false
  newCrop.value = { name: '', variety: '' }
}
</script>