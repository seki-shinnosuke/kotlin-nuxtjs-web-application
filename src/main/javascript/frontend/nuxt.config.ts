import { defineNuxtConfig } from 'nuxt'

export default defineNuxtConfig({
  publicRuntimeConfig: {
    BASE_URL: process.env.API_URL
  }
})
