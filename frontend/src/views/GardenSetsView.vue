<template>
  <div class="min-h-screen bg-gradient-to-br from-emerald-50 via-green-50 to-teal-50 flex flex-col">
    <!-- Header -->
    <div class="bg-white shadow-sm px-4 py-3">
      <div class="flex items-center">
        <button
          @click="goBack"
          class="flex items-center text-gray-500 active:text-gray-700 transition-colors p-2 -ml-2"
        >
          <ArrowLeft class="w-5 h-5" />
        </button>
        <h1 class="text-xl font-bold text-gray-800 ml-1">Zostavy výsadby</h1>
      </div>
    </div>

    <!-- Main content -->
    <div class="flex-1 p-4 overflow-auto pb-24">
      <!-- Loading State -->
      <div v-if="loading" class="flex items-center justify-center py-20">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-green-700"></div>
      </div>

      <!-- Sets List -->
      <div v-else-if="sets.length > 0" class="space-y-3">
        <div
          v-for="set in sets"
          :key="set.id"
          class="bg-white rounded-xl shadow-sm p-4"
        >
          <div class="flex items-center justify-between mb-2">
            <div class="flex items-center gap-2">
              <div class="w-3 h-3 rounded-full" :style="{ backgroundColor: set.color }"></div>
              <h3 class="font-bold text-gray-800">{{ set.name }}</h3>
            </div>
            <span
              :class="[
                'text-[10px] px-2 py-0.5 rounded-full font-medium',
                set.active ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'
              ]"
            >
              {{ set.active ? 'Aktívna' : 'Neaktívna' }}
            </span>
          </div>

          <p v-if="set.description" class="text-sm text-gray-500 mb-2">{{ set.description }}</p>

          <!-- Components summary -->
          <div class="bg-gray-50 rounded-lg p-3 mb-3">
            <p class="text-[11px] text-gray-400 uppercase tracking-wide mb-1">Komponenty</p>
            <div v-for="item in set.items" :key="item.id" class="flex justify-between text-sm py-0.5">
              <span class="text-gray-600">{{ item.gardenItem.name }}</span>
              <span class="text-gray-800 font-medium">{{ item.quantity }} {{ item.gardenItem.unit }}</span>
            </div>
            <p v-if="!set.items || set.items.length === 0" class="text-sm text-gray-400">Žiadne komponenty</p>
          </div>

          <div class="flex gap-2">
            <button
              @click="router.push(`/gardens/sets/${set.id}/edit`)"
              class="flex-1 bg-gray-100 text-gray-700 py-2 rounded-lg font-medium text-sm active:bg-gray-200 flex items-center justify-center gap-1"
            >
              <Pencil class="w-3.5 h-3.5" />
              Upraviť
            </button>
            <button
              @click="handleDelete(set.id)"
              class="px-4 bg-red-50 text-red-600 py-2 rounded-lg font-medium text-sm active:bg-red-100"
            >
              <Trash2 class="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-20">
        <Layers class="w-16 h-16 text-gray-300 mx-auto mb-4" />
        <p class="text-gray-500">Žiadne zostavy</p>
        <button
          @click="router.push('/gardens/sets/new')"
          class="mt-4 text-[#2d6a4f] font-medium"
        >
          Vytvoriť prvú zostavu
        </button>
      </div>
    </div>

    <!-- FAB -->
    <button
      @click="router.push('/gardens/sets/new')"
      class="fixed bottom-6 right-6 w-14 h-14 bg-[#2d6a4f] rounded-full shadow-lg flex items-center justify-center text-white active:bg-[#40916c] transition-colors z-40"
    >
      <Plus class="w-7 h-7" />
    </button>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Pencil, Trash2, Layers, Plus } from 'lucide-vue-next'
import { useGardenStore } from '../stores/garden'
import { gardenApi } from '../services/api'

const router = useRouter()
const gardenStore = useGardenStore()

const loading = computed(() => gardenStore.loading)
const sets = computed(() => gardenStore.sets)

const handleDelete = async (id) => {
  if (!confirm('Naozaj chcete odstrániť túto zostavu?')) return
  try {
    await gardenApi.deleteSet(id)
    await gardenStore.fetchSets()
  } catch (error) {
    alert('Nepodarilo sa odstrániť zostavu')
  }
}

onMounted(async () => {
  await gardenStore.fetchSets()
})

const goBack = () => {
  router.back()
}
</script>
