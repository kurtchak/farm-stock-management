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
        <h1 class="text-xl font-bold text-gray-800 ml-1">Materiál</h1>
      </div>
    </div>

    <!-- Category Tabs -->
    <div class="bg-white border-b px-4 py-2 flex gap-2 overflow-x-auto">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        @click="activeTab = tab.value"
        :class="[
          'px-4 py-1.5 rounded-full text-sm font-medium whitespace-nowrap transition-colors',
          activeTab === tab.value
            ? 'bg-[#2d6a4f] text-white'
            : 'bg-gray-100 text-gray-600 active:bg-gray-200'
        ]"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- Main content -->
    <div class="flex-1 p-4 overflow-auto pb-24">
      <!-- Loading State -->
      <div v-if="loading" class="flex items-center justify-center py-20">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-green-700"></div>
      </div>

      <!-- Items List -->
      <div v-else-if="filteredItems.length > 0" class="space-y-3">
        <div
          v-for="item in filteredItems"
          :key="item.id"
          @click="openDetail(item)"
          class="bg-white rounded-xl shadow-sm p-4 active:bg-gray-50 transition-colors cursor-pointer"
        >
          <div class="flex items-center justify-between">
            <div class="flex-1">
              <div class="flex items-center gap-2">
                <h3 class="font-semibold text-gray-800">{{ item.name }}</h3>
                <span class="text-[10px] px-2 py-0.5 rounded-full font-medium"
                  :class="categoryBadge(item.category)">
                  {{ categoryLabel(item.category) }}
                </span>
              </div>
              <p class="text-sm text-gray-500 mt-1">{{ item.quantity }} {{ item.unit }}</p>
            </div>
            <div class="flex items-center gap-2">
              <div v-if="isLowStock(item)" class="flex items-center gap-1 text-red-500">
                <AlertTriangle class="w-4 h-4" />
                <span class="text-xs font-medium">Nízky stav</span>
              </div>
              <button
                @click.stop="router.push(`/forest/items/${item.id}/adjust`)"
                class="w-9 h-9 rounded-lg bg-[#2d6a4f] flex items-center justify-center text-white active:bg-[#40916c]"
              >
                <ArrowUpDown class="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-20">
        <TreePine class="w-16 h-16 text-gray-300 mx-auto mb-4" />
        <p class="text-gray-500">Žiadny materiál</p>
        <button
          @click="router.push('/forest/items/create')"
          class="mt-4 text-[#2d6a4f] font-medium"
        >
          Pridať prvý materiál
        </button>
      </div>
    </div>

    <!-- Detail Modal -->
    <div v-if="detailItem" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-2xl p-6 max-w-sm w-full">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-xl font-bold text-gray-800">{{ detailItem.name }}</h2>
          <button @click="detailItem = null" class="text-gray-400 active:text-gray-600">
            <X class="w-5 h-5" />
          </button>
        </div>

        <div class="space-y-3 text-sm">
          <div class="flex justify-between">
            <span class="text-gray-500">Kategória</span>
            <span class="font-medium">{{ categoryLabel(detailItem.category) }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-500">Množstvo</span>
            <span class="font-medium">{{ detailItem.quantity }} {{ detailItem.unit }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-500">Min. zásoba</span>
            <span class="font-medium">{{ detailItem.minStock }} {{ detailItem.unit }}</span>
          </div>
          <div v-if="detailItem.notes" class="pt-2 border-t">
            <p class="text-gray-500 mb-1">Poznámky</p>
            <p class="text-gray-700">{{ detailItem.notes }}</p>
          </div>
        </div>

        <div class="flex gap-2 mt-5">
          <button
            @click="detailItem = null; router.push(`/forest/items/${detailItem.id}/adjust`)"
            class="flex-1 bg-[#2d6a4f] text-white py-2.5 rounded-xl font-medium"
          >
            Upraviť stav
          </button>
          <button
            @click="handleDelete(detailItem.id)"
            class="px-4 bg-red-50 text-red-600 py-2.5 rounded-xl font-medium active:bg-red-100"
          >
            <Trash2 class="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>

    <!-- FAB -->
    <button
      @click="router.push('/forest/items/create')"
      class="fixed bottom-6 right-6 w-14 h-14 bg-[#2d6a4f] rounded-full shadow-lg flex items-center justify-center text-white active:bg-[#40916c] transition-colors z-40"
    >
      <Plus class="w-7 h-7" />
    </button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, AlertTriangle, ArrowUpDown, TreePine, Plus, X, Trash2 } from 'lucide-vue-next'
import { useForestStore } from '../stores/forest'
import { forestApi } from '../services/api'

const router = useRouter()
const forestStore = useForestStore()

const activeTab = ref(null)
const detailItem = ref(null)

const tabs = [
  { label: 'Všetky', value: null },
  { label: 'Stromy', value: 'TREE' },
  { label: 'Kolíky', value: 'STAKE' },
  { label: 'Ochrana', value: 'PROTECTION' },
  { label: 'Ostatné', value: 'OTHER' }
]

const loading = computed(() => forestStore.loading)
const filteredItems = computed(() => {
  if (!activeTab.value) return forestStore.items
  return forestStore.items.filter(i => i.category === activeTab.value)
})

const isLowStock = (item) => item.quantity <= item.minStock && item.minStock > 0

const categoryLabel = (cat) => {
  const labels = { TREE: 'Strom', STAKE: 'Kolík', PROTECTION: 'Ochrana', OTHER: 'Ostatné' }
  return labels[cat] || cat
}

const categoryBadge = (cat) => {
  const classes = {
    TREE: 'bg-green-100 text-green-700',
    STAKE: 'bg-amber-100 text-amber-700',
    PROTECTION: 'bg-blue-100 text-blue-700',
    OTHER: 'bg-gray-100 text-gray-600'
  }
  return classes[cat] || 'bg-gray-100 text-gray-600'
}

const openDetail = (item) => {
  detailItem.value = item
}

const handleDelete = async (id) => {
  if (!confirm('Naozaj chcete odstrániť túto položku?')) return
  try {
    await forestApi.deleteItem(id)
    detailItem.value = null
    await forestStore.fetchItems()
  } catch (error) {
    alert('Nepodarilo sa odstrániť položku')
  }
}

onMounted(async () => {
  await forestStore.fetchItems()
})

const goBack = () => {
  router.back()
}
</script>
