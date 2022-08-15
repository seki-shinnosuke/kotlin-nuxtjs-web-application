<script setup lang="ts">
import { useMeta } from '#app'
useMeta({
  title: 'サンプル',
})
  const config = useRuntimeConfig()
  const { data: getResponse } = await useFetch('/api/v1/sample', { baseURL: config.BASE_URL, method: 'GET', headers: [{ key: 'Content-Type', value: 'application/json' }] })
  
  let postResponse = ref('')
  async function post(){
    const { data: postResponse } = await useFetch('/api/v1/sample', { baseURL: config.BASE_URL, method: 'POST', headers: [{ key: 'Content-Type', value: 'application/json' }] })
    return postResponse
  }
</script>

<template>
  <div>
    <div>
      ページ表示と同時に値を取得してみる: {{ getResponse }}
    </div>
    <div>
      <p>ボタンを押してPOST送信の結果を受信してみる: {{ postResponse }}</p>
      <button @click="post()">POST送信</button>
    </div>
  </div>
</template>