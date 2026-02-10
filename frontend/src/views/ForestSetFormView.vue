<template>
  <div class="min-h-screen bg-[#f5f7fa] flex flex-col">
    <div class="p-4 bg-white shadow-sm flex items-center">
      <button @click="goBack" class="mr-4">
        <ArrowLeft class="w-6 h-6" />
      </button>
      <h1 class="text-xl font-semibold">{{ isEdit ? 'Upraviť zostavu' : 'Nová zostava' }}</h1>
    </div>

    <div class="flex-grow p-4">
      <!-- Loading -->
      <div v-if="loadingData" class="flex items-center justify-center py-20">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-green-700"></div>
      </div>

      <form v-else @submit.prevent="submitForm" class="max-w-lg mx-auto bg-white rounded-lg shadow-sm p-6">
        <!-- Name -->
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">Názov zostavy</label>
          <input
            v-model="form.name"
            type="text"
            required
            placeholder="Napr. Štandardná výsadba"
            class="w-full p-2 border rounded-md"
          />
        </div>

        <!-- Description -->
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">Popis</label>
          <textarea
            v-model="form.description"
            rows="2"
            class="w-full p-2 border rounded-md"
            placeholder="Voliteľný popis..."
          ></textarea>
        </div>

        <!-- Color -->
        <div class="mb-5">
          <label class="block text-sm font-medium text-gray-700 mb-2">Farba</label>
          <div class="flex gap-2 flex-wrap">
            <button
              v-for="c in colorPresets"
              :key="c"
              type="button"
              @click="form.color = c"
              :class="[
                'w-8 h-8 rounded-full border-2 transition-all',
                form.color === c ? 'border-gray-800 scale-110' : 'border-transparent'
              ]"
              :style="{ backgroundColor: c }"
            ></button>
          </div>
        </div>

        <!-- Components -->
        <div class="mb-5">
          <div class="flex items-center justify-between mb-2">
            <label class="text-sm font-medium text-gray-700">Komponenty</label>
            <button
              type="button"
              @click="addComponent"
              class="text-sm text-[#2d6a4f] font-medium flex items-center gap-1"
            >
              <Plus class="w-4 h-4" /> Pridať
            </button>
          </div>

          <div v-if="form.items.length === 0" class="bg-gray-50 rounded-lg p-4 text-center text-sm text-gray-400">
            Pridajte aspoň jeden komponent
          </div>

          <div v-for="(comp, idx) in form.items" :key="idx" class="flex gap-2 mb-2 items-center">
            <select
              v-model="comp.forestItemId"
              required
              class="flex-1 p-2 border rounded-md text-sm"
            >
              <option value="">Vyberte materiál</option>
              <option
                v-for="item in availableItems"
                :key="item.id"
                :value="item.id"
              >
                {{ item.name }} ({{ item.quantity }} {{ item.unit }})
              </option>
            </select>
            <input
              v-model="comp.quantity"
              type="number"
              min="1"
              step="1"
              required
              placeholder="Počet"
              class="w-20 p-2 border rounded-md text-sm"
            />
            <button
              type="button"
              @click="removeComponent(idx)"
              class="w-8 h-8 flex items-center justify-center text-red-500 active:text-red-700"
            >
              <X class="w-4 h-4" />
            </button>
          </div>
        </div>

        <!-- Submit -->
        <button
          type="submit"
          :disabled="submitting || form.items.length === 0"
          class="w-full bg-[#2d6a4f] text-white py-3 rounded-lg font-semibold active:bg-[#40916c] disabled:opacity-50"
        >
          <span v-if="submitting">Ukladám...</span>
          <span v-else>{{ isEdit ? 'Uložiť zmeny' : 'Vytvoriť zostavu' }}</span>
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, Plus, X } from 'lucide-vue-next'
import { forestApi } from '../services/api'

const router = useRouter()
const route = useRoute()
const submitting = ref(false)
const loadingData = ref(true)
const availableItems = ref([])

const isEdit = computed(() => !!route.params.id)

const colorPresets = [
  '#16a34a', '#2d6a4f', '#059669', '#0891b2',
  '#2563eb', '#7c3aed', '#dc2626', '#ea580c',
  '#ca8a04', '#65a30d'
]

const form = ref({
  name: '',
  description: '',
  color: '#16a34a',
  items: []
})

const addComponent = () => {
  form.value.items.push({ forestItemId: '', quantity: 1 })
}

const removeComponent = (idx) => {
  form.value.items.splice(idx, 1)
}

onMounted(async () => {
  try {
    const itemsResp = await forestApi.getAllItems()
    availableItems.value = itemsResp.data

    if (isEdit.value) {
      const setResp = await forestApi.getSet(route.params.id)
      const set = setResp.data
      form.value.name = set.name
      form.value.description = set.description || ''
      form.value.color = set.color
      form.value.items = set.items.map(i => ({
        forestItemId: i.forestItem.id,
        quantity: i.quantity
      }))
    }
  } catch (error) {
    console.error('Failed to load data:', error)
  } finally {
    loadingData.value = false
  }
})

const submitForm = async () => {
  try {
    submitting.value = true
    const payload = {
      name: form.value.name,
      description: form.value.description || null,
      color: form.value.color,
      items: form.value.items.map(i => ({
        forestItemId: parseInt(i.forestItemId),
        quantity: parseFloat(i.quantity)
      }))
    }

    if (isEdit.value) {
      await forestApi.updateSet(route.params.id, payload)
    } else {
      await forestApi.createSet(payload)
    }
    router.push('/forest/sets')
  } catch (error) {
    console.error('Failed to save set:', error)
    alert('Nepodarilo sa uložiť zostavu: ' + (error.response?.data?.message || error.message))
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.back()
}
</script>
