<template>
  <section class="todoapp">
    <header class="header">
      <input class="new-todo" autocomplete="off" placeholder="工作列表" @keyup.enter="addTodo">
    </header>
    <section v-show="todos.length" class="main">
      <input id="toggle-all" :checked="allChecked" class="toggle-all" type="checkbox" @change="toggleAll({ done: !allChecked })">
      <label for="toggle-all" />
      <ul class="todo-list">
        <todo v-for="(todo, index) in filteredTodos" :key="index" :todo="todo" @toggleTodo="toggleTodo" @editTodo="editTodo" @deleteTodo="deleteTodo" />
      </ul>
    </section>
    <!-- footer -->
    <footer v-show="todos.length" class="footer">
      <span class="todo-count">
        <strong>{{ remaining }}</strong>条未完成
      </span>
      <ul class="filters">
        <li v-for="(val, key) in filters" :key="key">
          <a :class="{ selected: visibility === key }" @click.prevent="visibility = key">{{ val.name }}</a>
        </li>
      </ul>
    </footer>
  </section>
</template>

<script>
import Todo from './Todo.vue'
import store from '@/store'

const STORAGE_KEY = 'yunshan_todos' + store.getters.account
const filters = {
  all: { filter: todos => todos, name: '全部' },
  active: { filter: todos => todos.filter(todo => !todo.done), name: '正在做' },
  completed: { filter: todos => todos.filter(todo => todo.done), name: '已做完' }
}
const defalutList = [
  { text: '修改你的账户默认的登录密码', done: false },
  { text: '熟悉这个系统的使用', done: false },
  { text: '尝试添加一条工作备忘录', done: false }
]
export default {
  components: { Todo },
  filters: {
    pluralize: (n, w) => n === 1 ? w : w + 's',
    capitalize: s => s.charAt(0).toUpperCase() + s.slice(1)
  },
  data() {
    return {
      visibility: 'all',
      filters,
      todos: JSON.parse(window.localStorage.getItem(STORAGE_KEY)) || defalutList
    }
  },
  computed: {
    allChecked() {
      return this.todos.every(todo => todo.done)
    },
    filteredTodos() {
      return filters[this.visibility].filter(this.todos)
    },
    remaining() {
      return this.todos.filter(todo => !todo.done).length
    }
  },
  methods: {
    setLocalStorage() {
      window.localStorage.setItem(STORAGE_KEY, JSON.stringify(this.todos))
    },
    addTodo(e) {
      const text = e.target.value
      if (text.trim()) {
        this.todos.push({
          text,
          done: false
        })
        this.setLocalStorage()
      }
      e.target.value = ''
    },
    toggleTodo(val) {
      val.done = !val.done
      this.setLocalStorage()
    },
    deleteTodo(todo) {
      this.todos.splice(this.todos.indexOf(todo), 1)
      this.setLocalStorage()
    },
    editTodo({ todo, value }) {
      todo.text = value
      this.setLocalStorage()
    },
    clearCompleted() {
      this.todos = this.todos.filter(todo => !todo.done)
      this.setLocalStorage()
    },
    toggleAll({ done }) {
      this.todos.forEach(todo => {
        todo.done = done
        this.setLocalStorage()
      })
    }
  }
}
</script>

<style lang="scss">
@import "./index.scss";
</style>
