<template>
  <div id="cart-app">
    <header><img src="@/assets/solar-geeks-logo.png" alt="Solar Geeks Logo"></header>
      <nav class="navbar">
      <div class="navbar-left">
        <router-link v-bind:to="{ name: 'home' }"> Home </router-link>
        <router-link v-if="isLoggedIn" v-bind:to="{ name: 'cart' }"> Cart </router-link>
      </div>
      <div class="navbar-right">
        <router-link v-bind:to="{ name: 'logout' }" v-if="$store.state.token">
          Logout
        </router-link>
        <router-link v-bind:to="{ name: 'login' }" v-else>Login</router-link>
      </div>
    </nav>
    <main>
      <router-view />
    </main>
    <footer>Created by Christopher Anderson</footer>
  </div>
</template>

<script>
export default {
computed: {
    isLoggedIn() {
      return this.$store.state.token.length > 0;
    }
  }
};
</script>

<style scoped>
html {
  margin: 0;
  padding: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

#cart-app {
  padding: 0;
  margin: 0;
}

header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  display: flex;
  background-image: linear-gradient(to bottom, #000515 , black);
  align-items: center;
  justify-content: center;
  z-index: 1000;
  margin: 0;
  padding: 0;
  height: 200px;
  
}

header img {
  max-width: 100%;
  height: auto; 
  margin-top: 0px;
}

.navbar {
  position: fixed;
  top: 200px;
  left: 0;
  width: 100%;
  background-color: black; 
  padding: 10px 20px;
  display: flex;
  justify-content: space-between; 
  align-items: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  z-index: 3;
  border-top: 4px solid;
  border-image: linear-gradient(to right, #007bff, white, #007bff) 1;

}


.navbar-left {
  display: flex; 
}


.navbar a {
  color: white; 
  text-decoration: none; 
  padding: 10px 20px;
  font-size: 16px;
  transition: background-image 0.3s, transform 0.3s; 
  border-radius: 5px; 
  font-weight: bold;
}


.navbar a:hover {
  background-image: linear-gradient(to bottom, black 10%, #9acbff83); 
  color: #007bff ; 
  font-weight: bold;
}


.navbar a.router-link-active {
  background-image: linear-gradient(to bottom, black 10%, #007bff ); 
  color: white; 
}


.navbar-right {
  display: flex; 
}

footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background-image: linear-gradient(to bottom, #000515 , black);
  color: white;
  text-align: center;
  padding: 10px;
  border-top: 4px solid;
  border-image: linear-gradient(to right, #007bff, white, #007bff) 1;
}
</style>