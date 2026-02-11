import { ref, onMounted, onUnmounted } from 'vue'

export function usePullToRefresh(scrollElRef, onRefresh) {
  const pullDistance = ref(0)
  const isRefreshing = ref(false)

  const THRESHOLD = 60
  let startY = 0
  let isPulling = false

  function onTouchStart(e) {
    const el = scrollElRef.value
    if (!el || el.scrollTop !== 0) return
    startY = e.touches[0].clientY
    isPulling = true
  }

  function onTouchMove(e) {
    if (!isPulling || isRefreshing.value) return
    const el = scrollElRef.value
    if (!el || el.scrollTop !== 0) {
      isPulling = false
      pullDistance.value = 0
      return
    }
    const diff = e.touches[0].clientY - startY
    if (diff > 0) {
      pullDistance.value = Math.min(diff * 0.5, 100)
      if (diff > 10) {
        e.preventDefault()
      }
    }
  }

  async function onTouchEnd() {
    if (!isPulling) return
    isPulling = false
    if (pullDistance.value >= THRESHOLD && !isRefreshing.value) {
      isRefreshing.value = true
      pullDistance.value = THRESHOLD
      try {
        await onRefresh()
      } finally {
        isRefreshing.value = false
      }
    }
    pullDistance.value = 0
  }

  let el = null

  onMounted(() => {
    el = scrollElRef.value
    if (!el) return
    el.addEventListener('touchstart', onTouchStart, { passive: true })
    el.addEventListener('touchmove', onTouchMove, { passive: false })
    el.addEventListener('touchend', onTouchEnd, { passive: true })
  })

  onUnmounted(() => {
    if (!el) return
    el.removeEventListener('touchstart', onTouchStart)
    el.removeEventListener('touchmove', onTouchMove)
    el.removeEventListener('touchend', onTouchEnd)
  })

  return { pullDistance, isRefreshing }
}
