<template>
  <div class="h-screen bg-[#f5f7fa] relative">
    <!-- Center content wrapper -->
    <div class="h-full flex items-center justify-center">
      <div class="relative">
        <!-- Main Circle Button -->
        <button @click="navigateToSell"
                class="w-40 h-40 bg-[#94c1e8] text-white rounded-full
                       hover:bg-[#7eb3e2] transition-colors duration-300
                       flex flex-col items-center justify-center shadow-lg">
          <ArrowLeftRight class="w-16 h-16 mb-2" />
          <span class="text-xl">In/Out</span>
        </button>

        <!-- Bottom Left and Right Buttons -->
        <div class="absolute w-full flex justify-between" style="top: 120%">
          <button @click="navigateToAdjust"
                  class="w-16 h-16 bg-[#9eb8ac] text-white rounded-full
                         hover:bg-[#8ba797] transition-colors duration-300
                         flex items-center justify-center shadow-md tooltip"
                  data-tooltip="Upraviť sklad">
            <Settings2 class="w-8 h-8" />
          </button>

          <button @click="navigateToBrowser"
                  class="w-16 h-16 bg-[#e2b4bd] text-white rounded-full
                         hover:bg-[#d9a1ac] transition-colors duration-300
                         flex items-center justify-center shadow-md tooltip"
                  data-tooltip="Prehliadať sklad">
            <LayoutList class="w-8 h-8" />
          </button>
        </div>
      </div>
    </div>

    <!-- Add Button - adjusted positioning -->
    <div class="absolute right-6 bottom-6">
      <button @click="navigateToCreate"
              class="w-20 h-20 bg-[#a8d5ba] text-white rounded-full
                     hover:bg-[#97c4a9] transition-colors duration-300
                     flex items-center justify-center shadow-lg tooltip z-10"
              data-tooltip="Pridať položku">
        <Plus class="w-10 h-10" />
      </button>
    </div>
  </div>
</template>

<script setup>
import { ArrowLeftRight, Settings2, LayoutList, Plus } from 'lucide-vue-next'
import { useRouter } from 'vue-router'

const router = useRouter()
const navigateToSell = () => {
  router.push('/scan')
}
const navigateToAdjust = () => {
  router.push('/adjust')
}
const navigateToBrowser = () => {
  router.push('/stocks')
}
const navigateToCreate = () => {
  router.push('/create-item')
}
</script>

<style>
.tooltip {
  position: relative;
}

.tooltip:hover::after {
  content: attr(data-tooltip);
  position: absolute;
  bottom: -30px;
  left: 50%;
  transform: translateX(-50%);
  padding: 4px 8px;
  background: #333;
  color: white;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
}
</style>