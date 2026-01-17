import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

export const stockApi = {
  // Get all stocks
  getAllStocks() {
    return apiClient.get('/api/stock')
  },

  // Get stock by batch code
  getStockByBatchCode(batchCode) {
    return apiClient.get(`/api/stock/batch/${batchCode}`)
  },

  // Create new stock
  createStock(stockData) {
    return apiClient.post('/api/stock', stockData)
  },

  // Adjust stock
  adjustStock(stockId, adjustmentData) {
    return apiClient.post(`/api/stock/${stockId}/adjust`, adjustmentData)
  },

  // Get stock configuration
  getConfig() {
    return apiClient.get('/api/stock/config')
  },

  // Delete stock
  deleteStock(stockId) {
    return apiClient.delete(`/api/stock/${stockId}`)
  },

  // Get all deleted stocks
  getDeletedStocks() {
    return apiClient.get('/api/stock/deleted')
  },

  // Restore deleted stock
  restoreStock(stockId) {
    return apiClient.post(`/api/stock/${stockId}/restore`)
  }
}

export const stockMovementApi = {
  // Get statistics
  getStatistics() {
    return apiClient.get('/api/stock-movements/statistics')
  },

  // Get recent movements
  getRecentMovements(limit = 10) {
    return apiClient.get('/api/stock-movements/recent', {
      params: { limit }
    })
  }
}

export const cropApi = {
  // Get all crops
  getAllCrops() {
    return apiClient.get('/api/crops')
  },

  // Create new crop
  createCrop(cropData) {
    return apiClient.post('/api/crops', cropData)
  }
}

export default apiClient