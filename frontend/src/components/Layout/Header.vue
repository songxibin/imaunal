<script setup>
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 直接使用store中的信息
const username = computed(() => userStore.username)
</script>