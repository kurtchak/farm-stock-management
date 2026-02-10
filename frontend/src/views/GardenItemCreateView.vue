<template>
  <div class="min-h-screen bg-[#f5f7fa] flex flex-col">
    <div class="p-4 bg-white shadow-sm flex items-center">
      <button @click="goBack" class="mr-4">
        <ArrowLeft class="w-6 h-6" />
      </button>
      <h1 class="text-xl font-semibold">Nový materiál</h1>
    </div>

    <div class="flex-grow p-4">
      <form @submit.prevent="submitForm" class="max-w-lg mx-auto bg-white rounded-lg shadow-sm p-6">
        <!-- Name -->
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">Názov</label>
          <input
            v-model="form.name"
            type="text"
            required
            placeholder="Napr. Smrek pichľavý"
            class="w-full p-2 border rounded-md"
          />
        </div>

        <!-- Category -->
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">Kategória</label>
          <select v-model="form.category" required class="w-full p-2 border rounded-md">
            <option value="">Vyberte kategóriu</option>
            <option value="TREE">Strom</option>
            <option value="STAKE">Kolík</option>
            <option value="PROTECTION">Ochrana</option>
            <option value="OTHER">Ostatné</option>
          </select>
        </div>

        <!-- Quantity and Unit -->
        <div class="mb-4 grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Množstvo</label>
            <input
              v-model="form.quantity"
              type="number"
              step="1"
              min="0"
              required
              class="w-full p-2 border rounded-md"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Jednotka</label>
            <select v-model="form.unit" required class="w-full p-2 border rounded-md">
              <option value="ks">Kusy</option>
              <option value="m">Metre</option>
              <option value="kg">Kilogramy</option>
              <option value="bal">Balenie</option>
            </select>
          </div>
        </div>

        <!-- Min stock -->
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">Minimálna zásoba</label>
          <input
            v-model="form.minStock"
            type="number"
            step="1"
            min="0"
            class="w-full p-2 border rounded-md"
          />
          <p class="text-xs text-gray-400 mt-1">Upozornenie pri nízkom stave</p>
        </div>

        <!-- Notes -->
        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-1">Poznámky</label>
          <textarea
            v-model="form.notes"
            rows="3"
            class="w-full p-2 border rounded-md"
            placeholder="Voliteľné poznámky..."
          ></textarea>
        </div>

        <!-- Submit -->
        <button
          type="submit"
          :disabled="submitting"
          class="w-full bg-[#2d6a4f] text-white py-3 rounded-lg font-semibold active:bg-[#40916c] disabled:opacity-50"
        >
          <span v-if="submitting">Ukladám...</span>
          <span v-else>Vytvoriť materiál</span>
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from 'lucide-vue-next'
import { gardenApi } from '../services/api'

const router = useRouter()
const submitting = ref(false)

const form = ref({
  name: '',
  category: '',
  quantity: 0,
  unit: 'ks',
  minStock: 0,
  notes: ''
})

const submitForm = async () => {
  try {
    submitting.value = true
    await gardenApi.createItem({
      name: form.value.name,
      category: form.value.category,
      quantity: parseFloat(form.value.quantity),
      unit: form.value.unit,
      minStock: parseFloat(form.value.minStock) || 0,
      notes: form.value.notes || null
    })
    router.push('/gardens/items')
  } catch (error) {
    console.error('Failed to create garden item:', error)
    alert('Nepodarilo sa vytvoriť materiál: ' + (error.response?.data?.message || error.message))
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.back()
}
</script>
