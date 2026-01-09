import { defineStore } from 'pinia'

export const useStockStore = defineStore('stock', {
  state: () => ({
    stocks: []
  }),
  actions: {
    async fetchStocks() {
      // Implementation here
    }
  }
})
