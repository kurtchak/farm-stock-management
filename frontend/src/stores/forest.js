import { defineStore } from 'pinia'
import { forestApi } from '../services/api'

export const useForestStore = defineStore('forest', {
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
          const forestItem = state.items.find(i => i.id === setItem.forestItem.id)
          const available = forestItem ? forestItem.quantity : setItem.forestItem.quantity
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
        const response = await forestApi.getAllItems(category)
        this.items = response.data
      } catch (error) {
        this.error = error.message
        console.error('Error fetching forest items:', error)
      } finally {
        this.loading = false
      }
    },

    async fetchSets() {
      this.loading = true
      this.error = null
      try {
        const response = await forestApi.getAllSets()
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
        const response = await forestApi.getActiveSets()
        this.activeSets = response.data
      } catch (error) {
        console.error('Error fetching active sets:', error)
      }
    },

    async fetchStatistics() {
      try {
        const response = await forestApi.getStatistics()
        this.statistics = response.data
      } catch (error) {
        console.error('Error fetching forest statistics:', error)
      }
    },

    async fetchMovements() {
      this.loading = true
      this.error = null
      try {
        const response = await forestApi.getMovements()
        this.movements = response.data
      } catch (error) {
        this.error = error.message
        console.error('Error fetching forest movements:', error)
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
      await forestApi.executeSet(setId, count)
      await this.fetchDashboardData()
    }
  }
})
