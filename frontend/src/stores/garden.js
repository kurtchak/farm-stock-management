import { defineStore } from 'pinia'
import { gardenApi } from '../services/api'

export const useGardenStore = defineStore('garden', {
  state: () => ({
    items: [],
    sets: [],
    activeSets: [],
    statistics: {
      totalItems: 0,
      treeCount: 0,
      stakeCount: 0,
      protectionCount: 0,
      otherCount: 0,
      lowStockCount: 0,
      weeklyIn: 0,
      weeklyOut: 0
    },
    movements: [],
    loading: false,
    error: null
  }),

  getters: {
    maxExecutions: (state) => {
      return (set) => {
        if (!set.items || set.items.length === 0) return 0
        let min = Infinity
        for (const setItem of set.items) {
          const gardenItem = state.items.find(i => i.id === setItem.gardenItem.id)
          const available = gardenItem ? gardenItem.quantity : setItem.gardenItem.quantity
          const perExec = setItem.quantity
          if (perExec > 0) {
            min = Math.min(min, Math.floor(available / perExec))
          }
        }
        return min === Infinity ? 0 : min
      }
    }
  },

  actions: {
    async fetchItems(category) {
      this.loading = true
      this.error = null
      try {
        const response = await gardenApi.getAllItems(category)
        this.items = response.data
      } catch (error) {
        this.error = error.message
        console.error('Error fetching garden items:', error)
      } finally {
        this.loading = false
      }
    },

    async fetchSets() {
      this.loading = true
      this.error = null
      try {
        const response = await gardenApi.getAllSets()
        this.sets = response.data
      } catch (error) {
        this.error = error.message
        console.error('Error fetching planting sets:', error)
      } finally {
        this.loading = false
      }
    },

    async fetchActiveSets() {
      try {
        const response = await gardenApi.getActiveSets()
        this.activeSets = response.data
      } catch (error) {
        console.error('Error fetching active sets:', error)
      }
    },

    async fetchStatistics() {
      try {
        const response = await gardenApi.getStatistics()
        this.statistics = response.data
      } catch (error) {
        console.error('Error fetching garden statistics:', error)
      }
    },

    async fetchMovements() {
      this.loading = true
      this.error = null
      try {
        const response = await gardenApi.getMovements()
        this.movements = response.data
      } catch (error) {
        this.error = error.message
        console.error('Error fetching garden movements:', error)
      } finally {
        this.loading = false
      }
    },

    async fetchDashboardData() {
      this.loading = true
      this.error = null
      try {
        await Promise.all([
          this.fetchItems(),
          this.fetchActiveSets(),
          this.fetchStatistics()
        ])
      } finally {
        this.loading = false
      }
    },

    async executeSet(setId, count) {
      await gardenApi.executeSet(setId, count)
      await this.fetchDashboardData()
    }
  }
})
