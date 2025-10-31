import { defineStore } from 'pinia'
import { ref } from 'vue'
import { genreApi } from '@/api'

export const useGenreStore = defineStore('genre', () => {
  const genres = ref([])
  const loading = ref(false)

  // 获取所有类型
  const getGenres = async () => {
    if (genres.value.length > 0) {
      return genres.value
    }

    loading.value = true
    try {
      const response = await genreApi.getGenres()
      genres.value = response.data
      return genres.value
    } catch (error) {
      console.error('获取电影类型失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 根据ID获取类型名称
  const getGenreName = (genreId) => {
    const genre = genres.value.find(g => g.id === genreId)
    return genre ? genre.name : '未知类型'
  }

  return {
    genres,
    loading,
    getGenres,
    getGenreName
  }
})
