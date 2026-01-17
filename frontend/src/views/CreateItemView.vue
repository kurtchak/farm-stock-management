<!-- views/CreateItemView.vue -->
<template>
  <div class="min-h-screen bg-[#f5f7fa] flex flex-col">
    <div class="p-4 bg-white shadow-sm flex items-center">
      <button @click="goBack" class="mr-4">
        <ArrowLeft class="w-6 h-6" />
      </button>
      <h1 class="text-xl">Nová položka</h1>
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
                {{ crop.name }} ({{ crop.variety }})
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
                  v-model="form.unit"
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
            type="submit"
            class="w-full bg-[#94c1e8] text-white py-3 rounded-lg hover:bg-[#7eb3e2]"
        >
          Vytvoriť položku
        </button>
      </form>
    </div>
    <!-- QR Code Modal -->
    <div v-if="showQRModal"
         class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-lg p-6 max-w-sm w-full">
        <h2 class="text-xl font-bold mb-4">Položka vytvorená</h2>
        <div class="flex justify-center mb-4 qr-canvas">
          <VueQrcode :value="createdItem?.batchCode || ''" :options="{ width: 200 }" />
        </div>
        <p class="text-center mb-4 text-gray-600">
          Kód: {{ createdItem?.batchCode }}
        </p>
        <div class="flex gap-2">
          <button @click="downloadQR"
                  class="flex-1 bg-[#94c1e8] text-white py-2 rounded-lg hover:bg-[#7eb3e2]">
            Stiahnuť QR
          </button>
          <button @click="closeModal"
                  class="flex-1 bg-gray-200 text-gray-800 py-2 rounded-lg hover:bg-gray-300">
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
import { ArrowLeft } from 'lucide-vue-next'
import VueQrcode from 'qrcode.vue'

const router = useRouter()
const crops = ref([])
const selectedCropId = ref('')
const isAddingNewCrop = ref(false)
const createdItem = ref(null)
const showQRModal = ref(false)

// Initialize form with all required fields
const form = ref({
  name: '',
  variety: '',
  quantity: '',
  unitOfMeasure: 'kg',
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
    const response = await fetch(`${import.meta.env.VITE_API_URL}/api/crops`)
    crops.value = await response.json()
  } catch (error) {
    console.error('Failed to fetch crops:', error)
  }
})

const submitForm = async () => {
  try {
    const stockData = {
      name: newCrop.value.name,
      variety: newCrop.value.variety,
      quantity: form.value.quantity,
      unitOfMeasure: form.value.unitOfMeasure,
      harvestDate: new Date(
          form.value.harvestYear,
          form.value.harvestMonth - 1,
          form.value.harvestDay
      ).toISOString(),
      notes: form.value.notes
    }

    const response = await fetch(`${import.meta.env.VITE_API_URL}/api/stock`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(stockData)
    })

    if (!response.ok) throw new Error('Failed to create stock')
    const stock = await response.json()
    createdItem.value = stock // Contains batchCode
    showQRModal.value = true
  } catch (error) {
    console.error('Failed to create stock:', error)
  }
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

const closeModal = () => {
  showQRModal.value = false
  router.push('/stocks')
}

const goBack = () => {
  router.back()
}

const saveNewCrop = async () => {
  try {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/api/crops`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(newCrop.value)
    })

    if (!response.ok) throw new Error('Failed to create crop')

    const createdCrop = await response.json()
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