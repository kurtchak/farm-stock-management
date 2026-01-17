import { defineStore } from 'pinia'
import { stockApi, stockMovementApi } from '../services/api'

export const useStockStore = defineStore('stock', {
  state: () => ({
    stocks: [],
    statistics: {
      totalItems: 0,
      weeklyIn: 0,
      weeklyOut: 0
    },
    recentMovements: [],
    loading: false,
    error: null,
    deleteThreshold: 0
  }),

  getters: {
    totalStockItems: (state) => state.statistics.totalItems,
    weeklyInCount: (state) => state.statistics.weeklyIn,
    weeklyOutCount: (state) => state.statistics.weeklyOut,
    formattedRecentMovements: (state) => {
      return state.recentMovements.map(movement => {
        const cropName = movement.cropVariety
          ? `${movement.cropName} ${movement.cropVariety}`
          : movement.cropName

        return {
          id: movement.id,
          name: cropName || 'Neznáma plodina',
          time: formatRelativeTime(movement.createdAt),
          amount: `${Math.abs(movement.quantity)} ${movement.unitOfMeasure}`,
          type: movement.movementType.toLowerCase(),
          user: movement.userFullName || movement.userName
        }
      })
    }
  },

  actions: {
    async fetchStocks() {
      this.loading = true
      this.error = null
      try {
        const response = await stockApi.getAllStocks()
        this.stocks = response.data
      } catch (error) {
        this.error = error.message
        console.error('Error fetching stocks:', error)
      } finally {
        this.loading = false
      }
    },

    async fetchStatistics() {
      this.loading = true
      this.error = null
      try {
        const response = await stockMovementApi.getStatistics()
        this.statistics = response.data
      } catch (error) {
        this.error = error.message
        console.error('Error fetching statistics:', error)
      } finally {
        this.loading = false
      }
    },

    async fetchRecentMovements(limit = 10) {
      this.loading = true
      this.error = null
      try {
        const response = await stockMovementApi.getRecentMovements(limit)
        this.recentMovements = response.data
      } catch (error) {
        this.error = error.message
        console.error('Error fetching recent movements:', error)
      } finally {
        this.loading = false
      }
    },

    async fetchDashboardData() {
      await Promise.all([
        this.fetchStatistics(),
        this.fetchRecentMovements(3)
      ])
    },

    async fetchStockConfig() {
      try {
        const response = await stockApi.getConfig()
        this.deleteThreshold = response.data.deleteThreshold
      } catch (error) {
        console.error('Error fetching stock config:', error)
        this.deleteThreshold = 0
      }
    }
  }
})

// Helper function to format relative time
function formatRelativeTime(dateTime) {
  const date = new Date(dateTime)
  const now = new Date()
  const diffMs = now - date
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)

  if (diffMins < 60) {
    return diffMins <= 1 ? 'Práve teraz' : `Pred ${diffMins} min`
  } else if (diffHours < 24) {
    return `Dnes, ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
  } else if (diffDays === 1) {
    return `Včera, ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
  } else if (diffDays < 7) {
    return `Pred ${diffDays} dňami`
  } else {
    return date.toLocaleDateString('sk-SK')
  }
}
